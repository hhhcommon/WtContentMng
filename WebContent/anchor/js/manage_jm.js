$(function(){
  var rootPath=getRootPath();
  var subType=1;//subType=1代表在上传节目页面提交,subType=2代表在修改节目页面提交
  var pubType=1;//pubType=1代表在上传节目页面提交,pubType=2代表在修改节目页面提交
  var uploadType=1;//uploadType=1代表上传文件,uploadType=2代表上传图片
  
  //00-1获取栏目筛选条件
  $.ajax({
    type:"POST",
    url:rootPath+"content/getFiltrates.do",
    dataType:"json",
    data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
          "MobileClass":"Chrome",
          "UserId":"123",
          "PCDType":"3",
          "MediaType":"MediaAsset"
    },
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getChannelLabel(resultData);//得到栏目的筛选标签
        getAlbumLabel(resultData);//得到专辑的筛选标签
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //00-2得到栏目的筛选标签
  function getChannelLabel(resultData){
    for(var i=0;i<resultData.ResultList.ChannelList.length;i++){
      var filterChannel='<li class="trig_item chnel" data_idx='+i+' id='+resultData.ResultList.ChannelList[i].id+' pid='+resultData.ResultList.ChannelList[i].parentId+'>'+
                          '<a  class="ss1" href="javascript:void(0)">'+resultData.ResultList.ChannelList[i].nodeName+'</a>'+
                        '</li>';
      $("#channel .attrValues .av_ul").append(filterChannel); 
      var fccg='<ul class="tab_cont_item chnels" data_idx='+i+' data_name='+resultData.ResultList.ChannelList[i].nodeName+' parentId='+resultData.ResultList.ChannelList[i].id+'></ul>';
      $("#channel").append(fccg);
      if(resultData.ResultList.ChannelList[i].isParent=="true"){
        for(var j=0;j<resultData.ResultList.ChannelList[i].children.length;j++){
          var filterChannelChildren='<li class="trig_item_li">'+
                                      '<a class="ss1" href="javascript:void(0)" id='+resultData.ResultList.ChannelList[i].children[j].id+'>'+resultData.ResultList.ChannelList[i].children[j].nodeName+'</a>'+
                                    '</li>';
          $('ul[parentId='+resultData.ResultList.ChannelList[i].id+']').append(filterChannelChildren);
        }
      }else{
        $('ul[parentId='+resultData.ResultList.ChannelList[i].id+']').append("<li style='display:block;text-align:center;float:none;margin:0px auto;'>暂时没有二级栏目</li>");
      }
    }
  }
  
  //00-3得到专辑的筛选标签
  function getAlbumLabel(resultData){
    for(var i=0;i<resultData.ResultList.SeqMediaList.length;i++){
      var filterAlbum='<li class="trig_item" id='+resultData.ResultList.SeqMediaList[i].PubId+'>'+
                        '<div class="check_cate"></div>'+
                        '<a class="ss1" href="javascript:void(0)">'+resultData.ResultList.SeqMediaList[i].PubName+'</a>'+
                      '</li>';
      $("#album .attrValues .av_ul").append(filterAlbum);                
    }
  }
  
  //00-4获取节目列表
  var dataParam={"DeviceId":"3279A27149B24719991812E6ADBA5584","MobileClass":"Chrome","PCDType":"3","UserId":"123","FlagFlow":"0","ChannelId":"0","SeqMediaId":"0"};
  getContentList(dataParam);
  function getContentList(obj){
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/getMediaList.do",
      dataType:"json",
      data:JSON.stringify(obj),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          getMediaList(resultData); //得到节目列表
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //00-4.1得到节目列表
  function getMediaList(resultData){
    $(".ri_top3_con").html("");//每次加载之前先清空
    for(var i=0;i<resultData.ResultList.AllCount;i++){
      if(resultData.ResultList.List[i].ContentSeqName){
        var programBox= '<div class="rtc_listBox" contentSeqId='+resultData.ResultList.List[i].ContentSeqId+' contentId='+resultData.ResultList.List[i].ContentId+'>'+
                        '<div class="rtcl_img">'+
                          '<img src='+resultData.ResultList.List[i].ContentImg+' alt="节目图片" />'+
                        '</div>'+
                        '<div class="rtcl_con">'+
                          '<h4>'+resultData.ResultList.List[i].ContentName+'</h4>'+
                          '<p class="zj_name">'+resultData.ResultList.List[i].ContentSeqName+'</p>'+
                          '<p class="other">'+
                            '<span>时间 ：</span>'+
                            '<span>'+resultData.ResultList.List[i].CTime+'</span>'+
                          '</p>'+
                        '</div>'+
                        '<p class="jm_st">'+resultData.ResultList.List[i].ContentPubChannels[0].FlowFlagState+'</p>'+
                        '<div class="op_type">'+
                          '<p class="jm_edit">编辑</p>'+
                          '<p class="jm_pub">发布</p>'+
                          '<p class="jm_del">删除</p>'+
                          '<p class="jm_recal">撤回</p>'+
                        '</div>'+
                      '</div>';
      }else{
        var programBox= '<div class="rtc_listBox" contentSeqId='+resultData.ResultList.List[i].ContentSeqId+' contentId='+resultData.ResultList.List[i].ContentId+'>'+
                        '<div class="rtcl_img">'+
                          '<img src='+resultData.ResultList.List[i].ContentImg+' alt="节目图片" />'+
                        '</div>'+
                        '<div class="rtcl_con">'+
                          '<h4>'+resultData.ResultList.List[i].ContentName+'</h4>'+
                          '<p class="zj_name">暂未绑定专辑</p>'+
                          '<p class="other">'+
                            '<span>时间 ：</span>'+
                            '<span>'+resultData.ResultList.List[i].CTime+'</span>'+
                          '</p>'+
                        '</div>'+
                        '<p class="jm_st">'+resultData.ResultList.List[i].ContentPubChannels[0].FlowFlagState+'</p>'+
                        '<div class="op_type">'+
                          '<p class="jm_edit">编辑</p>'+
                          '<p class="jm_pub">发布</p>'+
                          '<p class="jm_del">删除</p>'+
                          '<p class="jm_recal">撤回</p>'+
                        '</div>'+
                      '</div>';
      }
      
      $(".ri_top3_con").append(programBox);     
    }
  }
  
  //11-1点击上传节目按钮
  $(document).on("click",".ri_top_li3",function(){
    clear();//清空数据
    subType=1;
    pubType=1;
  });
  
  //22-1点击编辑节目按钮
   $(document).on("click",".jm_edit",function(){
    var contentId=$(this).parents(".rtc_listBox").attr("contentid");
    subType=2;
    pubType=2;
    edit_jm(contentId);
  })
  
  //33-1点击提交按钮，上传节目/修改节目
  $("#submitBtn").on("click",function(){
    if(subType=="1")  add_jm();
    if(subType=="2")  save_edit_jm();
  })
  
  //55-1点击发布按钮，上传节目/修改节目
  $("#pubBtn").on("click",function(){
    if(pubType=="1") pub_add_jm();
    if(pubType=="2") pub_edit_jm();
  })
  
  //33-1.1上传节目方法
  function add_jm(){
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentURI=$(".upl_file").attr("value");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
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
    _data.MemberType=memberTypelist;
    var str_time=$(".layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/addMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("新增节目成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(dataParam);//重新加载节目列表
        }else{
          alert(resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //33-1.2保存编辑后的信息
  function save_edit_jm(){
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentId=$(".jmId").val();
    _data.ContentURI=$(".upl_file").attr("value");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.SeqMediaId=$(".upl_zj option:selected").attr("id");
    var taglist=[];
    $(".upl_bq").find(".upl_bq_img").each(function(){
      var tag={};//标签对象
      var tagTxt=$(this).children("span").html();
      $(".my_tag_con1").each(function(){
        if($(this).children(".my_tag_con1_span").html()==tagTxt){
          $(".my_tag_con1").children(".my_tag_con1_check").prop("checked",false);
          $(".my_tag_con1").children(".my_tag_con1_check").attr("disabled",false);
          $(this).children(".my_tag_con1_check").prop("checked",true);
          $(this).children(".my_tag_con1_check").attr("disabled",true);
          tag.TagName=$(this).children(".my_tag_con1_span").html();
          tag.TagOrg="我的标签";
        }
      })
      $(".gg_tag_con1").each(function(){
        if($(this).children(".gg_tag_con1_span").html()==tagTxt){
          $(".gg_tag_con1").children(".gg_tag_con1_check").prop("checked",false);
          $(".gg_tag_con1").children(".gg_tag_con1_check").attr("disabled",false);
          $(this).children(".gg_tag_con1_check").prop("checked",true);
          $(this).children(".gg_tag_con1_check").attr("disabled",true);
          tag.TagName=$(this).children(".gg_tag_con1_span").html();
          tag.TagOrg="公共标签";
        }
      })
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
    _data.MemberType=memberTypelist;
    var str_time=$(".layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("修改节目成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(dataParam);//重新加载节目列表
        }else{
          alert(resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //22-1.1请求编辑节目时保存的信息
  function edit_jm(contentId){
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/getMediaInfo.do",
      dataType:"json",
      data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
            "MobileClass":"Chrome",
            "PCDType":"3",
            "UserId":"123",
            "ContentId":contentId
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          $(".mask,.add").show();
          $("body").css({"overflow":"hidden"});
          fillJmContent(resultData);//填充节目信息
        }else{
          alert(resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //22-1.2填充节目信息
  function fillJmContent(resultData){
    clear();//填充前清空数据
    $(".jmId").attr("value",resultData.Result.ContentId);
    $(".iboxtitle h4").html("修改节目");
    $(".yp_mz").val("aa.mp3");//数据库没有存这一字段，因为有需要，我自己加上的
    $(".upl_file").attr("value",resultData.Result.ContentPlay);
    $(".uplTitle").val(resultData.Result.ContentName);
    $(".defaultImg").attr("src",resultData.Result.ContentImg);
    $(".upl_zj option").each(function(){
      if($(this).attr("id")==resultData.Result.ContentSeqId){
        $(".upl_zj option").attr("selected",false);
        $(this).attr("selected",true); 
      }
    })
    if(resultData.Result.ContentKeyWords!=null){
      for(var i=0;i<resultData.Result.ContentKeyWords.length;i++){
        var new_tag= '<li class="upl_bq_img bqImg" tagId='+resultData.Result.ContentKeyWords[i].TagId+'>'+
                      '<span>'+resultData.Result.ContentKeyWords[i].TagName+'</span>'+
                      '<img class="upl_bq_cancelimg1 cancelImg" src="img/upl_img2.png" alt="" />'+
                    '</li>';
        $(".upl_bq").append(new_tag);
        var tagId=resultData.Result.ContentKeyWords[i].TagId;
        $(".my_tag_con").find(".my_tag_con1").each(function(){
          if($(this).attr("tagid")==tagId){
            $(this).children("input").prop("checked",true);
            $(this).children("input").attr("disabled",true)
          }
        })
        $(".gg_tag_con").find(".gg_tag_con1").each(function(){
          if($(this).attr("tagid")==tagId){
            $(this).children("input").prop("checked",true);
            $(this).children("input").attr("disabled",true)
          }
        })
      }
    }
    $(".uplDecn").val(resultData.Result.ContentDesc);
    if(resultData.Result.ContentMemberTypes!=null){
      for(var i=0;i<resultData.Result.ContentMemberTypes.length;i++){
        var new_czfs= '<li class="czfs_tag_li bqImg" czfs_typeId='+resultData.Result.ContentMemberTypes[i].TypeId+'>'+
                        '<div class="czfs_tag_div">'+
                        '<span class="czfs_tag_span1">'+resultData.Result.ContentMemberTypes[i].TypeName+' : </span>'+
                        '<span class="czfs_tag_span2">'+resultData.Result.ContentMemberTypes[i].TypeInfo+'</span>'+
                        '</div>'+
                        '<img class="cancelImg" src="img/upl_img2.png" alt="" />'+
                      '</li>';
        $(".czfs_tag").append(new_czfs); 
      }
    }
    $(".layer-date").val(resultData.Result.CTime);
  }
 
  //44-1点击删除节目按钮
  $(document).on("click",".jm_del",function(){
    $('.shade', parent.document).show();
    var contentId=$(this).parents(".rtc_listBox").attr("contentid");
    var contentSeqId=$(this).parents(".rtc_listBox").attr("contentseqid");
    del_jm(contentId,contentSeqId);
  })
  function del_jm(contentId,contentSeqId){
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/removeMedia.do",
      dataType:"json",
      data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
            "MobileClass":"Chrome",
            "PCDType":"3",
            "UserId":"123",
            "ContentId":contentId,
            "SeqMediaId":contentSeqId
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("成功删除节目");
          $('.shade', parent.document).hide();
          getContentList(dataParam);//重新加载节目列表
        }else{
          alert(resultData.Message);
          $('.shade', parent.document).hide();
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //55-1点击发布节目按钮
  $(document).on("click",".jm_pub",function(){
    $('.shade', parent.document).show();
    var contentId=$(this).parents(".rtc_listBox").attr("contentid");
    var contentSeqId=$(this).parents(".rtc_listBox").attr("contentseqid");
    pub_jm(contentId,contentSeqId);
  })
  function pub_jm(contentId,contentSeqId){
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaStatus.do",
      dataType:"json",
      data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
            "MobileClass":"Chrome",
            "PCDType":"3",
            "UserId":"123",
            "ContentId":contentId,
            "SeqMediaId":contentSeqId
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("节目发布成功");
          $('.shade', parent.document).hide();
          getContentList(dataParam);//重新加载节目列表
        }else{
          alert(resultData.Message);
          $('.shade', parent.document).hide();
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  
  /*
       弹出页面上的方法
   * */
 //1上传节目页面获取公共标签
  $.ajax({
    type:"POST",
    url:rootPath+"content/getTags.do",
    dataType:"json",
    data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
          "MobileClass":"Chrome",
          "PCDType":"3",
          "UserId":"123",
          "MediaType":"1",
          "SeqMediaId":"704df034185448e3b9ed0801351859fb",
          "ChannelIds":"cn31",
          "TagType":"1",
          "TagSize":"20"
    },
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getPubLabel(resultData);//得到上传节目页面公共标签元素
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //1.1得到上传节目页面公共标签元素
  function getPubLabel(resultData){
    for(var i=0;i<resultData.AllCount;i++){
      var label='<li class="gg_tag_con1" tagType='+resultData.ResultList[i].TagOrg+' tagId='+resultData.ResultList[i].TagId+'>'+
                  '<input type="checkbox" class="gg_tag_con1_check" />'+
                  '<span class="gg_tag_con1_span">'+resultData.ResultList[i].TagName+'</span>'+
                '</li>';
      
      $(".gg_tag_con").append(label); 
    }
  }
  
  //2上传节目页面获取我的标签
  $.ajax({
    type:"POST",
    url:rootPath+"content/getTags.do",
    dataType:"json",
    data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
          "MobileClass":"Chrome",
          "PCDType":"3",
          "UserId":"123",
          "MediaType":"1",
          "SeqMediaId":"704df034185448e3b9ed0801351859fb",
          "ChannelIds":"cn31",
          "TagType":"2",
          "TagSize":"20"
    },
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getMyLabel(resultData);//得到上传节目页面我的标签元素
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //2.1得到上传节目页面我的标签元素
  function getMyLabel(resultData){
    for(var i=0;i<resultData.AllCount;i++){
      var label='<li class="my_tag_con1" tagType='+resultData.ResultList[i].TagOrg+' tagId='+resultData.ResultList[i].TagId+'>'+
                  '<input type="checkbox" class="my_tag_con1_check" />'+
                  '<span class="my_tag_con1_span">'+resultData.ResultList[i].TagName+'</span>'+
                '</li>';
      $(".my_tag_con").append(label); 
    }
  }
  
  //3.点击上传文件
  $(".upl_wj").on("click",function(){
    $(".yp_mz").val("");//清空放文件名的input框
    $(".upl_file").click();
  });
  $(".upl_file").change(function(){
    uploadType=1;
    $(".uploadStatus").hide();
    var oMyForm = new FormData();
    var filePath=$(this).val();
    var _this=$(this);
    var arr=filePath.split('\\');
    var fileName=arr[arr.length-1];
    $(".yp_mz").val(fileName);
    $(".sonProgress,.parentProgress").show();
    $(".cancelUpload").show();
    oMyForm.append("ContentFile", $(this)[0].files[0]);
    oMyForm.append("DeviceId", "3279A27149B24719991812E6ADBA5584");
    oMyForm.append("MobileClass", "Chrome");
    oMyForm.append("PCDType", "3");
    oMyForm.append("UserId", "123");
    oMyForm.append("SrcType", "2");
    oMyForm.append("Purpose", "1");
    if(($(this)[0].files[0].size)/1048576>100){//判断文件大小是否大于100M
      alert("文件过大，请选择合适的文件上传！");
      $(".yp_mz").val("");
    }else{
      requestUpload(_this,oMyForm,uploadType);//请求上传文件
    }
  });
  
  //4.点击上传图片
  $(".upl_pt_img").on("click",function(){
    var bb=$(window.parent.document).find("#myIframe").attr({"src":"photoClip.html"});
  });
//$(".upl_pt_img").on("click",function(){
//  $(".upl_img").click();
//});
//$(".upl_img").change(function(){
//  uploadType=2;
//  $(".img_uploadStatus").hide();
//  //图片预览
//  if($(".defaultImg").css("display")!="none"){
//    $(".defaultImg").css({"display":"none"});
//  }
//  var fileReader = new FileReader();
//  fileReader.onload = function(evt){
//    if(FileReader.DONE==fileReader.readyState){
//      var newImg =  $("<img class='newImg' alt='front cover' />");
//      newImg.attr({"src":this.result});//是Base64的data url数据
//      if($(".previewImg").children().length>1){
//        $(".previewImg img:last").replaceWith(newImg);
//      }else{
//        $(".previewImg").append(newImg);
//      }
//    }
//  }
//  fileReader.readAsDataURL($(this)[0].files[0]);
//  var oMyForm = new FormData();
//  var filePath=$(this).val();
//  var _this=$(this);
//  var arr=filePath.split('\\');
//  var fileName=arr[arr.length-1];
//  oMyForm.append("ContentFile", $(this)[0].files[0]);
//  oMyForm.append("DeviceId", "3279A27149B24719991812E6ADBA5584");
//  oMyForm.append("MobileClass", "Chrome");
//  oMyForm.append("PCDType", "3");
//  oMyForm.append("UserId", "123");
//  oMyForm.append("SrcType", "1");
//  oMyForm.append("Purpose", "2");
//  if(($(this)[0].files[0].size)/1048576>1){//判断图片大小是否大于1M
//    alert("图片过大，请选择合适的图片上传！");
//  }else{
//    requestUpload(_this,oMyForm,uploadType);
//  }
//});
  
  //5.请求上传文件
  function requestUpload(_this,oMyForm,uploadType){
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
      success: function (opeResult){
        if(opeResult.ful[0].success=="TRUE"){
          _this.attr("value",opeResult.ful[0].FilePath);
          $(".cancelUpload").hide();
          if(uploadType=="1") $(".uploadStatus").show();
          if(uploadType=="2") $(".img_uploadStatus").show();
        }else{
          alert(opeResult.err);
        }
      },
      error: function(XHR){
        alert("发生错误" + jqXHR.status);
      }
    });
  };
  //侦查文件上传情况,,这个方法大概0.05-0.1秒执行一次
  function onprogress(evt){
    var loaded = evt.loaded;     //已经上传大小情况 
    var tot = evt.total;      //文件总大小 
    var per = Math.floor(100*loaded/tot);  //已经上传的百分比 
    $(".sonProgress").html( per +"%" );
    $(".sonProgress").css("width" , per +"%");
  }
  //因网速较慢，取消正在上传的文件
  /*注：关于取消此时正在请求的ajax的问题未解决，暂时先不做*/
  $(document).on("click",".cancelUpload",function(){
    var gnl=confirm("你确定要取消正在上传的文件吗?");
    if (gnl==true){
      $(".sonProgress,.parentProgresshide,.cancelUpload").hide();
      $(".upl_file").attr("value","");
    }else{
      return false;
    }
  })
  
  //6.获取选择专辑列表
  $.ajax({
    type:"POST",
    url:rootPath+"content/seq/getSeqMediaList.do",
    dataType:"json",
    data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
          "MobileClass":"Chrome",
          "PCDType":"3",
          "UserId":"123",
          "FlagFlow":"0",
          "ChannelId":"0",
          "ShortSearch":"false"
    },
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getAlbumList(resultData); //得到专辑列表,上传节目时使用
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //6.1得到专辑列表
  function getAlbumList(resultData){
    for(var i=0;i<resultData.ResultList.length;i++){
      var option='<option value="" id='+resultData.ResultList[i].ContentId+'>'+resultData.ResultList[i].ContentName+'</option>';
      $(".upl_zj").append(option);
    }
  }
  
  //7.获取创作方式列表
  $.ajax({
    type:"POST",
    url:rootPath+"baseinfo/getCataTree4View.do",
    dataType:"json",
    data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
          "MobileClass":"Chrome",
          "PCDType":"3",
          "UserId":"123",
          "CatalogType":"4",
          "TreeViewType":"zTree"
    },
    success:function(resultData){
      if(resultData.ReturnType == "1001"){
        getArtMethodList(resultData); //得到创作方式列表
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //7.1得到创作方式列表
  function getArtMethodList(resultData){
    for(var i=0;i<resultData.Data.children.length;i++){
      var option='<option value="" id='+resultData.Data.children[i].id+'>'+resultData.Data.children[i].name+'</option>';
      $(".change_czfs").append(option);
    }
  }
  
  //7.2点击确认按钮，添加创作方式
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
  
  //7.3取消新添加的创作方式
  $(document).on("click",".cancelImg",function(){
    $(this).parent(".bqImg").remove();
    $(".czfs_author_ipt").val("");
  });
  
  //8.定时发布的时间格式转换成时间戳
  function js_strto_time(str_time){
    var new_str = str_time.replace(/:/g,'-');
    new_str = new_str.replace(/ /g,'-');
    var arr = new_str.split("-");
    var datum = new Date(Date.UTC(arr[0],arr[1]-1,arr[2],arr[3]-8,arr[4],arr[5]));
    return strtotime = datum.getTime();
  }
  
  //9.点击上传节目页面上的发布按钮，发布节目
  function pub_add_jm(){
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentURI=$(".upl_file").attr("value");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
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
    _data.MemberType=memberTypelist;
    var str_time=$(".layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    _data.FlowFlag="2";
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/addMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("节目发布成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(dataParam);//重新加载节目列表
        }else{
          alert(resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //10.点击修改节目页面上的发布按钮，发布节目
  function pub_edit_jm(){
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentId=$(".jmId").val();
    _data.ContentURI=$(".upl_file").attr("value");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.SeqMediaId=$(".upl_zj option:selected").attr("id");
    var taglist=[];
    $(".upl_bq").find(".upl_bq_img").each(function(){
      var tag={};//标签对象
      var tagTxt=$(this).children("span").html();
      $(".my_tag_con1").each(function(){
        if($(this).children(".my_tag_con1_span").html()==tagTxt){
          $(".my_tag_con1").children(".my_tag_con1_check").prop("checked",false);
          $(".my_tag_con1").children(".my_tag_con1_check").attr("disabled",false);
          $(this).children(".my_tag_con1_check").prop("checked",true);
          $(this).children(".my_tag_con1_check").attr("disabled",true);
          tag.TagName=$(this).children(".my_tag_con1_span").html();
          tag.TagOrg="我的标签";
        }
      })
      $(".gg_tag_con1").each(function(){
        if($(this).children(".gg_tag_con1_span").html()==tagTxt){
          $(".gg_tag_con1").children(".gg_tag_con1_check").prop("checked",false);
          $(".gg_tag_con1").children(".gg_tag_con1_check").attr("disabled",false);
          $(this).children(".gg_tag_con1_check").prop("checked",true);
          $(this).children(".gg_tag_con1_check").attr("disabled",true);
          tag.TagName=$(this).children(".gg_tag_con1_span").html();
          tag.TagOrg="公共标签";
        }
      })
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
    _data.MemberType=memberTypelist;
    var str_time=$(".layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          pubEditJm(_data);
        }else{
          alert(resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  function pubEditJm(_data){
    var contentId=$(".jmId").val();
    var contentSeqId=_data.SeqMediaId
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaStatus.do",
      dataType:"json",
      data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
            "MobileClass":"Chrome",
            "PCDType":"3",
            "UserId":"123",
            "ContentId":contentId,
            "SeqMediaId":contentSeqId
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("节目发布成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(dataParam);//重新加载节目列表
        }else{
          alert(resultData.Message);
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //点击上传修改之前的清空
  function clear(){
    $(".mask,.add").show();
    $(".sonProgress").html(" ");
    $(".parentProgress,.sonProgress").hide();
    $("body").css({"overflow":"hidden"});
    $(".jmId,.upl_file,.upl_img").attr("value","");
    $(".uplTitle,.yp_mz,.uplDecn,.czfs_author_ipt,.layer-date").val("");
    $(".upl_bq,.czfs_tag").html("");
    $(".newImg").remove();
    $(".defaultImg").show();
    $(".img_uploadStatus,.uploadStatus").hide();
    $(".my_tag_con1,.gg_tag_con1").each(function(){
      $(this).children("input[type='checkbox']").prop("checked",false);
      $(this).children("input[type='checkbox']").attr("disabled",false);
    })
    $(".upl_zj option").each(function(){
      $(this).attr("selected",false);
    })
    $(".change_czfs option").each(function(){
      $(this).attr("selected",false);
    })
  }
  
  //点击节目的封面图片，跳到这个节目的详情页
  $(document).on("click",".rtcl_img",function(){
    var contentId=$(this).parent(".rtc_listBox").attr("contentId");
    $("#newIframe", parent.document).attr({"src":"jm_detail.html?contentId="+contentId+""});
    $("#myIframe", parent.document).hide();
    $("#newIframe", parent.document).show();
  });
});
