package com.jklas.search.interceptors.ibatis.dto;

import com.jklas.search.annotations.IndexReference;
import com.jklas.search.annotations.IndexableContainer;
import com.jklas.search.annotations.SearchContained;
import com.jklas.search.interceptors.ibatis.domain.Person;

@IndexableContainer
public class InsertPersonDto {
	
	@SearchContained(reference=IndexReference.SELF)
	private Person person;
	
	private String password;

	public void setPerson(Person person) {
		this.person = person;
	}

	public Person getPerson() {
		return person;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
}
