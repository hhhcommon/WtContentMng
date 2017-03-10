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
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          clear();
          contentCount=resultData.ResultInfo.Count;
          contentCount=(contentCount%10==0)?(contentCount/10):(Math.ceil(contentCount/10));
          $(".totalPage").text(contentCount);
          loadPersonList(resultData);//加载主播列表
        }else{
          $(".totalPage").text("0");
          $(".ric_con2_content").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到相关信息</div>");
        }
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
                '<span class="ric_txt31 fl ellipsis" style="width:110px;">'+perId+'</span>'+
                '<span class="ric_txt32 fl ellipsis">'+phNum+'</span>'+
                '<span class="ric_txt33 fl c07 ellipsis person_name" perid='+perId+'>'+perName+'</span>'+
                '<span class="ric_txt34 fl ellipsis">'+reaName+'</span>'+
                '<span class="ric_txt35 fl ellipsis">'+idNumber+'</span>'+
                '<span class="ric_txt36 fl ellipsis">'+perSource+'</span>'+
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
  
  /*翻页*/
  $(".pagination span").on("click",function(){
    var data_action=$(this).attr("data_action");
    if($(".ri_top_li2_inp").val()==""){//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
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
        opts(seaFy,anchorFy);
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
        opts(seaFy,anchorFy);
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
        opts(seaFy,anchorFy);
        return;
      }
    }
  });
  //判断在点击翻页之前是否进行了搜索查询
  function opts(seaFy,anchorFy){
    destroy(data2);
    data2.UserId="123";
    data2.PageSize="10";
    if(seaFy==2){//seaFy=2搜索列表加载出来后翻页
      current_page=1;
      data2.Page=current_page;
      data2.SearchWord=$(".ri_top_li2_inp").val();
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
    destroy(data2);
    data2.UserId="123";
    data2.PageSize="10";
    current_page="1";
    data2.Page=current_page;
    $(".currentPage").html(current_page);
    if($(".ri_top_li2_inp").val()==""){
      alert("请输入搜索内容");
      $(".ri_top_li2_inp").focus();
    }else{
      data2.SearchWord=$(".ri_top_li2_inp").val();
    }
    $(".dropdown_menu li").each(function(){
      if($(this).hasClass("selected")){
        anchorFy=2;
        var cataId=$(this).attr("catalogId");
        data2.StatusType=cataId;
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
    $("#newIframe", parent.document).attr({"src":"anchor_detail.html?personId="+perid});
    $("#myIframe", parent.document).hide();
    $("#newIframe", parent.document).show();
  });
  
  /*选中不同状态的主播--进行筛选*/
  $(".dropdown_menu").on("click","li",function(){
    destroy(data2);
    data2.UserId="123";
    data2.PageSize="10";
    current_page="1";
    data2.Page=current_page;
    $(".currentPage").html(current_page);
    if($(".ri_top_li2_inp").val()){
      data2.SearchWord=$(".ri_top_li2_inp").val();
    }
    anchorFy=2;//anchorFy=1未选中不同状态的主播前翻页,anchorFy=2选中不同状态的主播后翻页
    var cataId=$(this).attr("catalogId");
    data2.StatusType=cataId;
    getPersonsList(data2);
    $(this).parent(".dropdown_menu").addClass("dis");
    $(this).parent(".dropdown_menu").siblings(".dropdown").children("img").attr({"src":"img/filter2.png"});
    $(this).addClass("selected").siblings("li").removeClass("selected");
  });
  
  /*清空*/
  function clear(){
    $(".ric_con2_content,.totalPage").html("");
    $(".toPage").val("");
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
    }else{
      $(".checkbox_img").attr({"src":"img/checkbox1.png"});
      $(this).addClass("checkbox1");
      $(".ric_con2_content .ric_cc_listBox").each(function(){
        $(this).children(".ric_img_check").addClass("checkbox1");
      }); 
    }
  });
  /*点击单个勾选框*/
  $(document).on("click",".ric_img_check",function(){
    if($(this).hasClass("checkbox1")){
      $(this).attr({"src":"img/checkbox2.png"}).removeClass("checkbox1");
    }else{
      $(this).attr({"src":"img/checkbox1.png"}).addClass("checkbox1");
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
    if(perids==""){
      alert("请先选中内容再进行操作");
      return;
    }else{
      data3.UserId="123";
      data3.PersonIds=perids;
      data3.StatusType=statusType;
      $.ajax({
        type:"POST",
        url:rootPath+"CM/person/updatePersonStatus.do",
        dataType:"json",
        data:JSON.stringify(data3),
        success:function(resultData){
          if(resultData.ReturnType=="1001"){
            getPersonsList(data2);//获取主播列表
          }else{
            $(".totalPage").text("0");
            $(".ric_con2_content").html("<div style='text-align:center;height:300px;line-height:200px;'>没有找到相关信息</div>");
          }
        },
        error:function(jqXHR){
          alert("发生错误："+ jqXHR.status);
        }
      });
    }
  });
    
  /*e--改变主播的状态*/
});
