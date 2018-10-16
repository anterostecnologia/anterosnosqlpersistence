package br.com.anteros.nosql.persistence.session.query.filter;

public interface Visitable {
  public void accept(final FilterVisitor visitor) throws FilterException;
}
