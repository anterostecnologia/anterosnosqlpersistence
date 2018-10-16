package br.com.anteros.nosql.persistence.session;

public class FindAndModifyOptions {

	private boolean returnNew;
	private boolean upsert;
	private boolean remove;

	public static FindAndModifyOptions options() {
		return new FindAndModifyOptions();
	}


	public static FindAndModifyOptions of(FindAndModifyOptions source) {

		FindAndModifyOptions options = new FindAndModifyOptions();
		if (source == null) {
			return options;
		}

		options.returnNew = source.returnNew;
		options.upsert = source.upsert;
		options.remove = source.remove;

		return options;
	}

	public FindAndModifyOptions returnNew(boolean returnNew) {
		this.returnNew = returnNew;
		return this;
	}

	public FindAndModifyOptions upsert(boolean upsert) {
		this.upsert = upsert;
		return this;
	}

	public FindAndModifyOptions remove(boolean remove) {
		this.remove = remove;
		return this;
	}

	
	public boolean isReturnNew() {
		return returnNew;
	}

	public boolean isUpsert() {
		return upsert;
	}

	public boolean isRemove() {
		return remove;
	}

	
}
