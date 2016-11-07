$(function(){
  var isExisted = true;//定义存在为true
  var tag_sum=0;//定义添加的标签数量,最大是5
  //添加自定义标签
  document.onkeydown = function(event){
    if(event.keyCode == 13){//13是enter键，32是空格键
      event.preventDefault();
      if($(".tag_txt").val()!=""){
        var count = $(".tag_txt").val().replace(/[^\x00-\xff]/g,"**").length;
        var txt=$(".tag_txt").val();
        if(count<=12){
          isExiste(txt);//调用函数判断即将添加的标签是否应经存在
          if(!isExisted){
            if(tag_sum>=5){
              alert("最多添加5个标签");
              $(".tag_txt").val("");
              return;
            }
            var new_tag= '<li class="upl_bq_img bqImg">'+
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
  
  //对新添加的标签的样式改变
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
  
  //对我的标签和公共标签进行添加操作
  $(document).on("click",".my_tag_con1_check, .gg_tag_con1_check",function(){
    var txt=$(this).siblings("span").html();
    var obj=$(this);
    addTag(obj,txt);
  });
  
  //添加标签的方法
  function addTag(obj,txt){
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
      var new_tag= '<li class="upl_bq_img bqImg">'+
                        '<span>'+txt+'</span>'+
                        '<img class="upl_bq_cancelimg1 cancelImg" src="img/upl_img2.png" alt="" />'+
                      '</li>';
      $(".upl_bq").append(new_tag);
      tag_sum++;
    }else{
      alert("你添加的标签已存在!");
    }
  }
  
  //删除新添加的标签
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
  
  //判断即将添加标签是否已经存在
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
  
  //点击换一批
  $(document).on("click",".hyp",function(){
    alert("请求加载另一批数据");
  });
  
  /*创作方式*/
  //点击确认按钮，添加创作方式
  $(document).on("click",".czfs_author",function(){
    if($(".czfs_author_ipt").val()==""||$(".czfs_author_ipt").val()==null){
      alert("作者名字不能为空");
    }else{
      var new_czfs= '<li class="czfs_tag_li bqImg">'+
                      '<div class="czfs_tag_div">'+
                      '<span class="czfs_tag_span1">'+$(".change_czfs option:selected").text()+' : </span>'+
                      '<span class="czfs_tag_span2">'+$(".czfs_author_ipt").val()+'</span>'+
                      '</div>'+
                      '<img class="cancelImg" src="img/upl_img2.png" alt="" />'+
                    '</li>';
      $(".czfs_tag").append(new_czfs);              
    }
  });
  
  //取消新添加的创作方式
  $(document).on("click",".cancelImg",function(){
    $(this).parent(".bqImg").remove();
    $(".czfs_author_ipt").val("");
  });
});
