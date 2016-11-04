$(function(){
  var rootPath=getRootPath();
  //获取筛选条件标签
  $.ajax({
    type:"POST",
    url:rootPath+"content/getFiltrates.do",
    dataType:"json",
    data:{"UserId":"123","MediaType":"MediaAsset"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        console.log(resultData.ResultList.ChannelList);
        for(var i=0;i<3;i++){
          var filter= '<li class="trig_item" >'+
                        '<a  href="javascript:void(0)">你好<img src="img/del1.png" alt="取消操作" class="delLi"/></a>'+
                      '</li>';
          $("#channel .attrValues .av_ul").append(filter); 
        }
                 
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
});
