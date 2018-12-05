package com.taobao.taoke.utils;

import com.taobao.taoke.entity.Result;
import com.taobao.taoke.entity.TKGoodsInfo;

/**
 * @author: fhn
 * @version: V1.0
 * @title: Goods
 * @package: com.taobao.taoke.entity
 * @description: TODO
 * @date: 2018/8/9 15:31
 */
public class ResultUtil {

    public static Result success(Object object) {
        Result result = new Result();
        result.setCode(0);
        result.setMsg("成功");
        result.setData(object);
        return result;
    }

    public static Result success() {
        return success(null);
    }

    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
