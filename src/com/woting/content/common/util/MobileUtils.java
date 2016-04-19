package com.woting.content.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

import com.spiritdata.framework.util.JsonUtils;

public class MobileUtils {

	public static Map<String, Object> getDataFromRequest(ServletRequest req) {
        InputStreamReader isr=null;
        BufferedReader br=null;
        try {
            isr=new InputStreamReader((ServletInputStream)req.getInputStream(), "UTF-8");
            br=new BufferedReader(isr);
            String line=null;
            StringBuilder sb=new StringBuilder();
            while ((line=br.readLine())!=null) sb.append(line);
            line=sb.toString();
            if (line!=null&&line.length()>0) {
                return (Map<String, Object>)JsonUtils.jsonToObj(sb.toString(), Map.class);
            } else {
                return RequestUtils.getDataFromRequestParam(req);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (isr!=null) try {isr.close();} catch(Exception e) {}
            if (br!=null) try {br.close();} catch(Exception e) {}
        }
        return null;
    }
}
