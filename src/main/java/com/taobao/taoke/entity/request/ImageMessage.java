package com.taobao.taoke.entity.request;

/**
 * @author fhn
 * @version V1.0
 * @title: ImageMessage
 * @package com.taobao.taoke.entity.request
 * @description: 图片消息
 * @date 2018/8/14 19:36
 */
public class ImageMessage extends BaseMessage {
    // 图片链接
    private String PicUrl;

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }
}
