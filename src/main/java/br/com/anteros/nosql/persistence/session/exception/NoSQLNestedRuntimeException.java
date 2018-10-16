package br.com.anteros.nosql.persistence.session.exception;

public class NoSQLNestedRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 5439915454935047936L;

	static {
		NoSQLNestedExceptionUtils.class.getName();
	}

	public NoSQLNestedRuntimeException(String msg) {
		super(msg);
	}

	public NoSQLNestedRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	@Override

	public String getMessage() {
		return NoSQLNestedExceptionUtils.buildMessage(super.getMessage(), getCause());
	}

	public Throwable getRootCause() {
		return NoSQLNestedExceptionUtils.getRootCause(this);
	}

	public Throwable getMostSpecificCause() {
		Throwable rootCause = getRootCause();
		return (rootCause != null ? rootCause : this);
	}

	public boolean contains(Class<?> exType) {
		if (exType == null) {
			return false;
		}
		if (exType.isInstance(this)) {
			return true;
		}
		Throwable cause = getCause();
		if (cause == this) {
			return false;
		}
		if (cause instanceof NoSQLNestedRuntimeException) {
			return ((NoSQLNestedRuntimeException) cause).contains(exType);
		} else {
			while (cause != null) {
				if (exType.isInstance(cause)) {
					return true;
				}
				if (cause.getCause() == cause) {
					break;
				}
				cause = cause.getCause();
			}
			return false;
		}
	}

}
