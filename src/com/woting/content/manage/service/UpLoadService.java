package com.woting.content.manage.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.spiritdata.framework.util.DateUtils;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.woting.WtContentMngConstants;
import com.woting.cm.core.dict.model.DictDetail;
import com.woting.cm.core.media.model.MediaAsset;
import com.woting.cm.core.media.model.SeqMediaAsset;
import com.woting.cm.core.media.service.MediaService;
import com.woting.content.publish.utils.CacheUtils;

@Service
public class UpLoadService {
	@Resource
	private DataSource dataSource;
	@Resource
	private MediaService mediaService;
	private String filepath =WtContentMngConstants.ROOT_PATH+"media/";
	
	public UpLoadService() {
		filepath =filepath+DateUtils.convert2DateStr(new Date(System.currentTimeMillis()))+"/";
	}

	public Map<String, Object> saveFileInfo(MultipartFile[] upfiles, Map<String, Object> uploadmap) {
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
		String[] filepaths = new String[2];
		String filename = "";
		String imgname = "";
		// 1.把文件存入服务器里
		for (MultipartFile upfile : upfiles) {
			try {
				if (!upfile.isEmpty()) {
					if (upfile.getContentType().contains("audio")) { // 上传的音频资源
						filename = upfile.getOriginalFilename();
						filepaths[1] = (filepath+filename).trim();
						File file = CacheUtils.createFile(filepaths[1]);
						upfile.transferTo(file);
					}
					if (upfile.getContentType().contains("image")) { // 上传的图片资源
						imgname = upfile.getOriginalFilename();
						filepaths[0] = (filepath+imgname).trim();
						File file = CacheUtils.createFile(filepaths[0]);
						upfile.transferTo(file);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String sequid = uploadmap.get("ContentSequId")+"";
		String sequtitle = uploadmap.get("ContentSequTitle")+"";
		String mediaid = SequenceUUID.getPureUUID(); // 节目id
		Timestamp ctime = new Timestamp(System.currentTimeMillis()); // 节目创建时间
//		// 2.存入wt_MediaAsset表中
//		String sql = "insert into wt_MediaAsset(id,maTitle,maPubType,maPubId,maImg,maURL,keyWords,langDid,language,descn,pubCount,cTime) values(?,?,?,?,?,?,?,?,?,?,?,?)";
//		try {
//			conn = dataSource.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, mediaid);
//			ps.setString(2, (String) uploadmap.get("ContentName"));
//			ps.setInt(3, 3);
//			ps.setString(4, uploadmap.get("UserId") + "");
//			ps.setString(5, StringUtils.isNullOrEmptyOrSpace(filepaths[0])?"默认图片":filepaths[0]);
//			ps.setString(6, filepaths[1]);
//			ps.setString(7, "上传文件测试用待删除");
//			ps.setString(8, "zho");
//			ps.setString(9, "中文");
//			ps.setString(10, StringUtils.isNullOrEmptyOrSpace((String) uploadmap.get("ContentDesc"))?"这家伙真懒，什么都没留下~~":(String) uploadmap.get("ContentDesc"));
//			ps.setInt(11, 0);
//			ps.setTimestamp(12, ctime);
//			ps.executeUpdate();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {closeConnection(conn, ps, rs);}
//
//		// 3.存入wt_MaSource表中
//		sql = "insert into wt_MaSource (id,maId,maSrcType,maSrcId,maSource,smType,playURI,isMain,descn,cTime) values(?,?,?,?,?,?,?,?,?,?)";
//		try {
//			conn = dataSource.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, SequenceUUID.getPureUUID());
//			ps.setString(2, mediaid);
//			ps.setInt(3, 3);
//			ps.setString(4, uploadmap.get("UserId")+"");
//			ps.setString(5, uploadmap.get("SourceId")+"");
//			ps.setInt(6, 1);
//			ps.setString(7, filepaths[1]);
//			ps.setInt(8, 1);
//			ps.setString(9, "上传文件测试用待删除");
//			ps.setTimestamp(10, ctime);
//			ps.executeUpdate();
//		} catch (Exception e) {}finally {closeConnection(conn, ps, rs);}
//		// 4.存入wt_Person_Ref表中

		
		MediaAsset ma = new MediaAsset();
		ma.setId(SequenceUUID.getPureUUID());
		ma.setMaTitle(uploadmap.get("ContentName")+"");
		ma.setMaImg(StringUtils.isNullOrEmptyOrSpace(filepaths[0])?"默认图片":filepaths[0]);
		ma.setMaURL(filepaths[1]);
		ma.setKeyWords("上传文件测试用待删除");
		ma.setMaPubType(3);
		ma.setMaPubId(uploadmap.get("UserId")+"");
		ma.setDescn(StringUtils.isNullOrEmptyOrSpace(uploadmap.get("ContentDesc")+"")?"这家伙真懒，什么都没留下":(uploadmap.get("ContentDesc")+""));
		ma.setPubCount(0);
		ma.setCTime(ctime);
		
		//保存单体资源
		mediaService.saveMa(ma);
		
		//保存专辑与单体媒体对应表
		if (!StringUtils.isNullOrEmptyOrSpace(sequid)||!sequid.equals("null")) {
			SeqMediaAsset sma = new SeqMediaAsset();
			sma.setId(sequid);
			sma.setSmaTitle(sequtitle);
			mediaService.bindMa2Sma(ma, sma);
		}
		
		return null;
	}
}