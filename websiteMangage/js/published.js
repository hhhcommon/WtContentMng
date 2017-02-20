$(function(){
  var rootPath=getRootPath();
  var flowflag="2";
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  
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
        getContentList(current_page,flowflag);
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
        getContentList(current_page,flowflag);
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
        getContentList(current_page,flowflag);
        return;
      }
    }
  });
  /*得到资源列表*/
  getContentList(current_page,flowflag);
  function getContentList(current_page,flowflag){
    var data3={
                "UserId":"123",
                "ContentFlowFlag":flowflag,
                "Page":current_page,
                "PageSize":"10"
              };
    $.ajax({
      type:"POST",
      url:rootPath+"CM/content/getContents.do",
      dataType:"json",
      data:JSON.stringify(data3),
      success:function(resultData){
        $(".ri_top3_con").html("");
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
  function loadContentList(resultData){
    for(var i=0;i<resultData.ResultList.length;i++){
      var cptime=resultData.ResultList[i].ContentTime;
      var listbox='<div class="rtc_listBox" contentId='+resultData.ResultList[i].ContentId+' contentChannelId='+resultData.ResultList[i].ChannelId+' mediaType='+resultData.ResultList[i].MediaType+'>'+
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
      $(".audio_time").eq(i).text(getLocalTime(cptime));
      if(resultData.ResultList[i].MediaType=="wt_SeqMediaAsset"){
        $(".sequ_name").eq(i).text("该内容已经是专辑");
      }else if(resultData.ResultList[i].MediaType=="wt_MediaAsset"){
        if(resultData.ResultList[i].ContentSeqName){
          $(".sequ_name").eq(i).attr({"contentSeqId":resultData.ResultList[i].ContentSeqId}).text(resultData.ResultList[i].ContentSeqName);
        }else{
          $(".sequ_name").eq(i).text("自定义专辑名称");
        }
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
  /*已发布内容--撤回*/
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
                UserId:"zhangsan",
                ContentIds:contentIds,
                OpeType:$(".nopass").attr("opetype")
    };
    $.ajax({
      type: "POST",
      url:rootPath+"CM/content/updateContentStatus.do",
      dataType:"json",
      data:JSON.stringify(data4),
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
  /*点击跳到对应的详情页*/
  $(document).on("click",".rtc_listBox .rtcl_img",function(){
    var contentId=$(this).parent(".rtc_listBox").attr("contentid");
    alert(contentId);
    if($(this).parent(".rtc_listBox").attr("mediatype")=="wt_SeqMediaAsset"){//专辑
      $("#newIframe", parent.document).attr({"src":"zj_detail.html?contentId="+contentId});
    }else if($(this).parent(".rtc_listBox").attr("mediatype")=="wt_MediaAsset"){//节目
      $("#newIframe", parent.document).attr({"src":"jm_detail.html?contentId="+contentId});
    }else{//电台
      
    }
    $("#myIframe", parent.document).hide();
    $("#newIframe", parent.document).show();
  });
});