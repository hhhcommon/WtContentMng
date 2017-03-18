$(function(){
  var rootPath=getRootPath();
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var data1={};
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchKey="";//搜索关键词
  var anchorFy=1;//anchorFy=1未选中不同状态的主播前翻页,anchorFy=2选中不同状态的主播后翻页
  
  /*获取主播的四个状态*/
  data1.UserId="123";
  data1.CatalogType="10";
  data1.ResultType="1";
  getCatalogInfo(data1);
  function getCatalogInfo(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/common/getCatalogInfo.do",
      dataType:"json",
      data:JSON.stringify(dataParam),
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          for(var i=0;i<resultData.CatalogData.SubCata.length;i++){
            var li= '<li catalogId='+resultData.CatalogData.SubCata[i].CatalogId+'>'+
                      '<span>'+resultData.CatalogData.SubCata[i].CatalogName+'</span>'+
                    '</li>';
            $(".dropdown_menu").append(li);
          }
        }else{
          alert("错误信息："+resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  
  /*获取主播列表*/
  var data2={};
  data2.UserId="123";
  data2.PageSize="10";
  data2.Page=current_page;
  getPersonsList(data2);
  function getPersonsList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/person/getPersons.do",
      dataType:"json",
      data:JSON.stringify(dataParam),
      beforeSend: function(){
        $(".ric_con2_content").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载节目列表...</div>");
        $('.shade', parent.document).show();
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          clear();
          allCount=resultData.ResultInfo.Count;
          contentCount=(allCount%10==0)?(allCount/10):(Math.ceil(allCount/10));
          loadPersonList(resultData);//加载主播列表
          pagitionInit(contentCount,allCount,dataParam.Page);//初始化翻页插件
        }else{
          $(".ric_con2_content").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到相关信息</div>");
        }
        $('.shade', parent.document).hide();
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  function loadPersonList(resultData){
    for(var i=0;i<resultData.ResultInfo.List.length;i++){
      var recovetime=resultData.ResultInfo.List[i].RecoverTime;
      recovetime=new Date(parseInt(recovetime)).toLocaleString('chinese',{hour12:false}).replace(/\//g, "-");  
      var perId=(resultData.ResultInfo.List[i].PersonId)?(resultData.ResultInfo.List[i].PersonId):("主播ID");
      var phNum=(resultData.ResultInfo.List[i].PhoneNum)?(resultData.ResultInfo.List[i].PhoneNum):("000000000000");
      var perName=(resultData.ResultInfo.List[i].PersonName)?(resultData.ResultInfo.List[i].PersonName):("昵称");
      var reaName=(resultData.ResultInfo.List[i].RealName)?(resultData.ResultInfo.List[i].RealName):("真实姓名");
      var idNumber=(resultData.ResultInfo.List[i].IDNumber)?(resultData.ResultInfo.List[i].IDNumber):("身份证号");
      var perSource=(resultData.ResultInfo.List[i].PersonSource)?(resultData.ResultInfo.List[i].PersonSource):("来源");
      var perStatus=(resultData.ResultInfo.List[i].PersonStatus)?(resultData.ResultInfo.List[i].PersonStatus):("主播状态");
      var li= '<li class="ric_cc_listBox fl" psId='+resultData.ResultInfo.List[i].PersonStatusId+'>'+
                '<img src="img/checkbox1.png" alt="" class="ric_img_check fl checkbox_img checkbox1"/>'+
                '<span class="fl ellipsis dis">'+perId+'</span>'+
                '<span class="fl c07 ellipsis person_name" perid='+perId+' style="width:130px;padding-right: 5px;">'+perName+'</span>'+
                '<span class="fl ellipsis" style="width:150px;padding:0px 5px;">'+reaName+'</span>'+
                '<span class="fl ellipsis" style="width:155px;padding:0px 5px;">'+phNum+'</span>'+
                '<span class="fl ellipsis" style="width:210px;padding:0px 2px;">'+idNumber+'</span>'+
                '<span class="fl ellipsis" style="width:155px;padding:0px 2px;">'+perSource+'</span>'+
                '<span class="fl st ellipsis">'+perStatus+'</span>'+
                '<span class="ric_txt39 fl dis mark">!</span>'+
                '<div class="mark_notes fl dis">'+
                  '<div class="square">'+
                    '<span class="mark_time fl c07 ellipsis">'+recovetime+'</span>'+
                    '<span class="mark_note fl ellipsis">恢复正常使用</span>'+
                  '</div>'+
                '</div>'+
              '</li>';
      $(".ric_con2_content").append(li);
      switch(perStatus){
        case "正常使用":$(".st").eq(i).addClass("bsg").removeClass("byl,bsb,bsr");
                        break;
        case "禁言一周":$(".st").eq(i).addClass("byl").removeClass("bsg,bsb,bsr");
                        $(".ric_cc_listBox").eq(i).children(".mark").removeClass("dis");
                        break;
        case "禁言一月":$(".st").eq(i).addClass("bsb").removeClass("bsg,byl,bsr");
                        $(".ric_cc_listBox").eq(i).children(".mark").removeClass("dis");
                        break;
        case "永久禁言":$(".st").eq(i).addClass("bsr").removeClass("bsg,byl,bsb");
                        break;
        default:
                        break;
      }
    }
  }
  
  /*鼠标放在感叹号上面提示*/
  $(document).on("mouseenter",".mark",function(){
    $(this).siblings(".mark_notes").removeClass("dis");
  });
  $(document).on("mouseleave",".mark",function(){
    $(this).siblings(".mark_notes").addClass("dis").delay(200);
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
      if($(this).hasClass("selected")){
        anchorFy=2;
        return false;
      }else{//anchorFy=1未选中不同状态的主播前翻页,anchorFy=2选中不同状态的主播后翻页
        anchorFy=1;
      }
    });
    opts(seaFy,anchorFy,current_page);
  }
 
  //判断在点击翻页之前是否进行了搜索查询
  function opts(seaFy,anchorFy,current_page){
    destroy(data2);
    data2.UserId="123";
    data2.PageSize="10";
    if(seaFy==2){//seaFy=2搜索列表加载出来后翻页
      current_page=1;
      data2.Page=current_page;
      data2.SearchWord=$.trim($(".ri_top_li2_inp").val());
    }else{
      data2.Page=current_page;
    }
    if(anchorFy==2){//anchorFy=1未选中不同状态的主播前翻页,anchorFy=2选中不同状态的主播后翻页
      $(".dropdown_menu li").each(function(){
        if($(this).hasClass("selected")){
          var cataId=$(this).attr("catalogId");
          data2.StatusType=cataId;
          return;
        }
      });
    }
    getPersonsList(data2);  
  }
  
  /*s--搜索的键盘事件*/
  $(document).keydown(function(e){//键盘上的事件
    e = e || window.event;
    var keycode = e.which ? e.which : e.keyCode;
    if(keycode == 13){//键盘上的enter
      searchList();//加载搜索列表
    }
  });
  $(".ri_top_li2_img").on("click",function(){
    searchList();//加载搜索列表
  });
  function searchList(){
    searchWord=$.trim($(".ri_top_li2_inp").val());
    destroy(data2);
    data2.UserId="123";
    data2.PageSize="10";
    current_page="1";
    data2.Page=current_page;
    if(searchWord==""){
      alert("请输入搜索内容");
      $(".ri_top_li2_inp").focus();
      return;
    }else{
      data2.SearchWord=searchWord;
    }
    $(".dropdown_menu li").each(function(){
      if($(this).hasClass("selected")){
        anchorFy=2;
        data2.StatusType=$(this).attr("catalogId");
        return false;
      }else{//anchorFy=1未选中不同状态的主播前翻页,anchorFy=2选中不同状态的主播后翻页
        anchorFy=1;
      }
    });
    getPersonsList(data2);  
  }
  /*e--搜索的键盘事件*/
 
  /*点击主播昵称--进入主播详情页*/
  $(document).on("click",".person_name",function(){
    var perid=$(this).attr("perid");
    $("#myIframe", parent.document).attr({"src":"anchor_detail.html?personId="+perid});
  });
  
  /*选中不同状态的主播--进行筛选*/
  $(".dropdown_menu").on("click","li",function(){
    destroy(data2);
    data2.UserId="123";
    data2.PageSize="10";
    current_page="1";
    data2.Page=current_page;
    if($.trim($(".ri_top_li2_inp").val())){
      data2.SearchWord=$.trim($(".ri_top_li2_inp").val());
    }
    anchorFy=2;//anchorFy=1未选中不同状态的主播前翻页,anchorFy=2选中不同状态的主播后翻页
    data2.StatusType=$(this).attr("catalogId");
    getPersonsList(data2);
    $(this).parent(".dropdown_menu").addClass("dis");
    $(this).parent(".dropdown_menu").siblings(".dropdown").children("img").attr({"src":"img/filter2.png"});
    $(this).addClass("selected").siblings("li").removeClass("selected");
  });
  
  /*清空*/
  function clear(){
    $(".ric_con2_content").html("");
    $(".ric_con1 button").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
  }
  /*销毁obj对象的key-value*/
  function destroy(obj){
    for(var key in obj){//清空对象
      delete obj[key];
    }
  }
  
  /*s--改变主播的状态*/
  /*点击全选*/
  $(document).on("click",".all_check",function(){
    if($(this).hasClass("checkbox1")){
      $(".checkbox_img").attr({"src":"img/checkbox2.png"});
      $(this).removeClass("checkbox1");
      $(".ric_con2_content .ric_cc_listBox").each(function(){
        $(this).children(".ric_img_check").removeClass("checkbox1");
      });
      $(".ric_con1 button").removeAttr("disabled");
      $(".gay_week").css({"color":"#fff","background":"#f60"});
      $(".gay_month").css({"color":"#fff","background":"#0077c7"});
      $(".gay_forever").css({"color":"#fff","background":"darkred"});
      $(".gay_revoke").css({"color":"#fff","background":"darkgreen"});
    }else{
      $(".checkbox_img").attr({"src":"img/checkbox1.png"});
      $(this).addClass("checkbox1");
      $(".ric_con2_content .ric_cc_listBox").each(function(){
        $(this).children(".ric_img_check").addClass("checkbox1");
      });
      $(".ric_con1 button").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
    }
  });
  
  /*点击单个勾选框*/
  $(document).on("click",".ric_img_check",function(){
    if($(this).hasClass("checkbox1")){
      $(this).attr({"src":"img/checkbox2.png"}).removeClass("checkbox1");
      $(".ric_con1 button").removeAttr("disabled");
      $(".gay_week").css({"color":"#fff","background":"#f60"});
      $(".gay_month").css({"color":"#fff","background":"#0077c7"});
      $(".gay_forever").css({"color":"#fff","background":"darkred"});
      $(".gay_revoke").css({"color":"#fff","background":"darkgreen"});
    }else{
      $(this).attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
      $(".ric_con1 button").attr({"disabled":"disabled"}).css({"color":"#000","background":"#ddd"});
    }
  });
  
  /*改变主播的状态*/
  var data3={};
  $(".ric_con1 .ric_txt2").on("click",function(){
    var perids='';
    var statusType=$(this).attr("statusType");
    $(".ric_con2_content .ric_cc_listBox").each(function(){
      if($(this).children(".ric_img_check").hasClass("checkbox1")){//未选中
        
      }else{//已选中
        if(perids==""){
          perids=$(this).children(".person_name").attr("perId");
        }else{
          perids+=","+$(this).children(".person_name").attr("perId");
        }
      }
    });
    if(perids!=""){
      data3.UserId="123";
      data3.PersonIds=perids;
      data3.StatusType=statusType;
      $.ajax({
        type:"POST",
        url:rootPath+"CM/person/updatePersonStatus.do",
        dataType:"json",
        data:JSON.stringify(data3),
        beforeSend: function(){
          $(".ric_con2_content").html("<div style='font-size:16px;text-align:center;line-height:40px;'>正在加载节目列表...</div>");
          $('.shade', parent.document).show();
        },
        success:function(resultData){
          if(resultData.ReturnType=="1001"){
            getPersonsList(data2);//获取主播列表
          }else{
            $(".ric_con2_content").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到相关信息</div>");
          }
          $('.shade', parent.document).hide();
        },
        error:function(jqXHR){
          alert("发生错误："+ jqXHR.status);
        }
      });
    }
  });
    
  /*e--改变主播的状态*/
});
