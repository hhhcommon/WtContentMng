$(function(){
  var rootPath=getRootPath();
  var flowflag="2";
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var optfy=1;//optfy=1未选中具体筛选条件前翻页,optfy=2选中具体筛选条件后翻页

  var data1={};
  
  /*时间戳转日期*/
  function getLocalTime(cptime) {     
    return new Date(parseInt(cptime)).toLocaleString('chinese',{hour12:false}).replace(/\//g, "-"); 
  }
  /*翻页*/
  $(".pagination span").on("click",function(){
    var data_action=$(this).attr("data_action");
    if(data_action=="previous"){
      if(current_page <= 1){
        current_page=1;
        $(".previous").addClass('disabled');
        return false;
      }else{
        current_page--;
        $(".toPage").val("");
        $(".currentPage").text(current_page);
        $(".page").find("span").removeClass("disabled");
        opts();
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
        $(".currentPage").text(current_page);
        $(".page").find("span").removeClass("disabled");
        opts();
        return ;
      }
    }else{ //跳至进行输入合理数字范围检测
      var reg = new RegExp("^[0-9]*$");
      if(!reg.test($(".toPage").val()) || $(".toPage").val()<1 || $(".toPage").val() > contentCount){  
        alert("请输入有效页码！");
        return false;
      }else{
        current_page = $(".toPage").val();
        $(".currentPage").text(current_page);
        $(".page").find("span").removeClass("disabled");
        opts();
        return;
      }
    }
  });
  //判断在点击翻页之前是否选择了筛选条件
  function opts(){
    destroy(data1);
    data1.UserId="123";
    data1.ContentFlowFlag=flowflag;
    data1.PageSize="10";
    if(optfy==1){//optfy=1未选中具体筛选条件前翻页
      data1.Page=current_page;
    }else{//optfy=2选中具体筛选条件后翻页
      var catalogsId=null;
      var sourceId=null;
      $(document).find(".new_cate li").each(function(){
        if($(".new_cate li").size()>="0"){
          var pId=$(this).attr("pid");
          var id=$(this).attr("id");
          if(pId=="channel"){
            catalogsId=$(this).attr("id");
          }else{
            sourceId=$(this).attr("id");
          }
        }
      });
      data1.Page=current_page;
      data1.CatalogsId=catalogsId;
      data1.SourceId=sourceId;
    }
    getContentList(data1);
  }
  /*得到资源列表*/
  data1.UserId="123";
  data1.ContentFlowFlag=flowflag;
  data1.Page=current_page;
  data1.PageSize="10";
  getContentList(data1);
  function getContentList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/content/getContents.do",
      dataType:"json",
      data:JSON.stringify(dataParam),
      success:function(resultData){
        clear();
        contentCount=resultData.AllCount;
        contentCount=(contentCount%10==0)?(contentCount/10):(Math.ceil(contentCount/10));
        $(".totalPage").text(contentCount);
        if(resultData.ReturnType == "1001"){
          loadContentList(resultData);//加载来源的筛选条件
        }else{
          $(".ri_top3_con").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到节目</div>");
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  function loadContentList(resultData){//加载来源的筛选条件
    for(var i=0;i<resultData.ResultList.length;i++){
      var cptime=resultData.ResultList[i].ContentTime;
      var listbox='<div class="rtc_listBox" contentId='+resultData.ResultList[i].ContentId+' contentChannelId='+resultData.ResultList[i].ChannelId+' mediaType='+resultData.ResultList[i].MediaType+'>'+
                    '<img src="img/checkbox1.png" alt="" class="rtcl_img_check fl checkbox_img checkbox1"/>'+
                    '<div class="rtcl_img fl">'+
                      '<img src='+resultData.ResultList[i].ContentImg+' alt="节目图片" />'+
                    '</div>'+
                    '<div class="rtcl_con fl">'+
                      '<p class="rtcl_con_p ellipsis">'+resultData.ResultList[i].ContentName+'</p>'+
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
      $(".audio_time").eq(i).text(getLocalTime(cptime)); 
      if(resultData.ResultList[i].ChannelName){
        $(".rtcl_con_channel1s").eq(i).text(resultData.ResultList[i].ChannelName);
      }else{
        $(".rtcl_con_channel1s").eq(i).text("暂无");
      }
      if(resultData.ResultList[i].MediaSize){
        $(".sequ_sum").eq(i).text(resultData.ResultList[i].MediaSize);
      }else{
        $(".sequ_sum").eq(i).text("1");
      }
      if(resultData.ResultList[i].PersonName){
        $(".anchor_name").eq(i).text(resultData.ResultList[i].PersonName);
      }else{
        $(".anchor_name").eq(i).text("暂无");
      }
      if(resultData.ResultList[i].ContentSource){
        $(".source_form").eq(i).text(resultData.ResultList[i].ContentSource);
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
        }else{//电台
          
        }
        contentList.ChannelIds=$(this).attr("contentchannelid");
        contentIds.push(contentList);
      }
    });
    if(contentIds.length==0){//未选中内容
      alert("请先选中内容再进行操作");
      return;
    }else{
      var data2={
                UserId:"zhangsan",
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
            $(".page").find("span").removeClass("disabled");
            destroy(data1);
            current_page=1;
            $(".currentPage").html(current_page);
            data1.UserId="123";
            data1.PageSize="10";
            data1.Page=current_page;
            data1.ContentFlowFlag=flowflag;
            if($(".new_cate li").size()>"0"){
              $(document).find(".new_cate li").each(function(){
                var pId=$(this).attr("pid");
                var id=$(this).attr("id");
                if(pId=="channel"){
                  data1.CatalogsId=$(this).attr("id");
                }else{
                  data1.CatalogsId=$(this).attr("id");
                }
              });
            }
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
  $(".rto_nopass").on("click",function(){
    $(".nc_txt1").text(" ");
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
        }else{//电台
          
        }
        contentList.ChannelIds=$(this).attr("contentchannelid");
        contentIds.push(contentList);
      }
    });
    if(contentIds.length==0){//未选中内容
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
      $(".nc_txt1").text("选择了"+contentIds.length+"个节目，您确认所选的节目不能通过审核么？");
      $(".nopass_container").show();
    }
  });
  //点击不通过原因页面的确定
  $(".nc_txt7").on("click",function(){
    var data4={
                UserId:"123",
                ContentIds:contentIds,
                OpeType:$(".nopass").attr("opetype")
    };
    $.ajax({
      type: "POST",
      url:rootPath+"CM/content/updateContentStatus.do",
      dataType:"json",
      data:JSON.stringify(data1),
      success: function(resultData){
        if(resultData.ReturnType=="1001"){
          alert("不通过的具体原因提交成功");
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
    $(".page").find("span").removeClass("disabled");
    destroy(data1);
    current_page=1;
    $(".currentPage").html(current_page);
    data1.UserId="123";
    data1.PageSize="10";
    data1.Page=current_page;
    data1.ContentFlowFlag=flowflag;
    if($(".new_cate li").size()>"0"){
      $(document).find(".new_cate li").each(function(){
        var pId=$(this).attr("pid");
        var id=$(this).attr("id");
        if(pId=="channel"){
          data1.CatalogsId=$(this).attr("id");
        }else{
          data1.SourceId=$(this).attr("id");
        }
      });
    }
    if(($(".startPubTime").val())&&($(".endPubTime").val())){
      data1.BeginContentPubTime=new Date($(".startPubTime").val()).getTime();
      data1.EndContentPubTime=new Date($(".endPubTime").val()).getTime();
    }
    getContentList(data1);
  });
  $(document).on("click",".cate_img",function(){//点击取消所选的筛选条件
    $(".page").find("span").removeClass("disabled");
    destroy(data1);
    current_page=1;
    $(".currentPage").html(current_page);
    data1.UserId="123";
    data1.PageSize="10";
    data1.Page=current_page;
    data1.ContentFlowFlag=flowflag;
    if($(".new_cate li").size()>"0"){
      $(document).find(".new_cate li").each(function(){
        var pId=$(this).attr("pid");
        var id=$(this).attr("id");
        if(pId=="channel"){
          data1.CatalogsId=$(this).attr("id");
        }else{
          data1.SourceId=$(this).attr("id");
        }
      });
    }else{
      optfy=1;//未选中具体筛选条件前翻页
    }
    if(($(".startPubTime").val())&&($(".endPubTime").val())){
      data1.BeginContentPubTime=new Date($(".startPubTime").val()).getTime();
      data1.EndContentPubTime=new Date($(".endPubTime").val()).getTime();
    }
    getContentList(data1);
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
      $(".page").find("span").removeClass("disabled");
      destroy(data1);
      current_page=1;
      $(".currentPage").html(current_page);
      data1.UserId="123";
      data1.PageSize="10";
      data1.Page=current_page;
      data1.ContentFlowFlag=flowflag;
      data1.BeginContentPubTime=new Date($(".startPubTime").val()).getTime();
      data1.EndContentPubTime=new Date($(".endPubTime").val()).getTime();
      if($(".new_cate li").size()>"0"){
        $(document).find(".new_cate li").each(function(){
          var pId=$(this).attr("pid");
          var id=$(this).attr("id");
          if(pId=="channel"){
            data1.CatalogsId=$(this).attr("id");
          }else{
            data1.SourceId=$(this).attr("id");
          }
        });
      }
      getContentList(data1);
    }
  });
  /*点击筛选条件日期附近的清除按钮*/
  $(".clean").on("click",function(){
    $(".startPubTime,.endPubTime").val("");
    $(".page").find("span").removeClass("disabled");
    destroy(data1);
    current_page=1;
    $(".currentPage").html(current_page);
    data1.UserId="123";
    data1.PageSize="10";
    data1.Page=current_page;
    data1.ContentFlowFlag=flowflag;
    if($(".new_cate li").size()>"0"){
      optfy=2;//选中具体筛选条件后翻页
      $(document).find(".new_cate li").each(function(){
        var pId=$(this).attr("pid");
        var id=$(this).attr("id");
        if(pId=="channel"){
          data1.CatalogsId=$(this).attr("id");
        }else{
          data1.SourceId=$(this).attr("id");
        }
      });
    }else{
      optfy=1;//选中具体筛选条件后翻页
    }
    getContentList(data1);
  });
  /*清空*/
  function clear(){
    $(".ri_top3_con,.totalPage").html("");
    $(".toPage").val("");
  }
  /*销毁obj对象的key-value*/
  function destroy(obj){
    for(var key in obj){//清空对象
      delete obj[key];
    }
  }
});
