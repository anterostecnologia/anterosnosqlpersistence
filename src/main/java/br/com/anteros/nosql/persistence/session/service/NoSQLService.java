package br.com.anteros.nosql.persistence.session.service;

import java.util.Optional;

import br.com.anteros.nosql.persistence.session.NoSQLSession;
import br.com.anteros.nosql.persistence.session.NoSQLSessionFactory;
import br.com.anteros.nosql.persistence.session.query.Example;
import br.com.anteros.nosql.persistence.session.query.NoSQLQuery;
import br.com.anteros.nosql.persistence.session.query.Page;
import br.com.anteros.nosql.persistence.session.query.Pageable;
import br.com.anteros.nosql.persistence.session.query.Sort;

public interface NoSQLService<T, ID> {
	

	public <S extends T> S save(S entity) throws Exception;

	public <S extends T> Iterable<S> save(Iterable<S> entities);
	
	public Optional<T> findById(ID id);

	public boolean existsById(ID id);

	public Iterable<T> findAll();
	
	public Iterable<T> find(String query);

	public Iterable<T> findById(Iterable<ID> ids);
	
	public Iterable<T> find(NoSQLQuery<?> query);
	
	public Page<T> findWithPage(NoSQLQuery<?> query);
	
	public Page<T> findWithPage(NoSQLQuery<?> query, Pageable pageable);

	public long count();

	public void removeById(ID id);

	public void remove(T entity);

	public void remove(Iterable<? extends T> entities);

	public void removeAll();

	public Iterable<T> findAll(Sort sort);

	public Page<T> findAll(Pageable pageable);

	public <S extends T> Optional<S> findOne(Example<S> example);

	public <S extends T> Iterable<S> find(Example<S> example);

	public <S extends T> Iterable<S> find(Example<S> example, Sort sort);

	public <S extends T> Page<S> find(Example<S> example, Pageable pageable);

	public <S extends T> long count(Example<S> example);

	public <S extends T> boolean exists(Example<S> example);
	
	public NoSQLSession<?> getSession();

	public NoSQLSession<?> openSession();
	
	public void setSession(NoSQLSession<?> session);
	
	public NoSQLSessionFactory getSessionFactory();
	
	public NoSQLQuery<?> parseRsql(String rsql);

}
