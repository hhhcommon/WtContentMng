package com.woting.security.approve.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.core.model.Page;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.security.approve.persis.pojo.PlatUserExtPo;
import com.woting.security.approve.persis.pojo.PlatUserProgressPo;
import com.woting.security.role.service.SecurityRoleService;

public class ApproveRoleService {
    @Resource(name="defaultDAO")
    private MybatisDAO<PlatUserExtPo> platUserExtDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<PlatUserProgressPo> platUserProgressDao;
    @Resource
    private SecurityRoleService roleService;

    @PostConstruct
    public void initParam() {
        platUserExtDao.setNamespace("PLAT_APPROVE");
        platUserProgressDao.setNamespace("PLAT_APPROVE");
    }

    public Map<String, Object> approveRole(String userId, String iDCard, String frontImg, String reverseImg, String mixImg, String anchorCardImg, String applyDescn, String applyRoleId, String reallyName) {
        Map<String, Object> map=new HashMap<String, Object>();
        if (StringUtils.isNullOrEmptyOrSpace(iDCard) || StringUtils.isNullOrEmptyOrSpace(frontImg)
                || StringUtils.isNullOrEmptyOrSpace(reverseImg) || StringUtils.isNullOrEmptyOrSpace(mixImg) 
                || StringUtils.isNullOrEmptyOrSpace(applyRoleId) || StringUtils.isNullOrEmptyOrSpace(reallyName)) {
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
        _param.put("reallyName", reallyName);
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
            return null;
        }
    }

    /**
     * 获取用户申请认证列表
     */
    public Map<String, Object> getApproves(int page, int pageSize, int flag) {
        List<Map<String, Object>> _ret=null;
        int count=0;
        if (page==0) {// 获取全部
            _ret=platUserExtDao.queryForListAutoTranform("getApproveList", null);
            if (_ret!=null && _ret.size()>0) count=_ret.size();
        } else {// 分页获取
            Page<Map<String, Object>> mapPage=platUserExtDao.pageQueryAutoTranform(null, "getApproveList", null, page, pageSize);
            if (mapPage!=null&&mapPage.getDataCount()>0) {
                _ret=new ArrayList<Map<String, Object>>();
                _ret.addAll(mapPage.getResult());
                count=mapPage.getDataCount();
            }
        }
        if (_ret==null||_ret.isEmpty()) return null;
        List<Map<String, Object>> ret=new ArrayList<Map<String, Object>>(_ret.size());
        for (int i=0; i<_ret.size(); i++) {
            Map<String, Object> one=_ret.get(i);
            Map<String, Object> _one=new HashMap<String, Object>();
            if (one.get("userId")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("userId")+"")) _one.put("UserId", one.get("userId"));
            if (one.get("reallyName")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("reallyName")+"")) _one.put("reallyName", one.get("reallyName"));
            if (one.get("iDCard")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("iDCard")+"")) _one.put("IDCard", one.get("iDCard"));
            if (one.get("frontImg")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("frontImg")+"")) _one.put("FrontImg", one.get("frontImg"));
            if (one.get("reverseImg")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("reverseImg")+"")) _one.put("ReverseImg", one.get("reverseImg"));
            if (one.get("mixImg")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("mixImg")+"")) _one.put("MixImg", one.get("mixImg"));
            String anchorCardImg=one.get("anchorCardImg").toString();
            if (anchorCardImg!=null&&!StringUtils.isNullOrEmptyOrSpace(anchorCardImg)) _one.put("AnchorCardImg", anchorCardImg);
            if (one.get("imgUrl")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("imgUrl")+"")) _one.put("ContentLoopImg", one.get("imgUrl")+"");
            if (flag==1) {//实名认证列表
                if (anchorCardImg==null || anchorCardImg.equals("") || anchorCardImg.toLowerCase().equals("null")) {
                    ret.add(_one);
                }
            } else if (flag==2) {//资格认证列表
                if (anchorCardImg!=null && !anchorCardImg.equals("") && !anchorCardImg.toLowerCase().equals("null")) {
                    ret.add(_one);
                }
            } else {//获取全部认证列表
                ret.add(_one);
            }
        }
        Map<String, Object> retM=new HashMap<String, Object>();
        retM.put("ResultList", ret);
        retM.put("AllCount", count);
        return retM;
    }

    /**
     * 审核
     * @param userIdList 需要审核的用户Id
     * @param reState 认证状态
     * @param applyDescn 审核意见
     * @return boolean
     */
    public boolean updateApproveStatus(List<String> userIdList, int reState, String applyDescn) {
        if (userIdList==null || userIdList.size()<=0) return false;

        Map<String, Object> param=new HashMap<String, Object>();
        param.put("userIdList", userIdList);
        param.put("reState", reState);
        param.put("applyDescn", applyDescn);
        try {
            platUserExtDao.update("updateUserApproveState", param);
            for (String userId : userIdList) {
                PlatUserProgressPo userProgress=getUserApproveProgress(userId);
                if (userProgress!=null) {
                    String roleId=userProgress.getApplyRoleId();
                    roleService.setUserRole(userId, roleId);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
