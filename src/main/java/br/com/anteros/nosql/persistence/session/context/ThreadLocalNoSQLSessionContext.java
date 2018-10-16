/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package br.com.anteros.nosql.persistence.session.context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.nosql.persistence.dialect.NoSQLDialect;
import br.com.anteros.nosql.persistence.session.NoSQLSession;
import br.com.anteros.nosql.persistence.session.NoSQLSessionFactory;
import br.com.anteros.nosql.persistence.session.NoSQLSessionListener;
import br.com.anteros.nosql.persistence.session.transaction.NoSQLTransactionException;
import br.com.anteros.nosql.persistence.session.transaction.NoSQLTransactionSynchronization;


public class ThreadLocalNoSQLSessionContext implements CurrentNoSQLSessionContext {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerProvider.getInstance().getLogger(ThreadLocalNoSQLSessionContext.class.getName());

	private static final ThreadLocal<Map<NoSQLSessionFactory, NoSQLSession<?>>> context = new ThreadLocal<Map<NoSQLSessionFactory, NoSQLSession<?>>>();

	protected final NoSQLSessionFactory factory;

	public ThreadLocalNoSQLSessionContext(NoSQLSessionFactory factory) {
		this.factory = factory;
	}

	public final NoSQLSession<?> currentSession() {
		NoSQLSession<?> current = existingSession(factory);
		NoSQLDialect dialect = factory.getDescriptionEntityManager().getDialect();
		if (current == null) {
			current = factory.openSession();
			current.getTransaction().registerSynchronization(new CleaningSession(factory));
			if (needsWrapping(current)) {
				current = wrap(current,dialect.getSessionInterface());
			}
			registerNoSQLTSessionListener(current);
			doBind(current, factory);
		}
		return current;
	}

	protected NoSQLSession<?> wrap(NoSQLSession<?> session, Class<?> sessionClass) {
		TransactionProtectionWrapper wrapper = new TransactionProtectionWrapper(session);
		NoSQLSession<?> wrapped = (NoSQLSession<?>) Proxy.newProxyInstance(NoSQLSession.class.getClassLoader(),
				new Class[] { NoSQLSession.class, sessionClass }, wrapper);
		wrapper.setWrapped(wrapped);
		return wrapped;
	}

	private boolean needsWrapping(NoSQLSession<?> session) {
		if (session == null)
			return false;

		return !Proxy.isProxyClass(session.getClass()) || (Proxy.getInvocationHandler(session) != null
				&& !(Proxy.getInvocationHandler(session) instanceof TransactionProtectionWrapper));
	}

	private void registerNoSQLTSessionListener(NoSQLSession<?> session) {
		session.addListener(new NoSQLSessionListener() {
			@Override
			public void onClose(NoSQLSession<?> session) {
				unbind(session.getNoSQLSessionFactory());
			}
		});
	}

	protected NoSQLSessionFactory getNoSQLSessionFactory() {
		return factory;
	}

	public static void bind(NoSQLSession<?> session) {
		NoSQLSessionFactory factory = session.getNoSQLSessionFactory();
		cleanupAnyOrphanedSession(factory);
		doBind(session, factory);
	}

	private static void cleanupAnyOrphanedSession(NoSQLSessionFactory factory) {
		NoSQLSession<?> orphan = doUnbind(factory, false);
		if (orphan != null) {
			log.warn("Already session bound on call to bind(); make sure you clean up your sessions!");
			try {
				if (orphan.getTransaction() != null && orphan.getTransaction().isActive()) {
					try {
						orphan.getTransaction().rollback();
					} catch (Throwable t) {
						log.debug("Unable to rollback transaction for orphaned session", t);
					}
				}
				orphan.close();
			} catch (Throwable t) {
				log.debug("Unable to close orphaned session", t);
			}
		}
	}

	public static NoSQLSession<?> unbind(NoSQLSessionFactory factory) {
		return doUnbind(factory, true);
	}

	private static NoSQLSession<?> existingSession(NoSQLSessionFactory factory) {
		Map<NoSQLSessionFactory, NoSQLSession<?>> sessionMap = sessionMap();
		if (sessionMap == null) {
			return null;
		} else {
			return sessionMap.get(factory);
		}
	}

	protected static Map<NoSQLSessionFactory, NoSQLSession<?>> sessionMap() {
		return context.get();
	}

	private static void doBind(NoSQLSession<?> session, NoSQLSessionFactory factory) {
		Map<NoSQLSessionFactory, NoSQLSession<?>> sessionMap = sessionMap();
		if (sessionMap == null) {
			sessionMap = new HashMap<NoSQLSessionFactory, NoSQLSession<?>>();
			context.set(sessionMap);
		}
		sessionMap.put(factory, session);
	}

	private static NoSQLSession<?> doUnbind(NoSQLSessionFactory factory, boolean releaseMapIfEmpty) {
		Map<NoSQLSessionFactory, NoSQLSession<?>> sessionMap = sessionMap();
		NoSQLSession<?> session = null;
		if (sessionMap != null) {
			session = (NoSQLSession<?>) sessionMap.remove(factory);
			if (releaseMapIfEmpty && sessionMap.isEmpty()) {
				context.set(null);
			}
		}
		return session;
	}

	protected static class CleaningSession implements NoSQLTransactionSynchronization, Serializable {
		private static final long serialVersionUID = 1L;
		protected final NoSQLSessionFactory factory;

		public CleaningSession(NoSQLSessionFactory factory) {
			this.factory = factory;
		}

		public void beforeCompletion() {
		}

		public void afterCompletion(int i) {
			unbind(factory);
		}
	}

	private class TransactionProtectionWrapper implements InvocationHandler, Serializable {
		private static final long serialVersionUID = 1L;
		private final NoSQLSession<?> realSession;
		private NoSQLSession<?> wrappedSession;

		public TransactionProtectionWrapper(NoSQLSession<?> realSession) {
			this.realSession = realSession;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				if ("close".equals(method.getName())) {
					unbind(realSession.getNoSQLSessionFactory());
				} else if ("toString".equals(method.getName()) || "equals".equals(method.getName())
						|| "hashCode".equals(method.getName()) || "addListener".equals(method.getName())
						|| "setClientId".equals(method.getName()) || "getStatistics".equals(method.getName())
						|| "isOpen".equals(method.getName()) || "getListeners".equals(method.getName()) // useful for
																										// HSearch in
																										// particular
				) {
				} else if (realSession.isClosed()) {
				} else if (!realSession.isWithoutTransactionControl() && !realSession.getTransaction().isActive()) {
					if ("beginTransaction".equals(method.getName()) || "startTransaction".equals(method.getName()) || "getTransaction".equals(method.getName())
							|| "setFlushMode".equals(method.getName())
							|| "getNoSQLSessionFactory".equals(method.getName())) {
						log.debug("allowing method [" + method.getName() + "] in non-transacted context");
					} else {
						throw new NoSQLTransactionException(method.getName() + " is not valid without active transaction");
					}
				}
				log.debug("allowing proxied method [" + method.getName() + "] to proceed to real session");
				return method.invoke(realSession, args);
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof RuntimeException) {
					throw (RuntimeException) e.getTargetException();
				} else {
					throw e;
				}
			}
		}

		public void setWrapped(NoSQLSession<?> wrapped) {
			this.wrappedSession = wrapped;
		}

		private void writeObject(ObjectOutputStream oos) throws IOException {
			oos.defaultWriteObject();
			if (existingSession(factory) == wrappedSession) {
				unbind(factory);
			}
		}

		private void readObject(ObjectInputStream ois) throws Exception {
			ois.defaultReadObject();
			realSession.getTransaction().registerSynchronization(new CleaningSession(factory));
			doBind(wrappedSession, factory);
		}
	}
}
