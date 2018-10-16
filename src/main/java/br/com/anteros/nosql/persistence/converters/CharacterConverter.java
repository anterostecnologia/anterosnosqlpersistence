package br.com.anteros.nosql.persistence.converters;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class CharacterConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

	public CharacterConverter() {
        super(char.class, Character.class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object fromNoSQLObject, final NoSQLDescriptionField descriptionField) {
        if (fromNoSQLObject == null) {
            return null;
        }

        if (fromNoSQLObject instanceof String) {
            final char[] chars = ((String) fromNoSQLObject).toCharArray();
            if (chars.length == 1) {
                return chars[0];
            } else if (chars.length == 0) {
                return (char) 0;
            }
        }
        throw new NoSQLMappingException("Trying to map multi-character data to a single character: " + fromNoSQLObject);
    }

    @Override
    public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
        return value == null || value.equals('\0') ? null : String.valueOf(value);
    }
}
