package com.taobao.taoke.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: fhn
 * @version: V1.0
 * @title: TaokeInfoProperties
 * @package: com.taobao.taoke.properties
 * @description: 自己淘客信息及相关api请求地址
 * @date: 2018/8/11 14:32
 */
@Component
@ConfigurationProperties(prefix = "taoke")
public class TaokeInfoProperties {
    private String session;
    private Integer adzone_id;
    private Integer site_id;
    private String goods_request_url;
    private String tkl_request_url;
    private String tkl_jx_url;
    private String alipay_money1;
    private String alipay_money2;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Integer getAdzone_id() {
        return adzone_id;
    }

    public void setAdzone_id(Integer adzone_id) {
        this.adzone_id = adzone_id;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public String getGoods_request_url() {
        return goods_request_url;
    }

    public void setGoods_request_url(String goods_request_url) {
        this.goods_request_url = goods_request_url;
    }

    public String getTkl_request_url() {
        return tkl_request_url;
    }

    public void setTkl_request_url(String gettkl_request_url) {
        this.tkl_request_url = gettkl_request_url;
    }

    public String getTkl_jx_url() {
        return tkl_jx_url;
    }

    public void setTkl_jx_url(String tkl_jx_url) {
        this.tkl_jx_url = tkl_jx_url;
    }

    public String getAlipay_money1() {
        return alipay_money1;
    }

    public void setAlipay_money1(String alipay_money1) {
        this.alipay_money1 = alipay_money1;
    }

    public String getAlipay_money2() {
        return alipay_money2;
    }

    public void setAlipay_money2(String alipay_money2) {
        this.alipay_money2 = alipay_money2;
    }
}
