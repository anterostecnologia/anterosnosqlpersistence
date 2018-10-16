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

import java.util.HashMap;
import java.util.Map;

import br.com.anteros.nosql.persistence.session.NoSQLSession;
import br.com.anteros.nosql.persistence.session.NoSQLSessionException;
import br.com.anteros.nosql.persistence.session.NoSQLSessionFactory;

public class ManagedNoSQLSessionContext implements CurrentNoSQLSessionContext {

	private static final long serialVersionUID = 1L;

	private static final ThreadLocal<Map<NoSQLSessionFactory, NoSQLSession>> context = new ThreadLocal<Map<NoSQLSessionFactory, NoSQLSession>>();
	
	private final NoSQLSessionFactory factory;

	public ManagedNoSQLSessionContext(NoSQLSessionFactory factory) {
		this.factory = factory;
	}

	public NoSQLSession currentSession() {
		NoSQLSession current = existingSession( factory );
		if ( current == null ) {
			throw new NoSQLSessionException( "No session currently bound to execution context" );
		}
		return current;
	}

	public static boolean hasBind(NoSQLSessionFactory factory) {
		return existingSession( factory ) != null;
	}

	public static NoSQLSession bind(NoSQLSession session) {
		return sessionMap( true ).put( session.getNoSQLSessionFactory(), session );
	}

	public static NoSQLSession unbind(NoSQLSessionFactory factory) {
		NoSQLSession existing = null;
		Map<NoSQLSessionFactory, NoSQLSession> sessionMap = sessionMap();
		if ( sessionMap != null ) {
			existing = sessionMap.remove( factory );
			doCleanup();
		}
		return existing;
	}

	private static NoSQLSession existingSession(NoSQLSessionFactory factory) {
		Map<NoSQLSessionFactory, NoSQLSession> sessionMap = sessionMap();
		if ( sessionMap == null ) {
			return null;
		}
		else {
			return sessionMap.get( factory );
		}
	}

	protected static Map<NoSQLSessionFactory, NoSQLSession> sessionMap() {
		return sessionMap( false );
	}

	private static synchronized Map<NoSQLSessionFactory, NoSQLSession> sessionMap(boolean createMap) {
		Map<NoSQLSessionFactory, NoSQLSession> sessionMap = context.get();
		if ( sessionMap == null && createMap ) {
			sessionMap = new HashMap<NoSQLSessionFactory, NoSQLSession>();
			context.set( sessionMap );
		}
		return sessionMap;
	}

	private static synchronized void doCleanup() {
		Map<NoSQLSessionFactory, NoSQLSession> sessionMap = sessionMap( false );
		if ( sessionMap != null ) {
			if ( sessionMap.isEmpty() ) {
				context.set( null );
			}
		}
	}
}

