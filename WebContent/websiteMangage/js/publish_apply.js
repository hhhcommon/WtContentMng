$(function(){
  var rootPath=getRootPath();
  var flowflag="1";
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var optfy=1;//optfy=1未选中具体筛选条件前翻页,optfy=2选中具体筛选条件后翻页
  var data1={};
  var audioList=[];//节目播放列表
  var listNum=0;//控制播放节目的序号
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchWord="";
  
  /*日期处理--日历插件*/
  $("#time .input-daterange").datepicker({keyboardNavigation:!1,forceParse:!1,autoclose:!0});
  /*翻页*/
  $(".pagination span").on("click",function(){
    var data_action=$(this).attr("data_action");
    searchWord=$(".ri_top_li2_inp").val();
    if(searchWord==""){//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
      seaFy=1;
    }else{//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
      seaFy=2;
    }
    if(data_action=="previous"){
      if(current_page <= 1){
        current_page=1;
        $(".previous").addClass('disabled');
        return false;
      }else{
        current_page--;
        $(".toPage").val("");
        opts(seaFy,current_page);
        return ;
      }
    }else if(data_action=="next"){
      if(current_page >= contentCount){
        current_page=contentCount;
        $(".next").addClass('disabled');
        return false;
      }else{
        current_page++;
        $(".toPage").val("");
        opts(seaFy,current_page);
        return ;
      }
    }else{ //跳至进行输入合理数字范围检测
      var reg = new RegExp("^[0-9]*$");
      if(!reg.test($(".toPage").val()) || $(".toPage").val()<1 || $(".toPage").val() > contentCount){  
        alert("请输入有效页码！");
        return false;
      }else{
        current_page = $(".toPage").val();
        opts(seaFy,current_page);
        return;
      }
    }
  });
  //判断在点击翻页之前是否选择了筛选条件
  function opts(seaFy,current_page){
    destroy(data1);
    data1.UserId="123";
    data1.ContentFlowFlag=flowflag;
    data1.PageSize="10";
    data1.Page=current_page;
    data1.MediaType="AUDIO";
    $(".currentPage").text(current_page);
    searchWord=$(".ri_top_li2_inp").val();
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
      data:JSON.stringify(dataParam),
      success:function(resultData){
        clear();
        if(resultData.ReturnType == "1001"){
          contentCount=resultData.AllCount;
          contentCount=(contentCount%10==0)?(contentCount/10):(Math.ceil(contentCount/10));
          $(".totalPage").text(contentCount);
          loadContentList(resultData);//加载来源的筛选条件
        }else{
          $(".totalPage").text("0");
          $(".page").find("span").addClass("disabled");
          $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到节目</div>");
        }
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
        $(".audio").attr({"src":""});
        $(".player_panel .title").html("");
        $(".player_panel .title").html(audioList[0].title);
        $(".audio").attr("src",audioList[0].playUrl);
        getTime();
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
  /*待审核内容--通过发布*/
  $(".rto_pass").on("click",function(){
    var contentIds=[];
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
    if(contentIds.length==0){//未选中内容
      alert("请先选中内容再进行操作");
      return;
    }else{
      var data2={
                UserId:"123",
                ContentIds:contentIds,
                OpeType:$(this).attr("opetype")
      };
      $.ajax({
        type: "POST",
        url:rootPath+"CM/content/updateContentStatus.do",
        dataType:"json",
        data:JSON.stringify(data2),
        success: function(resultData){
          if(resultData.ReturnType=="1001"){
            $(".pass_note").show();
            setTimeout(function(){$(".pass_note").hide();},2000);
            anew(flowflag);//在每次加载具体的资源列表时候的公共方法
            if(($(".startPubTime").val())&&($(".endPubTime").val())){
              data1.BeginContentPubTime=new Date($(".startPubTime").val()).getTime();
              data1.EndContentPubTime=new Date($(".endPubTime").val()).getTime();
            }
            getContentList(data1);
          }else{
            alert(resultData.Message);
          }
        },
        error: function(jqXHR){
          $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>获取数据发生错误："+jqXHR.status+"</div>");
        }     
      });
    }
  });
  /*待审核内容--不予发布*/
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
        if($(this).attr("mediatype")=="wt_MediaAsset"){//节目
          contentList.MediaType="AUDIO";
        }
        contentList.ChannelIds=$(this).children(".rtcl_img").attr("chIds");
        contentids.push(contentList);
      }
    });
    if(contentids.length==0){//未选中内容
      alert("请先选中内容再进行操作");
      return;
    }else{
      var height=$(".containers").height();
      var width1=$(".nopass_masker").width();
      var width2=$(".nopass_container").width();
      var left=(width1-width2)/2;
      $(".nopass_container").css({"left":left+"px"});
      $(".nopass_masker").css({"height":height}).show();
      $("body").css({"overflow-x":"hidden"});
      $(".nc_txt1").text("选择了"+contentids.length+"个节目，您确认所选的节目不予发布么？");
      $(".nopass_container").show();
    }
  });
  //点击不通过原因页面的确定
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
    var data4={
                UserId:"123",
                ContentIds:contentids,
                OpeType:$(".rto_nopass").attr("opetype"),
                ReDescn:reDesc
    };
    $.ajax({
      type: "POST",
      url:rootPath+"CM/content/updateContentStatus.do",
      dataType:"json",
      data:JSON.stringify(data1),
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
      },
      error: function(jqXHR){
        $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>获取数据发生错误："+jqXHR.status+"</div>");
      }     
    });
  })
  /*点击全部播放*/
  $(".rto_play").on("click",function(){
    $(".audio").attr({"src":audioList[0].playUrl});
    $(".player_panel .title").html(audioList[0].title);
    $(".audio")[0].play();
  });
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
    current_page="1";
    $(".currentPage").html(current_page);
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
  
  /*s--全局播放器*/
  /*全局播放器面板的展开*/
  $(document).on("click",".glp_mini",function(){
    if(audioList.length==0){
      alert("当前列表无可播放的节目，请选择其他页面");
      return;
    }else{
      $(this).css({"left":'-60px',"transition" :"all 0.1s ease 0s"});
      $(this).siblings(".glp_block").css({"left":'0px',"transition" :"all 0.1s ease 0s"});
    }
  });
  /*实时获取播放时长和改变进度条进度*/
  $(".audio")[0].addEventListener("timeupdate",function(){
    if(!isNaN(this.duration)){
      //播放进度条
      var progressValue = this.currentTime/this.duration*($(".player_progressbar").width());
      $('.player_circle')[0].style.left = parseInt(progressValue) + 'px';
      $(".player_playbar").css("width",progressValue+"px");
      //播放时长
      if(formatTime(Math.floor(this.currentTime))!=0){
        $(".sound_position").text(formatTime(Math.floor(this.currentTime)));
      }
    };
  },false);
  /*监控audio是否播放完毕,播放完毕后自动播放下一首*/
  $(".audio")[0].addEventListener("ended",function(){ 
    $(".nextBtn").click();
  },false);
  /*实现播放快进和后退*/
  $(".player_progressbar").on("click",function(event){
    var e = event || window.event;
    var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
    var x = e.pageX || e.clientX + scrollX;
    var pleft=x-335;
    var clickPlayTime=pleft/$(".player_progressbar").width();
    $('.player_circle')[0].style.left = parseInt(pleft) + 'px';
    $(".player_playbar").css("width",pleft+"px");
    $('.audio')[0].currentTime=clickPlayTime;
    $(".sound_position").text(formatTime(Math.floor(clickPlayTime)));
  });
  /*实现静音*/
  var clickVolume="1";//音量大小
  $(".volume_img").on("click",function(event){
    if($(this).hasClass("no_mute")){
      $(this).removeClass("no_mute").attr("src","img/mute.png");
      $('.audio')[0].volume="0";
    }else{
      $(this).addClass("no_mute").attr("src","img/volume.png");
      $('.audio')[0].volume=clickVolume;
    }
  });
  /*实现音量的增大和减小*/
  $(".volume_progressbar").on("click",function(event){
    var e = event || window.event;
    var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
    var x = e.pageX || e.clientX + scrollX;
    var pleft=x-770;
    clickVolume=pleft/$(".volume_progressbar").width();
    $('.volume_circle')[0].style.left = parseInt(pleft) + 'px';
    $(".volume_playbar").css("width",pleft+"px");
    $('.audio')[0].volume=clickVolume;
  });
  /*点击播放器面板上的上一首按钮*/
  $(document).on("click",".prevBtn",function(){
    if(listNum<=0){
//    alert("当前已经是第一个节目了");
      return false;
    }else{
      reset();
      listNum--;
//    console.log("上一个"+listNum);
      for(var i=0;i<audioList.length;i++){
        if(listNum==i){
          $(".audio").attr({"src":audioList[listNum].playUrl});
          $(".player_panel .title").html(audioList[listNum].title);
          $(".sound_position").html("00:00");
          getTime();
          return;
        }
      }
    }
  });
  /*点击播放器面板上的下一首按钮*/
  $(document).on("click",".nextBtn",function(){
    if(listNum>=audioList.length-1){
//    alert("当前已经是最后一个节目了");
      return false;
    }else{
      reset();
      listNum++;
//    console.log("下一个"+listNum);
      for(var i=0;i<audioList.length;i++){
        if(listNum==i){
          $(".audio").attr({"src":audioList[listNum].playUrl});
          $(".player_panel .title").html(audioList[listNum].title);
          $(".sound_position").html("00:00");
          getTime();
          return;
        }
      }
    }
  });
  /*e--全局播放器*/
 
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
    searchWord=$(".ri_top_li2_inp").val();
    if(searchWord==""){
      alert("请输入搜索内容");
      $(".ri_top_li2_inp").focus();
    }else{
      destroy(data1);
      data1.UserId="123";
      data1.ContentFlowFlag=flowflag;
      data1.PageSize="10";
      current_page="1";
      data1.Page=current_page;
      data1.MediaType="AUDIO";
      data1.SearchWord=searchWord;
      $(".currentPage").html(current_page);
    }
    getSearchList(data1);  
  }
  function getSearchList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/content/searchContents.do",
      dataType:"json",
      data:JSON.stringify(dataParam),
      success:function(resultData){
        clear();
        if(resultData.ReturnType == "1001"){
          contentCount=resultData.ResultInfo.Count;
          contentCount=(contentCount%10==0)?(contentCount/10):(Math.ceil(contentCount/10));
          $(".totalPage").text(contentCount);
          loadSearchList(resultData);//加载来源的筛选条件
        }else{
          $(".totalPage").text("0");
          $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到节目</div>");
        }
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
        var audioObj={};
        audioObj.title=resultData.ResultInfo.List[i].ContentName;
        audioObj.playUrl=resultData.ResultInfo.List[i].ContentPlayUrl;
        audioList.push(audioObj);
        $(".audio").attr({"src":""});
        $(".player_panel .title").html("");
        $(".player_panel .title").html(audioList[0].title);
        $(".audio").attr("src",audioList[0].playUrl);
        getTime();
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
  
});
