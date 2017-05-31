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
import com.woting.security.approve.persis.pojo.ApproveInfoPo;
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

    /**
     * 用户提交认证信息
     * @param userId 用户Id
     * @param iDCard 身份证号码
     * @param frontImg 身份证正面照片
     * @param reverseImg 身份证背面照片
     * @param mixImg 用户与身份证照片
     * @param anchorCardImg 专业主播证照片
     * @param applyDescn 认证说明
     * @param applyRoleId 认证的角色的Id
     * @param reallyName 用户真实姓名
     * @return 返回提交成功与否
     */
    public Map<String, Object> approveRole(String flag, String userId, String iDCard, String frontImg, String reverseImg, String mixImg, String anchorCardImg, String applyDescn, String applyRoleId, String reallyName) {
        Map<String, Object> map=new HashMap<String, Object>();
        if (StringUtils.isNullOrEmptyOrSpace(iDCard) || StringUtils.isNullOrEmptyOrSpace(frontImg)
                || StringUtils.isNullOrEmptyOrSpace(reverseImg) || StringUtils.isNullOrEmptyOrSpace(mixImg) 
                || StringUtils.isNullOrEmptyOrSpace(applyRoleId) || StringUtils.isNullOrEmptyOrSpace(reallyName) || StringUtils.isNullOrEmptyOrSpace(flag)) {
            map.put("ReturnType", "1005");
            map.put("Message", "认证信息提交失败");
            return map;
        }
        if (flag.equals("1")) {//提交认证申请
            Map<String, Object> param=new HashMap<String, Object>();
            param.put("userId", userId);
            param.put("iDCard", iDCard);
            try {
                int count=platUserExtDao.getCount("getApproveCount", param);
                if (count>0) {
                    map.put("ReturnType", "1006");
                    map.put("Message", "认证信息已经提交过了，请耐心等待审核");
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
            param.put("reallyName", reallyName);
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
                try{
                  //删除错误申请
                    platUserProgressDao.delete("deleteErrorApprove", _param);
                } catch (Exception e1){}
                map.put("ReturnType", "1005");
                map.put("Message", "认证信息提交失败");
                return map;
            }
        } else {//修改认证申请信息
            Map<String, Object> param=new HashMap<String, Object>();
            param.put("userId", userId);
            param.put("iDCard", iDCard);
            try {
                int count=platUserExtDao.getCount("getApproveCount", param);
                if (count<=0) {
                    map.put("ReturnType", "1005");
                    map.put("Message", "请先提交认证申请");
                    return map;
                }
            } catch (Exception e) {
                e.printStackTrace();
                map.put("ReturnType", "1005");
                map.put("Message", "认证信息修改失败");
                return map;
            }
            param.put("frontImg", frontImg);
            param.put("reverseImg", reverseImg);
            param.put("mixImg", mixImg);
            param.put("reallyName", reallyName);
            if (!StringUtils.isNullOrEmptyOrSpace(anchorCardImg)) {
                param.put("anchorCardImg", anchorCardImg);
            }
            Map<String, Object> _param=new HashMap<String, Object>();
            _param.put("userId", userId);
            _param.put("applyRoleId", applyRoleId);
            if (!StringUtils.isNullOrEmptyOrSpace(applyDescn)) {
                _param.put("applyDescn", applyDescn);
            }
            try {
                platUserExtDao.update("updateApproveInfo", param);
                platUserProgressDao.update("updateUserApproveRole", _param);
                map.put("ReturnType", "1001");
                map.put("Message", "认证信息修改成功");
                return map;
            } catch (Exception e) {
                e.printStackTrace();
                map.put("ReturnType", "1005");
                map.put("Message", "认证信息修改失败");
                return map;
            }
        }
    }

    /**
     * 获取用户认证的进度
     * @param userId 用户Id
     * @return 返回进度信息
     */
    public PlatUserProgressPo getUserApproveProgress(String userId) {
        if (StringUtils.isNullOrEmptyOrSpace(userId)) return null;
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("userId", userId);
        try {
            Map<String, Object> map=platUserProgressDao.queryForObjectAutoTranform("getUserApproveProgress", param);
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
     * 获取认证用户的信息
     * @param userId 认证用户Id
     * @return
     */
    public ApproveInfoPo getApproveInfo(String userId) {
        if (StringUtils.isNullOrEmptyOrSpace(userId)) return null;
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("userId", userId);
        try {
            Map<String, Object> map=platUserProgressDao.queryForObjectAutoTranform("getUserApproveProgress", param);
            Map<String, Object> _map=platUserExtDao.queryForObjectAutoTranform("getUserApproveInfo", param);
            if (map==null || map.size()<=0) return null;
            if (_map==null || _map.size()<=0) return null;
            ApproveInfoPo approveInfoPo=new ApproveInfoPo();
            if (_map.get("reallyName")!=null && !_map.get("reallyName").toString().equals("")) {
                approveInfoPo.setReallyName(_map.get("reallyName").toString());
            }
            if (_map.get("iDCard")!=null && !_map.get("iDCard").toString().equals("")) {
                approveInfoPo.setiDCard(_map.get("iDCard").toString());
            }
            if (_map.get("frontImg")!=null && !_map.get("frontImg").toString().equals("")) {
                approveInfoPo.setFrontImg(_map.get("frontImg").toString());
            }
            if (_map.get("reverseImg")!=null && !_map.get("reverseImg").toString().equals("")) {
                approveInfoPo.setReverseImg(_map.get("reverseImg").toString());
            }
            if (_map.get("mixImg")!=null && !_map.get("mixImg").toString().equals("")) {
                approveInfoPo.setMixImg(_map.get("mixImg").toString());
            }
            if (_map.get("anchorCardImg")!=null && !_map.get("anchorCardImg").toString().equals("")) {
                approveInfoPo.setAnchorCardImg(_map.get("anchorCardImg").toString());
            }
            if (map.get("userId")!=null && !map.get("userId").toString().equals("")) {
                approveInfoPo.setUserId(map.get("userId").toString());
            }
            if (map.get("checkerId")!=null && !map.get("checkerId").toString().equals("")) {
                approveInfoPo.setCheckerId(map.get("checkerId").toString());
            }
            if (map.get("applyRoleId")!=null && !map.get("applyRoleId").toString().equals("")) {
                approveInfoPo.setApplyRoleId(map.get("applyRoleId").toString());
            }
            if (map.get("reState")!=null && !map.get("reState").toString().equals("")) {
                approveInfoPo.setReStatu(Integer.valueOf(map.get("reState").toString()));
            }
            if (map.get("reDescn")!=null && !map.get("reDescn").toString().equals("")) {
                approveInfoPo.setReDescn(map.get("reDescn").toString());
            }
            if (map.get("modifyTime")!=null && !map.get("modifyTime").toString().equals("")) {
                approveInfoPo.setModifyTime(Timestamp.valueOf(map.get("modifyTime").toString()));
            }
            return approveInfoPo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取用户申请认证列表
     */
    public Map<String, Object> getApproves(int page, int pageSize, int flag) {
        List<String> userIdList;
        try {
            Map<String, Object> param=new HashMap<String, Object>();
            param.put("reState", 0);
            List<Map<String, Object>> list=platUserProgressDao.queryForListAutoTranform("getNotPassUser", param);
            if (list==null || list.size()<=0) return null;
            userIdList=new ArrayList<>();
            String userId;
            for (int i=0; i<list.size(); i++) {
                userId=(String) list.get(i).get("userId");
                if (userId!=null && !userId.equals("")) {
                    userIdList.add(userId);
                    userId=null;
                }
            }
            if (flag!=0) {
                List<Map<String, Object>> ret=platUserExtDao.queryForListAutoTranform("getApproveList", param);
                for (int i=0; i<ret.size(); i++) {
                    Map<String, Object> one=ret.get(i);
                    String anchorCardImg="";
                    if (one.get("anchorCardImg")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("anchorCardImg")+"")) {
                        anchorCardImg=one.get("anchorCardImg").toString();
                    }
                    String id;
                    if (flag==1) {//实名认证列表
                        if (anchorCardImg!=null && !anchorCardImg.equals("") && !anchorCardImg.toLowerCase().equals("null")) {
                            id=one.get("userId").toString();
                            if (id!=null && !id.equals("")) {
                                if (userIdList.contains(id)) {
                                    userIdList.remove(id);
                                    id=null;
                                }
                            }
                        }
                    } else if (flag==2) {//资格认证列表
                        if (anchorCardImg==null || anchorCardImg.equals("") || anchorCardImg.toLowerCase().equals("null")) {
                            id=one.get("userId").toString();
                            if (id!=null && !id.equals("")) {
                                if (userIdList.contains(id)) {
                                    userIdList.remove(id);
                                    id=null;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        if (userIdList==null || userIdList.size()<=0) return null;
        Map<String, Object> param=new HashMap<String, Object>();
        param.put("userIdList", userIdList);
        List<Map<String, Object>> _ret=null;
        int count=0;
        if (page==0) {// 获取全部
            _ret=platUserExtDao.queryForListAutoTranform("getApproveList", param);
            if (_ret!=null && _ret.size()>0) count=_ret.size();
        } else {// 分页获取
            Page<Map<String, Object>> mapPage=platUserExtDao.pageQueryAutoTranform(null, "getApproveList", param, page, pageSize);
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
            String anchorCardImg;
            if (one.get("anchorCardImg")!=null&&!StringUtils.isNullOrEmptyOrSpace(one.get("anchorCardImg")+"")) {
                anchorCardImg=one.get("anchorCardImg").toString();
                if (anchorCardImg!=null&&!StringUtils.isNullOrEmptyOrSpace(anchorCardImg)) _one.put("AnchorCardImg", anchorCardImg);
            } else {
                anchorCardImg="";
            }
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
    public boolean updateApproveStatus(List<String> userIdList, int reState, String reDescn) {
        if (userIdList==null || userIdList.size()<=0) return false;

        Map<String, Object> param=new HashMap<String, Object>();
        param.put("userIdList", userIdList);
        param.put("reState", reState);
        param.put("reDescn", reDescn);
        try {
            platUserProgressDao.update("updateUserApproveState", param);
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
