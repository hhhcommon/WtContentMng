$(function(){
 
  //获取用户的id
  var userId=$(".login_user span",parent.document).attr("userid");

  var rootPath=getRootPath();
  var subType=1;//subType=1代表在上传节目页面保存,subType=2代表在修改节目页面保存
  var pubType=1;//pubType=1代表在上传节目页面提交,pubType=2代表在修改节目页面提交
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var allCount=0;//总记录数
  var pageSize=10;//每页条数
  var flagflow=0;//节目的状态

  /*s--获取筛选条件*/
  var dataF={"MobileClass":"Chrome",
             "UserId":userId,
             "PCDType":"3",
             "MediaType":"MediaAsset",
  };
  getFiltrates(dataF);
  function getFiltrates(data){
    $.ajax({
      type:"POST",
      url:rootPath+"content/getFiltrates.do",
      dataType:"json",
      data:JSON.stringify(data),
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          getChannelLabel(resultData);//得到栏目的筛选标签
          getAlbumLabel(resultData);//得到专辑的筛选标签
          getStatusLabel(resultData);//得到节目的状态标签
        }
      },
      error:function(jqXHR){
        alert("加载筛选发生错误:"+ jqXHR.status);
      }
    });
  }
  //得到栏目的筛选标签
  function getChannelLabel(resultData){
    for(var i=0;i<resultData.ResultList.ChannelList.length;i++){
      var li='<li class="trig_item chnel" data_idx='+i+' id='+resultData.ResultList.ChannelList[i].id+' pid='+resultData.ResultList.ChannelList[i].parentId+'>'+
                '<div class="check_cate"></div>'+
                '<a class="ss1" href="javascript:void(0)">'+resultData.ResultList.ChannelList[i].nodeName+'</a>'+
              '</li>';
      var li_tab_ul='<ul class="tab_cont_item chnels" data_idx='+i+' data_name='+resultData.ResultList.ChannelList[i].nodeName+' pid='+resultData.ResultList.ChannelList[i].parentId+'></ul>';
      $(".attrTabcon").append(li_tab_ul);
      $("#channel .attrValues .av_ul").append(li);
      if(resultData.ResultList.ChannelList[i].isParent=="true"){
        for(var j=0;j<resultData.ResultList.ChannelList[i].children.length;j++){
          var li_tab_ul_li='<li class="trig_item_li" id='+resultData.ResultList.ChannelList[i].children[j].id+' pid='+resultData.ResultList.ChannelList[i].children[j].parentId+'>'+
                              '<a class="ss1" href="javascript:void(0)">'+resultData.ResultList.ChannelList[i].children[j].nodeName+'</a>'+
                            '</li>';
          $(".tab_cont_item").eq(i).append(li_tab_ul_li);
        }
      }else{
        var li_tab_ul_li='<li class="trig_item_li">'+
                            '<a class="ss1" href="javascript:void(0)" >暂无二级栏目</a>'+
                          '</li>';
        $(".tab_cont_item").eq(i).append(li_tab_ul_li);                  
      }
    }
  }
  //得到专辑的筛选标签
  function getAlbumLabel(resultData){
    for(var i=0;i<resultData.ResultList.SeqMediaList.length;i++){
      var filterAlbum='<li class="trig_item" id='+resultData.ResultList.SeqMediaList[i].PubId+'>'+
                        '<div class="check_cate"></div>'+
                        '<a class="ss1" href="javascript:void(0)">'+resultData.ResultList.SeqMediaList[i].PubName+'</a>'+
                      '</li>';
      $("#album .attrValues .av_ul").append(filterAlbum);                
    }
  }
  //得到节目的状态标签
  function getStatusLabel(resultData){
    for(var i=0;i<resultData.ResultList.FlowFlag.length;i++){
      var filterStatus='<li id='+resultData.ResultList.FlowFlag[i].FlowFlagId+'><span>'+resultData.ResultList.FlowFlag[i].FlowFlagName+'</span></li>';
      $(".dropdown_menu").append(filterStatus);                
    }
  }
  /*e--获取栏目筛选条件*/
 
  /*s--获取节目列表*/
  var jmData={};
  jmData.MobileClass="Chrome";
  jmData.PCDType="3";
  jmData.UserId=userId;
  jmData.FlagFlow=flagflow;
  jmData.PageSize=pageSize;
  jmData.Page=current_page;
  getContentList(jmData);
  function getContentList(obj){
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/getMediaList.do",
      dataType:"json",
      data:JSON.stringify(obj),
      beforeSend: function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>正在加载节目列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".ri_top3_con").html("");//每次加载之前先清空
          allCount=resultData.AllCount;
          getMediaList(resultData); //加载节目列表
        }else{
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>没有得到节目列表....</div>");//每次加载之前先清空
          allCount="0";
        }
        contentCount=(allCount%pageSize==0)?(allCount/pageSize):(Math.ceil(allCount/pageSize));       
        pagitionInit(contentCount,allCount,jmData.Page);//init翻页
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>加载节目列表发生错误:"+jqXHR.status+"</div>");
        $('.shade', parent.document).hide();
      }
    });
  }
  //加载节目列表
  function getMediaList(resultData){
    for(var i=0;i<resultData.ResultList.length;i++){
      var programBox= '<div class="rtc_listBox" contentSeqId='+resultData.ResultList[i].ContentSeqId+' contentId='+resultData.ResultList[i].ContentId+'>'+
                        '<img src="../anchorResource/img/checkbox1.png" alt="" class="ric_img_check fl checkbox_img checkbox1"/>'+
                        '<div class="rtcl_img">'+
                          '<img src='+resultData.ResultList[i].ContentImg+' alt="节目图片" />'+
                        '</div>'+
                        '<div class="rtcl_con">'+
                          '<h4></h4>'+
                          '<p class="zj_name">'+
                            '<span>专辑名称:</span>'+
                            '<span class="zj_names"></span>'+
                          '</p>'+
                          '<p class="other">'+
                            '<span>时间:</span>'+
                            '<span class="ctime"></span>'+
                          '</p>'+
                        '</div>'+
                        '<p class="jm_st"></p>'+
                        '<div class="op_type" id="op_Box'+i+'">'+
                          '<p class="jm_edit c173">编辑</p>'+
                          '<p class="jm_submit c173">提交</p>'+
                          '<p class="jm_recal c173">撤回</p>'+
                          '<p class="jm_del c173">删除</p>'+
                        '</div>'+
                      '</div>';
      $(".ri_top3_con").append(programBox);
      $(".rtc_listBox").children(".rtcl_con").children("h4").eq(i).text(((resultData.ResultList[i].ContentName)?(resultData.ResultList[i].ContentName):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".zj_name").children(".zj_names").eq(i).text(((resultData.ResultList[i].ContentSeqName)?(resultData.ResultList[i].ContentSeqName):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".other").children(".ctime").eq(i).text(((resultData.ResultList[i].CTime)?(resultData.ResultList[i].CTime):"暂无"));
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
//      var flowflag=resultData.ResultList[i].ContentPubChannels[0].FlowFlag;
//      switch(flowflag){//0全部，1已保存，2发布审核中，3已审核(定时发布)，4已发布，5发布未通过,6待撤回(实际是撤回审核中)，7已撤回，8撤回未通过
//        case "1":case "5":case "7": $("#op_Box"+i).children(".jm_edit,.jm_submit,.jm_del").removeClass("c173").addClass("cf60");
//        break;
//        case "2":case "3":case "6": $("#op_Box"+i).children(".jm_edit,.jm_submit,.jm_del,.jm_recal").removeClass("cf60").addClass("c173");
//        break;
//        case "4":case "8": $("#op_Box"+i).children(".jm_recal").removeClass("c173").addClass("cf60");
//        break;        
//        default:break;
//      }
      }
    }
  }
  /*e--获取节目列表*/
  
  /*s--根据搜索/筛选/翻页获取节目列表*/
  var optfy=1;//optfy=1未选中具体筛选条件前翻页,optfy=2选中具体筛选条件后翻页
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchWord="";
  
  //翻页之后的回调函数
  function pagitionBack(current_page){
    if($(".new_cate li").size()>"0"){//已经选中筛选条件
      optfy=2;//选中具体筛选条件后翻页
    }else{
      optfy=1;//未选中具体筛选条件后翻页
    }
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
    destroy(jmData);
    jmData.MobileClass="Chrome";
    jmData.PCDType="3";
    jmData.UserId=userId;
    jmData.PageSize=pageSize;
    jmData.Page=current_page;
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(optfy==2){//optfy=2选中具体筛选条件后翻页
      $(document).find(".new_cate li").each(function(){
        if($(".new_cate li").size()>="0"){
          var pId=$(this).attr("pid");
          var id=$(this).attr("id");
          if(pId=="channel"){
            jmData.ChannelId=$(this).attr("id");
          }else{
            jmData.SeqMediaId=$(this).attr("id");
          }
        }
      });
      $(".dropdown_menu li").each(function(){
        if($(this).hasClass("selected")){
          var cataId=$(this).attr("catalogId");
          jmData.FlagFlow=cataId;
        }
      });
    }
    if(seaFy==1){//seaFy=1未搜索关键词前翻页
      getContentList(jmData);
    }else{//seaFy=2搜索列表加载出来后翻页
      jmData.SearchWord=searchWord;
      getSearchList(jmData);
    }
  }
  
  /*选中某个节目的状态*/
  $(".dropdown_menu").on("click","li",function(){
    $(this).parent(".dropdown_menu").addClass("dis");
    $(this).parent(".dropdown_menu").siblings(".dropdown").children("img").attr({"src":"../anchorResource/img/filter2.png"});
    $(this).addClass("selected").siblings("li").removeClass("selected");
  });
  
  /*搜索*/
  //键盘上的enter事件
  $(".ri_top_li2_inp").keydown(function(e){
    var evt=event?event:(window.event?window.event:null);//兼容IE和FF
    if (evt.keyCode==13){
      $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
      $(".dropdown_menu .menu0").addClass("selected").siblings().removeClass("selected");
      $("#album,#channel").show();
      searchList();//加载搜索列表  待定
    }
  });
  //点击搜索小图标
  $(".ri_top_li2_img").on("click",function(){
    $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
    $(".dropdown_menu .menu0").addClass("selected").siblings().removeClass("selected");
    $("#album,#channel").show();
    searchList();//加载搜索列表  待定
  });
  function searchList(){
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(searchWord==""){
      alert("请输入搜索关键词");
      $(".ri_top_li2_inp").focus();
    }else{
      destroy(jmData);
      jmData.MobileClass="Chrome";
      jmData.PCDType="3";
      jmData.UserId=userId;
      jmData.FlagFlow=flagflow;
      jmData.PageSize=pageSize;
      current_page=1;
      jmData.Page=current_page;
      jmData.SearchWord=searchWord;
      getSearchList(jmData);
    }
  }
  //得到搜索列表 
  function getSearchList(obj){//暂无本接口
    $.ajax({
      type:"POST",
      url:rootPath+"CM/content/searchContents.do",
      dataType:"json",
      data:JSON.stringify(obj),
      beforeSend: function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>正在加载节目列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".ri_top3_con").html("");//每次加载之前先清空
          allCount=resultData.AllCount;
          getMediaList(resultData);//加载搜索得到的节目列表
        }else{
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>没有得到专辑列表...</div>");
          allCount="0";
        }
        contentCount=(allCount%pageSize==0)?(allCount/pageSize):(Math.ceil(allCount/pageSize));
        pagitionInit(contentCount,allCount,zjData.Page);//init翻页
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:140px;min-height:230px;'>加载专辑列表发生错误:"+jqXHR.status+"</div>");
        $('.shade', parent.document).hide();
      }
    });
  }
  /*e--根据搜索/筛选/翻页获取节目列表*/
  
  /*s--点击上传节目按钮*/
  $(document).on("click",".ri_top_li3",function(){
    clear_jm();//清空数据
    subType=1;//subType=1代表在上传节目页面保存
    pubType=1;//pubType=1代表在上传节目页面提交
    $(".iboxtitle h4").html("上传节目");
  });
  
  /*s--节目弹出页面获取标签*/
  var data1={};//公共标签
  var data2={};//我的标签
  
  //获取节目弹出页面公共标签
  destroy(data1);
  data1.MobileClass="Chrome";
  data1.PCDType="3";
  data1.UserId=userId;
  data1.MediaType="2";
  data1.TagType="1";
  data1.TagSize="20";
  loadTag(data1);
  
  //获取节目弹出页面我的标签
  destroy(data2);
  data2.MobileClass="Chrome";
  data2.PCDType="3";
  data2.UserId=userId;
  data2.MediaType="2";
  data2.TagType="2";
  data2.TagSize="20";
  loadTag(data2);
  
  //点击节目弹出页面“换一换”，更换节目弹出页面公共标签
  $(document).on("click",".add_jm .gg_tag .hyp",function(){
    destroy(data1);
    data1.MobileClass="Chrome";
    data1.PCDType="3";
    data1.UserId=userId;
    data1.MediaType="2";
    data1.TagType="1";
    data1.TagSize="20";
    data1.SeqMediaId=$(".upl_zj option:selected").attr("id");
    loadTag(data1);
  })
  
  //点击节目弹出页面“换一换”，更换节目弹出页面我的标签
  $(document).on("click",".add_jm .my_tag .hyp",function(){
    destroy(data2);
    data2.MobileClass="Chrome";
    data2.PCDType="3";
    data2.UserId=userId;
    data2.MediaType="2";
    data2.TagType="2";
    data2.TagSize="20";
    data2.SeqMediaId=$(".upl_zj option:selected").attr("id");
    loadTag(data2);
  })
  /*e--节目弹出页面获取标签*/
  
  /*e--点击上传节目按钮*/
  
  /*s--点击节目列表相关操作的编辑按钮*/
  $(document).on("click",".jm_edit",function(){
    var contentId=$(this).parents(".rtc_listBox").attr("contentid");
    var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("jmstatusid");
//  if(flowFlag=="2"||flowFlag=="3"||flowFlag=="4"||flowFlag=="6"||flowFlag=="8"){
      //2发布审核中，3已审核(定时发布)，4已发布，6待撤回(实际是撤回审核中)，8撤回未通过
    if(flowFlag=="1"||flowFlag=="2"){//0(已提交),1(审核中),2(已发布),3(未通过),4(已撤回)
      alert("当前状态不支持编辑操作");
      return;
    }else{
      subType=2;//subType=2代表在上传节目页面保存
      pubType=2;//pubType=2代表在上传节目页面提交
      clear_jm();//填充前清空数据
      edit_jm(contentId);//编辑节目时得到节目的详细信息
    }
  });
  //编辑节目时得到节目的详细信息
  function edit_jm(contentId){
    var _data={"MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":userId,
               "ContentId":contentId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/getMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend:function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $("body").css({"overflow":"hidden"});
          getTime();
          fillJmContent(resultData);//填充节目信息
        }else{
          alert("得到节目信息:"+resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("得到节目信息发生错误:"+ jqXHR.status);
        $('.shade', parent.document).hide();
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
        if(resultData.Result.ContentKeyWords[i]){
          var new_tag='<li class="upl_bq_img bqImg" tagId='+resultData.Result.ContentKeyWords[i].TagId+'>'+
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
    }
    if(resultData.Result.ContentMemberTypes!=null){
      for(var i=0;i<resultData.Result.ContentMemberTypes.length;i++){
        if(resultData.Result.ContentMemberTypes[i]){
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
    }
    $(".uplDecn").val(resultData.Result.ContentDesc);
    $(".layer-date").val(resultData.Result.CTime);
  }
  /*e--点击节目列表相关操作的编辑按钮*/

  /*s--点击上传节目/修改节目页面上的保存按钮*/
  $("#submitBtn").on("click",function(){
    if(subType=="1")  save_add_jm();
    if(subType=="2")  save_edit_jm();
  })
  //点击上传节目页面的保存按钮，保存节目
  function save_add_jm(){
    if($(".previewImg").attr("isDefaultImg")=="true"){
      $(".upl_img").attr("value","http://www.wotingfm.com:908/CM/resources/images/default.png");
    }
    var _data={};
    _data.UserId=userId;
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentURI=$(".audio").attr("src");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.SeqMediaId=$(".upl_zj option:selected").attr("id");
    _data.TimeLong=$(".timeLong").attr("value");
    var taglist=[];
    $(".upl_bq").find(".upl_bq_img").each(function(){
      var tag={};//标签对象
      if($(this).attr("tagType")=="我的标签"){
        tag.TagName=$(this).children("span").html();
        tag.TagOrg="我的标签";
      }
      if($(this).attr("tagType")=="公共标签"){
        tag.TagName=$(this).children("span").html();
        tag.TagOrg="公共标签";
      }
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
    _data.FlowFlag="1";
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/addMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend:function(){
        $(".btn_group input").attr("disabled","disabled").css("background","#ccc");
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".mask_jm").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert("新增节目失败:"+resultData.Message);
        }
        $(".btn_group input").removeAttr("disabled").css("background","#ffa634");
      },
      error:function(jqXHR){
        alert("新增节目发生错误:"+ jqXHR.status);
      }
    });
  }
  //点击修改节目页面的保存按钮，保存节目
  function save_edit_jm(){
    if(!$(".upl_img").attr("value")){
      $(".upl_img").attr("value",$(".defaultImg").attr("src"));
    }
    var _data={};
    _data.UserId=userId;
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
        $(".btn_group input").attr("disabled","disabled").css("background","#ccc");
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          $(".mask_jm").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert("修改节目失败:"+resultData.Message);
        }
        $(".btn_group input").removeAttr("disabled").css("background","#ffa634");
      },
      error:function(jqXHR){
        alert("修改节目发生错误:"+ jqXHR.status);
      }
    });
  }
  /*e--点击上传节目/修改节目页面上的保存按钮*/
 
  /*s--点击上传节目/修改节目页面上的提交按钮*/
  $("#pubBtn").on("click",function(){
    if(pubType=="1") pub_add_jm();
    if(pubType=="2") pub_edit_jm();
  })
  //点击上传节目页面上的发布按钮，发布节目
  function pub_add_jm(){
    if($(".previewImg").attr("isDefaultImg")=="true"){
      $(".upl_img").attr("value","http://www.wotingfm.com:908/CM/resources/images/default.png");
    }
    var _data={};
    _data.UserId=userId;
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentURI=$(".audio").attr("src");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
    _data.SeqMediaId=$(".upl_zj option:selected").attr("id");
    _data.TimeLong=$(".timeLong").attr("value");
    var taglist=[];
    $(".upl_bq").find(".upl_bq_img").each(function(){
      var tag={};//标签对象
      if($(this).attr("tagType")=="我的标签"){
        tag.TagName=$(this).children("span").html();
        tag.TagOrg="我的标签";
      }
      if($(this).attr("tagType")=="公共标签"){
        tag.TagName=$(this).children("span").html();
        tag.TagOrg="公共标签";
      }
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
    _data.FlowFlag="2";
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/addMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend:function(){
        $(".btn_group input").attr("disabled","disabled").css("background","#ccc");
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".mask_jm").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert("节目发布失败:"+resultData.Message);
        }
        $(".btn_group input").removeAttr("disabled").css("background","#ffa634");
      },
      error:function(jqXHR){
        alert("节目发布发生错误:"+ jqXHR.status);
      }
    });
  }
  //点击修改节目页面上的发布按钮，发布节目
  function pub_edit_jm(){
    var _data={};
    _data.UserId=userId;
    _data.MobileClass="Chrome";
    _data.PCDType="3";
    _data.ContentId=$(".jmId").val();
    _data.ContentURI=$(".audio").attr("src");
    _data.ContentName=$(".uplTitle").val();
    _data.ContentImg=$(".upl_img").attr("value");
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
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          pubEditJm(_data);
        }
      },
      error:function(jqXHR){
        alert("更改节目信息发生错误："+ jqXHR.status);
      }
    });
  }
  function pubEditJm(_data){
    var contentId=$(".jmId").val();
    var contentSeqId=_data.SeqMediaId;
    var list=[{"ContentId":contentId,"SeqMediaId":contentSeqId}];
    var data4={"MobileClass":"Chrome",
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
        $(".btn_group input").attr("disabled","disabled").css("background","#ccc");
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".mask_jm").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert("节目发布失败:"+resultData.Message);
        }
        $(".btn_group input").removeAttr("disabled").css("background","#ffa634");
      },
      error:function(jqXHR){
        alert("节目发布发生错误:"+jqXHR.status);
      }
    });
  }
  /*e--点击上传节目/修改节目页面上的提交按钮*/
  
  /*s--点击节目列表相关操作的删除按钮*/
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
    var _data={"MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":userId,
               "ContentIds":contentId,
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/removeMedia.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend:function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert("当前操作失败:"+resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("当前操作发生错误:"+ jqXHR.status);
      }
    });
  }
  /*e--点击节目列表相关操作的删除按钮*/
  
  /*s--点击节目列表相关操作的提交按钮*/
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
    var _data={"MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":userId,
               "UpdateList":[{"ContentId":contentId,"SeqMediaId":contentSeqId}],
               "ContentFlowFlag":"2"
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaStatus.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend:function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert("节目发布失败:"+resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("节目发布发生错误:"+ jqXHR.status);
      }
    });
  }
  /*e--点击节目列表相关操作的提交按钮*/
  
  /*s--点击节目列表相关操作的撤回按钮*/
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
    var _data={"MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":userId,
               "UpdateList":[{"ContentId":contentId,"SeqMediaId":seqId}],
               "ContentFlowFlag":"4"
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/updateMediaStatus.do",
      dataType:"json",
      data:JSON.stringify(_data),
      beforeSend:function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert("节目撤回失败:"+resultData.Message);
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("节目撤回发生错误:"+ jqXHR.status);
      }
    });
  }
  /*e--点击节目列表相关操作的撤回按钮*/
  
  /*s---批量提交、撤回、删除节目 */
  //公共的提交、撤回、删除
  var lists=[];//提交、撤回节目
  var delList='';//删除专辑
  $(".rt_opt .opetype").on("click",function(){
    var type=$.trim($(this).attr("type"));
    $(".ri_top3_con .rtc_listBox").each(function(){
      if($(this).children(".ric_img_check").hasClass("checkbox1")){//未选中
        
      }else{//已选中
        var list={};
        list.ContentId=$(this).attr("contentid");
        list.SeqMediaId=$(this).attr("contentseqId");
        list.FlowFlag=$(this).children(".jm_st").attr("jmStatusId");
        lists.push(list);
        if(delList=='') delList=$(this).attr("contentid");
        else delList+=','+$(this).attr("contentid");
      }
    });
    if(lists.length!=0){//选中内容
      var isSure=false;//是否存在不允许操作的节目
      switch(type){
        case "submit"://发布
          for(var i=0;i<lists.length;i++){
//          if(lists[i].FlowFlag=="4"||lists[i].FlowFlag=="8"){//4已发布，8撤回未通过
            if(lists[i].FlowFlag=="1"||lists[i].FlowFlag=="2"||lists[i].FlowFlag=="3"){//0(已提交),1(审核中),2(已发布),3(未通过),4(已撤回)
              alert("你所选择的节目里有不支持提交的节目，请重新选择");//3未通过（撤回未通过和发布未通过）存在争议
              $(".ri_top3_con .rtc_listBox").each(function(){
                $(this).children(".ric_img_check").attr({"src":"../anchorResource/img/checkbox1.png"}).removeClass("checkbox1");
              });
              if(!$(".all_check").hasClass("checkbox1")) $(".all_check").attr({"src":"../anchorResource/img/checkbox1.png"}).removeClass("checkbox1");
              $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
              isSure=true;
              return;
            }else{
              isSure=false;
            }
          }
          if(isSure==false){
            var url="content/media/updateMediaStatus.do";
            var data6={};
            data6.PCDType="3";
            data6.MobileClass="Chrome";
            data6.UserId=userId;
            data6.ContentFlowFlag="2";
            data6.UpdateList=lists;
            commonAjax(data6,url);
            console.log(data6);
          }
        break;
        case "revoke"://撤回
          for(var i=0;i<lists.length;i++){
//          if(lists[i].FlowFlag=="1"||lists[i].FlowFlag=="2"||lists[i].FlowFlag=="3"||lists[i].FlowFlag=="5"||lists[i].FlowFlag=="6"||lists[i].FlowFlag=="7"){
              //1已保存，2发布审核中，3已审核(定时发布)，5发布未通过,6待撤回(实际是撤回审核中)，7已撤回
            if(lists[i].FlowFlag=="0"||lists[i].FlowFlag=="1"||lists[i].FlowFlag=="4"){//0(已提交),1(审核中),2(已发布),3(未通过),4(已撤回)
              alert("你所选择的节目里有不支持撤回的节目，请重新选择");
              $(".ri_top3_con .rtc_listBox").each(function(){
                $(this).children(".ric_img_check").attr({"src":"../anchorResource/img/checkbox1.png"}).removeClass("checkbox1");
              });
              if(!$(".all_check").hasClass("checkbox1")) $(".all_check").attr({"src":"../anchorResource/img/checkbox1.png"}).removeClass("checkbox1");
              $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
              isSure=true;
              return;
            }else{
              isSure=false;
            }
          }
          if(isSure==false){
            var url="content/media/updateMediaStatus.do";
            var data6={};
            data6.PCDType="3";
            data6.MobileClass="Chrome";
            data6.UserId=userId;
            data6.ContentFlowFlag="4";
            data6.UpdateList=lists;
            commonAjax(data6,url);
            console.log(data6);
          }
        break;
        case "delete"://删除
          for(var i=0;i<lists.length;i++){
//          if(lists[i].FlowFlag=="2"||lists[i].FlowFlag=="3"||lists[i].FlowFlag=="4"||lists[i].FlowFlag=="6"||lists[i].FlowFlag=="8"){
              //2发布审核中，3已审核(定时发布)，4已发布，6待撤回(实际是撤回审核中)，8撤回未通过
            if(lists[i].FlowFlag=="1"||lists[i].FlowFlag=="2"||lists[i].FlowFlag=="3"){//0(已提交),1(审核中),2(已发布),3(未通过),4(已撤回)
              alert("你所选择的节目里有不支持删除的节目，请重新选择");
              $(".ri_top3_con .rtc_listBox").each(function(){
                $(this).children(".ric_img_check").attr({"src":"../anchorResource/img/checkbox1.png"}).removeClass("checkbox1");
              });
              if(!$(".all_check").hasClass("checkbox1")) $(".all_check").attr({"src":"../anchorResource/img/checkbox1.png"}).removeClass("checkbox1");
              $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
              isSure=true;
              return;
            }else{
              isSure=false;
            }
          }
          if(isSure==false){
            var url="content/media/removeMedia.do";
            var data6={};
            data6.PCDType="3";
            data6.MobileClass="Chrome";
            data6.UserId=userId;
            data6.ContentIds=delList;
            commonAjax(data6,url);
            console.log(data6);
          }
        break;
        default:break;
      }
    }
  });
  //公共的提交、撤回、删除
  function commonAjax(data,url){
    $.ajax({
      type:"POST",
      url:rootPath+url,
      dataType:"json",
      data:JSON.stringify(data),
      beforeSend:function(){
        $(".shade",parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
          $(".opetype").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
          $(".all_check").addClass("checkbox1").attr({"src":"../anchorResource/img/checkbox1.png"});
        }else{
          alert("当前操作失败:"+resultData.Message);
        }
        $(".shade",parent.document).hide();
      },
      error:function(jqXHR){
        alert("当前操作发生错误:"+ jqXHR.status);
      }
    });
  }
  /*e---批量提交、撤回、删除节目 */
  
  //点击节目的封面图片，跳到这个节目的详情页
  $(document).on("click",".rtcl_img",function(){
    var contentId=$(this).parent(".rtc_listBox").attr("contentId");
    $("#myIframe", parent.document).attr({"src":"jmANDzj/jm_detail.html?contentId="+contentId});
  });
  
  /*根据不同的筛选条件得到不同的节目列表*/
  $(document).on("click",".trig_item,.trig_item_li",function(){//选中某个筛选条件
    optfy=2;////选中具体筛选条件后翻页
    destroy(jmData);
    jmData.MobileClass="Chrome";
    jmData.PCDType="3";
    jmData.UserId=userId;
    jmData.PageSize=pageSize;
    jmData.Page=current_page;
    if($(".new_cate li").size()>"0"){
      $(document).find(".new_cate li").each(function(){
        var pId=$(this).attr("pid");
        var id=$(this).attr("id");
        if(pId=="album") jmData.SeqMediaId=$(this).attr("id");
        else jmData.ChannelId=$(this).attr("id");
      });
    }
    $(".dropdown_menu li").each(function(){
      if($(this).hasClass("selected")){
        var cataId=$(this).attr("catalogId");
        jmData.FlowFlag=cataId;
      }
    });
    getContentList(jmData);
  });
  
  /*根据不同的筛选条件得到不同的节目列表*/
  $(document).on("click",".cate_img",function(){//取消某个筛选条件
    optfy=1;//未选中具体筛选条件后翻页
    destroy(jmData);
    jmData.MobileClass="Chrome";
    jmData.PCDType="3";
    jmData.UserId=userId;
    jmData.PageSize=pageSize;
    jmData.Page=current_page;
    if($(".new_cate li").size()>"0"){
      $(document).find(".new_cate li").each(function(){
        var pId=$(this).attr("pid");
        var id=$(this).attr("id");
        if(pId=="album") jmData.SeqMediaId=$(this).attr("id");
        else jmData.ChannelId=$(this).attr("id");
      });
    }
    $(".dropdown_menu li").each(function(){
      if($(this).hasClass("selected")){
        var cataId=$(this).attr("catalogId");
        jmData.FlowFlag=cataId;
      }
    });
    getContentList(jmData);
  });
  
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
  
});

