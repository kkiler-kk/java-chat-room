package net.kk.chat.utils;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Set;

public class MessageUtil {
    public static String getMessNames(Set<String> names) {
        StringBuilder builder = new StringBuilder("{\"userCount\":[");
        for (String name : names) {
            builder.append("{\"name\":\"").append(name + "\",").append("\"url\":\"").append("/upload/" + name + ".jpg\"},");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("],\"type\": \"userCount\"}");
        return builder.toString();
    }
    /**
     * 向目的URL发送get请求
     * @param url       目的url
     * @return  String
     */
    public static String sendGetRequest(String url){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));

        MultiValueMap<String,String> param = new LinkedMultiValueMap<String, String>();
        //将参数和header组成一个请求
        HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<MultiValueMap<String,String>>(param,httpHeaders);

        try {
            ResponseEntity<String> responseEntity =
                    restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            return responseEntity.getBody();
        }catch (Exception ex){
            //进行错误处理
        }
        return "";
    }
}
