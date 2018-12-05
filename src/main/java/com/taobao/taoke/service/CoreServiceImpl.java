package com.taobao.taoke.service;

import com.taobao.taoke.entity.Result;
import com.taobao.taoke.entity.TKGoodsInfo;
import com.taobao.taoke.entity.request.Article;
import com.taobao.taoke.entity.response.NewsMessage;
import com.taobao.taoke.entity.response.TextMessage;
import com.taobao.taoke.utils.MessageUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fhn
 * @version V1.0
 * @title: CoreServiceImpl
 * @package com.taobao.taoke.service
 * @description: 核心服务类
 * @date 2018/8/13 19:11
 */
@Service("coreService")
public class CoreServiceImpl implements CoreService {
    private static Logger logger = LoggerFactory.getLogger(CoreServiceImpl.class);
    @Autowired
    private TaokeService taokeService;

    /**
     * 处理微信发来的请求（包括事件的推送）
     *
     * @param request
     * @return
     */
    @Override
    public String processRequest(HttpServletRequest request) {
        String respMessage = null;
        try {
            // 默认返回的文本消息内容
            String respContent = "请求处理异常，请稍候尝试！";
            // xml请求解析
            Map<String, String> requestMap = MessageUtil.xmlToMap(request);
            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            // 回复文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(System.currentTimeMillis());
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            textMessage.setFuncFlag(0);

            // 创建图文消息
            NewsMessage newsMessage = new NewsMessage();
            newsMessage.setToUserName(fromUserName);
            newsMessage.setFromUserName(toUserName);
            newsMessage.setCreateTime(System.currentTimeMillis());
            newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
            newsMessage.setFuncFlag(0);

            List<Article> articleList = new ArrayList<Article>();
            // 接收文本消息内容
            String content = requestMap.get("Content");
            // 自动回复文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {

                //如果用户发送表情，则回复同样表情。
                if (isQqFace(content)) {
                    respContent = content;
                    textMessage.setContent("就你最皮，回敬你一个吧：" + respContent);
                    // 将文本消息对象转换成xml字符串
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                } else if (taokeService.hasId(content)) {
                    Result<TKGoodsInfo> result = taokeService.returnTKL(content);
                    TKGoodsInfo tkGoodsInfo = result.getData();
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("【优惠券】").append(tkGoodsInfo.getCouponTitle()).append("\n");
                    buffer.append("-----------------").append("\n");
                    buffer.append("復·制这段描述,").append("\n");
                    buffer.append(tkGoodsInfo.getTKL()).append(",打開【手机淘宝】即可查看").append("\n");

                    respContent = String.valueOf(buffer);
                    textMessage.setContent(respContent);
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                } else if (taokeService.hasTKL(content)) {
                    Result<TKGoodsInfo> result = taokeService.returnTKGoodsInfo(content);
                    StringBuffer buffer = new StringBuffer();
                    if (0 == result.getCode()) {
                        TKGoodsInfo tkGoodsInfo = result.getData();
                        buffer.append(tkGoodsInfo.getGoodsTitle()).append("\n");
                        buffer.append("【在售价】").append(tkGoodsInfo.getGoodsPrice()).append("\n");
                        buffer.append("【优惠券】").append(tkGoodsInfo.getCouponTitle()).append("\n");
                        buffer.append("【返利】").append(tkGoodsInfo.getOutCommission()).append("\n");
                        buffer.append("-----------------").append("\n");
                        buffer.append("復·制这段描述,").append("\n");
                        buffer.append(tkGoodsInfo.getTKL()).append(",打開【手机淘宝】即可查看").append("\n");
                    }else {
                        buffer.append("【错误】").append("\n").append(result.getMsg());
                    }

                    respContent = String.valueOf(buffer);
                    textMessage.setContent(respContent);
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                } else {
                    //回复固定消息
                    switch (content) {
                        case "8": {
                            StringBuffer buffer = new StringBuffer();
                            buffer.append("您好，我是小8，请回复数字选择服务：").append("\n\n");
                            buffer.append("1  领取1号支付宝红包").append("\n");
                            buffer.append("2  领取2号支付宝红包").append("\n");
                            buffer.append("#  换着领红包更大哦").append("\n\n");

                            buffer.append("11 可查看测试单图文").append("\n");
                            buffer.append("12 可测试多图文发送").append("\n");
                            buffer.append("13 可测试网址").append("\n");
                            buffer.append("或者您可以尝试发送表情").append("\n\n");

                            buffer.append("回复“8”显示此帮助菜单").append("\n");
                            respContent = String.valueOf(buffer);
                            textMessage.setContent(respContent);
                            respMessage = MessageUtil.textMessageToXml(textMessage);
                            break;
                        }
                        case "11": {
                            //测试单图文回复
                            Article article = new Article();
                            article.setTitle("微信公众帐号开发教程Java版");
                            // 图文消息中可以使用QQ表情、符号表情
                            article.setDescription("这是测试有没有换行\n\n如果有空行就代表换行成功\n\n点击图文可以跳转到百度首页");
                            // 将图片置为空
                            article.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                            article.setUrl("http://www.baidu.com");
                            articleList.add(article);
                            newsMessage.setArticleCount(articleList.size());
                            newsMessage.setArticles(articleList);
                            respMessage = MessageUtil.newsMessageToXml(newsMessage);
                            break;
                        }
                        case "12": {
                            //多图文发送
                            Article article1 = new Article();
                            article1.setTitle("紧急通知，不要捡这种钱！湛江都已经传疯了！\n");
                            article1.setDescription("");
                            article1.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                            article1.setUrl("http://www.baidu.com");

                            Article article2 = new Article();
                            article2.setTitle("湛江谁有这种女儿，请给我来一打！");
                            article2.setDescription("");
                            article2.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                            article2.setUrl("http://www.baidu.com");

                            articleList.add(article1);
                            articleList.add(article2);
                            newsMessage.setArticleCount(articleList.size());
                            newsMessage.setArticles(articleList);
                            respMessage = MessageUtil.newsMessageToXml(newsMessage);
                            break;
                        }

                        case "13": {
                            //测试网址回复
                            respContent = "<a href=\"http://www.baidu.com\">百度主页</a>";
                            textMessage.setContent(respContent);
                            // 将文本消息对象转换成xml字符串
                            respMessage = MessageUtil.textMessageToXml(textMessage);
                            break;
                        }

                        case "1": {
                            respContent = taokeService.getAlipayMoney(1);
                            textMessage.setContent(respContent);
                            // 将文本消息对象转换成xml字符串
                            respMessage = MessageUtil.textMessageToXml(textMessage);
                            break;
                        }

                        case "2": {
                            respContent = taokeService.getAlipayMoney(2);
                            textMessage.setContent(respContent);
                            // 将文本消息对象转换成xml字符串
                            respMessage = MessageUtil.textMessageToXml(textMessage);
                            break;
                        }

                        default: {
                            respContent = "很抱歉，现在小8暂时无法提供此功能给您使用。\n\n回复“8”显示帮助信息";
                            textMessage.setContent(respContent);
                            // 将文本消息对象转换成xml字符串
                            respMessage = MessageUtil.textMessageToXml(textMessage);
                        }
                    }
                }
            }
            // 图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                respContent = "您发送的是图片消息！";
                textMessage.setContent(respContent);
                // 将文本消息对象转换成xml字符串
                respMessage = MessageUtil.textMessageToXml(textMessage);
            }
            // 地理位置消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                respContent = "您发送的是地理位置消息！";
                textMessage.setContent(respContent);
                // 将文本消息对象转换成xml字符串
                respMessage = MessageUtil.textMessageToXml(textMessage);
            }
            // 链接消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "您发送的是链接消息！";
//                if (hasId(content)){
//                    respContent = taokeService.returnTKL(content);
//                }
                textMessage.setContent(respContent);
                // 将文本消息对象转换成xml字符串
                respMessage = MessageUtil.textMessageToXml(textMessage);

            }
            // 音频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "您发送的是音频消息！";
                textMessage.setContent(respContent);
                // 将文本消息对象转换成xml字符串
                respMessage = MessageUtil.textMessageToXml(textMessage);
            }
            //关注事件
            else if (MessageUtil.REQ_MESSAGE_TYPE_EVENT.equals(msgType)) {
                // 事件分成多种，分别判断处理
                String eventType = requestMap.get("Event");
                // 这里先写一个关注之后的事件
                if (MessageUtil.EVENT_TYPE_SUBSCRIBE.equals(eventType)) {
                    respContent = "感谢关注！回复数字“8”，即可唤出【小8】提示！" + "\n" +
                            "-----------------" + "\n" +
                            "目前基础转换功能已实现，今天先放出来测试下。" + "\n" +
                            "今天关注的都是老铁，很感谢！" + "\n" +
                            "【详细使用介绍】会尽快赶出来的！请等待。。";
                    textMessage.setContent(respContent);
                    // 将文本消息对象转换成xml字符串
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                   //message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return respMessage;
    }
    /**
     * @Description: 判断是否是QQ表情
     * @param [content]
     * @return boolean
     * @author fhn
     * @date 2018/8/14 22:23
     */
    public static boolean isQqFace(String content) {
        boolean result = false;
        // 判断QQ表情的正则表达式
        String qqfaceRegex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";
        Pattern p = Pattern.compile(qqfaceRegex);
        Matcher m = p.matcher(content);
        if (m.matches()) {
            result = true;
        }
        return result;
    }


}
