$(function(){
  var rootPath=getRootPath();
  var subType=1;//subType=1代表在上传节目页面保存,subType=2代表在修改节目页面保存
  var pubType=1;//pubType=1代表在上传节目页面提交,pubType=2代表在修改节目页面提交
  
  //00-1获取栏目筛选条件
  var dataF={ "DeviceId":"3279A27149B24719991812E6ADBA5584",
              "MobileClass":"Chrome",
              "UserId":"123",
              "PCDType":"3",
              "MediaType":"MediaAsset"
  };
  getFiltrates(dataF);
  function getFiltrates(data){
    $.ajax({
      type:"POST",
      url:rootPath+"content/getFiltrates.do",
      dataType:"json",
      data:JSON.stringify(data),
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
  }

  //00-2得到栏目的筛选标签
  function getChannelLabel(resultData){
    for(var i=0;i<resultData.ResultList.ChannelList.length;i++){
      var li='<li class="trig_item chnel" data_idx='+i+' id='+resultData.ResultList.ChannelList[i].id+' pid='+resultData.ResultList.ChannelList[i].parentId+'>'+
                '<div class="check_cate"></div>'+
                '<a class="ss1" href="javascript:void(0)">'+resultData.ResultList.ChannelList[i].nodeName+'</a>'+
              '</li>';
      var li_tab_ul='<ul class="tab_cont_item chnels" data_idx='+i+' data_name='+resultData.ResultList.ChannelList[i].nodeName+' pid='+resultData.ResultList.ChannelList[i].parentId+'></ul>';
      $(".attrTabcon").append(li_tab_ul);
      $("#channel .attrValues .av_ul").append(li);
      if(resultData.ResultList.ChannelList[i].isParent=="true"){
        for(var j=0;j<resultData.ResultList.ChannelList[i].children.length;j++){
          var li_tab_ul_li='<li class="trig_item_li" id='+resultData.ResultList.ChannelList[i].children[j].id+'>'+
                              '<a class="ss1" href="javascript:void(0)">'+resultData.ResultList.ChannelList[i].children[j].nodeName+'</a>'+
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
  var jmData={};
  jmData.DeviceId="3279A27149B24719991812E6ADBA5584";
  jmData.MobileClass="Chrome";
  jmData.PCDType="3";
  jmData.UserId="123";
  getContentList(jmData);
  function getContentList(obj){
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/getMediaList.do",
      dataType:"json",
      data:JSON.stringify(obj),
      beforeSend: function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载节目列表...</div>");
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          $(".ri_top3_con").html("");//每次加载之前先清空
          getMediaList(resultData); //得到节目列表
        }else{
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>没有查到任何内容</div>");//每次加载之前先清空
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //00-4.1得到节目列表
  function getMediaList(resultData){
    for(var i=0;i<resultData.ResultList.AllCount;i++){
      var programBox= '<div class="rtc_listBox" contentSeqId='+resultData.ResultList.List[i].ContentSeqId+' contentId='+resultData.ResultList.List[i].ContentId+' channelId='+channelds+'>'+
                        '<div class="rtcl_img">'+
                          '<img src='+resultData.ResultList.List[i].ContentImg+' alt="节目图片" />'+
                        '</div>'+
                        '<div class="rtcl_con">'+
                          '<h4></h4>'+
                          '<p class="zj_name"></p>'+
                          '<p class="other">'+
                            '<span>时间 ：</span>'+
                            '<span class="ctime"></span>'+
                          '</p>'+
                        '</div>'+
                        '<p class="jm_st" flowFlag='+resultData.ResultList.List[i].ContentPubChannels[0].FlowFlag+'></p>'+
                        '<div class="op_type" id="op_Box'+i+'">'+
                          '<p class="jm_edit c173">编辑</p>'+
                          '<p class="jm_pub c173">发布</p>'+
                          '<p class="jm_del c173">删除</p>'+
                          '<p class="jm_recal c173">撤回</p>'+
                        '</div>'+
                      '</div>';
      $(".ri_top3_con").append(programBox);
      $(".rtc_listBox").children(".rtcl_con").children("h4").eq(i).text(((resultData.ResultList.List[i].ContentName)?(resultData.ResultList.List[i].ContentName):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".zj_name").eq(i).text(((resultData.ResultList.List[i].ContentSeqName)?(resultData.ResultList.List[i].ContentSeqName):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".other").children(".ctime").eq(i).text(((resultData.ResultList.List[i].CTime)?(resultData.ResultList.List[i].CTime):"暂无"));
      $(".rtc_listBox").children(".jm_st").eq(i).text(((resultData.ResultList.List[i].ContentPubChannels[0].FlowFlagState)?(resultData.ResultList.List[i].ContentPubChannels[0].FlowFlagState):"未知"));
      if(resultData.ResultList.List[i].ContentPubChannels){
        var channelds="";
        for(var j=0;j<resultData.ResultList.List[i].ContentPubChannels.length;j++){
          if(channelds==""){
            channelds=resultData.ResultList.List[i].ContentPubChannels[j].ChannelId;
          }else{
            channelds+=","+resultData.ResultList.List[i].ContentPubChannels[j].ChannelId;
          }
        }
      }
      if(resultData.ResultList.List[i].ContentPubChannels[0].FlowFlag=="0"){//提交
        $("#op_Box"+i).children(".jm_edit,.jm_pub,.jm_del").removeClass("c173").addClass("cf60");
      }else if(resultData.ResultList.List[i].ContentPubChannels[0].FlowFlag=="1"){//审核
        $("#op_Box"+i).children(".jm_edit,.jm_pub,.jm_del,.jm_recal").removeClass("cf60").addClass("c173");
      }else if(resultData.ResultList.List[i].ContentPubChannels[0].FlowFlag=="2"){//发布
        $("#op_Box"+i).children(".jm_recal").removeClass("c173").addClass("cf60");
      }else if(resultData.ResultList.List[i].ContentPubChannels[0].FlowFlag=="4"){//撤回
        $("#op_Box"+i).children(".jm_edit,.jm_pub,.jm_del").removeClass("c173").addClass("cf60");
      }
    }
  }
  
  //11-1点击上传节目按钮
  $(document).on("click",".ri_top_li3",function(){
    clear();//清空数据
    subType=1;
    pubType=1;
    $(".iboxtitle h4").html("上传节目");
  });
  
  //22-1点击编辑节目按钮
   $(document).on("click",".jm_edit",function(){
    var contentId=$(this).parents(".rtc_listBox").attr("contentid");
    var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("flowFlag");
    if(flowFlag=="1"||flowFlag=="2") {//1审核2发布
      alert("当前状态不支持编辑操作");
      return;
    }else{
      subType=2;
      pubType=2;
      edit_jm(contentId);
    }
  })
  
  //33-1点击保存按钮，上传节目/修改节目
  $("#submitBtn").on("click",function(){
    if(subType=="1")  save_add_jm();
    if(subType=="2")  save_edit_jm();
  })
  
  //55-1点击提交按钮，上传节目/修改节目
  $("#pubBtn").on("click",function(){
    if(pubType=="1") pub_add_jm();
    if(pubType=="2") pub_edit_jm();
  })
  
  //33-1.1上传节目方法
  function save_add_jm(){
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentURI=$(".audio").attr("src");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.SeqMediaId=$(".upl_zj option:selected").attr("id");
    _data.TimeLong=$(".timeLong").attr("value");
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
    _data.FlowFlag="1";
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/addMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend: function(){
        $(".btn_group input").attr("disabled","disabled");
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("新增节目成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert(resultData.Message);
        }
        $(".btn_group input").removeAttr("disabled");
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //33-1.2保存编辑后的信息
  function save_edit_jm(){
    if(!$(".upl_img").attr("value")){
      $(".upl_img").attr("value",$(".defaultImg").attr("src"));
    }
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentId=$(".jmId").val();
    _data.ContentURI=$(".audio").attr("src");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value")
    _data.SeqMediaId=$(".upl_zj option:selected").attr("id");
    _data.TimeLong=$(".timeLong").attr("value");
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
      beforeSend: function(){
        $(".btn_group input").attr("disabled","disabled");
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("修改节目成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert(resultData.Message);
        }
        $(".btn_group input").removeAttr("disabled");
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //22-1.1请求编辑节目时保存的信息
  function edit_jm(contentId){
    var _data={"DeviceId":"3279A27149B24719991812E6ADBA5584",
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":"123",
               "ContentId":contentId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/getMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          clear();//填充前清空数据
          $("body").css({"overflow":"hidden"});
          getTime();
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
    $(".jmId").attr("value",resultData.Result.ContentId);
    $(".iboxtitle h4").html("修改节目");
    $(".yp_mz").val("aa.mp3");//数据库没有存这一字段，因为有需要，我自己加上的
    $(".upl_file").attr("value",resultData.Result.ContentPlay);
    $(".audio").attr("src",resultData.Result.ContentPlay);
    $(".uplTitle").val(resultData.Result.ContentName);
    $(".defaultImg").attr("src",resultData.Result.ContentImg);
    $(".upl_img").attr("value",resultData.Result.ContentImg);
    $(".upl_zj option").each(function(){
      if($(this).attr("id")==resultData.Result.ContentSeqId){
        $(".upl_zj option").prop("selected",false);
        $(this).prop("selected",true); 
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
          if($(this).attr("tagId")==tagId){
            $(this).children("input").prop("checked",true);
            $(this).children("input").prop("disabled",true);
          }
        })
        $(".gg_tag_con").find(".gg_tag_con1").each(function(){
          if($(this).attr("tagId")==tagId){
            $(this).children("input").prop("checked",true);
            $(this).children("input").prop("disabled",true);
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
    var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("flowFlag");
    if(flowFlag=="0"||flowFlag=="4") {//0提交4撤回
      $('.shade', parent.document).show();
      var contentId=$(this).parents(".rtc_listBox").attr("contentid");
      var contentSeqId=$(this).parents(".rtc_listBox").attr("contentseqid");
      del_jm(contentId,contentSeqId);
    }else{
      alert("当前节目不支持删除操作");
      return;
    }
  })
  function del_jm(contentId,contentSeqId){
    var _data={"DeviceId":"3279A27149B24719991812E6ADBA5584",
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":"123",
               "ContentId":contentId,
               "SeqMediaId":contentSeqId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/removeMedia.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("成功删除节目");
          $('.shade', parent.document).hide();
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
     var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("flowFlag");
     if(flowFlag=="1"||flowFlag=="2") {//1审核2发布
      alert("当前状态不支持发布操作");
      return;
    }else{
      var contentId=$(this).parents(".rtc_listBox").attr("contentid");
      var contentSeqId=$(this).parents(".rtc_listBox").attr("contentseqid");
      $('.shade', parent.document).show();
      pub_jm(contentId,contentSeqId);
    }
  })
  function pub_jm(contentId,contentSeqId){
    var _data={"DeviceId":"3279A27149B24719991812E6ADBA5584",
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":"123",
               "ContentId":contentId,
               "SeqMediaId":contentSeqId,
               "ContentFlowFlag":"2"
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaStatus.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("节目发布成功");
          $('.shade', parent.document).hide();
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
  
  //66-1点击撤回节目按钮
  $(document).on("click",".jm_recal",function(){
    var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("flowFlag");
    if(flowFlag=="2"||flowFlag=="0"){//0提交1审核2发布4撤回
      $('.shade', parent.document).show();
      var contentId=$(this).parents(".rtc_listBox").attr("contentid");
      var seqId=$(this).parents(".rtc_listBox").attr("contentseqid");
      recal_jm(contentId,seqId);
    }else{
      alert("当前节目不支持撤回操作");
      return;
    }
  })
  function recal_jm(contentId,seqId){
    var _data={"DeviceId":"3279A27149B24719991812E6ADBA5584",
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":"123",
               "ContentId":contentId,
               "SeqMediaId":seqId,
               "ContentFlowFlag":"4"
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaStatus.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("撤回节目请求已发送");
          $('.shade', parent.document).hide();
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
          getPubLabel(resultData);//得到上传节目页面公共标签元素
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //1.1得到上传节目页面公共标签元素
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
  
  //1.2请求更换一批公共标签
  $(document).on("click",".gg_tag .hyp",function(){
    loadPubTag(data1);
  })
  
  //2上传节目页面获取我的标签
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
          getMyLabel(resultData);//得到上传节目页面我的标签元素
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //2.1得到上传节目页面我的标签元素
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
  
  //2.2请求更换一批我的标签
  $(document).on("click",".my_tag .hyp",function(){
    loadMyTag(data2);
  })
  
  //3.点击上传文件
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
  };
  
  function getTime() {
    setTimeout(function () {
      var duration = $(".audio")[0].duration;
      if(isNaN(duration)){//检查参数是否是非数字
        getTime();
      }else{
        console.info("该歌曲的总时间为："+$(".audio")[0].duration+"秒");
        var time=$(".audio")[0].duration;
        var timeLong=parseInt(time);
        $(".timeLong").attr("value",timeLong);
      }
    }, 10);
  }
  
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
//$(document).on("click",".cancelUpload",function(){
//  var gnl=confirm("你确定要取消正在上传的文件吗?");
//  if (gnl==true){
//    $(".sonProgress,.parentProgresshide,.cancelUpload").hide();
//    $(".upl_file").attr("value","");
//  }else{
//    return false;
//  }
//})
  
  //6.获取选择专辑列表
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
      $(".czfs_author_ipt").val("");
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
    _data.ContentURI=$(".audio").attr("src");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.SeqMediaId=$(".upl_zj option:selected").attr("id");
    _data.TimeLong=$(".timeLong").attr("value");
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
      beforeSend: function(){
        $(".btn_group input").attr("disabled","disabled");
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("节目发布成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert(resultData.Message);
        }
        $(".btn_group input").removeAttr("disabled");
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
    _data.ContentURI=$(".audio").attr("src");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.SeqMediaId=$(".upl_zj option:selected").attr("id");
    _data.TimeLong=$(".timeLong").attr("value");
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
      beforeSend: function(){
        $(".btn_group input").attr("disabled","disabled");
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          pubEditJm(_data);
        }else{
          alert(resultData.Message);
        }
        $(".btn_group input").removeAttr("disabled");
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  function pubEditJm(_data){
    var contentId=$(".jmId").val();
    var contentSeqId=_data.SeqMediaId;
    var data4={"DeviceId":"3279A27149B24719991812E6ADBA5584",
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":"123",
               "ContentId":contentId,
               "SeqMediaId":contentSeqId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaStatus.do",
      dataType:"json",
      data:JSON.stringify(data4),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("节目发布成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
    $(".sonProgress").html(" ");
    $(".parentProgress,.sonProgress").hide();
    $("body").css({"overflow":"hidden"});
    $(".jmId,.upl_file,.upl_img,.timeLong").attr({"value":""});
    $(".audio").attr("src","");
    $(".uplTitle,.yp_mz,.uplDecn,.czfs_author_ipt,.layer-date").val("");
    $(".upl_bq,.czfs_tag").html("");
    $(".newImg").remove();
    $(".defaultImg").attr({"src":"http://wotingfm.com:908/CM/resources/images/default.png"}).show();
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
    $(".mask,.add").show();
  }
  
  //点击节目的封面图片，跳到这个节目的详情页
  $(document).on("click",".rtcl_img",function(){
    var contentId=$(this).parent(".rtc_listBox").attr("contentId");
    $("#newIframe", parent.document).attr({"src":"jm_detail.html?contentId="+contentId});
    $("#myIframe", parent.document).hide();
    $("#newIframe", parent.document).show();
  });
  
  /*根据不同的筛选条件得到不同的节目列表*/
  $(document).on("click",".trig_item,.trig_item_li,.cate_img",function(){//选中或取消某个筛选条件
    destroy(jmData);
    jmData.DeviceId="3279A27149B24719991812E6ADBA5584";
    jmData.MobileClass="Chrome";
    jmData.PCDType="3";
    jmData.UserId="123";
    if($(".new_cate li").size()>"0"){
      $(document).find(".new_cate li").each(function(){
        var pId=$(this).attr("pid");
        var id=$(this).attr("id");
        if(pId=="status"){
          jmData.FlowFlag=$(this).attr("id");
        }else if(pId=="album"){
          jmData.SeqMediaId=$(this).attr("id");
        }else if(pId=="channel"){
          jmData.ChannelId=$(this).attr("id");
        }
      });
    }
    getContentList(jmData);
  });
});
