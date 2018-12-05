package com.taobao.taoke.enums;

/**
 * @author: fhn
 * @version: V1.0
 * @title: Goods
 * @package: com.taobao.taoke.entity
 * @description: 枚举错误信息
 * @date: 2018/8/9 15:31
 */
public enum ResultEnum {
    UNKONW_ERROR(-1, "未知错误"),
    SUCCESS(0, "成功"),
    LOGGER_TKL_JX_ERROR(200, "淘口令解析时错误"),
    TKL_JX_BACK_ERROR(101, "抱歉，淘口令出错了，请按说明介绍重新复制淘口令！"),
    LOGGER_TKL_JX_PRICE_ERROR(201, "淘口令解析后获取参数列表(price)时错误。"),
    LOGGER_TKL_JX_BACK_ERROR(202, "淘口令解析后获取参数列表时错误。"),
    LOGGER_GET_TKGOODS_ERROR(203, "获取淘客商品时错误，该宝贝无佣金无券。"),
    GET_TKGOODS_ERROR(102, "抱歉，该商品无优惠券及返利！"),
    ;

    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ERROR:\t" +
                "ResultEnum{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
