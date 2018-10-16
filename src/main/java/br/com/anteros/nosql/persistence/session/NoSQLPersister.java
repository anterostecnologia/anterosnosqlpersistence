package br.com.anteros.nosql.persistence.session;

import java.util.Collection;

public interface NoSQLPersister {

	public Object save(NoSQLSession session, Object object) throws Exception;

	public void save(NoSQLSession session, Object[] objects) throws Exception;

	public void save(NoSQLSession session, Collection<?> objects) throws Exception;

	public void remove(NoSQLSession session, Object object) throws Exception;

	public void remove(NoSQLSession session, Object[] objects) throws Exception;

}
