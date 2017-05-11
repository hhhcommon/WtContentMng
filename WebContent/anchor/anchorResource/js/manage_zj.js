$(function(){
  
  //获取用户的id
  var userId=$(".login_user span",parent.document).attr("userid");

  var rootPath=getRootPath();
  var subType=1;//subType=1代表在创建专辑页面保存,subType=2代表在修改专辑页面保存
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var allCount=0;//总记录数
  var pageSize=10;//每页条数
  var flagflow=0;//专辑的状态
  
  /*s--获取筛选条件*/
  var dataF={"MobileClass":"Chrome",
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
        if(resultData.ReturnType=="1001"){
          getChannelLabel(resultData);//得到栏目的筛选标签
        }
      },
      error:function(jqXHR){
        alert("加载筛选条件出错:"+jqXHR.status);
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
    //12个栏目以上,出现更多
    if(resultData.ResultList.ChannelList.length>12) $("#channel .more1").removeClass("dis");
    else $("#channel .more1").addClass("dis");
  }
  /*e--获取筛选条件*/
  
  /*s--获取专辑列表*/
  var zjData={};
  zjData.MobileClass="Chrome";
  zjData.PCDType="3";
  zjData.UserId=userId;
  zjData.FlagFlow=flagflow;
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
      beforeSend:function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>正在加载专辑列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".ri_top3_con").html("");//每次加载之前先清空
          allCount=resultData.AllCount;
          getSeqMediaList(resultData); //加载专辑列表
        }else{
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>没有得到专辑列表...</div>");//每次加载之前先清空
          allCount="0";
        }
        contentCount=(allCount%pageSize==0)?(allCount/pageSize):(Math.ceil(allCount/pageSize));
        pagitionInit(contentCount,allCount,zjData.Page);//init翻页
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>加载专辑列表发生错误:"+jqXHR.status+"</div>");
        $('.shade', parent.document).hide();
      }
    });
  }
  //加载专辑列表
  function getSeqMediaList(resultData){
    for(var i=0;i<resultData.ResultList.length;i++){
      var albumBox= '<div class="rtc_listBox" contentId='+resultData.ResultList[i].ContentId+' >'+
                      '<img src="./../anchorResource/img/checkbox1.png" alt="" class="ric_img_check fl checkbox_img checkbox1"/>'+
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
                      '<p class="zj_st" jmnum='+resultData.ResultList[i].SubCount+'>'+resultData.ResultList[i].SubCount+'个节目'+'</p>'+
                      '<div class="op_type" id="op_Box'+i+'">'+
                        '<p class="jm_up cf60">上传节目</p>'+
                        '<p class="jm_mg cf60" hrefs="jmANDzj/manage_jm.html">节目管理</p>'+
                        '<p class="zj_ed cf60">编辑专辑</p>'+
                      '</div>'+
                    '</div>';
      $(".ri_top3_con").append(albumBox);
      $(".rtc_listBox").children(".rtcl_con").children(".zjmc").eq(i).text(((resultData.ResultList[i].ContentName)?(resultData.ResultList[i].ContentName):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".jmnum").eq(i).text(((resultData.ResultList[i].SubCount)?(resultData.ResultList[i].SubCount):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".other").children(".ctime").eq(i).text(((resultData.ResultList[i].CTime)?(resultData.ResultList[i].CTime):"暂无"));
      if(resultData.ResultList[i].ContentPubChannels){
        var channelds="";
        for(var j=0;j<resultData.ResultList[i].ContentPubChannels.length;j++){
          var li='<li class="fl" flowflag='+resultData.ResultList[i].ContentPubChannels[j].FlowFlag+' channelId='+resultData.ResultList[i].ContentPubChannels[j].ChannelId+'>'+'【'+resultData.ResultList[i].ContentPubChannels[j].ChannelName+'】'+'</li>';
          $(".jmnum").eq(i).append(li);
          if(channelds==""){
            channelds=resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }else{
            channelds+=","+resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }
        }
        $(".rtc_listBox").eq(i).attr("channelId",channelds);
      }
    }
  }
  /*e--获取专辑列表*/

  /*s--根据搜索/筛选/翻页获取专辑列表*/
  var optfy=1;//optfy=1未选中具体筛选条件前翻页,optfy=2选中具体筛选条件后翻页
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchWord="";
  
  //翻页之后的回调函数
  function pagitionBack(current_page){
    if($(".new_cate li").size()>"0"){//已经选中筛选条件
      optfy=2;//选中具体筛选条件后翻页
    }else{
      optfy=1;//未选中具体筛选条件后翻页
    }
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
      getSearchList(zjData);
    }
  }
  
  /*搜索*/
  //键盘上的enter事件
  $(".ri_top_li2_inp").keydown(function(e){
    var evt=event?event:(window.event?window.event:null);//兼容IE和FF
    if (evt.keyCode==13){
      $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
      $("#channel").show();
      searchList();//搜索
    }
  });
  //点击搜索小图标
  $(".ri_top_li2_img").on("click",function(){
    $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
    $("#channel").show();
    searchList();//搜索
  });
  function searchList(){
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(searchWord==""){
      alert("请输入搜索关键词");
      $(".ri_top_li2_inp").focus();
    }else{
      destroy(zjData);
      zjData.MobileClass="Chrome";
      zjData.PCDType="3";
      zjData.UserId=userId;
      zjData.FlowFlag=flagflow;
      zjData.PageSize=pageSize;
      current_page=1;
      zjData.Page=current_page;
      zjData.SearchWord=searchWord;
      getSearchList(zjData);
    }
  }
  //得到搜索列表 
  function getSearchList(obj){//暂无本接口
    $.ajax({
      type:"POST",
      url:rootPath+"content/searchSeqList.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(obj),
      beforeSend:function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>正在加载专辑列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".ri_top3_con").html("");//每次加载之前先清空
          allCount=resultData.AllCount;
          getSeqMediaList(resultData); //加载专辑列表
        }else{
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>没有得到专辑列表...</div>");//每次加载之前先清空
          allCount="0";
        }
        contentCount=(allCount%pageSize==0)?(allCount/pageSize):(Math.ceil(allCount/pageSize));
        pagitionInit(contentCount,allCount,zjData.Page);//init翻页
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>加载专辑列表发生错误:"+jqXHR.status+"</div>");
        $('.shade', parent.document).hide();
      }
    });
  }
  /*e--根据搜索/筛选/翻页获取专辑列表*/
  
  /*s--点击相关操作的上传节目按钮*/
  $(document).on("click",".jm_up",function(){
    clear_jm();//清空上传节目模态框上的数据
    var seqId=$(this).parent(".op_type").parent(".rtc_listBox").attr("contentId");
    $(".add_jm .seqId").val(seqId);
    tags();//获取标签
  });
  
  //上传节目页面--点击保存按钮
  var _data={};
  function add_jmData(seqId){
    if($(".mask_jm .previewImg").attr("isDefaultImg")=="true"){
      $(".mask_jm .upl_img").attr("value","http://www.wotingfm.com:908/CM/resources/images/default.png");
    }
    _data.UserId=userId;
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentURI=$(".mask_jm .audio").attr("src");
    _data.ContentName=$(".mask_jm .uplTitle").val();
    _data.ContentImg=$(".mask_jm .upl_img").attr("value");
    _data.SeqMediaId=$(".mask_jm .seqId").val();
    _data.TimeLong=$(".mask_jm .timeLong").attr("value");
    var taglist=[];
    $(".mask_jm .upl_bq").find(".upl_bq_img").each(function(){
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
    _data.ContentDesc=$(".mask_jm .uplDecn").val();
    var memberTypelist=[];
    $(".mask_jm .czfs_tag").find(".czfs_tag_li").each(function(){
      var czfsObj={};//创作方式对象
      var czfs_t=""+$(this).children().children(".czfs_tag_span1").html();
      var czfs_txt=czfs_t.split(":")[0];
      czfsObj.TypeName=czfs_txt;
      czfsObj.TypeId=$(this).attr("czfs_typeid");
      czfsObj.TypeInfo=$(this).children().children(".czfs_tag_span2").html();
      memberTypelist.push(czfsObj);
    });
    _data.MemberType=memberTypelist;
    var str_time=$(".mask_jm .layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
  }
  $(".mask_jm #submitBtn").on("click",function(){
    _data.FlowFlag="1";//1提交
    add_jmData();
    addJM(_data);
  });
  function addJM(_data){
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/addMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend:function(){
        $(".mask_jm .btn_group input").attr("disabled","disabled").css("background","#ccc");
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".mask_jm").hide();
          $("body").css({"overflow":"auto"});
          getContentList(zjData);//重新加载专辑列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert("保存或发布发生错误:"+resultData.Message);
        }
        $(".mask_jm .btn_group input").removeAttr("disabled").css("background","#ffa634");
      },
      error:function(jqXHR){
        alert("保存或发布发生错误:"+ jqXHR.status);
        $(".mask_jm .btn_group input").removeAttr("disabled").css("background","#ffa634");
      }
    });
  }
  //上传节目页面--点击发布按钮
  $(".mask_jm #pubBtn").on("click",function(){
    _data.FlowFlag="2";//2发布
    add_jmData();
    addJM(_data);
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
    tags();//获取标签
  });
  //编辑专辑之前加载专辑的详细信息
  function edit_zj(contentId){
    var _data={ "MobileClass":"Chrome",
                "PCDType":"3",
                "UserId":userId,
                "ContentId":contentId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/getSeqMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend:function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $("body").css({"overflow":"hidden"});
          fillZjContent(resultData);//填充专辑信息
        }else{
          alert("得到专辑信息:"+resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("得到专辑信息发生错误:"+ jqXHR.status);
        $('.shade', parent.document).hide();
      }
    });
  }
  //填充专辑信息
  function fillZjContent(resultData){
    $(".mask_zj .channelBox").html("");
    $(".mask_zj .iboxtitle h4").html("修改专辑");
    $(".mask_zj .zjId").attr("value",resultData.Result.ContentId);
    $(".mask_zj .uplTitle").val(resultData.Result.ContentName);
    $(".mask_zj .defaultImg").attr("src",resultData.Result.ContentImg);
    if(resultData.Result.ContentKeyWords!=null){
      for(var i=0;i<resultData.Result.ContentKeyWords.length;i++){
        if(resultData.Result.ContentKeyWords[i]){
          var new_tag='<li class="upl_bq_img bqImg" tagId='+resultData.Result.ContentKeyWords[i].TagId+'>'+
                        '<span>'+resultData.Result.ContentKeyWords[i].TagName+'</span>'+
                        '<img class="upl_bq_cancelimg1 cancelImg" src="../anchorResource/img/upl_img2.png" alt="" />'+
                      '</li>';
          $(".mask_zj .upl_bq").append(new_tag);
          var tagId=resultData.Result.ContentKeyWords[i].TagId;
          $(".mask_zj .my_tag_con1").each(function(){
            if($(this).attr("tagid")==tagId){
              $(this).children("input[type='checkbox']").prop("checked",true);
              $(this).children("input[type='checkbox']").prop("disabled",true);
            }
          });
          $(".mask_zj .gg_tag_con1").each(function(){
            if($(this).attr("tagid")==tagId){
              $(this).children("input").prop("checked",true);
              $(this).children("input").prop("disabled",true);
            }
          });
        }
      }
    }
    if(resultData.Result.ContentPubChannels!=null){
      for(var i=0;i<resultData.Result.ContentPubChannels.length;i++){
        if(resultData.Result.ContentPubChannels[i]){
          var li= '<li class="channel_bq bqImg" id='+resultData.Result.ContentPubChannels[i].ChannelId+'>'+
                    '<span>'+resultData.Result.ContentPubChannels[i].ChannelName+'</span>'+
                    '<img class="channel_bq_cancelimg1 cancelImg" src="../anchorResource/img/upl_img2.png" alt="" style="display: none;">'+
                  '</li>';
          $(".mask_zj .channelBox").append(li);
        }
      }
    }
    $(".mask_zj .uplDecn").val(resultData.Result.ContentDesc);
    $(".mask_zj .layer-date").val(resultData.Result.CTime);
  }
  /*e--点击相关操作的编辑专辑按钮*/
  
  /*s--专辑/节目弹出页面获取标签*/
  var data1={};//公共标签
  var data2={};//我的标签
  function tags(){
    if($(".mask_zj").css("display")=="block"){//专辑弹出页面显示
      //获取专辑弹出页面公共标签
      destroy(data1);
      data1.MobileClass="Chrome";
      data1.PCDType="3";
      data1.UserId=userId;
      data1.MediaType="1";
      data1.TagType="1";
      data1.TagSize="20";
      loadTag(data1);
      
      //获取专辑弹出页面我的标签
      destroy(data2);
      data2.MobileClass="Chrome";
      data2.PCDType="3";
      data2.UserId=userId;
      data2.MediaType="1";
      data2.TagType="2";
      data2.TagSize="20";
      loadTag(data2);
    }else{//节目弹出页面显示
      //获取节目弹出页面公共标签
      destroy(data1);
      data1.MobileClass="Chrome";
      data1.PCDType="3";
      data1.UserId=userId;
      data1.MediaType="2";
      data1.TagType="1";
      data1.TagSize="20";
      data1.SeqMediaId=$(".add_jm .seqId").val();
      loadTag(data1);
      
      //获取节目弹出页面我的标签
      destroy(data2);
      data2.MobileClass="Chrome";
      data2.PCDType="3";
      data2.UserId=userId;
      data2.MediaType="2";
      data2.TagType="2";
      data2.TagSize="20";
      data2.SeqMediaId=$(".add_jm .seqId").val();
      loadTag(data2);
    }
  }
  
  //点击专辑弹出页面“换一换”，更换专辑弹出页面公共标签
  $(document).on("click",".add_zj .gg_tag .hyp",function(){
    destroy(data1);
    data1.MobileClass="Chrome";
    data1.PCDType="3";
    data1.UserId=userId;
    data1.MediaType="1";
    data1.TagType="1";
    data1.TagSize="20";
    if($(".channelBox .channel_bq").length>0){
      var chids='';
      $(".channelBox .channel_bq").each(function(){
        if(chids=='') chids=$(this).attr("id");
        else chids+=','+$(this).attr("id");
      })
      data1.ChannelIds=chids;
    }
    loadTag(data1);
  })
  
  //点击专辑弹出页面“换一换”，更换专辑弹出页面我的标签
  $(document).on("click",".add_zj .my_tag .hyp",function(){
    destroy(data2);
    data2.MobileClass="Chrome";
    data2.PCDType="3";
    data2.UserId=userId;
    data2.MediaType="1";
    data2.TagType="2";
    data2.TagSize="20";
    if($(".channelBox .channel_bq").length>0){
       var chids='';
      $(".channelBox .channel_bq").each(function(){
        if(chids=='') chids=$(this).attr("id");
        else chids+=','+$(this).attr("id");
      })
      data2.ChannelIds=chids;
    }
    loadTag(data2);
  })
  
  //点击节目弹出页面“换一换”，更换节目弹出页面公共标签
  $(document).on("click",".add_jm .gg_tag .hyp",function(){
    destroy(data1);
    data1.MobileClass="Chrome";
    data1.PCDType="3";
    data1.UserId=userId;
    data1.MediaType="2";
    data1.TagType="1";
    data1.TagSize="20";
    data1.SeqMediaId=$(".add_jm .seqId").val();
    loadTag(data1);
  })
  
  //点击节目弹出页面“换一换”，更换节目弹出页面我的标签
  $(document).on("click",".add_jm .my_tag .hyp",function(){
    destroy(data2);
    data2.MobileClass="Chrome";
    data2.PCDType="3";
    data2.UserId=userId;
    data2.MediaType="2";
    data2.TagType="2";
    data2.TagSize="20";
    data2.SeqMediaId=$(".add_jm .seqId").val();
    loadTag(data2);
  })
  /*e--专辑/节目弹出页面获取标签*/
  
  /*s--点击创建专辑按钮*/
  $(document).on("click",".ri_top_li3",function(){
    subType=1;
    $(".mask_zj .iboxtitle h4").html("创建专辑");
    clear_zj();//清空专辑模态框上的数据
    clear_jm();//清空节目模态框上的数据
    $(".mask_jm").hide();
    tags();//获取标签
  });
  /*e--点击创建专辑按钮*/

  /*s--点击创建专辑/编辑专辑页面上的保存按钮*/
  $(".mask_zj #submitBtn").on("click",function(){
    if(subType=="1")  save_add_zj();
    if(subType=="2")  save_edit_zj();
  })
  //点击创建专辑页面上的保存按钮
  function save_add_zj(){
    var chIdstr="";
    $(".mask_zj .channelBox li").each(function(){
      var ids=$(this).attr("id");
      if(chIdstr=="") chIdstr=ids;
      else chIdstr+=","+ids;
    })
    if($(".mask_zj .previewImg").attr("isDefaultImg")=="true"){
      $(".mask_zj .upl_img").attr("value","http://www.wotingfm.com:908/CM/resources/images/default.png");
    }
    var _data={};
    _data.UserId=userId;
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentName=$(".mask_zj .uplTitle").val();
    _data.ContentImg=$(".mask_zj .upl_img").attr("value");
    _data.ChannelId=chIdstr;
    var taglist=[];
    $(".mask_zj .upl_bq").find(".upl_bq_img").each(function(){
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
    _data.ContentDesc=$(".mask_zj .uplDecn").val();
    var str_time=$(".mask_zj .layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/addSeqMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend:function(){
        $(".mask_zj .btn_group input").attr("disabled","disabled").css("background","#ccc");
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".mask_zj").hide();
          $("body").css({"overflow":"auto"});
          getContentList(zjData);//重新加载专辑列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert("新增专辑失败:"+resultData.Message);
        }
        $(".mask_zj .btn_group input").removeAttr("disabled").css("background","#ffa634");
      },
      error:function(jqXHR){
        alert("新增专辑发生错误:"+ jqXHR.status);
        $(".mask_zj .btn_group input").removeAttr("disabled").css("background","#ffa634");
      }
    });
  }
  //点击编辑专辑页面上的保存按钮
  function save_edit_zj(){
    var chIdstr="";
    $(".mask_zj .channelBox li").each(function(){
      var ids=$(this).attr("id");
      if(chIdstr=="") chIdstr=ids;
      else chIdstr+=","+ids;
    })
    if(!$(".mask_zj .upl_img").attr("value")){
      $(".mask_zj .upl_img").attr("value",$(".defaultImg").attr("src"));
    }
    var _data={};
    _data.UserId=userId;
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentName=$(".mask_zj .uplTitle").val();
    _data.ContentId=$(".mask_zj .zjId").attr("value");
    _data.ContentImg=$(".mask_zj .upl_img").attr("value");
    _data.ChannelId=chIdstr;
    var taglist=[];
    $(".mask_zj .upl_bq").find(".upl_bq_img").each(function(){
      var tag={};//标签对象
      var tagTxt=$(this).children("span").html();
      $(".mask_zj .my_tag_con1").each(function(){
        if($(this).children(".my_tag_con1_span").html()==tagTxt){
          $(".my_tag_con1").children(".my_tag_con1_check").prop("checked",false);
          $(".my_tag_con1").children(".my_tag_con1_check").attr("disabled",false);
          $(this).children(".my_tag_con1_check").prop("checked",true);
          $(this).children(".my_tag_con1_check").attr("disabled",true);
          tag.TagName=$(this).children(".my_tag_con1_span").html();
          tag.TagOrg="我的标签";
        }
      })
      $(".mask_zj .gg_tag_con1").each(function(){
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
    _data.ContentDesc=$(".mask_zj .uplDecn").val();
    var str_time=$(".mask_zj .layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/updateSeqMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend: function(){
        $(".mask_zj .btn_group input").attr("disabled","disabled").css("background","#ccc");
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".mask_zj").hide();
          $("body").css({"overflow":"auto"});
          getContentList(zjData);//重新加载专辑列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert("修改专辑失败:"+resultData.Message);
        }
        $(".mask_zj .btn_group input").removeAttr("disabled").css("background","#ffa634");
      },
      error:function(jqXHR){
        alert("修改专辑发生错误:"+jqXHR.status);
        $(".mask_zj .btn_group input").removeAttr("disabled").css("background","#ffa634");
      }
    });
  }
  /*e--点击创建专辑/编辑专辑页面上的保存按钮*/
  
  /*s---批量撤回、删除专辑 */
  var lists=[];//撤回专辑
  var delList='';//删除专辑
  var revoke=true;//revoke=true默认要撤回的专辑里面都有节目,revoke=false要撤回的专辑里面没有节目
  $(".rt_opt .opetype").on("click",function(){
    var type=$.trim($(this).attr("type"));
    $(".ri_top3_con .rtc_listBox").each(function(){
      if($(this).children(".ric_img_check").hasClass("checkbox1")){//未选中
        
      }else{//已选中
        var list={};
        list.ContentId=$(this).attr("contentid");
        lists.push(list);
        if(delList=='') delList=$(this).attr("contentid");
        else delList+=','+$(this).attr("contentid");
        if($(this).children(".zj_st").attr("jmnum")=='0'){//专辑里面没有节目不支持撤回
          revoke=false;
          return false;
        }
      }
    });
    if(lists.length!=0){//选中内容
      switch(type){
        case "revoke":
          var url="content/seq/updateSeqMediaStatus.do";
          var data6={};
          data6.PCDType="3";
          data6.MobileClass="Chrome";
          data6.UserId=userId;
          data6.ContentFlowFlag="4";
          data6.UpdateList=lists;
          commonAjax(revoke,data6,url);
        break;
        case "delete":
          var url="content/seq/removeSeqMedia.do";
          var data6={};
          data6.PCDType="3";
          data6.MobileClass="Chrome";
          data6.UserId=userId;
          data6.ContentIds=delList;
          commonAjax(revoke,data6,url);
        break;
        default:break;
      }
    }
  });
  //公共的提交、撤回、删除
  function commonAjax(revoke,data,url){
    if(!revoke){
      alert("所选择的专辑里面没有节目,不支持撤回操作");
      return false;
    }else{
      $.ajax({
        type:"POST",
        url:rootPath+url,
        dataType:"json",
        data:JSON.stringify(data),
        beforeSend:function(){
          $(".shade",parent.document).show();
        },
        success:function(resultData){
          if(resultData.ReturnType=="1001"){
            $("body").css({"overflow":"auto"});
            getContentList(zjData);//重新加载专辑列表
            $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
            $("#channel .chnels").remove();
            getFiltrates(dataF);//重新加载筛选条件
            $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
            $(".all_check").addClass("checkbox1").attr({"src":"./../anchorResource/img/checkbox1.png"});
          }else{
            alert("当前操作失败:"+resultData.Message);
          }
          $(".shade",parent.document).hide();
        },
        error:function(jqXHR){
          alert("当前操作发生错误:"+ jqXHR.status);
          $(".shade",parent.document).hide();
        }
      });
    }
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
    success: function(resultData){
      if(resultData.jsonType=="1"){
        if(resultData.data.children[0]){
          resultData=resultData.data.children[0];
          getChannelList(resultData);//得到专辑栏目列表
        }
      }
    },
    error: function(jqXHR){
      alert("加载栏目树发生错误:" + jqXHR.status);
    }
  });
  //得到专辑栏目列表
  function getChannelList(resultData){
    for(var i=0;i<resultData.children.length;i++){
      var ss=resultData.children[i].attributes.nPy.substring(0,1);
      switch(ss){
        case "A":case "B":case "C":case "D":case "E":case "F":case "G":
          var li='<li value="" id='+resultData.children[i].id+'>'+resultData.children[i].name+'</li>';
          $(".mask_zj .cul_ag").append(li);
          break;
        case "H":case "I":case "J":case "K":case "L":
          var li='<li value="" id='+resultData.children[i].id+'>'+resultData.children[i].name+'</li>';
          $(".mask_zj .cul_hl").append(li);
          break;
        case "M":case "N":case "O":case "P":case "Q":case "R":case "S":
          var li='<li value="" id='+resultData.children[i].id+'>'+resultData.children[i].name+'</li>';
          $(".mask_zj .cul_ms").append(li);
          break;
        case "T":case "U":case "V":case "W":case "X":case "Y":case "Z":
          var li='<li value="" id='+resultData.children[i].id+'>'+resultData.children[i].name+'</li>';
          $(".mask_zj .cul_tz").append(li);
          break;
        default:
          break;
      }
    }
  }
  //选择一级/二级栏目的切换
  $(".mask_zj .channelimg1").on("click",function(){
    $(".mask_zj .cBox1_nav div:eq(0)").addClass("active").siblings().removeClass("active");
    $('.mask_zj .cBox1_con .cBox1_conb:eq(0)').show().siblings().hide();
    $(".mask_zj .channelBox1").toggle();
  });
  $(".mask_zj .cBox1_nav div").on("click",function(){
    var index=$(this).index();
    $(this).addClass('active').siblings().removeClass('active');
    $('.mask_zj .cBox1_con .cBox1_conb').eq(index).show().siblings().hide();
    if(index==0){//点击一级栏目
      $(".mask_zj .cBox1_conF li").removeClass("selectedF");
      $(".mask_zj .channelBox li").each(function(){
        var pId=$(this).attr("pid");
        $(".mask_zj .cBox1_conF li").each(function(){
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
    $(this).attr({"src":"../anchorResource/img/upl_img6.png"});
  });
  $(document).on("mouseleave",".cancelImg",function(event){
    event.stopPropagation();
    $(this).attr({"src":"../anchorResource/img/upl_img2.png"});
    $(this).hide();
  });
  //取消已经选中的二级栏目
  var exit=false;
  $(document).on("click",".channel_bq_cancelimg1",function(){
    $(this).parent().remove();
    var id=$(this).parent().attr("id");
    var pId=$(this).parent().attr("pid");
    $(".mask_zj .cBox1_conS li").each(function(){
      if($(this).attr("id")==id){
        $(this).removeClass("selectedF");
      }
    });
    $(".mask_zj .channelBox li").each(function(){
      if($(this).attr("pid")!=pId){
        exit=false;
      }else{
        exit=true;
        return false;
      }
    });
    if(exit==false){
      $(".mask_zj .cBox1_conF li").each(function(){
        if($(this).attr("id")==pId){
          $(this).removeClass("selectedF");
        }
      });
    }
    $(".mask_zj .cBox1_conS").html(" ").append("<li class='chsec'>请先选择具体的一级栏目</li>");
  })
  //选中具体的一级栏目
  $(document).on("click",".mask_zj .cul li",function(){
    if($(".mask_zj .channelBox li").size()<"5"){
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
        success: function(resultData){
          if(resultData.jsonType=="1"){
            if(resultData.data.children[0]){
              resultData=resultData.data.children[0];
              channelSecondList(chid,resultData);//加载选中的一级栏目下面的二级栏目
            }
          }
        },
        error: function(jqXHR){
          alert("加载栏目发生错误:"+jqXHR.status);
        }
      });
    }else{
      alert("最多选择五个栏目");
      return;
    }
  });
  //加载选中的一级栏目下面的二级栏目
  function channelSecondList(chid,resultData){
    $(".mask_zj .cBox1_conS").html(" ");//清空其他一级栏目的二级栏目
    for(var i=0;i<resultData.children.length;i++){
      if(chid==resultData.children[i].id){
        if(resultData.children[i].isParent==true){
          for(var j=0;j<resultData.children[i].children.length;j++){
            var li="<li id="+resultData.children[i].children[j].attributes.id+" pid="+resultData.children[i].children[j].attributes.parentId+" pname="+resultData.children[i].name+">"+resultData.children[i].children[j].name+"</li>"
            $(".mask_zj .cBox1_conS").append(li);
          }
        }else{
          $(".mask_zj .cBox1_conS").append("<li class='chsec'>暂无二级栏目</li>");
        }
        $(".mask_zj .cBox1_nav div:eq(0)").removeClass("active").siblings().addClass("active");
        $(".mask_zj .cBox1_conF").hide();
        $(".mask_zj .cBox1_conS").show();
        return;
      }
    }
  }
  //选中二级栏目
  var isSelected=false;//当前二级栏目是否已经选中
  $(document).on("click",".mask_zj .cBox1_conS li",function(){
    var id=$(this).attr("id");
    var pid=$(this).attr("pid");
    var pname=$(this).attr("pname");
    var txt=$(this).text();
    txt=pname+">"+txt;
    if($(".mask_zj .channelBox li").size()==0){
      var li='<li class="channel_bq bqImg" id='+id+' pid='+pid+'>'+
                '<span>'+txt+'</span>'+
                '<img class="channel_bq_cancelimg1 cancelImg" src="../anchorResource/img/upl_img2.png" alt="" style="display: none;">'+
              '</li>';
      $(".mask_zj .channelBox").append(li);
      $(this).addClass("selectedF");
    }else if($(".mask_zj .channelBox li").size()<"5"){
      $(".mask_zj .channelBox li").each(function(){
        if($(this).attr("id")!=id){
          isSelected=false;
        }else{
          isSelected=true;
          return;
        }
      });
      if(isSelected==false){
        var li= '<li class="channel_bq bqImg" id='+id+' pid='+pid+'>'+
                  '<span>'+txt+'</span>'+
                  '<img class="channel_bq_cancelimg1 cancelImg" src="../anchorResource/img/upl_img2.png" alt="" style="display: none;">'+
                '</li>';
        $(".mask_zj .channelBox").append(li);
        $(".mask_zj .cBox1_conS li").each(function(){
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
    $("#myIframe", parent.document).attr({"src":"jmANDzj/zj_detail.html?contentId="+contentId});
  });
  
  //根据不同的筛选条件得到不同的专辑列表
  $(document).on("click",".trig_item,.trig_item_li",function(){//选中某个筛选条件
    optfy=2;////选中具体筛选条件后翻页
    destroy(zjData);
    zjData.MobileClass="Chrome";
    zjData.PCDType="3";
    zjData.UserId=userId;
    zjData.PageSize=pageSize;
    zjData.Page=current_page;
    if($(".new_cate li").size()>"0"){
      $(document).find(".new_cate li").each(function(){
        zjData.ChannelId=$(this).attr("id");
      });
    }
    getContentList(zjData);
  });
  
  //根据不同的筛选条件得到不同的专辑列表
  $(document).on("click",".cate_img",function(){//取消某个筛选条件
    optfy=1;//未选中具体筛选条件后翻页
    destroy(zjData);
    zjData.MobileClass="Chrome";
    zjData.PCDType="3";
    zjData.UserId=userId;
    zjData.PageSize=pageSize;
    zjData.Page=current_page;
    if($(".new_cate li").size()>"0"){
      $(document).find(".new_cate li").each(function(){
        zjData.ChannelId=$(this).attr("id");
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
      mode :'click',//默认值是link，可选link或者click
      click :function(current_page){//点击后的回调函数可自定义
        this.selectPage(current_page);
        pagitionBack(current_page);//翻页之后的回调函数
        return false;
      }
    },true);
  };
  /*e--翻页插件初始化*/
  
});


