$(function(){
  var href=$("#myIframe", parent.document).attr("src");
  var contentId=href.split('?')[1].split("=")[1];
  var rootPath=getRootPath();
  //获取用户的id
  var userId=$(".login_user span",parent.document).attr("userid");
  
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var pageSize=2;//每页条数
  
  var data={"MobileClass":"Chrome",
            "UserId":userId,
            "PCDType":"3",
            "ContentId":contentId
  };
  getZjDetail(data);//得到专辑信息
  function getZjDetail(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/getSeqMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(dataParam),
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          fillZjDetail(resultData);//填充专辑数据
        }else{
          alert(resultData.Message);
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  var _data={"MobileClass":"Chrome",
             "UserId":userId,
             "PCDType":"3",
             "PageSize":pageSize,
             "Page":current_page,
             "FlagFlow":"0",
             "SeqMediaId":contentId
  };
  //得到专辑下面的节目列表
  getZj_jmList(_data);
  function getZj_jmList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/getMediaList.do",
      dataType:"json",
      data:JSON.stringify(dataParam),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          allCount=resultData.AllCount;
          fillZj_jmList(resultData);//填充专辑数据
        }else{
          $(".list_content").html("");//每次加载之前先清空
          $(".list_content").html("<li style='text-align: center;'>暂无节目数据</li>");
          $(".list_title h4").html("共0个节目");
          allCount="0";
        }
        contentCount=(allCount%pageSize==0)?(allCount/pageSize):(Math.ceil(allCount/pageSize));
        pagitionInit(contentCount,allCount,_data.Page);//init翻页
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //填充专辑数据
  function fillZjDetail(resultData){
    $(".dcl_zjcontent2 h3").html(resultData.Result.ContentName);
    $(".dcl_zjcontent2 .h3span").html("("+resultData.Result.SubCount+"个节目)");
    $(".dcl_zjcontent1 img").attr("src",resultData.Result.ContentImg);
    $(".dcl_zjcontent2 p span").html("【"+resultData.Result.ContentPubChannels[0].ChannelName+"】");
    if(resultData.Result.ContentKeyWords!=null){
      for(var i=0;i<resultData.Result.ContentKeyWords.length;i++){
        var newli='<li><span>'+resultData.Result.ContentKeyWords[i].TagName+'</span></li>';
        $(".dcl_zjcontent2 ul").append(newli);
      }
    }
    $(".ctimes").html("最近更新时间："+resultData.Result.CTime);
    $(".dcl_zjcontent3_p1").html(resultData.Result.ContentDesc);
    $(".list_title h4").html("共"+resultData.Result.SubCount+"个节目");
  }
  
  getMyZjDetail();//得到我的所有专辑信息
  function getMyZjDetail(){
    $.ajax({
      type:"POST",
      url:rootPath+"content/seq/getSeqMediaList.do",
      dataType:"json",
      data:{"MobileClass":"Chrome",
            "UserId":userId,
            "PCDType":"3",
            "FlagFlow":"0",
            "ShortSearch":"false"
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          fillMyZjDetail(resultData);//填充我的所有专辑的数据
        }else{
          alert(resultData.Message);
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  //填充我的所有专辑的数据
  function fillMyZjDetail(resultData){
    for(var i=0;i<resultData.ResultList.length;i++){
      var newzj='<div class="dcr_zjlist1" contentId='+resultData.ResultList[i].ContentId+'>'+
                '<div class="list1_img">'+
                  '<img src='+resultData.ResultList[i].ContentImg+' alt="节目图片" />'+
                '</div>'+
                '<div class="list1_con">'+
                  '<p class="list1_con_name">'+
                    '<span>专辑名称 ：</span>'+
                    '<span class="zjname">'+resultData.ResultList[i].ContentName+'</span>'+
                  '</p>'+
                  '<p class="list1_con_num">'+
                    '<span>节目数量 ： </span>'+
                    '<span>'+resultData.ResultList[i].SubCount+'个</span>'+
                  '</p>'+
                  '<p class="list1_con_time">'+
                    '<span>时间 ：</span>'+
                    '<span>'+resultData.ResultList[i].CTime+'</span>'+
                  '</p>'+
                '</div>'+
              '</div>';
      $(".dcr_zjlist").append(newzj);
    }
  }
  
  //点击右侧我的所有专辑列表，调到对应的详情页
  $(document).on("click",".list1_img",function(){
    var contentId=$(this).parent(".dcr_zjlist1").attr("contentId");
    $("#myIframe", parent.document).attr({"src":"jmANDzj/zj_detail.html?contentId="+contentId+""});
    getZjDetail(contentId);
  })
  
  //专辑的节目列表
  function fillZj_jmList(resultData){
    for(var i=0;i<resultData.ResultList.length;i++){
      var programBox= '<li class="rtc_listBox" contentSeqId='+resultData.ResultList[i].ContentSeqId+' contentId='+resultData.ResultList[i].ContentId+'>'+
                        '<img src="../anchorResource/img/checkbox1.png" alt="" class="ric_img_check fl checkbox_img checkbox1"/>'+
                        '<div class="rtcl_img fl">'+
                          '<img src='+resultData.ResultList[i].ContentImg+' alt="节目图片" />'+
                        '</div>'+
                        '<div class="rtcl_con">'+
                          '<h4></h4>'+
                          '<p class="other">'+
                            '<span>时间：</span>'+
                            '<span class="ctime"></span>'+
                          '</p>'+
                          '<p class="other">'+
                            '<span>喜欢次数 ：</span>'+
                            '<span class="loveCount"></span>'+
                          '</p>'+
                        '</div>'+
                        '<p class="jm_st"></p>'+
                        '<div class="op_type" id="op_Box'+i+'">'+
                          '<p class="jm_edit c173">编辑</p>'+
                          '<p class="jm_submit c173">提交</p>'+
                          '<p class="jm_recal c173">撤回</p>'+
                          '<p class="jm_del c173">删除</p>'+
                        '</div>'+
                      '</li>';
      $(".list_content").append(programBox);
      $(".rtc_listBox").children(".rtcl_con").children("h4").eq(i).text(((resultData.ResultList[i].ContentName)?(resultData.ResultList[i].ContentName):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".other").children(".ctime").eq(i).text(((resultData.ResultList[i].CTime)?(resultData.ResultList[i].CTime):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".other").children(".loveCount").eq(i).text(((resultData.ResultList[i].ContentFavorite)?(resultData.ResultList[i].ContentFavorite):"暂无"));
      if(resultData.ResultList[i].ContentPubChannels){
        $(".rtc_listBox").children(".jm_st").eq(i).text(((resultData.ResultList[i].ContentPubChannels[0].FlowFlagState)?(resultData.ResultList[i].ContentPubChannels[0].FlowFlagState):"未知"));
        var channelds="";
        for(var j=0;j<resultData.ResultList[i].ContentPubChannels.length;j++){
          if(channelds==""){
            channelds=resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }else{
            channelds+=","+resultData.ResultList[i].ContentPubChannels[j].ChannelId;
          }
        }
        $(".rtc_listBox").children(".jm_st").eq(i).attr("jmStatusId",resultData.ResultList[i].ContentPubChannels[0].FlowFlag);
        $(".rtc_listBox").eq(i).attr("channelId",channelds);
        var flowflag=resultData.ResultList[i].ContentPubChannels[0].FlowFlag;
        switch(flowflag){//0(已提交),1(审核中),2(已发布),3(未通过),4(已撤回)
          case "0": $("#op_Box"+i).children(".jm_edit,.jm_submit,.jm_del").removeClass("c173").addClass("cf60");
          break;
          case "1": $("#op_Box"+i).children(".jm_edit,.jm_submit,.jm_del,.jm_recal").removeClass("cf60").addClass("c173");
          break;
          case "2": $("#op_Box"+i).children(".jm_recal").removeClass("c173").addClass("cf60");
          break; 
          case "3": $("#op_Box"+i).children(".jm_edit,.jm_submit,.jm_del,.jm_recal").removeClass("c173").addClass("cf60");
          break;
          case "4": $("#op_Box"+i).children(".jm_edit,.jm_submit,.jm_del").removeClass("c173").addClass("cf60");
          break;
          default:break;
        }
//       var flowflag=resultData.ResultList.List[i].ContentPubChannels[0].FlowFlag;
//       switch(flowflag){//0全部，1已保存，2发布审核中，3已审核(定时发布)，4已发布，5发布未通过,6待撤回(实际是撤回审核中)，7已撤回，8撤回未通过
//         case "1":case "5":case "7": $("#op_Box"+i).children(".jm_edit,.jm_submit,.jm_del").removeClass("c173").addClass("cf60");
//         break;
//         case "2":case "3":case "6": $("#op_Box"+i).children(".jm_edit,.jm_submit,.jm_del,.jm_recal").removeClass("cf60").addClass("c173");
//         break;
//         case "4":case "8": $("#op_Box"+i).children(".jm_recal").removeClass("c173").addClass("cf60");
//         break;        
//         default:break;
//       }
      }
    }
  }
  
  //选择不同的节目状态
    var count=0;
    var FlowFlag;
    $(".dropdown-menu").on("click","li",function(){
      switch($(this).attr("type")){
        case 'saved':
          FlowFlag=0;
          count=0;
          var dataParam={"MobileClass":"Chrome",
                         "PCDType":"3",
                         "UserId":userId,
                         "SeqMediaId":contentId,
                         "FlagFlow":FlowFlag,
                         "Page":current_page,
                         "PageSize":pageSize
          };
          $(".dropdown").html($(this).attr("text"));
          $(".list_content").html("");
          getZj_jmList(dataParam);
          break;
        case 'pending':
          FlowFlag=1;
          count=0;
          var dataParam={"MobileClass":"Chrome",
                         "PCDType":"3",
                         "UserId":userId,
                         "SeqMediaId":contentId,
                         "FlagFlow":FlowFlag,
                         "PageSize":pageSize,
                         "Page":current_page
          };
          $(".dropdown").html($(this).attr("text"));
          $(".list_content").html("");
          getZj_jmList(dataParam);
          break;
        case 'pended':
          FlowFlag=1;
          count=0;
          var dataParam={"MobileClass":"Chrome",
                         "PCDType":"3",
                         "UserId":userId,
                         "SeqMediaId":contentId,
                         "FlagFlow":FlowFlag,
                         "PageSize":pageSize,
                         "Page":current_page
          };
          $(".dropdown").html($(this).attr("text"));
          $(".list_content").html("");
          getZj_jmList(dataParam);
          break;
        case 'published':
          FlowFlag=2;
          count=0;
          var dataParam={"MobileClass":"Chrome",
                         "PCDType":"3",
                         "UserId":userId,
                         "SeqMediaId":contentId,
                         "FlagFlow":FlowFlag,
                         "PageSize":pageSize,
                         "Page":current_page
          };
          $(".dropdown").html($(this).attr("text"));
          $(".list_content").html("");
          getZj_jmList(dataParam);
          break;
        case 'notPass':
          FlowFlag=3;
          count=0;
          var dataParam={"MobileClass":"Chrome",
                         "PCDType":"3",
                         "UserId":userId,
                         "SeqMediaId":contentId,
                         "FlagFlow":FlowFlag,
                         "PageSize":pageSize,
                         "Page":current_page
          };
          $(".dropdown").html($(this).attr("text"));
          $(".list_content").html("");
          getZj_jmList(dataParam);
          break;
        case 'recall':
          FlowFlag=4;
          count=0;
          var dataParam={"MobileClass":"Chrome",
                         "PCDType":"3",
                         "UserId":userId,
                         "SeqMediaId":contentId,
                         "FlagFlow":FlowFlag,
                         "PageSize":pageSize,
                         "Page":current_page
          };
          $(".dropdown").html($(this).attr("text"));
          $(".list_content").html("");
          getZj_jmList(dataParam);
          break;
        default:
      }
    })
    
    
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
  
  /*s--根据搜索/筛选/翻页获取节目列表*/
  var optfy=1;//optfy=1未选中具体筛选条件前翻页,optfy=2选中具体筛选条件后翻页
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchWord="";
  
  /*选中某个节目的状态*/
  $(".dropdown_menu").on("click","li",function(){
    $(this).parent(".dropdown_menu").addClass("dis");
    $(this).parent(".dropdown_menu").siblings(".dropdown").children("img").attr({"src":"../anchorResource/img/filter2.png"});
    $(this).addClass("selected").siblings("li").removeClass("selected");
    optfy=2;//选中具体筛选条件后翻页
  });
  
  //翻页之后的回调函数
  function pagitionBack(current_page){
    $(".dropdown_menu li").each(function(){
      if($(this).hasClass("selected")){
        optfy=2;//选中具体筛选条件后翻页
        return;
      }else{
        optfy=1;//未选中具体筛选条件后翻页
      }
    });
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
    destroy(_data);
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.UserId=userId;
    _data.PageSize=pageSize;
    _data.Page=current_page;
    _data.SeqMediaId=contentId;
    $(".dropdown_menu li").each(function(){
      if($(this).hasClass("selected")){
        var cataId=$(this).attr("catalogId");
        _data.FlagFlow=cataId;
      }
    });
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(seaFy==1){//seaFy=1未搜索关键词前翻页
      getZj_jmList(_data);
    }else{//seaFy=2搜索列表加载出来后翻页
      _data.SearchWord=searchWord;
      getSearchList(_data);
    }
  }
  
  /*搜索*/
  //键盘上的enter事件
  $(".ri_top_li2_inp").keydown(function(e){
    var evt=event?event:(window.event?window.event:null);//兼容IE和FF
    if(evt.keyCode==13){
      searchList();//加载搜索列表
    }
  });
  //点击搜索小图标
  $(".ri_top_li2_img").on("click",function(){
    searchList();//加载搜索列表 
  });
  //加载搜索列表 
  function searchList(){
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(searchWord==""){
      alert("请输入搜索内容");
      $(".ri_top_li2_inp").focus();
    }else{
      destroy(_data);
      _data.MobileClass="Chrome";
      _data.PCDType="3";
      _data.UserId=userId;
      _data.PageSize=pageSize;
      _data.Page=current_page;
      _data.FlagFlow="0";
      _data.ChannelId="0";
      _data.SeqMediaId=contentId;
      _data.SearchWord=searchWord;
      getSearchList(_Data);
    }
  }
  function getSearchList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/content/searchContents.do",
      dataType:"json",
      data:JSON.stringify(dataParam),
      beforeSend: function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载节目列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        clear_jm();
        if(resultData.ReturnType == "1001"){
          $(".ri_top3_con").html("");//每次加载之前先清空
          allCount=resultData.AllCount;
          contentCount=(allCount%2==0)?(allCount/2):(Math.ceil(allCount/2));
          fillZj_jmList(resultData);//加载搜索得到的节目列表
        }else{
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>没有找到节目...</div>");
          allCount="0";
          contentCount=(allCount%2==0)?(allCount/2):(Math.ceil(allCount/2));
        }
        pagitionInit(contentCount,allCount,_data.Page);//init翻页
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  /*e--根据搜索/筛选/翻页获取节目列表*/
 
  /*s--相关操作的集合*/
  /*s--点击相关操作的编辑按钮*/
  $(document).on("click",".jm_edit",function(){
    var contentId=$(this).parents(".rtc_listBox").attr("contentid");
    var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("jmstatusid");
//  if(flowFlag=="2"||flowFlag=="3"||flowFlag=="4"||flowFlag=="6"||flowFlag=="8"){
      //2发布审核中，3已审核(定时发布)，4已发布，6待撤回(实际是撤回审核中)，8撤回未通过
    if(flowFlag=="1"||flowFlag=="2"){//0(已提交),1(审核中),2(已发布),3(未通过),4(已撤回)
      alert("当前状态不支持编辑操作");
      return;
    }else{
      edit_jm(contentId);//编辑节目时得到节目的详细信息
    }
  });
  //编辑节目时得到节目的详细信息
  function edit_jm(contentId){
    var datas={"DeviceId":deviceId,
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":userId,
               "ContentId":contentId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/getMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(datas),
      beforeSend: function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          clear_jm();//填充前清空数据
          $("body").css({"overflow":"hidden"});
          getTime();
          fillJmContent(resultData);//填充节目信息
        }else{
          alert(resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  //填充节目信息
  function fillJmContent(resultData){
    $(".jmId").attr("value",resultData.Result.ContentId);
    $(".iboxtitle h4").html("修改节目");
    $(".yp_mz").val("aa.mp3");//数据库没有存这一字段，因为有需要，我自己加上的
    $(".upl_file").attr("value",resultData.Result.ContentPlay);
    $(".audio").attr("src",resultData.Result.ContentPlay);
    $(".uplTitle").val(resultData.Result.ContentName);
    $(".defaultImg").attr("src",resultData.Result.ContentImg);
    $(".upl_img").attr("value",resultData.Result.ContentImg);
    $(".upl_zj option").each(function(){
      if($(this).attr("id")==resultData.Result.ContentSeqId){
        $(".upl_zj option").prop("selected",false);
        $(this).prop("selected",true); 
      }
    })
    if(resultData.Result.ContentKeyWords!=null){
      for(var i=0;i<resultData.Result.ContentKeyWords.length;i++){
        var new_tag= '<li class="upl_bq_img bqImg" tagId='+resultData.Result.ContentKeyWords[i].TagId+'>'+
                      '<span>'+resultData.Result.ContentKeyWords[i].TagName+'</span>'+
                      '<img class="upl_bq_cancelimg1 cancelImg" src="../anchorResource/img/upl_img2.png" alt="" />'+
                    '</li>';
        $(".upl_bq").append(new_tag);
        var tagId=resultData.Result.ContentKeyWords[i].TagId;
        $(".my_tag_con").find(".my_tag_con1").each(function(){
          if($(this).attr("tagId")==tagId){
            $(this).children("input").prop("checked",true);
            $(this).children("input").prop("disabled",true);
          }
        })
        $(".gg_tag_con").find(".gg_tag_con1").each(function(){
          if($(this).attr("tagId")==tagId){
            $(this).children("input").prop("checked",true);
            $(this).children("input").prop("disabled",true);
          }
        })
      }
    }
    $(".uplDecn").val(resultData.Result.ContentDesc);
    if(resultData.Result.ContentMemberTypes!=null){
      for(var i=0;i<resultData.Result.ContentMemberTypes.length;i++){
        var new_czfs= '<li class="czfs_tag_li bqImg" czfs_typeId='+resultData.Result.ContentMemberTypes[i].TypeId+'>'+
                        '<div class="czfs_tag_div">'+
                        '<span class="czfs_tag_span1">'+resultData.Result.ContentMemberTypes[i].TypeName+' : </span>'+
                        '<span class="czfs_tag_span2">'+resultData.Result.ContentMemberTypes[i].TypeInfo+'</span>'+
                        '</div>'+
                        '<img class="cancelImg" src="../anchorResource/img/upl_img2.png" alt="" />'+
                      '</li>';
        $(".czfs_tag").append(new_czfs); 
      }
    }
    $(".layer-date").val(resultData.Result.CTime);
  }
  //点击编辑页面的保存
  $("#submitBtn").on("click",function(){
    if(!$(".upl_img").attr("value")){
      $(".upl_img").attr("value",$(".defaultImg").attr("src"));
    }
    var _data={};
    _data.UserId=userId;
    _data.DeviceId=deviceId;
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentId=$(".jmId").val();
    _data.ContentURI=$(".audio").attr("src");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value")
    _data.SeqMediaId=$(".upl_zj option:selected").attr("id");
    _data.TimeLong=$(".timeLong").attr("value");
    var taglist=[];
    $(".upl_bq").find(".upl_bq_img").each(function(){
      var tag={};//标签对象
      var tagTxt=$(this).children("span").html();
      $(".my_tag_con1").each(function(){
        if($(this).children(".my_tag_con1_span").html()==tagTxt){
          $(".my_tag_con1").children(".my_tag_con1_check").prop("checked",false);
          $(".my_tag_con1").children(".my_tag_con1_check").attr("disabled",false);
          $(this).children(".my_tag_con1_check").prop("checked",true);
          $(this).children(".my_tag_con1_check").attr("disabled",true);
          tag.TagName=$(this).children(".my_tag_con1_span").html();
          tag.TagOrg="我的标签";
        }
      })
      $(".gg_tag_con1").each(function(){
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
    _data.ContentDesc=$(".uplDecn").val();
    var memberTypelist=[];
    $(".czfs_tag").find(".czfs_tag_li").each(function(){
      var czfsObj={};//创作方式对象
      var czfs_t=""+$(this).children().children(".czfs_tag_span1").html();
      var czfs_txt=czfs_t.split(":")[0];
      czfsObj.TypeName=czfs_txt;
      czfsObj.TypeId=$(this).attr("czfs_typeid");
      czfsObj.TypeInfo=$(this).children().children(".czfs_tag_span2").html();
      memberTypelist.push(czfsObj);
    });
    _data.MemberType=memberTypelist;
    var str_time=$(".layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend: function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("修改内容保存成功");
          $(".mask_jm,.add_jm").hide();
          $("body").css({"overflow":"auto"});
          getZj_jmList(_data);//重新加载节目列表
        }else{
          alert(resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  });
  //点击编辑页面的提交
  var _datas={};
  $("#pubBtn").on("click",function(){
    _datas.UserId=userId;
    _datas.DeviceId=deviceId;
    _datas.MobileClass="Chrome";
    _datas.PCDType="3";
    _datas.ContentId=$(".jmId").val();
    _datas.ContentURI=$(".audio").attr("src");
    _datas.ContentName=$(".uplTitle").val();
    _datas.ContentImg=$(".upl_img").attr("value");
    _datas.SeqMediaId=$(".upl_zj option:selected").attr("id");
    _datas.TimeLong=$(".timeLong").attr("value");
    var taglist=[];
    $(".upl_bq").find(".upl_bq_img").each(function(){
      var tag={};//标签对象
      var tagTxt=$(this).children("span").html();
      $(".my_tag_con1").each(function(){
        if($(this).children(".my_tag_con1_span").html()==tagTxt){
          $(".my_tag_con1").children(".my_tag_con1_check").prop("checked",false);
          $(".my_tag_con1").children(".my_tag_con1_check").attr("disabled",false);
          $(this).children(".my_tag_con1_check").prop("checked",true);
          $(this).children(".my_tag_con1_check").attr("disabled",true);
          tag.TagName=$(this).children(".my_tag_con1_span").html();
          tag.TagOrg="我的标签";
        }
      })
      $(".gg_tag_con1").each(function(){
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
    _datas.TagList=taglist;
    _datas.ContentDesc=$(".uplDecn").val();
    var memberTypelist=[];
    $(".czfs_tag").find(".czfs_tag_li").each(function(){
      var czfsObj={};//创作方式对象
      var czfs_t=""+$(this).children().children(".czfs_tag_span1").html();
      var czfs_txt=czfs_t.split(":")[0];
      czfsObj.TypeName=czfs_txt;
      czfsObj.TypeId=$(this).attr("czfs_typeid");
      czfsObj.TypeInfo=$(this).children().children(".czfs_tag_span2").html();
      memberTypelist.push(czfsObj);
    });
    _datas.MemberType=memberTypelist;
    var str_time=$(".layer-date").val();
    var rst_strto_time=js_strto_time(str_time);
    _data.FixedPubTime=rst_strto_time;
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_datas),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          pubEditJm(_datas);
        }else{
          alert(resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  });
  function pubEditJm(_datas){
    var contentId=$(".jmId").val();
    var contentSeqId=_datas.SeqMediaId;
    var list=[{"ContentId":contentId,"SeqMediaId":contentSeqId}];
    var data4={"DeviceId":deviceId,
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":userId,
               "ContentFlowFlag":"2",
               "UpdateList":list
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaStatus.do",
      dataType:"json",
      data:JSON.stringify(data4),
      beforeSend: function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("节目发布成功");
          $(".mask_jm,.add_jm").hide();
          $("body").css({"overflow":"auto"});
          getZj_jmList(_datas);//重新加载节目列表
        }else{
          alert(resultData.Message);
        }
        $('.shade', parent.document).show();
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  /*e--点击相关操作的编辑按钮*/
  
  /*s--点击相关操作的提交按钮*/
  $(document).on("click",".jm_submit",function(){
     var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("jmstatusid");
     if(flowFlag=="1"||flowFlag=="2") {//0(已提交),1(审核中),2(已发布),3(未通过),4(已撤回)
      alert("当前状态不支持发布操作");
      return;
    }else{
      var contentId=$(this).parents(".rtc_listBox").attr("contentid");
      var contentSeqId=$(this).parents(".rtc_listBox").attr("contentseqid");
      $('.shade', parent.document).show();
      submit_jm(contentId,contentSeqId);
    }
  })
  function submit_jm(contentId,contentSeqId){
    var datas={"DeviceId":deviceId,
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":userId,
               "UpdateList":[{"ContentId":contentId,"SeqMediaId":contentSeqId}],
               "ContentFlowFlag":"2"
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaStatus.do",
      dataType:"json",
      data:JSON.stringify(datas),
      beforeSend:function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("发布节目请求成功");
//        var _data={"DeviceId":deviceId,
//                  "MobileClass":"Chrome",
//                  "UserId":userId,
//                  "PCDType":"3",
//                  "PageSize":pageSize,
//                  "Page":current_page,
//                  "FlagFlow":"0",
//                  "ChannelId":"0",
//                  "SeqMediaId":contentId
//      };
//      getZj_jmList(_data);//重新加载节目列表
        }else{
          alert(resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  /*e--点击相关操作的提交按钮*/
 
  /*s--点击相关操作的撤回按钮*/
  $(document).on("click",".jm_recal",function(){
    var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("jmstatusid");
    if(flowFlag=="2"||flowFlag=="3"){//0(已提交),1(审核中),2(已发布),3(未通过),4(已撤回)
      $('.shade', parent.document).show();
      var contentId=$(this).parents(".rtc_listBox").attr("contentid");
      var seqId=$(this).parents(".rtc_listBox").attr("contentseqid");
      recal_jm(contentId,seqId);
    }else{
      alert("当前节目不支持撤回操作");
      return;
    }
  })
  function recal_jm(contentId,seqId){
    var datas={"DeviceId":deviceId,
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":userId,
               "UpdateList":[{"ContentId":contentId,"SeqMediaId":seqId}],
               "ContentFlowFlag":"4"
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaStatus.do",
      dataType:"json",
      data:JSON.stringify(datas),
      beforeSend:function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("撤回节目请求已发送");
//        var _data={"DeviceId":deviceId,
//                  "MobileClass":"Chrome",
//                  "UserId":userId,
//                  "PCDType":"3",
//                  "PageSize":pageSize,
//                  "Page":current_page,
//                  "FlagFlow":"0",
//                  "ChannelId":"0",
//                  "SeqMediaId":contentId
//      };
//      getZj_jmList(_data);//重新加载节目列表
        }else{
          alert(resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  /*e--点击相关操作的撤回按钮*/
 
  /*s--点击相关操作的删除按钮*/
  $(document).on("click",".jm_del",function(){
    var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("jmstatusid");
    if(flowFlag=="0"||flowFlag=="4"){//0(已提交),1(审核中),2(已发布),3(未通过),4(已撤回)
      $('.shade', parent.document).show();
      var contentId=$(this).parents(".rtc_listBox").attr("contentid");
      var contentSeqId=$(this).parents(".rtc_listBox").attr("contentseqid");
      del_jm(contentId,contentSeqId);
    }else{
      alert("当前节目不支持删除操作");
      return;
    }
  })
  function del_jm(contentId,contentSeqId){
    var datas={"DeviceId":deviceId,
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":userId,
               "ContentIds":contentId,
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/removeMedia.do",
      dataType:"json",
      data:JSON.stringify(datas),
      beforeSend:function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("节目删除成功");
//        var _data={"DeviceId":deviceId,
//                  "MobileClass":"Chrome",
//                  "UserId":userId,
//                  "PCDType":"3",
//                  "PageSize":pageSize,
//                  "Page":current_page,
//                  "FlagFlow":"0",
//                  "ChannelId":"0",
//                  "SeqMediaId":contentId
//      };
//      getZj_jmList(_data);//重新加载节目列表
        }else{
          alert(resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  /*e--点击相关操作的删除按钮*/
 
  /*e--相关操作的集合*/
  
 
});