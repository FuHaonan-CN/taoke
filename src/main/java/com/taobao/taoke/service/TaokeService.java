package com.taobao.taoke.service;

import com.taobao.taoke.entity.Goods;
import com.taobao.taoke.entity.Result;
import com.taobao.taoke.entity.TKGoodsInfo;
import com.taobao.taoke.enums.ResultEnum;
import com.taobao.taoke.exception.GoodsException;
import com.taobao.taoke.properties.TaokeInfoProperties;
import com.taobao.taoke.utils.ResultUtil;
import net.sf.json.JSONObject;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author: fhn
 * @version: V1.0
 * @title: TaokeService
 * @package: com.taobao.taoke.service
 * @description: 淘客相关service处理
 * @date: 2018/8/11 15:16
 */
@Service
public class TaokeService {
    private static final Logger logger = LoggerFactory.getLogger(TaokeService.class);
    private static final char TKL_SIGN1 = '￥';
    private static final char TKL_SIGN2 = '€';
    private static final int TKL_LENGTH = 11;
    //无券有佣金时
    private static final int HAS_COUPON = 5;
    private static final int JX_TKL_TRUE_CODE = 0;
    //对外佣金百分比
    private static final double OUT_COMMISSION = 0.008;
    private static final String NOCOUPON = "抱歉，您的宝贝暂无优惠券。。但仍有佣金返利呦。。";
    DecimalFormat df = new DecimalFormat("#.00");

    private final TaokeInfoProperties taokeInfo;

    @Autowired
    private TaokeService(TaokeInfoProperties taokeInfo) {
        this.taokeInfo = taokeInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result returnTKGoodsInfo(String TKL) throws Exception {
        JSONObject TKCouponInfo = jxTKL(TKL);
        TKGoodsInfo tkGoodsInfo = new TKGoodsInfo();
        if ("false".equals(TKCouponInfo.getString("suc"))){
            logger.info(ResultEnum.LOGGER_TKL_JX_BACK_ERROR.toString());
            return ResultUtil.error(ResultEnum.TKL_JX_BACK_ERROR.getCode(), ResultEnum.TKL_JX_BACK_ERROR.getMsg());
        }
        //存入商品标题
        tkGoodsInfo.setGoodsTitle(TKCouponInfo.getString("content"));
        //存入图片链接(小图)thumb_pic_url
        tkGoodsInfo.setPicUrl(TKCouponInfo.getString("pic_url"));
        //存入原始价格
        if(!hasOneAttributes(TKCouponInfo,"price")) {
            logger.info(ResultEnum.LOGGER_TKL_JX_PRICE_ERROR.toString());
            return ResultUtil.error(ResultEnum.TKL_JX_BACK_ERROR.getCode(), ResultEnum.TKL_JX_BACK_ERROR.getMsg());
        }
        double price = Double.parseDouble(TKCouponInfo.getString("price"));
        tkGoodsInfo.setGoodsPrice(price);
        //不带id长链接转为正常一般淘宝链接
        String taoBaoUrl = toTaoBaoUrl(TKCouponInfo.getString("url"));
        //初始化淘宝商品，附上基础信息，id，手机端
        Goods goods = getGoods(taoBaoUrl);
        //得到淘客商品
        JSONObject TKGoods = getTKGoods(goods);
        if (TKGoods == null) {
            return ResultUtil.error(ResultEnum.GET_TKGOODS_ERROR.getCode(), ResultEnum.GET_TKGOODS_ERROR.getMsg());
        } else if (TKGoods.size() > HAS_COUPON) {
            tkGoodsInfo.setCouponTitle(TKGoods.getString("coupon_info"));
        } else {
            tkGoodsInfo.setCouponTitle(NOCOUPON);
        }
        //存入佣金率
        double maxCommissionRate = Double.parseDouble(TKGoods.getString("max_commission_rate"));
        tkGoodsInfo.setMaxCommissionRate(maxCommissionRate);
        //存入对外佣金
        double outCommission = Double.parseDouble(df.format(price * maxCommissionRate * OUT_COMMISSION));
        tkGoodsInfo.setOutCommission(outCommission);
        //存入淘客商品、优惠券二合一长链接
        tkGoodsInfo.setGoodsAndCouponUrl(TKGoods.getString("coupon_click_url"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tkGoodsInfo.setTKL(getTKL(tkGoodsInfo));
        return ResultUtil.success(tkGoodsInfo);
    }


    /**
     * @param: [weChatURL]
     * @return: java.lang.String
     * @description: 传入带有商品id的淘宝链接，获取淘口令
     * @author: fhn
     * @date: 2018/8/11 16:26
     */
    @Transactional(rollbackFor = Exception.class)
    public Result returnTKL(String weChatURL) throws Exception {
        TKGoodsInfo tkGoodsInfo = new TKGoodsInfo();
        Goods goods = getGoods(weChatURL);
        JSONObject TKGoods = getTKGoods(goods);
        //无佣金无券，获取淘客商品失败
        if (TKGoods == null) {
            logger.info(ResultEnum.GET_TKGOODS_ERROR.toString());
            return  ResultUtil.error(ResultEnum.GET_TKGOODS_ERROR.getCode(), ResultEnum.GET_TKGOODS_ERROR.getMsg());
        }else if (TKGoods.size() > HAS_COUPON) {
            tkGoodsInfo.setCouponTitle(TKGoods.getString("coupon_info"));
        } else {
            tkGoodsInfo.setCouponTitle(NOCOUPON);
        }
        double maxCommissionRate = Double.parseDouble(TKGoods.getString("max_commission_rate"));
        tkGoodsInfo.setMaxCommissionRate(maxCommissionRate);
        tkGoodsInfo.setGoodsAndCouponUrl(TKGoods.getString("coupon_click_url"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tkGoodsInfo.setTKL(getTKL(tkGoodsInfo));
        return ResultUtil.success(tkGoodsInfo);
    }

    /**
     * @description: 接收正常的淘宝链接, 将信息存入一个淘宝实体类中
     * @param: [WeChatURL]
     * @return: com.taobao.taoke.entity.Goods
     * @author: fhn
     * @date: 2018/8/17 14:38
     */
    private static Goods getGoods(String url) {
        String itemId = urlToMap(url).get("id");
        Goods goods = new Goods();
        goods.setItemId(Long.parseLong(itemId));
        goods.setPlatform(1);
        return goods;
    }

    /**
     * @description: 通过传入商品id，httpclient请求获取json对象
     * @param: [goods]
     * @return: net.sf.json.JSONObject
     * @author: fhn
     * @date: 2018/8/17 14:39
     */
    private JSONObject getTKGoods(Goods goods) throws Exception {
        Map<String, String> param = new HashMap<>(16);
        param.put("session", taokeInfo.getSession());
        param.put("adzone_id", taokeInfo.getAdzone_id().toString());
        param.put("site_id", taokeInfo.getSite_id().toString());
        param.put("item_id", String.valueOf(goods.getItemId()));
        param.put("platform", goods.getPlatform().toString());
        JSONObject TKGoods = getJsonResult(taokeInfo.getGoods_request_url(), param);
        if (5 == TKGoods.size()) {
            //throw new GoodsException(ResultEnum.GET_TKGOODS_ERROR);
            //无佣金无券，获取淘客商品失败
            logger.info(ResultEnum.LOGGER_GET_TKGOODS_ERROR.toString());
            return null ;
        }
        return TKGoods.getJSONObject("result").getJSONObject("data");
    }

    /**
     * @description: httpPost带参请求获取JSONObject对象
     * @param: [url, param]
     * @return: net.sf.json.JSONObject
     * @author: fhn
     * @date: 2018/8/17 15:14
     */
    private static JSONObject getJsonResult(String url, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response;
        String resultString;
        JSONObject jsonResult = null;
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "UTF-8");
                httpPost.setEntity(entity);
                logger.info("executing request " + httpPost.getURI());
                // 执行http请求
                response = httpClient.execute(httpPost);
                try {
                    HttpEntity entity1 = response.getEntity();
                    //读取服务器返回过来的json字符串数据
                    resultString = EntityUtils.toString(entity1, "UTF-8");
//                    if (null != entity1) {
                    logger.info("--------------------------------------");
                    logger.info("Response content: " + resultString);
                    logger.info("--------------------------------------");
//                    }
//                    if (noNeedResponse) {
//                        return null;
//                    }
                    //把json字符串转换成json对象
                    jsonResult = JSONObject.fromObject(resultString);
                } finally {
                    response.close();
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonResult;
    }

    public String getRedirectInfo(String url) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpPost = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int statusCode = response.getStatusLine().getStatusCode();
        //返回码
        System.out.println("statusCode==" + statusCode);
        String location = "";
        if ((statusCode == HttpStatus.SC_MOVED_TEMPORARILY) || (statusCode == HttpStatus.SC_MOVED_PERMANENTLY) ||
                (statusCode == HttpStatus.SC_SEE_OTHER) || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT) || statusCode == 200) {
            // 读取新的 URL 地址
            for (Header header : response.getAllHeaders()) {
                if ("Location".equals(header.getName())) {
                    location = header.getValue();
                    System.out.println(location);
                }
            }
            Header header = response.getFirstHeader("Location");
//            if (header!=null){
//                //重定向地址
//                location =header.getValue();
//                System.out.println(location);
//            }else {
//                System.out.println("Invalid redirect");
//            }
        }
        return location;
//        String realUrl="";
//        try {
//            URL Url = new URL(url);
//            HttpURLConnection conn=(HttpURLConnection)Url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setInstanceFollowRedirects(false);
//            conn.addRequestProperty("Accept-Charset", "UTF-8");
//            conn.connect();
//            realUrl = conn.getHeaderField("Location");
//            System.out.println(realUrl);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        try {
//            URL Url = new URL(url);
//            HttpURLConnection conn=(HttpURLConnection)Url.openConnection();
//            conn.getResponseCode();
//            realUrl=conn.getURL().toString();//跳转后所返回的链接
//            System.out.println(realUrl);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

//        return realUrl;
    }

    /**
     * @description: 传入获取的对象，解析得到长链接
     * @param: [TKGoods]
     * @return: java.lang.String
     * @author: fhn
     * @date: 2018/8/17 14:52
     */
    private static String getLongURL(JSONObject TKGoods) {
        JSONObject jsonResult = TKGoods.getJSONObject("result");
        JSONObject jsonData = jsonResult.getJSONObject("data");
        return jsonData.getString("coupon_click_url");
    }

    /**
     * @description: 将长链接转换为淘口令
     * @param: [longURL]
     * @return: java.lang.String
     * @author: fhn
     * @date: 2018/8/17 14:53
     */
    private String getTKL(TKGoodsInfo tkGoodsInfo) {
        Map<String, String> param = new HashMap<>(16);
        param.put("text", "感谢大家的支持！---fhn\n" + tkGoodsInfo.getGoodsTitle());
        param.put("url", tkGoodsInfo.getGoodsAndCouponUrl());
        param.put("logo", tkGoodsInfo.getPicUrl());
        JSONObject TKLObject = getJsonResult(taokeInfo.getTkl_request_url(), param);
        JSONObject TKLData = TKLObject.getJSONObject("data");
        return TKLData.getString("model");
    }

    /**
     * @description: 解析淘口令，获取淘宝商品id
     * @param: [TKL]
     * @return: java.lang.String
     * @author: fhn
     * @date: 2018/8/17 14:54
     */
    public JSONObject jxTKL(String TKL) throws Exception {
        Map<String, String> param = new HashMap<>(16);
        param.put("tkl", TKL);
        JSONObject TKLObject = getJsonResult(taokeInfo.getTkl_jx_url(), param);
        int errorCode = Integer.parseInt(TKLObject.getString("error_code"));
        if (JX_TKL_TRUE_CODE != errorCode) {
//            throw new GoodsException(ResultEnum.LOGGER_TKL_JX_ERROR);
            logger.info(ResultEnum.LOGGER_TKL_JX_ERROR.toString());
            return null ;
        }
        return TKLObject.getJSONObject("data");
    }

    /**
     * @description: 传入url，将其中参数拆分成hashmap
     * 注：舍弃url开头 ？之前的值，如（http://192.3.3.172:8085/lemis/filext-api?）
     * @param: [url]
     * @return: java.util.Map<java.lang.String,java.lang.String>
     * @author: fhn
     * @date: 2018/8/17 14:55
     */
    private static Map<String, String> urlToMap(String url) {
        Map<String, String> urlMap = new HashMap<String, String>(16);
        //舍弃url开头,"？"之前的值
        url = url.substring(url.indexOf("?") + 1);
        String[] params = url.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 1) {
                urlMap.put(p[0], "");
            }
            if (p.length == 2) {
                urlMap.put(p[0], p[1]);
            }
        }
        return urlMap;
    }

    /**
     * @description: 将map转换成url
     * @param: [map]
     * @return: java.lang.String
     * @author: fhn
     * @date: 2018/8/17 15:02
     */
    private static String getUrlByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String url = sb.toString();
        if (url.endsWith("&")) {
            url = org.apache.commons.lang.StringUtils.substringBeforeLast(url, "&");
        }
        return url;
    }

    /**
     * @description: 用httpget获取新的链接地址
     * @param: [url, referer, name]
     * @return: java.lang.String
     * @author: fhn
     * @date: 2018/8/17 15:06
     */
    public String httpGetHeader(String url, String referer, String name) {
        String value = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(url);
            HttpParams params = new BasicHttpParams();
            // 默认不让重定向
            params.setParameter("http.protocol.handle-redirects", false);
            // 这样就能拿到Location头了
            httpget.setParams(params);
            httpget.setHeader("Referer", referer);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            for (Header header : response.getAllHeaders()) {
                if (name.equals(header.getName())) {
                    value = header.getValue();
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * @description: URL地址被escape编码, 该方法为unescape解码函数
     * @param: [src]
     * @return: java.lang.String
     * @throws:
     * @author: fhn
     * @date: 2018/8/17 15:10
     */
    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * @description: 解析淘客链接为淘宝商品链接
     * @param: [url]
     * @return: java.lang.String
     * @author: fhn
     * @date: 2018/8/17 15:11
     */
    public String toTaoBaoUrl(String url) {
        if (hasId(url)) {
            return url;
        }
        String tu = httpGetHeader(url, "", "Location");
        if (hasId(tu)) {
            return tu;
        }
        String ref = tu.substring(tu.indexOf("tu=") + 3, tu.length());
        ref = unescape(ref);
        String taobaoUrl = httpGetHeader(ref, tu, "Location");
        return taobaoUrl;
    }

    /**
     * @description: 判断链接是否有id
     * @param: [content]
     * @return: boolean
     * @author: fhn
     * @date: 2018/8/17 15:12
     */
    public boolean hasId(String content) {
        boolean result = false;
        int new_index = content.indexOf("?");
        String new_str = content.substring(new_index + 1, content.length());
        String[] vars = new_str.split("&");
        for (int i = 0; i < vars.length; i++) {
            String[] pair = vars[i].split("=");
            if ("id".equals(pair[0])) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * @description: 判断链接是否有淘口令
     * @param: [content]
     * @return: boolean
     * @author: fhn
     * @date: 2018/8/17 15:13
     */
    public boolean hasTKL(String content) {
        boolean result = false;
        int start1 = content.indexOf(TKL_SIGN1);
        int start2 = content.indexOf(TKL_SIGN2);
        if (-1 != start1 && TKL_SIGN1 == content.charAt(start1 + 1 + TKL_LENGTH)) {
            result = true;
        } else if (-1 != start2 && TKL_SIGN2 == content.charAt(start2 + 1 + TKL_LENGTH)) {
            result = true;
        }
        return result;
    }

    private static boolean hasOneAttributes(JSONObject jsonObject, String attributes) {
        //然后用Iterator迭代器遍历取值，建议用反射机制解析到封装好的对象中
        boolean flag = false;
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (key.equals(attributes)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private static String getOneAttributes(JSONObject jsonObject, String attributes) {
        return jsonObject.getString(attributes);
    }

    public String testToTaoBaoUrl(String url) {
        String tu = httpGetHeader(url, "", "Location");
        String ref = tu.substring(tu.indexOf("tu=") + 3, tu.length());
        ref = unescape(ref);
        String taobaoUrl = httpGetHeader(ref, tu, "Location");
        return taobaoUrl;
    }

    public String getAlipayMoney(int i) {
        if (i == 1) {
            return taokeInfo.getAlipay_money1();
        } else {
            return taokeInfo.getAlipay_money2();
        }
    }

}