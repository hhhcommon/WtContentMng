$(function(){
  var rootPath=getRootPath();
  var isExisted = true;//定义存在为true
  var tag_sum=0;//定义添加的标签数量,最大是5
  var type=2;//type=1点击多选,type=2未点击多选
  var overChannel=false;

  $(document).on("click",".more1",function(){//点击更多
    if($("#channel").children(".attrValues").children("ul").hasClass("h40")){
      $("#channel").children(".attrValues").children("ul").removeClass("h40");
      $(this).children("span").text("收起");
    }else{
      $("#channel").children(".attrValues").children("ul").addClass("h40");
      $(this).children("span").text("更多");
    }
  });
  $(document).on("click",".more3",function(){//点击多选
    type=1;
    $(this).siblings(".attrValues").children(".av_ul").children(".trig_item").children(".check_cate").show();
    $(this).hide();
    $(this).siblings(".btns").show();
  });
  $(document).on("click",".ri_top_li4",function(){//点击收起筛选
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
  $(document).on("click",".trig_item",function(){//选中某一项
//  debugger;
    var pId=$(this).parents(".attr").attr("id");
    var id=$(this).attr("id");
    var pTitle='';
    if((pId=="status")&&($(this).parents(".attr").attr("ids")=="jmstatus")){
      pTitle="节目状态：";
    }
    if((pId=="status")&&($(this).parents(".attr").attr("ids")=="zjstatus")){
      pTitle="专辑状态：";
    }
    if(pId=="album"){
      pTitle="所属专辑：";
    }
    if(pId=="channel"){
      pTitle="所属栏目：";
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
      if(i==sl){
        $(".ri_top_li4").hide();
      }else{
        $(".ri_top_li4").show();
      }
      if($(".all").is(':hidden')) $(".all").show();
    }
  });
  $(document).on("click",".btns_sub",function(){//点击多选里面的确定
//  debugger;
    type=2;
    var pId=$(this).parents(".attr").attr("id");
    var id=$(this).attr("id");
    var pTitle='';
    if(pId=="status"){
      pTitle="节目状态：";
    }
    if(pId=="album"){
      pTitle="所属专辑：";
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
    console.log("I=="+i);
    if(i==sl){
      $(".ri_top_li4").hide();
    }else{
      $(".ri_top_li4").show();
    }
    if($(".all").is(':hidden')) $(".all").show();
  });
  $(document).on("click",".btns_can",function(){//点击多选里面的取消
    var pId=$(this).parents(".attr").attr("id"); 
    $("#"+pId).children(".attrValues").children(".av_ul").children(".trig_item").children(".check_cate").css({"background-position":"-41px -414px"}).hide();
    $("#"+pId).children(".attrValues").children(".av_ul").children(".trig_item").removeAttr("selected");
    $(this).parent(".btns").hide();
    $(this).parent(".btns").siblings(".check_more").show();
  });
  $(document).on("click",".cate_img",function(){//点击所有分类里面的取消
//  debugger;
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
  $(document).on("mouseenter","#channel .chnel",function(){//鼠标放在一级栏目上
    overChannel=true;
    var pid=$(this).attr("data_idx");
    $(".chnels").each(function(){
      if(pid==$(this).attr("data_idx")){
        $(".chnels").hide();
        $("#channel .chnel").removeClass("trig_curr");
        $("#channel .chnel[data_idx="+pid+"]").addClass("trig_curr");
        if(pid>=0&&pid<=9){
          $(this).css({"top":"39px"}).show();
        }else if(pid>=10&&pid<=19){
          $(this).css({"top":"79px"}).show();
        }else if(pid>=20&&pid<=29){
          $(this).css({"top":"119px"}).show();
        }else{
          $(this).css({"top":"159px"}).show();
        }
      }
    })
  });
  $(document).on("mouseleave","#channel .chnel",function(){//鼠标离开一级栏目
    overChannel=false;
    setTimeout(function(){
      if(overChannel) return;
      $("#channel .chnel").removeClass("trig_curr");
      $(".chnels").css({"top":"39px"}).hide();
    },200)
  });
  $(document).on("mouseenter","#channel .chnels",function(){//鼠标放在二级栏目上
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
  $(document).on("mouseleave","#channel .chnels",function(){//鼠标离开二级栏目
    overChannel=false;
    setTimeout(function(){
      if(overChannel) return;
      $("#channel .chnel").removeClass("trig_curr");
      $("#channel .chnels").hide();
    },200)
  });
  $(document).on("click",".trig_item_li",function(){//选中二级栏目里面的一级栏目
    var pname=$(this).parent(".chnels").attr("data_name");
    var newFilter='<li class="cate" pId="channel">'+
                    '<span class="cate_desc"><span class="cate_desc_title">所属栏目&gt;'+pname+'</span>'+"&gt;"+$(this).children(".ss1").text()+'</span>'+
                    '<span class="cate_img">×</span>'+
                  '</li>';
    $(".new_cate").append(newFilter);
    $("#channel").children(".attrValues").children(".av_ul").children(".trig_item").children(".check_cate").css({"background-position":"-41px -414px"}).hide();
    $("#channel").children(".attrValues").children(".av_ul").children(".trig_item").removeAttr("selected");
    $("#channel").hide();
    if($(".all").is(':hidden')) $(".all").show();
  });
  
  /*点击取消，罩层和上传节目的页面消失*/
  $(".collapse-link,.cancel").on("click",function(){
    $("form")[0].reset();
    $(".mask,.add").hide();
    $(".sonProgress").html(" ");
    $(".parentProgress,.sonProgress").hide();
    $("body").css({"overflow":"auto"});
  });
  
  /*底部footer显示/隐藏*/
  $(window).on("scroll", function(){ 
    var sTop = $(window).scrollTop();  
    var sTop = parseInt(sTop);  
    if (sTop >= 10){ 
      if(!$('.footer', parent.document).is(":visible")){ 
        $('.wrapper', parent.document).css({"height":"64%"});
        $('.footer', parent.document).show();
      }  
    }else{  
      if($('.footer', parent.document).is(":visible")){  
        $('.footer', parent.document).hide();
        $('.wrapper', parent.document).css({"height":"84%"});
      }  
    }
  }); 
  //点击footer_hide，footer隐藏
  $('.footer_hide', parent.document).on("click",function(){
    $('.wrapper', parent.document).css({"height":"84%"});
    $('.footer', parent.document).hide();
  });
  
  /*
   * 
   * 弹出节面的公共方法*/
  
  //1.对新添加的标签的样式改变
  $(document).on("mouseenter",".bqImg",function(){
    $(this).children(".cancelImg").show();
  });
  $(document).on("mouseleave",".bqImg",function(event){
    $(this).children(".cancelImg").hide();
  });
  $(document).on("mouseenter",".cancelImg",function(){
    event.stopPropagation();
    $(this).attr({"src":"img/upl_img6.png"});
  });
  $(document).on("mouseleave",".cancelImg",function(event){
    event.stopPropagation();
    $(this).attr({"src":"img/upl_img2.png"});
    $(this).hide();
  });
  
  //2.对我的标签和公共标签进行添加操作
  $(document).on("click",".my_tag_con1_check, .gg_tag_con1_check",function(){
    if($(".upl_bq").children("li")){
      tag_sum=$(".upl_bq").children("li").length;
    }else{
      tag_sum=0;
    }
    var txt=$(this).siblings("span").html();
    var tagId=$(this).parent("li").attr("tagid");
    var tagType=$(this).parent("li").attr("tagType");
    var obj=$(this);
    addTag(obj,txt,tagId,tagType);
  });
  
  //3.添加标签的方法
  function addTag(obj,txt,tagId,tagType){
    isExiste(txt);//调用函数判断即将添加的标签是否应经存在
    if(!isExisted){
      if(tag_sum>=5){
        alert("最多添加5个标签");
        obj.attr("checked",false);
        return;
      }else{
        obj.attr("checked",true);
        obj.attr("disabled",true);
      }
      var new_tag= '<li class="upl_bq_img bqImg" tagId='+tagId+' tagType='+tagType+'>'+
                      '<span>'+txt+'</span>'+
                      '<img class="upl_bq_cancelimg1 cancelImg" src="img/upl_img2.png" alt="" />'+
                    '</li>';
      $(".upl_bq").append(new_tag);
      tag_sum++;
    }else{
      alert("你添加的标签已存在!");
    }
  }
  
  //4.删除新添加的标签
  $(document).on("click",".upl_bq_cancelimg1",function(){
    --tag_sum;
    var cancel_txt=$(this).siblings("span").html();
     $(".my_tag_con1,.gg_tag_con1").each(function(){
      if($(this).children("span").html()==cancel_txt){
        $(this).children("input[type='checkbox']").attr("checked",false);
        $(this).children("input[type='checkbox']").attr("disabled",false);
      }
    });
    $(this).parent().remove();
    $(".tag_txt").focus();
  });
  
  //5.判断即将添加标签是否已经存在
  function isExiste(objValue){
    if($(document).find(".upl_bq .upl_bq_img").length>0){//如果页面上.upl_bq里面已经存在标签
      $(document).find(".upl_bq .upl_bq_img").each(function(){
        if($(this).children("span").html()==objValue){
          isExisted = true;
          return false;
        }else{
          isExisted=false;
        }
      });
    }else{
      isExisted=false;
    }
  }
  
  //6.添加自定义标签
  document.onkeydown = function(event){
    if(event.keyCode == 32){return false;}//禁止输入空格
    if(event.keyCode == 13){//13是enter键，32是空格键
      var txt=$.trim($(".tag_txt").val());
      if(txt!=""){
        var count = txt.replace(/[^\x00-\xff]/g,"**").length;
        if(count<=12){
          isExiste(txt);//调用函数判断即将添加的标签是否应经存在
          if(!isExisted){
            if(tag_sum>=5){
              alert("最多添加5个标签");
              $(".tag_txt").val("");
              return;
            }
            var new_tag= '<li class="upl_bq_img bqImg" tagType="自定义标签">'+
                              '<span>'+txt+'</span>'+
                              '<img class="upl_bq_cancelimg1 cancelImg" src="img/upl_img2.png" alt="" />'+
                            '</li>';
            $(".upl_bq").append(new_tag);
            tag_sum++;
          }else{
            alert("你添加的标签已存在!");
          }
        }else{
          alert("输入内容超出范围");
        }
        $(".tag_txt").val("");
      }
    }
  };
  //当页面宽度发生变化时
  window.onresize=function(){
    laydate.reset(); 
  }
  //日历插件的位置跟随着滚动条变化
  $(".add").on("scroll", function(){ 
    laydate.reset();//重设日历控件坐标，一般用于页面dom结构改变时
  }) 
  
  /*
    图片裁剪上传
   * */
  $(".upl_pt_img").on("click",function(){
    $(".mask_clip,.container_clip").show();
  });
  $(".upload_pic").on("click",function(){
    $(".picFile").click();
  })
  $('.picFile').on('change', function(){
    cleCanvas();
    uploadPic();
  });
  function uploadPic(){
    var ics = new imgStroke();
    var reader=new FileReader();
    reader.onload=function(){ 
      // 通过 reader.result 来访问生成的 DataURL
      var url=reader.result;
      ics.init({"canvasId":"myCanvas","url":url,"x":20,"y":20});
      demo_report();
    };       
    reader.readAsDataURL($(".picFile")[0].files[0]);
    var jqObj=$(".picFile");
    jqObj.val("");
    var domObj = jqObj[0];
    domObj.outerHTML = domObj.outerHTML;
    var newJqObj = jqObj.clone();
    jqObj.before(newJqObj);
    jqObj.remove();
    $(".picFile").unbind().change(function (){
      uploadPic();
    });
  }
  $('#btnSave').on('click', function(){
    var imgBase64Data=$(document).find(".cropped img").attr("src");
    var oMyForm = new FormData();
    oMyForm.append("ContentFile",imgBase64Data);
    oMyForm.append("DeviceId","3279A27149B24719991812E6ADBA5584");
    oMyForm.append("MobileClass","Chrome");
    oMyForm.append("PCDType","3");
    oMyForm.append("UserId","123");
    oMyForm.append("Purpose","2");
    oMyForm.append("SrcType","1");
    $.ajax({
      url:rootPath+"common/uploadCM.do",
      type:"POST",
      data:oMyForm,
      cache: false,
      processData: false,
      contentType: false,
      dataType:"json",
      //表单提交前进行验证
      success: function(resultData){
        if(resultData.Success =true){
          alert("图片裁剪上传成功");
          $(".upl_img").attr("value",resultData.FilePath);
          cleCanvas();
          $(".container_clip,.mask_clip").hide();
          if($(".defaultImg").css("display")!="none"){
            $(".defaultImg").css({"display":"none"});
          }
          var newImg =$("<img class='newImg' src="+resultData.FilePath+" alt='front cover' />");
          if($(".previewImg").children().length>1){
            $(".previewImg img:last").replaceWith(newImg);
          }else{
            $(".previewImg").append(newImg);
          } 
        }else{
          alert(resultData.err);
        }
      },
      error: function(XHR){
        alert("发生错误" + jqXHR.status);
      }
    });
  });
  
  $("#resetId").on("click",function(){
    if(confirm("您确定要取消裁剪吗？")){
      cleCanvas();
      $(".container_clip,.mask_clip").hide();
    }
  });
  
  //清空画布
  function cleCanvas(){
    var myctx=document.getElementById("myCanvas").getContext("2d");
    myctx.restore();
    var towctx=document.getElementById("myCanvasTow").getContext("2d");
    towctx.restore();
    $("#myCanvas,#myCanvasTow").hide();
    $("#cutImgId").attr({"src":""});
  }
  
  //销毁obj对象的key-value
  function destroy(obj){
    for(var key in obj){//清空对象
      delete obj[key];
    }
  }
  
})
