package com.qf.conmon.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CartItem implements Serializable {

    private Long productId;
    private int count;
    private Date updateTime;
}
