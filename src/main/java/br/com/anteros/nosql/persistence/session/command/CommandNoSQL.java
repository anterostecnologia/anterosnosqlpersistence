package br.com.anteros.nosql.persistence.session.command;

public interface CommandNoSQL {
	
	
	public Object execute();

	public String getTargetCollectionName();

}
