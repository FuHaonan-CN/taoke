package com.taobao.taoke.entity.request;

/**
 * @author: fhn
 * @version: V1.0
 * @title: LinkMessage
 * @package: com.taobao.taoke.entity.request
 * @description: 链接消息
 * @date: 2018/8/14 19:37
 */
public class LinkMessage extends BaseMessage {
    /**消息标题*/
    private String Title;
    /**消息描述*/
    private String Description;
    /**消息链接*/
    private String Url;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
