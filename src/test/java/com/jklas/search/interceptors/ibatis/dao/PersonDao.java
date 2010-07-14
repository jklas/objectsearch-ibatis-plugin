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
