package com.taobao.taoke.controller;

import com.taobao.taoke.service.CoreService;
import com.taobao.taoke.utils.CheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: fhn
 * @version: V1.0
 * @title: GoodsController
 * @package: com.taobao.taoke.controller
 * @description: 核心控制层
 * @date: 2018/8/9 17:35
 */
@RestController
@RequestMapping("/taoke")
public class CoreController {
    private final static Logger logger = LoggerFactory.getLogger(CoreController.class);

    private final CoreService coreService;

    @Autowired
    public CoreController(CoreService coreService) {
        this.coreService = coreService;
    }

    /**
     * @description: 验证是否来自微信服务器的消息
     * @param: [request, response]
     * @return: void
     * @author: fhn
     * @date: 2018/8/17 15:24
     */
    @GetMapping("")
    public void tokenCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        echostr = CheckUtil.tokenCheck(signature, timestamp, nonce, echostr);

        response.getWriter().write(echostr);
    }

    /**
     * @description: 调用核心业务类接收消息、处理消息跟推送消息
     * @param: [request]
     * @return: java.lang.String
     * @author: fhn
     * @date: 2018/8/17 15:23
     */
    @PostMapping("")
    public String post(HttpServletRequest request) throws Exception {
        return coreService.processRequest(request);
    }

}
