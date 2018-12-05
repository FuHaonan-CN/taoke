package com.taobao.taoke.entity;

/**
 * @title: TKGoodsInfo
 * @package: com.taobao.taoke.entity
 * @description: 存储淘客商品所有信息
 * @author: fhn
 * @date: 2018/8/17 21:02
 * @version: V1.0
 */
public class TKGoodsInfo {
    /** 商品标题 */
    private String goodsTitle;

    /** 商品价格 */
    private double goodsPrice;

    /** 淘口令 */
    private String TKL;

    /** 最大佣金率 */
    private double maxCommissionRate;

    /** 优惠券标题 */
    private String couponTitle;

    /** 优惠券图片 */
    private String picUrl;

    /** 淘客商品、优惠券二合一长链接 */
    private String goodsAndCouponUrl;

    /** 对外佣金 */
    private double outCommission;

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getTKL() {
        return TKL;
    }

    public void setTKL(String TKL) {
        this.TKL = TKL;
    }

    public double getMaxCommissionRate() {
        return maxCommissionRate;
    }

    public void setMaxCommissionRate(double maxCommissionRate) {
        this.maxCommissionRate = maxCommissionRate;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getGoodsAndCouponUrl() {
        return goodsAndCouponUrl;
    }

    public void setGoodsAndCouponUrl(String goodsAndCouponUrl) {
        this.goodsAndCouponUrl = goodsAndCouponUrl;
    }

    public double getOutCommission() {
        return outCommission;
    }

    public void setOutCommission(double outCommission) {
        this.outCommission = outCommission;
    }

    public String getCouponTitle() {
        return couponTitle;
    }

    public void setCouponTitle(String couponTitle) {
        this.couponTitle = couponTitle;
    }

    @Override
    public String toString() {
        return "TKGoodsInfo{" +
                "goodsTitle='" + goodsTitle + '\'' +
                ", goodsPrice=" + goodsPrice +
                ", TKL='" + TKL + '\'' +
                ", maxCommissionRate=" + maxCommissionRate +
                ", couponTitle='" + couponTitle + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", goodsAndCouponUrl='" + goodsAndCouponUrl + '\'' +
                ", outCommission=" + outCommission +
                '}';
    }
}
