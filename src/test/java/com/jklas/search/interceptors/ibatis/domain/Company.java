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
package com.jklas.search.interceptors.ibatis.dao;

import org.apache.ibatis.session.SqlSession;

import com.jklas.search.interceptors.ibatis.domain.Person;
import com.jklas.search.interceptors.ibatis.dto.InsertPersonDto;
import com.jklas.search.interceptors.ibatis.mapper.PersonMapper;
import com.jklas.search.util.Pair;

public class PersonDao {

	private PersonMapper personMapper;
	
	public PersonDao(SqlSession session) {
		this.personMapper = session.getMapper(PersonMapper.class);
	}
	
	public void insertPerson(Person person, String password) {
		InsertPersonDto pDto = new InsertPersonDto();
		pDto.setPassword(password);
		pDto.setPerson(person);
		personMapper.insertPerson(pDto);
		
		for (Person contact : person.getContacts()) {
			personMapper.insertContact(new Pair<Long,Long>(person.getId(), contact.getId()));
		}

	}

	public void deletePerson(Person person) {
		this.personMapper.deletePerson(person);
		this.personMapper.deleteContacts(person.getId());
	}	
}
