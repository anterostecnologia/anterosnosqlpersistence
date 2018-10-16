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
package br.com.anteros.nosql.persistence.session.repository;

import br.com.anteros.nosql.persistence.session.query.Pageable;
import br.com.anteros.nosql.persistence.session.query.Sort;

public class PageRequest extends AbstractPageRequest {

	private static final long serialVersionUID = -4541509938956089562L;
	private Sort sort;

	public PageRequest(int page, int size) {
		super(page, size);	
	}
	
	public PageRequest(int page, int size, Sort sort) {
		super(page, size);
		this.sort = sort;
	}

	public Pageable next() {
		return new PageRequest(getPageNumber() + 1, getPageSize());
	}

	public PageRequest previous() {
		return getPageNumber() == 0 ? this : new PageRequest(getPageNumber() - 1, getPageSize());
	}

	public Pageable first() {
		return new PageRequest(0, getPageSize());
	}

	@Override
	public String toString() {
		return String.format("Page request [number: %d, size %d]", getPageNumber(), getPageSize());
	}

	@Override
	public Sort getSort() {
		return sort;
	}
}
