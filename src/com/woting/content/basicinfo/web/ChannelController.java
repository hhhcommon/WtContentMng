package com.woting.content.basicinfo.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 栏目信息前台控制
 * @author wanghui
 */
@Controller
@RequestMapping(value="/baseinfo/")
public class ChannelController {

    /**
     * 添加一个栏目
     */
    @RequestMapping(value="addChannel.do")
    @ResponseBody
    public Map<String,Object> addChannel(HttpServletRequest request) {
        return null;
    }

    /**
     * 修改一个栏目
     */
    @RequestMapping(value="updateChannel.do")
    @ResponseBody
    public Map<String,Object> updateChannel(HttpServletRequest request) {
        return null;
    }

    /**
     * 删除一个栏目
     */
    @RequestMapping(value="delChannel.do")
    @ResponseBody
    public Map<String,Object> delChannel(HttpServletRequest request) {
        return null;
    }
}