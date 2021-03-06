$(function(){
  var swiper = new Swiper('#swiper-container', {//swiper滑页
    autoHeight: true,
    onlyExternal :true
  });
  
  var rootPath=getRootPath();
  var indexs=0;//获取indexs属性，对应星期几
  var _index=0;//索引值，和星期几配合使用的weekContent
  var isNowTime=true;
  var $this=null;
  var requestTimes=0;
//var onet=0;//一天的时间戳
//var nowt=0;//今天的时间戳
//var week =0;//今天是周几
  var week=new Date().getDay();//今天是周几（1-6周一到周六，0周日）
  var onet=24*3600*1000;//一天的时间戳
  var nowt=new Date().getTime();//今天的时间戳
  //控制播放的节目的顺序号
  var listNum=0;
  //获取播放元素容器
  var audio=$("audio")[0];
  //打开链接时，自动播放对应声音资源
  $("#raAudio")[0].play();
  $(".playControl").addClass("play");
  
  //资源准备就绪后，获取声音长度，否则NaN
  $("audio").on("canplay",function(){
    if(typeof($(this).attr("id"))!="undefined"){
      var st=$("#raAudio")[0].duration;
      $(".fullTime").text(formatTime(Math.round(st)));
      $(audio).removeAttr("id");
    }
  })
    
  //播放控制面板的播放控制
  $(".playControl").on("click",function(){
    if($("#raAudio")[0]){
      if($(".playControl").hasClass("play")){//处于播放状态
        $(".playControl").removeClass("play");
        $("#raAudio")[0].pause();
      }else{
        $(".playControl").addClass("play");
        $("#raAudio")[0].play();
      }
    }else{
      if($(".playControl").hasClass("play")){//处于播放状态
        $(document).find(".state").each(function(i,val){
          if($(this).hasClass("playGif")){
            $(val).removeClass("playGif").addClass("playPng");
            return false;
          }
        });
        $(".playControl").removeClass("play");
        audio.pause();
      }else{
        $(document).find(".state").each(function(i,val){
          if($(this).hasClass("playPng")){
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
    if($(this).index()=="0"){//上一个按钮变成灰色
      $(".previous").css({"background":"url(../../templet/dt_templet/imgs/audio_play.png) no-repeat 0 -162px","cursor":"default"});
    }else if($(this).index()==len){//下一个按钮变成灰色
      $(".next").css({"background":"url(../../templet/dt_templet/imgs/audio_play.png) no-repeat 0 -192px","cursor":"default"});
    }else{
      $(".previous").css({"background":"url(../../templet/dt_templet/imgs/wt_play_left.png)","backgroundSize":"100% 100%"});
      $(".next").css({"background":"url(../../templet/dt_templet/imgs/wt_play_right.png)","backgroundSize":"100% 100%"});
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
      if($(this).hasClass("playGif")){
        if(i<=0){
          alert("当前已经是第一首了");
          return false;
        }else if(i==1){
          $(".previous").css({"background":"url(../../templet/dt_templet/imgs/audio_play.png) no-repeat 0 -162px","cursor":"default"});
        }
        if(i<=$(".listBox").length-1){
          $(".next").css({"background":"url(../../templet/dt_templet/imgs/wt_play_right.png)","backgroundSize":"100% 100%"});
        }
        listNum=i-1;
        if(listNum>=0){
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
          var contentId=$(".palyCtrlBox").children("h4").attr("contentId");
          comment(contentId);//加载评论列表
        }
      }
    });
  });

  //点击下一首
  var isList=false;
  $(".next").on("click",function(){
    if(isList){
      $(document).find(".state").each(function(i,val){
        if($(this).hasClass("playGif")){
          //判断是否为最后一个
          //如果有正在播放的列表，那么播放下一条，如果没有，则播放第一条
          if(i>=$(".listBox").length-1){
            alert("当前已经是最后一个节目了");
            return false;
          }
          if(i==$(".listBox").length-2){
            $(".next").css({"background":"url(../../templet/dt_templet/imgs/audio_play.png) no-repeat 0 -192px","cursor":"default"});
          }
          if(i>=0){
            $(".previous").css({"background":"url(../../templet/dt_templet/imgs/wt_play_left.png)","backgroundSize":"100% 100%"});
          }
          $(".listBox").children(".listCon").children(".lcpp").children(".state").removeClass("playPng").removeClass("playGif");
          $('.listBox').children(".listCon").children(".span").css({"color":"#f60"});
          listNum=i+1;
        }
      })
    }else{
      $(audio).removeAttr("id");
      isList=true;
      listNum=0;
    }
    if(listNum<=$(".listBox").length){
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
      var contentId=$(".palyCtrlBox").children("h4").attr("contentId");
      comment(contentId);//加载评论列表
    }
  });
  
  //请求推荐资源列表
  var searchStr=$(".palyCtrlBox").children("h4").text();
  var _data={
        "RemoteUrl":"http://www.wotingfm.com:808/wt/searchByText.do",
        "IMEI":"3279A27149B24719991812E6ADBA5583",
        "PCDType":"3",
        "SearchStr":"北京新闻广播",
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
        loadRecomList(resultData);
      }else{
        return;
      }
    },
    error: function(jqXHR){
      alert("发生错误" + jqXHR.status);
    }
  });
  
  //请求查看评论
  var contentId=eval('('+$("audio").attr("raOpenApp").split("=")[1]+')').ContentId;
  comment(contentId);
  function comment(contentId){
    var data={
          "RemoteUrl":"http://www.wotingfm.com:808/wt/discuss/article/getList.do",
          "IMEI":"3279A27149B24719991812E6ADBA5583",
          "PCDType":"3",
          "ContentId":contentId,
          "MediaType":"AUDIO",
          "Page":"1",
          "PageSize":"20"
    };
    $.ajax({
      url: rootPath+"common/jsonp.do",
      type:"POST",
      dataType:"json",
      data:JSON.stringify(data),
      success: function(resultData) {
        var resultData=eval('(' + resultData.Data + ')');
        if (resultData.ReturnType=="1001"){
          loadCommentList(resultData);
        }else{
          $(".comment").html("");
          $(".comment").append("<li class='noComment'>暂无评论</li>");
          $(".noComment").css({"height":$(".ulBox").height()});
        }
      },
      error: function(jqXHR){
        alert("发生错误" + jqXHR.status);
      }
    });
  }
  
  //打开APP或下载
  $(".downLoad,.like").click(function(){
    window.location=$("#raAudio").attr("raOpenApp");
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
    alert(11);
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
    $('.radioList ul').eq(index).show().siblings().hide();
  })
  
  //点击节目单
  selected();//点击默认选中节目单
  $(".border").click(function(){
    swiper.slideNext();
    selected();//点击默认选中节目单
    var height=$(".jmd").height();
    $(".swiper-wrapper").css({"height":height+"px"});
  })
  //点击返回按钮
  $(".jmd_head").click(function(){
    swiper.slidePrev();
    $(".swiper-wrapper").css({"height":$(".container").height()+"px"});
    selected();
  })
  //节目单页面出现的时候默认选中当前的日期
  function selected(){
    $('.jmd_nav .week').each(function(){//初始化节目单，默认显示当前是周几
      indexs=$(this).attr("indexs");
      _index=$(this).index();
      $this=$(this);
      if(indexs==week){
        requestTimes=nowt;
        isNowTime=true;
        console.log($this,_index,requestTimes,indexs,isNowTime);
        getJMD($this,_index,requestTimes,indexs,isNowTime);//得到当天的节目单
      }
    }) 
  }
  //节目单时间的切换
  $('.jmd_nav .week').click(function(){
    indexs=$(this).attr("indexs");
    _index=$(this).index();
    $this=$(this);
    if(indexs!=0){//选中的不是周日
      if(indexs<week){
        requestTimes=nowt-onet*(week-indexs);//当前选中日期的时间戳
        isNowTime=false;
      }else if(indexs>week){
        requestTimes=nowt+onet*(indexs-week);//当前选中日期的时间戳
        isNowTime=false;
      }else{
        requestTimes=nowt;//当前选中日期的时间戳
        isNowTime=true;
      }
    }else{//选中的是周日
      if(indexs!=week){
        requestTimes=nowt+onet*(indexs-week);//当前选中日期的时间戳
        isNowTime=false;
      }else{
        requestTimes=nowt;//当前选中日期的时间戳
        isNowTime=true;
      }
    }
    getJMD($this,_index,requestTimes,indexs,isNowTime);//得到当天的节目单
  });
  //请求加载节目单
  function getJMD($this,_index,requestTimes,indexs,isNowTime){
//  var contentId=$("#raAudio").attr("raOpenApp").split("=")[1].ContentId;
    var _data={
      "RemoteUrl":"http://www.wotingfm.com:808/wt/content/getBCProgramme.do",
      "IMEI":"3279A27149B24719991812E6ADBA5583",
      "PCDType":"3",
      "PageType":"0",
      "UserId":"123",
      "BcId":"cb165a78315b",
      "RequestTimes":requestTimes
    };
    $.ajax({
      url: rootPath+"common/jsonp.do",
      type:"POST",
      dataType:"json",
      data:JSON.stringify(_data),
      success: function(resultData) {
        var resultData=eval('(' + resultData.Data + ')');
        if(resultData.ReturnType=="1001"){
          $("#week"+indexs).html("");
          for(var i=0;i<resultData.ResultList[0].List.length;i++){
            if(resultData.ResultList[0].List[i].Title) title=resultData.ResultList[0].List[i].Title;
            else title="暂无标题";
            if(resultData.ResultList[0].List[i].BeginTime) bt=resultData.ResultList[0].List[i].BeginTime;
            else bt="未知";
            if(resultData.ResultList[0].List[i].EndTime) et=resultData.ResultList[0].List[i].EndTime;
            else et="未知";
            var bt=bt.substring(0,bt.lastIndexOf(":"));
            var et=et.substring(0,et.lastIndexOf(":"));
            var lib='<div class="wcListBox">'+
                      '<span class="lbc">'+title+'</span>'+
                      '<span class="lbt" bt='+bt+' et='+et+'>'+bt+'-'+et+'</span>'+
                      '<span class="lbz">直播</span>'+
                    '</div>';
            $("#week"+indexs).append(lib);
            $this.addClass('active').siblings().removeClass('active');
            $('.jmd_con .weekCont').eq(_index).show().siblings().hide();
          }
          if(isNowTime){
            $('.jmd_con .weekCont').eq(_index).children(".wcListBox").each(function(){
              var beginTime=$(this).children(".lbt").attr("bt");
              var endTime=$(this).children(".lbt").attr("et");
              var _this=$(this);
              time_range(_this,beginTime,endTime);
            })
          }
        }else{
          $("#week"+indexs).html("");
          $("#week"+indexs).append("<span class='nojmd'>暂无节目单</span>");  
        }
      },
      error: function(jqXHR){
        alert("发生错误" + jqXHR.status);
      }
    });
  }
  
  //加载评论列表
  function loadCommentList(resultData){
    $(".comment").html("");
    for(var i=0;i<resultData.AllCount;i++){
      var commentTime=resultData.DiscussList[i].Time;
      var commentList='<li class="ctList" commentId='+resultData.DiscussList[i].Id+'>'+
                        '<div class="default"></div>'+
                        '<img src="http://qingting-pic.b0.upaiyun.com/www/UpYunImage/06d4dd215d2c3bd6d305c78065015552.png" class="audioImg" alt="节目图片">'+
                        '<div class="listCon">'+
                          '<p class="lcs">'+
                            '<span class="commentName">'+resultData.DiscussList[i].UserInfo.UserName+'</span>'+
                            '<span class="commentTime"></span>'+
                          '</p>'+
                          '<div class="commentContent">'+resultData.DiscussList[i].Discuss+'</div>'+
                       '</div>'+
                      '</li>';
      $(".comment").append(commentList);
      $(".commentTime").eq(i).text(formatCommentDate(commentTime));
    }
  }
  
  //评论时间转换
  function formatCommentDate(tt){ 
    var date = new Date(parseInt(tt));
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours() + ':';
    m = date.getMinutes() + ':';
    s = date.getSeconds(); 
    return Y+M+D+h+m+s;
  } 
  
  //当前时间以后的节目都可以播
  function time_range(_this,beginTime,endTime){ 
    var strb = beginTime.split (":");  
    if(strb.length != 2){  
      return false;  
    }  
    var stre = endTime.split (":");  
    if(stre.length != 2){  
      return false;  
    }  
    var b = new Date ();  
    var e = new Date ();  
    var n = new Date ();  
    b.setHours (strb[0]);  
    b.setMinutes (strb[1]);  
    e.setHours (stre[0]);  
    e.setMinutes (stre[1]);  
    if (n.getTime () - b.getTime () > 0 && n.getTime () - e.getTime () < 0){ 
      _this.css({"color":"#000"});
      _this.children(".lbz").show();
      return true;  
    }else{  
      _this.css({"color":"#959595"});
      return false;  
    }  
  }    
});
