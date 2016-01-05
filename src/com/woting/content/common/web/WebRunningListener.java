package com.woting.content.common.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;

public class WebRunningListener implements ServletContextListener {
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    //初始化
    public void contextInitialized(ServletContextEvent arg0) {
        try {
        } catch(Exception e) {
            logger.error("Web运行时监听启动异常：",e);
        }
    }

    @Override
    //销毁
    public void contextDestroyed(ServletContextEvent arg0) {
    }
}