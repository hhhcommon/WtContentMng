$(function(){
  var rootPath=getRootPath();
  
  var page=2;
  var nextPage="false";
  //控制播放按钮的播与停
  var audio=$("<audio></audio>")[0];
  $(".ulBox").on("click",".playBtn",function(){
    if($(this).hasClass("play")){
      $(this).removeClass("play");
      audio.pause();
    }else{
      if($(".playBtn").hasClass("play")){
        audio.pause();
        $(".playBtn").removeClass("play");
      }
      audio.src=$(this).attr("data_src");
      //播放当前资源，并改变当前按钮状态
      $(this).addClass("play");
      audio.play();
    }
  });
  
  //详情和节目的切换
  $(".tab_nav span").on("click",function(){
    $(this).addClass("selected").siblings().removeClass();
    $(".tab_con > .tc").hide().eq($(".tab_nav span").index(this)).show();
  });
  
  //加载更多
  function loadMore(resultData){
    for(var i=0;i<length;i++){
      var listBox= '<li class="listBox playBtn" data_src="#####audioplay#####">'+
                    '<h4>#####audioname#####</h4>'+
                    '<div class="time">#####audiotime#####</div>'+
                    '<p class="lcp">'+
                      '<img src="../../templet/zj_templet/imgs/sl.png" alt=""/>'+
                      '<span>#####audioplaycount#####</span>'+
                      '<img src="../../templet/zj_templet/imgs/sc.png" alt="" class="sc"/>'+
                      '<span class="contentT">#####audioplaytime#####</span>'+
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
          "RemoteUrl":"http://www.wotingfm.com:808/wt/content/getContentInfo.do",
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
            var resultData=eval('(' + resultData + ')');
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
  
  //获取节目列表的节目时长
  $(document).find(".ulBox li").each(function(){
    var timeLong=$(this).children(".lcp").children(".contentT").text();
    $(this).children(".lcp").children(".contentT").text(formatTimeTJ(timeLong/1000));
  })

  //秒转时分秒格式--节目列表
  function formatTimeTJ(longTime){
    debugger;
    var time=parseFloat(longTime);
    if(time!=null && time !=""){
      if(time<60){
        var s=time;
        if(s<10){
          time="00'0"+s+"\"";
        }else{
          time="00'"+s+"\"";
        }
      }else if(time>60){
        var m=parseInt(time / 60);
        var s=parseInt(time %60);
        m = m >= 10 ? m : "0" + m;
        s = s >= 10 ? s : "0" + s;
        time=m+"\'"+s+"\"";
      }
    }
    return time;
  }
  
});
