$(function(){
  var rootPath=getRootPath();
  var isExisted = true;//定义存在为true
  var is_exist=false;//要添加的创作方式是否存在
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
  $(document).on("click",".trig_item_li",function(){//选中一级栏目里面的二级栏目
    var pname=$(this).parent(".chnels").attr("data_name");
    var pid=$(this).attr("pid");
    var id=$(this).attr("id");
    var newFilter='<li class="cate" pId="channel" p_id='+pid+' id='+id+'>'+
                    '<span class="cate_desc"><span class="cate_desc_title">所属栏目&gt;'+pname+'</span>'+"&gt;"+$(this).children(".ss1").text()+'</span>'+
                    '<span class="cate_img">×</span>'+
                  '</li>';
    $(".new_cate").append(newFilter);
    $("#channel").children(".attrValues").children(".av_ul").children(".trig_item").children(".check_cate").css({"background-position":"-41px -414px"}).hide();
    $("#channel").children(".attrValues").children(".av_ul").children(".trig_item").removeAttr("selected");
    $("#channel").hide();
    if($(".all").is(':hidden')) $(".all").show();
  });
  
  /*点击取消，罩层和模态框消失*/
  $(".add_zj .collapse-link,.add_zj .cancel,.add_jm .collapse-link,.add_jm .cancel").on("click",function(){
    $("form")[0].reset();
    $(".mask_zj,.add_zj,.mask_jm,.add_jm").hide();
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
  
  //当页面宽度发生变化时
  window.onresize=function(){
    laydate.reset(); 
  }
  
  //日历插件的位置跟随着滚动条变化
  $(".add").on("scroll", function(){ 
    laydate.reset();//重设日历控件坐标，一般用于页面dom结构改变时
  }) 
  
  /*s--图片裁剪上传 */
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
    if(!imgBase64Data||imgBase64Data=="undefined"){
      alert("请先点击裁剪再进行保存");
      return;
    }else{
      $(".previewImg").attr("isDefaultImg","false");
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
    }
  });
  $("#resetId").on("click",function(){
    if(confirm("您确定要取消裁剪吗？")){
      cleCanvas();
      $(".previewImg").attr("isDefaultImg","true");
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
  /*e--图片裁剪上传 */
 
  //销毁obj对象的key-value
  function destroy(obj){
    for(var key in obj){//清空对象
      delete obj[key];
    }
  }
  
  /*s---新增节目--弹出页面上操作/方法集合 */
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
  //添加标签的方法
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
  //添加自定义标签
  $(".tag_txt").keydown(function(e){
    var evt=event?event:(window.event?window.event:null);//兼容IE和FF
    if (evt.keyCode==13){
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
  });
  //上传节目页面获取公共标签
  var data1={"DeviceId":"3279A27149B24719991812E6ADBA5584",
             "MobileClass":"Chrome",
             "PCDType":"3",
             "UserId":"123",
             "MediaType":"1",
             "SeqMediaId":"704df034185448e3b9ed0801351859fb",
             "ChannelIds":"cn31",
             "TagType":"1",
             "TagSize":"20"
  };
  loadPubTag(data1);
  function loadPubTag(data){
    $.ajax({
      type:"POST",
      url:rootPath+"content/getTags.do",
      dataType:"json",
      data:JSON.stringify(data1),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          getPubLabel(resultData);//加载上传节目页面公共标签元素
        }else{
//        alert("获取公共标签失败，请刷新页面重新获取");
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  //加载上传节目页面公共标签元素
  function getPubLabel(resultData){
    $(".gg_tag_con").html("");
    for(var i=0;i<resultData.AllCount;i++){
      var label='<li class="gg_tag_con1" tagType='+resultData.ResultList[i].TagOrg+' tagId='+resultData.ResultList[i].TagId+'>'+
                  '<input type="checkbox" class="gg_tag_con1_check" />'+
                  '<span class="gg_tag_con1_span">'+resultData.ResultList[i].TagName+'</span>'+
                '</li>';
      
      $(".gg_tag_con").append(label); 
    }
  }
  //点击“换一换”，更换公共标签
  $(document).on("click",".gg_tag .hyp",function(){
    loadPubTag(data1);
  })
  //上传节目页面获取我的标签
  var data2={"DeviceId":"3279A27149B24719991812E6ADBA5584",
             "MobileClass":"Chrome",
             "PCDType":"3",
             "UserId":"123",
             "MediaType":"1",
             "SeqMediaId":"704df034185448e3b9ed0801351859fb",
             "ChannelIds":"cn31",
             "TagType":"2",
             "TagSize":"20"
  };
  loadMyTag(data2);
  function loadMyTag(data){
    $.ajax({
      type:"POST",
      url:rootPath+"content/getTags.do",
      dataType:"json",
      data:JSON.stringify(data2),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          getMyLabel(resultData);//加载上传节目页面我的标签元素
        }else{
//        alert("获取我的标签失败，请刷新页面重新获取");
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  //加载上传节目页面我的标签元素
  function getMyLabel(resultData){
    $(".my_tag_con").html("");
    for(var i=0;i<resultData.AllCount;i++){
      var label='<li class="my_tag_con1" tagType='+resultData.ResultList[i].TagOrg+' tagId='+resultData.ResultList[i].TagId+'>'+
                  '<input type="checkbox" class="my_tag_con1_check" />'+
                  '<span class="my_tag_con1_span">'+resultData.ResultList[i].TagName+'</span>'+
                '</li>';
      $(".my_tag_con").append(label); 
    }
  }
  //点击“换一换”，更换我的标签
  $(document).on("click",".my_tag .hyp",function(){
    loadMyTag(data2);
  })
  
  //点击上传文件
  $(".upl_wj").on("click",function(){
    $(".upl_file").click();
  });
  $(".upl_file").change(function(){
    $(".upl_file").attr("value","");
    $(".uploadStatus").hide();
    $(".yp_mz").val("");
    $(".audio").attr("src","");
    var oMyForm = new FormData();
    var _this=$(this);
    $(".sonProgress,.parentProgress").show();
    oMyForm.append("ContentFile", $(this)[0].files[0]);
    oMyForm.append("DeviceId", "3279A27149B24719991812E6ADBA5584");
    oMyForm.append("MobileClass", "Chrome");
    oMyForm.append("PCDType", "3");
    oMyForm.append("UserId", "123");
    oMyForm.append("SrcType", "2");
    oMyForm.append("Purpose", "1");
    if(($(this)[0].files[0].size)/1048576>100){//判断文件大小是否大于100M
      alert("文件过大，请选择合适的文件上传！");
    }else{
      requestUpload(_this,oMyForm);//请求上传文件
    }
  });
  //请求上传文件
  function requestUpload(_this,oMyForm){
    $.ajax({
      url:rootPath+"common/uploadCM.do",
      type:"POST",
      data:oMyForm,
      cache: false,
      processData: false,
      contentType: false,
      dataType:"json",
      xhr: function(){
        var xhr = $.ajaxSettings.xhr();
        if(onprogress && xhr.upload) {
         xhr.upload.addEventListener("progress" , onprogress, false);
         return xhr;
        }  
      },
      //表单提交前进行验证
      success: function (resultData){
        if(resultData.Success==true){
          $(".audio").attr("src",resultData.FilePath);
          $(".yp_mz").val(resultData.FileOrigName);
          $(".upl_file").attr("value",resultData.FilePath);
          getTime();
          $(".uploadStatus").show();
        }else{
          alert(resultData.err);
        }
      },
      error: function(XHR){
        alert("发生错误" + jqXHR.status);
      }
    });
  }
  //侦查文件上传情况,,这个方法大概0.05-0.1秒执行一次
  function onprogress(evt){
    var loaded = evt.loaded;     //已经上传大小情况 
    var tot = evt.total;      //文件总大小 
    var per = Math.floor(100*loaded/tot);  //已经上传的百分比 
    $(".sonProgress").html( per +"%" );
    $(".sonProgress").css("width" , per +"%");
    if(per=="100"){
      $(".parentProgress").css({"border":"none"});
    }
  }
  //获取音频的时长
  function getTime() {
    setTimeout(function () {
      var duration = $(".audio")[0].duration;
      if(isNaN(duration)){//检查参数是否是非数字
        getTime();
      }else{
        var time=$(".audio")[0].duration;
        var timeLong=parseInt(time);
        $(".timeLong").attr("value",timeLong);
      }
    }, 10);
  }
  
  //获取选择专辑列表
  var data5={"DeviceId":"3279A27149B24719991812E6ADBA5584",
             "MobileClass":"Chrome",
             "PCDType":"3",
             "UserId":"123",
             "FlagFlow":"0",
             "ChannelId":"0",
             "ShortSearch":"false"
  };
  $.ajax({
    type:"POST",
    url:rootPath+"content/seq/getSeqMediaList.do",
    dataType:"json",
    data:JSON.stringify(data5),
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getAlbumList(resultData); //加载专辑列表,上传节目时使用
      }else{
//      alert("得到专辑列表失败，请刷新页面重新加载");
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  //加载专辑列表
  function getAlbumList(resultData){
    for(var i=0;i<resultData.ResultList.length;i++){
      var option='<option value="" id='+resultData.ResultList[i].ContentId+'>'+resultData.ResultList[i].ContentName+'</option>';
      $(".upl_zj").append(option);
    }
  }
  
  //获取创作方式列表
  var data3={"DeviceId":"3279A27149B24719991812E6ADBA5584",
             "MobileClass":"Chrome",
             "PCDType":"3",
             "UserId":"123",
             "CatalogType":"4",
             "TreeViewType":"zTree"
  };
  $.ajax({
    type:"POST",
    url:rootPath+"baseinfo/getCataTree4View.do",
    dataType:"json",
    data:JSON.stringify(data3),
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getArtMethodList(resultData); //得到创作方式列表
      }else{
//      alert("得到创作方式失败，请刷新页面重新加载");
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  //得到创作方式列表
  function getArtMethodList(resultData){
    for(var i=0;i<resultData.Data.children.length;i++){
      var option='<option value="" id='+resultData.Data.children[i].id+'>'+resultData.Data.children[i].name+'</option>';
      $(".change_czfs").append(option);
    }
  }
  //点击enter，添加创作方式
  $(".czfs_author_ipt").keydown(function(e){
    var evt=event?event:(window.event?window.event:null);//兼容IE和FF
    if (evt.keyCode==13){
      if($(".czfs_author_ipt").val()==""||$(".czfs_author_ipt").val()==null){
        alert("作者名字不能为空");
      }else{
        var member=[];
        $(".czfs_tag").find(".czfs_tag_li").each(function(){
          var czfsObj={};//创作方式对象
          var czfs_t=""+$(this).children().children(".czfs_tag_span1").html();
          var czfs_txt=czfs_t.split(":")[0];
          czfsObj.TypeName=$.trim(czfs_txt);
          czfsObj.TypeId=$(this).attr("czfs_typeid");
          czfsObj.TypeInfo=$.trim($(this).children().children(".czfs_tag_span2").html());
          member.push(czfsObj);
        });
        for(var i=0;i<member.length;i++){ 
          if((member[i].TypeName==$(".change_czfs option:selected").text())&&(member[i].TypeInfo==$(".czfs_author_ipt").val())){ 
            alert("你添加的创作方式已存在,请重新添加");
            $(".czfs_author_ipt").val(" ");
            is_exist=true;
            return;
          }else{
            is_exist=false;
          }
        }
        if(is_exist==false){
          var new_czfs= '<li class="czfs_tag_li bqImg" czfs_typeId='+$(".change_czfs option:selected").attr("id")+'>'+
                        '<div class="czfs_tag_div">'+
                        '<span class="czfs_tag_span1">'+$(".change_czfs option:selected").text()+' : </span>'+
                        '<span class="czfs_tag_span2">'+$(".czfs_author_ipt").val()+'</span>'+
                        '</div>'+
                        '<img class="cancelImg" src="img/upl_img2.png" alt="" />'+
                      '</li>';
          $(".czfs_tag").append(new_czfs);
          $(".czfs_author_ipt").val("");
        }
      }
    }
  });
  //取消新添加的创作方式
  $(document).on("click",".cancelImg",function(){
    $(this).parent(".bqImg").remove();
    $(".czfs_author_ipt").val("");
  });
  /*e---弹出页面上操作/方法集合 */
 
  /*s--批量操作，点击勾选框*/
  //点击全选
  $(document).on("click",".all_check",function(){
    if($(this).hasClass("checkbox1")){
      $(".checkbox_img").attr({"src":"img/checkbox2.png"});
      $(this).removeClass("checkbox1");
      $(".ri_top3_con .rtc_listBox").each(function(){
        $(this).children(".ric_img_check").removeClass("checkbox1");
      }); 
    }else{
      $(".checkbox_img").attr({"src":"img/checkbox1.png"});
      $(this).addClass("checkbox1");
      $(".ri_top3_con .rtc_listBox").each(function(){
        $(this).children(".ric_img_check").addClass("checkbox1");
      }); 
    }
  });
  //点击单个勾选框
  $(document).on("click",".ric_img_check",function(){
    if($(this).hasClass("checkbox1")){
      $(this).attr({"src":"img/checkbox2.png"}).removeClass("checkbox1");
    }else{
      $(this).attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
    }
  });
  /*e--批量操作，点击勾选框*/
});

/*s--清空数据与转换时间格式*/
//定时发布的时间格式转换成时间戳
function js_strto_time(str_time){
  var new_str = str_time.replace(/:/g,'-');
  new_str = new_str.replace(/ /g,'-');
  var arr = new_str.split("-");
  var datum = new Date(Date.UTC(arr[0],arr[1]-1,arr[2],arr[3]-8,arr[4],arr[5]));
  return strtotime = datum.getTime();
}
//清空创建专辑模态框上的数据
function clear_zj(){
  $("body").css({"overflow":"hidden"});
  $(".add_zj .upl_img").attr("value","");
  $(".add_zj .zjId,.add_zj .uplTitle,.add_zj .uplDecn,.add_zj .layer-date").val("");
  $(".add_zj .upl_bq").html("");
  $(".add_zj .newImg").remove();
  $(".add_zj .defaultImg").attr({"src":"http://wotingfm.com:908/CM/resources/images/default.png"}).show();
  $(".add_zj .img_uploadStatus").hide();
  $(".add_zj .my_tag_con1,.add_zj .gg_tag_con1").each(function(){
    $(this).children("input[type='checkbox']").prop("checked",false);
    $(this).children("input[type='checkbox']").attr("disabled",false);
  })
  $(".add_zj .channelBox").html("");
  $(".add_zj .cBox1_conS").html("<li class='center'>还未选择一级栏目</li>");
  $(".add_zj .channelBox1 li").removeClass("selectedF");
  $(".add_zj .channelBox1").hide();
  $(".mask_zj,.add_zj").show();
}  
//清空上传节目模态框上的数据
function clear_jm(){
  $("body").css({"overflow":"hidden"});
  $(".add_jm .sonProgress").html(" ");
  $(".add_jm .parentProgress,.add_jm .sonProgress").hide();
  $(".add_jm .upl_img").attr("value","");
  $(".add_jm .jmId,.add_jm .upl_file,.add_jm .upl_img,.add_jm .timeLong").attr({"value":""});
  $(".add_jm .audio").attr("src","");
  $(".add_jm .uplTitle,.add_jm .yp_mz,.add_jm .uplDecn,.add_jm .czfs_author_ipt,.add_jm .layer-date").val("");
  $(".add_jm .upl_bq,.czfs_tag").html("");
  $(".add_jm .newImg").remove();
  $(".add_jm .defaultImg").attr({"src":"http://wotingfm.com:908/CM/resources/images/default.png"}).show();
  $(".add_jm .img_uploadStatus,.add_jm .uploadStatus").hide();
  $(".add_jm .my_tag_con1,.add_jm .gg_tag_con1").each(function(){
    $(this).children("input[type='checkbox']").prop("checked",false);
    $(this).children("input[type='checkbox']").attr("disabled",false);
  })
  $(".add_jm .upl_zj option").each(function(){
    $(this).attr("selected",false);
  })
  $(".add_jm .change_czfs option").each(function(){
    $(this).attr("selected",false);
  })
  $(".mask_jm,.add_jm").show();
}
/*e--清空数据与转换时间格式*/

