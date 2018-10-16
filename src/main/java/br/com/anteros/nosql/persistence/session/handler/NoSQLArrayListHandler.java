package br.com.anteros.nosql.persistence.session.handler;

import java.util.ArrayList;
import java.util.List;

import br.com.anteros.nosql.persistence.session.resultset.NoSQLResultSet;

public class NoSQLArrayListHandler implements NoSQLResultSetHandler {
	
    private final NoSQLRowProcessor convert;


    public NoSQLArrayListHandler(NoSQLRowProcessor convert) {
        super();
        this.convert = convert;
    }
	
	public List<Object> handle(NoSQLResultSet rs) throws Exception {
		List<Object> rows = new ArrayList<Object>();
		while (rs.next()) {
			rows.add(this.handleRow(rs));
		}
		return rows;
	}

	protected Object[] handleRow(NoSQLResultSet rs) throws Exception {
		return convert.toArray(rs);
	}


}
