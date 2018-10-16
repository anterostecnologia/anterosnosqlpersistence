package br.com.anteros.nosql.persistence.session.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import br.com.anteros.core.utils.Assert;
import br.com.anteros.core.utils.TypeResolver;
import br.com.anteros.nosql.persistence.session.NoSQLSession;
import br.com.anteros.nosql.persistence.session.NoSQLSessionFactory;
import br.com.anteros.nosql.persistence.session.query.Example;
import br.com.anteros.nosql.persistence.session.query.NoSQLQuery;
import br.com.anteros.nosql.persistence.session.query.Page;
import br.com.anteros.nosql.persistence.session.query.Pageable;
import br.com.anteros.nosql.persistence.session.query.Sort;
import br.com.anteros.nosql.persistence.session.query.impl.PageImpl;


@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractSimpleRepository<T, ID> implements NoSQLRepository<T, ID> {

	protected NoSQLSession<?> session;
	protected NoSQLSessionFactory sessionFactory;
	protected Class<T> persistentClass;


	public AbstractSimpleRepository(NoSQLSession session) {
		this.session = session;
		Class<?>[] typeArguments = TypeResolver.resolveRawArguments(AbstractSimpleRepository.class, getClass());
		if (typeArguments != null) {
			this.persistentClass = (Class<T>) typeArguments[0];
		}
	}

	public AbstractSimpleRepository(NoSQLSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		Class<?>[] typeArguments = TypeResolver.resolveRawArguments(AbstractSimpleRepository.class, getClass());
		if (typeArguments != null) {
			this.persistentClass = (Class<T>) typeArguments[0];
		}
	}

	public AbstractSimpleRepository(NoSQLSession<?> session, Class<T> type) {
		this.session = session;
		this.persistentClass = type;
	}

	public AbstractSimpleRepository(NoSQLSessionFactory sessionFactory, Class<T> type) {
		this.sessionFactory = sessionFactory;
		this.persistentClass = type;
	}

	@Override
	public NoSQLSession<?> getSession() {
		if (session != null)
			return session;
		else if (sessionFactory != null) {
			try {
				return sessionFactory.getCurrentSession();
			} catch (Exception e) {
				throw new NoSQLRepositoryException(e);
			}
		}
		throw new NoSQLRepositoryException(
				"Não foi configurado nenhuma NoSQLSession ou NoSQLSessionFactory para o repositório.");
	}

	@Override
	public <S extends T> S save(S entity) {
		Assert.notNull(entity, "Entity must not be null!");
		return getSession().save(entity);
	}

	@Override
	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		Assert.notNull(entities, "The given Iterable of entities not be null!");
		Stream<S> source = StreamSupport.stream(entities.spliterator(), false);
		return source.map(this::save).collect(Collectors.toList());
	}

	@Override
	public Optional<T> findById(ID id) {
		Assert.notNull(id, "The given id must not be null!");
		return Optional.of(getSession().findById(id, persistentClass));
	}

	@Override
	public boolean existsById(ID id) {
		Assert.notNull(id, "The given id must not be null!");
		return getSession().exists(doGetIdQuery(id), persistentClass);
	}

	@Override
	public Iterable<T> findAll() {
		return getSession().findAll(persistentClass);
	}

	@Override
	public Iterable<T> find(String query) {
		return getSession().find(doParseQuery(query), persistentClass);
	}

	@Override
	public Iterable<T> findById(Iterable<ID> ids) {
		return getSession().find(doGetIdsQuery(ids), persistentClass);
	}
	

	@Override
	public long count() {
		return getSession().count(persistentClass);
	}
	
	@Override
	public long count(NoSQLQuery query) {
		return getSession().count(query, persistentClass);
	}
	
	@Override
	public long count(String query) {
		return count(doParseQuery(query));
	}


	@Override
	public void removeById(ID id) {
		getSession().remove(doGetIdQuery(id), persistentClass);
	}

	@Override
	public void remove(T entity) {
		getSession().remove(entity);

	}

	@Override
	public void remove(Iterable<? extends T> entities) {
		Assert.notNull(entities, "The given Iterable of entities not be null!");
		entities.forEach(this::remove);
	}

	@Override
	public void removeAll() {
		getSession().remove(doGetEmptyQuery(), persistentClass);
	}

	@Override
	public Iterable<T> findAll(Sort sort) {
		Assert.notNull(sort, "Sort must not be null!");
		NoSQLQuery query = doGetQueryWithSort(sort);
		return this.find(query);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		Assert.notNull(pageable, "Pageable must not be null!");
		NoSQLQuery query = doGetQueryWithPage(pageable);
		Long count = count();
		List<T> list = this.find(query);
		return new PageImpl<T>(list, pageable, count);
	}

	@Override
	public <S extends T> Optional<S> findOne(Example<S> example) {

		Assert.notNull(example, "Sample must not be null!");

		NoSQLQuery query = doGetQueryByExample(example, null, null);

		S result = (S) getSession().findOne(query, persistentClass);
		return Optional.ofNullable(result);
	}

	@Override
	public <S extends T> Iterable<S> find(Example<S> example) {
		return find(example, Sort.unsorted());
	}

	@Override
	public <S extends T> Iterable<S> find(Example<S> example, Sort sort) {
		Assert.notNull(example, "Sample must not be null!");
		Assert.notNull(sort, "Sort must not be null!");

		NoSQLQuery query = doGetQueryByExample(example, sort, null);

		return getSession().find(query, persistentClass);
	}

	@Override
	public <S extends T> Page<S> find(Example<S> example, Pageable pageable) {
		Assert.notNull(example, "Sample must not be null!");
		Assert.notNull(pageable, "Pageable must not be null!");
		NoSQLQuery query = doGetQueryByExample(example, null, pageable);
		List<S> list = getSession().find(query, persistentClass);
		return getPage(list, pageable, () -> getSession().count(query, persistentClass));
	}

	@Override
	public <S extends T> long count(Example<S> example) {
		Assert.notNull(example, "Sample must not be null!");

		NoSQLQuery query = doGetQueryByExample(example, null, null);
		return getSession().count(query, persistentClass);
	}

	@Override
	public <S extends T> boolean exists(Example<S> example) {
		Assert.notNull(example, "Sample must not be null!");

		NoSQLQuery query = doGetQueryByExample(example, null, null);
		return getSession().exists(query, persistentClass);
	}

	@Override
	public List<T> find(NoSQLQuery query) {
		if (query == null) {
			return Collections.emptyList();
		}
		return getSession().find(query, persistentClass);
	}
	
	@Override
	public Page<T> find(String query, Pageable pageable) {
		Assert.notNull(query, "Query string must not be null!");
		Assert.notNull(pageable, "Pageable must not be null!");
		Stream<T> source = StreamSupport.stream(this.find(query).spliterator(), false);
		return getPage(source.collect(Collectors.toList()), pageable, () -> count(query));
	}

	@Override
	public Page<T> find(NoSQLQuery<?> query, Pageable pageable) {
		Assert.notNull(query, "Query must not be null!");
		Assert.notNull(pageable, "Pageable must not be null!");
		Stream<T> source = StreamSupport.stream(this.find(query).spliterator(), false);
		return getPage(source.collect(Collectors.toList()), pageable, () -> count(query));
	}	

	public static <T> Page<T> getPage(List<T> content, Pageable pageable, LongSupplier totalSupplier) {

		Assert.notNull(content, "Content must not be null!");
		Assert.notNull(pageable, "Pageable must not be null!");
		Assert.notNull(totalSupplier, "TotalSupplier must not be null!");

		if (pageable.isUnpaged() || pageable.getOffset() == 0) {

			if (pageable.isUnpaged() || pageable.getPageSize() > content.size()) {
				return new PageImpl<>(content, pageable, content.size());
			}

			return new PageImpl<>(content, pageable, totalSupplier.getAsLong());
		}

		if (content.size() != 0 && pageable.getPageSize() > content.size()) {
			return new PageImpl<>(content, pageable, pageable.getOffset() + content.size());
		}

		return new PageImpl<>(content, pageable, totalSupplier.getAsLong());
	}
	
	@Override
	public void setSession(NoSQLSession<?> session) {
		this.session = session;		
	}

	@Override
	public NoSQLSession<?> openSession() {
		if (sessionFactory == null)
			throw new NoSQLRepositoryException(
					"Nenhuma fábrica de sessões foi atribuída ao repositório não é possível criar uma nova sessão NoSQL.");
		return sessionFactory.openSession();
	}

	@Override
	public NoSQLSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected abstract NoSQLQuery doGetIdQuery(Object id);

	protected abstract NoSQLQuery doGetQueryWithSort(Sort sort);

	protected abstract NoSQLQuery doGetEmptyQuery();

	protected abstract NoSQLQuery doGetQueryWithPage(Pageable page);

	protected abstract NoSQLQuery doGetQueryByExample(Example<?> example, Sort sort, Pageable page);

	protected abstract NoSQLQuery doGetIdsQuery(Iterable<ID> ids);

	protected abstract NoSQLQuery doParseQuery(String query);

	
	

	

}
