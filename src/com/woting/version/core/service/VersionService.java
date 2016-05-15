package com.woting.version.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.StringUtils;
import com.woting.WtContentMngConstants;
import com.woting.version.core.model.Version;
import com.woting.version.core.model.VersionConfig;

@Lazy(true)
@Service
public class VersionService {
    @Resource(name="defaultDAO")
    private MybatisDAO<Version> verDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<VersionConfig> verCfgDao;

    @PostConstruct
    public void initParam() {
        verDao.setNamespace("P_VERSION");
        verCfgDao.setNamespace("P_VERSION");
    }

    //版本配置======================================================================================================
    /**
     * 得到版本配置信息
     * @return
     */
    public VersionConfig getVerConfig() {
        return verCfgDao.getInfoObject("getCfgList", null);
    }

    /**
     * 根据所给版本号version，获得该版本号详细信息
     * @param version 所给版本号
     * @return 版本信息，若所给版本号不存在，返回null
     */
    public Version getVersion(String version) {
        if (StringUtils.isNullOrEmptyOrSpace(version)) return null;
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("version", version);
        try {
            return verDao.getInfoObject(param);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断该版本是否是最新的版本
     * @param version
     * @return
     */
    public Map<String, Object> judgeVersion(String version) {
        Map<String, Object> retm=new HashMap<String, Object>();
        try {
            Map<String, Object> param=new HashMap<String, Object>();
            param.put("version", version);
            Version appVer=verDao.getInfoObject(param);
            Version curVer=verDao.getInfoObject("getCurrentVersion", null); //获取当前版本
            if (curVer==null) return null;

            boolean mastUpdate=false;
            List<String> noVersionList=new ArrayList<String>();

            int beginVerNum=(appVer==null?1:appVer.getVerNum());
            int endVerNum=curVer.getVerNum();
            param.clear();
            param.put("appVerNum", beginVerNum);
            param.put("curVerNum", endVerNum);
            List<Version> l=verDao.queryForList("getNoVersionList", param);
            if (l!=null&&!l.isEmpty()) {
                for (Version _v: l) {
                    noVersionList.add(_v.getVersion());
                    if (mastUpdate==false) mastUpdate=(_v.getVersion().indexOf(".X.")!=-1);
                }
            }

            retm.put("MastUpdate", mastUpdate?"1":"0");
            retm.put("NoVersionList", noVersionList);
            VersionConfig vc=(VersionConfig)SystemCache.getCache(WtContentMngConstants.APP_VERSIONCONFIG).getContent();
            retm.put("DownLoadUrl", vc.getPubUrl());
            retm.put("CurVersion", curVer.toViewMap4App());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return retm;
    }
}