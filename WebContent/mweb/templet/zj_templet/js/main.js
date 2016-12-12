$(function(){
  var rootPath=getRootPath();
  
  var page=2;
  var nextPage="false";
  //详情和节目的切换
  $(".tab_nav span").on("click",function(){
    $(this).addClass("selected").siblings().removeClass();
    $(".tab_con > .tc").hide().eq($(".tab_nav span").index(this)).show();
  });
  
  //加载更多
  function loadMore(resultData){
    for(var i=0;i<length;i++){
      var listBox= '<li class="listBox">'+
                    '<h4>封神演义 第123回</h4>'+
                    '<div class="time">2014-10-23</div>'+
                    '<p class="lcp">'+
                      '<img src="imgs/sl.png" alt=""/>'+
                      '<span>1234</span>'+
                      '<img src="imgs/sc.png" alt="" class="sc"/>'+
                      '<span class="contentT">3333</span>'+
                    '</p>'+
                  '</li>';
      $(".ulBox").append(listBox);           
    }
  }
  
  
  //添加滚动条事件
  window.onscroll=function(){
    //当滚动到最底部以上60像素时,加载新内容  
    if($(document).height() - $(this).scrollTop() - $(this).height()<60){
      $.ajax({
        type: "POST",
        url:rootPath+"common/jsonp.do",
        dataType: "json",
        data:{
          "RemoteUrl":"http://www.wotingfm.com:808/wt/getContentInfo.do",
          "IMEI":"3279A27149B24719991812E6ADBA5583",
          "PCDType":"3",
          "ContentId":$(".PicBox").attr("contentId"),
          "ResultType":"0",
          "PageType":"0",
          "Page":page,
          "PageSize":"20"
        },
        success: function(resultData){
          if (resultData.ReturnType=="1001"){
            loadMore(resultData);
            if(resultData.NextPage=="true"){
              page++;
            }
          }else{
            return;
          }
        },
        error: function(jqXHR){  
          alert("发生错误" + jqXHR.status);
        }     
      });
    }
  }
  
  
  
});
