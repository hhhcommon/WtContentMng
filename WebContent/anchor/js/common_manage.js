$(function(){
  var isExisted = true;//定义存在为true
  var tag_sum=0;//定义添加的标签数量,最大是5
  
  /*点击一级栏目出现tab切换*/
  $(document).on("click",".trig_item",function(){
    var ids=$(this).parents(".attr").attr('id');
    if(ids=="channel"){
      if($(this).hasClass("trig_item_li")){
        $(".trig_item_li").removeClass("border_show").addClass("border_none");
        $(this).siblings().children("a").children("img").hide();
        $(this).children("a").children("img").show();
        $(this).removeClass("border_none").addClass("border_show");
      }else{
        $(this).siblings().removeClass("trig_curr");
        $(this).siblings().children("a").children("img").hide();
        $(this).siblings().removeClass("border_show").addClass("border_none");
        $(this).children("a").children("img").show();
        $(this).addClass("trig_curr");
        $(this).removeClass("border_none").addClass("border_show");
        $(".tab_cont_item .trig_item_li").children("a").children("img").hide();
        $(".tab_cont_item .trig_item_li").removeClass("border_show").addClass("border_none");
        $(".tab_cont_item").siblings().hide().eq($('#channel .trig_item').index(this)).show();
      }
    }else{
      $("#"+ids+" .trig_item").removeClass("border_show").addClass("border_none");
      $("#"+ids+" .trig_item").children("a").children("img").hide();
      $(this).addClass("border_show");
      $(this).children("a").children("img").show();
    }
  });
  
  /*点击更多按钮*/
  $(document).on("click",".av-options",function(){
    if($(this).children(".avo-more").text()=="更多"){
      $(this).children(".avo-more").text("收起");
      $(this).children(".avo-more-img").attr({"src":"img/filter1.png"});
      $(this).siblings(".av-gather").css({"height":"auto"});
    }else{
      $(this).children(".avo-more").text("更多");
      $(this).children(".avo-more-img").attr({"src":"img/filter2.png"});
      $(this).siblings(".av-gather").css({"height":"30px"});
    }
  });
  
  /*点击每个小分类下面的删除标签*/
  $(document).on("click",".delLi",function(event){
    event.stopPropagation();
    var i=$(this).parent().parent().index();
    if($(this).parents(".attr").attr("id")=="channel"){
      if($(this).parent().parent().parent().hasClass("tab_cont_item")){
        $(this).parent().parent().addClass("border_none");;
        $(this).hide();
        $(".tab_cont_item").hide();
        $(".av-gather .trig_item").each(function(){
          if($(this).hasClass("trig_curr")){
            $(this).removeClass("trig_curr");
            $(this).addClass("border_show");
          }
        })
      }else{
        $(this).parent().parent().removeClass("border_show").addClass("border_none");
        $(this).hide();
        $(".tab_cont_item").hide();
        $(".av-gather .trig_item").removeClass("trig_curr");
      }
    }else{
      $(this).parent().parent().removeClass("border_show").addClass("border_none");
      $(this).hide();
    }
  });
  
  /*点击取消，罩层和上传节目的页面消失*/
  $(".collapse-link,.cancel").on("click",function(){
    $("form")[0].reset();
    $(".mask,.add").hide();
    $("body").css({"overflow":"auto"});
  });
  
  /*底部footer显示/隐藏*/
  $(window).on("scroll", function(){ 
    var sTop = $(window).scrollTop();  
    var sTop = parseInt(sTop);  
    if (sTop >= 120){ 
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
  
  //7.点击换一批
  $(document).on("click",".hyp",function(){
    alert("请求加载另一批数据");
  });
  
})
