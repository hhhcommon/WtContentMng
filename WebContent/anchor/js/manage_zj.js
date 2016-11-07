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
  
  //获取专辑列表
  $.ajax({
    type:"POST",
    url:rootPath+"content/seq/getSeqMediaList.do",
    dataType:"json",
    data:{"UserId":"123","FlagFlow":"0","ChannelId":"lmType1","ShortSearch":"false"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getSeqMediaList(resultData); //得到专辑列表
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //得到专辑列表
  function getSeqMediaList(resultData){
    for(var i=0;i<resultData.ResultList.length;i++){
      var albumBox= '<div class="rtc_listBox">'+
                      '<div class="rtcl_img">'+
                        '<img src='+resultData.ResultList[i].ContentImg+' alt="节目图片" />'+
                      '</div>'+
                      '<div class="rtcl_con">'+
                        '<p class="rtcl_con_name">'+
                          '<span>专辑名称 ：</span>'+
                          '<span>'+resultData.ResultList[i].ContentName+'</span>'+
                        '</p>'+
                        '<p class="rtcl_con_num">'+
                          '<span>节目数量 ： </span>'+
                          '<span>'+resultData.ResultList[i].SubCount+'</span>'+
                        '</p>'+
                        '<p class="rtcl_con_time">'+
                          '<span>时间 ：</span>'+
                          '<span>'+resultData.ResultList[i].CTime+'</span>'+
                        '</p>'+
                      '</div>'+
                      '<p class="jm_st">'+resultData.ResultList[i].ContentPubChannels[0].FlowFlagState+'</p>'+
                      '<div class="op_type">'+
                        '<p class="jm_edit">编辑</p>'+
                        '<p class="jm_pub">发布</p>'+
                        '<p class="jm_del">删除</p>'+
                        '<p class="jm_recal">撤回</p>'+
                      '</div>'+
                    '</div>';
      $(".ri_top3_con").append(albumBox);              
    }
  }
})
