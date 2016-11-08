$(function(){
  var rootPath=getRootPath();
  //获取栏目筛选条件
  $.ajax({
    type:"POST",
    url:rootPath+"content/getFiltrates.do",
    dataType:"json",
    data:{"UserId":"123","MediaType":"MediaAsset"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getChannelLabel(resultData);//得到栏目的筛选标签
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //得到栏目的筛选标签
  function getChannelLabel(resultData){
    for(var i=0;i<resultData.ResultList.ChannelList.length;i++){
      var filterChannel='<li class="trig_item" id='+resultData.ResultList.ChannelList[i].id+' pid='+resultData.ResultList.ChannelList[i].parentId+'>'+
                          '<a  href="javascript:void(0)">'+resultData.ResultList.ChannelList[i].nodeName+'<img src="img/del1.png" alt="取消操作" class="delLi"/></a>'+
                        '</li>';
      $("#channel .attrValues .av_ul").append(filterChannel); 
      var fccg='<div class="tab_cont_item av_ul" parentId='+resultData.ResultList.ChannelList[i].id+'></div>';
      $(".tab_cont").append(fccg);
      if(resultData.ResultList.ChannelList[i].isParent=="true"){
        for(var j=0;j<resultData.ResultList.ChannelList[i].children.length;j++){
          var filterChannelChildren='<div class="trig_item trig_item_li" >'+
                                      '<a  href="javascript:void(0)" id='+resultData.ResultList.ChannelList[i].children[j].id+'>'+resultData.ResultList.ChannelList[i].children[j].nodeName+'<img src="img/del1.png" alt="取消操作" class="delLi"/></a>'+
                                    '</div>';
          $('div[parentId='+resultData.ResultList.ChannelList[i].id+']').append(filterChannelChildren);
        }
      }else{
        $('div[parentId='+resultData.ResultList.ChannelList[i].id+']').append("<span style='display:block;text-align:center;'>暂时没有二级栏目<span>");
      }
    }
  }
  
  //获取专辑列表
  $.ajax({
    type:"POST",
    url:rootPath+"content/seq/getSeqMediaList.do",
    dataType:"json",
    data:{"UserId":"123","FlagFlow":"0","ChannelId":"lmType1","ShortSearch":"false"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getSeqMediaList(resultData); //得到专辑列表
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //得到专辑列表
  function getSeqMediaList(resultData){
    for(var i=0;i<resultData.ResultList.length;i++){
      var albumBox= '<div class="rtc_listBox">'+
                      '<div class="rtcl_img">'+
                        '<img src='+resultData.ResultList[i].ContentImg+' alt="节目图片" />'+
                      '</div>'+
                      '<div class="rtcl_con">'+
                        '<p class="rtcl_con_name">'+
                          '<span>专辑名称 ：</span>'+
                          '<span>'+resultData.ResultList[i].ContentName+'</span>'+
                        '</p>'+
                        '<p class="rtcl_con_num">'+
                          '<span>节目数量 ： </span>'+
                          '<span>'+resultData.ResultList[i].SubCount+'</span>'+
                        '</p>'+
                        '<p class="rtcl_con_time">'+
                          '<span>时间 ：</span>'+
                          '<span>'+resultData.ResultList[i].CTime+'</span>'+
                        '</p>'+
                      '</div>'+
                      '<p class="jm_st">'+resultData.ResultList[i].ContentPubChannels[0].FlowFlagState+'</p>'+
                      '<div class="op_type">'+
                        '<p class="jm_edit">编辑</p>'+
                        '<p class="jm_pub">发布</p>'+
                        '<p class="jm_del">删除</p>'+
                        '<p class="jm_recal">撤回</p>'+
                      '</div>'+
                    '</div>';
      $(".ri_top3_con").append(albumBox);              
    }
  }
  /*
  * 
  * 创建专辑页面
  * */
 //1.创建专辑页面获取公共标签
  $.ajax({
    type:"POST",
    url:rootPath+"content/getTags.do",
    dataType:"json",
    data:{"UserId":"123","MediaType":"1","SeqMediaId":"704df034185448e3b9ed0801351859fb","ChannelIds":"cn31","TagType":"1","TagSize":"20"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getPubLabel(resultData);//得到创建专辑页面公共标签元素
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //1.1得到创建专辑页面公共标签元素
  function getPubLabel(resultData){
    for(var i=0;i<resultData.AllCount;i++){
      var label='<li class="gg_tag_con1" tagType='+resultData.ResultList[i].TagOrg+' tagId='+resultData.ResultList[i].TagId+'>'+
                  '<input type="checkbox" class="gg_tag_con1_check" />'+
                  '<span class="gg_tag_con1_span">'+resultData.ResultList[i].TagName+'</span>'+
                '</li>';
      
      $(".gg_tag_con").append(label); 
    }
  }
  
  //2.创建专辑页面获取我的标签
  $.ajax({
    type:"POST",
    url:rootPath+"content/getTags.do",
    dataType:"json",
    data:{"UserId":"123","MediaType":"1","SeqMediaId":"704df034185448e3b9ed0801351859fb","ChannelIds":"cn31","TagType":"2","TagSize":"20"},
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getMyLabel(resultData);//得到创建专辑页面我的标签元素
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //2.1得到创建专辑页面我的标签元素
  function getMyLabel(resultData){
    for(var i=0;i<resultData.AllCount;i++){
      var label='<li class="my_tag_con1" tagType='+resultData.ResultList[i].TagOrg+' tagId='+resultData.ResultList[i].TagId+'>'+
                  '<input type="checkbox" class="my_tag_con1_check" />'+
                  '<span class="my_tag_con1_span">'+resultData.ResultList[i].TagName+'</span>'+
                '</li>';
      $(".my_tag_con").append(label); 
    }
  }
  
  //3.点击上传图片
  $(".upl_pt_img").on("click",function(){
    $(".upl_img").click();
  });
  $(".upl_img").change(function(){
    //图片预览
    if($(".defaultImg").css("display")!="none"){
      $(".defaultImg").css({"display":"none"});
    }
    var fileReader = new FileReader();
    fileReader.onload = function(evt){
      if(FileReader.DONE==fileReader.readyState){
        var newImg =  $("<img alt='front cover' />");
        newImg.attr({"src":this.result});//是Base64的data url数据
        if($(".previewImg").children().length>1){
          $(".previewImg img:last").replaceWith(newImg);
        }else{
          $(".previewImg").append(newImg);
        }
      }
    }
    fileReader.readAsDataURL($(this)[0].files[0]);
    var oMyForm = new FormData();
    var filePath=$(this).val();
    var _this=$(this);
    var arr=filePath.split('\\');
    var fileName=arr[arr.length-1];
    oMyForm.append("ContentFile", $(this)[0].files[0]);
    oMyForm.append("UserId", "123");
    oMyForm.append("SrcType", "1");
    oMyForm.append("Purpose", "2");
    if(($(this)[0].files[0].size)/1048576>1){//判断图片大小是否大于1M
      alert("图片过大，请选择合适的图片上传！");
    }else{
      requestUpload(_this,oMyForm);
    }
  });
  
  //4.请求上传文件
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
      error: function(XHR){
        alert("发生错误" + jqXHR.status);
      }
    });
  };
  
  //5.获得专辑的分类信息
  var _data={"cataId":"3"};
  $.ajax({
    url:rootPath+"common/getCataTreeWithSelf.do",
    type:"POST",
    data:JSON.stringify(_data),
    cache: false,
    processData: false,
    contentType: false,
    dataType:"json",
    //表单提交前进行验证
    success: function (resultData){
      if(resultData.jsonType == "1"){
        getCatalogList(resultData);//得到专辑分类列表
      }
    },
    error: function(XHR){
      alert("发生错误" + jqXHR.status);
    }
  });
  
  //5.1得到专辑分类列表
  function getCatalogList(resultData){
    for(var i=0;i<resultData.data.children.length;i++){
      var option='<option value="" id='+resultData.data.children[i].id+'>'+resultData.data.children[i].name+'</option>';
      $(".upl_zj").append(option);
    }
  }
  
  
  
  
});
