package com.taobao.taoke.entity.response;

/**
 * @author fhn
 * @version V1.0
 * @title: TextMessage
 * @package com.taobao.taoke.entity.response
 * @description: 文本消息
 * @date 2018/8/14 19:32
 */
public class TextMessage extends BaseMessage {
    // 回复的消息内容
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
