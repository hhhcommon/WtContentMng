package com.woting.version.manage.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.spiritdata.framework.core.web.AbstractFileUploadController;
import com.spiritdata.framework.util.StringUtils;
import com.woting.version.core.model.Version;
import com.woting.version.core.model.VersionConfig;
import com.woting.version.core.service.VersionService;

@Lazy(true)
@Controller
public class V_FileUploadController extends AbstractFileUploadController {
    private VersionConfig verCfg=null;
    @Resource
    private VersionService verService;

    @Override
    public void afterUploadAllFiles(Map<String, Object> retMap, Map<String, Object> a, Map<String, Object> p) {
        try {
            //1-获得参数
            //版本
            String version=p.get("Version")==null?null:p.get("Version")+"";
            String appName=p.get("AppName")==null?null:p.get("AppName")+"";
            //版本状态
            int pubFlag=0;
            if (p.get("PubFlag")!=null) {
                try {pubFlag=Integer.parseInt(p.get("PubFlag")+"");} catch(Exception e) {};
            }
            //是否强制0不强制；1强制；
            int force=0;
            if (p.get("Force")!=null) {
                try {force=Integer.parseInt(p.get("Force")+"");} catch(Exception e) {};
            }
            //说明
            String descn=p.get("Descn")==null?null:p.get("Descn")+"";
            //bug记录
            String bug=p.get("BugPatch")==null?null:p.get("BugPatch")+"";

            //2-拷贝
            if (pubFlag==1) {//发布
                
            }

            //3-插入方法
            Version v=new Version();
            v.setAppName(appName);
            v.setVersion(version);
            v.setVerMemo(descn);
            v.setBugMemo(bug);
            v.setPubFlag(pubFlag);
            v.setApkFile("文件名，不包括路径");//待改
            v.setApkSize(234);//待改
            v.setIsCurVer(1);

            verService.insert(v, force);

            retMap.put("ReturnType", "1001");
            retMap.put("Message", "新增成功");
        } catch(Exception e) {
            e.printStackTrace();
            retMap.put("ReturnType", "T");
            retMap.put("TClass", e.getClass().getName());
            retMap.put("Message", e.getMessage());
        }
    }

    @Override
    public Map<String, Object> beforeUploadFile(Map<String, Object> a, Map<String, Object> p) {
        Map<String,Object> map=new HashMap<String, Object>();
        if (this.verCfg==null) verCfg=verService.getVerConfig();
        try {
            //0-判断权限：目前没有这个功能
            //1-获得参数
            //版本
            String version=p.get("Version")==null?null:p.get("Version")+"";
            String appName=p.get("AppName")==null?null:p.get("AppName")+"";
            //是否强制
            int force=0;
            if (p.get("Force")!=null) {
                try {force=Integer.parseInt(p.get("Force")+"");} catch(Exception e) {};
            }
            //说明
            String descn=p.get("Descn")==null?null:p.get("Descn")+"";

            //2-参数是否符合规则
            if (StringUtils.isNullOrEmptyOrSpace(descn) //参数是否都获得齐全
              ||StringUtils.isNullOrEmptyOrSpace(version)
              ||StringUtils.isNullOrEmptyOrSpace(appName)) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获得有用参数");
                return map;
            }
            int validateVer=verService.validateVer(version, -1); //版本号是否符合规则
            if (validateVer!=1) {
                if (validateVer==2) {
                    map.put("ReturnType", "2001");
                    map.put("Message", "版本号不合法，版本域不符合规定");
                } else if (validateVer==3) {
                    map.put("ReturnType", "2002");
                    map.put("Message", "版本号不合法，版本号小于之前的版本");
                } else if (validateVer==4) {
                    map.put("ReturnType", "2003");
                    map.put("Message", "版本号不合法，编译号小于之前的版本");
                }
                return map;
            }
            if (force==0&&verService.judgeInsert()==0) {//判断是否可以加入
                map.put("ReturnType", "1002");
                map.put("Message", "还有未发布的版本，不能新增版本");
                return map;
            }
            return null;
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", e.getMessage());
            return map;
        }
    }

    @Override
    public void setMySavePath(Map<String, Object> a, Map<String, Object> p) {
        String version=p.get("Version")==null?null:p.get("Version")+"";
        if (version!=null) this.setSavePath("versionFile/"+version);
    }
}