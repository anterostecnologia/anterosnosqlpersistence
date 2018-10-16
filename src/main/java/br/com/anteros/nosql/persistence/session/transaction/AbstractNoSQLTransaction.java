package br.com.anteros.nosql.persistence.session.transaction;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.nosql.persistence.client.NoSQLConnection;
import br.com.anteros.nosql.persistence.session.NoSQLPersistenceContext;
import br.com.anteros.nosql.persistence.session.transaction.impl.NoSQLSynchronizationRegistry;

public abstract class AbstractNoSQLTransaction implements NoSQLTransaction {

	private static Logger log = LoggerProvider.getInstance().getLogger(AbstractNoSQLTransaction.class.getName());
	private final NoSQLSynchronizationRegistry synchronizationRegistry = new NoSQLSynchronizationRegistry();

	private NoSQLConnection connection;
	private NoSQLPersistenceContext context;

	protected NoSQLTransactionStatus status = NoSQLTransactionStatus.NOT_ACTIVE;

	public AbstractNoSQLTransaction(NoSQLConnection connection, NoSQLPersistenceContext context) {
		this.connection = connection;
		this.context = context;
	}

	@Override
	public void begin(NoSQLTransactionOptions options) {
		if (context.isWithoutTransactionControl()) {
			status = NoSQLTransactionStatus.ACTIVE;
		} else {
			if (status == NoSQLTransactionStatus.ACTIVE) {
				throw new NoSQLTransactionException("transações aninhadas não são suportadas");
			}
			if (status == NoSQLTransactionStatus.FAILED_COMMIT) {
				throw new NoSQLTransactionException("não foi possível reiniciar a transação após o commit ter falhado");
			}

			log.debug("begin");

			doBegin(options);

			status = NoSQLTransactionStatus.ACTIVE;
		}
	}

	@Override
	public void begin() {
		begin(null);
	}

	protected abstract void doBegin(NoSQLTransactionOptions options);

	@Override
	public void commit() throws Exception {
		if (context.isWithoutTransactionControl()) {
			status = NoSQLTransactionStatus.COMMITTED;
		} else {
			if (status != NoSQLTransactionStatus.ACTIVE) {
				throw new NoSQLTransactionException("A transação não foi iniciada");
			}

			log.debug("commit");

			notifySynchronizationsBeforeTransactionCompletion();

			getPersistenceContext().onBeforeExecuteCommit(getConnection());

			try {
				doCommit();
				status = NoSQLTransactionStatus.COMMITTED;
				getPersistenceContext().onAfterExecuteCommit(getConnection());
				notifySynchronizationsAfterTransactionCompletion(Status.STATUS_COMMITTED);
			} catch (Exception e) {
				log.error("commit failed", e);
				status = NoSQLTransactionStatus.FAILED_COMMIT;
				notifySynchronizationsAfterTransactionCompletion(Status.STATUS_UNKNOWN);
				throw new NoSQLTransactionException("commit failed", e);
			}
		}
	}

	protected abstract void doCommit();

	private void notifySynchronizationsBeforeTransactionCompletion() {
		synchronizationRegistry.notifySynchronizationsBeforeTransactionCompletion();
	}

	private void notifySynchronizationsAfterTransactionCompletion(int status) {
		synchronizationRegistry.notifySynchronizationsAfterTransactionCompletion(status);
	}

	protected NoSQLPersistenceContext getPersistenceContext() {
		return context;
	}

	protected NoSQLConnection getConnection() {
		return connection;
	}

	@Override
	public void rollback() {
		if (context.isWithoutTransactionControl()) {
			status = NoSQLTransactionStatus.ROLLED_BACK;
		} else {
			if (status != NoSQLTransactionStatus.ACTIVE && status != NoSQLTransactionStatus.FAILED_COMMIT) {
				throw new NoSQLTransactionException("Transação não foi iniciada");
			}

			log.debug("rollback");

			if (status != NoSQLTransactionStatus.FAILED_COMMIT) {
				try {
					getPersistenceContext().onBeforeExecuteRollback(getConnection());
					doRollback();
					status = NoSQLTransactionStatus.ROLLED_BACK;
					getPersistenceContext().onAfterExecuteRollback(getConnection());
					notifySynchronizationsAfterTransactionCompletion(Status.STATUS_ROLLEDBACK);
				} catch (Exception e) {
					notifySynchronizationsAfterTransactionCompletion(Status.STATUS_UNKNOWN);
					throw new NoSQLTransactionException("rollback failed", e);
				}
			}
		}
	}

	protected abstract void doRollback();

	@Override
	public boolean isActive() {
		if (context.isWithoutTransactionControl()) {
			return status == NoSQLTransactionStatus.ACTIVE; 
		}
		return status == NoSQLTransactionStatus.ACTIVE && doExtendedActiveCheck();
	}

	protected boolean doExtendedActiveCheck() {
		return true;
	}

	public void registerSynchronization(NoSQLTransactionSynchronization synchronization) {
		synchronizationRegistry.registerSynchronization(synchronization);
	}

}