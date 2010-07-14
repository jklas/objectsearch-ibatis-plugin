package com.jklas.search.interceptors.ibatis.domain;

public class Country {

	private long id;
	
	private String name;
	
	private long mainLanguageId;

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setMainLanguageId(long mainLanguageId) {
		this.mainLanguageId = mainLanguageId;
	}

	public long getMainLanguageId() {
		return mainLanguageId;
	}
	
}
