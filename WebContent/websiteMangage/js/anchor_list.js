$(function(){
  var rootPath=getRootPath();
  var current_page=1;//当前页码
  var contentCount=0;//总页码数
  var data1={};
  var seaFy=1;//seaFy=1未搜索关键词前翻页,seaFy=2搜索列表加载出来后翻页
  var searchKey="";//搜索关键词
  
  
  
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
  data2.SearchWord="";
  getPersonsList(data2);
  function getPersonsList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/person/getPersons.do",
      dataType:"json",
      data:JSON.stringify(dataParam),
      success:function(resultData){
        clear();
        contentCount=resultData.ResultInfo.Count;
        contentCount=(contentCount%10==0)?(contentCount/10):(Math.ceil(contentCount/10));
        $(".totalPage").text(contentCount);
        if(resultData.ReturnType=="1001"){
          loadPersonList(resultData);//加载主播列表
        }else{
          alert("错误信息："+resultData.Message);
        }
      },
      error:function(jqXHR){
        alert("发生错误："+ jqXHR.status);
      }
    });
  }
  function loadPersonList(resultData){
    for(var i=0;i<resultData.ResultInfo.List.length;i++){
      var perId=(resultData.ResultInfo.List[i].PersonId)?(resultData.ResultInfo.List[i].PersonId):("主播ID");
      var phNum=(resultData.ResultInfo.List[i].PhoneNum)?(resultData.ResultInfo.List[i].PhoneNum):("000000000000");
      var perName=(resultData.ResultInfo.List[i].PersonName)?(resultData.ResultInfo.List[i].PersonName):("昵称");
      var reaName=(resultData.ResultInfo.List[i].RealName)?(resultData.ResultInfo.List[i].RealName):("真实姓名");
      var idNumber=(resultData.ResultInfo.List[i].IDNumber)?(resultData.ResultInfo.List[i].IDNumber):("身份证号");
      var perSource=(resultData.ResultInfo.List[i].PersonSource)?(resultData.ResultInfo.List[i].PersonSource):("来源");
      var perStatus=(resultData.ResultInfo.List[i].PersonStatus)?(resultData.ResultInfo.List[i].PersonStatus):("身份证号");
      var li= '<li class="ric_cc_listBox fl" psId='+resultData.ResultInfo.List[i].PersonStatusId+'>'+
                '<img src="img/checkbox1.png" alt="" class="ric_img_check fl checkbox_img checkbox1"/>'+
                '<span class="ric_txt31 fl ellipsis" style="width:110px;">'+perId+'</span>'+
                '<span class="ric_txt32 fl ellipsis">'+phNum+'</span>'+
                '<span class="ric_txt33 fl c07 ellipsis person_name" perid='+perId+'>'+perName+'</span>'+
                '<span class="ric_txt34 fl ellipsis">'+reaName+'</span>'+
                '<span class="ric_txt35 fl ellipsis">'+idNumber+'</span>'+
                '<span class="ric_txt36 fl ellipsis">'+perSource+'</span>'+
                '<span class="fl bsg st ellipsis">'+perStatus+'</span>'+
                '<span class="ric_txt39 fl dis mark">!</span>'+
                '<div class="mark_notes fl dis">'+
                  '<div class="square">'+
                    '<span class="mark_time fl c07 ellipsis">2017-11-15 12:12:21</span>'+
                    '<span class="mark_note fl ellipsis">恢复正常使用</span>'+
                  '</div>'+
                '</div>'+
              '</li>';
      $(".ric_con2_content").append(li);
    }
  }
  /*翻页*/
  $(".pagination span").on("click",function(){
    var data_action=$(this).attr("data_action");
    searchWord=$(".ri_top_li2_inp").val();
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
        $(".currentPage").text(current_page);
        $(".page").find("span").removeClass("disabled");
        opts(seaFy);
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
        opts(seaFy);
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
        opts(seaFy);
        return;
      }
    }
  });
  //判断在点击翻页之前是否进行了搜索查询
  function opts(seaFy){
    destroy(data2);
    data2.UserId="123";
    data2.PageSize="10";
    data2.Page=current_page;
    data2.SearchWord=$(".ri_top_li2_inp").val();
    getPersonsList(data2);
  }
  /*s--搜索的键盘事件*/
  $(document).keydown(function(e){//键盘上的事件
    e = e || window.event;
    var keycode = e.which ? e.which : e.keyCode;
    if(keycode == 13){//键盘上的enter
      $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
      $(".startPubTime,.endPubTime").val("");
      $("#source,#channel").show();
      searchList();//加载搜索列表
    }
  });
  $(".ri_top_li2_img").on("click",function(){
    $(".all").css("display","none").children(".new_cate").html("");//每次搜索时都要清除筛选条件，search的优先级大于filters
    $(".startPubTime,.endPubTime").val("");
    $(".cate_img").click();
    searchList();//加载搜索列表
  });
  /*e--搜索的键盘事件*/
  /*点击主播昵称--进入主播详情页*/
  $(document).on("click",".person_name",function(){
    var perid=$(this).attr("perid");
    $("#newIframe", parent.document).attr({"src":"anchor_detail.html?personId="+perid});
    $("#myIframe", parent.document).hide();
    $("#newIframe", parent.document).show();
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
});
