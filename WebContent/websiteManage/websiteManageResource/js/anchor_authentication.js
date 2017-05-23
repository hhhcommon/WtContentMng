$(function(){
  var rootPath=getRootPath();
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var pageSize=10;//每页显示的记录数量
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchWord="";//搜索关键词
  var anchorFy=1;//anchorFy=1未选中申请专业或者资格主播翻页,anchorFy=2选中申请专业或者资格主播后翻页
  var userId='0579efbaf9a9';//W003
  
  
  /*放大或缩小身份证照片*/
  $(document).on("click",".enlarge_div",function(){
    var index=$(".enlarge_div").index(this);
    var _this=$(".rtc_listBoxLists .rtc_listBoxList .rtc_listBox4 .rtc_listBox40 .rtc_listBox41");
    if($(this).hasClass("large")){//此时已处于放大状态
      $(_this).each(function(i){
        if(index==i){
          $(this).children(".enlarge_div").removeClass("large").children(".enlarge_img").attr("src","../websiteManageResource/img/enlarge.png");
          $(this).removeClass("border_img").children(".enlarge_div").removeClass("enlarge_br");
          $(this).children(".rtc_listBox42").removeClass("border_img1");
          $(this).parent(".rtc_listBox40").parent(".rtc_listBox4").children(".big").addClass("dis").attr("src","");
          return false;
        }
      });
    }else{//此时处于未放大状态
      $(_this).children(".enlarge_div").removeClass("large").children(".enlarge_img").attr("src","../websiteManageResource/img/enlarge.png");
      $(_this).removeClass("border_img").children(".enlarge_div").removeClass("enlarge_br");
      $(_this).children(".rtc_listBox42").removeClass("border_img1");
      $(_this).each(function(i){
        if(index==i){
          var l0=$(_this).length;
          var l2=$(this).width();
          var ll=i*l2;
          if(ll>=320) ll=320;
          var _src=$(_this).children(".rtc_listBox42").attr("src");
          $(this).children(".enlarge_div").addClass("large").children(".enlarge_img").attr("src","../websiteManageResource/img/narrow.png");
          $(this).addClass("border_img").children(".enlarge_div").addClass("enlarge_br");
          $(this).children(".rtc_listBox42").addClass("border_img1");
          $(this).parent(".rtc_listBox40").parent(".rtc_listBox4").children(".big").attr("src",_src).removeClass("dis").css("margin-left",ll+"px");
          return false;
        }
      });
    }
  });
  
  /*主播禁言状态的下拉菜单的切换*/
  $(".dropdown").on("click",function(){
    $(this).addClass("selected").siblings().removeClass("selected");
    if($(this).siblings(".dropdown_menu").hasClass("dis")){
      $(this).children("img").attr({"src":"../websiteManageResource/img/filter1.png"});
      $(this).siblings(".dropdown_menu").removeClass("dis");
    }else{
      $(this).children("img").attr({"src":"../websiteManageResource/img/filter2.png"});
      $(this).siblings(".dropdown_menu").addClass("dis");
    }
  });
  
  /*获取主播认证列表*/
  var data1={};
  data1.UserId=userId;
  data1.PCDType="3";
  data1.Flag="0";//0默认全部,1实名认证,2资格认证
  data1.Page=current_page;
  data1.PageSize=pageSize;
  getAuthenticationList(data1);
  function getAuthenticationList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/security/getApproves.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(dataParam),
      beforeSend:function(){
        $(".rtc_listBoxLists").html("<div class='labels'>正在加载主播认证列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          allCount=resultData.AllCount;
          loadAuthenticationList(resultData);//加载主播认证列表
        }else{
          allCount="0";
          $(".rtc_listBoxLists").html("<div class='labels'>得到主播认证列表失败:"+resultData.Message+"</div>");
        }
        contentCount=(allCount%pageSize==0)?(allCount/pageSize):(Math.ceil(allCount/pageSize));
        pagitionInit(contentCount,allCount,dataParam.Page);//初始化翻页插件
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        $(".rtc_listBoxLists").html("<div class='labels'>加载主播认证列表发生错误:"+jqXHR.status+"</div>");
        $('.shade', parent.document).hide();
      }
    });
  }
  //加载主播认证列表
  function loadAuthenticationList(resultData){
    $(".rtc_listBoxLists").html("");//清空
    for(var i=0;i<resultData.ResultList.length;i++){
      var list='<div class="rtc_listBoxList">'+
                '<ul class="rtc_listBox fl">'+
                  '<li class="rtc_listBox1 fl ellipsis">'+
                    '<div class="rtc_listBox11 fl">主播账号 : </div>'+
                    '<div class="rtc_listBox12 fl"></div>'+
                  '</li>'+
                  '<li class="rtc_listBox2 fl ellipsis">'+
                    '<div class="rtc_listBox21 fl">真实姓名 : </div>'+
                    '<div class="rtc_listBox22 fl"></div>'+
                  '</li>'+
                  '<li class="rtc_listBox3 fl ellipsis">'+
                    '<div class="rtc_listBox31 fl">身份证号 : </div>'+
                    '<div class="rtc_listBox32 fl"></div>'+
                  '</li>'+
                '</ul>'+
                '<div class="rtc_listBox4 fl">'+
                  '<ul class="rtc_listBox40"></ul>'+
                  '<img src="" alt="" class="big dis" />'+
                '</div>'+
                '<div class="rtc_listBox47 fl"></div>'+
                '<div class="rtc_listBox43 fl">'+
                  '<div class="rtc_listBox44">查验身份</div>'+
                  '<div class="rtc_listBox45">通过</div>'+
                  '<div class="rtc_listBox46">不通过</div>'+
                '</div>'+
              '</div>';
      $(".rtc_listBoxLists").append(list);
      $(".rtc_listBox12").eq(i).text((resultData.ResultList[i].UserId)?(resultData.ResultList[i].UserId):("xxxxx"));
      $(".rtc_listBox22").eq(i).text((resultData.ResultList[i].reallyName)?(resultData.ResultList[i].reallyName):("保密"));
      $(".rtc_listBox32").eq(i).text((resultData.ResultList[i].IDCard)?(resultData.ResultList[i].IDCard):("XXXXXXXXXXXXXXX"));
      if(resultData.ResultList[i].FrontImg){//身份证正面照片
        var img='<li class="rtc_listBox41 fl">'+
                  '<img src='+resultData.ResultList[i].FrontImg+' alt="" class="rtc_listBox42"/>'+
                  '<div class="enlarge_div">'+
                    '<img src="../websiteManageResource/img/enlarge.png" alt="" class="enlarge_img"/>'+
                  '</div>'+
                '</li>';
        $(".rtc_listBox40").eq(i).append(img);
      }
      if(resultData.ResultList[i].ReverseImg){//身份证正面照片
        var img='<li class="rtc_listBox41 fl">'+
                  '<img src='+resultData.ResultList[i].ReverseImg+' alt="" class="rtc_listBox42"/>'+
                  '<div class="enlarge_div">'+
                    '<img src="../websiteManageResource/img/enlarge.png" alt="" class="enlarge_img"/>'+
                  '</div>'+
                '</li>';
        $(".rtc_listBox40").eq(i).append(img);
      }
      if(resultData.ResultList[i].MixImg){//身份证正面照片
        var img='<li class="rtc_listBox41 fl">'+
                  '<img src='+resultData.ResultList[i].MixImg+' alt="" class="rtc_listBox42"/>'+
                  '<div class="enlarge_div">'+
                    '<img src="../websiteManageResource/img/enlarge.png" alt="" class="enlarge_img"/>'+
                  '</div>'+
                '</li>';
        $(".rtc_listBox40").eq(i).append(img);
      }
      if(resultData.ResultList[i].AnchorCardImg){//身份证正面照片
        var img='<li class="rtc_listBox41 fl">'+
                  '<img src='+resultData.ResultList[i].AnchorCardImg+' alt="" class="rtc_listBox42"/>'+
                  '<div class="enlarge_div">'+
                    '<img src="../websiteManageResource/img/enlarge.png" alt="" class="enlarge_img"/>'+
                  '</div>'+
                '</li>';
        $(".rtc_listBox40").eq(i).append(img);
      }
      var imgLength=$(".rtc_listBox40").eq(i).children(".rtc_listBox41").length;
      if(imgLength==1&&resultData.ResultList[i].AnchorCardImg){//只有一张主播专业认证图片
        $(".rtc_listBox43").eq(i).css("margin","15px 0px 0px 0px").children(".rtc_listBox44").removeClass("dis");
        $(".rtc_listBox47").eq(i).text("资格认证");
      }else if(imgLength==4){
        $(".rtc_listBox43").eq(i).css("margin","30px 0px 0px 0px").children(".rtc_listBox44").addClass("dis");
        $(".rtc_listBox47").eq(i).text("资格认证");
      }else{
        $(".rtc_listBox43").eq(i).css("margin","30px 0px 0px 0px").children(".rtc_listBox44").addClass("dis");
        $(".rtc_listBox47").eq(i).text("专业认证");
      }
    }
  }
  
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
    $(".dropdown_menu li").each(function(){
      if($(this).hasClass("selected")&&$(this).attr("flag")!="0"){
        anchorFy=2;
        return false;
      }else{//anchorFy=1未选中申请专业或者资格主播翻页,anchorFy=2选中申请专业或者资格主播后翻页
        anchorFy=1;
      }
    });
    opts(seaFy,anchorFy,current_page);
  }
  
  //判断在点击翻页之前是否进行了搜索查询
  function opts(seaFy,anchorFy,current_page){
    var data1={};
    data1.UserId=userId;
    data1.PCDType="3";
    data1.Page=current_page;
    data1.PageSize=pageSize;
    if(seaFy==2){//seaFy=2搜索列表加载出来后翻页
      data1.SearchWord=$.trim($(".ri_top_li2_inp").val());
    }
    if(anchorFy==2){//anchorFy=1未选中申请专业或者资格主播翻页,anchorFy=2选中申请专业或者资格主播后翻页
      $(".dropdown_menu li").each(function(){
        if($(this).hasClass("selected")){
          var flag=$(this).attr("flag");//0默认全部,1实名认证,2资格认证
          data1.Flag=flag;
          return;
        }
      });
    }
    getAuthenticationList(data1);  
  }
  
  /*选中不同的认证状态--进行筛选*/
  $(".dropdown_menu").on("click","li",function(){
    var data1={};
    data1.UserId=userId;
    data1.PCDType="3";
    data1.PageSize=pageSize;
    current_page="1";
    data1.Page=current_page;
    data1.Flag=$(this).attr("flag");
    if($.trim($(".ri_top_li2_inp").val())){
      data1.SearchWord=$.trim($(".ri_top_li2_inp").val());
      seaFy=2;
    }
    anchorFy=2;//anchorFy=1未选中申请专业或者资格主播翻页,anchorFy=2选中申请专业或者资格主播后翻页
    getAuthenticationList(data1);
    $(this).parent(".dropdown_menu").addClass("dis");
    $(this).parent(".dropdown_menu").siblings(".dropdown").children("img").attr({"src":"../websiteManageResource/img/filter2.png"});
    $(this).addClass("selected").siblings("li").removeClass("selected");
  });
  
  
  
  
  
  
  /*点击通过*/
  $(document).on("click",".rtc_listBox45",function(){
    var data2={};
    data2.UserId=userId;
    data2.PageSize=pageSize;
    data2.Page=current_page;
    data2.SourceId=sourceId;
    data2.UserId=userId;
    data2.PageSize=pageSize;
    data2.Page=current_page;
    data2.SourceId=sourceId;
  });
  
  /*点击不通过*/
  $(document).on("click",".rtc_listBox46",function(){
    
  });
  
  /*点击查验身份*/
  $(document).on("click",".rtc_listBox44",function(){
    
  });
  
})
