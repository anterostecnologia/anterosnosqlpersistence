package br.com.anteros.nosql.persistence.session;

import java.util.HashSet;
import java.util.Set;

public enum ShowCommandsType {

	ALL, NONE, SELECT, INSERT, UPDATE, DELETE;

	public static boolean contains(ShowCommandsType[] list, ShowCommandsType... items) {
		if ((list==null)||(items==null))
			return false;
		for (ShowCommandsType t : list) {
			for (ShowCommandsType item : items) {
				if (t.equals(item))
					return true;
			}
		}
		return false;
	}
	
	public static ShowCommandsType[] parse(String showSql) {
		String[] splitShowSql = showSql.split("\\,");
		return ShowCommandsType.parse(splitShowSql);
	}

	public static ShowCommandsType[] parse(String[] splitShowSql) {
		Set<ShowCommandsType> result = new HashSet<ShowCommandsType>();
		for (String s : splitShowSql){
			if (s.trim().toLowerCase().equals("true")){
				result.add(ShowCommandsType.ALL);
			} else if (s.trim().toLowerCase().equals("false")){
				result.add(ShowCommandsType.NONE);
			} else if (s.trim().toLowerCase().equals("select")){
				result.add(ShowCommandsType.SELECT);
			} else if (s.trim().toLowerCase().equals("insert")){
				result.add(ShowCommandsType.INSERT);
			} else if (s.trim().toLowerCase().equals("delete")){
				result.add(ShowCommandsType.DELETE);
			} else if (s.trim().toLowerCase().equals("update")){
				result.add(ShowCommandsType.UPDATE);
			}
		}
		return result.toArray(new ShowCommandsType[]{});
	}
	
	public static String parse(ShowCommandsType[] showSql) {
		String result = "";
		boolean appendDelimiter = false;
		for (ShowCommandsType s : showSql){
			if (appendDelimiter)
				result += ",";
			result += s.toString();
		}
		return result;
	}
	
	@Override
	public String toString() {
		if (this.equals(ALL))
		return "true";
		else if (this.equals(DELETE))
			return  "delete";
		else if (this.equals(INSERT))
			return "insert";
		else if (this.equals(NONE))
			return "false";
		else if (this.equals(SELECT))
			return "select";
		else if (this.equals(UPDATE))
			return "update";
		return "false";
	}
}
