package com.jklas.search.interceptors.ibatis.domain;


public class Company {

	private long id;
	
	private String name;
	
	private Country country;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
	
	@Override
	public String toString() {	
		return "ID: "+ getId() + "\n" + "NAME: " + getName();
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Country getCountry() {
		return country;
	}
	
}
