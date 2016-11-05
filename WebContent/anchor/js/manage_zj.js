$(function(){
  var rootPath=getRootPath();
  //获取专辑列表
  $.ajax({
    type:"POST",
    url:rootPath+"content/seq/getSeqMediaList.do",
    dataType:"json",
    data:{"UserId":"123","FlagFlow":"0","ChannelId":"lmType1","ShortSearch":"false"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getAlbumList(resultData); //得到专辑列表
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //得到专辑列表
  function getAlbumList(resultData){
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
