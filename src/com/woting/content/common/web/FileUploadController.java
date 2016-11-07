package com.woting.content.common.web;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import com.spiritdata.framework.core.web.AbstractFileUploadController;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.FileUtils;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.SequenceUUID;

@Controller
public class FileUploadController extends AbstractFileUploadController{

	@Override
	public Map<String, Object> afterUploadOneFileOnSuccess(Map<String, Object> m, Map<String, Object> rqtAttrs, Map<String, Object> rqtParams, HttpSession session) {
		if (rqtParams!=null) {
			System.out.println("rqtParams="+JsonUtils.objToJson(rqtParams));
		}
		String filepath = m.get("storeFilename")+"";
        String filename = FileNameUtils.getFileName(filepath);
        String path = FileNameUtils.getFilePath(filepath);
        String newname = SequenceUUID.getPureUUID()+filename.substring(filename.lastIndexOf("."), filename.length());
        String newpath = path+"/"+newname;
        FileUtils.copyFile(filepath, newpath);
        FileUtils.deleteFile(new File(filepath));
        m.remove("warn");
        m.put("orglFilename", newname);
        m.put("storeFilename",newpath);
        return m;
	}
}