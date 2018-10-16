package br.com.anteros.nosql.persistence.session.service;

import java.util.Optional;

import br.com.anteros.core.utils.Assert;
import br.com.anteros.core.utils.TypeResolver;
import br.com.anteros.nosql.persistence.session.NoSQLSession;
import br.com.anteros.nosql.persistence.session.NoSQLSessionFactory;
import br.com.anteros.nosql.persistence.session.query.Example;
import br.com.anteros.nosql.persistence.session.query.NoSQLQuery;
import br.com.anteros.nosql.persistence.session.query.Page;
import br.com.anteros.nosql.persistence.session.query.Pageable;
import br.com.anteros.nosql.persistence.session.query.Sort;
import br.com.anteros.nosql.persistence.session.repository.NoSQLRepository;
import br.com.anteros.nosql.persistence.session.repository.PageRequest;


public abstract class AbstractSimpleService<T, ID> implements NoSQLService<T, ID> {

	protected NoSQLRepository<T, ID> repository;
	protected NoSQLSessionFactory sessionFactory;

	public AbstractSimpleService() {
	}

	public AbstractSimpleService(NoSQLSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		Class<?>[] typeArguments = TypeResolver.resolveRawArguments(AbstractSimpleService.class, getClass());
		if (typeArguments != null) {
			this.repository = doGetDefaultRepository(sessionFactory, typeArguments[0]);
		}
	}	

	protected void checkRepository() {
		Assert.notNull(repository, "O repositório não foi criado. Verifique se a sessionFactory foi atribuida.");
	}
	
	public NoSQLSessionFactory getSessionFactory() {
		checkRepository();
		return sessionFactory;
	}

	public void setSessionFactory(NoSQLSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		Class<?>[] typeArguments = TypeResolver.resolveRawArguments(AbstractSimpleService.class, getClass());
		if (typeArguments != null) {
			this.repository = doGetDefaultRepository(sessionFactory, typeArguments[0]);
		}
	}
	
	protected abstract NoSQLRepository<T, ID> doGetDefaultRepository(NoSQLSessionFactory sessionFactory, Class entityClass);
	
	@Override
	public void setSession(NoSQLSession<?> session) {
		checkRepository();
		repository.setSession(session);
	}

	@Override
	public NoSQLSession<?> openSession() {
		checkRepository();
		return repository.openSession();
	}

	@Override
	public <S extends T> S save(S entity) throws Exception {
		checkRepository();
		return repository.save(entity);
	}

	@Override
	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		checkRepository();
		return repository.save(entities);
	}

	@Override
	public Optional<T> findById(ID id) {
		checkRepository();
		return repository.findById(id);
	}

	@Override
	public boolean existsById(ID id) {
		checkRepository();
		return repository.existsById(id);
	}

	@Override
	public Iterable<T> findAll() {
		checkRepository();
		return repository.findAll();
	}

	@Override
	public Iterable<T> find(String query) {
		checkRepository();
		return repository.find(query);
	}
	
	@Override
	public Iterable<T> find(NoSQLQuery<?> query) {
		checkRepository();
		return repository.find(query);
	}

	@Override
	public Iterable<T> findById(Iterable<ID> ids) {
		checkRepository();
		return repository.findById(ids);
	}
	
	@Override
	public Page<T> findWithPage(NoSQLQuery<?> query) {
		checkRepository();
		return repository.find(query, new PageRequest(query.getLimit(), (int) query.getOffset()));
	}

	@Override
	public Page<T> findWithPage(NoSQLQuery<?> query, Pageable pageable) {
		checkRepository();
		return repository.find(query, new PageRequest(query.getLimit(), (int) query.getOffset()));
	}


	@Override
	public long count() {
		checkRepository();
		return repository.count();
	}

	@Override
	public void removeById(ID id) {
		checkRepository();
		repository.removeById(id);
	}

	@Override
	public void remove(T entity) {
		checkRepository();
		repository.remove(entity);
	}

	@Override
	public void remove(Iterable<? extends T> entities) {
		checkRepository();
		repository.remove(entities);
	}

	@Override
	public void removeAll() {
		checkRepository();
		repository.removeAll();
	}

	@Override
	public Iterable<T> findAll(Sort sort) {
		checkRepository();
		return repository.findAll(sort);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		checkRepository();
		return repository.findAll(pageable);
	}

	@Override
	public <S extends T> Optional<S> findOne(Example<S> example) {
		checkRepository();
		return repository.findOne(example);
	}

	@Override
	public <S extends T> Iterable<S> find(Example<S> example) {
		checkRepository();
		return repository.find(example);
	}

	@Override
	public <S extends T> Iterable<S> find(Example<S> example, Sort sort) {
		checkRepository();
		return repository.find(example, sort);
	}

	@Override
	public <S extends T> Page<S> find(Example<S> example, Pageable pageable) {
		checkRepository();
		return repository.find(example, pageable);
	}

	@Override
	public <S extends T> long count(Example<S> example) {
		checkRepository();
		return repository.count(example);
	}

	@Override
	public <S extends T> boolean exists(Example<S> example) {
		checkRepository();
		return repository.exists(example);
	}

	@Override
	public NoSQLSession<?> getSession() {
		checkRepository();
		return repository.getSession();
	}

	

}
