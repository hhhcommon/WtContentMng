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

<link rel="stylesheet" href="<%=path%>/resources/plugins/zTree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="<%=path%>/resources/plugins/zTree/js/jquery.ztree.core-3.5.js"></script>
<script type="text/javascript" src="<%=path%>/resources/plugins/zTree/js/jquery.ztree.excheck-3.5.js"></script>

<title>电台列表</title>
</head>

<body class="easyui-layout" data-options="fit:true">

<div data-options="region:'west',split:true" style="width:320px;border:0px">
  <div class="easyui-layout" data-options="fit:true" style="border:none;">
    <div data-options="region:'center'" style="border:0px">
      <div id="lt_tab" class="easyui-tabs" data-options="fit:true" >
        <div title="内容分类" style="padding:0px">
          <div id="cataTree" class="easyui-layout" style="overflow:auto;">
          </div>
        </div>
      </div>
    </div>
    <div data-options="region:'south',split:true" style="height:260px;border:0px;">
      <div id="lt_tab" class="easyui-tabs" data-options="fit:true,tabPosition:'bottom'" >
        <div title="属性" style="padding:10px"></div>
        <div title="统计" style="padding:10px"></div>
      </div>
    </div>
  </div>
</div>
<div data-options="region:'center'" style="padding-left:0px;border:0px;">
  <div class="easyui-layout" data-options="fit:true" style="border:0px;">
    <div data-options="region:'center'" style="border:0px">
      <div id="lt_tab" class="easyui-tabs" data-options="fit:true" >
        <div title="电台列表">
        <table id="bcList" class="easyui-datagrid" data-options="fit:true,singleSelect:false,method:'get',toolbar:'#tb'" fitColumns="true" pagination="true" style="border:0px;">
          <thead>
            <tr>
              <th data-options="field:'id',hidden:'true'">Id</th>
              <th data-options="field:'img',width:30,align:'center'">&nbsp;</th>
              <th data-options="field:'bcTitle',width:200,align:'center'">电台名称</th>
              <th data-options="field:'bcPublisher',width:200,align:'center'">所属集团</th>
              <th data-options="field:'bcUrl',width:180,align:'center'">电台网址</th>
              <th data-options="field:'bcSource',width:180,align:'center'">主来源</th>
              <th data-options="field:'flowURI',width:180,align:'center'">主直播流</th>
              <th data-options="field:'areaName',width:80,align:'center'">地区</th>
              <th data-options="field:'typeName',width:80,align:'center'">分类</th>
            </tr>
          </thead>
        </table>
        </div>
      </div>
    </div>
    <div data-options="region:'south',split:true" style="height:260px;border:0px;">
      <div id="lt_tab" class="easyui-tabs" data-options="fit:true,tabPosition:'bottom'" >
        <div title="属性" style="padding:10px"></div>
        <div title="节目单" style="padding:10px"></div>
        <div title="各地频段" style="padding:10px"></div>
        <div title="声音来源" style="padding:10px"></div>
        <div title="人工分类" style="padding:10px"></div>
        <div title="关键字" style="padding:10px"></div>
      </div>
    </div>
  </div>
</div>

<div id="tb">
  <a href="#" class="easyui-linkbutton" iconCls="icon-add" id="new" plain="true" onclick="newBc()">新增</a>
  <a href="#" class="easyui-linkbutton" iconCls="icon-edit" id="update" plain="true" onclick="editBc()">修改</a>
  <a href="#" class="easyui-linkbutton" iconCls="icon-remove" id="del" plain="true" onclick="delBc()">删除</a>
  <span style="width:200px; "></span>
  <input class="easyui-input" style="width:110px">
  <a href="#" class="easyui-linkbutton" iconCls="icon-search" id="search" plain="true">搜</a>
</div>

<!-- 新增或修改 -->
<div id="w" class="easyui-window" title="新增" data-options="modal:true,closed:true,collapsible:false,minimizable:false, maximizable:false, resizable:false"
  style="width:900px;height:600px;padding:0px; overflow:hidden;">
  <iframe id="addAndUpdate"  name="addAndUpdate" src="" scrolling="no" frameborder="0" style="width:100%;height:100%;"></iframe>
</div>

<!-- 树型选择窗口 -->
<div id="sw" class="easyui-window" title="选择" data-options="modal:true,closed:true,collapsible:false,minimizable:false, maximizable:false, resizable:false"
  style="width:320px;height:480px;padding:0px;">
  <div class="easyui-layout" data-options="fit:true" style="border:none;">
    <div class="easyui-layout" data-options="region:'center',split:false" style="overflow:auto; border:0px;padding:5px;padding-top:10px;">
      <ul id="selTree" class="ztree" style="overflow:auto;"/>
    </div>
    <div data-options="region:'south',split:false" style="height:40px;border:0px;border-top:1px solid #95B8E7;">
      <div class="easyui-layout" data-options="fit:true" style="border:none;align:center;text-align:center;padding-top:4px;">
        <a href="#" class="easyui-linkbutton" iconCls="icon-ok" onclick="selOk()">确定</a>&nbsp;&nbsp;&nbsp;&nbsp;
        <a href="#" class="easyui-linkbutton" iconCls="icon-cancel" onclick="selCancel()">取消</a>
      </div>
    </div>
  </div>
</div>

</body>
<script>
var zTreeObj;
var selectedNodes=null;//被选中的结点信息，在新增修改页面使用
var curSelId=null;

var curBcDataList=null;//当前列表中的数据
var selectsId=null;//当前选中的记录Id
$(function(){
  //调整表格样式和大小
  $(".datagrid-wrap").css("border","0px");
  $('#bcList').datagrid('resize');
  //表头粗体
  $($('.datagrid-header')[1]).find(".datagrid-header-row").find("div").each(function(i, ths){
    $(ths).css("font-weight", "bolder");
  });
  $("#w").window({
    onClose:function(){
      $("#addAndUpdate").attr("src", "");
    }
  });
  $("#sw").window({
    onClose:function(){
      $.fn.zTree.destroy("selTree");
    }
  });

  //表格控制
  $("#bcList").datagrid({
    onSelect:function(index,row) {
      var rows = $('#bcList').datagrid('getSelections');
      if (rows.length==0) {
        $("#update").linkbutton("disable");
        $("#del").linkbutton("disable");
      } else if (rows.length==1) {
        $("#update").linkbutton("enable");
        $("#del").linkbutton("enable");
      } else {
        $("#update").linkbutton("disable");
        $("#del").linkbutton("enable");
      }
      selectsId="";
      for(var i=0; i<rows.length; i++){
        var row = rows[i];
        selectsId+=","+row.id;
      }
      selectsId=selectsId.substring(1);
      //刷新详细页签
    },
    onUnselect:function(index,row) {
      var rows = $('#bcList').datagrid('getSelections');
      if (rows.length==0) {
        $("#update").linkbutton("disable");
        $("#del").linkbutton("disable");
      } else if (rows.length==1) {
        $("#update").linkbutton("enable");
        $("#del").linkbutton("enable");
      } else {
        $("#update").linkbutton("disable");
        $("#del").linkbutton("enable");
      }
      selectsId="";
      for(var i=0; i<rows.length; i++){
        var row = rows[i];
        selectsId+=","+row.id;
      }
      selectsId=selectsId.substring(1);
      //刷新详细页签
    }
  });
  $("#search").linkbutton("disable");
  $("#update").linkbutton("disable");
  $("#del").linkbutton("disable");

  $("#cataTree").tree({});
  loadData();//读取列表数据
});

//读取数据，并进行初始化
function loadData() {
  //读取树
  loadTree();
  //读取列表
  loadList();
}
function loadTree() {
  $.ajax({type:"post", async:true, url:'<%=path%>/bc/getCataTrees4View.do', dataType:"json",
    success: function(jsonData) {
      if (jsonData.jsonType==1) $('#cataTree').tree("loadData", jsonData.data);
      else $('#cataTree').html("没有数据");
    }
  });
}
//读取列表数据
function loadList() {
  $.ajax({type:"post", async:true, url:'<%=path%>/bc/loadBc.do', dataType:"json",
    success: function(data) {
      curBcDataList=data;
      $('#bcList').datagrid("loadData", curBcDataList);
      selectsId="";
    }
  });
}
function newBc() {
  $("#addAndUpdate").attr("src", "addupdate.jsp?type=new");
  $("#w").window({title:"新增"});
  $("#w").window("open");
}

function editBc() {
  alert("功能还未完成");
//  $("#addAndUpdate").attr("src", "addupdate.jsp?type=update&id=23123");
  //$("#w").window({title:"修改"});
  //$("#w").window("open");
}
function delBc() {
  delData={};
  delData.ids=selectsId;
  $.ajax({type:"post", async:true, data:delData, url:'<%=path%>/bc/delBc.do', dataType:"json",
    success: function(data) {
      alert("删除成功");
      loadList();
    }
  });
}

function openSel(label, multiple, cataId, selIds) {
  $("#sw").window({title:"选择"+(label?"["+label+"]":"")+(multiple==1?":多选":":单选")});
  $("#sw").window("open");
  selectedNodes=[];
  curSelId=cataId;
  param={}
  param.ids=selIds;
  param.cataId=cataId;
  $.ajax({type:"post", async:true, data:param, url:'<%=path%>/common/getCataTreeWithSel.do', dataType:"json",
    success: function(jsonData) {
      if (jsonData.jsonType==1) {
        setting = {
          view: { selectedMulti: false },
          check: { enable: true, chkStyle: "checkbox" }
        };
      	if (multiple==0) {
          setting = {
            view: { selectedMulti: false },
            check: { enable: true, chkStyle: "radio", radioType: "all" }
          };
      	}
        zTreeObj=$.fn.zTree.init($("#selTree"), setting, jsonData.data.children);
      }
    }
  });
}
function selCancel() {
  $("#sw").window("close");
}
function selOk() {
  zTreeObj= $.fn.zTree.getZTreeObj("selTree");
  var nodes = zTreeObj.getCheckedNodes(true);
  selectedNodes=[];
  for (var i=0; i<nodes.length; i++) selectedNodes[i]=nodes[i].attributes;
  if (curSelId==1) window.frames['addAndUpdate'].setCType(selectedNodes);
  if (curSelId==2) window.frames['addAndUpdate'].setBcArea(selectedNodes);
  $("#sw").window("close");
}
</script>
</html>