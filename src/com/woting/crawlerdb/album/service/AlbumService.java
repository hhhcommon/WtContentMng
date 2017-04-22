package com.woting.crawlerdb.album.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.woting.crawlerdb.album.persis.po.AlbumPo;

@Service
public class AlbumService {

	@Resource(name = "defaultDAO_DB")
	private MybatisDAO<AlbumPo> albumDao;

	@PostConstruct
	public void initParam() {
		albumDao.setNamespace("A_ALBUM");
	}
	
	public void insertAlbumList(List<AlbumPo> albumlist){
		Map<String, Object> m = new HashMap<>();
		m.put("list", albumlist);
		albumDao.insert("insertList", m);
	}
	
	public List<AlbumPo> getAlbumList(int page, int pagesize ,String num){
		Map<String, Object> m = new HashMap<String,Object>();
		m.put("page", (page-1)*pagesize);
		m.put("pagesize", pagesize);
		m.put("crawlerNum", num);
		List<AlbumPo> aus = albumDao.queryForList("getAlbumList", m);
		return aus;
	}
	
	public List<AlbumPo> getAlbumListBy(Map<String, Object> m) {
		List<AlbumPo> als = albumDao.queryForList("getList", m);
		if (als!=null && als.size()>0) {
			return als;
		}
		return null;
	}
	
	public List<AlbumPo> getAlbumList(String num){
		Map<String, Object> m = new HashMap<String,Object>();
		m.put("crawlerNum", num);
		List<AlbumPo> aus = albumDao.queryForList("getAlbumListByCrawlerNum", m);
		return aus;
	}
	
	public List<AlbumPo> getAlbumListById(String id){
		List<AlbumPo> aus = albumDao.queryForList("getAlbumInfo", id);
		return aus;
	}
	
	public AlbumPo getAlbumInfo(String id){
		return albumDao.getInfoObject("getAlbumInfo", id);
	}
	
	public int countNum(String crawlerNum){
		int num = albumDao.getCount("count",crawlerNum);
		return num;
	}
	
	public void removeAlbumById(String id) {
		Map<String, Object> m = new HashMap<>();
		m.put("id", id);
		albumDao.delete("deleteBy", m);
	}
	
	public void removeSameAlbum(String albumId, String publisher ,String num){
		Map<String, Object> m = new HashMap<String,Object>();
		m.put("albumId", albumId);
		m.put("albumPublisher", publisher);
		m.put("crawlerNum", num);
		albumDao.delete("deleteBy", m);
	}
}
