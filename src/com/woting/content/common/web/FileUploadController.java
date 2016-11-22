package com.woting.content.common.web;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import com.spiritdata.framework.core.web.AbstractFileUploadController;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.FileUtils;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.dataanal.gather.API.persis.pojo.ApiLogPo;
import com.woting.passport.session.DeviceType;

import net.coobird.thumbnailator.Thumbnails;

@Controller
public class FileUploadController extends AbstractFileUploadController{
	private ApiLogPo alPo=new ApiLogPo();
	
	@Override
	public Map<String, Object> afterUploadOneFileOnSuccess(Map<String, Object> m, Map<String, Object> rqtAttrs, Map<String, Object> rqtParams, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		String srcType = rqtParams.get("SrcType") + "";
//		if (StringUtils.isNullOrEmptyOrSpace(srcType) || srcType.toLowerCase().equals("null")) {
//			map.put("ReturnType", "1011");
//			map.put("Message", "无文件类型参数");
//			return map;
//		}
		String purpose = rqtParams.get("Purpose") + "";
//		if (StringUtils.isNullOrEmptyOrSpace(purpose) || purpose.toLowerCase().equals("null")) {
//			map.put("ReturnType", "1011");
//			map.put("Message", "无文件用途参数");
//			return map;
//		}
		if (srcType.equals("1")) { //图片处理
			String filepath = m.get("storeFilename")+""; //原始文件路径
		    String path = FileNameUtils.getFilePath(filepath);
//            String filename = FileNameUtils.getFileName(filepath);
            String newname = SequenceUUID.getPureUUID();
            String newfilepath = path + "/" + newname + ".png";
            FileUtils.copyFile(filepath, newfilepath); //复制原始文件
			if (purpose.equals("1")) { //用户头像处理
				try {
                    String img150path = path + "/" + newname + ".150_150.png";
                    String img300path = path + "/" + newname + ".300_300.png";
                    String img450path = path + "/" + newname + ".450_450.png";
                    Thumbnails.of(new File(filepath)).size(150, 150).toFile(img150path);
                    Thumbnails.of(new File(filepath)).size(300, 300).toFile(img300path);
                    Thumbnails.of(new File(filepath)).size(450, 450).toFile(img450path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (purpose.equals("2")) { //内容图片处理
					try {
						String img180path = path + "/" + newname + ".180_180.png";
		                String img300path = path + "/" + newname + ".300_300.png";
		                Thumbnails.of(new File(filepath)).size(180, 180).toFile(img180path);
		                Thumbnails.of(new File(filepath)).size(300, 300).toFile(img300path);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if (purpose.equals("3")) { //轮播图处理
						try {
							String img1080_450path = path + "/" + newname + ".1080_450.png";
			                Thumbnails.of(new File(filepath)).size(1080, 450).toFile(img1080_450path);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			FileUtils.deleteFile(new File(filepath));
			map.put("FilePath", newfilepath.replace("/opt/tomcat8_CM/webapps/", "http://www.wotingfm.com:908/"));
		} else {
			if(srcType.equals("2")) {
				String filepath = m.get("storeFilename")+""; //原始文件路径
			    String path = FileNameUtils.getFilePath(filepath);
	            String filename = FileNameUtils.getFileName(filepath);
	            String newname = SequenceUUID.getPureUUID()+filename.substring(filename.lastIndexOf("."), filename.length());
	            String newfilepath = path + "/" + newname;
	            FileUtils.copyFile(filepath, newfilepath); //复制原始文件
	            FileUtils.deleteFile(new File(filepath));
	            map.put("FilePath", newfilepath.replace("/opt/tomcat8_CM/webapps/", "http://www.wotingfm.com:908/"));
			}
		}
		map.put("FileSize", m.get("size"));
		map.put("TimeConsuming", m.get("timeConsuming"));
		map.put("success", "true");
		m.clear();
		m.putAll(map);
        return m;
	}
	
	@Override
	public Map<String, Object> beforeUploadFile(Map<String, Object> rqtAttrs, Map<String, Object> rqtParams,
			HttpSession session) {
		//数据收集处理==1
        alPo.setId(SequenceUUID.getPureUUID());
        alPo.setMethod("POST");
        alPo.setBeginTime(new Timestamp(System.currentTimeMillis()));
        alPo.setApiName("1.1.4-common/uploadCM.do");
        alPo.setObjType("000");//不确定对象
        Map<String, Object> m=new HashMap<String, Object>();
//        m.putAll(rqtAttrs);
        m.putAll(rqtParams);
        alPo.setReqParam(JsonUtils.objToJson(m));
        alPo.setDealFlag(2);//处理失败

        //数据收集处理==2
        alPo.setOwnerType(201);
        if (m.get("UserId")!=null&&!StringUtils.isNullOrEmptyOrSpace(m.get("UserId")+"")) {
            alPo.setOwnerId(m.get("UserId")+"");
        } else {
            if (m.get("DeviceId")!=null&&!StringUtils.isNullOrEmptyOrSpace(m.get("DeviceId")+"")) {
                alPo.setOwnerId(m.get("DeviceId")+"");
            } else {
                alPo.setOwnerId("0");
            }
        }
        if (m.get("DeviceId")!=null&&!StringUtils.isNullOrEmptyOrSpace(m.get("DeviceId")+"")) {
            alPo.setDeviceId(m.get("DeviceId")+"");
        }
        if (m.get("PCDType")!=null&&!StringUtils.isNullOrEmptyOrSpace(m.get("PCDType")+"")) {
            int pcdType=0;
            try {pcdType=Integer.parseInt(m.get("PCDType")+"");} catch(Exception e) {}
            alPo.setDeviceType(pcdType);
        }
        if (m!=null) {
            if (DeviceType.buildDtByPCDType(alPo.getDeviceType())==DeviceType.PC) {
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
        return null;
	}
}