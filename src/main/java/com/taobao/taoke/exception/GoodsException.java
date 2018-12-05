package com.taobao.taoke.exception;


import com.taobao.taoke.enums.ResultEnum;


public class GoodsException extends RuntimeException{

    private Integer code;

    public GoodsException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
