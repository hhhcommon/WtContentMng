var audioList=[];//节目播放列表
$(function(){
  /*s--内容与轮播图切换*/
  $(".nav").on("click","div",function(){
    $(this).addClass("selected").siblings().removeClass("selected");
    var _index=$(this).index();
    $(".navcon").eq(_index).removeClass("dis").siblings(".navcon").addClass("dis");
    if(_index==1){//点击的是轮播图，收起筛选隐藏
      $(".ri_top_li3").addClass("dis");
    }else{
      $(".ri_top_li3").removeClass("dis");
    }
  });
  /*e--内容与轮播图切换*/
  
  /*e--右边边框的高度赋给左边*/
  
  var rootPath=getRootPath();
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var allCount=0;//总记录数
  var optfy=1;//optfy=1未选中具体筛选条件前翻页,optfy=2选中具体筛选条件后翻页
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchWord="";//搜索词
  
  var userId='0579efbaf9a9';//W003
  var flowflag='2';
  var channelId='';//栏目id
  
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
    data.PageSize="10";
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
    data.PageSize="10";
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
    data.PageSize="10";
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
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载节目列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        $(".ri_top3_con").html("");
        $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
        if(resultData.ReturnType=="1001"){
          allCount=resultData.AllCount;
          contentCount=(allCount%10==0)?(allCount/10):(Math.ceil(allCount/10));
          loadContentList(resultData);//加载来源的筛选条件
        }else{
          $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到节目</div>");
          allCount="0";
          contentCount=(allCount%10==0)?(allCount/10):(Math.ceil(allCount/10));
        }
        $(".fixed").show();
        pagitionInit(contentCount,allCount,dataParam.Page);//init翻页
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  /*e--翻页/搜索*/
  
  /*s--销毁obj对象的key-value*/
  function destroy(obj){
    for(var key in obj){//清空对象
      delete obj[key];
    }
  }
  /*e--销毁obj对象的key-value*/
  
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
            zTreeObj.addNodes(null,jsonData.Data.children,false);
          }
          loadRecursion(index);
        },
        error:function(jqXHR){
          alert("发生错误" + jqXHR.status);
        }
      });
    }
  }
  
  //选中树上的栏目后的回调函数
  var data={};
  function requestList(event,treeId,treeNode){
    destroy(data);
    data.UserId=userId;
    data.PCDType="3";
    data.FlagFlow=flowflag;
    current_page=1;
    data.Page=current_page;
    data.PageSize="10";
    var nodes=zTreeObj.getSelectedNodes();//当前被勾选的节点集合  
    data.ChannelId=nodes[0].id;
    getContentList(data);//请求加载内容列表
    
    //切换栏目时还原状态
    $(".all").css("display","none").children(".new_cate").html("");
    $(".startPubTime,.endPubTime").val("");
    $("#source,#type").show();
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
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载内容列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        $(".ri_top3_con").html(" ");//清空内容列表
        if(resultData.ReturnType=="1001"){
          allCount=resultData.AllCount;
          contentCount=(allCount%10==0)?(allCount/10):(Math.ceil(allCount/10));
          loadContentList(resultData);//加载内容列表
        }else{
          $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到内容</div>");
          allCount="0";
          contentCount=(allCount%10==0)?(allCount/10):(Math.ceil(allCount/10));
        }
        $(".fixed").show();
        pagitionInit(contentCount,allCount,data.Page);//init翻页
        $('.shade', parent.document).hide();
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  //加载内容列表
  function loadContentList(resultData){
    $(".locker").removeClass("dis");
    audioList=[];//每次加载数据之前先清空存数据的数组
    for(var i=0;i<resultData.ResultList.length;i++){
      var listBox='<div class="rtc_listBox">'+
                '<img src="img/checkbox1.png" alt="" class="rtcl_img_check fl checkbox_img checkbox1"/>'+
                '<div class="rtcl_img fl">'+
                  '<img src="" alt="节目图片" />'+
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
      else $(".rtcl_img img").eq(i).attr("src","http://wotingfm.com:908/CM/resources/images/default.png");
      $(".rtcl_con_p").eq(i).text(resultData.ResultList[i].ContentName?(resultData.ResultList[i].ContentName):"未知");
      if(resultData.ResultList[i].MediaType=='wt_MediaAsset'){//加载节目
        $(".sequ_num").eq(i).text((resultData.ResultList[i].ContentSeqName)?("专辑:《"+resultData.ResultList[i].ContentSeqName+"》"):"专辑：《未知》");
        $(".sequ_num").eq(i).attr("seqId",resultData.ResultList[i].ContentSeqId);
        if(resultData.ResultList[i].ContentPlayUrl){
          var audioObj={};
          audioObj.title=resultData.ResultList[i].ContentName;
          audioObj.playUrl=resultData.ResultList[i].ContentPlayUrl;
          audioList.push(audioObj);
          $(".rtc_listBox").eq(i).addClass("playurl");
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
    $("#audioIframe").attr("src","globalplayer.html");
  }
  
  //如果是专辑，带到专辑的的声音列表，获取第一个声音的播放地址
  function getSeqInfo(seqId,i){
    var data={"UserId":userId,
              "Page":"1",
              "PageSize":"10",
              "ContentId":seqId,
              "MediaType":"wt_SeqMediaAsset"
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/getContentInfo.do",
      dataType:"json",
      cache:false,
      async:false,
      data:JSON.stringify(data),
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          if(resultData.SubList[0].ContentPlay){
            var audioObj={};
            audioObj.title=resultData.SubList[0].ContentName;
            audioObj.playUrl=resultData.SubList[0].ContentPlay;
            audioList.push(audioObj);
            $(".rtc_listBox").eq(i).addClass("playurl");
          }
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  /*e--ztree的操作集合*/
 
  /*s--点击轮播图*/
  $(document).on("click",".carousel",function(){
    var contentId=$(this).parent(".opetype1").attr("contentId");
    var contenttxt=$(this).parent(".opetype1").siblings(".rtcl_con").children(".rtcl_con_p").text();
    var nodes=zTreeObj.getSelectedNodes();//当前被勾选的节点集合  
    channelId=nodes[0].id;
    var mediatype=$(this).parent(".opetype1").siblings(".rtcl_con").children(".sequ_num").attr("mediatype");
    if(mediatype=="wt_MediaAsset"){//节目
      mediatype="AUDIO";
    }else if(mediatype=="wt_SeqMediaAsset"){//专辑
      mediatype="SEQU ";
    }else{//电台
      mediatype="RADIO ";
    }
    console.log(contentId,contenttxt,channelId,mediatype);
    $(".cm_content3").text("<"+contenttxt+">").attr({"contentId":contentId,"mediatype":mediatype,"channelId":channelId});
    $("body").css("overflow","hidden");
    $(".carousel_mask").removeClass("dis");
  });
  
  //点击上传图片
  $(".cm_content6").on("click",function(){
    $(".upload_pic").click();
  });
  $(".upload_pic").change(function(){
    carouselImg();
  });
  function carouselImg(){
    $(".upload_pic").attr("value","");
    var _this=$(".upload_pic");
    var oMyForm = new FormData();
    oMyForm.append("ContentFile",$(_this)[0].files[0]);
    oMyForm.append("MobileClass", "Chrome");
    oMyForm.append("PCDType", "3");
    oMyForm.append("UserId", userId);
    oMyForm.append("SrcType", "1");
    oMyForm.append("Purpose", "3");
    requestUpload(_this,oMyForm);//请求上传文件
  }
  
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
      beforeSend:function(){
        $(".carouselImgMask").removeClass("dis");
      },
      success:function(resultData){
        if(resultData.Success==true){
          $(".carouselImgMask").addClass("dis");
          $(".upl_file").attr("value",resultData.FilePath);
          if($(".defaultImg").css("display")!="none"){
            $(".defaultImg").css({"display":"none"});
          }
          var newImg =$("<img class='newImg' src="+resultData.FilePath+" alt='front cover' />");
          if($(".cm_content7").children().length>1){
            $(".cm_content7 img:last").replaceWith(newImg);
          }else{
            $(".cm_content7").append(newImg);
          } 
        }else{
          alert(resultData.err);
        }
      },
      error: function(jqXHR){
        alert("发生错误" + jqXHR.status);
      }
    });
    var jqObj=$(".upload_pic");
    jqObj.val("");
    var domObj = jqObj[0];
    domObj.outerHTML = domObj.outerHTML;
    var newJqObj = jqObj.clone();
    jqObj.before(newJqObj);
    jqObj.remove();
    $(".upload_pic").unbind().change(function (){
      carouselImg();
    });
  }
  
  //点击保存设置
  $(".cmf_save").on("click",function(){
    //待定--设置轮播图（缺少轮播图图片的地址）
    var contentId=$(".cm_content3").attr("contentId");
    var mediatype=$(".cm_content3").attr("mediatype");
    var channelid=$(".cm_content3").attr("channelid");
    var data={"PCDType":"3",
              "UserId":userId,
              "MediaType":mediatype,
              "ContentId":contentId,
              "ChannelId":channelid,
              "LoopSort":"0"
    };
    $.ajax({
      url:rootPath+"content/addLoopImage.do",
      type:"POST",
      data:JSON.stringify(data),
      cache: false,
      processData: false,
      contentType: false,
      dataType:"json",
      beforeSend:function(){
        $(".cm_footer").children("input[typwe='button']").attr("disabled","disabled");
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          alert("设置轮播图成功");
          $(".carousel_mask").addClass("dis");
        }else{
          alert("设置轮播图失败");
        }
        $(".cm_footer").children("input[typwe='button']").removeAttr("disabled");
      },
      error: function(XHR){
        alert("发生错误" + jqXHR.status);
      }
    });
  });
  
  //点击关闭和取消设置，轮播图弹出层关闭
  $(".cmh_close,.cmf_cancel").on("click",function(){
    $("body").css("overflow","auto");
    $(".carousel_mask").addClass("dis");
  });
  /*e--点击轮播图*/
 
  /*s--置顶/取消置顶相关操作*/
  //点击置顶或取消置顶按钮
  var hasTop=false;//是否有置顶内容
  $(document).on("click",".top",function(){
    var text=$(this).text().replace(/\s/g, "");
    var contentId=$(this).parent(".opetype1").attr("contentId");
    var box=$(".ri_top3_con .rtc_listBox");
    var nodes=zTreeObj.getSelectedNodes();//当前被勾选的节点集合  
    channelId=nodes[0].id;
    var mediatype=$(this).parent(".opetype1").siblings(".rtcl_con").children(".sequ_num").attr("mediatype");
    if(mediatype=="wt_MediaAsset"){//节目
      mediatype="AUDIO";
    }else if(mediatype=="wt_SeqMediaAsset"){//专辑
      mediatype="SEQU ";
    }else{//电台
      mediatype="RADIO ";
    }
    if(text=="置顶"){//点击置顶
      $(box).each(function(){
        if($(this).hasClass("isTop")){
          hasTop=true;
          return false;
        }
      });
      if(hasTop==true){//已有置顶内容
        if(confirm("已有置顶内容，你确定要替换吗")){
          $(box).each(function(){
            $(this).removeClass("isTop");
            $(this).children(".opetype1").children(".top").css({"color":"#0077C7","letter-spacing":"3.6px"}).text("置  顶");
          });
          var data={"PCDType":"3",
                    "MediaType":mediatype,
                    "ChannelId":channelId,
                    "ContentId":contentId,
                    "IsOnlyTop":"1",
                    "UserId":userId,
                    "Top":"1"//设置置顶
          };
          setTop(data);//设置置顶
        }else{
          alert("你已经取消对本内容的置顶");
          return false;
        }
      }else{//暂无置顶内容    
        var data={"PCDType":"3",
                  "MediaType":mediatype,
                  "ChannelId":channelId,
                  "ContentId":contentId,
                  "IsOnlyTop":"1",
                  "UserId":userId,
                  "Top":"1"//设置置顶
        };
        setTop(data);//设置置顶
      }
    }else{//点击取消置顶
      var data={"PCDType":"3",
                "MediaType":mediatype,
                "ChannelId":channelId,
                "ContentId":contentId,
                "IsOnlyTop":"1",
                "UserId":userId,
                "Top":"0"//取消置顶
      };
    }
  });
  
  //设置/取消置顶
  function setTop(data){
    $.ajax({
      url:rootPath+"content/setTop.do",
      type:"POST",
      data:JSON.stringify(data),
      cache: false,
      processData: false,
      contentType: false,
      dataType:"json",
      success:function(resultData){
        if(data.Top=="1"){//设置置顶
          if(resultData.ReturnType=="1001"){
            alert("设置置顶成功");
            var topBox=$(this).parent(".opetype1").parent(".rtc_listBox");
            $(".ri_top3_con").prepend(topBox);
            $(topBox).addClass("isTop");
            $(this).css({"color":"#000","letter-spacing":"0px"}).text("取消置顶");
          }else{
            alert("设置置顶失败");  
          }  
        }else{//取消置顶
          if(resultData.ReturnType=="1001"){
            alert("取消置顶成功");
            $(box).each(function(){
              $(this).removeClass("isTop");
              $(this).children(".opetype1").children(".top").css({"color":"#0077C7","letter-spacing":"3.6px"}).text("置  顶");
            });
          }else{
            alert("取消置顶失败");
          }  
        }
      },
      error: function(jqXHR){
        alert("发生错误" + jqXHR.status);
      }
    });
  }
  /*e--置顶/取消置顶相关操作*/
  
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
      $("body").css("overflow","hidden");
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
      $("body").css("overflow","hidden");
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
    var data2={"PCDType":"3",
               "UserId":userId,
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
          alert("内容撤回成功");
          $(".checkbox_img").attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
          $(".nopass_masker").addClass("dis");
          $("body").css({"overflow":"auto"});
          requestList();//再次请求内容列表
          $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
          $(".all_check").addClass("checkbox1").attr({"src":"img/checkbox1.png"});
        }else{
          alert(resultData.Message);
        }
        $('.nc_txt7').removeAttr("disabled");
      },
      error: function(jqXHR){
        $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>获取数据发生错误："+jqXHR.status+"</div>");
      }     
    });
  })
  
  //点击弹出页面上的勾选框
  $(document).on("click",".nc_checkimg",function(){
    if($(this).hasClass("checkbox1")) $(this).attr({"src":"img/checkbox2.png"}).removeClass("checkbox1");
    else $(this).attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
  });
  
  //点击撤回原因弹出页面上的关闭按钮
  $(document).on("click",".nh_span2",function(){
    $(".checkbox_img").attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
    $(".nopass_masker").addClass("dis");
    $("body").css({"overflow":"auto"});
    
  });
  /*e--点击撤回相关操作*/
  
  /*s--切换到轮播图之后的相关操作*/
  //请求栏目下的所有轮播图
  var data1={"PCDType":"3",
             "UserId":userId,
             "ChannelId":channelId
  };
//getLoopImages(data1);
  function getLoopImages(data){
    $.ajax({
      type:"POST",
      url:rootPath+"content/getLoopImages.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(data),
      beforeSend:function(){
        $(".lb_div5").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载...</div>");
        $('.shade', parent.document).show();
      },
      success: function(resultData){
        if(resultData.ReturnType=="1001"){
          loadLoopImagesList(returnData);//加载轮播图列表
        }else{
          alert(resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error: function(jqXHR){
        $(".lb_div5").html("<div style='text-align:center;height:300px;line-height:200px;'>获取数据发生错误："+jqXHR.status+"</div>");
      }     
    });
  }
  
  //加载轮播图列表
  function getLoopImages(returnData){
    return;
    $(".lb_div5").html(" ");//每次加载之前都要清空
    for(var i=0;i<returnData.ReturnList.length;i++){
      var list='<div class="lbd_box" id='+returnData.ReturnList[i].Id+'>'+
                  '<div class="lbd_box1 fl">第'+i+'帧</div>'+
                  '<div class="lbd_box2 fl">'+
                    '<img src='+returnData.ReturnList[i].ContentImg+' alt=""  class="lbd_box3"/>'+
                    '<div class="lbd_box4">上传图片</div>'+
                  '</div>'+
                  '<div class="lbd_box5 fl" contentId='+returnData.ReturnList[i].ContentId+'><'+returnData.ReturnList[i].ContentName+'></div>'+
                  '<div class="lbd_box6 fl">'+
                    '<div class="lbd_box61">上移一层</div>'+
                    '<div class="lbd_box62">下移一层</div>'+
                    '<div class="lbd_box63">删除此帧</div>'+
                  '</div>'+
                '</div>';
      $(".lb_div5").append(list);
      if(i==0){//第一个不支持上移
        $(".lbd_box61").eq(i).css("color","#ccc").attr("disabled","disabled");
      }
      if(i==(returnData.ReturnList.length-1)){//最后一个不支持下移
        $(".lbd_box62").eq(i).css("color","#ccc").attr("disabled","disabled");
      }
    }
  }
  
  //点击上移一层
  $(document).on("click",".lbd_box61",function(){
    var $li=$(this).parent(".lbd_box6").parent(".lbd_box");
    var prev_index=$li.prev().index()+1;
    var now_index=$li.index()+1;
    $li.prev().before($li);
    $li.children(".lbd_box1").text("第"+prev_index+"帧");
    $li.next().children(".lbd_box1").text("第"+now_index+"帧");
    return;
    var data2={"PCDType":"3",
               "ChannelId":channelId,
               "UserId":userId,
               "Sort":sort
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/upCarousel.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(data2),
      beforeSend:function(){
        $(".lb_div5").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载...</div>");
        $('.shade', parent.document).show();
      },
      success: function(resultData){
        if(resultData.ReturnType=="1001"){
          var $li=$(this).parent(".lbd_box6").parent(".lbd_box");
          var prev_index=$li.prev().index()+1;
          var now_index=$li.index()+1;
          $li.prev().before($li);
          $li.children(".lbd_box1").text("第"+prev_index+"帧");
          $li.next().children(".lbd_box1").text("第"+now_index+"帧");
//        loadCarouselList(returnData);//重新加载轮播图列表
        }else{
          alert(resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error: function(jqXHR){
        $(".lb_div5").html("<div style='text-align:center;height:300px;line-height:200px;'>获取数据发生错误："+jqXHR.status+"</div>");
      }     
    });
  });
  
  //点击下移一层
  $(document).on("click",".lbd_box62",function(){
    var $li=$(this).parent(".lbd_box6").parent(".lbd_box");
    var next_index=$li.next().index()+1;
    var now_index=$li.index()+1;
    $li.next().after($li);
    $li.children(".lbd_box1").text("第"+next_index+"帧");
    $li.prev().children(".lbd_box1").text("第"+now_index+"帧");
    return;
    var data2={"PCDType":"3",
               "ChannelId":channelId,
               "UserId":userId,
               "Sort":sort
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/downCarousel.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(data2),
      beforeSend:function(){
        $(".lb_div5").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载...</div>");
        $('.shade', parent.document).show();
      },
      success: function(resultData){
        if(resultData.ReturnType=="1001"){
          var $li=$(this).parent(".lbd_box6").parent(".lbd_box");
          var next_index=$li.next().index()+1;
          var now_index=$li.index()+1;
          $li.next().after($li);
          $li.children(".lbd_box1").text("第"+next_index+"帧");
          $li.prev().children(".lbd_box1").text("第"+now_index+"帧");
//        loadCarouselList(returnData);//重新加载轮播图列表
        }else{
          alert(resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error: function(jqXHR){
        $(".lb_div5").html("<div style='text-align:center;height:300px;line-height:200px;'>获取数据发生错误："+jqXHR.status+"</div>");
      }     
    });
  });
  
  //点击删除此帧
  $(document).on("click",".lbd_box63",function(){
    var $li=$(this).parent(".lbd_box6").parent(".lbd_box");
    $li.remove();
    $(".lb_div5 .lbd_box").each(function(i){
      var index=i+1;
      $(this).children(".lbd_box1").text("第"+index+"帧");
    });
    return;
    
    var data2={"PCDType":"3",
               "ChannelId":channelId,
               "UserId":userId,
               "Sort":sort
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/downCarousel.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(data2),
      beforeSend:function(){
        $(".lb_div5").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载...</div>");
        $('.shade', parent.document).show();
      },
      success: function(resultData){
        if(resultData.ReturnType=="1001"){
          var $li=$(this).parent(".lbd_box6").parent(".lbd_box");
          $li.remove();
          $(".lb_div5 .lbd_box").each(function(i){
            var index=i+1;
            $(this).children(".lbd_box1").text("第"+index+"帧");
          });
//        loadCarouselList(returnData);//重新加载轮播图列表
        }else{
          alert(resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error: function(jqXHR){
        $(".lb_div5").html("<div style='text-align:center;height:300px;line-height:200px;'>获取数据发生错误："+jqXHR.status+"</div>");
      }     
    });
  });
  /*e--切换到轮播图之后的相关操作*/
  
});
