package com.woting.content.manage.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.springframework.util.Base64Utils;

import com.spiritdata.framework.util.StringUtils;
import com.woting.content.publish.utils.CacheUtils;

public class Base64ImgUtils {

	public static boolean GenerateImage(String path, String fileName, String base64){
		if (!StringUtils.isNullOrEmptyOrSpace(base64)) {
			base64 = base64.replace("data:image/jpeg;base64,", "").replace("data:image/png;base64,", "").replace("data:image/jpg;base64,", "");
			byte[] by = Base64Utils.decodeFromString(base64);
			File file = CacheUtils.createFile(path+fileName);
			try {
				OutputStream ou = new FileOutputStream(file);
				ou.write(by);
				ou.flush();
				ou.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
}
