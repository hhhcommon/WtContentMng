$(function(){
  var rootPath=getRootPath();
  var flowflag="2";
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var optfy=1;//optfy=1未选中具体筛选条件前翻页,optfy=2选中具体筛选条件后翻页
  var mediaType=1;//mediaType=1选中节目,mediaType=2选中专辑
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
    data1.ApplyFlowFlag=flowflag;
    data1.ReFlowFlag="0";
    data1.PageSize="10";
    data1.Page=current_page;
    $(".currentPage").text(current_page);
    $(".dropdown_menu li").each(function(){
      if($(this).hasClass("selected")){
        data1.MediaType=$(this).attr("mediatype");
        return;
      }
    })
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
  data1.ApplyFlowFlag=flowflag;
  data1.ReFlowFlag="0";
  data1.Page=current_page;
  data1.PageSize="10";
  $(".dropdown_menu li").each(function(){
    if($(this).hasClass("selected")){
      data1.MediaType=$(this).attr("mediatype");
      return;
    }
  })
  getContentList(data1);
  function getContentList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/content/getAppRevocation.do",
      dataType:"json",
      async:false,
      data:JSON.stringify(dataParam),
      success:function(resultData){
        clear();
        if(resultData.ReturnType == "1001"){
          contentCount=resultData.ResultInfo.AllCount;
          contentCount=(contentCount%10==0)?(contentCount/10):(Math.ceil(contentCount/10));
          $(".totalPage").text(contentCount);
          loadContentList(resultData);//加载资源列表
        }else{
          $(".totalPage").text("0");
          $(".page").find("span").addClass("disabled");
          $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到内容</div>");
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  function loadContentList(resultData){//加载资源列表
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
                      '<div class="rtcl_con_channels">'+
                        '<span class="rtcl_con_channel1 fl">栏目：</span>'+
                        '<ul class="rtcl_con_channel1s fl">'+
      //                  '<li class="rtcl_con_channel2 fl">罗振宇</li>'+
      //                  '<li class="rtcl_con_channel2 fl">脱口秀</li>'+
      //                  '<li class="rtcl_con_channel2 fl">逻辑思维</li>'+
                        '</ul>'+
                      '</div>'+
                      '<ul class="rtcl_con_sum">'+
                        '<li class="play">'+
                          '<img class="play1" alt="播放" src="img/act1.png"/>'+
                          '<span class="play2">3.83万</span>'+
                        '</li>'+
                        '<li class="love">'+
                          '<img class="love1" alt="喜欢" src="img/act3.png"/>'+
                          '<span class="love2">4.83万</span>'+
                        '</li>'+
                        '<li class="intransit">'+
                          '<img class="intransit1" alt="转发" src="img/act5.png"/>'+
                          '<span class="intransit2">8.83万</span>'+
                        '</li>'+
                      '</ul>'+
                    '</div>'+
                    '<span class="sequ_sum fl"></span>'+
                    '<span class="anchor_name fl"></span>'+
                    '<span class="source_form fl"></span>'+
                    '<span class="audio_time fl"></span>'+
                  '</div>';
      $(".ri_top3_con").append(listbox);
      if(resultData.ResultInfo.List[i].ContentPubChannels){
        var chIds="";//发布栏目的id集合
        for(var j=0;j<resultData.ResultInfo.List[i].ContentPubChannels.length;j++){
          var li='<li class="rtcl_con_channel2 fl" channelId='+resultData.ResultInfo.List[i].ContentPubChannels[j].ChannelId+'>'+resultData.ResultInfo.List[i].ContentPubChannels[j].ChannelName+'</li>';
          $(".rtcl_con_channel1s").eq(i).append(li);
          if(chIds==""){
            chIds=resultData.ResultInfo.List[i].ContentPubChannels[j].ChannelId;
          }else{
            chIds+=","+resultData.ResultInfo.List[i].ContentPubChannels[j].ChannelId;
          }
        }
        $(".rtcl_con_channels").eq(i).attr("chIds",chIds);
      }
      $(".audio_time").eq(i).text(getLocalTime(cptime)); 
      if(resultData.ResultInfo.List[i].ContentPlayURI){
        var audioObj={};
        audioObj.title=resultData.ResultInfo.List[i].ContentName;
        audioObj.playUrl=resultData.ResultInfo.List[i].ContentPlayURI;
        audioList.push(audioObj);
        $(".audio").attr({"src":""});
        $(".player_panel .title").html("");
        $(".player_panel .title").html(audioList[0].title);
        $(".audio").attr("src",audioList[0].playUrl);
        getTime();
        $(".rtc_listBox").eq(i).addClass("playurl");
      }
      if(resultData.ResultInfo.List[i].MediaSize){
        $(".sequ_sum").eq(i).text(resultData.ResultInfo.List[i].MediaSize);
      }else{
        $(".sequ_sum").eq(i).text("0");
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
    }
  }
  /*待审核内容--同意撤回*/
  $(".rto_pass").on("click",function(){
    var contentIds=[];
    $(".ri_top3_con .rtc_listBox").each(function(){
      if($(this).children(".rtcl_img_check").hasClass("checkbox1")){//未选中
        
      }else{//已选中
        var contentList={};
        contentList.Id=$(this).attr("contentId");
        if($(this).attr("mediatype")=="wt_SeqMediaAsset"){//专辑
          contentList.MediaType="SEQU";
        }else if($(this).attr("mediatype")=="wt_MediaAsset"){//节目
          contentList.MediaType="AUDIO";
        }
        contentList.ChannelIds=$(this).children(".rtcl_con").children(".rtcl_con_channels").attr("chIds");
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
  /*待审核内容--不予撤回*/
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
        if($(this).attr("mediatype")=="wt_SeqMediaAsset"){//专辑
          contentList.MediaType="SEQU";
        }else if($(this).attr("mediatype")=="wt_MediaAsset"){//节目
          contentList.MediaType="AUDIO";
        }
        contentList.ChannelIds=$(this).children(".rtcl_con").children(".rtcl_con_channels").attr("chIds");
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
      $(".nc_txt1").text("选择了"+contentids.length+"个节目，您确认所选的节目不能通过审核么？");
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
        red.ReDescn=$(this).siblings(".ct").text();
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
      data:JSON.stringify(data4),
      success: function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".checkbox_img").attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
          $(".nopass_masker,.nopass_container").hide();
          $("body").css({"overflow-x":"auto"});
          alert("具体原因提交成功");
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
      optfy=1;//选中具体筛选条件后翻页
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
    data1.ApplyFlowFlag=flowflag;
    data1.ReFlowFlag="0";
    $(".dropdown_menu li").each(function(){
      if($(this).hasClass("selected")){
        data1.MediaType=$(this).attr("mediatype");
        return;
      }
    })
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
  /*实现播放快进和后退*/
  $(".player_progressbar").on("click",function(event){
    var e = event || window.event;
    var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
    var x = e.pageX || e.clientX + scrollX;
    var pleft=x-335;
    var clickPlayTime=pleft*($(".audio")[0].duration)/$(".player_progressbar").width();
    $('.player_circle')[0].style.left = parseInt(pleft) + 'px';
    $(".player_playbar").css("width",pleft+"px");
    $('.audio')[0].currentTime=clickPlayTime;
    $(".sound_position").text(formatTime(Math.floor(clickPlayTime)));
  });
  /*实现静音*/
  var clickVolume="1";//音量大小
  var volumeLeft=" ";//音量的小圆点距离左边的距离
  $(".volume_img").on("click",function(event){
    if($(this).hasClass("no_mute")){
      $(this).removeClass("no_mute").attr("src","img/mute.png");
      $('.audio')[0].volume="0";
      $('.volume_circle')[0].style.left ='0px';
      $(".volume_playbar").css("width","0px");
    }else{
      $(this).addClass("no_mute").attr("src","img/volume.png");
      $('.volume_circle')[0].style.left = parseInt(volumeLeft) + 'px';
      $(".volume_playbar").css("width",volumeLeft+"px");
      $('.audio')[0].volume=clickVolume;
    }
  });
  /*实现音量的增大和减小*/
  $(".volume_progressbar").on("click",function(event){
    var e = event || window.event;
    var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
    var x = e.pageX || e.clientX + scrollX;
    volumeLeft=x-770;
    clickVolume=volumeLeft/$(".volume_progressbar").width();
    $('.volume_circle')[0].style.left = parseInt(volumeLeft) + 'px';
    $(".volume_playbar").css("width",volumeLeft+"px");
    $('.audio')[0].volume=clickVolume;
  });
  /*点击播放器面板上的上一首按钮*/
  $(document).on("click",".prevBtn",function(){
    if(listNum<=0){
      alert("当前已经是第一个节目了");
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
      alert("当前已经是最后一个节目了");
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
      data1.ApplyFlowFlag=flowflag;
      data1.ReFlowFlag="0";
      data1.PageSize="10";
      $(".dropdown_menu li").each(function(){
        if($(this).hasClass("selected")){
          data1.MediaType=$(this).attr("mediatype");
          return;
        }
      })
      current_page="1";
      data1.Page=current_page;
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
          $(".page").find("span").addClass("disabled");
          $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到节目</div>");
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  function loadSearchList(resultData){//加载资源列表
    audioList=[];//每次加载数据之前先清空存数据的数组
    for(var i=0;i<resultData.ResultInfo.List.length;i++){
      var cptime=resultData.ResultInfo.List[i].ContentTime;
      var listbox='<div class="rtc_listBox" contentId='+resultData.ResultInfo.List[i].ContentId+' mediaType='+resultData.ResultInfo.List[i].MediaType+'>'+
                    '<img src="img/checkbox1.png" alt="" class="rtcl_img_check fl checkbox_img checkbox1"/>'+
                    '<div class="rtcl_img fl">'+
                      '<img src='+resultData.ResultInfo.List[i].ContentImg+' alt="节目图片" />'+
                    '</div>'+
                    '<div class="rtcl_con fl">'+
                      '<p class="rtcl_con_p ellipsis">'+resultData.ResultInfo.List[i].ContentName+'</p>'+
                      '<div class="rtcl_con_channels">'+
                        '<span class="rtcl_con_channel1 fl">栏目：</span>'+
                        '<ul class="rtcl_con_channel1s fl">'+
      //                  '<li class="rtcl_con_channel2 fl">罗振宇</li>'+
      //                  '<li class="rtcl_con_channel2 fl">脱口秀</li>'+
      //                  '<li class="rtcl_con_channel2 fl">逻辑思维</li>'+
                        '</ul>'+
                      '</div>'+
                      '<ul class="rtcl_con_sum">'+
                        '<li class="play">'+
                          '<img class="play1" alt="播放" src="img/act1.png"/>'+
                          '<span class="play2">3.83万</span>'+
                        '</li>'+
                        '<li class="love">'+
                          '<img class="love1" alt="喜欢" src="img/act3.png"/>'+
                          '<span class="love2">4.83万</span>'+
                        '</li>'+
                        '<li class="intransit">'+
                          '<img class="intransit1" alt="转发" src="img/act5.png"/>'+
                          '<span class="intransit2">8.83万</span>'+
                        '</li>'+
                      '</ul>'+
                    '</div>'+
                    '<span class="sequ_sum fl"></span>'+
                    '<span class="anchor_name fl"></span>'+
                    '<span class="source_form fl"></span>'+
                    '<span class="audio_time fl"></span>'+
                  '</div>';
      $(".ri_top3_con").append(listbox);
      if(resultData.ResultInfo.List[i].ContentPubChannels){
        for(var j=0;j<resultData.ResultInfo.List[i].ContentPubChannels.length;j++){
          var li='<li class="rtcl_con_channel2 fl" channelId='+resultData.ResultInfo.List[i].ContentPubChannels[j].ChannelId+'>'+resultData.ResultInfo.List[i].ContentPubChannels[j].ChannelName+'</li>';
          $(".rtcl_con_channel1s").eq(i).append(li);
        }
      }
      $(".audio_time").eq(i).text(getLocalTime(cptime)); 
      if(resultData.ResultInfo.List[i].ContentPlayURI){
        var audioObj={};
        audioObj.title=resultData.ResultInfo.List[i].ContentName;
        audioObj.playUrl=resultData.ResultInfo.List[i].ContentPlayURI;
        audioList.push(audioObj);
        $(".audio").attr({"src":""});
        $(".player_panel .title").html("");
        $(".player_panel .title").html(audioList[0].title);
        $(".audio").attr("src",audioList[0].playUrl);
        getTime();
        $(".rtc_listBox").eq(i).addClass("playurl");
      }
      console.log(audioObj);
      if(resultData.ResultInfo.List[i].MediaSize){
        $(".sequ_sum").eq(i).text(resultData.ResultInfo.List[i].MediaSize);
      }else{
        $(".sequ_sum").eq(i).text("0");
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
    }
  }
  /*e--搜索*/
 
  /*状态的下拉菜单*/
  $(".dropdown").on("click",function(){
    if($(this).siblings(".dropdown_menu").hasClass("dis")){
      $(this).children("img").attr({"src":"img/filter1.png"});
      $(this).siblings(".dropdown_menu").removeClass("dis");
    }else{
      $(this).children("img").attr({"src":"img/filter2.png"});
      $(this).siblings(".dropdown_menu").addClass("dis");
    }
  });
  /*选中节目或专辑--进行筛选*/
  $(".dropdown_menu").on("click","li",function(){
    $(this).parent(".dropdown_menu").addClass("dis");
    $(this).parent(".dropdown_menu").siblings(".dropdown").children("img").attr({"src":"img/filter2.png"});
    $(this).addClass("selected").siblings("li").removeClass("selected");
    destroy(data1);
    data1.UserId="123";
    data1.PageSize="10";
    current_page="1";
    data1.Page=current_page;
    data1.ApplyFlowFlag=flowflag;
    data1.ReFlowFlag="0";
    $(".currentPage").html(current_page);
    data1.MediaType=$(this).attr('mediatype');
    getContentList(data1);
  });
  
  
  
  
});
