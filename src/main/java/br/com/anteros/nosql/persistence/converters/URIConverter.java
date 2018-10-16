package br.com.anteros.nosql.persistence.converters;

import java.net.URI;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class URIConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

	public URIConverter() {
		this(URI.class);
	}

	protected URIConverter(final Class<?> clazz) {
		super(clazz);
	}

	@Override
	public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
		if (val == null) {
			return null;
		}

		return URI.create(val.toString().replace("%46", "."));
	}

	@Override
	public String encode(final Object uri, final NoSQLDescriptionField descriptionField) {
		if (uri == null) {
			return null;
		}

		return uri.toString().replace(".", "%46");
	}
}
