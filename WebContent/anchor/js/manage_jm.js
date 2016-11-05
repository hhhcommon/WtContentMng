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
  
 //上传节目页面获取标签
  $.ajax({
    type:"POST",
    url:rootPath+"content/getTags.do",
    dataType:"json",
    data:{"UserId":"123","MediaType":"1","SeqMediaId":"704df034185448e3b9ed0801351859fb","ChannelIds":"cn31","TagType":"1","TagSize":"20"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getLabel(resultData);//得到上传节目页面标签元素
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //得到上传节目页面标签元素
  function getLabel(resultData){
    for(var i=0;i<resultData.AllCount;i++){
      var label='<li class="gg_tag_con1" tagId='+resultData.ResultList[i].TagId+'>'+
                  '<input type="checkbox" class="gg_tag_con1_check" />'+
                  '<span class="gg_tag_con1_span">'+resultData.ResultList[i].TagName+'</span>'+
                '</li>';
      $(".gg_tag_con").append(label); 
    }
    
              
  }
  




});
