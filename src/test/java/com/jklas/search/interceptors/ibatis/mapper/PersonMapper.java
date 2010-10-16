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
