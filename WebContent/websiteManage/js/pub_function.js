/*s--播放相关操作*/
//页面重置时播放器面板的样式也随着变化
window.onresize=function(){
  var w=$(".content").css("width");
  $(".audioIframe").css("width",w);
}

//播放器面板出现与隐藏
$(document).on("click",".locker",function(){
  if($(this).children(".locker_btn").hasClass("locked")){//此时隐藏,点击之后显示
    $(".audioIframe").show();
    $(".locker_btn").css({"background-image":"url(http://sss.qingting.fm/www/images/locker-locked-hover@2x.png)"});
    $(".ri_top3").css("padding-bottom","140px");
    $(this).css({"top":"423px","transition" :"all 0.1s ease 0s"});
    $(this).children(".locker_btn").removeClass("locked");
  }else{//此时显示，点击之后隐藏
    $(".audioIframe").hide();
    $(".locker_btn").css({"background-image":"url(http://sss.qingting.fm/www/images/locker-unlocked-hover@2x.png)"});
    $(".ri_top3").css("padding-bottom","0px");
    $(this).css({"top":"520px","transition" :"all 0.1s ease 0s"});
    $(this).children(".locker_btn").addClass("locked");
  }
});
/*e--播放相关操作*/

/*s--销毁obj对象的key-value*/
function destroy(obj){
  for(var key in obj){//清空对象
    delete obj[key];
  }
}
/*e--销毁obj对象的key-value*/

 /*s--设置计时器右边边框的高度赋给左边*/
  var times=setInterval(setTime,100);
  function setTime(){
    $(".ztree").css({"height":$(".nav_con").height()+"px","overflow":"auto"});
  }
  /*e--设置计时器右边边框的高度赋给左边*/

/*s--勾选框相关操作*/
//点击全选
$(document).on("click",".all_check",function(){
  var ll=$(".ri_top3_con").has(".rtc_listBox").length;
  if(ll==true){
    var l=$(".ri_top3_con .rtc_listBox .rtcl_img_check").length;
    if($(this).hasClass("checkbox1")){
      $(this).attr({"src":"img/checkbox2.png"}).removeClass("checkbox1");
      $(".ri_top3_con .rtc_listBox .checkbox_img").attr({"src":"img/checkbox2.png"});
      $(".ri_top3_con .rtc_listBox").each(function(){
        $(this).children(".rtcl_img_check").removeClass("checkbox1");
      });
      $(".opetype").removeAttr("disabled").css({"color":"#fff"});
      $(".rto_pass").css({"background":"#0077c7"});
      $(".rto_nopass").css({"background":"darkred"});
      $(".jmsum").text("你已经选择了"+l+"个内容").removeClass("dis");
    }else{
      $(this).attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
      $(".ri_top3_con .rtc_listBox .checkbox_img").attr({"src":"img/checkbox1.png"});
      $(".ri_top3_con .rtc_listBox").each(function(){
        $(this).children(".rtcl_img_check").addClass("checkbox1");
      }); 
      $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
      $(".jmsum").addClass("dis");
    }
  }else{
    alert("请先选择栏目或更换其他栏目");
    return false;
  }
});

//点击单个勾选框
$(document).on("click",".rtcl_img_check",function(){
  var num=0;
  var l=$(".ri_top3_con .rtc_listBox .rtcl_img_check").length;
  if($(this).hasClass("checkbox1")){
    $(this).attr({"src":"img/checkbox2.png"}).removeClass("checkbox1");
    $(".opetype").removeAttr("disabled").css({"color":"#fff"});
    $(".rto_pass").css({"background":"#0077c7"});
    $(".rto_nopass").css({"background":"darkred"});
    $(".ri_top3_con .rtc_listBox .rtcl_img_check").each(function(){//是否选中全选
      if($(this).hasClass("checkbox1")){
        
      }else{
        num++;
      }
    });
    if(num==l) $(".all_check").removeClass("checkbox1").attr({"src":"img/checkbox2.png"});
    $(".jmsum").text("你已经选择了"+num+"个内容").removeClass("dis");
  }else{
    $(this).attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
    $(".ri_top3_con .rtc_listBox .rtcl_img_check").each(function(){//是否选中全选
      if($(this).hasClass("checkbox1")){
        
      }else{
        num++;
      }
    });
    if(num!=l) $(".all_check").addClass("checkbox1").attr({"src":"img/checkbox1.png"});
    if(num==0){
      $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
      $(".jmsum").addClass("dis");
    }else{
      $(".jmsum").text("你已经选择了"+num+"个内容").removeClass("dis");
    }
  }
});
/*e--勾选框相关操作*/

/*s--弹出页面上的勾选框相关操作*/
$(document).on("click",".nc_checkimg",function(){
  if($(this).hasClass("checkbox1")){
    $(this).attr({"src":"img/checkbox2.png"}).removeClass("checkbox1");
  }else{
    $(this).attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
  }
});
/*e--弹出页面上的勾选框相关操作*/