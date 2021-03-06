package com.qf.mapper;

import com.qf.conmon.base.IBaseDao;
import com.qf.entity.TUser;

public interface TUserMapper extends IBaseDao<TUser> {

    TUser selectByUsername(String username);
}
