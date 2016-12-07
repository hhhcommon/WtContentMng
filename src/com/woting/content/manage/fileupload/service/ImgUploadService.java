package com.woting.content.manage.fileupload.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.content.manage.utils.Base64ImgUtils;

@Service
public class ImgUploadService {

	public Map<String, Object> SaveBase64Img(String base64Code, String purpose) {
		Map<String, Object> m = new HashMap<>();
		String path = SystemCache.getCache(FConstants.APPOSPATH).getContent()+"";
		path += "uploadFiles\\tempuplf\\";
		String fileName = SequenceUUID.getPureUUID()+".png";
		boolean isok = Base64ImgUtils.GenerateImage(path, fileName, base64Code);
		path = "http://www.wotingfm.com:908/CM/WebContent/uploadFiles/tempuplf/";
		if (isok) {
			m.put("FileName", fileName);
			m.put("FilePath", path+fileName);
			m.put("ReturnType", "1001");
			return m;
		}
		return null;
	}
}
