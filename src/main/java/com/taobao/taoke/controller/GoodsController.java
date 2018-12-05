package com.taobao.taoke.controller;

import com.taobao.taoke.entity.Result;
import com.taobao.taoke.entity.TKGoodsInfo;
import com.taobao.taoke.service.TaokeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author: fhn
 * @version: V1.0
 * @title: GoodsController
 * @package: com.taobao.taoke.controller
 * @description: TODO
 * @date: 2018/8/9 17:35
 */
@RestController
@RequestMapping("/taoke1")
public class GoodsController {
    private final static Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private TaokeService taokeService;

    @GetMapping("/getURL")
    public String getURL(@RequestParam("url") String url) throws Exception {
        return taokeService.returnTKL(url).toString();
    }

    @GetMapping("/gethttp")
    public String getsome(@RequestParam("url") String url) throws Exception {
//        String url = taokeService.jxTKL("无佣金无券t恤 €7xEgbXb7LCC€").getString("url");
//        String url = taokeService.jxTKL("有佣金无券衣服€QHJ6bcJS1P3€");
//        String url = taokeService.jxTKL("有公开券眼镜€np0lbcAXdLA€");
//        String url = taokeService.jxTKL("有佣金有内部券 €X4IfbXb8PuU€");
//        String url = taokeService.jxTKL("和规范化€YdiOb1jddTT€");
//        String url = taokeService.jxTKL("和规范化￥P7IxbczspAs￥");
//        String url1 = taokeService.getRedirectInfo(url);
        String s = taokeService.toTaoBaoUrl(url);
        return taokeService.returnTKL(s).toString();
    }

    @GetMapping("/jxTKL")
    public String jxTKL(){
//        return taokeService.jxTKL("和规范化€YdiOb1jddTT€");
//        String url = taokeService.getRedirectInfo("http://m.tb.cn/h.3elkV5d?sm=943e15");
        String url = taokeService.httpGetHeader("https://item.taobao.com/item.htm?id=561099048898", "","Location");
        return taokeService.toTaoBaoUrl(url);
    }

    @GetMapping("/returnTKGoods")
    public Result<TKGoodsInfo> returnTKGoods() throws Exception {
        return taokeService.returnTKGoodsInfo("问题他人淘口令 €OWd8bX2NlSv€");
//        return taokeService.returnTKGoodsInfo("问题手机壳 €j9TwbXf0Hpz€");
//        return taokeService.returnTKGoodsInfo("无佣金无券 €ZtYubXeK0oW€");
//        return taokeService.returnTKGoodsInfo("无佣金无券t恤 €7xEgbXb7LCC€");
//        return taokeService.returnTKGoodsInfo("有佣金无券衣服€QHJ6bcJS1P3€");
//        return taokeService.returnTKGoodsInfo("有公开券眼镜€np0lbcAXdLA€");
//        return taokeService.returnTKGoodsInfo("有佣金有内部券 €X4IfbXb8PuU€");
//        return taokeService.returnTKGoodsInfo("有佣金无券镜片 €bT6TbXePhhP€");
//        String url = taokeService.jxTKL("和规范化€YdiOb1jddTT€");
//        return taokeService.returnTKGoodsInfo("和规范化￥P7IxbczspAs￥");
//        String url1 = taokeService.getRedirectInfo(url);
//        return taokeService.toTaoBaoUrl(url);
    }

    @GetMapping("/testToTaoBaoUrl")
    public String testToTaoBaoUrl(@RequestParam("url") String url){
        return taokeService.testToTaoBaoUrl(url);
    }

//    @PostMapping("/doPost")
//    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
//        PrintWriter out = response.getWriter();
//        try {
//           Map<String, String> map = MessageUtil.xmlToMap(request);
//           String fromUserName = map.get("FromUserName");
//           String toUserName = map.get("ToUserName");
//           String msgType = map.get("MsgType");
//           String content = map.get("Content");
//
//           String message = null;
//           if ("text".equals(msgType)){
//               TextMessage text = new TextMessage();
//               text.setFromUserName(toUserName);
//               text.setToUserName(fromUserName);
//               text.setMsgType("text");
//               text.setCreateTime("你发送的信息是：" + content);
//               message = MessageUtil.textMessageToXml(text);
//           }
//           out.print(message);
//       }catch (DocumentException e){
//           e.printStackTrace();
//       }finally {
//           out.close();
//        }
//
//    }

}
