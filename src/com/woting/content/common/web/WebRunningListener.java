package com.woting.content.common.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.woting.WtContentMngConstants;
import com.woting.dataanal.gather.API.ApiGatherListener;
import com.woting.push.socketclient.SocketClientConfig;
import com.woting.push.socketclient.oio.SocketClient;

public class WebRunningListener implements ServletContextListener {
    private Logger logger=LoggerFactory.getLogger(this.getClass());

    @Override
    //初始化
    public void contextInitialized(ServletContextEvent arg0) {
        try {
        	//0-启动Socket
            SocketClient sc=new SocketClient(SocketClientConfig.loadConfig());
            sc.workStart();
            SystemCache.setCache(new CacheEle<SocketClient>(WtContentMngConstants.SOCKET_OBJ, "模块", sc));//注册到内存
            //1-启动Api访问监听服务
            ApiGatherListener.begin();
        } catch(Exception e) {
            logger.error("Web运行时监听启动异常：",e);
        }
    }

    @Override
    //销毁
    public void contextDestroyed(ServletContextEvent arg0) {
    }
}