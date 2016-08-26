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
    <div data-options="region:'south',split:true" style="height:240px;border:0px;">
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
              <th data-options="field:'bcTitle',width:200,align:'left'">电台名称</th>
              <th data-options="field:'bcPublisher',width:200,align:'left'">所属集团</th>
              <th data-options="field:'bcUrl',width:180,align:'left'">电台网址</th>
              <th data-options="field:'bcSource',width:180,align:'left'">主来源</th>
              <th data-options="field:'flowURI',width:180,align:'left'">主直播流</th>
              <th data-options="field:'areaName',width:80,align:'center'">地区</th>
              <th data-options="field:'typeName',width:80,align:'left'">分类</th>
            </tr>
          </thead>
        </table>
        </div>
      </div>
    </div>
    <div data-options="region:'south',split:true" style="height:240px;border:0px;">
      <div id="lt_tab" class="easyui-tabs" data-options="fit:true,tabPosition:'bottom'">
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

<!-- 新增或修改时 树型选择窗口 -->
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

var curPageSize=null;//当前页尺寸
var curPageNum=null;//当前页码

$(function(){
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

  //表格控制     记录被选中时，可作的操作
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
  
  //调整表格样式和大小
  $(".datagrid-wrap").css("border","0px");
  $('#bcList').datagrid('resize');
  $($('.datagrid-header')[1]).find(".datagrid-header-row").find("div").each(function(i, ths) {
    $(ths).css({"font-weight":"bold","text-align":"center"});
  });

  $("#search").linkbutton("disable");
  $("#update").linkbutton("disable");
  $("#del").linkbutton("disable");

  $("#cataTree").tree({});
  loadData();//读取列表数据

  var rId=null,mId=null;  //将分类ID传递到分页方法中，以便和分页内容一致
  $('#cataTree').tree({
        onClick: function(node){
            //alert(allFields(node));
            //alert(allFields(node.attributes));
            //alert(node.id);
            //alert(node.attributes.mId);
            //点击的如果是2大类根节点时，查询所有；如果点击的下面的分类则按分类查询
            if(node.id=='1' || node.id=='2'){
              loadList(1, $('#bcList').datagrid('getPager').pagination('options').pageSize);
              rId=null;
              mId=null;
            }else{
              loadList(1, $('#bcList').datagrid('getPager').pagination('options').pageSize,node.id,node.attributes.mId);
              rId=node.id;
              mId=node.attributes.mId;
            }
        }
    });
  //分页
  $('#bcList').datagrid('getPager').pagination({
    pageSize: 15,
    pageList: [15, 30, 50],
    onSelectPage: function (pageNumber, pageSize){
        loadList($('#bcList').datagrid('getPager').pagination('options').pageNumber, $('#bcList').datagrid('getPager').pagination('options').pageSize,rId,mId);
    }
  });
});

//分页方法
function fy(pageNumber, pageSize){
  loadList($('#bcList').datagrid('getPager').pagination('options').pageNumber, $('#bcList').datagrid('getPager').pagination('options').pageSize);
}
//读取数据，并进行初始化
function loadData() {
  //读取树
  loadTree();
  //读取列表
  //loadList(1, $('#bcList').datagrid('getPager').pagination('options').pageSize);
}

function loadTree() {
  $.ajax({type:"post", async:true, url:'<%=path%>/bc/getCataTrees4View.do', dataType:"json",
    success: function(jsonData) {
      if (jsonData.jsonType==1) $('#cataTree').init("loadData", jsonData.data);
      else $('#cataTree').html("没有数据");
    }
  });
}
//读取列表数据
function loadList(pageNum, pageSize,rId,mId) {
  $("#search").linkbutton("disable");
  $("#update").linkbutton("disable");
  $("#del").linkbutton("disable");
  $("#new").linkbutton("disable");
  var param={};
  if (!pageNum&&!pageSize) {
    param.pageNumber=curPageNum?curPageNum:1;
    param.pageSize=curPageSize?curPageSize:$('#bcList').datagrid('getPager').pagination('options').pageSize;
  } else {
    param.pageNumber=pageNum?pageNum:1;
    param.pageSize=pageSize?pageSize:$('#bcList').datagrid('getPager').pagination('options').pageSize;
  }
  
  //点击分类时增加分类名称参数
  if(rId!=null){
      param.rId=rId;
      param.mId=mId;
  }
 
  curPageNum=param.pageNumber;
  curPageSize=param.pageSize;

  $.ajax({type:"post", async:true, data:param, url:'<%=path%>/bc/loadBc.do', dataType:"json",
    success: function(data) {
      $('#bcList').datagrid("loadData", data.result);
      $('#bcList').find('datagrid-header-row').find("datagrid-cell").css('text-align','center')
      selectsId="";
      $("#new").linkbutton("enable");
      $('#bcList').datagrid('getPager').pagination('refresh',{total:data.dataCount, pageNumber:param.pageNumber});
    }
  });
}

//新增
function newBc() {
  $("#addAndUpdate").attr("src", "addupdate.jsp?type=new");
  $("#w").window({title:"新增"});
  $("#w").window("open");
}

//修改
function editBc() {
  $("#addAndUpdate").attr("src", "addupdate.jsp?type=update&bcId="+selectsId);
  $("#w").window({title:"修改"});
  $("#w").window("open");
}

//删除
function delBc() {
  $("#search").linkbutton("disable");
  $("#update").linkbutton("disable");
  $("#del").linkbutton("disable");
  $("#new").linkbutton("disable");
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
  var param={}
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
        //设置选中
        if (selIds) {
          var ids=selIds.split(",");
          var findNode=null;
          for (var i=0; i<ids.length; i++) {
            findNode=zTreeObj.getNodeByParam("id", ids[i]);
            if (findNode) zTreeObj.checkNode(findNode, true, true, true);
          }
        }
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
  if (curSelId==1) window.frames['addAndUpdate'].setCType(nodes);
  if (curSelId==2) window.frames['addAndUpdate'].setBcArea(nodes);
  $("#sw").window("close");
}
</script>
</html>