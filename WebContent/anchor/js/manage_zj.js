$(function(){
  var rootPath=getRootPath();
  var subType=1;//subType=1代表在创建专辑页面提交,subType=2代表在修改专辑页面提交
  var pubType=1;//pubType=1代表在创建专辑页面提交,pubType=2代表在修改专辑页面提交
  
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
      }
    },
    error:function(XHR){
      alert("发生错误："+ jqXHR.status);
    }
  });
  
  //00-2得到栏目的筛选标签
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
  
  //00-3获取专辑列表
  var dataParam={"url":rootPath+"content/seq/getSeqMediaList.do","data":{"DeviceId":"3279A27149B24719991812E6ADBA5584","MobileClass":"Chrome","UserId":"123","PCDType":"3","FlagFlow":"0","ChannelId":"0","ShortSearch":"false"}};
  getContentList(dataParam);
  function getContentList(obj){
    $.ajax({
      type:"POST",
      url:obj.url,
      dataType:"json",
      data:JSON.stringify(obj.data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          getSeqMediaList(resultData); //得到专辑列表
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //00-3.1得到专辑列表
  function getSeqMediaList(resultData){
    $(".ri_top3_con").html("");//加载专辑列表时候先清空之前的内容
    for(var i=0;i<resultData.ResultList.length;i++){
      var chas = resultData.ResultList[i].ContentPubChannels;
      var status = '';
      if(!chas) status ='不存在';
      else status = chas[0].FlowFlagState;
      var albumBox= '<div class="rtc_listBox" contentId='+resultData.ResultList[i].ContentId+'>'+
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
                      '<p class="zj_st">'+status+'</p>'+
                      '<div class="op_type">'+
                        '<p class="zj_edit">编辑</p>'+
                        '<p class="zj_pub">发布</p>'+
                        '<p class="zj_del">删除</p>'+
                        '<p class="zj_recal">撤回</p>'+
                      '</div>'+
                    '</div>';
      $(".ri_top3_con").append(albumBox);              
    }
  }
  
  //11-1点击创建专辑按钮
  $(document).on("click",".ri_top_li3",function(){
    clear();//清空数据
    subType=1;
    pubType=1;
  });
  
  //22-1点击编辑专辑按钮
   $(document).on("click",".zj_edit",function(){
    var contentId=$(this).parents(".rtc_listBox").attr("contentid");
    subType=2;
    pubType=2;
    edit_zj(contentId);
  })
   
  //33-1点击提交按钮，创建专辑/修改专辑
  $("#submitBtn").on("click",function(){
    if(subType=="1")  add_zj();
    if(subType=="2")  save_edit_zj();
  })
  
  //55-1点击发布按钮，创建专辑/修改专辑
  $("#pubBtn").on("click",function(){
    if(pubType=="1") pub_add_zj();
    if(pubType=="2") pub_edit_zj();
  })
  
  //33-1.1创建专辑方法
  function add_zj(){
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.ChannelId=$(".upl_zj option:selected").attr("id");
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
    var str_time=$(".layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    console.log(_data);
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/addSeqMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("创建专辑成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(dataParam);//重新加载专辑列表
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //33-1.2保存编辑后的信息
  function save_edit_zj(){
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentName=$(".uplTitle").val();
    _data.ContentId=$(".zjId").attr("value");
    _data.ContentImg=$(".upl_img").attr("value");
    _data.ChannelId=$(".upl_zj option:selected").attr("id");
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
    var str_time=$(".layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/updateSeqMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("专辑信息修改成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(dataParam);//重新加载专辑列表
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //22-1.1请求编辑专辑时保存的信息
  function edit_zj(contentId){
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/getSeqMediaInfo.do",
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
          fillZjContent(resultData);//填充专辑信息
        }else{
          alert(resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //22-1.2填充专辑信息
  function fillZjContent(resultData){
    clear();//填充前清空数据
    $(".iboxtitle h4").html("修改专辑");
    $(".zjId").attr("value",resultData.Result.ContentId);
    $(".uplTitle").val(resultData.Result.ContentName);
    $(".defaultImg").attr("src",resultData.Result.ContentImg);
    if(resultData.Result.ContentKeyWords!=null){
      for(var i=0;i<resultData.Result.ContentKeyWords.length;i++){
        var new_tag= '<li class="upl_bq_img bqImg" tagId='+resultData.Result.ContentKeyWords[i].TagId+'>'+
                      '<span>'+resultData.Result.ContentKeyWords[i].TagName+'</span>'+
                      '<img class="upl_bq_cancelimg1 cancelImg" src="img/upl_img2.png" alt="" />'+
                    '</li>';
        $(".upl_bq").append(new_tag);
        var tagId=resultData.Result.ContentKeyWords[i].TagId;
        $(".my_tag_con1").each(function(){
          if($(this).attr("tagid")==tagId){
            $(this).children("input[type='checkbox']").prop("checked",true);
            $(this).children("input[type='checkbox']").attr("disabled",true);
          }
        })
        $(".gg_tag_con1").each(function(){
          if($(this).attr("tagid")==tagId){
            $(this).children("input").prop("checked",true);
            $(this).children("input").attr("disabled",true);
          }
        })
      }
    }
    $(".upl_zj option").each(function(){
      if($(this).attr("id")==resultData.Result.ContentPubChannels[0].ChannelId){
        $(".upl_zj option").attr("selected",false);
        $(this).attr("selected",true); 
      }
    })
    $(".uplDecn").val(resultData.Result.ContentDesc);
    $(".layer-date").val(resultData.Result.CTime);
  }
  
  //44-1点击删除专辑按钮
  $(document).on("click",".zj_del",function(){
    $('.shade', parent.document).show();
    var contentId=$(this).parents(".rtc_listBox").attr("contentid");
    del_zj(contentId);
  })
  function del_zj(contentId){
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/removeSeqMedia.do",
      dataType:"json",
      data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
            "MobileClass":"Chrome",
            "PCDType":"3",
            "UserId":"123",
            "ContentId":contentId
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("成功删除专辑");
          $('.shade', parent.document).hide();
          getContentList(dataParam);//重新加载专辑列表
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //55-1点击发布节目按钮
  $(document).on("click",".zj_pub",function(){
    $('.shade', parent.document).show();
    var contentId=$(this).parents(".rtc_listBox").attr("contentid");
    pub_zj(contentId);
  })
  function pub_zj(contentId){
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/updateSeqMediaStatus.do",
      dataType:"json",
      data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
            "MobileClass":"Chrome",
            "PCDType":"3",
            "UserId":"123",
            "ContentId":contentId
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("专辑发布成功");
          $('.shade', parent.document).hide();
          getContentList(dataParam);//重新加载专辑列表
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
  //1创建专辑页面获取公共标签
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
        var newImg =  $("<img class='newImg' alt='front cover' />");
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
    oMyForm.append("DeviceId", "3279A27149B24719991812E6ADBA5584");
    oMyForm.append("MobileClass", "Chrome");
    oMyForm.append("PCDType", "3");
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
          $(".img_uploadStatus").show();
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
  
  //5.获得栏目的分类信息
  $.ajax({
    url:rootPath+"common/getChannelTreeWithSelf.do",
    type:"POST",
    cache: false,
    processData: false,
    contentType: false,
    dataType:"json",
    //表单提交前进行验证
    success: function (resultData){
      if(resultData.jsonType == "1"){
        getChannelList(resultData);//得到栏目分类列表
      }
    },
    error: function(XHR){
      alert("发生错误" + jqXHR.status);
    }
  });
  
  //5.1得到专辑分类列表
  function getChannelList(resultData){
    for(var i=0;i<resultData.data.children.length;i++){
      var option='<option value="" id='+resultData.data.children[i].id+'>'+resultData.data.children[i].name+'</option>';
      $(".upl_zj").append(option);
    }
  }
  
  //6.定时发布的时间格式转换成时间戳
  function js_strto_time(str_time){
    var new_str = str_time.replace(/:/g,'-');
    new_str = new_str.replace(/ /g,'-');
    var arr = new_str.split("-");
    var datum = new Date(Date.UTC(arr[0],arr[1]-1,arr[2],arr[3]-8,arr[4],arr[5]));
    return strtotime = datum.getTime();
  }

  //7.点击创建专辑页面上的发布按钮，发布专辑
  function pub_add_zj(){
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.ChannelId=$(".upl_zj option:selected").attr("id");
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
    var str_time=$(".layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/addSeqMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          pubAddOrEditZj(_data);
        }else{
          alert(resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //8.点击修改专辑页面上的发布按钮，发布专辑
  function pub_edit_zj(){
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.ContentId=$(".zjId").attr("value");
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
    var str_time=$(".layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/updateSeqMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          pubAddOrEditZj(_data);
        }else{
          alert(resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  function pubAddOrEditZj(_data){
    var contentId=$(".zjId").attr("value");
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/updateSeqMediaStatus.do",
      dataType:"json",
      data:{"DeviceId":"3279A27149B24719991812E6ADBA5584",
            "MobileClass":"Chrome",
            "PCDType":"3",
            "UserId":"123",
            "ContentId":contentId
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("专辑发布成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(dataParam);//重新加载专辑列表
        }else{
          alert(resultData.Message);
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
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
    $("body").css({"overflow":"hidden"});
    $(".upl_img").attr("value","");
    $(".zjId,.uplTitle,.uplDecn,.layer-date").val("");
    $(".upl_bq").html("");
    $(".newImg").remove();
    $(".defaultImg").show();
    $(".img_uploadStatus").hide();
    $(".my_tag_con1,.gg_tag_con1").each(function(){
      $(this).children("input[type='checkbox']").prop("checked",false);
      $(this).children("input[type='checkbox']").attr("disabled",false);
    })
    $(".upl_zj option").each(function(){
      $(this).attr("selected",false);
    })
  }
  
  //点击专辑的封面图片，跳到这个专辑的详情页
  $(document).on("click",".rtcl_img",function(){
    var contentId=$(this).parent(".rtc_listBox").attr("contentId");
    $("#newIframe", parent.document).attr({"src":"zj_detail.html?contentId="+contentId+""});
    $("#myIframe", parent.document).hide();
    $("#newIframe", parent.document).show();
  });
});
