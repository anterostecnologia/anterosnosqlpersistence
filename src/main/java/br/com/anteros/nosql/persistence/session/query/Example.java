package br.com.anteros.nosql.persistence.session.query;


import java.lang.reflect.Proxy;

import br.com.anteros.core.utils.Assert;
import br.com.anteros.core.utils.ClassUtils;

public interface Example<T> {

	static <T> Example<T> of(T probe) {
		return new TypedExample<>(probe, ExampleMatcher.matching());
	}

	static <T> Example<T> of(T probe, ExampleMatcher matcher) {
		return new TypedExample<T>(probe, matcher);
	}

	T getProbe();

	ExampleMatcher getMatcher();

	@SuppressWarnings("unchecked")
	default Class<T> getProbeType() {
		return (Class<T>) getUserClass(getProbe().getClass());
	}
	
	
	public static Class<?> getUserClass(Object source) {

		Assert.notNull(source, "Source object must not be null!");

		return getUserClass(getTargetClass(source));
	}
	
	
	public static Class<?> getTargetClass(Object candidate) {
		Assert.notNull(candidate, "Candidate object must not be null");
		Class<?> result = (ClassUtils.isCglibProxy(candidate) || Proxy.isProxyClass(candidate.getClass()) ? candidate.getClass().getSuperclass() : candidate.getClass());
		return result;
	}
	
}
