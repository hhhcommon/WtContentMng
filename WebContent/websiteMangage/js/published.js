$(function(){
  var rootPath=getRootPath();
  var flowflag="2";
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var allCount=0;//总记录数
  var optfy=1;//optfy=1未选中具体筛选条件前翻页,optfy=2选中具体筛选条件后翻页
  var data1={};
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchWord="";
  var audioList=[];//节目播放列表
  
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
    destroy(data1);
    data1.UserId="123";
    data1.ContentFlowFlag=flowflag;
    data1.PageSize="10";
    data1.Page=current_page;
    data1.MediaType="AUDIO";
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(optfy==2){//optfy=2选中具体筛选条件后翻页
      $(document).find(".new_cate li").each(function(){
        if($(".new_cate li").size()>="0"){
          var pId=$(this).attr("pid");
          var id=$(this).attr("id");
          if(pId=="channel"){
            data1.ChannelId=$(this).attr("id");
          }else{
            data1.SourceId=$(this).attr("id");
          }
        }
      });
    }
    if(seaFy==1){//seaFy=1未搜索关键词前翻页
      getContentList(data1);
    }else{//seaFy=2搜索列表加载出来后翻页
      data1.SearchWord=searchWord;
      getSearchList(data1);
    }
  }
  
  /*得到资源列表*/
  data1.UserId="123";
  data1.ContentFlowFlag=flowflag;
  data1.Page=current_page;
  data1.PageSize="10";
  data1.MediaType="AUDIO";
  getContentList(data1);
  function getContentList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/content/getContents.do",
      dataType:"json",
      async:false,
      cache:false, 
      data:JSON.stringify(dataParam),
      beforeSend: function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载节目列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        clear();
        if(resultData.ReturnType=="1001"){
          allCount=resultData.AllCount;
          contentCount=(allCount%10==0)?(allCount/10):(Math.ceil(allCount/10));
          loadContentList(resultData);//加载资源列表
          pagitionInit(contentCount,allCount,dataParam.Page);//init翻页
        }else{
          $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到节目</div>");
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  function loadContentList(resultData){//加载资源列表
    audioList=[];//每次加载数据之前先清空存数据的数组
    for(var i=0;i<resultData.ResultList.length;i++){
      var cptime=resultData.ResultList[i].ContentTime;
      var listbox='<div class="rtc_listBox" contentId='+resultData.ResultList[i].ContentId+' mediaType='+resultData.ResultList[i].MediaType+'>'+
                    '<img src="img/checkbox1.png" alt="" class="rtcl_img_check fl checkbox_img checkbox1"/>'+
                    '<div class="rtcl_img fl">'+
                      '<img src='+resultData.ResultList[i].ContentImg+' alt="节目图片" />'+
                    '</div>'+
                    '<div class="rtcl_con fl">'+
                      '<p class="rtcl_con_p ellipsis">'+resultData.ResultList[i].ContentName+'</p>'+
                      '<div class="rtcl_con_tags">'+
                        '<span class="rtcl_con_tag1 fl">标签：</span>'+
                        '<ul class="rtcl_con_tag1s fl">'+
//                        '<li class="rtcl_con_tag2 fl">罗振宇</li>'+
//                        '<li class="rtcl_con_tag2 fl">脱口秀</li>'+
//                        '<li class="rtcl_con_tag2 fl">逻辑思维</li>'+
//                        '<li class="rtcl_con_tag2 fl">脱口秀</li>'+
//                        '<li class="rtcl_con_tag2 fl">罗振宇</li>'+
//                        '<li class="rtcl_con_tag2 fl">脱口秀</li>'+
//                        '<li class="rtcl_con_tag2 fl">逻辑思维</li>'+
//                        '<li class="rtcl_con_tag2 fl">脱口秀</li>'+
                        '</ul>'+
                      '</div>'+
                      '<div class="rtcl_con_desc">'+
                        '<span class="rtcl_con_desc1 fl">简介：</span>'+
                        '<span class="rtcl_con_desc2 fl">'+resultData.ResultList[i].ContentDesc+'</span>'+
                      '</div>'+
                    '</div>'+
                    '<span class="sequ_name fl"></span>'+
                    '<span class="anchor_name fl"></span>'+
                    '<span class="source_form fl"></span>'+
                    '<span class="audio_time fl"></span>'+
                  '</div>';
      $(".ri_top3_con").append(listbox);
      if(resultData.ResultList[i].ContentPubChannels){
        var chIds="";//发布栏目的id集合
        for(var j=0;j<resultData.ResultList[i].ContentPubChannels.length;j++){
          if(chIds==""){
            chIds=resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }else{
            chIds+=","+resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }
        }
        $(".rtcl_img").eq(i).attr("chIds",chIds);
      }
      $(".audio_time").eq(i).text(getLocalTime(cptime));
      if(resultData.ResultList[i].ContentPlayUrl){
        var audioObj={};
        audioObj.title=resultData.ResultList[i].ContentName;
        audioObj.playUrl=resultData.ResultList[i].ContentPlayUrl;
        audioList.push(audioObj);
        $(".rtc_listBox").eq(i).addClass("playurl");
      }
      if(resultData.ResultList[i].ContentSeqName){
        $(".sequ_name").eq(i).attr({"contentSeqId":resultData.ResultList[i].ContentSeqId}).text(resultData.ResultList[i].ContentSeqName);
      }else{
        $(".sequ_name").eq(i).text("未知");
      }
      if(resultData.ResultList[i].PersonName){
        $(".anchor_name").eq(i).text(resultData.ResultList[i].PersonName);
      }else{
        $(".anchor_name").eq(i).text("暂无");
      }
      if(resultData.ResultList[i].ContentPublisher){
        $(".source_form").eq(i).text(resultData.ResultList[i].ContentPublisher);
      }else{
        $(".source_form").eq(i).text("未知");
      }
      if(resultData.ResultList[i].KeyWords){
        for(var j=0;j<resultData.ResultList[i].KeyWords.length;j++){
          var li='<li class="rtcl_con_tag2 fl" tagId='+resultData.ResultList[i].KeyWords[j].TagId+'>'+resultData.ResultList[i].KeyWords[j].TagName+'</li>';
          $(".rtcl_con_tag1s").eq(i).append(li);
        }
      }else{
        var li='<li class="rtcl_con_tag2 fl">暂无标签信息</li>';
        $(".rtcl_con_tag1s").eq(i).append(li);
      }
    }
  }
  
  //点击专辑名字进入专辑详情页
  $(document).on("click",".sequ_name",function(){
    var seqId=$(this).attr("contentSeqId");
    $("#myIframe", parent.document).attr({"src":"zj_detail.html?contentId="+seqId});
  });
  
  /*已发布内容--撤回*/
  var contentIds=[];
  $(".rto_nopass").on("click",function(){
    contentIds=[];
    $(".nc_txt1").text(" ");
    $(".other_reason").val(" ");
    $(".ri_top3_con .rtc_listBox").each(function(){
      if($(this).children(".rtcl_img_check").hasClass("checkbox1")){//未选中
        
      }else{//已选中
        var contentList={};
        contentList.Id=$(this).attr("contentId");
        if($(this).attr("mediatype")=="wt_MediaAsset"){//节目
          contentList.MediaType="AUDIO";
        }
        contentList.ChannelIds=$(this).children(".rtcl_img").attr("chIds");
        contentIds.push(contentList);
      }
    });
    if(contentIds.length!=0){//选中内容
      var height=$(".containers").height();
      var width1=$(".nopass_masker").width();
      var width2=$(".nopass_container").width();
      var left=(width1-width2)/2;
      $(".nopass_container").css({"left":left+"px"});
      $(".nopass_masker").css({"height":height}).show();
      $("body").css({"overflow-x":"hidden"});
      $(".nc_txt1").text("选择了"+contentIds.length+"个节目，您确认您确认撤回所选的节目么？");
      $(".nopass_container").show();
    }
  });
  
  //点击撤回原因页面的确定
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
    var data2={
                UserId:"123",
                ContentIds:contentIds,
                OpeType:$(".rto_nopass").attr("opetype")
    };
    $.ajax({
      type: "POST",
      url:rootPath+"CM/content/updateContentStatus.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(data2),
      beforeSend:function(){
        $('.nc_txt7').attr("disabled","disabled");
      },
      success: function(resultData){
        if(resultData.ReturnType=="1001"){
          alert("具体原因提交成功");
          $(".checkbox_img").attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
          $(".nopass_masker,.nopass_container").hide();
          $("body").css({"overflow-x":"auto"});
          getContentList(data1);
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
  
  /*根据不同的筛选条件得到不同的节目列表*/
  $(document).on("click",".trig_item,.trig_item_li",function(){
    optfy=2;//选中具体筛选条件后翻页
    anew(flowflag);//在每次加载具体的资源列表时候的公共方法
    if(($(".startPubTime").val())&&($(".endPubTime").val())){
      data1.BeginContentPubTime=new Date($(".startPubTime").val()).getTime();
      data1.EndContentPubTime=new Date($(".endPubTime").val()).getTime();
    }
    if(searchWord==""){
      seaFy=1;//seaFy=1未搜索关键词前翻页
      getContentList(data1);
    }else{
      seaFy=2;//seaFy=2搜索列表加载出来后翻页
      data1.SearchWord=searchWord;
      getSearchList(data1);
    }
  });
  
  /*点击取消所选的筛选条件*/
  $(document).on("click",".cate_img",function(){
    if($(".new_cate li").size()<="0"){
      optfy=1;//未选中具体筛选条件前翻页
    }
    anew(flowflag);//在每次加载具体的资源列表时候的公共方法
    if(($(".startPubTime").val())&&($(".endPubTime").val())){
      data1.BeginContentPubTime=new Date($(".startPubTime").val()).getTime();
      data1.EndContentPubTime=new Date($(".endPubTime").val()).getTime();
    }
    if(searchWord==""){
      seaFy=1;//seaFy=1未搜索关键词前翻页
      getContentList(data1);
    }else{
      seaFy=2;//seaFy=2搜索列表加载出来后翻页
      data1.SearchWord=searchWord;
      getSearchList(data1);
    }
  });
  
  /*点击筛选条件日期附近的确定按钮*/
  $(".ensure").on("click",function(){
    var st=new Date($(".startPubTime").val()).getTime();
    var et=new Date($(".endPubTime").val()).getTime();
    if(st>et){
      alert("你选择的时间段不合法，请重新选择");
      $(".startPubTime,.endPubTime").val("");
    }else{
      optfy=2;//选中具体筛选条件后翻页
      anew(flowflag);
      data1.BeginContentPubTime=new Date($(".startPubTime").val()).getTime();
      data1.EndContentPubTime=new Date($(".endPubTime").val()).getTime();
      if(searchWord==""){
        seaFy=1;//seaFy=1未搜索关键词前翻页
        getContentList(data1);
      }else{
        seaFy=2;//seaFy=2搜索列表加载出来后翻页
        data1.SearchWord=searchWord;
        getSearchList(data1);
      }
    }
  });
  
  /*点击筛选条件日期附近的清除按钮*/
  $(".clean").on("click",function(){
    $(".startPubTime,.endPubTime").val("");
    anew(flowflag);//在每次加载具体的资源列表时候的公共方法
    if($(".new_cate li").size()<="0"){
      optfy=1;//未选中具体筛选条件前翻页
    }
    if(searchWord==""){
      seaFy=1;//seaFy=1未搜索关键词前翻页
      getContentList(data1);
    }else{
      seaFy=2;//seaFy=2搜索列表加载出来后翻页
      data1.SearchWord=searchWord;
      getSearchList(data1);
    }
  });
  
  /*在每次加载具体的资源列表时候的公共方法*/
  function anew(flowflag){
    destroy(data1);
    current_page=1;
    data1.UserId="123";
    data1.PageSize="10";
    data1.Page=current_page;
    data1.ContentFlowFlag=flowflag;
    data1.MediaType="AUDIO";
    if($(".new_cate li").size()>"0"){
      optfy=2;//选中具体筛选条件后翻页
      $(document).find(".new_cate li").each(function(){
        var pId=$(this).attr("pid");
        var id=$(this).attr("id");
        if(pId=="channel"){
          data1.ChannelId=$(this).attr("id");
        }else{
          data1.SourceId=$(this).attr("id");
        }
      });
    }
    
  }
  
  /*s--搜索*/
  $(document).keydown(function(e){//键盘上的事件
    e = e || window.event;
    var keycode = e.which ? e.which : e.keyCode;
    if(keycode == 13){//键盘上的enter
      $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
      $(".startPubTime,.endPubTime").val("");
      $("#source,#channel").show();
      searchList();//加载搜索列表
    }
  });
  $(".ri_top_li2_img").on("click",function(){
    $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
    $(".startPubTime,.endPubTime").val("");
    $(".cate_img").click();
    searchList();//加载搜索列表
  });
  function searchList(){
    searchWord=$.trim($(".ri_top_li2_inp").val());
    destroy(data1);
    data1.UserId="123";
    data1.ContentFlowFlag=flowflag;
    data1.PageSize="10";
    current_page=1;
    data1.Page=current_page;
    data1.MediaType="AUDIO";
    if(searchWord==""){
      seaFy=1;//seaFy=1未搜索关键词前翻页
      getContentList(data1);  
    }else{
      seaFy=2;//seaFy=2搜索列表加载出来后翻页
      data1.SearchWord=searchWord;
      getSearchList(data1);  
    }
  }
  function getSearchList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/content/searchContents.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(dataParam),
      beforeSend:function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载节目列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        clear();
        if(resultData.ReturnType=="1001"){
          allCount=resultData.ResultInfo.Count;
          contentCount=(allCount%10==0)?(allCount/10):(Math.ceil(allCount/10));
          loadSearchList(resultData);//加载来源的筛选条件
          pagitionInit(contentCount,allCount,dataParam.Page);//init翻页
        }else{
          $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到节目</div>");
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  function loadSearchList(resultData){
    audioList=[];//每次加载数据之前先清空存数据的数组
    for(var i=0;i<resultData.ResultInfo.List.length;i++){
      var cptime=resultData.ResultInfo.List[i].ContentPubTime;
      var listbox='<div class="rtc_listBox" contentId='+resultData.ResultInfo.List[i].ContentId+' mediaType='+resultData.ResultInfo.List[i].MediaType+'>'+
                    '<img src="img/checkbox1.png" alt="" class="rtcl_img_check fl checkbox_img checkbox1"/>'+
                    '<div class="rtcl_img fl">'+
                      '<img src='+resultData.ResultInfo.List[i].ContentImg+' alt="节目图片" />'+
                    '</div>'+
                    '<div class="rtcl_con fl">'+
                      '<p class="rtcl_con_p ellipsis">'+resultData.ResultInfo.List[i].ContentName+'</p>'+
                      '<div class="rtcl_con_tags">'+
                        '<span class="rtcl_con_tag1 fl">标签：</span>'+
                        '<ul class="rtcl_con_tag1s fl">'+
//                        '<li class="rtcl_con_tag2 fl">罗振宇</li>'+
//                        '<li class="rtcl_con_tag2 fl">脱口秀</li>'+
//                        '<li class="rtcl_con_tag2 fl">逻辑思维</li>'+
//                        '<li class="rtcl_con_tag2 fl">脱口秀</li>'+
//                        '<li class="rtcl_con_tag2 fl">罗振宇</li>'+
//                        '<li class="rtcl_con_tag2 fl">脱口秀</li>'+
//                        '<li class="rtcl_con_tag2 fl">逻辑思维</li>'+
//                        '<li class="rtcl_con_tag2 fl">脱口秀</li>'+
                        '</ul>'+
                      '</div>'+
                      '<div class="rtcl_con_desc">'+
                        '<span class="rtcl_con_desc1 fl">简介：</span>'+
                        '<span class="rtcl_con_desc2 fl">'+resultData.ResultInfo.List[i].ContentDesc+'</span>'+
                      '</div>'+
                    '</div>'+
                    '<span class="sequ_name fl"></span>'+
                    '<span class="anchor_name fl"></span>'+
                    '<span class="source_form fl"></span>'+
                    '<span class="audio_time fl"></span>'+
                  '</div>';
      $(".ri_top3_con").append(listbox);
      $(".audio_time").eq(i).text(getLocalTime(cptime));
      if(resultData.ResultInfo.List[i].ContentPlayUrl){
        audioList=[];//每次加载数据之前先清空存数据的数组
        var audioObj={};
        audioObj.title=resultData.ResultInfo.List[i].ContentName;
        audioObj.playUrl=resultData.ResultInfo.List[i].ContentPlayUrl;
        audioList.push(audioObj);
        $(".rtc_listBox").eq(i).addClass("playurl");
      }
      if(resultData.ResultInfo.List[i].ContentSeqName){
        $(".sequ_name").eq(i).attr({"contentSeqId":resultData.ResultInfo.List[i].ContentSeqId}).text(resultData.ResultInfo.List[i].ContentSeqName);
      }else{
        $(".sequ_name").eq(i).text("未知");
      }
      if(resultData.ResultInfo.List[i].PersonName){
        $(".anchor_name").eq(i).text(resultData.ResultInfo.List[i].PersonName);
      }else{
        $(".anchor_name").eq(i).text("暂无");
      }
      if(resultData.ResultInfo.List[i].ContentPublisher){
        $(".source_form").eq(i).text(resultData.ResultInfo.List[i].ContentPublisher);
      }else{
        $(".source_form").eq(i).text("未知");
      }
      if(resultData.ResultInfo.List[i].KeyWords){
        for(var j=0;j<resultData.ResultInfo.List[i].KeyWords.length;j++){
          var li='<li class="rtcl_con_tag2 fl" tagId='+resultData.ResultInfo.List[i].KeyWords[j].TagId+'>'+resultData.ResultInfo.List[i].KeyWords[j].TagName+'</li>';
          $(".rtcl_con_tag1s").eq(i).append(li);
        }
      }else{
        var li='<li class="rtcl_con_tag2 fl">暂无标签信息</li>';
        $(".rtcl_con_tag1s").eq(i).append(li);
      }
    }
  }
  /*e--搜索*/
 
  /*节目管理--点击跳到对应的详情页*/
  $(document).on("click",".rtc_listBox .rtcl_img",function(){
    var contentId=$(this).parent(".rtc_listBox").attr("contentid");
    var seqId=$(this).siblings(".sequ_name").attr("contentseqid");
    if($(this).parent(".rtc_listBox").attr("mediatype")=="wt_MediaAsset"){//节目
      $("#myIframe", parent.document).attr({"src":"jm_detail.html?contentId="+contentId+"&&seqId="+seqId});
    }
  });
  
  /*点击全部播放*/
  $(".rto_play").on("click",function(){
    var audios=window.frames["audioIframe"].document.getElementById("audio");
    audios.setAttribute("src",audioList[0].playUrl);
    var audioTitle=window.frames["audioIframe"].document.getElementsByClassName("title")[0];
    audioTitle.innerHTML=audioList[0].title;
    var audioPlay=window.frames["audioIframe"].document.getElementsByClassName("playerBtn")[0];
    $(audioPlay).click();
    $(".locker").click();
  });
  
});