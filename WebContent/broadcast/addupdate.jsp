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
.lineinput {
  padding-top:0px;
  padding-bottom:25px;
  font-size:18px;
}
.lineinput span {
  font-size:18px;
  font-weight:bolder;
  width:150px;
  text-line:150px;
  text-align:right;
  padding-right:5px;
  text-valign:top;
  valign:top;
}
.lineinput input {
  font-size:14px;
  width:300px;
  text-line:300px;
  text-align:left;
  height:22px;
  border: 1px solid #95B8E7;
  vertical-align: middle;
}
.lineinput textarea {
  font-size:14px;
  width:300px;
  text-line:300px;
  text-align:left;
  height:22px;
  border: 1px solid #95B8E7;
  vertical-align: middle;
}
.lineinput select {
  font-size:14px;
  width:300px;
  text-line:300px;
  text-align:left;
  height:25px;
  border: 1px solid #95B8E7;
  vertical-align: middle;
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
  width:300px;
  height:100px;
  border:1px solid #95B8E7;
  overflow:yes;
}
.lf_input input {
  text-align:left;
  height:22px;
  border: 1px solid #95B8E7;
  vertical-align: middle;
}
.icon-play{
  background-imange:url('../resources/plugins/easyui-1.3.4/themes/default/images/pagination_icons.png') no-repeat center center;
  background-position:-32px 50%;
}
.lf_one {
  padding:3px;
  border-bottom:1px solid #efefef;
}
</style>
</head>
<body class="easyui-layout" data-options="fit:true">
<form id="ff" method="post">
<div data-options="region:'east',split:false" style="width:320px;border:0px;border-left:1px solid #95B8E7;">
  <div class="easyui-layout" data-options="fit:true" style="border:none;">
    <div data-options="region:'north',collapsible:false" title="直播流" style="height:300px;border:0px;padding:5px;">
      <div class="lf_title" style="width:300px;height:30px;">
        <div>来源</div><div style="padding-left:50px;">直播Url</div>
      </div>
      <div class="lf_input">
        <input style="width:80px" id="aSource" name="aSource"></input><input style="width:150px" id="aUrl" name="aUrl"></input>
        <a href="#" class="easyui-linkbutton" iconCls="icon-play" onclick="newBc()" title="播放"></a>
        <a href="#" class="easyui-linkbutton" iconCls="icon-add" onclick="newBc()" title="添加"></a>
      </div>
      <div class="lf_list">
      <!-- 
        <div class="lf_one">
          测试：是打发打发发的
          <a href="#" class="easyui-linkbutton" iconCls="icon-play" onclick="newBc()" title="播放"></a>
          <a href="#" class="easyui-linkbutton" iconCls="icon-add" onclick="newBc()" title="添加"></a>
        </div>
        <div class="lf_one">
          测试：是打发打发发的
          <a href="#" class="easyui-linkbutton" iconCls="icon-play" onclick="newBc()" title="播放"></a>
          <a href="#" class="easyui-linkbutton" iconCls="icon-add" onclick="newBc()" title="添加"></a>
        </div>
        <div class="lf_one">
          测试：是打发打发发的
          <a href="#" class="easyui-linkbutton" iconCls="icon-play" onclick="newBc()" title="播放"></a>
          <a href="#" class="easyui-linkbutton" iconCls="icon-add" onclick="newBc()" title="添加"></a>
        </div>
        <div class="lf_one">
          测试：是打发打发发的
          <a href="#" class="easyui-linkbutton" iconCls="icon-play" onclick="newBc()" title="播放"></a>
          <a href="#" class="easyui-linkbutton" iconCls="icon-add" onclick="newBc()" title="添加"></a>
        </div>
        <div class="lf_one">
          测试：是打发打发发的
          <a href="#" class="easyui-linkbutton" iconCls="icon-play" onclick="newBc()" title="播放"></a>
          <a href="#" class="easyui-linkbutton" iconCls="icon-add" onclick="newBc()" title="添加"></a>
        </div>
        <div class="lf_one">
          测试：是打发打发发的
          <a href="#" class="easyui-linkbutton" iconCls="icon-play" onclick="newBc()" title="播放"></a>
          <a href="#" class="easyui-linkbutton" iconCls="icon-add" onclick="newBc()" title="添加"></a>
        </div>  -->
      </div>
    </div>
    <!-- 
    <div data-options="region:'center'" title="分类" style="border:0px">
    </div> -->
    <div data-options="region:'center',collapsible:false" title="频段" style="height:150px;border:0px;">
    </div>
  </div>
</div>
<div data-options="region:'center',split:false" title="基本信息" style="border:0px; padding:30px;padding-left:30px;">
  <div class="lineinput">
    <span>电台名称:</span><input class="easyui-textbox" type="text" id="bcTitle" name="bcTitle" data-options="required:true"></input>
  </div>
  <div class="lineinput">
    <span>所属集团:</span><input class="easyui-textbox" type="text" id="bcPublisher" name="bcPublisher" data-options="required:true"></input>
  </div>
  <div class="lineinput">
    <span>电台网址:</span><input class="easyui-textbox" type="text" id="bcUrl" name="bcUrl" data-options="required:true"></input>
  </div>
  <div class="lineinput">
    <span>所属地区:</span><input class="easyui-textbox" type="text" id="bcArea" name="bcArea" data-options="required:true"></input>
  </div>
  <div class="lineinput">
    <span>内容类别:</span><select name="cType" id="cType">
    <option value="dtType1">新闻</option>
<option value="dtType10">体育</option>
<option value="dtType2">财经</option>
<option value="dtType3">生活</option>
<option value="dtType4">交通</option>
<option value="dtType5">综艺</option>
<option value="dtType6">音乐</option>
<option value="dtType7">故事</option>
<option value="dtType8">民族</option>
<option value="dtType9">网络</option>
<option value="dtType99">其他</option>
    </select>
  </div>
  <div class="lineinput">
    <span style="line-height:80px;">电台说明:</span><textarea class="easyui-textbox" type="textarea" id="descn" name="descn" style="height:80px"></textarea>
  </div>
</div>
<div data-options="region:'south',split:false" style="height:40px;border:0px;border-top:1px solid #95B8E7;">
  <div class="easyui-layout" data-options="fit:true" style="border:none;align:center;text-align:center;padding-top:4px;">
    <a href="#" class="easyui-linkbutton" iconCls="icon-save" onclick="commit()">提交</a>&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="#" class="easyui-linkbutton" iconCls="icon-undo" onclick="cancel()">取消</a>
  </div>
</div>
</form>
</body>
<script>
var _type="";
$(function(){
  _type=getUrlParam(window.location.href, "type");
  if (_type=="new") document.title="新增";
  if (_type=="update") document.title="修改";
  $(body).resize();
});

function commit() {
  formData={};
  if (!bcTitle.value) {alert("请输入名称");return;}
  formData.bcTitle=bcTitle.value;
  if (!bcPublisher.value) {alert("请输入所属集团");return;}
  formData.bcPublisher=bcPublisher.value;
  formData.bcUrl=bcUrl.value;
  if (!bcArea.value) {alert("请输入所属地区");return;}
  formData.bcArea=bcArea.value;
  if (!cType.value) {alert("请输入分类");return;}
  formData.cType=cType.value;
  formData.descn=descn.value;
  if (!aUrl.value||!aSource.value) {alert("请输入直播流");return;}
  formData.bcLiveFlows=aUrl.value+"::"+aSource.value+";;";
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
</script>
</html>