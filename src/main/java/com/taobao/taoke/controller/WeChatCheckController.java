package com.taobao.taoke.controller;

import com.taobao.taoke.utils.CheckUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author fhn
 * @version V1.0
 * @title: WeChatCheckController
 * @package: com.taobao.taoke.controller
 * @description: 与微信对接登陆验证
 * @date 2018/8/13 17:20
 */
@RestController
@RequestMapping("/wx")
public class WeChatCheckController {
    @GetMapping("/tokenCheck1")
    public void tokenCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        echostr = CheckUtil.tokenCheck(signature, timestamp, nonce, echostr);

        response.getWriter().write(echostr);
    }
}
