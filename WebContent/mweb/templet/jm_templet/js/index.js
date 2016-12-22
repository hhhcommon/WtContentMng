$(function(){
  var rootPath=getRootPath();
  
  //控制播放的节目的顺序号
  var listNum=0;
  //获取播放元素容器
  var audio=$("audio")[0];
  //打开链接时，自动播放对应声音资源
  $("#jmAudio")[0].play();
  $(".playControl").addClass("play");
  
  //资源准备就绪后，获取声音长度，否则NaN
  $("audio").on("canplay",function(){
    if(typeof($(this).attr("id"))!="undefined"){
      var st=$("#jmAudio")[0].duration;
      $(".fullTime").text(formatTime(Math.round(st)));
      $(audio).removeAttr("id");
    }
  })
    
  //播放控制面板的播放控制
  $(".playControl").on("click",function(){
    if($("#jmAudio")[0]){
      if($(".playControl").hasClass("play")){//处于播放状态
        $(".playControl").removeClass("play");
        $("#jmAudio")[0].pause();
      }else{
        $(".playControl").addClass("play");
        $("#jmAudio")[0].play();
      }
    }else{
      if($(".playControl").hasClass("play")){//处于播放状态
        $(document).find(".state").each(function(i,val){
          if(val.className!="state"){
            $(val).removeClass("playGif").addClass("playPng");
            return false;
          }
        });
        $(".playControl").removeClass("play");
        audio.pause();
      }else{
        $(document).find(".state").each(function(i,val){
          if(val.className!="state"){
            $(val).removeClass("playPng").addClass("playGif");
            return false;
          }
        });
        $(".playControl").addClass("play");
        audio.play();
      }
    } 
  });
  
  //实时获取播放时长和改变进度条进度
    audio.addEventListener("timeupdate",function(){
      if(!isNaN(this.duration)){
        //播放进度条
        var progressValue = this.currentTime/this.duration*($(".currentMusicBar").width());
        $('.currentMusicBarRound')[0].style.left = parseInt(progressValue) + 'px';
        //播放时长
        if(formatTime(Math.floor(this.currentTime))!=0){
          $(".playTime").text(formatTime(Math.floor(this.currentTime)));
        }
      };
    },false);
  
  //推荐声音列表的播放控制
  $(".ulBox").on("click",".listBox",function(){
    var len=$(".ulBox .listBox").size()-1;
    //声音列表变化及声音的控制
    if($(this).children(".listCon").children(".lcpp").children(".state").hasClass("playGif")){//正在播放
      audioPause($(this),audio);
    }else if($(this).children(".listCon").children(".lcpp").children(".state").hasClass("playPng")){//正在暂停
      audioPlay($(this),audio);
    }else{
      $(document).find(".state").each(function(){//还未点击过，最原始的状态
        $(this).removeClass("playPng").removeClass("playGif");
        audio.pause();
        $('.listBox').children(".listCon").children(".span").css({"color":"#f60"});
      });
      $(this).children(".listCon").children(".span").css({"color":"#ffa364"});
      $(this).children(".listCon").children(".lcpp").children(".state").removeClass("playPng").addClass("playGif");
      $(".palyCtrlBox").children("h4").text($(this).children(".listCon").children(".span").text());
      $(".palyCtrlBox").children("h4").attr({"contentId":$(this).attr("contentId")});
      audio.src=$(this).attr("data_src");
      audio.play();
    }
    //从推荐列表选择声音时，播放面板的控制变化
    if($(this).children(".listCon").children(".lcpp").children(".state").hasClass("playGif")){
      $(".playControl").addClass("play");
    }else{
      $(".playControl").removeClass("play");
    }
    if($(this).index()=="0"){//点击上一个
      $(".previous").css({"background":"url(../../templet/jm_templet/imgs/audio_play.png) no-repeat 0 -162px","cursor":"default"});
    }else if($(this).index()==len){//点击下一个
      $(".next").css({"background":"url(../../templet/jm_templet/imgs/audio_play.png) no-repeat 0 -192px","cursor":"default"});
    }else{
      $(".previous").css({"background":"url(../../templet/jm_templet/imgs/wt_play_left.png)","backgroundSize":"100% 100%"});
      $(".next").css({"background":"url(../../templet/jm_templet/imgs/wt_play_right.png)","backgroundSize":"100% 100%"});
    }
    //从推荐列表选择声音时，节目详情变化
    $(".detail").children(".dp1").children(".dpspan").text($(this).attr("dz"));
    $(".detail").children(".dp2").children(".dpspan").text($(this).attr("ds"));
    $(".detail").children(".dp3").children(".dpspan").text($(this).attr("dp"));
    $(".detail").children(".dp4").children(".dpspan").text($(this).children(".dn").text());
    var tt=$(this).children(".listCon").children(".lcp").children(".contentT").attr("fullTime");
    $(".fullTime").text(tt);
    $(".playTime").text("00:00");
    $('.currentMusicBarRound')[0].style.left="0px";
    var src=$(this).children(".audioImg").attr("src");
    $(".boximg").css({"background-image":"url("+src+")"});
  });
  
  //点击上一个
  $(".previous").on("click",function(){
    $(document).find(".state").each(function(i,val){
      if(val.className!="state"){
        if(i<=0){
          return false;
        }else if(i==1){
          $(".previous").css({"background":"url(../../templet/jm_templet/imgs/audio_play.png) no-repeat 0 -162px","cursor":"default"});
        }
        if(i<=$(".listBox").length-1){
          $(".next").css({"background":"url(../../templet/jm_templet/imgs/wt_play_right.png)","backgroundSize":"100% 100%"});
        }
        listNum=i-1;
        $(".listBox").children(".listCon").children(".lcpp").children(".state").removeClass("playPng").removeClass("playGif");
        $('.listBox').children(".listCon").children(".span").css({"color":"#f60"});
        $(".palyCtrlBox").children("h4").text($(".listBox").eq(listNum).children(".listCon").children(".span").text());
        $(".listBox").eq(listNum).children(".listCon").children(".span").css({"color":"#ffa364"});
        $(".playTime").text("00:00");
        $('.currentMusicBarRound')[0].style.left="0px";
        $(".fullTime").text($(".listBox").eq(listNum).children(".listCon").children(".lcp").children(".contentT").attr("fullTime"));
        $(".detail").children(".dp1").children(".dpspan").text($(".listBox").eq(listNum).attr("dz"));
        $(".detail").children(".dp2").children(".dpspan").text($(".listBox").eq(listNum).attr("ds"));
        $(".detail").children(".dp3").children(".dpspan").text($(".listBox").eq(listNum).attr("dp"));
        $(".detail").children(".dp4").children(".dpspan").text($(".listBox").eq(listNum).children(".dn").text());
        var src=$(".listBox").eq(listNum).children(".audioImg").attr("src");
        $(".box").css({"background-image":"url("+src+")"});
        audio.src=$(".listBox").eq(listNum).attr("data_src");
        audioPlay($(".listBox").eq(listNum),audio,true);
        $(".playControl").addClass("play");
        return false;
      }
    });
  });

  //点击下一首
  var isList=false;
  $(".next").on("click",function(){
    if(isList){
      $(document).find(".state").each(function(i,val){
        if(val.className!="state"){
          //判断是否为最后一个
          //如果有正在播放的列表，那么播放下一条，如果没有，则播放第一条
          if(i>=$(".listBox").length-1){
            return false;
          }
          if(i==$(".listBox").length-2){
            $(".next").css({"background":"url(../../templet/jm_templet/imgs/audio_play.png) no-repeat 0 -192px","cursor":"default"});
          }
          if(i>=0){
            $(".previous").css({"background":"url(../../templet/jm_templet/imgs/wt_play_left.png)","backgroundSize":"100% 100%"});
          }
          $(".listBox").children(".listCon").children(".lcpp").children(".state").removeClass("playPng").removeClass("playGif");
          $('.listBox').children(".listCon").children(".span").css({"color":"#f60"});
          listNum=i+1;
          return false;
        }
      })
    }else{
      $(audio).removeAttr("id");
      isList=true;
    }
    $(".detail").children(".dp1").children(".dpspan").text($(".listBox").eq(listNum).attr("dz"));
    $(".detail").children(".dp2").children(".dpspan").text($(".listBox").eq(listNum).attr("ds"));
    $(".detail").children(".dp3").children(".dpspan").text($(".listBox").eq(listNum).attr("dp"));
    $(".detail").children(".dp4").children(".dpspan").text($(".listBox").eq(listNum).children(".dn").text());
    $(".palyCtrlBox").children("h4").text($(".listBox").eq(listNum).children(".listCon").children(".span").text());
    $(".playTime").text("00:00");
    $('.currentMusicBarRound')[0].style.left="0px";
    $(".fullTime").text($(".listBox").eq(listNum).children(".listCon").children(".lcp").children(".contentT").attr("fullTime"));
    var src=$(".listBox").eq(listNum).children(".audioImg").attr("src");
    $(".box").css({"background-image":"url("+src+")"});
    audio.src=$(".listBox").eq(listNum).attr("data_src");
    audioPlay($(".listBox").eq(listNum),audio,true);
    $(".playControl").addClass("play");
  });
  
  //请求推荐资源列表
  var searchStr=$(".palyCtrlBox").children("h4").text();
  var _data={
        "RemoteUrl":"http://www.wotingfm.com:808/wt/searchByText.do",
        "IMEI":"3279A27149B24719991812E6ADBA5583",
        "PCDType":"3",
        "SearchStr":searchStr,
        "ResultType":"0",
        "PageType":"0",
        "Page":"1",
        "PageSize":"20"
  };
  $.ajax({
    url: rootPath+"common/jsonp.do",
    type:"POST",
    dataType:"json",
    data:JSON.stringify(_data),
    success: function(resultData) {
      var resultData=eval('(' + resultData.Data + ')');
      if (resultData.ReturnType=="1001"){
        console.log(resultData);
        loadRecomList(resultData);
      }else{
        return;
      }
    },
    error: function(jqXHR){
      alert("发生错误" + jqXHR.status);
    }
  });
  
  //打开APP或下载
  $(".downLoad,.like").click(function(){
    window.location=$("#jmAudio").attr("jmOpenApp");
    window.setTimeout(function () {
      window.location.href= "http://www.wotingfm.com/download/WoTing.apk";
    },2000);
  });
  
  function audioPause(obj,audio){
    obj.children(".listCon").children(".lcpp").children(".state").removeClass("playGif").addClass("playPng");
    audio.pause();
  }
  function audioPlay(obj,audio,listPlay){
    if(listPlay){
      obj.children(".listCon").children(".lcpp").children(".state").removeClass("playPng").addClass("playGif");
    }else{
      obj.children(".listCon").children(".lcpp").children(".state").removeClass("playPng").addClass("playGif");
    }
    audio.play();
  }
  
  //秒转时分秒格式--播放时刻
  function formatTime(longTime){
    var time=parseFloat(longTime);
    if(time!=null && time !=""){
      if(time<60){
        var s=time;
        if(s<10){
          time="00:0"+s;
        }else{
          time="00:"+s;
        }
      }else if(time>60 && time<3600){
        var m=parseInt(time / 60);
        var s=parseInt(time %60);
        m = m >= 10 ? m : "0" + m;
        s = s >= 10 ? s : "0" + s;
        time=m+":"+s;
      }else if(time>=3600 && time<86400){
        var h=parseInt(time / 3600);
        var m=parseInt(time % 3600 /60);
        var s=parseInt(time %3600 %60 %60);
        m = m >= 10 ? m : "0" + m;
        s = s >= 10 ? s : "0" + s;
        time=h+":"+m+":"+s;
      }
    }
    return time;
  }
  
  //秒转时分秒格式--推荐列表
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
  
  //创建相关推荐资源列表
  function loadRecomList(resultData){
    for(var i=0;i<resultData.ResultList.AllCount;i++){
      var detail={};
      if(resultData.ResultList.List[i].ContentTimes){
        var contentTime=parseInt(resultData.ResultList.List[i].ContentTimes/1000);
      }else{
        var contentTime="0";
      }
      if(resultData.ResultList.List[i].zhubo) detail.zhubo=resultData.ResultList.List[i].zhubo;
      else detail.zhubo="未知";
      if(resultData.ResultList.List[i].SeqInfo) detail.seqInfo=resultData.ResultList.List[i].SeqInfo.ContentName;
      else detail.seqInfo="未知";  
      if(resultData.ResultList.List[i].ContentPub) detail.contentPub=resultData.ResultList.List[i].ContentPub;
      else detail.contentPub="未知"; 
      if(resultData.ResultList.List[i].ContentDescn) detail.contentDescn=resultData.ResultList.List[i].ContentDescn;
      else detail.contentDescn="暂无描述";
      if(resultData.ResultList.List[i].PlayCount!=null) detail.playCount=resultData.ResultList.List[i].PlayCount;
      else detail.playCount="0";
      if(resultData.ResultList.List[i].ContentImg) detail.contentImg=resultData.ResultList.List[i].ContentImg;
      else detail.contentImg="暂无图片";
      if(resultData.ResultList.List[i].ContentName) detail.contentName=resultData.ResultList.List[i].ContentName;
      else detail.contentName="暂无图片";
      var newListBox= '<li contentId='+resultData.ResultList.List[i].ContentId+' data_src='+resultData.ResultList.List[i].ContentPlay+' dz='+detail.zhubo+' ds='+detail.seqInfo+' dp='+detail.contentPub+' class="listBox">'+
                        '<div class="default"></div>'+
                        '<div class="dn">'+detail.contentDescn+'</div>'+
                        '<img src='+detail.contentImg+' class="audioImg" alt="节目图片"/>'+
                        '<div class="listCon">'+
                          '<span class="span">'+detail.contentName+'</span>'+
                          '<p class="lcp lcpp">'+
                            '<img src="../../templet/jm_templet/imgs/zj.png" alt="" />'+
                            '<span>'+detail.contentPub+'</span>'+
                           ' <span alt="" class="state"/><span>'+
                          '</p>'+
                          '<p class="lcp">'+
                            '<img src="../../templet/jm_templet/imgs/sl.png" alt="" />'+
                            '<span>'+detail.playCount+'</span>'+
                           '<img src="../../templet/jm_templet/imgs/sc.png" alt="" class="sc"/>'+
                           '<span class="contentT" ></span>'+
                          '</p>'+
                        '</div>'+
                      '</li>';
      $(".ulBox").append(newListBox);
      $(".contentT").eq(i).text(formatTimeTJ(Math.round(contentTime))); 
      $(".contentT").eq(i).attr({"fullTime":formatTime(Math.round(contentTime))}); 
    }
  }
  
  //推荐和评论的切换
  $(".tj .tjh4").on("click",function(){
    var index = $(this).index();
    $(this).addClass('active').siblings().removeClass('active');
    $('.audioList ul').eq(index).show().siblings().hide();
  })
});
