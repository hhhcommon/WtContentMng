$(function(){
  var rootPath=getRootPath();
  
  //图片是404时使用默认图片
  $(".boximg").error(function(){ 
    var _img="http://www.wotingfm.com/dataCenter/shareH5/mweb/imgs/default_img.png";
    $(this).attr("src", _img); 
  }); 
  $(".pic1").error(function(){ 
    var _img="http://www.wotingfm.com/dataCenter/shareH5/mweb/imgs/default_img.png";
    $(this).attr("src", _img); 
  }); 
  
  $(".ulBox").on("click",".playBtn",function(){//点击专辑里面的某个节目，跳到节目页
    var shareUrl=$(this).attr("share_url");
    window.location.href=shareUrl;
  });
  
  //详情和节目,评论的切换
  $(".tab_nav span").on("click",function(){
    $(this).addClass("selected").siblings().removeClass();
    $(".tab_con > .tc").hide().eq($(".tab_nav span").index(this)).show();
  });
  
  //加载更多
  var timeLong='';
  function loadMore(resultData){
    var detail={};
    for(var i=0;i<resultData.ResultList.length;i++){
      var str=resultData.ResultList[i].CTime;
      var ct=str.substring(0,str.lastIndexOf(":"));
      if(resultData.ResultList[i].ContentTimes){
        timeLong=parseInt(resultData.ResultList[i].ContentTimes/1000);
      }else{
        timeLong="0";
      }
      var tl=formatTimeTJ(timeLong);
      if(resultData.ResultList[i].ContentPlay) detail.contentPlay=resultData.ResultList[i].ContentPlay;
      else detail.contentPlay="未知";
      if(resultData.ResultList[i].ContentShareURL) detail.contentShareUrl=resultData.ResultList[i].ContentShareURL;
      else detail.contentShareUrl="未知";
      if(resultData.ResultList[i].ContentName) detail.contentName=resultData.ResultList[i].ContentName;
      else detail.contentName="未知";
      if(resultData.ResultList[i].PlayCount) detail.playCount=resultData.ResultList[i].PlayCount;
      else detail.playCount="0";
      var listBox='<li class="listBox playBtn" contentId='+resultData.ResultList[i].ContentId+' data_src='+detail.contentPlay+' share_url='+detail.contentShareUrl+'>'+
                    '<h4>'+detail.contentName+'</h4>'+
                    '<div class="time">'+ct+'</div>'+
                    '<p class="lcp">'+
                      '<img src="../../imgs/sl.png" alt=""/>'+
                      '<span class="playCount"></span>'+
                      '<img src="../../imgs/sc.png" alt="" class="sc"/>'+
                      '<span class="contentT">'+tl+'</span>'+
                    '</p>'+
                  '</li>';
      $(".ulBox").append(listBox);
    }
  }
  
  /*s--节目列表分页插件*/
  var page1=2;
  $('.zj_box').dropload({
    scrollArea :window,
    loadDownFn :function(me){
      var _data={
                  "ContentId":$(".PicBox").attr("contentId"),
                  "MediaType":"SEQU",
                  "Page":page1,
      };
      $.ajax({
        type: "POST",
        url:rootPath+"content/getZJSubPage.do",
        data:JSON.stringify(_data),
        dataType:'json',
        success:function(resultData){
          if(resultData.ReturnType=="1001"){
            loadMore(resultData);
            getPlayCount(resultData);
            page1++;
          }else{
            me.lock();//锁定
            me.noData();//无数据
          }
          setTimeout(function(){//为了测试，延迟1秒加载
            // 每次数据插入，必须重置
            me.resetload();
          },1000);
        },
        error: function(xhr, type){
          alert('数据加载出错!');
          // 即使加载出错，也得重置
          me.resetload();
        }
      });
    }
  });
  /*e--节目列表分页插件*/
  
  //获取节目列表的playCount
  function getPlayCount(resultData){
    var contentIds='';
    var seqId=$(".PicBox").attr("ContentId");
    if(resultData.ResultList.length<=1){
      var jmId=resultData.ResultList[0].ContentId;
      var contentId=seqId+'_'+jmId;
    }else{//多于一天数据时用逗号拼接
      for(var i=0;i<resultData.ResultList.length;i++){
        var jmId=resultData.ResultList[i].ContentId;
        var contentId=seqId+'_'+jmId;
        if(contentIds==''){
          contentIds=contentId;
        }else{
          contentIds+=','+contentId;
        }
      }
    }
    var _data={"ContentIds":contentIds};
    $.ajax({
        type:"POST",
        url:rootPath+"content/getPlayCounts.do",
        dataType:"json",
        async:false,
        data:JSON.stringify(_data),
        success: function(resultData){
          if(resultData.ReturnType=="1001"){
            var playCounts=[];
            var keys=[];
            for(var key in resultData.ResultData){
              var _key=key;
              var _value=resultData.ResultData[key];
              keys.push(_key);
              playCounts.push(_value);
            }
            $(".listBox").each(function(){
              var cid=$(this).attr("contentId");
              for(var i=0;i<keys.length;i++){
                var ccid=keys[i].split("_")[1];
                if(ccid==cid){
                  var tid=playCounts[i];
                  $(this).children(".lcp").children(".playCount").text(tid);
                  return;
                }
              }
            });
          }
        },
        error: function(jqXHR){  
          alert("发生错误" + jqXHR.status);
        }     
      });
  }
  
  //获取节目列表的节目时长
  $(document).find(".ulBox li").each(function(){
    var tl=$(this).children(".lcp").children(".contentT").text();
    timeLong=parseInt(tl)/1000;
    $(this).children(".lcp").children(".contentT").text(formatTimeTJ(timeLong));
  })

  //秒转时分秒格式--节目列表
  function formatTimeTJ(longTime){ 
    var time=parseFloat(longTime);
    if(time!=null && time !=""){
      if(time<60){
        var s=time;
        if(s<10){
          time="00'0"+s+"\"";
        }else{
          time="00'"+s+"\"";
        }
      }else if(time>60){
        var m=parseInt(time / 60);
        var s=parseInt(time %60);
        m = m >= 10 ? m : "0" + m;
        s = s >= 10 ? s : "0" + s;
        time=m+"\'"+s+"\"";
      }
    }
    return time;
  }
  
  //打开APP或下载
  $(".downLoad,.like").click(function(){
    window.location=$(".PicBox").attr("zjOpenApp");
    window.setTimeout(function () {
      window.location.href= "http://www.wotingfm.com/download/WoTing.apk";
    },2000);
  });
  
  //请求查看评论
  var contentId=eval('('+$(".PicBox").attr("zjOpenApp").split("=")[1]+')').ContentId;
  comment(contentId);
  function comment(contentId){
    var data={
          "RemoteUrl":"http://www.wotingfm.com:808/wt/discuss/article/getList.do",
          "IMEI":"3279A27149B24719991812E6ADBA5583",
          "PCDType":"3",
          "ContentId":contentId,
          "MediaType":"AUDIO",
          "Page":"1",
          "PageSize":"20"
    };
    $.ajax({
      url: rootPath+"common/jsonp.do",
      type:"POST",
      dataType:"json",
      data:JSON.stringify(data),
      success: function(resultData) {
        var resultData=eval('(' + resultData.Data + ')');
        if(resultData.ReturnType=="1001"){
          loadCommentList(resultData);
        }else{
          $(".comment").html("");
          $(".comment").append("<li class='noComment'>暂无评论</li>");
          $(".noComment").css({"height":$(".tab_con").height()});
        }
      },
      error: function(jqXHR){
        alert("发生错误" + jqXHR.status);
      }
    });
  }
  
  //加载评论列表
  function loadCommentList(resultData){
    $(".comment").html("");
    for(var i=0;i<resultData.AllCount;i++){
      var commentTime=resultData.DiscussList[i].Time;
      var commentList='<li class="ctList" commentId='+resultData.DiscussList[i].Id+'>'+
                        '<div class="default"></div>'+
                        '<img src="http://qingting-pic.b0.upaiyun.com/www/UpYunImage/06d4dd215d2c3bd6d305c78065015552.png" class="audioImg" alt="节目图片">'+
                        '<div class="listCon">'+
                          '<p class="lcs">'+
                            '<span class="commentName">'+resultData.DiscussList[i].UserInfo.UserName+'</span>'+
                            '<span class="commentTime"></span>'+
                          '</p>'+
                          '<div class="commentContent">'+resultData.DiscussList[i].Discuss+'</div>'+
                       '</div>'+
                      '</li>';
      $(".comment").append(commentList);
      $(".commentTime").eq(i).text(formatCommentDate(commentTime));
    }
  }
  
  //评论时间转换
  function formatCommentDate(tt){ 
    var date = new Date(parseInt(tt));
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours() + ':';
    m = date.getMinutes() + ':';
    s = date.getSeconds(); 
    return Y+M+D+h+m+s;
  } 
  
});
