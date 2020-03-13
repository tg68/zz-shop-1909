package com.qf.mapper;

import com.qf.conmon.base.IBaseDao;
import com.qf.entity.TProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TProductMapper extends IBaseDao<TProduct> {

    List<TProduct> selectAll();
}
