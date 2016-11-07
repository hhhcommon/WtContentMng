$(function(){
  var rootPath=getRootPath();
  var isExisted = true;//定义存在为true
  var tag_sum=0;//定义添加的标签数量,最大是5
  
  //1.点击上传文件
  $(".upl_wj").on("click",function(){
    $(".yp_mz").val("");//清空放文件名的input框
    $(".upl_file").click();
  });
  $(".upl_file").change(function(){
    var oMyForm = new FormData();
    var filePath=$(this).val();
    var _this=$(this);
    var arr=filePath.split('\\');
    var fileName=arr[arr.length-1];
    $(".yp_mz").val(fileName);
    oMyForm.append("ContentFile", $(this)[0].files[0]);
    oMyForm.append("UserId", "18d611784ae0");
    oMyForm.append("SrcType", "2");
    oMyForm.append("Purpose", "1");
    if(($(this)[0].files[0].size)/1048576>100){//判断文件大小是否大于100M
      alert("文件过大，请选择合适的文件上传！");
      $(".yp_mz").val("");
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
      //表单提交前进行验证
      success: function (opeResult){
        if(opeResult.ful[0].success=="TRUE"){
          alert("上传成功！");
          _this.attr("value",opeResult.ful[0].FilePath);
        }else{
          alert(opeResult.err);
        }
      },
      error: function(jqXHR){
        alert("发生错误" + jqXHR.status);
      }
    });
  };
  
  //2.点击上传图片
  $(".upl_pt_img").on("click",function(){
    $(".upl_img").click();
  });
  $(".upl_img").change(function(){
    var oMyForm = new FormData();
    var filePath=$(this).val();
    var _this=$(this);
    var arr=filePath.split('\\');
    var fileName=arr[arr.length-1];
    oMyForm.append("ContentFile", $(this)[0].files[0]);
    oMyForm.append("UserId", "18d611784ae0");
    oMyForm.append("SrcType", "1");
    oMyForm.append("Purpose", "2");
    if(($(this)[0].files[0].size)/1048576>1){//判断图片大小是否大于1M
      alert("图片过大，请选择合适的图片上传！");
    }else{
      requestUpload(_this,oMyForm);
    }
  });
  
  //3.获取选择专辑列表
  $.ajax({
    type:"POST",
    url:rootPath+"content/seq/getSeqMediaList.do",
    dataType:"json",
    data:{"UserId":"123","FlagFlow":"0","ChannelId":"lmType1","ShortSearch":"false"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getAlbumList(resultData); //得到专辑列表,上传节目时使用
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  //得到专辑列表
  function getAlbumList(resultData){
    for(var i=0;i<resultData.ResultList.length;i++){
      var option='<option value="" id='+resultData.ResultList[i].ContentId+'>'+resultData.ResultList[i].ContentName+'</option>';
      $(".upl_zj").append(option);
    }
  }
  
  //4.添加自定义标签
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
  
  //5.获取创作方式列表
  $.ajax({
    type:"POST",
    url:rootPath+"baseinfo/getCataTree4View.do",
    dataType:"json",
    data:{"CatalogType":"4","TreeViewType":"zTree"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getArtMethodList(resultData); //得到创作方式列表
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
  
  //6.点击提交按钮，新增节目
  $("#submitBtn").on("click",function(){
    var _data={};
    _data.UserId="18d611784ae0";
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg={};
    _data.ContentImg.ContentMaxImg=$(".upl_img").attr("value");
    _data.ContentImg.ContentSmallImg=$(".upl_img").attr("value");
    _data.SeqMediaId=$(".upl_zj option:selected").attr("id");
    var taglist=[];
    $(".upl_bq").find(".upl_bq_img").each(function(){
      var tag={};//标签对象
      if($(this).attr("tagType")=="我的标签"){
        tag.TagName=$(this).children("span").html();
        tag.TagOrg="我的标签";
      }
      if($(this).attr("tagType")=="公共标签"){
        tag.TagName=$(this).children("span").html();
        tag.TagOrg="公共标签";
      }
      if($(this).attr("tagType")=="自定义标签"){
        tag.TagName=$(this).children("span").html();
        tag.TagOrg="自定义标签";
      }
      taglist.push(tag);
    });
    _data.TagList=taglist;
    _data.ContentDesc=$(".uplDecn").val();
    var memberTypelist=[];
    $(".czfs_tag").find(".czfs_tag_li").each(function(){
      var czfsObj={};//创作方式对象
      var czfs_t=""+$(this).children().children(".czfs_tag_span1").html();
      var czfs_txt=czfs_t.split(":")[0];
      czfsObj.TypeName=czfs_txt;
      czfsObj.TypeId=$(this).attr("czfs_typeid");
      czfsObj.TypeInfo=$(this).children().children(".czfs_tag_span2").html();
      memberTypelist.push(czfsObj);
    });
    _data.MemberTypelist=memberTypelist;
    var str_time=$(".layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    console.log(_data);
  });
  
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
      var new_czfs= '<li class="czfs_tag_li bqImg" czfs_typeId='+$(".change_czfs option:selected").attr("id")+'>'+
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
  
  //定时发布的时间格式转换成时间戳
  function js_strto_time(str_time){
    var new_str = str_time.replace(/:/g,'-');
    new_str = new_str.replace(/ /g,'-');
    var arr = new_str.split("-");
    var datum = new Date(Date.UTC(arr[0],arr[1]-1,arr[2],arr[3]-8,arr[4],arr[5]));
    return strtotime = datum.getTime()/1000;
  }
});
