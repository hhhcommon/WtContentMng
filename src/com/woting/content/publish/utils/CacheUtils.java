package com.woting.content.publish.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.spiritdata.framework.util.JsonUtils;
import com.woting.WtContentMngConstants;

/**
 * 静态数据生成工具
 * 
 * @author wbq
 *
 */
public abstract class CacheUtils {
	private static String zjpath = "mweb/zj/";
	private static String jmpath = "mweb/jm/";
	private static String templetpath = "mweb/templet/";
	// http://localhost:908/CM/ http://182.92.175.134:908/CM/
	private static String jmurlrootpath = "http://localhost:908/CM/"; // 静态节目content.html路径头信息
	private static String rootpath = WtContentMngConstants.ROOT_PATH; // 静态文件根路径

	/**
	 * 专辑静态文件发布(info.json,P*.json和content.html)
	 * @param map
	 */
	public static void publishZJ(Map<String, Object> map) {
		Map<String, Object> mapsequ = (Map<String, Object>) map.get("ContentDetail");
		List<Map<String, Object>> listaudio = (List<Map<String, Object>>) map.get("SubList");
		int audiosize = listaudio.size();
		String jsonstr = JsonUtils.objToJson(mapsequ);
		//生成 ZJ/info.json
		writeFile(jsonstr, rootpath + zjpath + mapsequ.get("ContentId").toString() + "/info.json");
		for (int i = 1; i < audiosize / 15 + 2; i++) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (int num = 0; num < ((i + 1) < (audiosize / 15 + 2) ? 15 : audiosize % 15); num++) {
				list.add(listaudio.get((i - 1) * 15 + num));
				Map<String, Object> map2 = listaudio.get((i - 1) * 15 + num);
				String audiojson = JsonUtils.objToJson(map2);
				//生成 JM/info.json和content.html文件
				writeFile(audiojson, rootpath + jmpath + map2.get("ContentId").toString() + "/info.json");
				createJMHtml(rootpath + jmpath + map2.get("ContentId").toString() + "/content.html", map2);
			}
			String audios = JsonUtils.objToJson(list);
			//生成 ZJ/P*.json文件和content.html文件
			writeFile(audios, rootpath + zjpath + mapsequ.get("ContentId").toString() + "/P" + i + ".json");
			if (i == 1)
				createZJHtml(rootpath + zjpath + mapsequ.get("ContentId").toString(), mapsequ, list,
						(audiosize / 15) > 0);// 生成content.html
		}
	}

	private static File createFile(String path) {
		File file = new File(path);
		try {
			if (!file.exists()) {
				if (!file.getParentFile().exists())
					file.getParentFile().mkdirs();
				else {
					file.createNewFile();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return file;
	}

	/**
	 * 创建ZJ文件夹里的html静态页面
	 * 
	 * @param str
	 *            WebContent路径
	 * @param rootpath
	 *            要编写文件的文件夹路径
	 * @param mapsequ
	 *            专辑信息
	 * @param listaudio
	 *            单体组信息
	 * @return
	 */
	private static boolean createZJHtml(String path, Map<String, Object> mapsequ, List<Map<String, Object>> listaudio,
			boolean hasnextpage) {
		//存放专辑html模版
		String htmlstr = "";
		//生成节目html模版
		String ulString = "<li class='audioLi' data-src='#####audioplay#####'><div class='audioIntro'><a href='#####audiourl#####'><h3>#####audioname#####</h3></a><p>2015-10-26</p></div><a href='javascript:void(0)' class='playBtn'></a></li>";
		//生成html代码判断是否加载完毕
		String jsstr = "<script>nextPage='#####nextpage#####';if(nextPage=='false'){$('.loadMore').text('全部加载完毕！').off('click');}</script>";
		//存放节目列表html
		String lis = "";
		htmlstr = readFile(rootpath + templetpath + "/zj_templet/index.html"); // 读取专辑html模版文件
		htmlstr = htmlstr.replace("#####sequname#####", mapsequ.get("ContentName").toString())
				.replace("#####sequdesc#####",
						mapsequ.get("ContentDesc").toString() == null ? "暂无描述" : mapsequ.get("ContentDesc").toString())
				.replace("#####sequimgs#####", mapsequ.get("ContentImg").toString() == null
						? "../../templet/zj_templet/imgs/default.png" : mapsequ.get("ContentImg").toString());
		htmlstr = htmlstr.replace("#####sequid#####", mapsequ.get("ContentId").toString()); // 替换指定的信息
		for (Map<String, Object> map : listaudio) {
			lis += ulString.replace("#####audioname#####", map.get("ContentName").toString())
					.replace("#####audioplay#####", map.get("ContentURI").toString()).replace("#####audiourl#####",
							jmurlrootpath + jmpath + map.get("ContentId").toString() + "/content.html");
		}

		String p2exists = "false"; // 下一页是否存在
		if (hasnextpage) p2exists = "true";
		
		htmlstr = htmlstr.replace("#####audiolist#####", lis);
		jsstr = jsstr.replace("#####nextpage#####", p2exists);
		htmlstr = htmlstr.replace("#####js#####", jsstr);
		writeFile(htmlstr, path + "/content.html");
		return false;
	}

	/**
	 * 创建JM文件夹下的html静态页面
	 * 
	 * @param path
	 * @param map
	 * @return
	 */
	private static boolean createJMHtml(String path, Map<String, Object> map) {
		//读取节目html模版
		String htmlstr = readFile(rootpath + templetpath + "/jm_templet/index.html");
		htmlstr = htmlstr.replace("#####audioname#####", map.get("ContentName").toString());
		htmlstr = htmlstr.replace("#####audioimgs#####", map.get("ContentImg").toString());
		htmlstr = htmlstr.replace("#####audioplay#####", map.get("ContentURI").toString());
		//处理节目时长
		long ctime = (long) map.get("ContentTimes") / 1000;
		String hous = String.valueOf(ctime / 360);
		String minute = String.valueOf(ctime % 360 / 60).length() == 1 ? ("0" + String.valueOf(ctime % 360 / 60))
				: String.valueOf(ctime % 360 / 60);
		String second = String.valueOf(ctime % 60).length() == 1 ? ("0" + String.valueOf(ctime % 60))
				: String.valueOf(ctime % 60);
		String playtime = "";
		if (!hous.equals("0")) playtime += hous + ":";
		if (minute.equals("0")) playtime += "00:";
		else playtime += minute + ":";
		playtime += second;
		htmlstr = htmlstr.replace("#####audiotime#####", playtime);
		writeFile(htmlstr, path);
		return false;
	}

	/**
	 * 写.json文件
	 * 
	 * @param jsonstr
	 * @param file
	 * @return
	 */
	public static boolean writeFile(String jsonstr, String path) {
		File file = createFile(path);
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(jsonstr);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (file.exists())
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String readFile(String path) {
		StringBuilder sb = new StringBuilder();
		InputStreamReader in = null;
		BufferedReader br = null;
		File file = new File(path);
		try {
			in = new InputStreamReader(new FileInputStream(file));
			br = new BufferedReader(in);
			String zjstr = "";
			try {
				while ((zjstr = br.readLine()) != null) {
					sb.append(zjstr);
					sb.append("\r\n");
				}
				in.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}