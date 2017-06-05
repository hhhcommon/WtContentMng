$(function(){
  var rootPath=getRootPath();
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var pageSize=2;//每页显示的记录数量
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchWord="";//搜索关键词
  var anchorFy=1;//anchorFy=1未选中申请专业或者资格主播翻页,anchorFy=2选中申请专业或者资格主播后翻页
  var userId=$(".login_user span",parent.document).attr("userid");
  
  
  /*放大或缩小身份证照片*/
  $(document).on("click",".enlarge_div",function(){
    var ii=$(this).parent(".rtc_listBox41").parent(".rtc_listBox40").parent(".rtc_listBox4").parent(".rtc_listBoxList").index();
    var _this0=$("#rtc_listBoxList"+ii);
    var _this=$("#rtc_listBoxList"+ii).children(".rtc_listBox4").children(".rtc_listBox40").children(".rtc_listBox41");
    var index=$(_this).children(".enlarge_div").index(this);
    if($(this).hasClass("large")){//此时已处于放大状态
      $(_this).children(".enlarge_div").each(function(i){
        if(index==i){
          $(this).removeClass("large").children(".enlarge_img").attr("src","../websiteManageResource/img/enlarge.png");
          $(this).removeClass("enlarge_br").parent(".rtc_listBox41").removeClass("border_img");
          $(this).siblings(".rtc_listBox42").removeClass("border_img1");
          $(this).parent(".rtc_listBox41").parent(".rtc_listBox40").siblings(".big").addClass("dis").attr("src","");
          return false;
        }
      });
    }else{
      var _this1=$(_this0).children(".rtc_listBox4").children(".rtc_listBox40");
      $(_this1).children(".rtc_listBox41").children(".enlarge_div").removeClass("large").children(".enlarge_img").attr("src","../websiteManageResource/img/enlarge.png");
      $(_this1).children(".rtc_listBox41").removeClass("border_img").children(".enlarge_div").removeClass("enlarge_br");
      $(_this1).children(".rtc_listBox41").children(".rtc_listBox42").removeClass("border_img1");
      $(_this1).siblings(".big").addClass("dis").attr("src","");
      $(_this).children(".enlarge_div").each(function(i){
        if(index==i){
          var l2=$(_this).width();
          var ll=i*l2+30;
          if(ll>=320) ll=280;
          var _src=$(this).siblings(".rtc_listBox42").attr("src");
          $(this).addClass("large").children(".enlarge_img").attr("src","../websiteManageResource/img/narrow.png");
          $(this).addClass("enlarge_br").parent(".rtc_listBox41").addClass("border_img");
          $(this).siblings(".rtc_listBox42").addClass("border_img1");
          $(this).parent(".rtc_listBox41").parent(".rtc_listBox40").siblings(".big").attr("src",_src).removeClass("dis").css("margin-left",ll+"px");
          return false;
        }
      });
    }
  });
  
  /*主播禁言状态的下拉菜单的切换*/
  $(".dropdown").on("click",function(){
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
          $(".rtc_listBoxLists").html("<div class='labels'>得到主播认证列表:"+resultData.Message+"</div>");
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
      var list='<div class="rtc_listBoxList" id=rtc_listBoxList'+i+'>'+
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
                '<div class="rtc_listBox43 fl" checkerId='+resultData.ResultList[i].UserId+'>'+
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
                  '<img src='+resultData.ResultList[i].FrontImg+' alt="身份证正面" class="rtc_listBox42"/>'+
                  '<div class="enlarge_div">'+
                    '<img src="../websiteManageResource/img/enlarge.png" alt="" class="enlarge_img"/>'+
                  '</div>'+
                '</li>';
        $(".rtc_listBox40").eq(i).append(img);
      }
      if(resultData.ResultList[i].ReverseImg){//身份证反面照片
        var img='<li class="rtc_listBox41 fl">'+
                  '<img src='+resultData.ResultList[i].ReverseImg+' alt="身份证反面" class="rtc_listBox42"/>'+
                  '<div class="enlarge_div">'+
                    '<img src="../websiteManageResource/img/enlarge.png" alt="" class="enlarge_img"/>'+
                  '</div>'+
                '</li>';
        $(".rtc_listBox40").eq(i).append(img);
      }
      if(resultData.ResultList[i].MixImg){//手持身份证照片
        var img='<li class="rtc_listBox41 fl">'+
                  '<img src='+resultData.ResultList[i].MixImg+' alt="手持身份证照片" class="rtc_listBox42"/>'+
                  '<div class="enlarge_div">'+
                    '<img src="../websiteManageResource/img/enlarge.png" alt="" class="enlarge_img"/>'+
                  '</div>'+
                '</li>';
        $(".rtc_listBox40").eq(i).append(img);
      }
      if(resultData.ResultList[i].AnchorCardImg){//资格证书照片
        var img='<li class="rtc_listBox41 fl">'+
                  '<img src='+resultData.ResultList[i].AnchorCardImg+' alt="资格证书照片" class="rtc_listBox42"/>'+
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
        $(".rtc_listBox47").eq(i).text("实名认证");
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
      if($(this).hasClass("selectId")&&$(this).attr("flag")!="0"){
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
        if($(this).hasClass("selectId")){
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
    $(this).addClass("selectId").siblings("li").removeClass("selectId");
  });
  
  /*点击弹出框的关闭按钮*/
  $(".mw_span2").on("click",function(){
    $(this).parent(".mw_head").parent(".mask_wrapper").parent().addClass("dis");
  });
  
  /*通过的弹出页面选中某一个认证方式*/
  $(".mw_div21 .checkbox_img").on("click",function(){
    if($(this).parent(".mw_div21").hasClass("selected")){
      $(this).parent(".mw_div21").removeClass("selected").children(".checkbox_img").removeClass("checkbox1").attr("src","../websiteManageResource/img/checkbox2.png");
      $(this).parent(".mw_div21").siblings(".mw_div21").addClass("selected").children(".checkbox_img").addClass("checkbox1").attr("src","../websiteManageResource/img/checkbox1.png");
    }else{
      $(this).parent(".mw_div21").addClass("selected").children(".checkbox_img").removeClass("checkbox1").attr("src","../websiteManageResource/img/checkbox2.png");
      $(this).parent(".mw_div21").siblings(".mw_div21").removeClass("selected").children(".checkbox_img").addClass("checkbox1").attr("src","../websiteManageResource/img/checkbox1.png");
    }
    var index=$(this).parent(".mw_div21").index();
    $(this).parent(".mw_div21").parent(".mw_div2").siblings(".mw_div3").children(".mw_con1").addClass("dis").eq(index).removeClass("dis");
  });
  
  /*点击主播通过/未通过资格认证的具体原因*/
  $(".mw_reason .checkbox_img").on("click",function(){
    if($(this).hasClass("checkbox1")){
      $(this).removeClass("checkbox1").attr("src","../websiteManageResource/img/checkbox2.png").siblings(".mw_txt1").css("color","#0077c7");
    }else{
      $(this).addClass("checkbox1").attr("src","../websiteManageResource/img/checkbox1.png").siblings(".mw_txt1").css("color","#000");
    }
  });
  
  /*点击通过--出现弹出页面或者主播通过认证*/
  $(document).on("click",".rtc_listBox45",function(){
    var _this=$(this);
    var checkerId=$(this).parent(".rtc_listBox43").attr("checkerId");
    $(".mask_pass").attr("checkerId",checkerId);
    var img_length=$(this).parent(".rtc_listBox43").siblings(".rtc_listBox4").children(".rtc_listBox40").children(".rtc_listBox41").length;
    if(img_length==4){//出现通过的提示弹出框
      $(".mw_div21").eq(0).removeClass("selected").children(".checkbox_img").addClass("checkbox1").attr("src","../websiteManageResource/img/checkbox1.png");
      $(".mw_div21").eq(1).addClass("selected").children(".checkbox_img").removeClass("checkbox1").attr("src","../websiteManageResource/img/checkbox2.png");
      if($(".mw_div21").hasClass("selected")){
        $(".mw_div21").parent(".mw_div2").siblings(".mw_div3").children(".mw_con1").eq(0).addClass("dis");
      }
      $(".mw_reason .checkbox_img").addClass("checkbox1").attr("src","../websiteManageResource/img/checkbox1.png").siblings(".mw_txt1").css("color","#000");
      $(".other_reason").val("");
      $(".mask_pass").removeClass("dis");
      return;
    }else{
      var data2={};
      data2.UserId=userId;
      data2.PCDType="3";
      data2.CheckerId=$(".mask_pass").attr("checkerId");
      data2.ReState="2";//0待处理，1未通过，2通过实名认证，3通过资格认证
      updateApprove(_this,data2);
    }
  });
  
  /*点击不通过--出现弹出页面使主播不通过认证*/
  $(document).on("click",".rtc_listBox46",function(){
    var checkerId=$(this).parent(".rtc_listBox43").attr("checkerId");
    $(".mw_reason .checkbox_img").addClass("checkbox1").attr("src","../websiteManageResource/img/checkbox1.png").siblings(".mw_txt1").css("color","#000");
    $(".other_reason").val("");
    $(".mask_nopass").removeClass("dis").attr("checkerId",checkerId);
  });
  
  
  /*点击通过弹出页面的确定按钮*/
  $(".mask_pass .mw_txt4").on("click",function(){
    var _this=$(this);
    var reDescn='';
    $(".mw_reason").each(function(){
      if($(this).children(".checkbox_img").hasClass("checkbox1")){
        
      }else{
        if(reDescn=='') reDescn=$(this).children(".mw_txt1").text();
        else reDescn+=','+$(this).children(".mw_txt1").text();
      }
    })
    if($(".other_reason").text()!=''){
      if(reDescn=='') reDescn=$(".other_reason").text();
      else reDescn+=','+$(".other_reason").text();
    }
    var data2={};
    data2.UserId=userId;
    data2.PCDType="3";
    data2.CheckerId=$(".mask_pass").attr("checkerId");
    data2.ReState="3";//0待处理，1未通过，2通过实名认证，3通过资格认证
    var _this=$(this);
    if(reDescn!='') data2.ReDescn=reDescn;
    updateApprove(_this,data2);
  });
  
  /*点击不通过弹出页面的确定按钮*/
  $(".mask_nopass .mw_txt4").on("click",function(){
    var _this=$(this);
    var reDescn='';
    $(".mw_reason").each(function(){
      if($(this).children(".checkbox_img").hasClass("checkbox1")){
        
      }else{
        if(reDescn=='') reDescn=$(this).children(".mw_txt1").text();
        else reDescn+=','+$(this).children(".mw_txt1").text();
      }
    })
    if($(".other_reason").text()!=''){
      if(reDescn=='') reDescn=$(".other_reason").text();
      else reDescn+=','+$(".other_reason").text();
    }
    var data2={};
    data2.UserId=userId;
    data2.PCDType="3";
    data2.CheckerId=$(".mask_nopass").attr("checkerId");
    data2.ReState="1";//0待处理，1未通过，2通过实名认证，3通过资格认证
    var _this=$(this);
    if(reDescn!='') data2.ReDescn=reDescn;
    updateApprove(_this,data2);
  });
  
  //更新认证状态
  function updateApprove(obj,dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/security/updateApproveStatus.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(dataParam),
      beforeSend:function(){
        $(obj).attr("disabled","disabled").css("color","#ccc");
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          $(".mask_pass,.mask_nopass").addClass("dis");
          var data1={};
          data1.UserId=userId;
          data1.PCDType="3";
          data1.Flag="0";//0默认全部,1实名认证,2资格认证
          data1.Page=current_page;
          data1.PageSize=pageSize;
          $(".dropdown_menu li").each(function(){
            if($(this).hasClass("selectId")){
              var flag=$(this).attr("flag");//0默认全部,1实名认证,2资格认证
              data1.Flag=flag;
              return;
            }
          });
          getAuthenticationList(data1);
        }else{
          alert("认证通过失败");
        }
        $(obj).removeAttr("disabled").css("color","#fff");
      },
      error:function(jqXHR){
        alert("认证通过发生错误:"+ jqXHR.status);
        $(obj).removeAttr("disabled").css("color","#fff");
      }
    });
  }
  
  /*点击查验身份*/
  $(document).on("click",".rtc_listBox44",function(){
    
  });
  
})
