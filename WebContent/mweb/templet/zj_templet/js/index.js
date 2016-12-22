$(function(){
  var rootPath=getRootPath();
  
  var page=2;
  var nextPage="false";
  //控制播放按钮的播与停
//var audio=$("<audio></audio>")[0];
  $(".ulBox").on("click",".playBtn",function(){
//  if($(this).hasClass("play")){
//    $(this).removeClass("play");
//    audio.pause();
//  }else{
//    if($(".playBtn").hasClass("play")){
//      audio.pause();
//      $(".playBtn").removeClass("play");
//    }
//    audio.src=$(this).attr("data_src");
//    //播放当前资源，并改变当前按钮状态
//    $(this).addClass("play");
//    audio.play();
//  }
    
    var shareUrl=$(this).attr("share_url");
    window.location.href=shareUrl;
  });
  
  //详情和节目的切换
  $(".tab_nav span").on("click",function(){
    $(this).addClass("selected").siblings().removeClass();
    $(".tab_con > .tc").hide().eq($(".tab_nav span").index(this)).show();
  });
  
  //加载更多
  function loadMore(resultData){
    var detail={};
    for(var i=0;i<resultData.ResultList.length;i++){
      var str=resultData.ResultList[i].CTime;
      var ct=str.substring(0,str.lastIndexOf(":"));
      var timeLong=resultData.ResultList[i].ContentTimes;
      var tl=formatTimeTJ(timeLong/1000);
      if(resultData.ResultList[i].ContentPlay) detail.contentPlay=resultData.ResultList[i].ContentPlay;
      else detail.contentPlay="未知";
      if(resultData.ResultList[i].ContentShareUrl) detail.contentShareUrl=resultData.ResultList[i].ContentShareUrl;
      else detail.contentShareUrl="未知";
      if(resultData.ResultList[i].ContentName) detail.contentName=resultData.ResultList[i].ContentName;
      else detail.contentName="未知";
      if(resultData.ResultList[i].PlayCount) detail.playCount=resultData.ResultList[i].PlayCount;
      else detail.playCount="0";
      var listBox= '<li class="listBox playBtn" data_src='+detail.contentPlay+' share_url='+detail.contentShareUrl+'>'+
                    '<h4>'+detail.contentName+'</h4>'+
                    '<div class="time">'+ct+'</div>'+
                    '<p class="lcp">'+
                      '<img src="../../templet/zj_templet/imgs/sl.png" alt=""/>'+
                      '<span>'+detail.playCount+'</span>'+
                      '<img src="../../templet/zj_templet/imgs/sc.png" alt="" class="sc"/>'+
                      '<span class="contentT">'+tl+'</span>'+
                    '</p>'+
                  '</li>';
      $(".ulBox").append(listBox);           
    }
  }
  
  //添加滚动条事件
  window.onscroll=function(){
    //当滚动到最底部以上60像素时,加载新内容  
    if($(document).height() - $(this).scrollTop() - $(this).height()==0){
      var _data={
                  "ContentId":$(".PicBox").attr("contentId"),
                  "MediaType":"SEQU",
                  "Page":page
      };
      $.ajax({
        type: "POST",
        url:rootPath+"content/getZJSubPage.do",
        dataType: "json",
        data:JSON.stringify(_data),
        success: function(resultData){
          if(resultData.ReturnType=="1001"){
            loadMore(resultData);
            page++;
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
  
  //打开APP或下载
  $(".downLoad").click(function(){
    window.location=$(".PicBox").attr("zjOpenApp");
    window.setTimeout(function () {
      window.location.href= "http://www.wotingfm.com/download/WoTing.apk";
    },2000);
  });
  
});
