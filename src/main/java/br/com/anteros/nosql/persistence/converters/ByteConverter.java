package br.com.anteros.nosql.persistence.converters;

import java.lang.reflect.Array;

import br.com.anteros.nosql.persistence.metadata.NoSQLDescriptionField;

public class ByteConverter extends NoSQLTypeConverter implements NoSQLSimpleValueConverter {

	public ByteConverter() {
		super(byte.class, Byte.class, byte[].class, Byte[].class);
	}

	@Override
	public Object decode(final Class<?> targetClass, final Object val, final NoSQLDescriptionField descriptionField) {
		if (val == null) {
			return null;
		}

		if (val.getClass().equals(targetClass)) {
			return val;
		}

		if (val instanceof Number) {
			return ((Number) val).byteValue();
		}

		if (targetClass.isArray() && val.getClass().equals(byte[].class)) {
			return convertToWrapperArray((byte[]) val);
		}
		return Byte.parseByte(val.toString());
	}

	@Override
	public Object encode(final Object value, final NoSQLDescriptionField descriptionField) {
		if (value instanceof Byte[]) {
			return super.encode(convertToPrimitiveArray((Byte[]) value), descriptionField);
		}
		return super.encode(value, descriptionField);
	}

	Object convertToPrimitiveArray(final Byte[] values) {
		final int length = values.length;
		final Object array = Array.newInstance(byte.class, length);
		for (int i = 0; i < length; i++) {
			Array.set(array, i, values[i]);
		}
		return array;
	}

	Object convertToWrapperArray(final byte[] values) {
		final int length = values.length;
		final Object array = Array.newInstance(Byte.class, length);
		for (int i = 0; i < length; i++) {
			Array.set(array, i, values[i]);
		}
		return array;
	}
}
