package br.com.anteros.nosql.persistence.session.transaction.impl;

import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.anteros.nosql.persistence.session.transaction.NoSQLTransactionSynchronization;

public class NoSQLSynchronizationRegistry {

	private static final Logger log = LoggerFactory.getLogger(NoSQLSynchronizationRegistry.class);

	private LinkedHashSet<NoSQLTransactionSynchronization> synchronizations;

	public void registerSynchronization(NoSQLTransactionSynchronization synchronization) {
		if (synchronization == null) {
			throw new NullSynchronizationException();
		}

		if (synchronizations == null) {
			synchronizations = new LinkedHashSet<NoSQLTransactionSynchronization>();
		}

		boolean added = synchronizations.add(synchronization);
		if (!added) {
			log.info("Synchronization [{}] was already registered", synchronization);
		}
	}

	public void notifySynchronizationsBeforeTransactionCompletion() {
		if (synchronizations != null) {
			for (NoSQLTransactionSynchronization synchronization : synchronizations) {
				try {
					synchronization.beforeCompletion();
				} catch (Throwable t) {
					log.error("exception calling user Synchronization [{}]", synchronization, t);
				}
			}
		}
	}

	public void notifySynchronizationsAfterTransactionCompletion(int status) {
		if (synchronizations != null) {
			for (NoSQLTransactionSynchronization synchronization : synchronizations) {
				try {
					synchronization.afterCompletion(status);
				} catch (Throwable t) {
					log.error("exception calling user Synchronization [{}]", synchronization, t);
				}
			}
		}
	}
}
