package com.woting.content.manage.fileupload.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.ext.io.StringPrintWriter;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.ReflectUtils;
import com.spiritdata.framework.util.StringUtils;
import com.woting.content.manage.utils.Base64ImgUtils;

public abstract class UploadController implements Controller, HandlerExceptionResolver {
	private String systemPath = ((CacheEle<String>) (SystemCache.getCache(FConstants.APPOSPATH))).getContent();

	private String _defaultPath = "/dataCenter";// 默认路径
	private String savePath = null;// 保存路径

	private int storageModel = 0; // 存储方案，默认0，目前只有一种

	public String get_defaultPath() {
		return _defaultPath;
	}

	public void set_defaultPath(String _defaultPath) {
		this._defaultPath = _defaultPath;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public int getStorageModel() {
		return storageModel;
	}

	public void setStorageModel(int storageModel) {
		this.storageModel = storageModel;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		StringPrintWriter strintPrintWriter = new StringPrintWriter();
		ex.printStackTrace(strintPrintWriter);

		MappingJackson2JsonView mjjv = new MappingJackson2JsonView();
		response.setHeader("Cache-Control", "no-cache");
		mjjv.setContentType("text/html; charset=UTF-8");
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("Data", strintPrintWriter.getString());
		mjjv.setAttributesMap(m);
		ModelAndView mav = new ModelAndView();
		mav.setView(mjjv);
		return mav;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> files = multipartRequest.getFileMap();
		// 得到其他的属性
		Map<String, Object> rqtParams = new HashMap<String, Object>();
		List<String> paramNameL = (List<String>) Collections.list(request.getParameterNames());
		for (String n : paramNameL)
			rqtParams.put(n, request.getParameter(n));
		Map<String, Object> rqtAttrs = new HashMap<String, Object>();
		List<String> attrNameL = (List<String>) Collections.list(request.getAttributeNames());
		for (String n : attrNameL)
			rqtAttrs.put(n, request.getAttribute(n));

		Map<String, Object> retMap = beforeUploadFile(rqtAttrs, rqtParams, request.getSession());
		if (retMap != null && retMap.containsKey("Model")) {
				boolean isTrue = false;
				if (files != null && files.size() > 0) {
					Iterator<String> iterator = files.keySet().iterator();
					while (iterator.hasNext()) {
						CommonsMultipartFile file = (CommonsMultipartFile) files.get(iterator.next());
						if (StringUtils.isNullOrEmptyOrSpace(file.getOriginalFilename())) continue;
						// 处理文件名
						String storeFilename = null;
						String extFileNamesuffix = FileNameUtils.getExt(file.getOriginalFilename());
						String extFileName = FileNameUtils.getPureFileName(file.getOriginalFilename());
						if (retMap.get("Model").equals("1")) {
							storeFilename = retMap.get("FileName")+"";
						} else if (retMap.get("Model").equals("2")) {
							storeFilename = retMap.get("FileName") + extFileNamesuffix;
						}
						String storepath = systemPath + this._defaultPath + retMap.get("Path");
					    storepath = FileNameUtils.concatPath(storepath, storeFilename);
						Map<String, Object> oneFileDealRetMap = saveMultipartFile2File(file, storepath);
						
						if (("" + oneFileDealRetMap.get("Success")).equalsIgnoreCase("TRUE")) {
							retMap = new HashMap<>();
							retMap.put("FileSize", file.getSize());
							retMap.put("FilePath", storepath);
							retMap.put("FileOrigName", extFileName+extFileNamesuffix);
							retMap.put("FileName", storeFilename);
							// 删除临时文件
							delTempFile(file.getFileItem());
							isTrue = true;
						}
					}
				} else {
					if (retMap.containsKey("ContentFile")) {
						String filestr = retMap.get("ContentFile")+"";
						if (filestr.contains(";base64,")) {
							String storeFilename = retMap.get("FileName") + "";
							String storepath = systemPath + this._defaultPath + retMap.get("Path")+"/";
							boolean isok = Base64ImgUtils.GenerateImage(storepath, storeFilename, filestr);
							if (isok) {
								File file = new File(FileNameUtils.concatPath(storepath, storeFilename));
								retMap = new HashMap<>();
								retMap.put("FileSize", file.length());
								retMap.put("FilePath", storepath+storeFilename);
								retMap.put("FileName", storeFilename);
								isTrue = true;
							}
						}
					}
				}
				if (isTrue) {
					retMap.put("Success", true);
					afterUploadOneFileOnSuccess(retMap, rqtAttrs, rqtParams, request.getSession());
				}
		} else {
			retMap = new HashMap<>();
			retMap.put("Success", false);
			retMap.put("Message", "未有处理事务");
		}
		//json处理
        MappingJackson2JsonView mjjv=new MappingJackson2JsonView();
        response.setHeader("Cache-Control", "no-cache");
        mjjv.setContentType("text/html; charset=UTF-8");
        mjjv.setAttributesMap(retMap);
        //mjjv.setAttributesMap(JsonUtils.obj2AjaxMap(retl, 1));
        ModelAndView mav=new ModelAndView();
        mav.setView(mjjv);
        return mav;
	}

	/*
	 * 保存MultipartFile类型文件到文件系统，采用NIO的方法
	 * 
	 * @param file 传过来的MultipartFile对象
	 * 
	 * @param fileName 保存路径，包括文件名
	 * 
	 * @return 保存file属性的Map，主要是是否成功和文件名称
	 */
	private Map<String, Object> saveMultipartFile2File(MultipartFile file, String fileName) {
		FileOutputStream fileOut = null;
		InputStream in = null;
		FileChannel fcOut = null;
		Map<String, String> em = new HashMap<String, String>();
		Map<String, Object> m = new HashMap<String, Object>();

		m.put("UploadTime", (new Date()).getTime());
		m.put("FileSize", file.getSize());
		// 处理文件名
		try {
			String dirName = FileNameUtils.getFilePath(fileName);
			File storeFilePath = new File(dirName);
			if (!storeFilePath.isDirectory())
				storeFilePath.mkdirs();

			File storeFile = new File(fileName);
			if (storeFile.isDirectory()) {// 如果文件名是一个路径，报错
				em.put("ErrCode", "FUE003");
				em.put("ErrMsg", "指定的上传文件名称已经作为目录存在了");
				em.put("ErrInfo", "目录[" + storeFile + "]已经存在，无法拷贝。");
				m.put("Error", em);
				m.put("StoreFileName", fileName);
			} else if (storeFile.isFile()) {// 如果文件已经存在
				// TODO
			}
		} catch (Exception e) {
			em.put("errCode", "FUE_E");
			em.put("errMsg", e.getMessage());
			m.put("error", em);
		}
		if (m.get("error") != null) {
			m.put("success", false);
			return m;
		}
		// 填充返回信息
		Class<?> clazz = file.getClass();
		Map<String, String> _m = ReflectUtils.Object2Map(clazz, file);
		StringBuffer _fileItem = new StringBuffer(_m.get("fileItem"));
		m.put("OrglFilename", _fileItem.substring(5, _fileItem.indexOf("StoreLocation") - 2));
		m.put("FieldName", _fileItem.substring(_fileItem.indexOf("FieldName=") + 10));
		try {
			File outputFile = new File(fileName);
			if (!outputFile.isFile()) {
				if (!outputFile.createNewFile()) {
					em.put("errCode", "FUE004");
					em.put("errMsg", "创建文件失败");
					em.put("errInfo", "文件[" + fileName + "]创建失败");
					m.put("error", em);
					m.put("storeFilename", fileName);
					m.put("success", false);
					return m;
				}
			}
			fileOut = new FileOutputStream(outputFile);
			fcOut = fileOut.getChannel();

			ByteBuffer buffer = ByteBuffer.allocate(1024);
			in = file.getInputStream();
			byte[] inbf = new byte[1024];
			while (in.read(inbf) != -1) {
				buffer.put(inbf);
				buffer.flip();
				fcOut.write(buffer);
				buffer.clear();
			}
			m.put("Success", true);
			m.put("StoreFileName", fileName);
			m.put("TimeConsuming", (new Date()).getTime() - Long.parseLong("" + m.get("UploadTime")));
			return m;
		} catch (Exception e) {
			em.put("ErrCode", "FUE_E");
			em.put("ErrMsg", e.getMessage());
			m.put("Euccess", false);
			m.put("EtoreFileName", fileName);
			m.put("TimeConsuming", (new Date()).getTime() - Long.parseLong("" + m.get("UploadTime")));
			m.put("Error", em);
			return m;
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fcOut != null) {
				try {
					fcOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 删除临时文件
			file.getOriginalFilename();
		}
	}

	private boolean delTempFile(FileItem fi) {
		String fiStr = fi.toString();
		String[] ss = StringUtils.splitString(fiStr, ",");
		fiStr = null;
		for (int i = 0; i < ss.length; i++) {
			if (ss[i].startsWith(" StoreLocation=")) {
				fiStr = ss[i];
				break;
			}
		}
		if (fiStr != null) {
			ss = StringUtils.splitString(fiStr, "=");
			fiStr = ss[1];
			File f = new File(fiStr);
			return f.delete();
		}
		return false;
	}

	/**
	 * 上传文件之间，调用此方法。用来进行一些必要的判断。节省上传文件的时间。
	 * 
	 * @param rqtAttrs
	 *            request的属性
	 * @param rqtParams
	 *            request的参数
	 * @return 此方法的返回值是Map，errorMap:Map，错误信息<br/>
	 *         如果返回值为空，则说明处理成功<br/>
	 */
	public Map<String, Object> beforeUploadFile(Map<String, Object> rqtAttrs, Map<String, Object> rqtParams,
			HttpSession session) {
		return null;
	}
	
	/**
     * 当成功上传一个文件后，调用此方法。
     * @param m 成功上传的文件的信息，包括：success——是否上传成功;storeFilename——保存在服务器端的文件名;fileInfo——上传文件的信息，类型为MultipartFile
     * 若上传失败，还会有error信息;<br/>
     * 警告信息会存储在warn信息中。<br/>
     * @param rqtAttrs request的属性
     * @param rqtParams request的参数
     * @return  此方法的返回值是Map，此Map需要有如下两个key:<br/>
     * 1-success:String类型,处理是否成功<br/>
     * 2-onFaildBreak:String类型("true" or "false"),若失败是否退出后需的处理<br/>
     * 如果返回值为空，或没有这些信息，本方法将按照sucess=true进行处理<br/>
     * 若要把自己的处理结果传递到本方法的外面，可以直接修改参数m，在m中加入自己的信息
     */
    public Map<String, Object> afterUploadOneFileOnSuccess(Map<String, Object> m, Map<String, Object> rqtAttrs, Map<String, Object> rqtParams, HttpSession session) {
        return null;
    }

}
