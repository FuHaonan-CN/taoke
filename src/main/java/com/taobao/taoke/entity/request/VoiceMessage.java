package com.taobao.taoke.entity.request;

/**
 * @author fhn
 * @version V1.0
 * @title: VoiceMessage
 * @package com.taobao.taoke.entity.request
 * @description: 音频消息
 * @date 2018/8/14 19:39
 */
public class VoiceMessage extends BaseMessage {
    // 媒体ID
    private String MediaId;
    // 语音格式
    private String Format;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }
}
