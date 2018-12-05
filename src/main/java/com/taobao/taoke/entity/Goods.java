package com.taobao.taoke.entity;

/**
 * @author: fhn
 * @version: V1.0
 * @title: Goods
 * @package: com.taobao.taoke.entity
 * @description: TODO
 * @date: 2018/8/9 15:31
 */
public class Goods {
    /** 商品id */
    private long itemId;
    /** 1：PC，2：无线，默认：1 */
    private Integer platform;
    /** 营销计划链接中的me参数 */
    private String me;
    /** 渠道关系ID，仅适用于渠道推广场景 */
    private String relationId;

    public Goods() {
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getMe() {
        return me;
    }

    public void setMe(String me) {
        this.me = me;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }
}
