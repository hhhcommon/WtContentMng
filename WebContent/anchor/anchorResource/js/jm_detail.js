$(function(){
  var href=$("#myIframe", parent.document).attr("src");
  var contentId=href.split('?')[1].split("=")[1];
  var rootPath=getRootPath();
  
  //获取用户的id
  var userId=$(".login_user span",parent.document).attr("userid");
  var jmdetail={"MobileClass":"Chrome",
                "UserId":userId,
                "PCDType":"3",
                "ContentId":contentId
              };
  getJmDetail(jmdetail);//得到节目信息
  function getJmDetail(data){
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/getMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(data),
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          fillJmDetail(resultData);//填充节目数据
        }else{
          alert("得到节目信息失败："+resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("得到节目信息发生错误："+ jqXHR.status);
      }
    });
  }
  
  //填充节目数据
  function fillJmDetail(resultData){
    $(".dcl_pic h6").html(resultData.Result.ContentName);
    $(".phone_pic1").attr("src",resultData.Result.ContentImg);
    $(".dcr_jmcontent1 img").attr("src",resultData.Result.ContentImg);
    $(".dcr_jmcontent2 h3").html(resultData.Result.ContentName);
    $(".czj").html("<li class='czj1 fl'>《"+resultData.Result.ContentSeqName+"》</li>");
    if(resultData.Result.ContentKeyWords){
      if(resultData.Result.ContentKeyWords[i]){
        for(var i=0;i<resultData.Result.ContentKeyWords.length;i++){
          var newli='<li class="newli"><span class="newspan">'+resultData.Result.ContentKeyWords[i].TagName+'</span></li>';
          $(".tags").append(newli);
        }
      }else{
        var newli='<div>暂无</div>';
        $(".tg1").append(newli);
      }
    }
    $(".cctimes").html(resultData.Result.CTime);
    $(".dcr_jmdesc .dcr_jmdesc_p1").html(resultData.Result.ContentDesc?resultData.Result.ContentDesc:"暂无");
    $("#audio").attr({"src":resultData.Result.ContentPlay});
    getTime();//调用加载数据
  }
  
  //点击节目详情页播放按钮
  $(document).on("click",".dctb1",function(){
    if(audio.paused){
      audio.play();
      $(".dctb1").attr({"src":"../anchorResource/img/act7.png"});
    }else{
      audio.pause();
      $(".dctb1").attr({"src":"../anchorResource/img/act6.png"});
    }
  });
  audio.addEventListener("timeupdate",function(){//当目前的播放位置已更改时
    if(Math.round(audio.currentTime)=="0"){
      $(document).find(".dctb2").text("00:00");
    }else{
      $(document).find(".dctb2").text(timeDispose(Math.round(audio.currentTime)));
    }
    var line1_length=$(".line1").width();
    var maxTime=$("#audio")[0].duration;
    var line2_length=parseInt(audio.currentTime/maxTime*line1_length);
    $(".line2").css("width",line2_length +"px");
    $(".circle").css("left",line2_length+100+"px");
    if(line1_length==line2_length){
      audio.pause();
      $(".dctb1").attr({"src":"../anchorResource/img/act6.png"});
    }
  });
    
  //获取音频文件的时间 兼容浏览器
  function getTime(){
    setTimeout(function (){
      var duration = $("#audio")[0].duration;
      if(isNaN(duration)){//检查参数是否是非数字
        getTime();
      }else{
        $(document).find(".dctb3").text(timeDispose(Math.round(duration)));
        $(document).find(".dctb2").text("00:00");
      }
    }, 10);
  }
  //对音频时间的处理,秒转时分秒格式
  function timeDispose(longTime){
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
  
  //点击标题的专辑管理--进入专辑管理页面
  $(document).on("click",".detail_title2",function(){
    $("#myIframe", parent.document).attr({"src":"jmANDzj/manage_jm.html"});
  })
});