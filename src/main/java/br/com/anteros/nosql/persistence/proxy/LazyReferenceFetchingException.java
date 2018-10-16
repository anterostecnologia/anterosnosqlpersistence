package br.com.anteros.nosql.persistence.proxy;

import java.util.ConcurrentModificationException;

public class LazyReferenceFetchingException extends ConcurrentModificationException {
	private static final long serialVersionUID = 1L;

	public LazyReferenceFetchingException(final String msg) {
		super(msg);
	}
}
