package br.com.anteros.nosql.persistence.metadata.configuration;

import java.lang.reflect.Method;

public class ClassMethodPair {
	public final Class<?> clazz;
	public final Method method;

	ClassMethodPair(final Class<?> c, final Method m) {
		clazz = c;
		method = m;
	}
}
