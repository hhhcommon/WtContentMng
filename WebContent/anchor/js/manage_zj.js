$(function(){
  var rootPath=getRootPath();
  var subType=1;//subType=1代表在创建专辑页面提交,subType=2代表在修改专辑页面提交
  var pubType=1;//pubType=1代表在创建专辑页面提交,pubType=2代表在修改专辑页面提交
  
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
  
  //00-3获取专辑列表
  var dataParam={"DeviceId":"3279A27149B24719991812E6ADBA5584","MobileClass":"Chrome","UserId":"123","PCDType":"3","FlagFlow":"0","ChannelId":"0","ShortSearch":"false"};
  getContentList(dataParam);
  function getContentList(obj){
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/getSeqMediaList.do",
      dataType:"json",
      data:JSON.stringify(obj),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          $(".ri_top3_con").html("");//每次加载之前先清空
          getSeqMediaList(resultData); //得到专辑列表
        }else{
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;'>没有查到任何内容</div>");//每次加载之前先清空
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
                      '<p class="zj_st" flowFlag='+resultData.ResultList[i].ContentPubChannels[0].FlowFlag+'>'+status+'</p>'+
                      '<div class="op_type" id="op_Box'+i+'">'+
                        '<p class="zj_edit c173">编辑</p>'+
                        '<p class="zj_pub c173">发布</p>'+
                        '<p class="zj_del c173">删除</p>'+
                        '<p class="zj_recal c173">撤回</p>'+
                      '</div>'+
                    '</div>';
      $(".ri_top3_con").append(albumBox);
      if(resultData.ResultList[i].ContentPubChannels[0].FlowFlag=="0"){//提交
        $("#op_Box"+i).children(".zj_edit,.zj_pub,.zj_del").removeClass("c173").addClass("cf60");
      }else if(resultData.ResultList[i].ContentPubChannels[0].FlowFlag=="1"){//审核
        $("#op_Box"+i).children(".zj_edit,.zj_pub,.zj_del,.zj_recal").removeClass("cf60").addClass("c173");
      }else if(resultData.ResultList[i].ContentPubChannels[0].FlowFlag=="2"){//发布
        $("#op_Box"+i).children(".zj_recal").removeClass("c173").addClass("cf60");
      }else if(resultData.ResultList[i].ContentPubChannels[0].FlowFlag=="3"){//撤回
        $("#op_Box"+i).children(".zj_edit,.zj_pub,.zj_del").removeClass("c173").addClass("cf60");
      }else if(resultData.ResultList[i].ContentPubChannels[0].FlowFlag=="4"){//未通过
        $("#op_Box"+i).children(".zj_edit,.zj_del").removeClass("c173").addClass("cf60");
      }
    }
  }
  
  //11-1点击创建专辑按钮
  $(document).on("click",".ri_top_li3",function(){
    clear();//清空数据
    subType=1;
    pubType=1;
    $(".iboxtitle h4").html("创建专辑");
  });
  
  //22-1点击编辑专辑按钮
   $(document).on("click",".zj_edit",function(){
    var contentId=$(this).parents(".rtc_listBox").attr("contentid");
    var flowFlag=$(this).parent(".op_type").siblings(".zj_st").attr("flowFlag");
    if(flowFlag=="1"||flowFlag=="2") {//0提交1审核2发布3撤回
      alert("当前状态不支持编辑操作");
      return;
    }else{
      subType=2;
      pubType=2;
      edit_zj(contentId);
    }
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
    var chIdstr="";
    $(".channelBox li").each(function(){
      var ids=$(this).attr("id");
      if(chIdstr==""){
        chIdstr=ids;
      }else{
        chIdstr+=","+ids;
      }
    })
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.ChannelId=chIdstr;
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
          alert("创建专辑成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(dataParam);//重新加载专辑列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
  function save_edit_zj(){
    var chIdstr="";
    $(".channelBox li").each(function(){
      var ids=$(this).attr("id");
      if(chIdstr==""){
        chIdstr=ids;
      }else{
        chIdstr+=","+ids;
      }
    })
    if(!$(".upl_img").attr("value")){
      $(".upl_img").attr("value",$(".defaultImg").attr("src"));
    }
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentName=$(".uplTitle").val();
    _data.ContentId=$(".zjId").attr("value");
    _data.ContentImg=$(".upl_img").attr("value");
    _data.ChannelId=chIdstr;
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
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //22-1.1请求编辑专辑时保存的信息
  function edit_zj(contentId){
    var _data={ "DeviceId":"3279A27149B24719991812E6ADBA5584",
                "MobileClass":"Chrome",
                "PCDType":"3",
                "UserId":"123",
                "ContentId":contentId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/getSeqMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
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
    $(".channelBox").html("");
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
            $(this).children("input[type='checkbox']").prop("disabled",true);
          }
        })
        $(".gg_tag_con1").each(function(){
          if($(this).attr("tagid")==tagId){
            $(this).children("input").prop("checked",true);
            $(this).children("input").prop("disabled",true);
          }
        })
      }
    }
    if(resultData.Result.ContentPubChannels!=null){
      for(var i=0;i<resultData.Result.ContentPubChannels.length;i++){
        var li='<li class="channel_bq bqImg" id='+resultData.Result.ContentPubChannels[i].ChannelId+'>'+
                  '<span>'+resultData.Result.ContentPubChannels[i].ChannelName+'</span>'+
                  '<img class="channel_bq_cancelimg1 cancelImg" src="img/upl_img2.png" alt="" style="display: none;">'+
                '</li>';
        $(".channelBox").append(li);
      }
    }
    $(".uplDecn").val(resultData.Result.ContentDesc);
    $(".layer-date").val(resultData.Result.CTime);
  }
  
  //44-1点击删除专辑按钮
  $(document).on("click",".zj_del",function(){
    var flowFlag=$(this).parent(".op_type").siblings(".zj_st").attr("flowFlag");
    if(flowFlag=="0"||flowFlag=="3"){//0提交1审核2发布3撤回
      $('.shade', parent.document).show();
      var contentId=$(this).parents(".rtc_listBox").attr("contentid");
      del_zj(contentId);
    }else{
      alert("当前专辑不支持删除操作");
      return;
    }
  })
  function del_zj(contentId){
    var _data={ "DeviceId":"3279A27149B24719991812E6ADBA5584",
                "MobileClass":"Chrome",
                "PCDType":"3",
                "UserId":"123",
                "ContentId":contentId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/removeSeqMedia.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("成功删除专辑");
          $('.shade', parent.document).hide();
          getContentList(dataParam);//重新加载专辑列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //55-1点击发布节目按钮
  $(document).on("click",".zj_pub",function(){
    var flowFlag=$(this).parent(".op_type").siblings(".zj_st").attr("flowFlag");
    if(flowFlag=="1"||flowFlag=="2") {//0提交1审核2发布3撤回
      alert("当前状态不支持发布操作");
      return;
    }else{
      $('.shade', parent.document).show();
      var contentId=$(this).parents(".rtc_listBox").attr("contentid");
      pub_zj(contentId);
    }
  })
  function pub_zj(contentId){
    var _data={ "DeviceId":"3279A27149B24719991812E6ADBA5584",
                "MobileClass":"Chrome",
                "PCDType":"3",
                "UserId":"123",
                "ContentId":contentId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/updateSeqMediaStatus.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("专辑发布成功");
          $('.shade', parent.document).hide();
          getContentList(dataParam);//重新加载专辑列表
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
  
  //66-1点击撤回专辑按钮
  $(document).on("click",".zj_recal",function(){
    var flowFlag=$(this).parent(".op_type").siblings(".zj_st").attr("flowFlag");
    if(flowFlag=="2"){//0提交1审核2发布3撤回
      $('.shade', parent.document).show();
      var contentId=$(this).parents(".rtc_listBox").attr("contentid");
      var channelId=$(this).parents(".rtc_listBox").attr("channelid");
      recal_zj(contentId,channelId,flowFlag);
    }else{
      alert("当前专辑不支持撤回操作");
      return;
    }
  })
  function recal_zj(contentId,channelId,flowFlag){
    var _data={"DeviceId":"3279A27149B24719991812E6ADBA5584",
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":"123",
               "MediaType": "SEQU",
               "ContentId":contentId,
               "ChannelId":channelId,
               "ContentFlowFlag":flowFlag,
               "OpeType":"revoke"
    };
    console.log(_data);
    $.ajax({
      type:"POST",
      url:rootPath+"content/updateContentStatus.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("成功撤回专辑");
          $('.shade', parent.document).hide();
          getContentList(dataParam);//重新加载专辑列表
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
  //1创建专辑页面获取公共标签
  var data1={ "DeviceId":"3279A27149B24719991812E6ADBA5584",
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
          getPubLabel(resultData);//得到创建专辑页面公共标签元素
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //1.1得到创建专辑页面公共标签元素
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
  
  //2.创建专辑页面获取我的标签
  var data2={ "DeviceId":"3279A27149B24719991812E6ADBA5584",
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
          getMyLabel(resultData);//得到创建专辑页面我的标签元素
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //2.1得到创建专辑页面我的标签元素
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
        getChannelList(resultData);//得到栏目栏目列表
      }
    },
    error: function(XHR){
      alert("发生错误" + jqXHR.status);
    }
  });
  
  //5.1得到专辑栏目列表
  function getChannelList(resultData){
    for(var i=0;i<resultData.data.children.length;i++){
      var ss=resultData.data.children[i].attributes.nPy.substring(0,1);
      switch(ss){
        case "A":case "B":case "C":case "D":case "E":case "F":case "G":
          var li='<li value="" id='+resultData.data.children[i].id+'>'+resultData.data.children[i].name+'</li>';
          $(".cul_ag").append(li);
          break;
        case "H":case "I":case "J":case "K":case "L":
          var li='<li value="" id='+resultData.data.children[i].id+'>'+resultData.data.children[i].name+'</li>';
          $(".cul_hl").append(li);
          break;
        case "M":case "N":case "O":case "P":case "Q":case "R":case "S":
          var li='<li value="" id='+resultData.data.children[i].id+'>'+resultData.data.children[i].name+'</li>';
          $(".cul_ms").append(li);
          break;
        case "T":case "U":case "V":case "W":case "X":case "Y":case "Z":
          var li='<li value="" id='+resultData.data.children[i].id+'>'+resultData.data.children[i].name+'</li>';
          $(".cul_tz").append(li);
          break;
        default:
          break;
      }
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
    var chIdstr="";
    $(".channelBox li").each(function(){
      var ids=$(this).attr("id");
      if(chIdstr==""){
        chIdstr=ids;
      }else{
        chIdstr+=","+ids;
      }
    })
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.ChannelId=chIdstr;
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
    var chIdstr="";
    $(".channelBox li").each(function(){
      var ids=$(this).attr("id");
      if(chIdstr==""){
        chIdstr=ids;
      }else{
        chIdstr+=","+ids;
      }
    })
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.ContentId=chIdstr;
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
    var _data={ "DeviceId":"3279A27149B24719991812E6ADBA5584",
                "MobileClass":"Chrome",
                "PCDType":"3",
                "UserId":"123",
                "ContentId":contentId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/updateSeqMediaStatus.do",
      dataType:"json",
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("专辑发布成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(dataParam);//重新加载专辑列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
    $("body").css({"overflow":"hidden"});
    $(".upl_img").attr("value","");
    $(".zjId,.uplTitle,.uplDecn,.layer-date").val("");
    $(".upl_bq").html("");
    $(".newImg").remove();
    $(".defaultImg").attr({"src":"http://wotingfm.com:908/CM/resources/images/default.png"}).show();
    $(".img_uploadStatus").hide();
    $(".my_tag_con1,.gg_tag_con1").each(function(){
      $(this).children("input[type='checkbox']").prop("checked",false);
      $(this).children("input[type='checkbox']").attr("disabled",false);
    })
    $(".channelBox").html("");
    $(".cBox1_conS").html("<li class='center'>还未选择一级栏目</li>");
    $(".channelBox1 li").removeClass("selectedF");
    $(".channelBox1").hide();
    $(".mask,.add").show();
  }
  
  //点击专辑的封面图片，跳到这个专辑的详情页
  $(document).on("click",".rtcl_img",function(){
    var contentId=$(this).parent(".rtc_listBox").attr("contentId");
    $("#newIframe", parent.document).attr({"src":"zj_detail.html?contentId="+contentId});
    $("#myIframe", parent.document).hide();
    $("#newIframe", parent.document).show();
  });
  
  /*根据不同的筛选条件得到不同的专辑列表*/
  var zjData={};
  zjData.DeviceId="3279A27149B24719991812E6ADBA5584";
  zjData.MobileClass="Chrome";
  zjData.PCDType="3";
  zjData.UserId="123";
  $(document).on("click",".trig_item",function(){
    $(document).find(".new_cate li").each(function(){
      var pId=$(this).attr("pid");
      var id=$(this).attr("id");
      if(pId!=null&&pId=="status"){
        if(id=="5") {
          zjData.FlowFlag='5';
        }
        if(id=="1") {
          zjData.FlowFlag='1';
        }
        if(id=="2") {
          zjData.FlowFlag='2';
        }
        if(id=="3") {
          zjData.FlowFlag='3';
        }
      }
      if(pId!=null&&pId=="channel"){
        zjData.ChannelId=$(this).attr("id");
      }
    });
    getContentList(zjData);
  });
  $(document).on("click",".cate_img",function(){
    if($(".new_cate li").size()=="0"){
      zjData.FlowFlag='0';
      zjData.SeqMediaId='0';
      zjData.ChannelId='0';
    }else{
      $(document).find(".new_cate li").each(function(){
        var pId=$(this).attr("pid");
        var id=$(this).attr("id");
        if(pId!="status"){
          zjData.FlowFlag='0';
        }
        if(pId!="album"){
          zjData.SeqMediaId='0';
        }
        if(pId!="channel"){
          zjData.ChannelId='0';
        }
      });
    }
    getContentList(zjData);
  });
  
  /*选择栏目的功能*/
  $(".channelimg1").on("click",function(){
    $(".cBox1_nav div:eq(0)").addClass("active").siblings().removeClass("active");
    $('.cBox1_con .cBox1_conb:eq(0)').show().siblings().hide();
    $(".channelBox1").toggle();
  });
  $(".cBox1_nav div").on("click",function(){
    var index = $(this).index();
    $(this).addClass('active').siblings().removeClass('active');
    $('.cBox1_con .cBox1_conb').eq(index).show().siblings().hide();
    if(index==0){
      $(".cBox1_conF li").removeClass("selectedF");
      $(".channelBox li").each(function(){
        var pId=$(this).attr("pid");
        $(".cBox1_conF li").each(function(){
          if($(this).attr("id")==pId){
            $(this).addClass("selectedF");
          }
        });
      });
    }
  });
  //新添加栏目样式变化
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
  var exit=false;//默认删除选中的二级栏目之后不存在相同的一级栏目
  $(document).on("click",".channel_bq_cancelimg1",function(){
    $(this).parent().remove();
    var id=$(this).parent().attr("id");
    var pId=$(this).parent().attr("pid");
    $(".cBox1_conS li").each(function(){
      if($(this).attr("id")==id){
        $(this).removeClass("selectedF");
      }
    });
    $(".channelBox li").each(function(){
      if($(this).attr("pid")==pId){
        exit=true;
      }else{
        exit=false;
      }
    });
    if(exit==false){
      $(".cBox1_conF li").each(function(){
        if($(this).attr("id")==pId){
          $(this).removeClass("selectedF");
        }
      })
    }
  })
  $(document).on("mouseenter",".cBox1_conb li",function(){
    /*此处hover与click事件是否可以同时存在还不确定，样式与设计图不符*/
    $(this).css({"color":"#666"});
  });
  $(document).on("mouseleave",".cBox1_conb li",function(){
    /*此处hover与click事件是否可以同时存在还不确定，样式与设计图不符*/
    $(this).css({"color":"#666"});
  });
  $(document).on("click",".cul li",function(){
    if($(".channelBox li").size()<"5"){
      $(this).addClass("selectedF");
      var chid=$(this).attr("id");
      $.ajax({
        url:rootPath+"common/getChannelTreeWithSelf.do",
        type:"POST",
        cache: false,
        "ChannelId":chid,
        processData: false,
        contentType: false,
        dataType:"json",
        //表单提交前进行验证
        success: function (resultData){
          if(resultData.jsonType == "1"){
            channelSecondList(chid,resultData);//加载选中的一级栏目下面的二级栏目
          }
        },
        error: function(XHR){
          alert("发生错误" + jqXHR.status);
        }
      });
    }else{
      alert("最多选择五个栏目");
      return;
    }
  });
  //加载选中的一级栏目下面的二级栏目
  function channelSecondList(chid,resultData){
    $(".cBox1_conS").html("");
    for(var i=0;i<resultData.data.children.length;i++){
      if(chid==resultData.data.children[i].id){
        if(resultData.data.children[i].isParent==true){
          for(var j=0;j<resultData.data.children[i].children.length;j++){
            var li="<li id="+resultData.data.children[i].children[j].attributes.id+" pid="+resultData.data.children[i].children[j].attributes.parentId+">"+resultData.data.children[i].children[j].name+"</li>"
            $(".cBox1_conS").append(li);
          }
        }else{
          $(".cBox1_conS").append("<li class='chsec'>暂无二级栏目</li>");
        }
        $(".cBox1_nav div:eq(0)").removeClass("active").siblings().addClass("active");
        $(".cBox1_conF").hide();
        $(".cBox1_conS").show();
        return;
      }
    }
  }
  //选中二级栏目
  $(document).on("click",".cBox1_conS li",function(){
    var isSelected=false;
    var id=$(this).attr("id");
    var pid=$(this).attr("pid");
    var txt=$(this).text();
    if($(".channelBox li").size()==0){
      var li='<li class="channel_bq bqImg" id='+id+' pid='+pid+'>'+
                  '<span>'+txt+'</span>'+
                  '<img class="channel_bq_cancelimg1 cancelImg" src="img/upl_img2.png" alt="" style="display: none;">'+
                '</li>';
      $(".channelBox").append(li);
      $(this).addClass("selectedF");
    }else if($(".channelBox li").size()<"5"){
      $(".channelBox li").each(function(){
        if($(this).attr("id")!=id){
          isSelected=false;
        }else{
          isSelected=true;
        }
      });
      if(isSelected==false){
        var li='<li class="channel_bq bqImg" id='+id+' pid='+pid+'>'+
                  '<span>'+txt+'</span>'+
                  '<img class="channel_bq_cancelimg1 cancelImg" src="img/upl_img2.png" alt="" style="display: none;">'+
                '</li>';
        $(".channelBox").append(li);
        $(".cBox1_conS li").each(function(){
          if($(this).attr("id")==id){
            $(this).addClass("selectedF")
          }
        })
      }else{
        alert("你应经选中过这个栏目了");
        return;
      }
    }else{
      alert("最多选中5个栏目");
      return;
    }
  });
});
