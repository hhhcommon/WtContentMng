<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%String path = request.getContextPath();%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<jsp:include page="/common/sysInclude.jsp" flush="true"/>
<title></title>
<style type="text/css">
.inp-line {
  padding-top:0px;
  padding-bottom:25px;
}
.inp-label {
  font-size:18px;
  font-weight:bolder;
  width:150px;
  text-line:150px;
  text-align:right;
  padding-right:5px;
  text-valign:top;
  valign:top;
}
.inp-text {
  font-size:14px;
  width:300px;
  text-line:300px;
  text-align:left;
  height:22px;
  border: 1px solid #95B8E7;
  vertical-align: middle;
}
.inp-txtarea  {
  resize: none;
  font-size:14px;
  height:180px;
  width:300px;
  text-line:300px;
  text-align:left;
  border: 1px solid #95B8E7;
}

.lf_title div {
  font-size:18px;
  font-weight:bolder;
  display:inline;
  padding-left:20px;
}
.lf_list {
  margin-top:5px;
  margin-left:3px;
  width:398px;
  height:140px;
  border:1px solid #95B8E7;
  overflow-y:auto;
  overflow-x:hidden;
}
.lf_input input {
  text-align:left;
  height:22px;
  border: 1px solid #95B8E7;
  vertical-align: middle;
}
.icon-play{
  background:url('../resources/plugins/easyui-1.3.4/themes/default/images/pagination_icons.png') no-repeat center center;
  background-position:-32px 50%;
}

.cntTbs {
  overflow-y:hidden;
  width:396px;
}
.cntTbs tr {
  border-bottom:1px solid #efefef;
}
.cntTbs td{
  padding:3px;
}
.trcn {
  width:306px;
}
.text-overflow {
  width:300px;
  overflow:hidden;;/* 内容超出宽度时隐藏超出部分的内容 */
  text-overflow:ellipsis;;/* 当对象内文本溢出时显示省略标记(...) ；需与overflow:hidden;一起使用。*/
  white-space:nowrap;/* 不换行 */
}
</style>
</head>
<body id="body" class="easyui-layout" data-options="fit:true" style="overflow:hidden;">
<div data-options="region:'east',split:false" style="width:420px;border:0px;border-left:1px solid #95B8E7;">
  <div class="easyui-layout" data-options="fit:true" style="border:none;">
    <div data-options="region:'north',collapsible:false" title="直播流" style="height:300px;border:0px;padding:5px;">
      <div class="lf_title" style="width:400px;height:30px;">
        <div>来源</div><div style="padding-left:70px;">直播Url</div>
      </div>
      <div class="lf_input">
        <input style="width:100px" id="aSource" name="aSource"></input><input style="width:223px" id="aUrl" name="aUrl"></input>
        <a href="#" class="easyui-linkbutton" iconCls="icon-play" onclick="playBc()" title="播放"></a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-add" onclick="addBc()" title="添加"></a>
      </div>
      <div class="lf_list">
      <table class="cntTbs" id="bcTbs">
      </table>
      </div>
    </div>
    <!-- 
    <div data-options="region:'center'" title="分类" style="border:0px"></div>
    -->
    <div data-options="region:'center',collapsible:false" title="频段" style="border:0px;">
    </div>
  </div>
</div>
<div data-options="region:'center',split:false" title="基本信息" id="baseInfo" style="border:0px; padding:30px;padding-left:30px;">
  <div class="inp-line">
    <span class="inp-label">电台名称:</span><input class="easyui-textbox" type="text" id="bcTitle" name="bcTitle" data-options="required:true"></input>
  </div>
  <div class="inp-line">
    <span class="inp-label">所属集团:</span><input class="easyui-textbox" type="text" id="bcPublisher" name="bcPublisher" data-options="required:true"></input>
  </div>
  <div class="inp-line">
    <span class="inp-label">电台网址:</span><input class="easyui-textbox" type="text" id="bcUrl" name="bcUrl" data-options="required:true"></input>
  </div>
  <div class="inp-line">
    <span class="inp-label">内容类别:</span><input class="easyui-textbox" type="text" id="cType" name="cType" data-options="required:true" readonly style="width:240px;text-line:240px;"></input>
    <a href="#" class="easyui-linkbutton" onclick="parent.openSel('内容类别',1,1,null)" id="selArea" style="width:50px;">选择</a>
  </div>
  <div class="inp-line">
    <span class="inp-label">所属地区:</span><input class="easyui-textbox" type="text" id="bcArea" name="bcArea" data-options="required:true" readonly style="width:240px;text-line:240px;"></input>
    <a href="#" class="easyui-linkbutton" onclick="parent.openSel('所属地区',0,2,null)" id="selArea" style="width:50px;">选择</a>
  </div>
  <div class="inp-line">
    <div style="width:80px;float:left;" class="inp-label">电台说明:</div><textarea class="inp-txtarea" id="descn" name="descn"></textarea>
  </div>
</div>
<div data-options="region:'south',split:false" style="height:40px;border:0px;border-top:1px solid #95B8E7;">
  <div class="easyui-layout" data-options="fit:true" style="border:none;align:center;text-align:center;padding-top:4px;">
    <a href="#" id="commit" class="easyui-linkbutton" iconCls="icon-save" onclick="commit()">提交</a>&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="#" id="cancel" class="easyui-linkbutton" iconCls="icon-undo" onclick="cancel()">取消</a>
  </div>
</div>
</body>
<script>
var 
var _ctype, _bcarea;
var _type="";
$(function(){
  _type=getUrlParam(window.location.href, "type");
  if (_type=="new") document.title="新增";
  if (_type=="update") document.title="修改";
  $("#selArea").linkbutton({
    width:"50px",
    text:"选择"
  });
  initPage();
  $(body).resize();
  $("#body").layout("resize");
});

function initPage() {
  //边框设置
  $(".panel-title").each(function(){
    if ($(this).html()=="基本信息"||$(this).html()=="直播流") {
      $(this).parent().css({
        "border-bottom":"1px solid #95B8E7",
        "border-top":"0px",
        "border-left":"0px",
        "border-right":"0px"
      });
    }
    if ($(this).html()=="频段") {
      $(this).parent().css({
      	"margin-top":"1px",
        "border-left":"0px",
        "border-right":"0px"
      });
    }
  });
  //录入区域
  $("#baseInfo").find("input").each(function(){
    $(this).css({
      "font-size":"14px",
      "width":"300px",
      "text-line":"300px",
      "text-align":"left",
      "height":"22px",
      "border":"1px solid #95B8E7",
      "vertical-align":"middle"
    });
    if ($(this).attr("name")=="bcArea"||$(this).attr("name")=="cType") {
      $(this).css({
        "width":"241px",
        "text-line":"241px"
      });
    }
  });
}

function commit() {
  formData={};
  if (!bcTitle.value) {alert("请输入名称");bcTitle.focus();return;}
  formData.bcTitle=bcTitle.value;
  if (!bcPublisher.value) {alert("请输入所属集团");bcPublisher.focus();return;}
  formData.bcPublisher=bcPublisher.value;
  formData.bcUrl=bcUrl.value;
  if (!bcArea.value) {alert("请输入所属地区");bcArea.focus();return;}
  formData.bcArea=_bcarea;
  formData.bcAreaName=bcArea.value;
  if (!cType.value) {alert("请输入分类");cType.focus();return;}
  formData.cType=_ctype;
  formData.cName=cType.value;
  formData.descn=descn.value;
  formData.bcLiveFlows=aUrl.value+"::"+aSource.value+";;";
  $("#commit").linkbutton("disable");
  $.ajax({type:"post", async:true, data:formData, url:'<%=path%>/bc/add.do', dataType:"json",
    success: function(data) {
      alert("新增成功!");
      parent.loadList();
      parent.$("#w").window("close");
    }
  });
}
function cancel() {
  parent.$("#w").window("close");
}
function setCType(nodes) {
  var _cname="";
  _ctype="";
  if (nodes) {
    for (var i=0; i<nodes.length; i++) {
      _ctype+=","+nodes[i].id;
      _cname+=","+nodes[i].nodeName;
    }
    _ctype=_ctype.substring(1);
  }
  $("#cType").val(_cname.substring(1));
}
function setBcArea(nodes) {
  var _bcAreaName="";
  _bcarea="";
  if (nodes) {
    for (var i=0; i<nodes.length; i++) {
      _bcarea+=","+nodes[i].bCode;
      _bcAreaName+=","+nodes[i].nodeName;
    }
    _bcarea=_bcarea.substring(1);
  }
  $("#bcArea").val(_bcAreaName.substring(1));
}
function addBc() {
  if (!aSource.value) {
    alert("请输入来源");
    $("#aSource").focus();
    return;
  }
  if (!aUrl.value) {
    alert("请输入直播流Url");
    $("#aUrl").focus();
    return;
  }
  var tr=$("<tr></tr>");
  var td=$("<td class='trcn'><div class='text-overflow'>"+aSource.value+":"+aUrl.value+"</div></td>");
  td.appendTo(tr);
  td=$("<td><input type='radio' name='bcIsMain' title='是否主直播流'/></td>");
  var abtn=$("<a href='#' class='easyui-linkbutton' iconCls='icon-play' onclick='play()' title='播放' style='margin-left:3px;'></a>");
  abtn.linkbutton({});
  abtn.appendTo(td);
  abtn=$("<a href='#' class='easyui-linkbutton' iconCls='icon-remove' onclick='dels()' title='删除' style='margin-left:3px;'></a>");
  abtn.linkbutton({});
  abtn.appendTo(td);
  td.appendTo(tr);
  tr.appendTo($("#bcTbs"));
  $("#aSource").val("");
  $("#aUrl").val("");
}
</script>
</html>