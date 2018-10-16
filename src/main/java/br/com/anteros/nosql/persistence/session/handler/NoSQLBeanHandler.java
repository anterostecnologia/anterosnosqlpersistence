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
package br.com.anteros.nosql.persistence.session.handler;

import br.com.anteros.nosql.persistence.session.resultset.NoSQLResultSet;

public class NoSQLBeanHandler implements NoSQLResultSetHandler{

    private final Class<?> type;
    private final NoSQLRowProcessor convert;

    public NoSQLBeanHandler(Class<?> type, NoSQLRowProcessor convert) {
        this.type = type;
        this.convert = convert;
    }

    public Object handle(NoSQLResultSet rs) throws Exception {
        return rs.next() ? this.convert.toBean(rs, this.type) : null;
    }

}
