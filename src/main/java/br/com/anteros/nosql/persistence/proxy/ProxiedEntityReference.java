package br.com.anteros.nosql.persistence.proxy;

import br.com.anteros.nosql.persistence.converters.Key;

//CHECKSTYLE:OFF
public interface ProxiedEntityReference extends ProxiedReference {
    Key<?> __getKey();
}
