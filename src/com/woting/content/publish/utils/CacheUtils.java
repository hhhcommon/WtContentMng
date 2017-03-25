package com.woting.content.publish.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.JsonUtils;

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
	private static String jmurlrootpath = "http://www.wotingfm.com/dataCenter/shareH5/"; // 静态节目content.html路径头信息
	private static String ossrootpath = "/opt/dataCenter/shareH5/";// SystemCache.getCache(FConstants.APPOSPATH).getContent()+""; // 静态文件根路径
	private static String rootpath = SystemCache.getCache(FConstants.APPOSPATH).getContent()+"";
	
	/**
	 * 专辑静态文件发布(info.json,P*.json和content.html)
	 * @param map
	 */
	@SuppressWarnings("unchecked")
	public static void publishZJ(Map<String, Object> map) {
		Map<String, Object> mapsequ = (Map<String, Object>) map.get("ContentDetail");
		List<Map<String, Object>> listaudio = (List<Map<String, Object>>) map.get("SubList");
		int audiosize = listaudio.size();
		String jsonstr = JsonUtils.objToJson(mapsequ);
		//生成 ZJ/info.json
		writeFile(jsonstr, ossrootpath + zjpath + mapsequ.get("ContentId").toString() + "/info.json");
		for (int i = 1; i < audiosize / 15 + 2; i++) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (int num = 0; num < ((i + 1) < (audiosize / 15 + 2) ? 15 : audiosize % 15); num++) {
				list.add(listaudio.get((i - 1) * 15 + num));
				Map<String, Object> map2 = listaudio.get((i - 1) * 15 + num);
				String audiojson = JsonUtils.objToJson(map2);
				//生成 JM/info.json和content.html文件
				writeFile(audiojson, ossrootpath + jmpath + map2.get("ContentId").toString() + "/info.json");
				createJMHtml(ossrootpath + jmpath + map2.get("ContentId").toString() + "/content.html", map2);
			}
			String audios = JsonUtils.objToJson(list);
			//生成 ZJ/P*.json文件和content.html文件
			writeFile(audios, ossrootpath + zjpath + mapsequ.get("ContentId").toString() + "/P" + i + ".json");
			if (i == 1)
				createZJHtml(ossrootpath + zjpath + mapsequ.get("ContentId").toString(), mapsequ, list, audiosize); // 生成content.html
		}
	}

	public static File createFile(String path) {
		File file = new File(path);
		try {
			if (!file.exists()) {
				if (!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
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
	 * @param str WebContent路径
	 * @param rootpath 要编写文件的文件夹路径
	 * @param mapsequ 专辑信息
	 * @param listaudio 单体组信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static void createZJHtml(String path, Map<String, Object> mapsequ, List<Map<String, Object>> listaudio, int num) {
		//存放专辑html模版
		String htmlstr = "";
		//生成节目html模版
		String ulString = "<li class='listBox playBtn' data_src='#####audioplay#####'  share_url='#####shareurl#####'><h4>#####audioname#####</h4><div class='time'>#####audiotime#####</div><p class='lcp'><img src='../../imgs/sl.png' alt=''/><span>#####audioplaycount#####</span><img src='../../imgs/sc.png' alt='' class='sc'/><span class='contentT'>#####audioplaytime#####</span></p></li>";
		//存放节目列表html
		String lis = "";
		htmlstr = readFile(rootpath + templetpath + "/zj_templet/index.html"); // 读取专辑html模版文件
		if (mapsequ.containsKey("ContentPersons")) {
			List<Map<String, Object>> poms = (List<Map<String, Object>>) mapsequ.get("ContentPersons");
			if (poms!=null && poms.size()>0) {
				Map<String, Object> pom = poms.get(0);
				htmlstr = htmlstr.replace("#####zhuboname#####", pom.get("PerName")+"").replace("#####zhuboimgs#####", pom.get("PerImg")+"");
			}
		} else {
			htmlstr = htmlstr.replace("#####zhuboname#####", "")
					.replace("#####zhuboimgs#####", "");
		}
		if (mapsequ.containsKey("ContentKeyWords")) {
			List<Map<String, Object>> kws = (List<Map<String, Object>>) mapsequ.get("ContentKeyWords");
			if (kws!=null && kws.size()>0) {
				String kwstr = "";
				for (Map<String, Object> map : kws) {
					kwstr += "/"+map.get("TagName");
				}
				kwstr = kwstr.substring(1);
				htmlstr = htmlstr.replace("#####sequtag#####", kwstr);
			}
		} else {
			htmlstr = htmlstr.replace("#####sequtag#####", "");
		}
		String descn = mapsequ.get("ContentDesc") == null || mapsequ.get("ContentDesc").toString().length()<1 ? "这家伙真懒，什么也不留下~~~" : mapsequ.get("ContentDesc").toString();
		if (descn!=null) {
			descn = descn.replace("\n", "").replace("\r", "");
		}
		htmlstr = htmlstr.replace("#####sequname#####", mapsequ.get("ContentName").toString())
				.replace("#####sequdescn#####", descn)
				.replace("#####sequimgs#####", mapsequ.get("ContentImg").toString() == null ? "../../imgs/default.png" : mapsequ.get("ContentImg").toString().replace(".png", ".300_300.png"))
		        .replace("#####sequid#####", mapsequ.get("ContentId").toString())
		        .replace("#####mediatype#####", "SEQU")
		        .replace("#####sequsum#####", num+""); // 替换指定的信息
		for (Map<String, Object> map : listaudio) {
			String pubtime = (map.get("ContentPubTime")+"").equals("null")?(map.get("CTime")+""):(map.get("ContentPubTime")+"");
			pubtime = pubtime.substring(0, pubtime.length()-3);
			lis += ulString.replace("#####audioname#####", map.get("ContentName").toString())
					.replace("#####audioplay#####", map.get("ContentPlay").toString())
					.replace("#####shareurl#####",jmurlrootpath + jmpath + map.get("ContentId").toString() + "/content.html")
					.replace("#####audiotime#####", pubtime)
					.replace("#####audioplaycount#####", map.get("PlayCount")+"")
					.replace("#####audioplaytime#####", map.get("ContentTimes")+"");
		}
		htmlstr = htmlstr.replace("#####audiolist#####", lis);
		writeFile(htmlstr, path + "/content.html");
	}

	/**
	 * 创建JM文件夹下的html静态页面
	 * 
	 * @param path
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static void createJMHtml(String path, Map<String, Object> map) {
		//读取节目html模版
		String htmlstr = readFile(rootpath + templetpath + "/jm_templet/index.html");
		if (map.containsKey("ContentPersons")) {
			List<Map<String, Object>> poms = (List<Map<String, Object>>) map.get("ContentPersons");
			if (poms!=null && poms.size()>0) {
				Map<String, Object> pom = poms.get(0);
				htmlstr = htmlstr.replace("#####audiozhubo#####", pom.get("PerName")+"");
			}
		} else {
			htmlstr = htmlstr.replace("#####audiozhubo#####", "");
		}
		String descn = map.get("ContentDesc")==null || map.get("ContentDesc").equals("null") || map.get("ContentDesc").toString().length()<1 ? "欢迎大家收听"+map.get("ContentName")+"" :map.get("ContentDesc")+"";
		if (descn!=null) {
			descn = descn.replace("\n", "").replace("\r", "");
		}
		htmlstr = htmlstr.replace("#####audioname#####", map.get("ContentName")+"")
				.replace("#####mediatype#####", "AUDIO")
		        .replace("#####audioimgs#####", (map.get("ContentImg")+"").equals("null")?"":map.get("ContentImg").toString().replace(".png", ".300_300.png"))
		        .replace("#####audioplay#####", map.get("ContentPlay")+"")
				.replace("#####audioid#####", map.get("ContentId")+"")
				.replace("#####audiotime#####", map.get("ContentTimes")+"")
				.replace("#####audiodescn#####", descn)
				.replace("#####audioseq#####", map.get("ContentSeqName")+"")
				.replace("#####audiosource#####", map.get("ContentPub")+"");
		writeFile(htmlstr, path);
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
		try {
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
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
			in = new InputStreamReader(new FileInputStream(file),"UTF-8");
			br = new BufferedReader(in);
			String zjstr = "";
			while ((zjstr = br.readLine()) != null) {
				sb.append(zjstr);
				sb.append("\r\n");
			}
			in.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
