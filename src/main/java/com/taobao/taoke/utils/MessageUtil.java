package com.taobao.taoke.utils;

import com.taobao.taoke.entity.request.Article;
import com.taobao.taoke.entity.response.NewsMessage;
import com.taobao.taoke.entity.response.TextMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: fhn
 * @version: V1.0
 * @title: MessageUtil
 * @package: com.taobao.taoke.utils
 * @description: 消息工具类
 * @date: 2018/8/12 19:31
 */
public class MessageUtil {
    /**
     * 返回消息类型：文本
     */
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";

    /**
     * 返回消息类型：音乐
     */
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";

    /**
     * 返回消息类型：图文
     */
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";

    /**
     * 请求消息类型：文本
     */
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";

    /**
     * 请求消息类型：图片
     */
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";

    /**
     * 请求消息类型：链接
     */
    public static final String REQ_MESSAGE_TYPE_LINK = "link";

    /**
     * 请求消息类型：地理位置
     */
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";

    /**
     * 请求消息类型：音频
     */
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";

    /**
     * 请求消息类型：推送
     */
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";

    /**
     * 事件类型：subscribe(订阅) and 未关注群体扫描二维码
     */
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

    /**
     * 事件类型：已关注群体扫描二维码
     */
    public static final String EVENT_TYPE_SCAN="SCAN";
    /**
     * 事件类型：unsubscribe(取消订阅)
     */
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

    /**
     * 事件类型：CLICK(自定义菜单点击事件)
     */
    public static final String EVENT_TYPE_CLICK = "CLICK";

    /**
     * 事件类型：VIEW(点击自定义菜单跳转链接时的事件)
     */
    public static final String EVENT_TYPE_VIEW = "VIEW";

    /**
     * 事件类型：transfer_customer_service(把消息推送给客服)
     */
    public static final String EVENT_TYPE_TRANSFER_CUSTOMER_SERVICE = "transfer_customer_service";

    /**
     * 接收xml消息转到Map集合
     */
    @SuppressWarnings("unchecked")
    //屏蔽某些编译时的警告信息(在强制类型转换的时候编译器会给出警告)

    /**
     * @description: xml转成Map
     * @param: [request]
     * @author: fhn
     * @date: 2018/8/17 15:18
     */
    public static Map<String,String> xmlToMap(HttpServletRequest request) throws IOException, DocumentException {
        Map<String,String> map = new HashMap<String, String>(16);

        //从request中获取输入流
        InputStream ins = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document doc = reader.read(ins);
        //获取根元素
        Element root = doc.getRootElement();
        //获取子节点集合
        List<Element> list = root.elements();
        //遍历节点元素，存入Map
        for (Element element : list) {
            //把每个元素存入map
            map.put(element.getName(),element.getText());
        }
        //释放输入流
        ins.close();
        return map;
    }

    /**
     * Text消息转xml
     */
    public static String textMessageToXml(TextMessage textMessage){
        XStream xStream = new XStream();
        xStream.alias("xml", textMessage.getClass());
        return xStream.toXML(textMessage);
    }

    /**
     * 图文消息对象转换成xml
     */
    public static String newsMessageToXml(NewsMessage newsMessage){
        XStream xStream = new XStream();
        xStream.alias("xml", newsMessage.getClass());
        xStream.alias("item", new Article().getClass());
        return xStream.toXML(newsMessage);
    }
    /**
     * 扩展xstream，使其支持CDATA块
     *
     * @date 2013-05-19
     */
    private static XStream xstream = new XStream(new XppDriver() {
        @Override
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;

                @SuppressWarnings("unchecked")
                @Override
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }
                @Override
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });

}
