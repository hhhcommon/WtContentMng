package com.woting.content.share.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.spiritdata.framework.util.JsonUtils;

public class FileUtils {

	public static boolean writeFile(String jsonstr, String path) {
		File file = createFile(path);
		try {
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "GBK");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(jsonstr);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (file.exists())
			return true;
		else
			return false;
	}
	
	public static boolean writeFile(String jsonstr, File file) {
		try {
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(jsonstr);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (file.exists())
			return true;
		else
			return false;
	}
	
	public static boolean writeTxtFile(String content, File fileName){
		boolean flag=false; 
		try {
			RandomAccessFile mm=null;  
			FileOutputStream o=null;  
			try {
				o = new FileOutputStream(fileName);  
			    o.write(content.getBytes("UTF-8"));  
			    o.close();
			    flag=true;
			} catch (Exception e) {
			    e.printStackTrace();
			}finally{
				if(mm!=null) mm.close();
			}  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;  
	}  

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> readFileByJson(String path) {
		String sb = "";
		List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
		File file = new File(path);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "gbk");
			BufferedReader reader = new BufferedReader(read);
			String line;
			while ((line = reader.readLine()) != null) {
				sb += line;
			}
			read.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		l = (List<Map<String, Object>>) JsonUtils.jsonToObj(sb, List.class);
		return l;
	}

	public static String readFile(String path) {
		String sb = "";
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");
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
	
	public static String readFile(File file) {
		String sb = "";
		if (!file.exists()) {
			return null;
		}
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
	
	public static void writeContentInfo(String key, String jsonstr) {
		File file = FileUtils.createFile("/mnt/contentinfo/"+key+".json");
		FileUtils.writeFile(jsonstr, file);
	}

	public static File createFile(String path) {
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

	public static void download(String urlString, String filename, String savePath) throws Exception {
		// 构造URL
		URL url = new URL(urlString);
		// 打开连接
		URLConnection con = url.openConnection();
		// 设置请求超时为5s
		con.setConnectTimeout(50 * 1000);
		// 输入流
		InputStream is = con.getInputStream();
		// 1K的数据缓冲
		byte[] bs = new byte[1024];
		// 读取到的数据长度
		int len;
		// 输出的文件流
		File sf = new File(savePath);
		if (!sf.exists()) {
			sf.mkdirs();
		}
		OutputStream os = new FileOutputStream(sf.getPath() + "/" + filename);
		// 开始读取
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		// 完毕，关闭所有链接
		os.close();
		is.close();
	}
}
