package com.qf.conmon.base;

public abstract class BaseServiceImpl<T> implements IBaseService<T> {

    public abstract IBaseDao<T> getBaseDao();

    @Override
    public int deleteByPrimaryKey(Long cid) {
        return getBaseDao().deleteByPrimaryKey(cid);
    }

    @Override
    public int insert(T record) {
        return getBaseDao().insert(record);
    }

    @Override
    public int insertSelective(T record) {
        return getBaseDao().insertSelective(record);
    }

    @Override
    public T selectByPrimaryKey(Long cid) {
        return getBaseDao().selectByPrimaryKey(cid);
    }

    @Override
    public int updateByPrimaryKeySelective(T record) {
        return getBaseDao().updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(T record) {
        return getBaseDao().updateByPrimaryKey(record);
    }
}
