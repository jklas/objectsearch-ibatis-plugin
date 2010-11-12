/**
 * Object Search Framework
 *
 * Copyright (C) 2010 Julian Klas
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.jklas.search.interceptors.ibatis.mapper;

import java.util.List;

import com.jklas.search.interceptors.ibatis.domain.Person;
import com.jklas.search.interceptors.ibatis.dto.InsertPersonDto;
import com.jklas.search.util.Pair;

public interface PersonMapper {
	public Person selectPerson(int id);
	
	public List<Person> selectByCompany(long id);
	
	public void insertPerson(InsertPersonDto insertDto);
	
	public void deletePerson(Person person);

	public void insertContact(Pair<Long, Long> pair);

	public void deleteContacts(long id);
}
