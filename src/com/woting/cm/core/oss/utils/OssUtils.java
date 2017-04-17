package com.woting.cm.core.oss.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.SequenceUUID;
import com.woting.cm.core.oss.persis.po.OssConfigPo;

public class OssUtils {
	
	/**
	 * OSS上传文件
	 * @param key 文件Object
	 * @param content 字符串
	 * @param isOrNoDelete 是否删除原文件
	 * @return
	 */
	public static boolean upLoadObject(String key, String content, boolean isOrNoDelete) {
		try {
			if (content!=null && key!=null) {
				OssConfigPo ossConfigPo = (OssConfigPo) getBean("ossconfig");
				if (ossConfigPo!=null) {
					OSSClient ossClient = new OSSClient(ossConfigPo.getEndpoint(), ossConfigPo.getAccessKeyId(), ossConfigPo.getAccessKeySecret());
					if (ossClient!=null) {
						File file = createFile(ossConfigPo.getTempFile()+SequenceUUID.getPureUUID()+".json");
						writeFile(content, file);
						ossClient.putObject(ossConfigPo.getBucketName(), key, file);
						if (ossClient.doesObjectExist(ossConfigPo.getBucketName(), key)) {
						    if (isOrNoDelete) deleteFile(file);
						    return true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	/**
	 * OSS上传文件
	 * @param key 文件Object
	 * @param file 文件
	 * @param isOrNoDelete 是否删除原文件
	 * @return
	 */
	public static boolean upLoadObject(String key, File file, boolean isOrNoDelete) {
		try {
			if (file!=null && key!=null) {
				OssConfigPo ossConfigPo = (OssConfigPo) getBean("ossconfig");
				if (ossConfigPo!=null) {
					OSSClient ossClient = new OSSClient(ossConfigPo.getEndpoint(), ossConfigPo.getAccessKeyId(), ossConfigPo.getAccessKeySecret());
					if (ossClient!=null) {
						ossClient.putObject(ossConfigPo.getBucketName(), key, file);
						if (ossClient.doesObjectExist(ossConfigPo.getBucketName(), key)) {
							if (isOrNoDelete) deleteFile(file);
							return true;	
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	/**
	 * 上传网络流
	 * @param key 文件Object
	 * @param url 网络流地址
	 * @return
	 */
	public static boolean upLoadObject(String key, InputStream in) {
		try {
			if (in!=null && key!=null) {
				OssConfigPo ossConfigPo = (OssConfigPo) getBean("ossconfig");
				if (ossConfigPo!=null) {
					OSSClient ossClient = new OSSClient(ossConfigPo.getEndpoint(), ossConfigPo.getAccessKeyId(), ossConfigPo.getAccessKeySecret());
					if (ossClient!=null) {
						ossClient.putObject(ossConfigPo.getBucketName(), key, in);
						if (ossClient.doesObjectExist(ossConfigPo.getBucketName(), key)) {
							in.close();
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	/**
	 * 上传网络流
	 * @param key 文件Object
	 * @param url 网络流地址
	 * @return
	 */
	public static boolean upLoadObject(String key, String url) {
		try {
			if (url!=null && url.length()>0 && key!=null) {
				OssConfigPo ossConfigPo = (OssConfigPo) getBean("ossconfig");
				if (ossConfigPo!=null) {
					OSSClient ossClient = new OSSClient(ossConfigPo.getEndpoint(), ossConfigPo.getAccessKeyId(), ossConfigPo.getAccessKeySecret());
					if (ossClient!=null) {
						InputStream in = new URL(url).openStream();
						if (in != null) {
							ossClient.putObject(ossConfigPo.getBucketName(), key, in);
							if (ossClient.doesObjectExist(ossConfigPo.getBucketName(), key)) {
								in.close();
								return true;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public static InputStream getObjectToInputStream(String key) {
		OssConfigPo ossConfigPo = (OssConfigPo) getBean("ossconfig");
		if (ossConfigPo!=null) {
			OSSClient ossClient = new OSSClient(ossConfigPo.getEndpoint(), ossConfigPo.getAccessKeyId(), ossConfigPo.getAccessKeySecret());
			if (ossClient!=null) {
				OSSObject ossObject = ossClient.getObject(ossConfigPo.getBucketName(), key);
				if (ossObject!=null) {
					InputStream in = ossObject.getObjectContent();
					if (in!=null) {
						return in;
					}
				}
			}
		}
		return null;
	}
	
	public static File getObjectToFile(String key) {
		InputStream is = getObjectToInputStream(key);
		OssConfigPo ossConfigPo = (OssConfigPo) getBean("ossconfig");
		if (is!=null && ossConfigPo!=null) {
			try {
				String name = key.substring(key.lastIndexOf("/"), key.length());
				File file = createFile(ossConfigPo.getTempFile()+name);
				if (writeFile(is, file)) {
					return file;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	public static String getObjectToString(String key) {
		InputStream is = getObjectToInputStream(key);
		if (is!=null) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
				StringBuffer buffer = new StringBuffer();
				String line = "";
				while ((line = in.readLine()) != null){
					buffer.append(line);
				}
				is.close();
				return buffer.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 图片缩放
	 * @param key 图片Object
	 * @param ResizePer 缩放大小
	 * @return
	 * 默认进行格式转换，转为png格式
	 */
	public static InputStream makePictureResize(String key, int ResizePer) {
		try {
			OssConfigPo ossConfigPo = (OssConfigPo) getBean("ossconfig");
			if (ossConfigPo!=null) {
				OSSClient ossClient = new OSSClient(ossConfigPo.getEndpoint(), ossConfigPo.getAccessKeyId(), ossConfigPo.getAccessKeySecret());
				if (ossClient!=null) {
					String style = "image/resize,m_lfit,w_"+ResizePer+",h_"+ResizePer+",limit_0/auto-orient,0";  
					GetObjectRequest request = new GetObjectRequest(ossConfigPo.getBucketName(), key);
					request.setProcess(style);
					OSSObject ossObject = ossClient.getObject(request);
					if (ossObject!=null) {
					    InputStream in = ossObject.getObjectContent();
						if (in!=null) {
							return in;
						}
					}
				}
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	private static boolean writeFile(String jsonstr, File file) {
		try {
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(jsonstr);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (file.exists()) return true;
		else return false;
	}
	
	private static boolean writeFile(InputStream is, File file) {
		try {
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			BufferedWriter writer = new BufferedWriter(write);
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null){
				buffer.append(line);
			}
			writer.write(buffer.toString());
			writer.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (file.exists()) return true;
		else return false;
	}
	
	private static File createFile(String path) {
		File file = new File(path);
		try {
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				} else {
					file.createNewFile();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	private static String readFile(File file) {
		String sb = "";
		if (!file.exists()) return null;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader reader = new BufferedReader(read);
			String line;
			while ((line = reader.readLine()) != null) {
				sb += line;
			}
			read.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb;
	}
	
	private static void deleteFile(File file) {
		if (file!=null && file.exists()) {
			file.delete();
		}
	}
	
	private static Object getBean(String beanName) {
		ServletContext sc=(SystemCache.getCache(FConstants.SERVLET_CONTEXT)==null?null:(ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent());
        if (WebApplicationContextUtils.getWebApplicationContext(sc)!=null) {
            return WebApplicationContextUtils.getWebApplicationContext(sc).getBean(beanName);
        }
		return null;
	}
}
