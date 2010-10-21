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

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.jklas.search.SearchEngine;
import com.jklas.search.index.dto.IndexObjectDto;
import com.jklas.search.interceptors.SearchInterceptor;


/**
 * 
 * Interceptor para iBATIS 3.0
 * 
 * @author Julián
 * @since 1.0
 * @date 2009-08-31
 * 
 */
@Intercepts({@Signature(
		type= Executor.class,
		method = "update",
		args = {MappedStatement.class,Object.class})})

public class Ibatis3Interceptor implements Interceptor {

	private static SearchInterceptor searchInterceptor ;
		
	// for ibatis
	public Ibatis3Interceptor() {
//		SearchFactory.getInstance().getGenericService("SearchInterceptor");
	}
	
	public void setSearchInterceptor(SearchInterceptor searchInterceptor) {
		Ibatis3Interceptor.searchInterceptor = searchInterceptor;
	}
	
	public SearchInterceptor getSearchInterceptor() {
		return searchInterceptor;
	}
	
	public Object intercept(Invocation invocation) throws Throwable {
		Object invocationReturn = invocation.proceed();

		if(invocation.getArgs().length < 2) return invocationReturn;		
		
		if(!(invocation.getArgs()[0] instanceof MappedStatement)) return invocationReturn;		

		
		MappedStatement st = (MappedStatement)invocation.getArgs()[0];
		Object entity = invocation.getArgs()[1];
		
		if(!SearchEngine.getInstance().getConfiguration().isMapped(entity)) return invocationReturn;
		
		try {
			switch(st.getSqlCommandType()) {
				case INSERT:	searchInterceptor.create(new IndexObjectDto(entity));
								break;
	
				case UPDATE:	searchInterceptor.update(new IndexObjectDto(entity));
								break;
	
				case DELETE:	searchInterceptor.delete(new IndexObjectDto(entity));
								break;

				default:  		return invocationReturn;

			}
		} catch (Exception e) {
			LogFactory.getLog(getClass()).error("Error al indexar el entity: "+entity+". Invocation:"+invocation,e);
		}

		return invocationReturn;
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {

	}
}
