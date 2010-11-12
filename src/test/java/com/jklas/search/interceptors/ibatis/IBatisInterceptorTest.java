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
package com.jklas.search.interceptors.ibatis;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import junit.framework.Assert;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jklas.search.interceptors.ibatis.Utils;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.memory.MemoryIndex;
import com.jklas.search.index.memory.MemoryIndexWriterFactory;
import com.jklas.search.indexer.DefaultIndexerService;
import com.jklas.search.indexer.pipeline.DefaultIndexingPipeline;
import com.jklas.search.interceptors.SearchInterceptor;
import com.jklas.search.interceptors.ibatis.dao.PersonDao;
import com.jklas.search.interceptors.ibatis.domain.Company;
import com.jklas.search.interceptors.ibatis.domain.Person;
import com.jklas.search.interceptors.ibatis.dto.InsertPersonDto;

public class IBatisInterceptorTest {

	private static SqlSession session = null;
	private static SqlSessionFactory sqlMapper = null;

	@BeforeClass
	public static void startup() throws IOException, ClassNotFoundException, SQLException, SearchEngineMappingException {
		String resource = "com/jklas/search/interceptors/ibatis/Configuration.xml";		
		Reader reader = Resources.getResourceAsReader(resource);
		sqlMapper = new SqlSessionFactoryBuilder().build(reader);
		
		Utils.configureAndMap(InsertPersonDto.class);
		Utils.configureAndMap(Person.class);
		
		new Ibatis3Interceptor().setSearchInterceptor(new SearchInterceptor(
				new DefaultIndexerService(
						new DefaultIndexingPipeline(),
						MemoryIndexWriterFactory.getInstance())));
		
		setupDDL();
	}

	private static void setupDDL() throws ClassNotFoundException, SQLException {
		Class.forName("org.hsqldb.jdbcDriver");
		Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:klink","sa","");
//		Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost","sa","");
		conn.setAutoCommit(false);
		Statement st = conn.createStatement();
		st.execute("CREATE SCHEMA KLINK AUTHORIZATION DBA;");
		st.execute("CREATE TABLE KLINK.PERSON (ID BIGINT NOT NULL, FIRST_NAME VARCHAR(256) NOT NULL, LAST_NAME VARCHAR(256) NOT NULL, EMAIL VARCHAR(256), PASSWORD VARCHAR(32) NOT NULL, COMPANY_ID BIGINT, PRIMARY KEY(ID))");
		st.execute("CREATE TABLE KLINK.COMPANY (ID BIGINT NOT NULL, NAME VARCHAR(256) NOT NULL, PRIMARY KEY(ID))");
		st.execute("CREATE TABLE KLINK.JOB (ID BIGINT NOT NULL, TITLE VARCHAR(256) NOT NULL, PRIMARY KEY(ID))");
		st.execute("CREATE TABLE KLINK.EMPLOYEE (PERSON_ID BIGINT NOT NULL, COMPANY_ID BIGINT NOT NULL, PRIMARY KEY(PERSON_ID,COMPANY_ID))");
		st.execute("CREATE TABLE KLINK.CONTACT (FROM_ID BIGINT NOT NULL, TO_ID BIGINT NOT NULL, PRIMARY KEY(FROM_ID, TO_ID))");
		st.execute("CREATE TABLE KLINK.COUNTRY(ID BIGINT NOT NULL, NAME VARCHAR(256), MAIN_LANG_ID BIGINT, PRIMARY KEY(ID))");
		st.execute("CREATE TABLE KLINK.COUNTRY_LANG(COUNTRY_ID BIGINT NOT NULL, LANG_ID BIGINT NOT NULL, PRIMARY KEY(COUNTRY_ID, LANG_ID))");
		st.execute("CREATE TABLE KLINK.LANGUAGE(ID BIGINT NOT NULL, BASE_NAME VARCHAR(256), LOCAL_NAME VARCHAR(256))");

		st.execute("create sequence klink.person_id start with 1 increment by 1");
		st.execute("create sequence klink.company_id start with 1 increment by 1");
		st.execute("create sequence klink.general_id start with 1 increment by 1");

		st.execute("CREATE TABLE DUAL(DUMMY VARCHAR(5))");
		st.execute("INSERT INTO DUAL VALUES('DUMMY')");
		st.execute("SET TABLE DUAL READONLY TRUE");		
		st.close();
		conn.commit();
		conn.close();
	}

	@Before
	public void before() {
		MemoryIndex.renewAllIndexes();
		session = sqlMapper.openSession();
	}
	
	@After
	public void after() {		
		if(session!=null) {		
//			session.commit();
			session.rollback();
			session.close();
		}
	}


	@Test
	public void PersonIsIndexed() throws IOException {		
		Company c = new Company();
		c.setId(1);
		Person p = new Person("Can","Cun","can@cun.com");
		p.setCompany(c);
		
		new PersonDao(session).insertPerson(p, "123456");
		
		Assert.assertEquals(1,MemoryIndex.getDefaultIndex().getObjectCount());
	}
	
	@Test
	public void PersonAndContactsAreIndexed() throws IOException {
				
		Company c = new Company();
		c.setId(1);
		
		Person q = new Person("Q","Q","q@q.com");
		q.setCompany(c);
		
		Person p = new Person("P","P","p@p.com");
		p.setCompany(c);

		LinkedList<Person> contacts = new LinkedList<Person>();
		contacts.add(q);
		p.setContacts(contacts);
				
		PersonDao personDao = new PersonDao(session);
		personDao.insertPerson(q, "654321");
		personDao.insertPerson(p, "123456");
		
		Assert.assertEquals(2,MemoryIndex.getDefaultIndex().getObjectCount());				
	}
	
	@Test
	public void DeletesAreIntercepted() throws IOException {		
		Company c = new Company();
		c.setId(1);
		Person p = new Person("Can","Cun","can@cun.com");
		p.setCompany(c);
		
		Assert.assertEquals(0,MemoryIndex.getDefaultIndex().getObjectCount());
		
		new PersonDao(session).insertPerson(p, "123456");
		
		Assert.assertEquals(1,MemoryIndex.getDefaultIndex().getObjectCount());
		
		new PersonDao(session).deletePerson(p);
		
		Assert.assertEquals(0,MemoryIndex.getDefaultIndex().getObjectCount());
	}
}
