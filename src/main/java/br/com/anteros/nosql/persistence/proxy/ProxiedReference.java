package br.com.anteros.nosql.persistence.proxy;

//CHECKSTYLE:OFF
public interface ProxiedReference extends AnterosProxyObject {
	Class<?> __getReferenceObjClass();

	boolean __isFetched();

	Object __unwrap();
}
