/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package br.com.anteros.nosql.persistence.session.query.parameter;

import java.util.HashMap;
import java.util.Map;

import br.com.anteros.nosql.persistence.metadata.annotations.EnumValue;
import br.com.anteros.nosql.persistence.metadata.annotations.EnumValues;

public class EnumeratedParameter extends SubstitutedParameter {

	private EnumeratedFormat format;

	protected EnumeratedParameter(String name, Object value) {
		super(name, value);
	}
	
	public static EnumeratedParameter defaultParameter(String name, Enum<?>... value){
		EnumeratedParameter result = new EnumeratedParameter(name,value);
		result.format = EnumeratedFormat.STRING;
		return result;
	}
	public static EnumeratedParameter withFormatParameter(String name, EnumeratedFormat format, Enum<?>... value){
		EnumeratedParameter result = new EnumeratedParameter(name,value);
		result.format = format;
		return result;
	}

	@Override
	public String toString() {
		String result = "";
		for (Object ev : (Enum[]) this.getValue()) {
			if (!"".equals(result))
				result += ",";
			String newValue;
			if (ev.getClass().isEnum()) {
				Map<String, String> enumValues = new HashMap<String, String>();
				if (ev.getClass().isAnnotationPresent(EnumValues.class)) {
					EnumValues enumAnnotation = ev.getClass().getAnnotation(EnumValues.class);
					if (enumAnnotation.value().length > 0) {
						for (EnumValue value : enumAnnotation.value()) {
							enumValues.put(value.enumValue(), value.value());
						}
					}
				} else {
					for (Object value : ev.getClass().getEnumConstants()) {
						enumValues.put(value.toString(), value.toString());
					}
				}
				newValue = enumValues.get(ev.toString());
			} else
				newValue = ev.toString();

			if (format.equals(EnumeratedFormat.ORDINAL))
				result += newValue;
			else
				result += "'" + newValue + "'";
		}
		return result;
	}

}
