package com.woting.security.approve.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.security.approve.persis.pojo.PlatUserExtPo;
import com.woting.security.approve.persis.pojo.PlatUserProgressPo;

public class ApproveRoleService {
    @Resource(name="defaultDAO")
    private MybatisDAO<PlatUserExtPo> platUserExtDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<PlatUserProgressPo> platUserProgressDao;

    @PostConstruct
    public void initParam() {
        platUserExtDao.setNamespace("PLAT_APPROVE");
        platUserProgressDao.setNamespace("PLAT_APPROVE");
    }

    public Map<String, Object> approveRole(String userId, String iDCard, String frontImg, String reverseImg, String mixImg, String anchorCardImg, String applyDescn, String applyRoleId) {
        Map<String, Object> map=new HashMap<String, Object>();
        if (StringUtils.isNullOrEmptyOrSpace(iDCard) || StringUtils.isNullOrEmptyOrSpace(frontImg)
                || StringUtils.isNullOrEmptyOrSpace(reverseImg) || StringUtils.isNullOrEmptyOrSpace(mixImg) || StringUtils.isNullOrEmptyOrSpace(applyRoleId)) {
            map.put("ReturnType", "1005");
            map.put("Message", "认证信息提交失败");
            return map;
        }
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("userId", userId);
        param.put("iDCard", iDCard);
        try {
            int count=platUserExtDao.getCount("getApproveCount", param);
            if (count>0) {
                map.put("ReturnType", "1006");
                map.put("Message", "认证信息已经提交，请耐心等待审核");
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "1005");
            map.put("Message", "认证信息提交失败");
            return map;
        }
        param.put("frontImg", frontImg);
        param.put("reverseImg", reverseImg);
        param.put("mixImg", mixImg);
        if (!StringUtils.isNullOrEmptyOrSpace(anchorCardImg)) {
            param.put("anchorCardImg", anchorCardImg);
        }
        Map<String, Object> _param=new HashMap<String, Object>();
        _param.put("id", SequenceUUID.getPureUUID());
        _param.put("userId", userId);
        _param.put("checkerId", "0");
        _param.put("applyRoleId", applyRoleId);
        _param.put("reStatus", 0);
        _param.put("modifyTime", new Timestamp(System.currentTimeMillis()));
        if (!StringUtils.isNullOrEmptyOrSpace(applyDescn)) {
            _param.put("applyDescn", applyDescn);
        }
        try {
            platUserProgressDao.insert("insertUserProgress", _param);
            platUserExtDao.insert("insertUserExt", param);
            map.put("ReturnType", "1001");
            map.put("Message", "认证信息提交成功");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "1005");
            map.put("Message", "认证信息提交失败");
            return map;
        }
    }

    public PlatUserProgressPo getUserApproveProgress(String userId) {
        if (StringUtils.isNullOrEmptyOrSpace(userId)) return null;
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("userId", userId);
        try {
            List<Map<String, Object>> list=platUserProgressDao.queryForListAutoTranform("getUserApproveProgress", param);
            if (list==null || list.size()<=0) return null;
            Map<String, Object> map=list.get(0);
            if (map==null || map.size()<=0) return null;
            PlatUserProgressPo platUserProgressPo=new PlatUserProgressPo();
            if (map.get("userId")!=null && !map.get("userId").toString().equals("")) {
                platUserProgressPo.setUserId(map.get("userId").toString());
            }
            if (map.get("checkerId")!=null && !map.get("checkerId").toString().equals("")) {
                platUserProgressPo.setCheckerId(map.get("checkerId").toString());
            }
            if (map.get("applyRoleId")!=null && !map.get("applyRoleId").toString().equals("")) {
                platUserProgressPo.setApplyRoleId(map.get("applyRoleId").toString());
            }
            if (map.get("reState")!=null && !map.get("reState").toString().equals("")) {
                platUserProgressPo.setReStatu(Integer.valueOf(map.get("reState").toString()));
            }
            if (map.get("applyDescn")!=null && !map.get("applyDescn").toString().equals("")) {
                platUserProgressPo.setApplyDescn(map.get("applyDescn").toString());
            }
            if (map.get("reDescn")!=null && !map.get("reDescn").toString().equals("")) {
                platUserProgressPo.setReDescn(map.get("reDescn").toString());
            }
            if (map.get("modifyTime")!=null && !map.get("modifyTime").toString().equals("")) {
                platUserProgressPo.setModifyTime(Timestamp.valueOf(map.get("modifyTime").toString()));
            }
            return platUserProgressPo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
