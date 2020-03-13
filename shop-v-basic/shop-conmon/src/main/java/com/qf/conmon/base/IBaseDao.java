package com.qf.conmon.base;

import org.springframework.stereotype.Repository;

@Repository
public interface IBaseDao<T> {

    int deleteByPrimaryKey(Long cid);

    int insert(T record);

    int insertSelective(T record);

    T selectByPrimaryKey(Long cid);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);


}
