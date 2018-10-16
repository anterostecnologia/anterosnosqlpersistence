
package br.com.anteros.nosql.persistence.session;


public class FindAndReplaceOptions {

	private boolean returnNew;
	private boolean upsert;


	public static FindAndReplaceOptions options() {
		return new FindAndReplaceOptions();
	}


	public static FindAndReplaceOptions empty() {
		return new FindAndReplaceOptions();
	}


	public FindAndReplaceOptions returnNew() {

		this.returnNew = true;
		return this;
	}

	public FindAndReplaceOptions upsert() {

		this.upsert = true;
		return this;
	}


	public boolean isReturnNew() {
		return returnNew;
	}


	public boolean isUpsert() {
		return upsert;
	}

}
