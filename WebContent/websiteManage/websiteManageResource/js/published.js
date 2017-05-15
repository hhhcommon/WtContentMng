var audioList=[];//节目播放列表
$(function(){
  var rootPath=getRootPath();
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var pageSize=10;//每页记录数
  var allCount=0;//总记录数
  var optfy=1;//optfy=1未选中具体筛选条件前翻页,optfy=2选中具体筛选条件后翻页
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchWord="";
  
  var userId='0579efbaf9a9';//W003
  var flowflag='2';//2已发布
  
  /*日期处理--日历插件*/
  $("#time .input-daterange").datepicker({keyboardNavigation:!1,forceParse:!1,autoclose:!0});
  
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
  
  /*s--翻页/搜索*/
  //翻页之后的回调函数
  function pagitionBack(current_page){
    if(($(".new_cate li").size()>"0")||(($(".startPubTime").val())&&($(".endPubTime").val()))){
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
    destroy(data);
    var nodes=zTreeObj.getSelectedNodes();//当前被勾选的节点集合  
    data.ChannelId=nodes[0].id;
    data.UserId=userId;
    data.ContentFlowFlag=flowflag;
    data.PageSize=pageSize;
    data.Page=current_page;
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(optfy==2){//optfy=2选中具体筛选条件后翻页
      $(document).find(".new_cate li").each(function(){
        if($(".new_cate li").size()>="0"){
          var pId=$(this).attr("pid");
          var id=$(this).attr("id");
          if(pId=="type"){
            if(id!='ALL') data.MediaType=$(this).attr("id");
          }else{
            data.PubliusherId=$(this).attr("id");
          }
        }
      });
      if(($(".startPubTime").val())&&($(".endPubTime").val())){
        data.BeginContentPubTime=new Date($(".startPubTime").val()).getTime();
        data.EndContentPubTime=new Date($(".endPubTime").val()).getTime();
      }
    }
    if(seaFy==1){//seaFy=1未搜索关键词前翻页
      getContentList(data);
    }else{//seaFy=2搜索列表加载出来后翻页
      data.SearchWord=searchWord;
      getSearchList(data);
    }
  }
  
  //根据不同的筛选条件得到不同的内容列表
  $(document).on("click",".trig_item",function(){
    optfy=2;//选中具体筛选条件后翻页
    anew(flowflag);//在每次加载具体的资源列表时候的公共方法
    if(searchWord==""){
      seaFy=1;//seaFy=1未搜索关键词前翻页
      getContentList(data);
    }else{
      seaFy=2;//seaFy=2搜索列表加载出来后翻页
      data.SearchWord=searchWord;
      getSearchList(data);
    }
  });
  
  //在每次加载具体的资源列表时候的公共方法
  function anew(flowflag){
    destroy(data);
    var nodes=zTreeObj.getSelectedNodes();//当前被勾选的节点集合  
    data.ChannelId=nodes[0].id;
    current_page=1;
    data.UserId=userId;
    data.PageSize=pageSize;
    data.Page=current_page;
    data.ContentFlowFlag=flowflag;
    if($(".new_cate li").size()>"0"){
      optfy=2;//选中具体筛选条件后翻页
      $(document).find(".new_cate li").each(function(){
        var pId=$(this).attr("pid");
        var id=$(this).attr("id");
        if(pId=="type"){
          if(id!='ALL') data.MediaType=$(this).attr("id");
        }else{
          data.PubliusherId=$(this).attr("id");
        }
      });
    }else{
      optfy=1;//未选中具体筛选条件后翻页
    }
    if(($(".startPubTime").val())&&($(".endPubTime").val())){
      optfy=2;//选中具体筛选条件后翻页
      data.BeginContentPubTime=new Date($(".startPubTime").val()).getTime();
      data.EndContentPubTime=new Date($(".endPubTime").val()).getTime();
    }else{
      optfy=1;//未选中具体筛选条件后翻页
    }
  }
  
  //点击取消所选的筛选条件
  $(document).on("click",".cate_img",function(){
    anew(flowflag);//在每次加载具体的资源列表时候的公共方法
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(searchWord==""){
      seaFy=1;//seaFy=1未搜索关键词前翻页
      getContentList(data);
    }else{
      seaFy=2;//seaFy=2搜索列表加载出来后翻页
      data.SearchWord=searchWord;
      getSearchList(data);
    }
  });
  
  //点击筛选条件日期附近的确定按钮
  $(".ensure").on("click",function(){
    var st=new Date($(".startPubTime").val()).getTime();
    var et=new Date($(".endPubTime").val()).getTime();
    if(st>et){
      alert("你选择的时间段不合法，请重新选择");
      $(".startPubTime,.endPubTime").val("");
    }else{
      optfy=2;//选中具体筛选条件后翻页
      anew(flowflag);
      searchWord=$.trim($(".ri_top_li2_inp").val());
      if(searchWord==""){
        seaFy=1;//seaFy=1未搜索关键词前翻页
        getContentList(data);
      }else{
        seaFy=2;//seaFy=2搜索列表加载出来后翻页
        data.SearchWord=searchWord;
        getSearchList(data);
      }
    }
  });
  
  //点击筛选条件日期附近的清除按钮
  $(".clean").on("click",function(){
    $(".startPubTime,.endPubTime").val("");
    anew(flowflag);//在每次加载具体的资源列表时候的公共方法
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(searchWord==""){
      seaFy=1;//seaFy=1未搜索关键词前翻页
      getContentList(data);
    }else{
      seaFy=2;//seaFy=2搜索列表加载出来后翻页
      data.SearchWord=searchWord;
      getSearchList(data);
    }
  });
  
  //点击键盘上的enter事件
  $(document).keydown(function(e){
    e = e || window.event;
    var keycode = e.which ? e.which : e.keyCode;
    if(keycode == 13){//键盘上的enter
      $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
      $(".startPubTime,.endPubTime").val("");
      $("#source,#type").show();
      searchList();//搜索
    }
  });
  
  //点击小的搜索图标
  $(".ri_top_li2_img").on("click",function(){
    $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
    $(".startPubTime,.endPubTime").val("");
    $("#source,#type").show();
    searchList();//搜索
  });
  
  //搜索
  function searchList(){
    destroy(data);
    var nodes=zTreeObj.getSelectedNodes();//当前被勾选的节点集合  
    data.ChannelId=nodes[0].id;
    data.UserId=userId;
    data.ContentFlowFlag=flowflag;
    data.PageSize=pageSize;
    current_page=1;
    data.Page=current_page;
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(searchWord==""){
      seaFy=1;//seaFy=1未搜索关键词前翻页
      getContentList(data);  
    }else{
      seaFy=2;//seaFy=2搜索列表加载出来后翻页
      data.SearchWord=searchWord;
      getSearchList(data);//得到搜索列表 
    }
  }
  
  //得到搜索列表 
  function getSearchList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"content/searchContents.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(dataParam),
      beforeSend:function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;height:300px;line-height:200px;'>正在加载内容列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        $(".ri_top3_con").html("");
        $(".all_check").attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");
        $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
        if(resultData.ReturnType=="1001"){
          allCount=resultData.AllCount;
          loadContentList(resultData);//加载来源的筛选条件
        }else{
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;height:300px;line-height:200px;'>没有找到内容</div>");
          allCount="0";
        }
        $(".fixed").show();
        contentCount=(allCount%pageSize==0)?(allCount/pageSize):(Math.ceil(allCount/pageSize));
        pagitionInit(contentCount,allCount,dataParam.Page);//init翻页
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;height:300px;line-height:200px;'>加载内容列表发生错误:"+jqXHR.status+"</div>");
        $('.shade', parent.document).hide();
      }
    });
  }
  /*e--翻页/搜索*/
  
  /*s--ztree的操作结合*/
  var zTreeObj,treeNode,treeNodeId;
  
  //初始化页面树样式
  zTreeObj=$.fn.zTree.init($("#ztree"),{
    view:{
      selectedMulti:false,//是否允许同时选中多个节点
      showLine: false,//不显示连线
      showIcon: false//不显示图标
    },
    edit:{
      enable: true,//是否开启异步模式
      showRemoveBtn: false,//是否显示删除按钮
      showRenameBtn: false//是否显示编辑名称按钮
    },
    async:{
      enable:true//强行异步加载父节点的子节点
    },
    data:{
      simpleData:{
        enable: true
      }
    },
    callback:{
      onClick:requestList,//选中页面上某一分类，加载对应的列表详情
    }
  });
  
  //加载树
  var loadTreeData=[{ChannelId:"",TreeViewType:"zTree"}];
  loadTree(loadTreeData);
  function loadTree(loadData){
    var _url=rootPath+"baseinfo/getChannelTree4View.do";
    var i=0;
    loadRecursion(0);
  
    function loadRecursion(index){
      if (index==loadTreeData.length) return;
      $.ajax({
        type: "POST",    
        url: _url,
        dataType: "json",
        data: loadData[index++],
        success: function(jsonData){
          if(jsonData.ReturnType=="1001"){
            zTreeObj.addNodes(null,jsonData.Data.children[0].children,false);
          }
          loadRecursion(index);
        },
        error:function(jqXHR){
          alert("加载栏目树发生错误" + jqXHR.status);
        }
      });
    }
  }
  
  //选中树上的栏目后的回调函数
  var data={};
  function requestList(event,treeId,treeNode){
    destroy(data);
    data.UserId=userId;
    data.ContentFlowFlag=flowflag;
    current_page=1;
    data.Page=current_page;
    data.PageSize="10";
    var nodes=zTreeObj.getSelectedNodes();//当前被勾选的节点集合  
    data.ChannelId=nodes[0].id;
    getContentList(data);//请求加载内容列表
  }
  
  //请求加载内容列表
  function getContentList(data){
    $.ajax({
      type:"POST",
      url:rootPath+"content/getContents.do",
      dataType:"json",
      cache:false,
//    async:false,
      data:JSON.stringify(data),
      beforeSend:function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;height:300px;line-height:200px;'>正在加载内容列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        $(".ri_top3_con").html(" ");//清空内容列表
        if(resultData.ReturnType=="1001"){
          allCount=resultData.AllCount;
          loadContentList(resultData);//加载内容列表
        }else{
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;height:300px;line-height:200px;'>没有找到内容</div>");
          allCount="0";
        }
        $(".fixed").show();
        contentCount=(allCount%pageSize==0)?(allCount/pageSize):(Math.ceil(allCount/pageSize));
        pagitionInit(contentCount,allCount,data.Page);//init翻页
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;height:300px;line-height:200px;'>加载内容列表发生发生错误："+jqXHR.status+"</div>");
        $('.shade', parent.document).hide();
      }
    });
  }
  //加载内容列表
  function loadContentList(resultData){
    $(".locker").removeClass("dis");
    audioList=[];//每次加载数据之前先清空存数据的数组
    for(var i=0;i<resultData.ResultList.length;i++){
      var listBox='<div class="rtc_listBox" contentId='+resultData.ResultList[i].ContentId+'>'+
                '<img src="../websiteManageResource/img/checkbox1.png" alt="" class="rtcl_img_check fl checkbox_img checkbox1"/>'+
                '<div class="rtcl_img fl">'+
                  '<img src="" alt="内容图片" />'+
                  '<div class="btn_player dis">'+
                    '<i class="icon"></i>'+
                  '</div>'+
                '</div>'+
                '<div class="rtcl_con fl">'+
                  '<p class="rtcl_con_p ellipsis"></p>'+
                  '<p class="sequ_num"></p>'+
                  '<div class="rtcl_con_desc">'+
                    '<span class="rtcl_con_desc1 fl">主播：</span>'+
                    '<span class="rtcl_con_desc2 fl ellipsis"></span>'+
                  '</div>'+
                '</div>'+
                '<ul class="rtcl_con_channel1s ellipsis fl">'+
//                '<li class="rtcl_con_channel2">电台丛林--故事</li>'+
//                '<li class="rtcl_con_channel2">电台丛林--故事</li>'+
//                '<li class="rtcl_con_channel2">电台丛林--故事</li>'+
//                '<li class="rtcl_con_channel2">电台丛林--故事</li>'+
//                '<li class="rtcl_con_channel2">电台丛林--故事</li>'+
                '</ul>'+
                '<span class="source_form fl"></span>'+
                '<span class="audio_time fl"></span>'+
                '<div class="opetype1 fl" contentId='+resultData.ResultList[i].ContentId+'>'+
                  '<div class="carousel">轮播图</div>'+
                  '<div class="top">置&nbsp;&nbsp;&nbsp;顶</div>'+
                  '<div class="revoke">撤&nbsp;&nbsp;&nbsp;回</div>'+
                '</div>'+
              '</div>';
      $(".ri_top3_con").append(listBox);
      if(resultData.ResultList[i].ContentImg) $(".rtcl_img img").eq(i).attr("src",resultData.ResultList[i].ContentImg);
      else $(".rtcl_img img").eq(i).attr("src","http://www.wotingfm.com:908/CM/resources/images/default.png");
      $(".rtcl_con_p").eq(i).text(resultData.ResultList[i].ContentName?(resultData.ResultList[i].ContentName):"未知");
      if(resultData.ResultList[i].MediaType=='wt_MediaAsset'){//加载节目
        $(".sequ_num").eq(i).text((resultData.ResultList[i].ContentSeqName)?("专辑:《"+resultData.ResultList[i].ContentSeqName+"》"):"专辑:《未知》");
        $(".sequ_num").eq(i).attr("seqId",resultData.ResultList[i].ContentSeqId);
        if(resultData.ResultList[i].ContentPlayUrl){
          var audioObj={};
          audioObj.title=resultData.ResultList[i].ContentName;
          audioObj.playUrl=resultData.ResultList[i].ContentPlayUrl;
          audioList.push(audioObj);
          $(".rtc_listBox").eq(i).addClass("playurl").attr("playurl",resultData.ResultList[i].ContentPlayUrl);
          $(".rtc_listBox").eq(i).attr("playurlName",resultData.ResultList[i].ContentName);
        }
      }else{//加载专辑
        $(".sequ_num").eq(i).text((resultData.ResultList[i].MediaSize)?(resultData.ResultList[i].MediaSize+"个声音"):"0个声音");
        //如果是专辑，ajax请求获取专辑的详细信息
        var seqId=resultData.ResultList[i].ContentId;
        getSeqInfo(seqId,i);
      }
      $(".sequ_num").eq(i).attr("mediaType",resultData.ResultList[i].MediaType);
      if(resultData.ResultList[i].ContentPersons){
        $(".rtcl_con_desc2").eq(i).text((resultData.ResultList[i].ContentPersons[0].PerName)?(resultData.ResultList[i].ContentPersons[0].PerName):"保密");
      }
      if(resultData.ResultList[i].ContentPubChannels){
        for(var j=0;j<resultData.ResultList[i].ContentPubChannels.length;j++){
          var li='<li class="rtcl_con_channel2" id='+resultData.ResultList[i].ContentPubChannels[j].ChannelId+'>【'+resultData.ResultList[i].ContentPubChannels[j].ChannelName+'】</li>';
          $(".rtcl_con_channel1s").eq(i).append(li);
        }
      }
      $(".source_form").eq(i).text((resultData.ResultList[i].ContentPublisher)?(resultData.ResultList[i].ContentPublisher):"未知");
      var contenttime=resultData.ResultList[i].ContentTime;
      contenttime=new Date(parseInt(contenttime)).toLocaleString('chinese',{hour12:false}).replace(/\//g, "-");
      $(".audio_time").eq(i).text((contenttime)?(contenttime):"0000-00-00:00:00:00");
      if(resultData.ResultList[i].ContentPubChannels){
        var chIds="";//发布栏目的id集合
        for(var j=0;j<resultData.ResultList[i].ContentPubChannels.length;j++){
          if(chIds==""){
            chIds=resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }else{
            chIds+=","+resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }
        }
        $(".sequ_num").eq(i).attr("chIds",chIds);
      }
    }
    $("#audioIframe").attr("src","../globalplayer.html");
  }
  
  //如果是专辑，带到专辑的的声音列表，获取第一个声音的播放地址
  function getSeqInfo(seqId,i){
    var data1={"UserId":userId,
              "ContentId":seqId,
              "MediaType":"wt_SeqMediaAsset"
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/getContentInfo.do",
      dataType:"json",
      cache:false,
      async:false,
      data:JSON.stringify(data1),
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          if(resultData.SubList[0]){
            if(resultData.SubList[0].ContentPlay){
              var audioObj={};
              audioObj.title=resultData.SubList[0].ContentName;
              audioObj.playUrl=resultData.SubList[0].ContentPlay;
              audioList.push(audioObj);
              $(".rtc_listBox").eq(i).addClass("playurl");
            }
          }
        }
      },
      error:function(jqXHR){
        alert("得到专辑信息发生错误:"+ jqXHR.status);
      }
    });
  }
  /*e--ztree的操作集合*/
 
  /*s--点击内容名字，进入内容详情*/
  $(document).on("click",".rtcl_con_p",function(){
    var mediaType=$(this).siblings(".sequ_num").attr("mediatype");
    var contentId=$(this).parent(".rtcl_con").siblings(".opetype1").attr("contentId");
    if(mediaType=="wt_MediaAsset"){//节目
      var seqId=$(this).siblings(".sequ_num").attr("seqId");
      $("#myIframe", parent.document).attr({"src":"contentsManage/jm_detail.html?contentId="+contentId+"&&seqId="+seqId});
    }else if(mediaType=="wt_SeqMediaAsset"){//专辑
      $("#myIframe", parent.document).attr({"src":"contentsManage/zj_detail.html?contentId="+contentId});
    }
  });
  /*e--点击内容名字，进入内容详情*/
 
 
  /*s--点击撤回相关操作*/
  //点击批量撤回
  var contentIds=[];
  $(document).on("click",".opetype",function(){
    contentIds=[];
    $(".other_reason").val(" ");
    $(".ri_top3_con .rtc_listBox").each(function(){
      if($(this).children(".rtcl_img_check").hasClass("checkbox1")){//未选中
        
      }else{//已选中
        var contentList={};
        contentList.Id=$(this).children(".opetype1").attr("contentId"); 
        contentList.ChannelIds=$(this).children(".rtcl_con").children(".sequ_num").attr("chIds");
        if($(this).children(".rtcl_con").children(".sequ_num").attr("mediatype")=="wt_MediaAsset"){//节目
          contentList.MediaType="AUDIO";
        }else if($(this).children(".rtcl_con").children(".sequ_num").attr("mediatype")=="wt_SeqMediaAsset"){//专辑
          contentList.MediaType="SEQU";
        }
        contentIds.push(contentList);
      }
    });
    if(contentIds.length!=0){//已有选中内容
      $("body").css("overflow-x","hidden");
      $(".nopass_masker").removeClass("dis");
    }
  });
  
  //点击撤回
  $(document).on("click",".revoke",function(){
    contentIds=[];
    $(".other_reason").val(" ");
    var contentList={};
    contentList.Id=$(this).parent(".opetype1").attr("contentId"); 
    contentList.ChannelIds=$(this).parent(".opetype1").siblings(".rtcl_con").children(".sequ_num").attr("chIds");
    if($(this).parent(".opetype1").siblings(".rtcl_con").children(".sequ_num").attr("mediatype")=="wt_MediaAsset"){//节目
      contentList.MediaType="AUDIO";
    }else if($(this).parent(".opetype1").siblings(".rtcl_con").children(".sequ_num").attr("mediatype")=="wt_SeqMediaAsset"){//专辑
      contentList.MediaType="SEQU";
    }
    contentIds.push(contentList);
    if(contentIds.length!=0){//已有选中内容
      $("body").css("overflow-x","hidden");
      $(".nopass_masker").removeClass("dis");
    }
  });
  
  //点击撤回原因弹出页面上的确定按钮
  $(".nc_txt7").on("click",function(){
    var reDesc=[];
    $(".nc_reasons .nc_checkimg").each(function(){
      if($(this).hasClass("checkbox1")){//未选中这个原因
        
      }else{//选中这个原因
        var red={};
        red.ReDescn=$(this).siblings(".nc_txt2").text();
        reDesc.push(red);
      }
    });
    if($(".other_reason").val()!=" "){
      var red={};
      red.ReDescn=$(".other_reason").val();
      reDesc.push(red);
    }
    var data2={"UserId":userId,
               "ContentIds":contentIds,
               "ReDescn":reDesc,
               "OpeType":"revoke"
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/updateContentStatus.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(data2),
      beforeSend:function(){
        $('.nc_txt7').attr("disabled","disabled");
      },
      success: function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".checkbox_img").attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");
          $(".nopass_masker").addClass("dis");
          $("body").css("overflow-x","auto");
          $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
          $(".all_check").addClass("checkbox1").attr({"src":"../websiteManageResource/img/checkbox1.png"});
          getContentList(data);
        }else{
          alert("撤回内容失败:"+resultData.Message);
        }
        $('.nc_txt7').removeAttr("disabled");
      },
      error: function(jqXHR){
        alert("获取数据发生错误:"+jqXHR.status);
        $('.nc_txt7').removeAttr("disabled");
      }     
    });
  })

  //点击撤回原因弹出页面上的关闭按钮
  $(document).on("click",".nh_span2",function(){
    $("body").css("overflow-x","auto");
    $(".checkbox_img").attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");
    $(".nopass_masker").addClass("dis");
  });
  /*e--点击撤回相关操作*/
  
});
