$(function(){
  var rootPath=getRootPath();
  //获取栏目筛选条件
  $.ajax({
    type:"POST",
    url:rootPath+"content/getFiltrates.do",
    dataType:"json",
    data:{"UserId":"123","MediaType":"MediaAsset"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getChannelLabel(resultData);//得到栏目的筛选标签
        getAlbumLabel(resultData);//得到专辑的筛选标签
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //得到栏目的筛选标签
  function getChannelLabel(resultData){
    for(var i=0;i<resultData.ResultList.ChannelList.length;i++){
      var filterChannel='<li class="trig_item" id='+resultData.ResultList.ChannelList[i].id+' pid='+resultData.ResultList.ChannelList[i].parentId+'>'+
                          '<a  href="javascript:void(0)">'+resultData.ResultList.ChannelList[i].nodeName+'<img src="img/del1.png" alt="取消操作" class="delLi"/></a>'+
                        '</li>';
      $("#channel .attrValues .av_ul").append(filterChannel); 
      var fccg='<div class="tab_cont_item av_ul" parentId='+resultData.ResultList.ChannelList[i].id+'></div>';
      $(".tab_cont").append(fccg);
      if(resultData.ResultList.ChannelList[i].isParent=="true"){
        for(var j=0;j<resultData.ResultList.ChannelList[i].children.length;j++){
          var filterChannelChildren='<div class="trig_item trig_item_li" >'+
                                      '<a  href="javascript:void(0)" id='+resultData.ResultList.ChannelList[i].children[j].id+'>'+resultData.ResultList.ChannelList[i].children[j].nodeName+'<img src="img/del1.png" alt="取消操作" class="delLi"/></a>'+
                                    '</div>';
          $('div[parentId='+resultData.ResultList.ChannelList[i].id+']').append(filterChannelChildren);
        }
      }else{
        $('div[parentId='+resultData.ResultList.ChannelList[i].id+']').append("<span style='display:block;text-align:center;'>暂时没有二级栏目<span>");
      }
    }
  }
  
  //得到专辑的筛选标签
  function getAlbumLabel(resultData){
    for(var i=0;i<resultData.ResultList.SeqMediaList.length;i++){
      var filterAlbum='<li class="trig_item" id='+resultData.ResultList.SeqMediaList[i].PubId+'>'+
                        '<a href="javascript:void(0)">'+resultData.ResultList.SeqMediaList[i].PubName+'<img src="img/del1.png" alt="取消操作" class="delLi"/></a>'+
                      '</li>';
      $("#album .attrValues .av_ul").append(filterAlbum);                
    }
  }
  
  //获取节目列表
  $.ajax({
    type:"POST",
    url:rootPath+"content/media/getMediaList.do",
    dataType:"json",
    data:{"UserId":"123","FlagFlow":"0","ChannelId":"0","SeqMediaId":"0"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getMediaList(resultData); //得到节目列表
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  //得到节目列表
  function getMediaList(resultData){
    for(var i=0;i<resultData.ResultList.AllCount;i++){
      var programBox= '<div class="rtc_listBox">'+
                        '<div class="rtcl_img">'+
                          '<img src='+resultData.ResultList.List[i].ContentImg+' alt="节目图片" />'+
                        '</div>'+
                        '<div class="rtcl_con">'+
                          '<h4>'+resultData.ResultList.List[i].ContentName+'</h4>'+
                          '<p class="zj_name">'+resultData.ResultList.List[i].ContentSeqName+'</p>'+
                          '<p class="other">'+
                            '<span>时间 ：</span>'+
                            '<span>'+resultData.ResultList.List[i].CTime+'</span>'+
                          '</p>'+
                        '</div>'+
                        '<p class="jm_st">'+resultData.ResultList.List[i].ContentPubChannels[0].FlowFlagState+'</p>'+
                        '<div class="op_type">'+
                          '<p class="jm_edit">编辑</p>'+
                          '<p class="jm_pub">发布</p>'+
                          '<p class="jm_del">删除</p>'+
                          '<p class="jm_recal">撤回</p>'+
                        '</div>'+
                      '</div>';
      $(".ri_top3_con").append(programBox);     
    }
  }
  
  /*
  * 
  * 上传节目页面
  * */
 //上传节目页面获取公共标签
  $.ajax({
    type:"POST",
    url:rootPath+"content/getTags.do",
    dataType:"json",
    data:{"UserId":"123","MediaType":"1","SeqMediaId":"704df034185448e3b9ed0801351859fb","ChannelIds":"cn31","TagType":"1","TagSize":"20"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getPubLabel(resultData);//得到上传节目页面公共标签元素
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //得到上传节目页面公共标签元素
  function getPubLabel(resultData){
    for(var i=0;i<resultData.AllCount;i++){
      var label='<li class="gg_tag_con1" tagType='+resultData.ResultList[i].TagOrg+' tagId='+resultData.ResultList[i].TagId+'>'+
                  '<input type="checkbox" class="gg_tag_con1_check" />'+
                  '<span class="gg_tag_con1_span">'+resultData.ResultList[i].TagName+'</span>'+
                '</li>';
      
      $(".gg_tag_con").append(label); 
    }
  }
  
  //上传节目页面获取我的标签
  $.ajax({
    type:"POST",
    url:rootPath+"content/getTags.do",
    dataType:"json",
    data:{"UserId":"123","MediaType":"1","SeqMediaId":"704df034185448e3b9ed0801351859fb","ChannelIds":"cn31","TagType":"2","TagSize":"20"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getMyLabel(resultData);//得到上传节目页面我的标签元素
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //得到上传节目页面我的标签元素
  function getMyLabel(resultData){
    for(var i=0;i<resultData.AllCount;i++){
      var label='<li class="my_tag_con1" tagType='+resultData.ResultList[i].TagOrg+' tagId='+resultData.ResultList[i].TagId+'>'+
                  '<input type="checkbox" class="my_tag_con1_check" />'+
                  '<span class="my_tag_con1_span">'+resultData.ResultList[i].TagName+'</span>'+
                '</li>';
      $(".my_tag_con").append(label); 
    }
  }



});
