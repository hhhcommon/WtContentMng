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
<title>电台列表</title>
</head>

<body class="easyui-layout" data-options="fit:true">

<div data-options="region:'west',split:true" style="width:320px;border:0px">
  <div class="easyui-layout" data-options="fit:true" style="border:none;">
    <div data-options="region:'center'" style="border:0px">
      <div id="lt_tab" class="easyui-tabs" data-options="fit:true" >
        <div title="内容分类" style="padding:10px">
          <div id="cataTree" class="easyui-layout" data-options="fit:true" >
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
  <div class="easyui-layout" data-options="fit:true" style="border:none;">
    <div data-options="region:'center'" style="border:0px">
      <div id="lt_tab" class="easyui-tabs" data-options="fit:true" >
        <div title="电台列表">
	      <table id="bcList" class="easyui-datagrid" data-options="fit:true,singleSelect:false,method:'get',toolbar:'#tb'"
	        fitColumns="true" pagination="true" style="border:0px;">
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

<div id="w" class="easyui-window" title="新增" data-options="modal:true,closed:true,collapsible:false,minimizable:false, maximizable:false"
  style="width:800px;height:600px;padding:0px;">
  <iframe id="addAndUpdate" src="" scrolling="no" frameborder="0" style="width:100%;height:100%;"></iframe>
</div>
</body>
<script>
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
  $.ajax({type:"post", async:true, url:'<%=path%>/bc/loadBcViewTree.do', dataType:"json",
    success: function(data) {
      curBcDataList=data;
      $('#bcList').datagrid("loadData", curBcDataList);
      selectsId="";
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
</script>
</html>