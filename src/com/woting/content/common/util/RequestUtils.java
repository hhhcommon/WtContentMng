package com.woting.content.common.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

public abstract class RequestUtils {
    /**
     * 从Request获得所有参数
     * @param req 请求内容
     * @return 返回参数
     */
    public static Map<String, Object> getDataFromRequestParam(ServletRequest req) {
        Map<String, Object> retM = new HashMap<String, Object>();
        Enumeration<String> enu=req.getParameterNames();
        while(enu.hasMoreElements()) {
            String name=(String)enu.nextElement();
            retM.put(name, req.getParameter(name));
        }
        return retM;
    }
}