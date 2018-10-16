package br.com.anteros.nosql.persistence.converters;

import java.util.Date;

import org.joda.time.DateTime;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class JodaDateTimeConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

    public JodaDateTimeConverter() {
        super(DateTime.class);
    }

    @Override
    public Object decode(Class<?> targetClass, Object fromNoSQLObject, NoSQLDescriptionField descriptionField) throws NoSQLMappingException {
        if (fromNoSQLObject == null) {
            return null;
        }

        if (fromNoSQLObject instanceof Date) {
            Date d = (Date) fromNoSQLObject;
            return new DateTime(d.getTime());
        }

        throw new RuntimeException(
                "Did not expect " + fromNoSQLObject.getClass().getName());
    }

    @Override
    public Object encode(Object value, NoSQLDescriptionField descriptionField) {
        if (value == null) {
            return null;
        }

        if (!(value instanceof DateTime)) {
            throw new RuntimeException(
                    "Did not expect " + value.getClass().getName());
        }

        DateTime dateTime = (DateTime) value;
        return dateTime.toDate();
    }

}