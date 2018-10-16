package br.com.anteros.nosql.persistence.session.resultset;

public interface NoSQLScrollableResultSet {

	public boolean next() throws NoSQLResultSetException;

	public boolean previous() throws NoSQLResultSetException;

	public boolean scroll(int i) throws NoSQLResultSetException;

	public boolean last() throws NoSQLResultSetException;

	public boolean first() throws NoSQLResultSetException;

	public void beforeFirst() throws NoSQLResultSetException;

	public void afterLast() throws NoSQLResultSetException;

	public boolean isFirst() throws NoSQLResultSetException;

	public boolean isLast() throws NoSQLResultSetException;

	public void close() throws NoSQLResultSetException;

	public Object[] get() throws NoSQLResultSetException;	

	public int getRowNumber() throws NoSQLResultSetException;

	public boolean setRowNumber(int rowNumber) throws NoSQLResultSetException;

}
