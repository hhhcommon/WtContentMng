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

/**
 * 静态数据工具
 * 
 * @author wbq
 *
 */
public abstract class CacheUtils {

	private static String zjpath = "mweb/zj/";
	private static String jmpath = "mweb/jm/";
	private static String templetpath = "mweb/templet/";

	public static void updateFile(Map<String, Object> map) {
		String str = CacheUtils.class.getClassLoader().getResource("").toString();
		str = str.replaceAll("file:/", "").replaceAll("WEB-INF/classes/", "");
		Map<String, Object> mapsequ = (Map<String, Object>) map.get("ContentDetail");
		List<Map<String, Object>> listaudio = (List<Map<String, Object>>) map.get("SubList");
		String path = str;
		int audiosize = listaudio.size();
		String jsonstr = JsonUtils.objToJson(mapsequ);
		File filesequ = createFile(path + zjpath + mapsequ.get("ContentId").toString() + "/info.json");
		writeFile(jsonstr, filesequ);
		File fileaudioinfo = null;
		for (int i = 1; i < audiosize / 15 + 2; i++) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (int num = 0; num < ((i + 1) < (audiosize / 15 + 2) ? 15 : audiosize % 15); num++) {
				list.add(listaudio.get(i - 1 + num));
				Map<String, Object> map2 = listaudio.get((i - 1) * 15 + num);
				String audiojson = JsonUtils.objToJson(map2);
				fileaudioinfo = createFile(path + jmpath + map2.get("ContentId").toString() + "/info.json");
				writeFile(audiojson, fileaudioinfo);
			}
			if (i == 1) // 生成content.html和P1.html文件
				createZJHtml(str, zjpath + mapsequ.get("ContentId").toString(), mapsequ, list);
			else // 生成静态的分页html文件
				createZJHtml(str, zjpath + mapsequ.get("ContentId").toString() + "/P" + i + ".html", null, list);
			String audios = JsonUtils.objToJson(list);
			File fileP = createFile(path + zjpath + mapsequ.get("ContentId").toString() + "/P" + i + ".json");
			writeFile(audios, fileP);
		}
	}

	public static boolean publishZJ(Map<String, Object> map) {
		return false;
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
	 * @param path
	 *            要编写文件的文件夹路径
	 * @param mapsequ
	 *            专辑信息
	 * @param listaudio
	 *            单体组信息
	 * @return
	 */
	private static boolean createZJHtml(String str, String path, Map<String, Object> mapsequ,
			List<Map<String, Object>> listaudio) {
		String htmlstr = "";
		String ulString = "<li><div class='audio_intro'><a href='#####audiohtml#####'><h3>#####audioname#####</h3></a><p>2015-10-26</p></div><a href='javascript:void(0)' class='play_btn'><audio src='#####audiourl#####' loop='loop' ></audio></a></li>";
		String lis = "";
		if (mapsequ != null) {
			htmlstr = readFile(str + templetpath + "/zj_templet/index.html"); // 读取专辑html模版文件
			htmlstr = htmlstr.replaceAll("#####sequname#####", mapsequ.get("ContentName").toString())
					.replaceAll("#####sequdesc#####", mapsequ.get("ContentDesc").toString()==null?"imgs/default.png":mapsequ.get("ContentDesc").toString())
					.replaceAll("#####sequimgs#####", "../../templet/zj_templet/imgs/default.png");//mapsequ.get("ContentImg").toString());
			for (Map<String, Object> map : listaudio) {
				lis += ulString.replaceAll("#####audioname#####", map.get("ContentName").toString()).replaceAll("#####audiourl#####", map.get("ContentURI").toString());
			}
			htmlstr = htmlstr.replaceAll("#####audiolist#####", lis);
			File file = createFile(str + path + "/P1.html"); // P1.html
			writeFile(lis, file);
			file = createFile(str + path + "/content.html");
			writeFile(htmlstr, file);
		} else {
			for (Map<String, Object> map : listaudio) { // 其他分页的html数据
				lis += ulString.replaceAll("#####audioname#####", map.get("ContentName").toString()).replaceAll("#####audiourl#####", map.get("ContentURI").toString());
			}
			File file = createFile(str + path);
			writeFile(lis, file);
		}
		return false;
	}

	/**
	 * 写.json文件
	 * 
	 * @param jsonstr
	 * @param file
	 * @return
	 */
	private static boolean writeFile(String jsonstr, File file) {
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

	private static String readFile(String path) {
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
