var rootPath=getRootPath();
var type=2;//type=1点击多选,type=2未点击多选

//点击收起筛选
$(document).on("click",".ri_top_li3",function(){
  if($(this).children(".filter").text()=="收起筛选"){
    $(this).children("img").attr({"src":"../websiteManageResource/img/filter2.png"});
    $(this).children(".filter").text("展开筛选");
    $(".ri_top2").hide();
  }else{
    $(this).children("img").attr({"src":"../websiteManageResource/img/filter1.png"});
    $(this).children(".filter").text("收起筛选");
    $(".ri_top2").show();
  }
});

//点击多选
$(document).on("click",".more3",function(){
  type=1;
  $(this).siblings(".attrValues").children(".av_ul").children(".trig_item").children(".check_cate").show();
  $(this).hide();
  $(this).siblings(".btns").show();
});

//选中某一中筛选条件
$(document).on("click",".trig_item",function(){//选中某一项
  var pId=$(this).parents(".attr").attr("id");
  var id=$(this).attr("id");
  var pTitle='';
  if(pId=="source"){
    pTitle="来源：";
  }
  if(pId=="type"){
    pTitle="类型：";
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
    if($(".all").is(':hidden')) $(".all").show();
  }
});
$(document).on("click",".btns_sub",function(){//点击多选之后的确定
  type=2;
  var pId=$(this).parents(".attr").attr("id");
  var id=$(this).attr("id");
  var pTitle='';
  if(pId=="source"){
    pTitle="来源：";
  }
  if(pId=="type"){
    pTitle="类型：";
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
  if($(".all").is(':hidden')) $(".all").show();
});

//点击多选之后的取消
$(document).on("click",".btns_can",function(){
  type=2;
  var pId=$(this).parents(".attr").attr("id"); 
  $("#"+pId).children(".attrValues").children(".av_ul").children(".trig_item").children(".check_cate").css({"background-position":"-41px -414px"}).hide();
  $("#"+pId).children(".attrValues").children(".av_ul").children(".trig_item").removeAttr("selected");
  $(this).parent(".btns").hide();
  $(this).parent(".btns").siblings(".check_more").show();
});

//所有筛选条件里面的取消
$(document).on("click",".cate_img",function(){
  $(this).parent(".cate").remove();
  var pId=$(this).parent(".cate").attr("pId");
  $("#"+pId).children(".btns").hide();
  $("#"+pId).children(".check_more").show();
  $("#"+pId).show();
  if($(".all").children(".new_cate").children("li").length<=0){
    $(".all").hide();
  }
})

/*s--得到来源的筛选条件*/
getSourceFilters();
function getSourceFilters(){
  $.ajax({
    type:"POST",
    url:rootPath+"content/getConditions.do",
    dataType:"json",
    cache:false, 
    success:function(resultData){
      if(resultData.ReturnType=="1001"){
        loadSourceFilters(resultData);//加载来源的筛选条件
      }else{
        $("#source .attrValues .av_ul").html("<li>暂无来源</li>");
      }
    },
    error:function(jqXHR){
      alert("得到来源的筛选条件发生错误："+ jqXHR.status);
    }
  });
}
function loadSourceFilters(resultData){
  $("#source .attrValues .av_ul").html(" ");//清空
  for(var i=0;i<resultData.Source.length;i++){
    var li='<li class="trig_item" id='+resultData.Source[i].SourceId+'>'+
              '<div class="check_cate"></div>'+
              '<a class="ss1" href="javascript:void(0)">'+resultData.Source[i].SourceName+'</a>'+
            '</li>';
    $("#source .attrValues .av_ul").append(li);
  }
}
/*s--得到来源的筛选条件*/
