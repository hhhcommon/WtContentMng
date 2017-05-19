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
  var contentflowflag='1';//1待审核  
  
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
    if(nodes.length!=0) data.ChannelId=nodes[0].id;
    data.UserId=userId;
    data.ContentFlowFlag=contentflowflag;
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
    anew(contentflowflag);//在每次加载具体的资源列表时候的公共方法
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
  function anew(contentflowflag){
    destroy(data);
    var nodes=zTreeObj.getSelectedNodes();//当前被勾选的节点集合
    if(nodes.length!=0) data.ChannelId=nodes[0].id;
    current_page=1;
    data.UserId=userId;
    data.PageSize=pageSize;
    data.Page=current_page;
    data.ContentFlowFlag=contentflowflag;
    if($(".new_cate li").size()>"0"){
      optfy=2;//选中具体筛选条件后翻页
      $(document).find(".new_cate li").each(function(){
        var pId=$(this).attr("pid");
        var id=$(this).attr("id");
        if(pId=="type"){
          if(id!='ALL') data.MediaType=$(this).attr("id");
        }else{
          data.SourceId=$(this).attr("id");
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
    anew(contentflowflag);//在每次加载具体的资源列表时候的公共方法
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
      anew(contentflowflag);
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
    anew(contentflowflag);//在每次加载具体的资源列表时候的公共方法
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
    if(nodes.length!=0) data.ChannelId=nodes[0].id;
    data.UserId=userId;
    data.ContentFlowFlag=contentflowflag;
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
      url:rootPath+"CM/content/searchContents.do",
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
        $(".jmsum").text(" ").addClass("dis");
        $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
        $(".rto_play").css({"color":"#000","background":"#ddd"});
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
    var _url=rootPath+"CM/baseinfo/getChannelTree4View.do";
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
          alert("加载栏目树发生错误:" + jqXHR.status);
        }
      });
    }
  }
  
  //选中树上的栏目后的回调函数
  var data={};
  function requestList(event,treeId,treeNode){
    destroy(data);
    data.UserId=userId;
    data.ContentFlowFlag=contentflowflag;
    current_page=1;
    data.Page=current_page;
    data.PageSize=pageSize;
    var nodes=zTreeObj.getSelectedNodes();//当前被勾选的节点集合  
    if(nodes.length!=0) data.ChannelId=nodes[0].id;
    getContentList(data);//请求加载内容列表
  }
  
  //请求加载内容列表
  function getContentList(data){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/content/getContents.do",
      dataType:"json",
      cache:false,
      data:JSON.stringify(data),
      beforeSend:function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;height:300px;line-height:200px;'>正在加载内容列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        $(".ri_top3_con").html("");
        $(".all_check").attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");
        $(".jmsum").text(" ").addClass("dis");
        $(".opetype,.rto_play").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
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
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;height:300px;line-height:200px;'>加载内容列表发生错误:"+jqXHR.status+"</div>");
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
                    '<div class="rtcl_con_p fl">'+
                      '<p class="rtcl_con_p1 ellipsis"></p>'+
                      '<div class="rtcl_img fl">'+
                        '<img src="" alt="内容图片" />'+
                        '<div class="btn_player dis">'+
                          '<i class="icon"></i>'+
                        '</div>'+
                      '</div>'+
                      '<div class="rtcl_con fl">'+
                        '<p class="sequ_num ellipsis"></p>'+
                        '<p class="sequ_name ellipsis"></p>'+
                        '<div class="tag">'+
                          '<span class="tag1 fl">标签:</span>'+
                          '<ul class="rtcl_con_tags ellipsis fl"></ul>'+
                        '</div>'+
                        '<div class="rtcl_con_desc">'+
                          '<span class="rtcl_con_desc1 fl">简介:</span>'+
                          '<div class="rtcl_con_desc2 fl"></div>'+
                        '</div>'+
                      '</div>'+
                    '</div>'+
                    '<span class="source_form fl"></span>'+
                    '<span class="audio_time fl"></span>'+
                  '</div>';
      $(".ri_top3_con").append(listBox);
      if(resultData.ResultList[i].ContentImg) $(".rtcl_img img").eq(i).attr("src",resultData.ResultList[i].ContentImg);
      else $(".rtcl_img img").eq(i).attr("src","http://www.wotingfm.com:908/CM/resources/images/default.png");
      $(".rtcl_con_p1").eq(i).text(resultData.ResultList[i].ContentName?(resultData.ResultList[i].ContentName):"未知");
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
      }
      $(".sequ_num").eq(i).attr("mediaType",resultData.ResultList[i].MediaType);
      if(resultData.ResultList[i].ContentPersons){
        $(".sequ_name").eq(i).text((resultData.ResultList[i].ContentPersons[0].PerName)?("主播: "+resultData.ResultList[i].ContentPersons[0].PerName):"主播:保密");
      }
      if(resultData.ResultList[i].ContentPubChannels){
        var chIds="";//发布栏目的id集合
        for(var j=0;j<resultData.ResultList[i].ContentPubChannels.length;j++){
          var li='<li class="rtcl_con_tags1 fl" id='+resultData.ResultList[i].ContentPubChannels[j].ChannelId+'>'+resultData.ResultList[i].ContentPubChannels[j].ChannelName+'</li>';
          $(".rtcl_con_tags").eq(i).append(li);
          if(chIds==""){
            chIds=resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }else{
            chIds+=","+resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }
        }
        $(".rtcl_img").eq(i).attr("chIds",chIds);
      }
      $(".rtcl_con_desc2").eq(i).text((resultData.ResultList[i].ContentDesc)?(resultData.ResultList[i].ContentDesc):"暂无");
      $(".source_form").eq(i).text((resultData.ResultList[i].ContentPublisher)?(resultData.ResultList[i].ContentPublisher):"未知");
      var contenttime=resultData.ResultList[i].ContentTime;
      contenttime=new Date(parseInt(contenttime)).toLocaleString('chinese',{hour12:false}).replace(/\//g, "-");
      $(".audio_time").eq(i).text((contenttime)?(contenttime):"0000-00-00:00:00:00");
    }
    $(".rto_play").removeAttr("disabled").css({"color":"#fff","background":"#ffa634"});
    $("#audioIframe").attr("src","../globalplayer.html");
}
  /*e--ztree的操作集合*/
  
  /*s--点击节目名字，进入节目详情*/
//$(document).on("click",".rtcl_con_p1",function(){
//  var mediaType=$(this).siblings(".rtcl_con").children(".sequ_num").attr("mediatype");
//  var contentId=$(this).parent(".rtcl_con_p").parent(".rtc_listBox").attr("contentId");
//  if(mediaType=="wt_MediaAsset"){//节目
//    var seqId=$(this).siblings(".rtcl_con").children(".sequ_num").attr("seqId");
//    $("#myIframe", parent.document).attr({"src":"contentsManage/jm_detail.html?contentId="+contentId+"&&seqId="+seqId});
//  }else if(mediaType=="wt_SeqMediaAsset"){//专辑
//    $("#myIframe", parent.document).attr({"src":"contentsManage/zj_detail.html?contentId="+contentId});
//  }
//});
  /*e--点击节目名字，进入节目详情*/
 
  /*s--点击专辑名字，进入专辑详情*/
//$(document).on("click",".sequ_num",function(){
//  var contentId=$(this).attr("seqId");
//  $("#myIframe", parent.document).attr({"src":"contentsManage/zj_detail.html?contentId="+contentId});
//});
  /*e--点击专辑名字，进入专辑详情*/
  
  /*s--全部播放*/
  $(".rto_play").on("click",function(){
    $(".locker").click();
  });
  /*e--全部播放*/
  
  /*s--通过发布*/
  var contentIds=[];
  $(".rto_pass").on("click",function(){
    contentIds=[];
    $(".ri_top3_con .rtc_listBox").each(function(){
      if($(this).children(".rtcl_img_check").hasClass("checkbox1")){//未选中
        
      }else{//已选中
        var contentList={};
        contentList.Id=$(this).attr("contentId");
        if($(this).children(".rtcl_con_p").children(".rtcl_con").children(".sequ_num").attr("mediatype")=="wt_MediaAsset"){//节目
          contentList.MediaType="AUDIO";
        }else{
          contentList.MediaType="SEQU";
        }
        contentList.ChannelIds=$(this).children(".rtcl_con_p").children(".rtcl_img").attr("chIds");
        contentIds.push(contentList);
      }
    });
    if(contentIds.length!=0){//选中内容
      var _data2={"UserId":userId,
                  "ContentIds":contentIds,
                  "OpeType":$(this).attr("opetype")
      };
      var _url2=rootPath+"CM/content/updateContentStatus.do";
      optList(_url2,_data2);//申请发布通过
    }
  });
  /*e--通过发布*/
 
  /*s--不通过发布*/ 
  var contentids=[];
  $(".rto_nopass").on("click",function(){
    contentids=[];
    $(".nc_txt1").text(" ");
    $(".other_reason").val(" ");
    $(".ri_top3_con .rtc_listBox").each(function(){
      if($(this).children(".rtcl_img_check").hasClass("checkbox1")){//未选中
        
      }else{//已选中
        var contentList={};
        contentList.Id=$(this).attr("contentId");
        if($(this).children(".rtcl_con_p").children(".rtcl_con").children(".sequ_num").attr("mediatype")=="wt_MediaAsset"){//节目
          contentList.MediaType="AUDIO";
        }
        contentList.ChannelIds=$(this).children(".rtcl_con_p").children(".rtcl_img").attr("chIds");
        contentids.push(contentList);
      }
    });
    if(contentids.length!=0){//选中内容
      $("body").css("overflow-x","hidden");
      $(".nc_txt1").text("选择了"+contentids.length+"个节目，您确认所选的节目不予发布么？");
      $(".nopass_masker").removeClass("dis");
    }
  });
  
  //点击不通过发布原因页面的确定按钮
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
    var data4={ "UserId":userId,
                "ContentIds":contentids,
                "OpeType":$(".rto_nopass").attr("opetype"),
                "ReDescn":reDesc
    };
    $.ajax({
      type: "POST",
      url:rootPath+"CM/content/updateContentStatus.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(data4),
      beforeSend:function(){
        $('.nc_txt7').attr("disabled","disabled");
      },
      success: function(resultData){
        if(resultData.ReturnType=="1001"){
          alert("提交不予发布的原因成功");
          $(".checkbox_img").attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");
          $(".nopass_masker").hide();
          $("body").css("overflow-x","auto");
          $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
          $(".rto_play").css({"color":"#000","background":"#ddd"});
          $(".all_check").addClass("checkbox1").attr({"src":"../websiteManageResource/img/checkbox1.png"});
          getContentList(data);
        }else{
          alert("提交不予发布的原因失败:"+resultData.Message);
        }
        $('.nc_txt7').removeAttr("disabled");
      },
      error: function(jqXHR){
        alert("提交不予发布的原因发生错误:"+jqXHR.status);
        $('.nc_txt7').removeAttr("disabled");
      }
    });
  })
  
  //点击不通过页面上的取消按钮
  $(document).on("click",".nh_span2",function(){
    $("body").css("overflow-x","auto");
    $(".checkbox_img").attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");
    $(".nopass_masker").hide();
  });
  /*e--不通过发布*/
  
  //通过发布申请
  function optList(_url,_data){
    $.ajax({
      type:"POST",
      url:_url,
      dataType:"json",
      cache:false, 
      data:JSON.stringify(_data),
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          if(_data.OpeType=='pass'){//点击的是发布
            $(".pass_note").show();
            setTimeout(function(){$(".pass_note").hide();},2000);
          }
          $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
          $(".rto_play").css({"color":"#000","background":"#ddd"});
          $(".all_check").addClass("checkbox1").attr({"src":"../websiteManageResource/img/checkbox1.png"});
          getContentList(data);//请求加载内容列表
        }else{
          alert("同意发布失败:"+resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("同意发布发生错误:"+jqXHR.status);
      }
    });
  }
  
});
  
