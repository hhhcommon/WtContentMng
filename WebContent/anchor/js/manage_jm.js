$(function(){
  var rootPath=getRootPath();
  var subType=1;//subType=1代表在上传节目页面保存,subType=2代表在修改节目页面保存
  var pubType=1;//pubType=1代表在上传节目页面提交,pubType=2代表在修改节目页面提交
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  
  /*s--获取筛选条件*/
  var dataF={ "DeviceId":"3279A27149B24719991812E6ADBA5584",
              "MobileClass":"Chrome",
              "UserId":"123",
              "PCDType":"3",
              "MediaType":"MediaAsset"
  };
  getFiltrates(dataF);
  function getFiltrates(data){
    $.ajax({
      type:"POST",
      url:rootPath+"content/getFiltrates.do",
      dataType:"json",
      data:JSON.stringify(data),
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          getChannelLabel(resultData);//得到栏目的筛选标签
          getAlbumLabel(resultData);//得到专辑的筛选标签
        }else{
          alert("筛选条件加载失败,请刷新页面重新加载！");
        }
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
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
  /*e--获取栏目筛选条件*/
 
  /*s--获取节目列表*/
  var jmData={};
  jmData.DeviceId="3279A27149B24719991812E6ADBA5584";
  jmData.MobileClass="Chrome";
  jmData.PCDType="3";
  jmData.UserId="123";
  jmData.FlowFlag="0";
  jmData.PageSize="10";
  jmData.Page=current_page;
  getContentList(jmData);
  function getContentList(obj){
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/getMediaList.do",
      dataType:"json",
      data:JSON.stringify(obj),
      beforeSend: function(){
        $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载节目列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          $(".ri_top3_con").html("");//每次加载之前先清空
          getMediaList(resultData); //加载节目列表
        }else{
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>没有内容....</div>");//每次加载之前先清空
        }
        $('.shade', parent.document).hide();
      },
      error:function(XHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  //加载节目列表
  function getMediaList(resultData){
    for(var i=0;i<resultData.ResultList.AllCount;i++){
      var programBox= '<div class="rtc_listBox" contentSeqId='+resultData.ResultList.List[i].ContentSeqId+' contentId='+resultData.ResultList.List[i].ContentId+' channelId='+channelds+'>'+
                        '<img src="img/checkbox1.png" alt="" class="ric_img_check fl checkbox_img checkbox1"/>'+
                        '<div class="rtcl_img">'+
                          '<img src='+resultData.ResultList.List[i].ContentImg+' alt="节目图片" />'+
                        '</div>'+
                        '<div class="rtcl_con">'+
                          '<h4></h4>'+
                          '<p class="zj_name"></p>'+
                          '<p class="other">'+
                            '<span>时间 ：</span>'+
                            '<span class="ctime"></span>'+
                          '</p>'+
                        '</div>'+
                        '<p class="jm_st" flowFlag='+resultData.ResultList.List[i].ContentPubChannels[0].FlowFlag+'></p>'+
                        '<div class="op_type" id="op_Box'+i+'">'+
                          '<p class="jm_edit c173">编辑</p>'+
                          '<p class="jm_submit c173">提交</p>'+
                          '<p class="jm_recal c173">撤回</p>'+
                          '<p class="jm_del c173">删除</p>'+
                        '</div>'+
                      '</div>';
      $(".ri_top3_con").append(programBox);
      $(".rtc_listBox").children(".rtcl_con").children("h4").eq(i).text(((resultData.ResultList.List[i].ContentName)?(resultData.ResultList.List[i].ContentName):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".zj_name").eq(i).text(((resultData.ResultList.List[i].ContentSeqName)?(resultData.ResultList.List[i].ContentSeqName):"暂无"));
      $(".rtc_listBox").children(".rtcl_con").children(".other").children(".ctime").eq(i).text(((resultData.ResultList.List[i].CTime)?(resultData.ResultList.List[i].CTime):"暂无"));
      $(".rtc_listBox").children(".jm_st").eq(i).text(((resultData.ResultList.List[i].ContentPubChannels[0].FlowFlagState)?(resultData.ResultList.List[i].ContentPubChannels[0].FlowFlagState):"未知"));
      if(resultData.ResultList.List[i].ContentPubChannels){
        var channelds="";
        for(var j=0;j<resultData.ResultList.List[i].ContentPubChannels.length;j++){
          if(channelds==""){
            channelds=resultData.ResultList.List[i].ContentPubChannels[j].ChannelId;
          }else{
            channelds+=","+resultData.ResultList.List[i].ContentPubChannels[j].ChannelId;
          }
        }
      }
      var flowflag=resultData.ResultList.List[i].ContentPubChannels[0].FlowFlag;
      switch(flowflag){//0全部，1已保存，2发布审核中，3已审核(定时发布)，4已发布，5发布未通过,6待撤回(实际是撤回审核中)，7已撤回，8撤回未通过
        case "1":case "5":case "7": $("#op_Box"+i).children(".jm_edit,.jm_submit,.jm_del").removeClass("c173").addClass("cf60");
        break;
        case "2":case "3":case "6": $("#op_Box"+i).children(".jm_edit,.jm_submit,.jm_del,.jm_recal").removeClass("cf60").addClass("c173");
        break;
        case "4":case "8": $("#op_Box"+i).children(".jm_recal").removeClass("c173").addClass("cf60");
        break;        
        default:break;
      }
    }
  }
  /*e--获取节目列表*/
  
  /*s--根据搜索/筛选/翻页获取节目列表*/
  var optfy=1;//optfy=1未选中具体筛选条件前翻页,optfy=2选中具体筛选条件后翻页
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchWord="";
  //翻页
  $(".pagination span").on("click",function(){
    var data_action=$(this).attr("data_action");
    searchWord=$.trim($(".ri_top_li2_inp").val());
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
  //点击键盘上的enter也可以进行翻页
  $(".toPage").keydown(function(e){
    var evt=event?event:(window.event?window.event:null);//兼容IE和FF
    if (evt.keyCode==13){
      //跳至进行输入合理数字范围检测
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
    destroy(jmData);
    jmData.DeviceId="3279A27149B24719991812E6ADBA5584";
    jmData.MobileClass="Chrome";
    jmData.PCDType="3";
    jmData.UserId="123";
    jmData.PageSize="10";
    jmData.Page=current_page;
    $(".currentPage").text(current_page);
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(optfy==2){//optfy=2选中具体筛选条件后翻页
      $(document).find(".new_cate li").each(function(){//待定
        if($(".new_cate li").size()>="0"){
          var pId=$(this).attr("pid");
          var id=$(this).attr("id");
          if(pId=="channel"){
            jmData.ChannelId=$(this).attr("id");
          }else{
            jmData.SourceId=$(this).attr("id");
          }
        }
      });
      $(".dropdown_menu li").each(function(){//待定
        if($(this).hasClass("selected")){
          var cataId=$(this).attr("catalogId");
          jmData.FlowFlag=cataId;
        }
      });
    }
    if(seaFy==1){//seaFy=1未搜索关键词前翻页
      getContentList(jmData);//待定
    }else{//seaFy=2搜索列表加载出来后翻页
      jmData.SearchWord=searchWord;
      getSearchList(jmData);//待定
    }
  }
  
  /*选中某个节目的状态*/
  $(".dropdown_menu").on("click","li",function(){
    $(this).parent(".dropdown_menu").addClass("dis");
    $(this).parent(".dropdown_menu").siblings(".dropdown").children("img").attr({"src":"img/filter2.png"});
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
  function searchList(){//待定
    searchWord=$.trim($(".ri_top_li2_inp").val());
    if(searchWord==""){
      alert("请输入搜索内容");
      $(".ri_top_li2_inp").focus();
    }else{
      destroy(jmData);
      jmData.DeviceId="3279A27149B24719991812E6ADBA5584";
      jmData.MobileClass="Chrome";
      jmData.PCDType="3";
      jmData.UserId="123";
      jmData.FlowFlag="0";
      jmData.PageSize="10";
      jmData.Page=current_page;
      $(".currentPage").text(current_page);
      jmData.SearchWord=searchWord;
    }
    getSearchList(jmData);  
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
          contentCount=resultData.ResultInfo.Count;
          contentCount=(contentCount%10==0)?(contentCount/10):(Math.ceil(contentCount/10));
          $(".totalPage").text(contentCount);
          getMediaList(resultData);//加载搜索得到的节目列表   待定
        }else{
          $(".totalPage").text("0");
          $(".page").find("span").addClass("disabled");
          $(".ri_top3_con").html("<div style='font-size:16px;text-align:center;line-height:40px;'>没有找到节目...</div>");
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  /*e--根据搜索/筛选/翻页获取节目列表*/
 
  /*s--点击上传节目按钮*/
  $(document).on("click",".ri_top_li3",function(){
    clear_jm();//清空数据
    subType=1;
    pubType=1;
    $(".iboxtitle h4").html("上传节目");
  });
  /*e--点击上传节目按钮*/
  
  /*s--点击页面上相关操作的编辑按钮*/
  $(document).on("click",".jm_edit",function(){
    var contentId=$(this).parents(".rtc_listBox").attr("contentid");
    var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("flowFlag");
    if(flowFlag=="2"||flowFlag=="3"||flowFlag=="4"||flowFlag=="6"||flowFlag=="8") {
      //2发布审核中，3已审核(定时发布)，4已发布，6待撤回(实际是撤回审核中)，8撤回未通过
      alert("当前状态不支持编辑操作");
      return;
    }else{
      subType=2;
      pubType=2;
      edit_jm(contentId);//编辑节目时得到节目的详细信息
    }
  });
  //编辑节目时得到节目的详细信息
  function edit_jm(contentId){
    var _data={"DeviceId":"3279A27149B24719991812E6ADBA5584",
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":"123",
               "ContentId":contentId
    };
    $.ajax({
      type:"POST",
      url:rootPath+"content/media/getMediaInfo.do",
      dataType:"json",
      data:JSON.stringify(_data),
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
                      '<img class="upl_bq_cancelimg1 cancelImg" src="img/upl_img2.png" alt="" />'+
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
                        '<img class="cancelImg" src="img/upl_img2.png" alt="" />'+
                      '</li>';
        $(".czfs_tag").append(new_czfs); 
      }
    }
    $(".layer-date").val(resultData.Result.CTime);
  }
  /*e--点击页面上相关操作的编辑按钮*/

  /*s--点击上传节目/修改节目页面上的保存按钮*/
  $("#submitBtn").on("click",function(){
    if(subType=="1")  save_add_jm();
    if(subType=="2")  save_edit_jm();
  })
  //点击上传节目页面的保存按钮，保存节目
  function save_add_jm(){
    if($(".previewImg").attr("isDefaultImg")=="true"){
      $(".upl_img").attr("value","http://wotingfm.com:908/CM/resources/images/default.png");
    }
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
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
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("新建节目保存成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
  //点击修改节目页面的保存按钮，保存节目
  function save_edit_jm(){
    if(!$(".upl_img").attr("value")){
      $(".upl_img").attr("value",$(".defaultImg").attr("src"));
    }
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
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
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
  /*e--点击上传节目/修改节目页面上的保存按钮*/
 
  /*s--点击上传节目/修改节目页面上的提交按钮*/
  $("#pubBtn").on("click",function(){
    if(pubType=="1") pub_add_jm();
    if(pubType=="2") pub_edit_jm();
  })
  //点击上传节目页面上的发布按钮，发布节目
  function pub_add_jm(){
    if($(".previewImg").attr("isDefaultImg")=="true"){
      $(".upl_img").attr("value","http://wotingfm.com:908/CM/resources/images/default.png");
    }
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
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
      beforeSend: function(){
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("节目发布成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
  //点击修改节目页面上的发布按钮，发布节目
  function pub_edit_jm(){
    var _data={};
    _data.UserId="123";
    _data.DeviceId="3279A27149B24719991812E6ADBA5584";
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
        if(resultData.ReturnType == "1001"){
          pubEditJm(_data);
        }else{
          alert(resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  function pubEditJm(_data){
    var contentId=$(".jmId").val();
    var contentSeqId=_data.SeqMediaId;
    var data4={"DeviceId":"3279A27149B24719991812E6ADBA5584",
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":"123",
               "ContentId":contentId,
               "SeqMediaId":contentSeqId
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
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
  /*e--点击上传节目/修改节目页面上的提交按钮*/
  
  /*s--点击页面上相关操作的删除按钮*/
  $(document).on("click",".jm_del",function(){
    var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("flowFlag");
    if(flowFlag=="0"||flowFlag=="4") {//0提交4撤回
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
    var _data={"DeviceId":"3279A27149B24719991812E6ADBA5584",
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":"123",
               "ContentId":contentId,
               "SeqMediaId":contentSeqId
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
        if(resultData.ReturnType == "1001"){
          alert("节目删除成功");
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
  /*e--点击页面上相关操作的删除按钮*/
  
  /*s--点击页面上相关操作的提交按钮*/
  $(document).on("click",".jm_submit",function(){
     var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("flowFlag");
     if(flowFlag=="1"||flowFlag=="2") {//1审核2发布
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
    var _data={"DeviceId":"3279A27149B24719991812E6ADBA5584",
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":"123",
               "ContentId":contentId,
               "SeqMediaId":contentSeqId,
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
        if(resultData.ReturnType == "1001"){
          alert("节目发布成功");
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
  /*e--点击页面上相关操作的提交按钮*/
  
  /*s--点击页面上相关操作的撤回按钮*/
  $(document).on("click",".jm_recal",function(){
    var flowFlag=$(this).parent(".op_type").siblings(".jm_st").attr("flowFlag");
    if(flowFlag=="2"||flowFlag=="0"){//0提交1审核2发布4撤回
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
    var _data={"DeviceId":"3279A27149B24719991812E6ADBA5584",
               "MobileClass":"Chrome",
               "PCDType":"3",
               "UserId":"123",
               "ContentId":contentId,
               "SeqMediaId":seqId,
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
        if(resultData.ReturnType == "1001"){
          alert("撤回节目请求已发送");
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
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
  /*e--点击页面上相关操作的撤回按钮*/
  
  /*s---批量提交、撤回、删除节目 */
  //公共的提交、撤回、删除
  var lists=[];
  $(".rt_opt .opetype").on("click",function(){
    var type=$.trim($(this).attr("type"));
    lists=[];
    $(".ri_top3_con .rtc_listBox").each(function(){
      if($(this).children(".ric_img_check").hasClass("checkbox1")){//未选中
        
      }else{//已选中
        var list={};
        list.ContentId=$(this).attr("contentid");
        list.SeqMediaId=$(this).attr("contentseqId");
        list.FlowFlag=$(this).children(".jm_st").attr("flowflag");
        lists.push(list);
      }
    });
    console.log(lists);
    if(lists.length==0){//未选中内容
      alert("请先选中内容再进行操作");
      return;
    }else{
      var isSure=false;//是否存在
      switch(type){
        case "submit":
          for(var i=0;i<lists.length;i++){
            if(lists[i].FlowFlag=="4"||lists[i].FlowFlag=="8"){//4已发布，8撤回未通过
              alert("你所选择的节目里有不支持提交的节目，请重新选择");
              $(".ri_top3_con .rtc_listBox").each(function(){
                $(this).children(".ric_img_check").attr({"src":"img/checkbox1.png"}).removeClass("checkbox1");
              });
              if(!$(".all_check").hasClass("checkbox1")) $(".all_check").attr({"src":"img/checkbox1.png"}).removeClass("checkbox1");
              isSure=true;
              return;
            }else{
              isSure=false;
            }
          }
          if(isSure==false){
            var url="content/media/updateMediaStatus.do";
            var data6={};
            data6.DeviceId="3279A27149B24719991812E6ADBA5584";
            data6.PCDType="3";
            data6.MobileClass="Chrome";
            data6.UserId="123";
            data6.ContentFlowFlag="2";
            data6.ContentList=lists;//待定
            console.log(data6);
            commonAjax(data6,url);
          }
        break;
        case "revoke":
          for(var i=0;i<lists.length;i++){
            if(lists[i].FlowFlag=="1"||lists[i].FlowFlag=="2"||lists[i].FlowFlag=="3"||lists[i].FlowFlag=="5"||lists[i].FlowFlag=="6"||lists[i].FlowFlag=="7"){
              //1已保存，2发布审核中，3已审核(定时发布)，5发布未通过,6待撤回(实际是撤回审核中)，7已撤回
              alert("你所选择的节目里有不支持撤回的节目，请重新选择");
              $(".ri_top3_con .rtc_listBox").each(function(){
                $(this).children(".ric_img_check").attr({"src":"img/checkbox1.png"}).removeClass("checkbox1");
              });
              if(!$(".all_check").hasClass("checkbox1")) $(".all_check").attr({"src":"img/checkbox1.png"}).removeClass("checkbox1");
              isSure=true;
              return;
            }else{
              isSure=false;
            }
          }
          if(isSure==false){
            var url="content/media/updateMediaStatus.do";
            var data6={};
            data6.DeviceId="3279A27149B24719991812E6ADBA5584";
            data6.PCDType="3";
            data6.MobileClass="Chrome";
            data6.UserId="123";
            data6.ContentFlowFlag="4";
            data6.ContentList=lists;//待定
            console.log(data6);
            commonAjax(data6,url);
          }
        break;
        case "delete":
          for(var i=0;i<lists.length;i++){
            if(lists[i].FlowFlag=="2"||lists[i].FlowFlag=="3"||lists[i].FlowFlag=="4"||lists[i].FlowFlag=="6"||lists[i].FlowFlag=="8"){
              //2发布审核中，3已审核(定时发布)，4已发布，6待撤回(实际是撤回审核中)，8撤回未通过
              alert("你所选择的节目里有不支持删除的节目，请重新选择");
              $(".ri_top3_con .rtc_listBox").each(function(){
                $(this).children(".ric_img_check").attr({"src":"img/checkbox1.png"}).removeClass("checkbox1");
              });
              if(!$(".all_check").hasClass("checkbox1")) $(".all_check").attr({"src":"img/checkbox1.png"}).removeClass("checkbox1");
              isSure=true;
              return;
            }else{
              isSure=false;
            }
          }
          if(isSure==false){
            var url="content/media/removeMedia.do";
            var data6={};
            data6.DeviceId="3279A27149B24719991812E6ADBA5584";
            data6.PCDType="3";
            data6.MobileClass="Chrome";
            data6.UserId="123";
            data6.ContentList=lists;//待定
            console.log(data6);
            commonAjax(data6,url);
          }
        break;
        default:break;
      }
    }
  });
  
  function commonAjax(data,url){
    $.ajax({
      type:"POST",
      url:rootPath+url,
      dataType:"json",
      data:JSON.stringify(data),
      beforeSend:function(){
        $(".common_mask").show();
      },
      success:function(resultData){
        if(resultData.ReturnType == "1001"){
          alert("操作成功");
          $(".mask,.add").hide();
          $("body").css({"overflow":"auto"});
          getContentList(jmData);//重新加载节目列表
          $("#album .attrValues .av_ul,#channel .attrValues .av_ul").html("");
          $("#channel .chnels").remove();
          getFiltrates(dataF);//重新加载筛选条件
        }else{
          alert(resultData.Message);
        }
        $(".common_mask").hide();
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  /*e---批量提交、撤回、删除节目 */
  
  //点击节目的封面图片，跳到这个节目的详情页
  $(document).on("click",".rtcl_img",function(){
    var contentId=$(this).parent(".rtc_listBox").attr("contentId");
    $("#newIframe", parent.document).attr({"src":"jm_detail.html?contentId="+contentId});
    $("#myIframe", parent.document).hide();
    $("#newIframe", parent.document).show();
  });
  
  /*根据不同的筛选条件得到不同的节目列表*/
  $(document).on("click",".trig_item,.trig_item_li,.cate_img",function(){//选中或取消某个筛选条件
    destroy(jmData);
    jmData.DeviceId="3279A27149B24719991812E6ADBA5584";
    jmData.MobileClass="Chrome";
    jmData.PCDType="3";
    jmData.UserId="123";
    jmData.PageSize="10";
    jmData.Page=current_page;
    if($(".new_cate li").size()>"0"){
      $(document).find(".new_cate li").each(function(){
        var pId=$(this).attr("pid");
        var id=$(this).attr("id");
        if(pId=="album"){
          jmData.SeqMediaId=$(this).attr("id");
        }else{
          jmData.ChannelId=$(this).attr("id");
        }
      });
    }
    $(".dropdown_menu li").each(function(){//待定
      if($(this).hasClass("selected")){
        var cataId=$(this).attr("catalogId");
        jmData.FlowFlag=cataId;
      }
    });
    getContentList(jmData);
  });
});
