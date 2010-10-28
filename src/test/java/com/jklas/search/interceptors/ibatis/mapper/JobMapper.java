package com.jklas.search.interceptors.ibatis.mapper;

import com.jklas.search.interceptors.ibatis.domain.Job;

public interface JobMapper {
	Job selectJob(int id);
}
