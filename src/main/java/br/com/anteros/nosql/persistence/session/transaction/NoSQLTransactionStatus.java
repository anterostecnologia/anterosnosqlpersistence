package br.com.anteros.nosql.persistence.session.transaction;

public enum NoSQLTransactionStatus {

	NOT_ACTIVE,

	ACTIVE,

	COMMITTED,

	ROLLED_BACK,

	FAILED_COMMIT;
}