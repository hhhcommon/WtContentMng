package com.woting.content.common.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.jsonconf.JsonConfig;
import com.spiritdata.framework.util.StringUtils;
import com.woting.WtContentMngConstants;
import com.woting.dataanal.gather.API.ApiGatherListener;
import com.woting.passport.UGA.PasswordConfig;
import com.woting.passport.UGA.service.UserService;
import com.woting.push.socketclient.SocketClientConfig;
import com.woting.push.socketclient.oio.SocketClient;

public class WebRunningListener implements ServletContextListener {
    private Logger logger=LoggerFactory.getLogger(this.getClass());

    @Override
    //初始化
    public void contextInitialized(ServletContextEvent arg0) {
        try {
            //加载配置文件
            loadConfig();
        	//0-启动Socket
            SocketClient sc=new SocketClient((SocketClientConfig)SystemCache.getCache(WtContentMngConstants.SOCKET_CFG).getContent());
            sc.workStart();
            SystemCache.setCache(new CacheEle<SocketClient>(WtContentMngConstants.SOCKET_OBJ, "模块", sc));//注册到内存
            //1-启动Api访问监听服务
            ApiGatherListener.begin();
            //2-看是否需要重新加密
            PasswordConfig pc=(PasswordConfig)SystemCache.getCache(WtContentMngConstants.PASSWORD_CFG).getContent();
            if (pc!=null&&pc.isReEncryption()&&pc.isUseEncryption()) {
                ServletContext _sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
                if (WebApplicationContextUtils.getWebApplicationContext(_sc)!=null) {
                    UserService us=(UserService)WebApplicationContextUtils.getWebApplicationContext(_sc).getBean("userService");
                    us.reEncryptionPwd();
                }
            }
        } catch(Exception e) {
            logger.error("Web运行时监听启动异常：",e);
        }
    }

    @Override
    //销毁
    public void contextDestroyed(ServletContextEvent arg0) {
    }
    /*
     * 加载服务配置
     * @param configFileName 配置文件
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void loadConfig() throws IOException {
        JsonConfig jc=null;
        try {
            String configFileName=(SystemCache.getCache(FConstants.APPOSPATH)==null?"":((CacheEle<String>)(SystemCache.getCache(FConstants.APPOSPATH))).getContent());
            configFileName+="WEB-INF"+File.separator+"app.jconf";
            jc=new JsonConfig(configFileName);
            logger.info("配置文件信息={}", jc.getAllConfInfo());
        } catch(Exception e) {
            logger.info(StringUtils.getAllMessage(e));
            jc=null;
            e.printStackTrace();
        }
        if (jc!=null) {
            SocketClientConfig scc=ConfigLoadUtils.getSocketClientConfig(jc);
            SystemCache.setCache(new CacheEle<SocketClientConfig>(WtContentMngConstants.SOCKET_CFG, "Socket处理配置", scc));

            PasswordConfig pc=ConfigLoadUtils.getPasswordConfig(jc);
            SystemCache.setCache(new CacheEle<PasswordConfig>(WtContentMngConstants.PASSWORD_CFG, "密码配置信息", pc));
        }
    }
}