var rootPath=getRootPath();
var overChannel=false;//鼠标是否放在栏目上
var type=2;//type=1点击多选,type=2未点击多选
$(document).on("click",".more1",function(){//点击更多
  if($("#channel").children(".attrValues").children("ul").hasClass("h40")){
    $("#channel").children(".attrValues").children("ul").removeClass("h40");
    $(this).children("span").text("收起");
  }else{
    $("#channel").children(".attrValues").children("ul").addClass("h40");
    $(this).children("span").text("更多");
  }
});
$(document).on("click",".ri_top_li3",function(){//点击收起筛选
  if($(this).children(".filter").text()=="收起筛选"){
    $(this).children("img").attr({"src":"img/filter2.png"});
    $(this).children(".filter").text("展开筛选");
    $(".ri_top2").hide();
  }else{
    $(this).children("img").attr({"src":"img/filter1.png"});
    $(this).children(".filter").text("收起筛选");
    $(".ri_top2").show();
  }
});
$(document).on("click",".more3",function(){//点击多选
  type=1;
  $(this).siblings(".attrValues").children(".av_ul").children(".trig_item").children(".check_cate").show();
  $(this).hide();
  $(this).siblings(".btns").show();
});
$(document).on("click",".trig_item",function(){//选中某一项
  var pId=$(this).parents(".attr").attr("id");
  var id=$(this).attr("id");
  var pTitle='';
  if(pId=="channel"){
    pTitle="所属栏目：";
  }
  if(pId=="source"){
    pTitle="来源：";
  }
  if(type=="1"){//点击多选后选中某一项
    if($(this).children(".check_cate").css("background-position")=="-62px -414px"){
      $(this).children(".check_cate").css({"background-position":"-41px -414px"});
      $(this).removeAttr("selected");
    }else{
      $(this).children(".check_cate").css({"background-position":"-62px -414px"});
      $(this).attr({"selected":"selected"});
    }
  }
  if(type=="2"){//没有点击多选直接选中某一项
    $(this).attr({"selected":"selected"});
    var newFilter='<li class="cate" pId='+pId+' id='+id+'>'+
                      '<span class="cate_desc"><span class="cate_desc_title">'+pTitle+'</span>'+$(this).children(".ss1").text()+'</span>'+
                      '<span class="cate_img">×</span>'+
                    '</li>';
    $(".new_cate").append(newFilter);
    $("#"+pId).children(".attrValues").children(".av_ul").children(".trig_item").children(".check_cate").css({"background-position":"-41px -414px"}).hide();
    $("#"+pId).children(".attrValues").children(".av_ul").children(".trig_item").removeAttr("selected");
    $("#"+pId).hide();
    var sl=$(".ri_top2").children(".attr").length;
    var i=0;
    $(".ri_top2 .attr").each(function(){
      if($(this).is(":hidden")){
        i++;
      }
    })
    console.log("隐藏的数目=="+i);
    if(i==sl){
      $(".ri_top2").removeClass("border1").addClass("border2"); 
      $(".ri_top_li4").hide();
    }else{
      $(".ri_top_li4").show();
    }
    if($(".all").is(':hidden')) $(".all").show();
  }
});
$(document).on("click",".trig_item_li",function(){
  var pname=$(this).parent(".chnels").attr("data_name");
  var id=$(this).attr("id")
  var newFilter='<li class="cate" pId="channel" id='+id+'>'+
                  '<span class="cate_desc"><span class="cate_desc_title">所属栏目&gt;'+pname+'</span>'+"&gt;"+$(this).children(".ss1").text()+'</span>'+
                  '<span class="cate_img">×</span>'+
                '</li>';
  $(".new_cate").append(newFilter);
  $("#channel").children(".attrValues").children(".av_ul").children(".trig_item").children(".check_cate").css({"background-position":"-41px -414px"}).hide();
  $("#channel").children(".attrValues").children(".av_ul").children(".trig_item").removeAttr("selected");
  $("#channel").hide();
  if($(".all").is(':hidden')) $(".all").show();
});
$(document).on("click",".btns_sub",function(){//点击多选之后的确定
  type=2;
  var pId=$(this).parents(".attr").attr("id");
  var id=$(this).attr("id");
  var pTitle='';
  if(pId=="channel"){
    pTitle="所属栏目：";
  }
  if(pId=="source"){
    pTitle="来源：";
  }
  var str=" ";
  var ids=" ";
  $(this).parent().siblings(".attrValues").children(".av_ul").children(".trig_item").each(function(){
    if(typeof($(this).attr("selected"))!="undefined"){
      var txt=$(this).children(".ss1").text();
      var id=$(this).attr("id");
      if(str==" "){
        str=txt;
      }else{
        str+=","+txt;
      }
      if(ids==" "){
        ids=id;
      }else{
        ids+=","+id;
      }
    }
  })
  console.log(str,ids);
  var newFilter='<li class="cate" pId='+pId+' id='+ids+'>'+
                  '<span class="cate_desc"><span class="cate_desc_title">'+pTitle+'</span>'+str+'</span>'+
                  '<span class="cate_img">×</span>'+
                '</li>';
  $(".new_cate").append(newFilter);
  $("#"+pId).children(".attrValues").children(".av_ul").children(".trig_item").children(".check_cate").css({"background-position":"-41px -414px"}).hide();
  $("#"+pId).children(".attrValues").children(".av_ul").children(".trig_item").removeAttr("selected");
  $("#"+pId).hide();
  var sl=$(".ri_top2").children(".attr").length;
  var i=0;
  $(".ri_top2 .attr").each(function(){
    if($(this).is(":hidden")){
      i++;
    }
  })
  console.log("隐藏的数目=="+i);
  if(i==sl){
    $(".ri_top2").removeClass("border1").addClass("border2"); 
    $(".ri_top_li4").hide();
  }else{
    $(".ri_top_li4").show();
  }
  if($(".all").is(':hidden')) $(".all").show();
});
$(document).on("click",".btns_can",function(){//点击多选之后的取消
  var pId=$(this).parents(".attr").attr("id"); 
  $("#"+pId).children(".attrValues").children(".av_ul").children(".trig_item").children(".check_cate").css({"background-position":"-41px -414px"}).hide();
  $("#"+pId).children(".attrValues").children(".av_ul").children(".trig_item").removeAttr("selected");
  $(this).parent(".btns").hide();
  $(this).parent(".btns").siblings(".check_more").show();
});
$(document).on("click",".cate_img",function(){//所有筛选条件里面的取消
  $(this).parent(".cate").remove();
  var pId=$(this).parent(".cate").attr("pId");
  $("#"+pId).children(".btns").hide();
  $("#"+pId).children(".check_more").show();
  if($(".ri_top2").hasClass("border2")){
    $(".ri_top2").removeClass("border2").addClass("border1");
    $(".ri_top_li4").show();
  }
  $("#"+pId).show();
  if($(".all").children(".new_cate").children("li").length<=0){
    $(".all").hide();
  }
});
$(document).on("click",".all_check",function(){//点击全选
  if($(this).hasClass("checkbox1")){
    $(".checkbox_img").attr({"src":"img/checkbox2.png"});
    $(this).removeClass("checkbox1");
    $(".ri_top3_con .rtc_listBox").each(function(){
      $(this).children(".rtcl_img_check").removeClass("checkbox1");
    }); 
  }else{
    $(".checkbox_img").attr({"src":"img/checkbox1.png"});
    $(this).addClass("checkbox1");
    $(".ri_top3_con .rtc_listBox").each(function(){
      $(this).children(".rtcl_img_check").addClass("checkbox1");
    }); 
  }
});
$(document).on("click",".rtcl_img_check",function(){//点击单个勾选框
  if($(this).hasClass("checkbox1")){
    $(this).attr({"src":"img/checkbox2.png"}).removeClass("checkbox1");
  }else{
    $(this).attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
  }
});
$(window).resize(function(){//时刻监听着页面的变化
  var height=$(".containers").height();
  var width1=$(".nopass_masker").width();
  var width2=$(".nopass_container").width();
  var left=(width1-width2)/2;
  if(left>=20){
    $(".nopass_container").css({"left":left+"px"});
  }else{
    return;
  }
})
$(document).on("click",".nopass_masker,.nh_span2",function(){//点击遮罩层或不通过页面上的取消按钮
  $(".checkbox_img").attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
  $(".nopass_masker,.nopass_container").hide();
  $("body").css({"overflow-x":"auto"});
});
$(document).on("mouseenter","#channel .chnel",function(){
  overChannel=true;
  var pid=$(this).attr("data_idx");
  $(".chnels").each(function(){
    if(pid==$(this).attr("data_idx")){
      $(".chnels").hide();
      $("#channel .chnel").removeClass("trig_curr");
      $("#channel .chnel[data_idx="+pid+"]").addClass("trig_curr");
      if(pid>=0&&pid<=11){
        $(this).css({"top":"39px"}).show();
      }else if(pid>=12&&pid<=23){
        $(this).css({"top":"79px"}).show();
      }else{
        $(this).css({"top":"119px"}).show();
      }
    } 
  })
});
$(document).on("mouseleave","#channel .chnel",function(){
  overChannel=false;
  setTimeout(function(){
    if(overChannel) return;
    $("#channel .chnel").removeClass("trig_curr");
    $(".chnels").css({"top":"39px"}).hide();
  },200)
});
$(document).on("mouseenter","#channel .chnels",function(){
  overChannel=true;
  var pid=$(this).attr("data_idx");
  $(".chnel").each(function(){
    if(pid==$(this).attr("data_idx")){
      $("#channel .chnel").removeClass("trig_curr");
      $(this).addClass("trig_curr");
      $("#channel .chnels").hide();
      $("#channel .chnels[data_idx="+pid+"]").show();
    }
  })
});
$(document).on("mouseleave","#channel .chnels",function(){
  overChannel=false;
  setTimeout(function(){
    if(overChannel) return;
    $("#channel .chnel").removeClass("trig_curr");
    $("#channel .chnels").hide();
  },200)
});
/*日期处理--日历插件*/
$(".input-daterange").datepicker({keyboardNavigation:!1,forceParse:!1,autoclose:!0});
//--设置当前月份
//$("input[name='StartPubTime']").val(getCurMonthFirstDay_format("yyyy-MM-dd"));
//$("input[name='EndPubTime']").val(getCurMonthLastDay_format("yyyy-MM-dd"));


/*获取栏目的筛选条件*/
var data1={};
getChannelFilters(data1);
function getChannelFilters(data1){
  $.ajax({
    type:"POST",
    url:rootPath+"CM/baseinfo/getChannelTree4View.do",
    dataType:"json",
    data:JSON.stringify(data1),
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        loadChannelFilters(resultData);//加载栏目的筛选条件
      }
    },
    error:function(jqXHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
}
function loadChannelFilters(resultData){
  for(var i=0;i<resultData.Data.children.length;i++){
    var li='<li class="trig_item chnel" data_idx='+i+' id='+resultData.Data.children[i].id+' pid='+resultData.Data.children[i].attributes.parentId+'>'+
              '<div class="check_cate"></div>'+
              '<a class="ss1" href="javascript:void(0)">'+resultData.Data.children[i].name+'</a>'+
            '</li>';
    var li_tab_ul='<ul class="tab_cont_item chnels" data_idx='+i+' data_name='+resultData.Data.children[i].name+' pid='+resultData.Data.children[i].attributes.parentId+'></ul>';
    $(".attrTabcon").append(li_tab_ul);
    $("#channel .attrValues .av_ul").append(li);
    if(resultData.Data.children[i].children){
      for(var j=0;j<resultData.Data.children[i].children.length;j++){
        var li_tab_ul_li='<li class="trig_item_li" id='+resultData.Data.children[i].children[j].id+'>'+
                            '<a class="ss1" href="javascript:void(0)">'+resultData.Data.children[i].children[j].name+'</a>'+
                          '</li>';
        $(".tab_cont_item").eq(i).append(li_tab_ul_li);
      }
    }else{
      var li_tab_ul_li='<li class="trig_item_li">'+
                          '<a class="ss1" href="javascript:void(0)" >暂无二级栏目</a>'+
                        '</li>';
      $(".tab_cont_item").eq(i).append(li_tab_ul_li);                  
    }
  }
}
/*获取来源的筛选条件*/
var data2={};
getSourceFilters(data2);
function getSourceFilters(data2){
  $.ajax({
    type:"POST",
    url:rootPath+"CM/content/getConditions.do",
    dataType:"json",
    data:JSON.stringify(data2),
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        loadSourceFilters(resultData);//加载来源的筛选条件
      }
    },
    error:function(jqXHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
}
function loadSourceFilters(resultData){
  for(var i=0;i<resultData.Source.length;i++){
    var li='<li class="trig_item" id='+resultData.Source[i].SourceId+'>'+
              '<div class="check_cate"></div>'+
              '<a class="ss1" href="javascript:void(0)">'+resultData.Source[i].SourceName+'</a>'+
            '</li>';
    $("#source .attrValues .av_ul").append(li);
  }
}
/*时间戳转日期*/
function getLocalTime(cptime){     
  cptime=new Date(parseInt(cptime)).toLocaleString('chinese',{hour12:false}).replace(/\//g, "-");  
  return cptime;
}
/*点击跳到对应的详情页*/
$(document).on("click",".rtc_listBox .rtcl_img",function(){
  var contentId=$(this).parent(".rtc_listBox").attr("contentid");
  var seqId=$(this).siblings(".sequ_name").attr("contentseqid");
  if($(this).parent(".rtc_listBox").attr("mediatype")=="wt_SeqMediaAsset"){//专辑
    $("#newIframe", parent.document).attr({"src":"zj_detail.html?contentId="+contentId});
  }else if($(this).parent(".rtc_listBox").attr("mediatype")=="wt_MediaAsset"){//节目
    $("#newIframe", parent.document).attr({"src":"jm_detail.html?contentId="+contentId+"&&seqId="+seqId});
  }else{//电台
    
  }
  $("#myIframe", parent.document).hide();
  $("#newIframe", parent.document).show();
});
