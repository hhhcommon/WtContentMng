package com.woting.content.manage.keyword.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.RequestUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.content.manage.keyword.service.KeyWordProService;
import com.woting.dataanal.gather.API.ApiGatherUtils;
import com.woting.dataanal.gather.API.mem.ApiGatherMemory;
import com.woting.dataanal.gather.API.persis.pojo.ApiLogPo;
import com.woting.passport.mobile.MobileParam;
import com.woting.passport.mobile.MobileUDKey;
import com.woting.passport.session.DeviceType;
import com.woting.passport.session.SessionService;

@Controller
public class KeyWordController {
	@Resource
	private KeyWordProService keyWordProService;
	@Resource(name="redisSessionService")
    private SessionService sessionService;
	
	/**
	 * 删除单体节目信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/content/getTags.do")
	@ResponseBody
	public Map<String, Object> getTags(HttpServletRequest request){
		//数据收集处理==1
        ApiLogPo alPo=ApiGatherUtils.buildApiLogDataFromRequest(request);
        alPo.setApiName("1.1.1-common/entryApp");
        alPo.setObjType("000");//一般信息
        alPo.setDealFlag(1);//处理成功

        Map<String,Object> map=new HashMap<String, Object>();
        try {
            //0-获取参数
            MobileUDKey mUdk=null;
            Map<String, Object> m=RequestUtils.getDataFromRequest(request);
            alPo.setReqParam(JsonUtils.objToJson(m));
            if (m==null||m.size()==0) {
                map.put("ReturnType", "0000");
                map.put("Message", "无法获取需要的参数");
            } else {
                MobileParam mp=MobileParam.build(m);
                if (StringUtils.isNullOrEmptyOrSpace(mp.getImei())&&DeviceType.buildDtByPCDType(StringUtils.isNullOrEmptyOrSpace(mp.getPCDType())?-1:Integer.parseInt(mp.getPCDType()))==DeviceType.PC) { //是PC端来的请求
                    mp.setImei(request.getSession().getId());
                }
                mUdk=mp.getUserDeviceKey();
                if (mUdk!=null) {
                    Map<String, Object> retM=sessionService.dealUDkeyEntry(mUdk, "common/entryApp");
                    map.putAll(retM);
                }
                map.put("ServerStatus", "1"); //服务器状态
            }
            //数据收集处理==2
            alPo.setOwnerType(201);
            if (map.get("UserId")!=null&&!StringUtils.isNullOrEmptyOrSpace(map.get("UserId")+"")) {
                alPo.setOwnerId(map.get("UserId")+"");
            } else {
                //过客
                if (mUdk!=null) alPo.setOwnerId(mUdk.getDeviceId());
                else alPo.setOwnerId("0");
            }
            if (mUdk!=null) {
                alPo.setDeviceType(mUdk.getPCDType());
                alPo.setDeviceId(mUdk.getDeviceId());
            }
            if (m!=null) {
                if (mUdk!=null&&DeviceType.buildDtByPCDType(mUdk.getPCDType())==DeviceType.PC) {
                    if (m.get("MobileClass")!=null&&!StringUtils.isNullOrEmptyOrSpace(m.get("MobileClass")+"")) {
                        alPo.setExploreVer(m.get("MobileClass")+"");
                    }
                    if (m.get("exploreName")!=null&&!StringUtils.isNullOrEmptyOrSpace(m.get("exploreName")+"")) {
                        alPo.setExploreName(m.get("exploreName")+"");
                    }
                } else {
                    if (m.get("MobileClass")!=null&&!StringUtils.isNullOrEmptyOrSpace(m.get("MobileClass")+"")) {
                        alPo.setDeviceClass(m.get("MobileClass")+"");
                    }
                }
            }
            if (map.get("ReturnType")!=null) return map;
            String userid = m.get("UserId")+"";
    		if(StringUtils.isNullOrEmptyOrSpace(userid)||userid.toLowerCase().equals("null")){
    			map.put("ReturnType", "1011");
    			map.put("Message", "无用户信息");
    			return map;
    		}
    		String mediatype = m.get("MediaType")+"";
    		if(StringUtils.isNullOrEmptyOrSpace(mediatype)||mediatype.toLowerCase().equals("null")){
    			map.put("ReturnType", "1012");
    			map.put("Message", "无内容类型信息");
    			return map;
    		}
    		String tagType = m.get("TagType")+"";
    		if(StringUtils.isNullOrEmptyOrSpace(tagType)||tagType.toLowerCase().equals("null")){
    			tagType = "1";
    		}
    		String tagsize = m.get("TagSize")+"";
    		if(StringUtils.isNullOrEmptyOrSpace(tagsize)||tagsize.toLowerCase().equals("null")){
    			tagsize = "10";
    		}
    		List<Map<String, Object>> ls = new ArrayList<>();
    		if (mediatype.equals("1")) {
    			String channelId = m.get("ChannelIds")+"";
    			if(StringUtils.isNullOrEmptyOrSpace(channelId)||channelId.toLowerCase().equals("null")){
    				map.put("ReturnType", "1014");
    				map.put("Message", "无栏目Id");
    				return map;
    			}
    			ls = keyWordProService.getKeyWordList(tagType, userid, channelId, tagsize);
    		}
    		if (mediatype.equals("2")) {
    			String seqMediaId = m.get("SeqMediaId")+"";
    		    if(StringUtils.isNullOrEmptyOrSpace(seqMediaId)||seqMediaId.toLowerCase().equals("null")){
    			    map.put("ReturnType", "1015");
    			    map.put("Message", "无专辑Id");
    			    return map;
    		    }
    		    ls = keyWordProService.getKeyWordListBySeqMedia(seqMediaId, tagType, userid, tagsize);
    		}
    		if (ls!=null && ls.size()>0) {
    			map.put("ReturnType", "1001");
    			map.put("ResultList", ls);
    			map.put("AllCount", ls.size());
    			return map;
    		} else {
    			map.put("ReturnType", "1016");
    			map.put("Message", "无内容");
    			return map;
    		}
        } catch(Exception e) {
            e.printStackTrace();
            map.put("ReturnType", "T");
            map.put("TClass", e.getClass().getName());
            map.put("Message", StringUtils.getAllMessage(e));
            alPo.setDealFlag(2);
            return map;
        } finally {
            //数据收集处理=3
            alPo.setEndTime(new Timestamp(System.currentTimeMillis()));
            alPo.setReturnData(JsonUtils.objToJson(map));
            try {
                ApiGatherMemory.getInstance().put2Queue(alPo);
            } catch (InterruptedException e) {}
        }
	}
	
}
