$(function(){
  //获取deviceId
//var deviceId=getCookie("JSESSIONID");
  var deviceId="59C1CE661FCA724EF50E47A281CE82A0";
  
  //获取用户的id
//var userId=$(".login_user span",parent.document).attr("userid");
  var userId="123";
  
  var rootPath=getRootPath();
  var subType=1;//subType=1代表在创建专辑页面保存,subType=2代表在修改专辑页面保存
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var allCount=0;//总记录数
  var pageSize=10;
  
  /*s--获取筛选条件*/
  var dataF={ "DeviceId":deviceId,
              "MobileClass":"Chrome",
              "UserId":userId,
              "PCDType":"3",
              "MediaType":"SeqMedia"
  };
  getFiltrates(dataF);
  function getFiltrates(data){
    $.ajax({
      type:"POST",
      url:rootPath+"content/getFiltrates.do",
      dataType:"json",
      cache:true,
      data:JSON.stringify(data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          getChannelLabel(resultData);//得到栏目的筛选标签
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  //得到栏目的筛选标签
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
          var li_tab_ul_li='<li class="trig_item_li" id='+resultData.ResultList.ChannelList[i].children[j].id+' pid='+resultData.ResultList.ChannelList[i].children[j].parentId+'>'+
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
  /*e--获取筛选条件*/
  
  /*s--获取专辑列表*/
  var zjData={};
  zjData.DeviceId=deviceId;
  zjData.MobileClass="Chrome";
  zjData.PCDType="3";
  zjData.UserId=userId;
  zjData.FlowFlag="0";
  zjData.PageSize=pageSize;
  zjData.Page=current_page;
  getContentList(zjData);
  function getContentList(obj){
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/getSeqMediaList.do",
      dataType:"json",
      cache:true,
      data:JSON.stringify(obj),
      beforeSend: function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载专辑列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          $(".ri_top3_con").html("");//每次加载之前先清空
          allCount=resultData.ResultList.length;
          contentCount=(allCount%10==0)?(allCount/10):(Math.ceil(allCount/10));
          getSeqMediaList(resultData); //加载专辑列表
        }else{
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>没有查到任何内容</div>");//每次加载之前先清空
          allCount="0";
          contentCount=(allCount%10==0)?(allCount/10):(Math.ceil(allCount/10));
        }
        pagitionInit(contentCount,allCount,obj.Page);//init翻页
        $('.shade', parent.document).hide();
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  //加载专辑列表
  function getSeqMediaList(resultData){
    for(var i=0;i<resultData.ResultList.length;i++){
      var albumBox= '<div class="rtc_listBox" contentId='+resultData.ResultList[i].ContentId+' >'+
                      '<img src="img/checkbox1.png" alt="" class="ric_img_check fl checkbox_img checkbox1"/>'+
                      '<div class="rtcl_img">'+
                        '<img src='+resultData.ResultList[i].ContentImg+' alt="节目图片" />'+
                      '</div>'+
                      '<div class="rtcl_con">'+
                        '<p class="rtcl_con_name">'+
                          '<span>专辑名称 ：</span>'+
                          '<span class="zjmc">'+resultData.ResultList[i].ContentName+'</span>'+
                        '</p>'+
                        '<div class="rtcl_con_num">'+
                          '<span class="fl">来自栏目 ： </span>'+
                          '<ul class="jmnum fl"></ul>'+
                        '</div>'+
                        '<p class="rtcl_con_time">'+
                          '<span>时间 ：</span>'+
                          '<span class="ctime">'+resultData.ResultList[i].CTime+'</span>'+
                        '</p>'+
                      '</div>'+
                      '<p class="zj_st">'+resultData.ResultList[i].SubCount+'个节目'+'</p>'+
                      '<div class="op_type" id="op_Box'+i+'">'+
                        '<p class="jm_up cf60">上传节目</p>'+
                        '<p class="jm_mg cf60" hrefs="manage_jm.html">节目管理</p>'+
                        '<p class="zj_ed cf60">编辑专辑</p>'+
                      '</div>'+
                    '</div>';
      $(".ri_top3_con").append(albumBox);
      $(".rtc_listBox").children(".rtcl_con").children(".zjmc").eq(i).text(((resultData.ResultList[i].ContentName)?(resultData.ResultList[i].ContentName):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".jmnum").eq(i).text(((resultData.ResultList[i].SubCount)?(resultData.ResultList[i].SubCount):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".other").children(".ctime").eq(i).text(((resultData.ResultList[i].CTime)?(resultData.ResultList[i].CTime):"暂无"));
      $(".rtc_listBox").children(".jm_st").eq(i).text(((resultData.ResultList[i].ContentPubChannels[0].FlowFlagState)?(resultData.ResultList[i].ContentPubChannels[0].FlowFlagState):"未知"));
      if(resultData.ResultList[i].ContentPubChannels){
        var channelds="";
        for(var j=0;j<resultData.ResultList[i].ContentPubChannels.length;j++){
          var li='<li class="fl" channelId='+resultData.ResultList[i].ContentPubChannels[j].ChannelId+'>'+'【'+resultData.ResultList[i].ContentPubChannels[j].ChannelName+'】'+'</li>';
          $(".jmnum").eq(i).append(li);
          if(channelds==""){
            channelds=resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }else{
            channelds+=","+resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }
        }
      }
      $(".rtc_listBox").eq(i).attr("channelId",channelds);
    }
  }
  /*e--获取专辑列表*/

  /*s--根据搜索/筛选/翻页获取专辑列表*/
  var optfy=1;//optfy=1未选中具体筛选条件前翻页,optfy=2选中具体筛选条件后翻页
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchWord="";
  
  //翻页之后的回调函数
  function pagitionBack(current_page){
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(searchWord==""){//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
      seaFy=1;
    }else{//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
      seaFy=2;
    }
    opts(seaFy,current_page);
  }
  
  //判断在点击翻页之前是否选择了筛选条件
  function opts(seaFy,current_page){
    destroy(zjData);
    zjData.DeviceId=deviceId;
    zjData.MobileClass="Chrome";
    zjData.PCDType="3";
    zjData.UserId=userId;
    zjData.PageSize=pageSize;
    zjData.Page=current_page;
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(optfy==2){//optfy=2选中具体筛选条件后翻页
      $(document).find(".new_cate li").each(function(){
        if($(".new_cate li").size()>="0"){
          var pId=$(this).attr("pid");
          var id=$(this).attr("id");
          zjData.ChannelId=$(this).attr("id");
        }
      });
    }
    if(seaFy==1){//seaFy=1未搜索关键词前翻页
      getContentList(zjData);
    }else{//seaFy=2搜索列表加载出来后翻页
      zjData.SearchWord=searchWord;
      getContentList(zjData);
    }
  }
  
  /*搜索*/
  //键盘上的enter事件
  $(".ri_top_li2_inp").keydown(function(e){
    var evt=event?event:(window.event?window.event:null);//兼容IE和FF
    if (evt.keyCode==13){
      $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
      $("#channel").show();
      searchList();
    }
  });
  //点击搜索小图标
  $(".ri_top_li2_img").on("click",function(){
    $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
    $("#channel").show();
    searchList();
  });
  function searchList(){
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(searchWord==""){
      alert("请输入搜索内容");
      $(".ri_top_li2_inp").focus();
    }else{
      destroy(zjData);
      zjData.DeviceId=deviceId;
      zjData.MobileClass="Chrome";
      zjData.PCDType="3";
      zjData.UserId=userId;
      zjData.FlowFlag="0";
      zjData.PageSize=pageSize;
      zjData.Page=current_page;
      zjData.SearchWord=searchWord;
      getContentList(zjData);
    }
  }
  /*e--根据搜索/筛选/翻页获取专辑列表*/
  
  /*s--点击相关操作的上传节目按钮*/
  $(document).on("click",".jm_up",function(){
    clear_jm();//清空上传节目模态框上的数据
  });
  /*e--点击上传节目按钮*/
  
  /*s--点击相关操作的节目管理按钮*/
  $(document).on("click",".jm_mg",function(){
    $("#myIframe",parent.document).attr({"src":$(this).attr("hrefs")});
  });
  /*e--点击相关操作的节目管理按钮*/
 
  /*s--点击相关操作的编辑专辑按钮*/
  $(document).on("click",".zj_ed",function(){
    var contentId=$(this).parent(".op_type").parent(".rtc_listBox").attr("contentid");
    subType=2;
    clear_zj();//清空专辑模态框上的数据
    edit_zj(contentId);
  });
  //编辑专辑时保存的信息
  function edit_zj(contentId){
    var _data={ "DeviceId":deviceId,
                "MobileClass":"Chrome",
                "PCDType":"3",
                "UserId":userId,
                "ContentId":contentId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/getSeqMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend: function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $("body").css({"overflow":"hidden"});
          fillZjContent(resultData);//填充专辑信息
        }else{
          alert(resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  //填充专辑信息
  function fillZjContent(resultData){
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
  /*e--点击相关操作的编辑专辑按钮*/
  
  /*s--点击创建专辑按钮*/
  $(document).on("click",".ri_top_li3",function(){
    subType=1;
    $(".add_zj .iboxtitle h4").html("创建专辑");
    clear_zj();//清空专辑模态框上的数据
  });
  
  //获取公共标签
  var data1={"DeviceId":deviceId,
             "MobileClass":"Chrome",
             "PCDType":"3",
             "UserId":userId,
             "MediaType":"1",
             "ChannelIds":"cn31",
             "TagType":"1",
             "TagSize":"20"
  };
  loadPubTag(data1);
  //点击“换一换”，更换公共标签
  $(document).on("click",".gg_tag .hyp",function(){
    loadPubTag(data1);
  })
  
  //获取我的标签
  var data2={"DeviceId":deviceId,
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":userId,
               "MediaType":"1",
               "ChannelIds":"cn31",
               "TagType":"2",
               "TagSize":"20"
  };
  loadMyTag(data2);
  //点击“换一换”，更换我的标签
  $(document).on("click",".my_tag .hyp",function(){
    loadMyTag(data2);
  })
  /*e--点击创建专辑按钮*/

  /*s--点击创建专辑/编辑专辑页面上的保存按钮*/
  $("#submitBtn").on("click",function(){
    if(subType=="1")  save_add_zj();
    if(subType=="2")  save_edit_zj();
  })
  //点击创建专辑页面上的保存按钮
  function save_add_zj(){
    var chIdstr="";
    $(".channelBox li").each(function(){
      var ids=$(this).attr("id");
      if(chIdstr==""){
        chIdstr=ids;
      }else{
        chIdstr+=","+ids;
      }
    })
    if($(".previewImg").attr("isDefaultImg")=="true"){
      $(".upl_img").attr("value","http://wotingfm.com:908/CM/resources/images/default.png");
    }
    var _data={};
    _data.UserId="123";
    _data.DeviceId=deviceId;
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
      beforeSend:function(){
        $(".btn_group input").attr("disabled","disabled");
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("创建专辑成功");
          $(".mask_zj,.add_zj").hide();
          $("body").css({"overflow":"auto"});
          getContentList(zjData);//重新加载专辑列表
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
  //点击编辑专辑页面上的保存按钮
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
    _data.UserId=userId;
    _data.DeviceId=deviceId;
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
      beforeSend: function(){
        $(".btn_group input").attr("disabled","disabled");
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("专辑信息修改成功");
          $(".mask_zj,.add_zj").hide();
          $("body").css({"overflow":"auto"});
          getContentList(zjData);//重新加载专辑列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }
        $(".btn_group input").removeAttr("disabled");
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  /*e--点击创建专辑/编辑专辑页面上的保存按钮*/
  
  /*s---批量撤回、删除专辑 */
  var lists=[];//撤回专辑
  var delList='';//删除专辑
  $(".rt_opt .opetype").on("click",function(){
    var type=$.trim($(this).attr("type"));
    $(".ri_top3_con .rtc_listBox").each(function(){
      if($(this).children(".ric_img_check").hasClass("checkbox1")){//未选中
        
      }else{//已选中
        var list={};
        list.ContentId=$(this).attr("contentid");
        lists.push(list);
        if(delList==''){
          delList=$(this).attr("contentid");
        }else{
          delList+=','+$(this).attr("contentid");
        }
      }
    });
    if(lists.length!=0){//选中内容
      var isSure=false;//是否存在
      switch(type){
        case "revoke":
          var url="content/seq/updateSeqMediaStatus.do";
          var data6={};
          data6.DeviceId=deviceId;
          data6.PCDType="3";
          data6.MobileClass="Chrome";
          data6.UserId=userId;
          data6.ContentFlowFlag="4";
          data6.UpdateList=lists;
          commonAjax(data6,url);
        break;
        case "delete":
          var url="content/seq/removeSeqMedia.do";
          var data6={};
          data6.DeviceId=deviceId;
          data6.PCDType="3";
          data6.MobileClass="Chrome";
          data6.UserId=userId;
          data6.ContentIds=delList;
          commonAjax(data6,url);
        break;
        default:break;
      }
    }
  });
  //公共的提交、撤回、删除
  function commonAjax(data,url){
    $.ajax({
      type:"POST",
      url:rootPath+url,
      dataType:"json",
      data:JSON.stringify(data),
      beforeSend:function(){
        $(".shade",parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("操作成功");
          $("body").css({"overflow":"auto"});
          getContentList(zjData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
          $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
        }else{
          alert(resultData.Message);
        }
        $(".shade",parent.document).hide();
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  /*e---批量撤回、删除专辑 */
  
  
  /*s--获取专辑模态框上面的栏目信息*/
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
  //得到专辑栏目列表
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
  //选择一级/二级栏目
  $(".channelimg1").on("click",function(){
    $(".cBox1_nav div:eq(0)").addClass("active").siblings().removeClass("active");
    $('.cBox1_con .cBox1_conb:eq(0)').show().siblings().hide();
    $(".channelBox1").toggle();
  });
  $(".cBox1_nav div").on("click",function(){
    var index = $(this).index();
    $(this).addClass('active').siblings().removeClass('active');
    $('.cBox1_con .cBox1_conb').eq(index).show().siblings().hide();
    if(index==0){//点击一级栏目
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
        return;
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
      $(".cBox1_conS").html(" ").append("<li class='chsec'>请先选择具体的一级栏目</li>");
    }
  })
  $(document).on("click",".cul li",function(){
    if($(".channelBox li").size()<"5"){
      $(this).addClass("selectedF");
      var chid=$(this).attr("id");
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
  /*e--获取专辑模态框上面的栏目信息*/
  
  //点击专辑的封面图片，跳到这个专辑的详情页
  $(document).on("click",".rtcl_img",function(){
    var contentId=$(this).parent(".rtc_listBox").attr("contentId");
    $("#myIframe", parent.document).attr({"src":"zj_detail.html?contentId="+contentId});
  });
  
  /*根据不同的筛选条件得到不同的专辑列表*/
  $(document).on("click",".trig_item,.trig_item_li,.cate_img",function(){//选中或取消某个筛选条件
    destroy(zjData);
    zjData.DeviceId=deviceId;
    zjData.MobileClass="Chrome";
    zjData.PCDType="3";
    zjData.UserId=userId;
    zjData.PageSize=pageSize;
    zjData.Page=current_page;
    if($(".new_cate li").size()>"0"){
      $(document).find(".new_cate li").each(function(){
        zjData.ContentId=$(this).attr("id");
      });
    }
    getContentList(zjData);
  });
  
  /*s--翻页插件初始化*/
  function pagitionInit(contentCount,allCount,current_page){
    var totalPage=contentCount;
    var totalRecords=allCount;
    var pageNo=current_page;
    //生成分页
    //有些参数是可选的，比如lang，若不传有默认值
    kkpager.generPageHtml({
      pno : pageNo,
      //总页码
      total : totalPage,
      //总数据条数
      totalRecords : totalRecords,
      //页码选项
      lang : {
        firstPageText : '首页',
        firstPageTipText  : '首页',
        lastPageText  : '尾页',
        lastPageTipText : '尾页',
        prePageText : '上一页',
        prePageTipText  : '上一页',
        nextPageText  : '下一页',
        nextPageTipText : '下一页',
        totalPageBeforeText : '共',
        totalPageAfterText  : '页',
        currPageBeforeText  : '当前第',
        currPageAfterText : '页',
        totalInfoSplitStr : '/',
        totalRecordsBeforeText  : '共',
        totalRecordsAfterText : '条数据',
        gopageBeforeText  : '&nbsp;转到',
        gopageButtonOkText  : '确定',
        gopageAfterText : '页',
        buttonTipBeforeText : '第',
        buttonTipAfterText  : '页'
      },
      mode : 'click',//默认值是link，可选link或者click
      click :function(current_page){//点击后的回调函数可自定义
        this.selectPage(current_page);
        pagitionBack(current_page);//翻页之后的回调函数
        return false;
      }
    },true);
  };
  /*e--翻页插件初始化*/
  
});


