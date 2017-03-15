/*清空*/
function clear(){
  $(".ri_top3_con,.totalPage").html("");
  $(".toPage").val("");
  $(".page").find("span").removeClass("disabled");
}
/*播放过程中切换节目进行复位*/
function reset(){
  $(".playerBtn").removeClass("playing").attr("src","img/play.png");
  $('.player_circle')[0].style.left ='0px';
  $(".player_playbar").css("width","0px");
  $(".playerBtn").click();
}
/*销毁obj对象的key-value*/
function destroy(obj){
  for(var key in obj){//清空对象
    delete obj[key];
  }
}
/*页面重置时播放器面板的样式也随着变化*/
window.onresize=function(){
  var w=$(".content").css("width");
  $(".audioIframe").css("width",w);
}

/*播放器面板出现与隐藏*/
$(document).on("click",".locker",function(){
  if($(this).children(".locker_btn").hasClass("locked")){//此时隐藏,点击之后显示
    $(".audioIframe").show();
    $(".locker_btn").css({"background-image":"url(http://sss.qingting.fm/www/images/locker-locked-hover@2x.png)"});
    $(this).css({"top":"-10px","transition" :"all 0.1s ease 0s"});
    $(this).children(".locker_btn").removeClass("locked");
  }else{//此时显示，点击之后隐藏
    $(".audioIframe").hide();
    $(".locker_btn").css({"background-image":"url(http://sss.qingting.fm/www/images/locker-unlocked-hover@2x.png)"});
    $(this).css({"top":"-40px","transition" :"all 0.1s ease 0s"});
    $(this).children(".locker_btn").addClass("locked");
  }
});
/*点击播放器面板上的播放按钮*/
$(document).on("click",".playerBtn",function(){
  if($(this).hasClass("playing")){//正在播放
      $(this).removeClass("playing").attr("src","img/play.png");
      $(".audio")[0].pause();
  }else{//正在暂停
      $(this).addClass("playing").attr("src","img/pause.png");
      $(".audio")[0].play();
  }
});
/*获取节目时长*/
function getTime(){
  setTimeout(function (){
    var duration = $(".audio")[0].duration;
    if(isNaN(duration)){//检查参数是否是非数字
      getTime();
    }else{
      var longtime=$(".audio")[0].duration;
      $(".sound_duration").text(formatTime(longtime));
    }
  }, 10);
}
/*秒转时分秒格式--播放时刻*/
function formatTime(longTime){
  var time=parseFloat(longTime);
  if(time!=null && time !=""){
    if(time<60){
      var s=parseInt(time);
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
