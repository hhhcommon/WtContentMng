package com.woting.content.common.web;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.cm.core.oss.utils.OssUtils;
import com.woting.content.manage.fileupload.web.UploadController;
import com.woting.dataanal.gather.API.persis.pojo.ApiLogPo;
import com.woting.passport.session.DeviceType;
import com.woting.passport.session.SessionService;

public class FileUploadController extends UploadController {
	private ApiLogPo alPo = new ApiLogPo();
	@Resource(name = "redisSessionService")
	private SessionService sessionService;

	@Override
	public Map<String, Object> afterUploadOneFileOnSuccess(Map<String, Object> m, Map<String, Object> rqtAttrs,
			Map<String, Object> rqtParams, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		String srcType = rqtParams.get("SrcType") + "";
		String purpose = rqtParams.get("Purpose") + "";
		if (srcType.equals("1")) { // 图片处理
			String filepath = m.get("FilePath") + ""; // 原始文件路径
			//FileNameUtils.getFilePath(filepath);
			String ext = FileNameUtils.getExt(filepath);
			String newname = FileNameUtils.getPureFileName(filepath);
			String newfilepath = "";
			if (purpose.equals("1")) { // 用户头像处理
				try {
					String path = "userimg";
					newfilepath = FileNameUtils.concatPath(path, newname + ext);
					String imgpath = FileNameUtils.concatPath(path, newname+ext);
					String img150path = FileNameUtils.concatPath(path, newname + ".150_150" + ext);
					String img300path = FileNameUtils.concatPath(path, newname + ".300_300" + ext);
					String img450path = FileNameUtils.concatPath(path, newname + ".450_450" + ext);
					OssUtils.upLoadObject(imgpath, new File(filepath), true);
					OssUtils.makePictureResize(imgpath, img150path, 150);
					OssUtils.makePictureResize(imgpath, img300path, 300);
					OssUtils.makePictureResize(imgpath, img450path, 450);
//					Thumbnails.of(new File(filepath)).size(150, 150).toFile(img150path);
//					Thumbnails.of(new File(filepath)).size(300, 300).toFile(img300path);
//					Thumbnails.of(new File(filepath)).size(450, 450).toFile(img450path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (purpose.equals("2")) { // 内容图片处理
					try {
						String path = "contentimg";
						newfilepath = FileNameUtils.concatPath(path, newname + ext);
						String imgpath = FileNameUtils.concatPath(path, newname + ext);
						String img180path = FileNameUtils.concatPath(path, newname + ".180_180" + ext);
						String img300path = FileNameUtils.concatPath(path, newname + ".300_300" + ext);
						OssUtils.upLoadObject(imgpath, new File(filepath), true);
						OssUtils.makePictureResize(imgpath, img180path, 180);
						OssUtils.makePictureResize(imgpath, img300path, 300);
//						Thumbnails.of(new File(filepath)).size(180, 180).toFile(img180path);
//						Thumbnails.of(new File(filepath)).size(300, 300).toFile(img300path);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if (purpose.equals("3")) { // 轮播图处理
						try {
							String path = "contentimg";
							newfilepath = FileNameUtils.concatPath(path, newname + ext);
							String imgpath = FileNameUtils.concatPath(path, newname + ext);
							String img1080_450path = FileNameUtils.concatPath(path, newname + ".1080_450" + ext);
							OssUtils.upLoadObject(imgpath, new File(filepath), true);
							OssUtils.makePictureResize(imgpath, img1080_450path, 1080, 450);
//							Thumbnails.of(new File(filepath)).size(1080, 450).toFile(img1080_450path);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						if (purpose.equals("4")) { // 栏目图处理
							try {
								String path = "contentimg";
								newfilepath = FileNameUtils.concatPath(path, newname + ext);
								String imgpath = FileNameUtils.concatPath(path, newname + ext);
								String img100_100path = FileNameUtils.concatPath(path, newname + ".100_100" + ext);
								OssUtils.upLoadObject(imgpath, new File(filepath), true);
								OssUtils.makePictureResize(imgpath, img100_100path, 100);
//								Thumbnails.of(new File(filepath)).size(100, 100).toFile(img100_100path);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			map.put("FilePath", "http://ac.wotingfm.com/" + newfilepath);
			map.put("Model", "1");
		} else {
			if (srcType.equals("2")) {
				String filepath = m.get("FilePath") + ""; // 原始文件路径
				String path = "contentmedia";
				String newname = FileNameUtils.getPureFileName(filepath);
				String ext = FileNameUtils.getExt(filepath);
				String newfilepath = path + "/" + newname + ext;
				OssUtils.upLoadObject(newfilepath, new File(filepath), true);
				map.put("FilePath", "http://ac.wotingfm.com/" + newfilepath);
				map.put("Model", "2");
			}
		}
		m.putAll(map);
		return m;
	}

	@Override
	public Map<String, Object> beforeUploadFile(Map<String, Object> rqtAttrs, Map<String, Object> rqtParams,
			HttpSession session) {
		// 数据收集处理==1
		alPo.setId(SequenceUUID.getPureUUID());
		alPo.setMethod("POST");
		alPo.setBeginTime(new Timestamp(System.currentTimeMillis()));
		alPo.setApiName("1.1.4-common/uploadCM.do");
		alPo.setObjType("000");// 不确定对象
		Map<String, Object> m = new HashMap<String, Object>();
		// m.putAll(rqtAttrs);
		m.putAll(rqtParams);
		alPo.setReqParam(JsonUtils.objToJson(m));
		alPo.setDealFlag(2);// 处理失败

		// 数据收集处理==2
		alPo.setOwnerType(201);
		if (m.get("UserId") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("UserId") + "")) {
			alPo.setOwnerId(m.get("UserId") + "");
		} else {
			if (m.get("DeviceId") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("DeviceId") + "")) {
				alPo.setOwnerId(m.get("DeviceId") + "");
			} else {
				alPo.setOwnerId("0");
			}
		}
		if (m.get("DeviceId") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("DeviceId") + "")) {
			alPo.setDeviceId(m.get("DeviceId") + "");
		}
		if (m.get("PCDType") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("PCDType") + "")) {
			int pcdType = 0;
			try {
				pcdType = Integer.parseInt(m.get("PCDType") + "");
			} catch (Exception e) {
			}
			alPo.setDeviceType(pcdType);
		}
		if (m != null) {
			if (DeviceType.buildDtByPCDType(alPo.getDeviceType()) == DeviceType.PC) {
				if (m.get("MobileClass") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("MobileClass") + "")) {
					alPo.setExploreVer(m.get("MobileClass") + "");
				}
				if (m.get("exploreName") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("exploreName") + "")) {
					alPo.setExploreName(m.get("exploreName") + "");
				}
			} else {
				if (m.get("MobileClass") != null && !StringUtils.isNullOrEmptyOrSpace(m.get("MobileClass") + "")) {
					alPo.setDeviceClass(m.get("MobileClass") + "");
				}
			}
		}
		String srcType = rqtParams.get("SrcType") + "";
		String purpose = rqtParams.get("Purpose") + "";
		if (srcType.equals("1")) { // 图片处理
			m.put("Model", "1");
			String newname = SequenceUUID.getPureUUID() + ".png";
			if (purpose.equals("1")) { // 用户头像处理
				m.put("FileName", newname);
				m.put("Path", "/userimg");
			} else {
				if (purpose.equals("2")) { // 内容图片处理
					m.put("FileName", newname);
					m.put("Path", "/contentimg");
				} else {
					if (purpose.equals("3")) { // 轮播图处理
						m.put("FileName", newname);
						m.put("Path", "/contentimg");
					} else {
						if (purpose.equals("4")) { // 栏目图处理
							m.put("FileName", newname);
							m.put("Path", "/contentimg");
						}
					}
				}
			}
		} else {
			if (srcType.equals("2")) {
				m.put("Model", "2");
				String newname = SequenceUUID.getPureUUID();
				m.put("FileName", newname);
				m.put("Path", "/contentmedia");
			}
		}
		m.putAll(rqtParams);
		return m;
	}

}
