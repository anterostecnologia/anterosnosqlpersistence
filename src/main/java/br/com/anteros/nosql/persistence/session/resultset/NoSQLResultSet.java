package br.com.anteros.nosql.persistence.session.resultset;

public interface NoSQLResultSet {
	
	boolean next() throws NoSQLResultSetException;	
	
	void close() throws NoSQLResultSetException;
	
	 boolean isBeforeFirst() throws NoSQLResultSetException;
	 
	 boolean isAfterLast() throws NoSQLResultSetException;
	 
	 boolean isFirst() throws NoSQLResultSetException;
	 
	 boolean isLast() throws NoSQLResultSetException;
	 
	 void beforeFirst() throws NoSQLResultSetException;
	 
	 void afterLast() throws NoSQLResultSetException;
	 
	 boolean first() throws NoSQLResultSetException;
	 
	 boolean last() throws NoSQLResultSetException;
	 
	 boolean isClosed() throws NoSQLResultSetException;
	 
	 int getRowNumber() throws NoSQLResultSetException;
	 
	 boolean previous() throws NoSQLResultSetException;
	 
	 Object getRowObject() throws NoSQLResultSetException;	

}
