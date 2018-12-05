package com.taobao.taoke.entity.request;

/**
 * @author fhn
 * @version V1.0
 * @title: MenuMessage
 * @package com.taobao.taoke.entity.request
 * @description: TODO
 * @date 2018/8/14 19:38
 */
public class MenuMessage extends BaseMessage {
    private String EventKey;

    public String getEventKey() {
        return EventKey;
    }

    public void setEventKey(String eventKey) {
        EventKey = eventKey;
    }
}
