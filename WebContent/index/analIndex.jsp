<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.spiritdata.framework.FConstants"%>
<%
  String path = request.getContextPath(); //base Url
  String sid = request.getSession().getId(); //sessionId
  //得到用户信息
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- 默认的应用主页面  -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>

<link rel="stylesheet" type="text/css" href="<%=path%>/resources/plugins/spiritui/themes/default/pageFrame.css"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/index/css/all_spiritUi.css"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/index/css/main.css"/>

<script type="text/javascript" src="<%=path%>/resources/js/mainPage.utils.js"></script><!-- 主界面标识 -->
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.pageFrame.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/spiritui/jq.spirit.tabs.js"></script>

<!-- 加载ZUI - 开源HTML5跨屏框架 -->
<link href="<%=path%>/resources/plugins/zui/css/zui.min.css" rel="stylesheet">
<link href="<%=path%>/resources/plugins/zui/css/example.css" rel="stylesheet">
<script src="<%=path%>/resources/plugins/zui/js/zui.min.js"></script>

<title>互联网+无模式开放数据探索分析平台—灵派诺达</title>
</head>
<body>
<!-- 遮罩层 -->
<div id="mask" style="display:yes; position:absolute;vertical-align:middle;text-align:center; align:center;">
  <div id="maskTitle">正在分析，请稍候...</div>
</div>

<!-- 头部:悬浮 -->
<div id="topSegment">
  <div id="logo" class="main_logo"></div>
  <div id="ifNavs" class="main_ifnavs">
    <div id="nav_homepage" class="nav" onclick="switchIF(0)">首页</div>
    <div id="nav_report" class="nav" style="left:100px" onclick="switchIF(1)">报告</div>
    <div id="newReportFlag" onclick="showNoVisitReportList()"></div>
    <div id="nav_file" class="nav" style="left:200px" onclick="switchIF(2)">文件</div>
  </div>
  <div id="funBar" class="main_funbar">
    <div class="input-group" style="display:block; margin-bottom:5px; width:288px">
      <input id="searchAll" class="form-control" style="width:208px; height:24px" type="text" placeholder="请输入查询内容...">
      <span class="input-group-btn">
        <button id="submitSearchAll" onclick="startSearch();" class="btn btn-default" type="button" style="font:18px Microsoft YaHei,Microsoft JhengHei,黑体;">搜索</button>
      </span>
    </div>
  </div>
  <div id="upf_btn" class="upf_btn" title="上传数据文件" onclick="selF()"></div>
  <div id="userManage" class="main_userm">
    <div id="userShow">用户：<span id="loginName"></span></div>
    <div class="loginButton" id="login" onclick="login()">&nbsp;登&nbsp;录&nbsp;</div>
    <div class="loginButton" id="register" onclick="register()">&nbsp;注&nbsp;册&nbsp;</div>
  </div>
</div>

<!-- 中间主框架 -->
<div id="mainSegment">
  <iframe frameborder="no" style="display:none;" id="ifmHomepage" _src="analApp/homepage.jsp"></iframe>
  <iframe frameborder="no" style="display:none;" id="ifmReport" _src="analApp/ReportView/main.jsp"></iframe>
  <iframe frameborder="no" style="display:none;" id="ifmFile" _src="analApp/FileView/main.jsp"></iframe>
  <iframe frameborder="no" style="display:none;" id="ifmSearch" _src="analApp/listView/main.jsp"></iframe>
</div>

<form method="post" action="<%=path%>/fileUpLoad.do" enctype="multipart/form-data" id="afUpload" target="tframe" style="width:0px; height:0px;">        
  <input id="upf" name="upf" type="file" style="display:none;" onchange="uploadF()"/>
</form>
<iframe id="tframe" name="tframe" style="width:600px;heigth:200px;display:none;"></iframe>

<!-- 用户登录后的menu -->
<div id="userMenuShell">
  <div id="userMenu">
    <div class="menuItem" id="logout" onclick="logout()">&nbsp;注&nbsp;销&nbsp;</div>
    <div class="menuItem" id="updateUser" onclick="updateUser()">&nbsp;修&nbsp;改&nbsp;</div>
  </div>
</div>
<!--
<div id="footSegment">
<center>
<div style="align:center;color:red;padding-top:5px;font-weight:bold">我们将于2015年7月28日(周一)22:00至次日凌晨5:00对系统进行维护，若给您带来不便，请见谅！</div>
</center>
</div>
-->
</body>

<script>
//把jsp中java取出的变量在javascript层进行转存
var newReportJson = null; //未读报告列表
var lastSearchStr = ""; //上一次查询的搜索串
var searchTxt = "请输入查询内容..."; //查询提示信息
var curFrameIndex = -1; //当前激活的页面
var needRefresh = [1,1,1,1];//是否需要刷新，0不需要刷新，1需要刷新，needRefresh[1]报告；needRefresh[2]文件；needRefresh[3]查询；needRefresh[0]首页，不需要
var activeFlag=getUrlParam(window.location.href, "activeFlag");

//登录窗口大小
var wHeight = "430";
var wWidth = "330";

//主窗口参数
var INIT_PARAM = {
  pageObjs: {
    topId: "topSegment",
//    footId: "footSegment",
    mainId: "mainSegment"
  },
  page_width: -1,
  page_height: -1,
  top_shadow_color:"#E6E6E6",
  top_height: 50,
  top_peg: false,
//  foot_height: 30,
//  foot_peg: false,
  win_min_width: 870, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分宽度也照此设置
  win_min_height: 480, //页面最小的高度。当窗口高度小于这个值，不对界面位置及尺寸进行调整。主体部分高度也照此设置
  myInit: initPosition,
  myResize: myResize
};

//主函数
$(function() {
  //设置主界面
  var initStr = $.spiritPageFrame(INIT_PARAM);
  if (initStr) {
    $.messager.alert("页面初始化失败", initStr, "error");
    return ;
  };
  //============效果处理
  //导航样式
  $(".nav").mouseover(function() {
    if ($(this).hasClass("nav_sel")) return;
    $(this).addClass("nav_mouseOver");
  }).mouseout(function() {
    if ($(this).hasClass("nav_sel")) return;
    $(this).removeClass("nav_mouseOver");
  }).click(function() {
    if ($(this).hasClass("nav_sel")) return;
    var txt = $(this).html();
    $(".nav").each(function(){
      if ($(this).html()!=txt) $(this).removeClass("nav_sel").removeClass("nav_mouseOver");
    });
    //去掉搜索的选中样式
    $("#funBar").removeClass("nav_sel");
    $(this).removeClass("nav_mouseOver");
    $(this).addClass("nav_sel");
  });
  //上传按钮
  $("#upf_btn").mouseover(function() {
    if ($(this).hasClass("upf_mouseOver")) return;
    $(this).addClass("upf_mouseOver");
  }).mouseout(function() {
    $(this).removeClass("upf_mouseOver");
  }).click(function() {
  });
  //用户管理相关
  $(".loginButton").mouseover(function() {
    if ($(this).hasClass("lb_mouseOver")) return;
    $(this).addClass("lb_mouseOver");
  }).mouseout(function() {
    $(this).removeClass("lb_mouseOver");
  });
  //用户菜单
  $("#userShow").mouseover(function(){
    $("#userMenuShell").css({"top":$("#userShow").offset().top+18, "left":$(window).width()-$("#userMenuShell").width()-20});
    $("#userMenuShell").show();
  });
  $(".menuItem").mouseover(function() {
    if ($(this).hasClass("menuItem_mouseOver")) return;
    $(this).addClass("menuItem_mouseOver");
  }).mouseout(function() {
    $(this).removeClass("menuItem_mouseOver");
  });
  //处理鼠标与菜单的关系
  $(document).bind("mousemove", function(ev) {
    Ev= ev || window.event;
    var mousePos = mouseCoords(ev);
    if ($("#userMenuShell").is(":visible")) {
      if (mousePos.y<$("#userMenu").offset().top-20) $("#userMenuShell").hide();
      if (mousePos.y>$("#userMenu").offset().top+$("#userMenu").height()+5) $("#userMenuShell").hide();
      if (mousePos.x<$("#userMenu").offset().left-2) $("#userMenuShell").hide();
      if (mousePos.x>$("#userMenu").offset().left+$("#userMenu").width()-2) $("#userMenuShell").hide();
    }
    function mouseCoords(ev) {
      if (ev.pageX || ev.pageY) return {x:ev.pageX, y:ev.pageY};
      return {
        x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,
        y:ev.clientY + document.body.scrollTop - document.body.clientTop
      };
    }
  });
  //搜索输入区域，回车就查询
  $("#searchAll").keydown(function(e) {
    if(e.keyCode==13&&$("#searchAll").is(":visible")) startSearch();
  });
  //================显示状态
  setInitPage();
  getNoVisitReports();//先查一次
  //setInterval(getNoVisitReports,30*1000);//半分钟获取一次未读足以，获取未读报告
  if (activeFlag) {
    var msg="", _type="info";
    if (activeFlag==0||activeFlag==2) {
      msg="激活码不正确，激活失败！";
      _type="error";
    }
    else if (activeFlag==1) msg="帐号激活成功！<br/>感谢您使用“灵派诺达”提供的数据服务功能！";
    else if (activeFlag==3) msg="帐号已经激活！<br/>请您放心使用“灵派诺达”提供的数据服务功能！";

    if (msg) $.messager.alert("帐号激活", msg, _type);
  }
});

//初始化界面
function initPosition() {
  $("#mainSegment>iframe").each(function(){
    $(this).width($("#mainSegment").width()-4).height($("#mainSegment").height()-4);
  });
  $("#mask").css({"width":$(window).width()-2, "height":$(window).height()-2});
  $("#maskTitle").css({"top":$(window).height()/2+40, "left":($(window).width()-2-$("#maskTitle").width())/2});
}
//当界面尺寸改变
function myResize() {
  $("#mainSegment>iframe").each(function(){
    $(this).width($("#mainSegment").width()-4).height($("#mainSegment").height()-4);
  });
  $("#mask").css({"width":$(window).width()-2, "height":$(window).height()-2});
  $("#maskTitle").css({"top":$(window).height()/2+40, "left":($(window).width()-2-$("#maskTitle").width())/2});
}

//=====显示状态的处理 begin
//0----初始化
function setInitPage() {//刚进入系统或浏览器刷新：看是否用该session处理过文件，若处理过文件，则显示所有页签，否则只显示首页
  if (__STATUS==1) {
    $("#nav_report").show();
    $("#nav_file").show();
    $("#funBar").show();
  } else {
    $("#nav_report").hide();
    $("#nav_file").hide();
    $("#funBar").hide();
  }
  //点击主页
  $("#nav_homepage").click();
  setLoginPage();//登录状态
}
//1----页签状态处理
function setAfterFirstUpload() {//上传文件后，显示所有页签，并定位到报告页面，只在未登录状态，第一次上传起作用
  __STATUS=1;
  getNoVisitReports();
  //显示页签
  var showReport=false;
  if ($("#nav_report").is(":hidden")) showReport=true;
  $("#nav_report").show();
  $("#nav_file").show();
  $("#funBar").show();
  //点击报告页
  if (showReport) $("#nav_report").click();
}
//2----登录状态处理
function setLoginPage() {
  if (_loginName) {//已登录
    $("#login").hide();
    $("#register").hide();
    $("#userShow").show();

    $("#loginName").html(_loginName);
//    $("#loginName").attr("title", _loginName);

    $("#nav_report").show();
    $("#nav_file").show();
    $("#funBar").show();
  } else {//未登录
    $("#login").show();
    $("#register").show();
    $("#userShow").hide();
  }
}
//=====显示状态的处理 end

//=======切换iframe页面，无搜索页，并刷新
function switchIF(_type, _param) {
  if (_type==curFrameIndex&&needRefresh[_type]==0) return;
  else curFrameIndex=_type;

  $("iframe").each(function(){
    $(this).css("display","none");
  });
  $("#upf_btn").show();
  var ifmId="";
  switch(_type) {
  case 0: //切换到主页
    ifmId="ifmHomepage";
    $("#upf_btn").hide();
    break;
  case 1: //切换到报告页
    ifmId="ifmReport";
    break;
  case 2: //切换到文件页
    ifmId="ifmFile";
    break;
  }

  if (ifmId) {
    var curObj=$("#"+ifmId);
    var _url = curObj.attr("_src");
    if (_param) _url+=(_url.indexOf("?")==-1?"?":"&")+_param;
    if (needRefresh[_type]==1)  _url+=(_url.indexOf("?")==-1?"?":"&")+"refreshme=yes";

    if (!curObj.attr("src")||curObj.attr("src").indexOf(curObj.attr("_src"))==-1||needRefresh[_type]==1) {//地址错误，需要重新设置src 或 强制刷新
      curObj.attr("src",_PATH+"/"+_url);
      needRefresh[_type]=0;
    }
    if (curObj.attr("src")) curObj.css("display","block");
  }
}

//========登录相关
//登录
function login() {
  _openSwin("登录", "/login/login.jsp");
}
/**
 * 注销
 */
function logout() {
  $("#userMenuShell").hide();
  $("#loginName").val("");
  var url=_PATH+"/logout.do";
  $.ajax({type:"post", async:true, url:url, data:null, dataType:"json",
    success: function(json) {
      if (json.type==1) {
        $.messager.alert("注销信息","注销成功!",'info',function(){
          _loginName="";
          __STATUS=0;
          $("#newReportFlag").hide();
          newReportJson = null;
          setInitPage();
        });
      } else {
        if(json.data==null){
          $.messager.alert("提示","您还未登录!",'info');
        }else{
          $.messager.alert("错误", "注销失败："+json.data+"!</br>返回未登录首页。", "error", function() {
            _loginName="";
            __STATUS=0;
            setInitPage();
          });
        }
      }
    },
    error: function(errorData) {
      $.messager.alert("错误", "注销失败：</br>"+(errorData?errorData.responseText:"")+"!</br>返回未登录首页。", "error", function(){
        _loginName="";
        __STATUS=0;
        setInitPage();
      });
    }
  });
};
//注册
function register(){
  _openSwin("注册", "/login/register.jsp");
}
//修改个人信息
function updateUser(){
  $("#userMenuShell").hide();
  _openSwin("修改", "/login/update.jsp");
}
function _openSwin(title, url) {
  var winOption={
    "url":_PATH+url,
    "title":title,
    "height":wHeight,
    "width":wWidth,
    "modal":true
  };
  openSWinInMain(winOption);
}

//======文件上传
function selF() {
  $("#upf").click();
}
function uploadF() {
  try {
    var fileName=$("#upf").val();
    var _pos=fileName.lastIndexOf('.');
    if (_pos==-1) showAlert("数据上传", "抱歉！目前系统不支持对此格式文件的数据处理。", "warning");
    var _ext = fileName.substr(_pos);
    if (_ext.toUpperCase()!=".XLS"&&_ext.toUpperCase()!=".XLSX") {
      showAlert("数据上传", "抱歉！目前系统不支持对此格式文件的数据处理。", "warning");
      return;
    }
    var form = $('#afUpload');
    $(form).attr('action', _PATH+'/fileUpLoad.do');
    $(form).attr('method', 'POST');
    $(form).attr('target', 'tframe');
    if (form.encoding) form.encoding = 'multipart/form-data';
    else form.enctype = 'multipart/form-data';
    $(form).form('submit',{
      async: true,
      success: function(respStr) {
        var respJson = null;
        try {
          respJson=str2JsonObj(respStr);
        } catch(e) {
          showAlert("上传异常", e.message+"<br/>返回数据为="+respStr,"error");
        }
        var success=(respJson.jsonType==1&&respJson.data&&(respJson.data.length==1&&respJson.data[0].success));
        if (success+""=="TRUE") getNoVisitReports();//重新获取未读
        else {
          var msg = "";
          if (respJson.data&&respJson.data[0].message) msg=respJson.data[0].message;
          else {
            if (respJson.message instanceof string) msg=respJson.message;
            else msg=allFields(respJson.message[0]);
          }
          if (!msg) msg="未知问题";
          showAlert("数据上传", "数据文件上传失败！<br/>"+msg, "error");
        }
      },
      error: function(errData) {
        showAlert("上传失败", errData, "error");
      }
    });
  } catch(e) {
    showAlert("上传失败", e.message, "error");
  }
}

//======获取未读报告
//提示次数
var maxAlertCount_getNoVisitReports=3;
var maxCount_getNoVisitReports=1000;
var alertCount_getNoVisitReports=0;
function equalNoVisitList(curNoVisitList, newReadNoVisitList) {
  if (!newReportJson||!curNoVisitList) return false;
  if (curNoVisitList.total!=newReadNoVisitList.total) return false;
  var ret = true;
  for (var i=0; i<curNoVisitList.rows.length; i++) {
    var found=false;
    for (var j=0; j<newReadNoVisitList.rows.length; j++) {
      if (curNoVisitList.rows[i].reportId==newReadNoVisitList.rows[j].reportId) {
        found=true;
        break;
      }
    }
    if (!found) return false;
  }
  return ret;
}
function getNoVisitReports() {//得到未访问列表信息
  var searchParam={"searchType":"fectchNewReport","searchStr":""};
  var url="<%=path%>/reportview/searchNewReport.do";
  $.ajax({type:"post", async:true, url:url, data:searchParam, dataType:"json",
    success: function(jsonData) {
      try {
        //刷新报告
        if (jsonData&&jsonData.rows&&jsonData.total>=0) {
          var isNewNoVisitList;
          if (!equalNoVisitList(newReportJson,jsonData)) {
            setNoVisitReportNum(jsonData.total);
            newReportJson = jsonData;
            switchIF(curFrameIndex);
            //刷新查询
            if (curFrameIndex==3) startSearch();
          }
        }
      } catch(e) {
        alertCount_getNoVisitReports++;
        if (alertCount_getNoVisitReports<maxAlertCount_getNoVisitReports) {
          $.messager.alert("获得未读报告异常", "查询结果解析成JSON失败："+(e.message)+"！", "error", function(){});
        }
        if (alertCount_getNoVisitReports==maxAlertCount_getNoVisitReports) {
          $.messager.alert("获得未读报告异常", "查询结果解析成JSON失败："+(e.message)+"！<br/>已提示多次，系统将进入提示静默状态！", "error", function(){});
        }
        if (alertCount_getNoVisitReports>maxCount_getNoVisitReports) {//清除计数，推出静默状态
          alertCount_getNoVisitReports=0;
        }
      }
    },
    error: function(errorData) {
      alertCount_getNoVisitReports++;
      if (alertCount_getNoVisitReports<maxAlertCount_getNoVisitReports) {
        $.messager.alert("查询未访问报告异常", "查询异常："+(errorData?errorData.responseText+"！":""), "error", function(){});      
      }
      if (alertCount_getNoVisitReports==maxAlertCount_getNoVisitReports) {
        $.messager.alert("查询未访问报告异常", "查询异常："+(errorData?errorData.responseText+"！":"")+"<br/>已提示多次，系统将进入提示静默状态！", "error", function(){});      
      }
      if (alertCount_getNoVisitReports>maxCount_getNoVisitReports) {//清除计数，推出静默状态
        alertCount_getNoVisitReports=0;
      }
    }
  });
}
function showNoVisitReportList() {//显示未读报告
  var winOption={
    url:"<%=path%>/analApp/ReportView/unReadTable.jsp",
    title:"未读报告列表",
    height:600,
    width:750,
    iframeScroll:"yes"
  };
  openSWinInMain(winOption);
}
function setNoVisitReportNum(num) {//设置未访问报告标签的值
  var _num = parseInt(num);
  if (_num>0) {
    $("#newReportFlag").attr("noVisitRepNum", _num);//记录下来
    $("#newReportFlag").html(_num>99?"...":_num+"");
    if (_num>99) $("#newReportFlag").attr("title", _num); else $("#newReportFlag").attr("title", ""); 
    $("#newReportFlag").show();
  } else $("#newReportFlag").hide();
}
/**
 * 返回指定的报告ID是否为未读报告，为报告页调用做准备
 * 当条件查询报告的时候，触发此方法
 * 如果areportId在newReportJson中，则返回true
 */
function isUnReadReportById(areportId) {
  if ((newReportJson&&newReportJson.rows&&newReportJson.total>0)&&areportId) {
    for(var i=0;i<newReportJson.total;i++) {
      var aRow = newReportJson.rows[i];
      if (aRow.reportId && aRow.reportId==areportId) return true;
    }
  }
  return false;
}
/**
 * 增加或删除未访问报告标签的值
 * tag:增加或删除标志：0:增加，1删除
 * reportList:报告对象列表，或报告对象本身，元素必须包括reportId属性
 */
function incremeNoVisitReports(tag, reportList) {
  var _tag = tag;
  if (_tag!=0) _tag=1;//默认是删除

  //当前数
  var _curNum = $("#newReportFlag").attr("noVisitRepNum");
  var __curNum=_curNum;

  if (_tag==1) { //删除
    if ($.isArray(reportList)) {
      var delRep = new Array();
      for (var i=0; i<reportList.length; i++) {
        if (reportList[i].reportId) {
          for (var j=0; j<newReportJson.total; j++) {
            if (reportList[i].reportId==newReportJson.rows[j].reportId) {
              delRep.push(j);
              break;
            }
          }
        }
      }
      if (delRep.length>0) {
        for (var k=0; k<delRep.length; k++) {
          newReportJson.rows.removeByIndex(delRep[k]);
          newReportJson.total--;
          _curNum--;
        }
      }
    } else {//只有一个对象
      if (reportList.reportId) {
        for (var j=0; j<newReportJson.total; j++) {
          if (reportList.reportId==newReportJson.rows[j].reportId) {
            newReportJson.rows.removeByIndex(j);
            newReportJson.total--;
            _curNum--;
            break;
          }
        }
      }
    } 
  } else { //增加
    if ($.isArray(reportList)) {
      for (var i=0; i<reportList.length; i++) {
        newReportJson.rows.push(reportList[i]);
        newReportJson.total++;
        _curNum++;
      }
    } else {//只有一个对象
      newReportJson.rows.push(reportList[i]);
      newReportJson.total++;
      _curNum++;
    }
  };
  if (_curNum!=__curNum) setNoVisitReportNum(_curNum);
}

//==============搜索
function startSearch() {
  curFrameIndex=3;
  $("#ifmHomepage").css("display","none");
  $("#ifmReport").css("display","none");
  $("#ifmFile").css("display","none");
  $("#ifmSearch").css("display","none");

  var searchStr = ($("#searchAll").val()==searchTxt?"":$("#searchAll").val());
  var urlParam = "searchStr="+encodeURIComponent(searchStr);

  var curObj=$("#ifmSearch");
  var _url = curObj.attr("_src");
  if (!curObj.attr("src")||curObj.attr("src").indexOf(curObj.attr("_src"))==-1) {//地址错误，需要重新设置src
    if (curObj.attr("_src")) {
      _url +="?"+urlParam;
      curObj.attr("src",_PATH+"/"+_url);
    }
  } else {//若原来iframe已经加载过
    if (lastSearchStr!=searchStr) {//若查询不相等，则需要重新查询
      lastSearchStr = searchStr;
      _url +="?"+urlParam;
      curObj.attr("src",_PATH+"/"+_url);
    }
  }
  curObj.css("display","block");

  //设置效果
  $("#funBar").addClass("nav_sel");
  $(".nav").each(function() {
    $(this).removeClass("nav_sel");
  });
  $("#upf_btn").show();
}

//======为其他页面调用
function setLogined(loginName) {
  //显示页签
  $("#nav_report").show();
  $("#nav_file").show();
  $("#funBar").show();
  _loginName=loginName;
  setLoginPage();
}
//显示遮罩层，flag=1为显示,flag=0为隐藏，message是遮罩层的名称
function showMask(flag, message) {
  if (flag) {
    $("#maskTitle").html(message+(message.indexOf("...")==-1?"...":""));
    $("#mask").show();
    $("#maskTitle").css({"left":($(window).width()-2-$("#maskTitle").width())/2});
  } else $("#mask").hide();
}
</script>
</html>