$(function(){
  var rootPath=getRootPath();
  var userId='0579efbaf9a9';//用户Id
  var pageSize='0';//0默认获取全部
  var current_page='0';//0默认获取全部
  var checkDiabled=false;//在新建角色或者编辑角色的时候不允许选中勾选框
  var saveType=1;//saveType=1默认新建角色的保存,saveType=2默认编辑角色的保存
  var otheropt=2;//otheropt=1默认此时正在新建或编辑,不允许进行其他操作,otheropt=2默认新建或编辑完成或取消

  /*得到角色列表*/
  var data0={"UserId":userId,
             "PCDType":"3",
             "PageSize":pageSize,
             "Page":current_page
  };
  getRoleList(data0);
  function getRoleList(dataParam){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/security/getRoleList.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(dataParam),
      beforeSend:function(){
        $(".ccl4").html("<div class='labels'>正在加载角色列表...</div>");
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          loadRoleList(resultData);
          $(".all_check").attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");          
        }else{
          $(".ccl4").html("<div class='labels'>得到角色列表失败:"+resultData.Message+"</div>");
        }
      },
      error:function(jqXHR){
        $(".ccl4").html("<div class='labels'>加载角色列表发生错误:"+jqXHR.status+"</div>");
      }
    });
  }
  function loadRoleList(resultData){
    $(".ccl4").html("");
    for(var i=0;i<resultData.ResultList.length;i++){
      var rolelist= '<li class="ccl4_list" roleType='+resultData.ResultList[i].roleType+'>'+
                      '<img src="../websiteManageResource/img/checkbox1.png" alt="" class="ccl4_img checkbox1 fl"/>'+
                      '<div class="ccl41 fl ellipsis" id='+resultData.ResultList[i].id+'>'+resultData.ResultList[i].roleName+'</div>'+
                      '<input type="text" class="ccl42 fl dis" value='+resultData.ResultList[i].roleName+'/>'+
                      '<button class="ccl43 fr">编辑</button>'+
                      '<div class="ccl44 fr dis">'+
                        '<button class="ccl45 fl">保存</button>'+
                        '<button class="ccl46 fl">取消</button>'+
                      '</div>'+
                    '</li>';
      $(".ccl4").append(rolelist);
    }
    $(".ccl4_list").eq(0).addClass("f5");
    var roleId=$(".ccl4_list").eq(0).children(".ccl41").attr("id");
    var data4={};
    data4.UserId=userId;
    data4.PCDType="3";
    data4.RoleId=roleId;
    getRoleFunlist(data4);
  }
  
  /*点击新建,新建角色*/
  $(".ccl23_div").on("click",function(){
    otheropt=1;//otheropt=1默认此时正在新建或编辑,不允许进行其他操作
    saveType=1//默认新建角色的保存
    checkDiabled=true;
    if($(this).hasClass("disabledstatus")){//当前处于不可新建的状态
      return;
    }else{
      $(this).addClass("disabledstatus").removeClass("ccl23_div1").attr("disabled","disabled");
      $(".ccl4 .ccl4_list .ccl43").attr("disabled","disabled").css({"color":"#ccc"});
      if($(".ccl4").children(".labels").length>0){//新建的是第一个角色
        $(".ccl4").html(" ");
      }
      $(document).find(".ccl4_list").each(function(){
        $(this).removeClass("f5");
      });
      if(typeof($(".ccr3_div3").attr("roleid"))!="undefined") $(".ccr3_div3").removeAttr("roleid");
      if(typeof($(".ccr3_div3").attr("channelids"))!="undefined") $(".ccr3_div3").removeAttr("channelids");  
      var rolelist= '<li class="ccl4_list f5">'+
                      '<img src="../websiteManageResource/img/checkbox1.png" alt="" class="ccl4_img fl checkbox1"/>'+
                      '<div class="ccl41 fl ellipsis dis"></div>'+
                      '<input type="text" class="ccl42 fl" value=""/>'+
                      '<button class="ccl43 fr dis">编辑</button>'+
                      '<div class="ccl44 fr">'+
                        '<button class="ccl45 fl">保存</button>'+
                        '<button class="ccl46 fl">取消</button>'+
                      '</div>'+
                    '</li>';
      $(".ccl4").append(rolelist);
      $(".ccr4_listdiv3").children(".ccr4_listdiv31").removeClass("dis");
      $(".ccr4_listdiv3").children(".ccr4_listdiv32").addClass("dis");
      var minheight=$(".ccr4_listdiv33").css("min-height");
      $(".ccr4_listdiv1,.ccr4_listdiv4").css({"line-height":minheight});
      $(".ccr4_listdiv3").css("height",minheight);
    }
  });
  
  /*点击编辑按钮,修改角色名称*/
  $(document).on("click",".ccl43",function(event){
    otheropt=1;
    saveType=2//默认编辑角色的保存
    checkDiabled=true;
    $(document).find(".ccl4_list").each(function(){
      $(this).removeClass("f5");
      $(this).children(".ccl41,.ccl43").removeClass("dis");
      $(this).children(".ccl42,.ccl44").addClass("dis");
    });
    var txt=$.trim($(this).siblings(".ccl41").text());
    $(this).parent(".ccl4_list").addClass("f5");
    $(this).siblings(".ccl41").addClass("dis");
    $(this).siblings(".ccl42").removeClass("dis").val(txt);
    $(this).addClass("dis").siblings(".ccl44").removeClass("dis");
    $(this).parent(".ccl4_list").siblings(".ccl4_list").children(".ccl43").attr("disabled","disabled").css({"color":"#ccc"});
    $(".ccl22_div").removeClass("ccl22_div1").attr("disabled","disabled");
    $(".ccl23_div").addClass("disabledstatus").removeClass("ccl23_div1").attr("disabled","disabled");
    event.stopPropagation();//阻止父元素的点击事件
    var roleId=$(this).siblings(".ccl41").attr("id");
    var data4={};
    data4.UserId=userId;
    data4.PCDType="3";
    data4.RoleId=roleId;
    getRoleFunlist(data4);
  });
  
  /*点击保存按钮,保存角色*/
  $(document).on("click",".ccl45",function(event){
    event.stopPropagation();//阻止父元素的点击事件
    var txt=$.trim($(this).parent(".ccl44").siblings(".ccl42").val());
    var _this=$(this);
    if(!txt||txt==''){
      alert("请输入角色名称");
      return;
    }
    if(saveType==1){//saveType=1默认新建角色的保存
      var data1={"UserId":userId,
                 "PCDType":"3",
                 "RoleName":txt
      };
      addRole(data1,_this);
    }else{//saveType=2默认编辑角色的保存
      var roleId=$(this).parent(".ccl44").siblings(".ccl41").attr("id");
      var data3={"UserId":userId,
                 "PCDType":"3",
                 "RoleId":roleId,
                 "RoleName":txt
      };
      modRole(data3,_this);
  }
  });
  
  /*新增角色的方法*/
  function addRole(dataParam,_this){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/security/addRole.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(dataParam),
      beforeSend:function(){
        $(_this).parent(".ccl44").children("button").attr("disabled","disabled").css({"background":"#ccc"});
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          alert("新增角色成功"); 
          otheropt=2;//otheropt=2默认新建或编辑完成或取消
          checkDiabled=false;
          $(".ccl22_div").removeClass("ccl22_div1").attr("disabled","disabled");
          $(".ccl23_div").removeClass("disabledstatus").addClass("ccl23_div1").removeAttr("disabled");
          $(_this).parent(".ccl44").children("button").removeAttr("disabled").css({"background":"#0077c7"});
          getRoleList(data0);//刷新角色列表
        }else{
          alert("新增角色失败:"+resultData.Message);
          $(_this).parent(".ccl44").children("button").removeAttr("disabled").css({"background":"#0077c7"});
        }
      },
      error:function(jqXHR){
        alert("新增角色发生错误:"+jqXHR.status);
        $(_this).parent(".ccl44").children("button").removeAttr("disabled").css({"background":"#0077c7"});
      }
    });
  }
  
  /*修改角色的方法*/
  function modRole(dataParam,_this){
    $.ajax({
      type:"POST",
      url:rootPath+"CM/security/updateRole.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(dataParam),
      beforeSend:function(){
        $(_this).parent(".ccl44").children("button").attr("disabled","disabled").css({"background":"#ccc"});
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          alert("修改角色成功");
          otheropt=2;//otheropt=2默认新建或编辑完成或取消
          checkDiabled=false;
          $(".ccl22_div").removeClass("ccl22_div1").attr("disabled","disabled");
          $(".ccl23_div").removeClass("disabledstatus").addClass("ccl23_div1").removeAttr("disabled");
          $(_this).parent(".ccl44").children("button").removeAttr("disabled").css({"background":"#0077c7"});
          getRoleList(data0);//刷新角色列表
        }else{
          alert("修改角色失败:"+resultData.Message);
          $(_this).parent(".ccl44").children("button").removeAttr("disabled").css({"background":"#0077c7"});
        }
      },
      error:function(jqXHR){
        alert("修改角色发生错误:"+jqXHR.status);
        $(_this).parent(".ccl44").children("button").removeAttr("disabled").css({"background":"#0077c7"});
      }
    });
  }
  
  /*点击取消按钮,取消目前正在进行的事情*/
  $(document).on("click",".ccl46",function(event){
    event.stopPropagation();//阻止父元素的点击事件
    otheropt=2;//otheropt=2默认新建或编辑完成或取消
    checkDiabled=false;
    var txt=$.trim($(this).parent(".ccl44").siblings(".ccl42").val());
    if(typeof($(this).parent(".ccl44").siblings(".ccl41").attr("id"))=="undefined"){//取消正在新建的角色
      $(this).parent(".ccl44").parent(".ccl4_list").remove();
    }else{//取消编辑已有的角色
      $(this).parent(".ccl44").removeClass("dis");
      $(this).parent(".ccl44").siblings(".ccl41").removeClass("dis").text(txt);
      $(this).parent(".ccl44").siblings(".ccl42").addClass("dis");
      $(this).parent(".ccl44").siblings(".ccl43").removeClass("dis");
      $(this).parent(".ccl44").addClass("dis");
    }
    $(document).find(".ccl4_list").each(function(i){
      $(this).removeClass("f5");
    });
    $(".ccl4_list").eq(0).addClass("f5");
    $(".ccl4 .ccl4_list .ccl43").removeAttr("disabled").css({"color":"#0077C7"});
    $(".ccl22_div").removeClass("ccl22_div1").attr("disabled","disabled");
    $(".ccl23_div").removeClass("disabledstatus").addClass("ccl23_div1").removeAttr("disabled");
    var roleId=$(".ccl4_list").eq(0).children(".ccl41").attr("id");
    var data4={};
    data4.UserId=userId;
    data4.PCDType="3";
    data4.RoleId=roleId;
    getRoleFunlist(data4);
  });
  
  /*选择勾选框--角色的删除*/
  //点击全选
  $(document).on("click",".all_check",function(){
    if(!checkDiabled){//此时允许选中角色进行删除
      var ll=$(".ccl4").has(".ccl4_list").length;
      if(ll==true){
        var l=$(".ccl4 .ccl4_list .ccl4_img").length;
        if($(this).hasClass("checkbox1")){
          $(this).attr({"src":"../websiteManageResource/img/checkbox2.png"}).removeClass("checkbox1");
          $(".ccl4 .ccl4_list .ccl4_img").attr({"src":"../websiteManageResource/img/checkbox2.png"}).removeClass("checkbox1");
          $(document).find(".ccl4_list").each(function(){
            $(this).children(".ccl43").attr("disabled","disabled").css({"color":"#ccc"});
          });
          $(".ccl22_div").addClass("ccl22_div1").removeAttr("disabled");
          $(".ccl23_div").addClass("disabledstatus").removeClass("ccl23_div1").attr("disabled","disabled");
        }else{
          $(this).attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");
          $(".ccl4 .ccl4_list .ccl4_img").attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");
          $(document).find(".ccl4_list").each(function(){
            $(this).children(".ccl43").removeAttr("disabled").css({"color":"#0077C7"});
          });
          $(".ccl4_list").eq(0).addClass("f5");
          $(".ccl22_div").removeClass("ccl22_div1").attr("disabled","disabled");
          $(".ccl23_div").removeClass("disabledstatus").addClass("ccl23_div1").removeAttr("disabled");
        }
      }
    }else{
      alert("完成上一步的操作后,才能进行当前操作");
      return;
    }
  });
  
  //点击单个勾选框
  $(document).on("click",".ccl4_img",function(event){
    if(!checkDiabled){//此时允许选中角色进行删除
      var num=0;//记录当前选中的勾选框数量
      var l=$(".ccl4 .ccl4_list .ccl4_img").length;
      if($(this).hasClass("checkbox1")){
        $(this).attr({"src":"../websiteManageResource/img/checkbox2.png"}).removeClass("checkbox1");
        $(".ccl22_div").addClass("ccl22_div1").removeAttr("disabled");
        $(document).find(".ccl4_list").each(function(){
          $(this).children(".ccl43").attr("disabled","disabled").css({"color":"#ccc"});
        });
        $(".ccl23_div").addClass("disabledstatus").removeClass("ccl23_div1").attr("disabled","disabled");
        $(".ccl4 .ccl4_list .ccl4_img").each(function(){//是否选中全选
          if($(this).hasClass("checkbox1")) return;
          else num++;
        });
        if(num==l) $(".all_check").removeClass("checkbox1").attr({"src":"../websiteManageResource/img/checkbox2.png"});
      }else{
        $(this).attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");
        $(".ccl4 .ccl4_list .ccl4_img").each(function(){//是否选中全选
          if($(this).hasClass("checkbox1")) return;
          else num++;
        });
        if(num!=l) $(".all_check").addClass("checkbox1").attr({"src":"../websiteManageResource/img/checkbox1.png"});
        if(num==0){//没有被勾选的角色
          $(".ccl22_div").removeClass("ccl22_div1").attr("disabled","disabled");
          $(document).find(".ccl4_list").each(function(){
            $(this).children(".ccl43").removeAttr("disabled").css({"color":"#0077C7"});
          });
          $(".ccl23_div").removeClass("disabledstatus").addClass("ccl23_div1").removeAttr("disabled");
        }
      }
    }else{
      alert("完成上一步的操作后,才能进行当前操作");
      return;
    }
    event.stopPropagation();//阻止父元素的点击事件
  });
   
  //点击删除按钮,删除角色
  $(".ccl22_div").on("click",function(){
    var roleIds='';
    $(".ccl4 .ccl4_list .ccl4_img").each(function(){
      if($(this).hasClass("checkbox1")){

      }else{
        if(roleIds=='') roleIds=$(this).siblings(".ccl41").attr("id");
        else roleIds+=","+$(this).siblings(".ccl41").attr("id");
      }
    })
    var data2={"UserId":userId,
               "PCDType":"3",
               "RoleId":roleIds
    };
    $.ajax({
      type:"POST",
      url:rootPath+"CM/security/delRole.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(data2),
      beforeSend:function(){
        $(".ccl22_div").removeClass("ccl22_div1").attr("disabled","disabled");
        $(".ccl23_div").addClass("disabledstatus").removeClass("ccl23_div1").attr("disabled","disabled");
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          alert('删除角色成功');
          getRoleList(data0);//刷新角色列表
          $(".ccl23_div").removeClass("disabledstatus").addClass("ccl23_div1").removeAttr("disabled");
        }else{
          alert("删除角色失败"+resultData.Message);
          $(".ccl23_div").removeClass("disabledstatus").addClass("ccl23_div1").removeAttr("disabled");
        }
      },
      error:function(jqXHR){
        alert("删除角色发生错误"+jqXHR.status);
        $(".ccl23_div").removeClass("disabledstatus").addClass("ccl23_div1").removeAttr("disabled");
      }
    });
  });
  
  /*编辑角色时取消input的focus,click对父元素的影响*/
  $(document).on("focus",".ccl42",function(event){
    event.stopPropagation();//阻止父元素的点击事件
  });
  $(document).on("click",".ccl42",function(event){
    event.stopPropagation();//阻止父元素的点击事件
  });
  
  /*点击展开/收起全部栏目*/
  $(".ccr4_listdiv34").on("click",function(){
    if(otheropt==1){
      alert("完成上一步的操作后,才能进行当前操作");
      return false;
    }else{
      if($(this).text()=="展开全部栏目"){
        $(this).text("收起全部栏目");
        var maxheight=$(".ccr4_listdiv33").css("max-height");
        $(".ccr4_listdiv1,.ccr4_listdiv4").css({"line-height":maxheight});
        $(".ccr4_listdiv3").css("height",maxheight);
        var rootNode=roleObj.getNodeByParam("id","top01", null);
        roleObj.expandNode(rootNode,true,false,false);
      }else{
        $(this).text("展开全部栏目");
        var minheight=$(".ccr4_listdiv33").css("min-height");
        $(".ccr4_listdiv1,.ccr4_listdiv4").css({"line-height":minheight});
        $(".ccr4_listdiv3").css("height",minheight);
        var rootNode=roleObj.getNodeByParam("id","top01", null);
        roleObj.expandNode(rootNode,false,false,false);
      }
    }
  });

  /*点击根节点展开节点*/
  function onExpand(event,treeId,treeNode){
    if(otheropt==1){
      alert("完成上一步的操作后,才能进行当前操作");
      return false;
    }else{
      if(treeNode.id=="top01"){//点击的是根节点
        if($(".ccr4_listdiv34").text()=="展开全部栏目"){
          $(".ccr4_listdiv34").text("收起全部栏目");
          var maxheight=$(".ccr4_listdiv33").css("max-height");
          $(".ccr4_listdiv1,.ccr4_listdiv4").css({"line-height":maxheight});
          $(".ccr4_listdiv3").css("height",maxheight);
          var rootNode=roleObj.getNodeByParam("id","top01", null);
          roleObj.expandNode(rootNode,true,false,false);
        }else{
          $(".ccr4_listdiv34").text("展开全部栏目");
          var minheight=$(".ccr4_listdiv33").css("min-height");
          $(".ccr4_listdiv1,.ccr4_listdiv4").css({"line-height":minheight});
          $(".ccr4_listdiv3").css("height",minheight);
          var rootNode=roleObj.getNodeByParam("id","top01", null);
          roleObj.expandNode(rootNode,false,false,false);
        }
      }else{
        return false;
      }
    }
  }
  /*点击根节点折叠节点*/
  function onCollapse(event,treeId,treeNode){
    if(otheropt==1){
      alert("完成上一步的操作后,才能进行当前操作");
      return false;
    }else{
      if(treeNode.id=="top01"){//点击的是根节点
        if($(".ccr4_listdiv34").text()=="展开全部栏目"){
          $(".ccr4_listdiv34").text("收起全部栏目");
          var maxheight=$(".ccr4_listdiv33").css("max-height");
          $(".ccr4_listdiv1,.ccr4_listdiv4").css({"line-height":maxheight});
          $(".ccr4_listdiv3").css("height",maxheight);
          var rootNode=roleObj.getNodeByParam("id","top01", null);
          roleObj.expandNode(rootNode,true,false,false);
        }else{
          $(".ccr4_listdiv34").text("展开全部栏目");
          var minheight=$(".ccr4_listdiv33").css("min-height");
          $(".ccr4_listdiv1,.ccr4_listdiv4").css({"line-height":minheight});
          $(".ccr4_listdiv3").css("height",minheight);
          var rootNode=roleObj.getNodeByParam("id","top01", null);
          roleObj.expandNode(rootNode,false,false,false);
        }
      }else{
        return false;
      }
    }
  }

  /*获取默认选中角色的权限*/
  function getRoleFunlist(dataParam){
    $(".ccr3_div3").removeAttr("roleid,channelids");
    $.ajax({
      type:"POST",
      url:rootPath+"CM/security/getRoleFunlist.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(dataParam),
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          if(resultData.RoleFun&&resultData.RoleFun!=null){
            $(".ccr3_div3").attr("roleid",resultData.RoleFun.roleId);
            if(resultData.RoleFun.objId&&resultData.RoleFun.objId!=null){
              $(".ccr3_div3").attr("channelids",resultData.RoleFun.objId);
              loadRoleFunlist(resultData);//加载角色权限列表
              $(".ccr4_listdiv32").removeClass("dis");
              $(".ccr4_listdiv31").addClass("dis");
            }else{
              $(".ccr3_div3").removeAttr("channelids");
              $(".ccr4_listdiv31").removeClass("dis");
              $(".ccr4_listdiv32").addClass("dis");
            }
          }
        }else{
          $(".ccr4_listdiv31").removeClass("dis");
          $(".ccr4_listdiv32").addClass("dis");
        }
      },
      error:function(jqXHR){
        alert("得到角色权限列表发生错误"+jqXHR.status);
      }
    });
  }
  function loadRoleFunlist(resultData){
    var _url=rootPath+"CM/baseinfo/getChannelTree4View.do";
    var loadRoleData=[{ChannelId:"",TreeViewType:"zTree"}];
    var chids=resultData.RoleFun.objId.split(",");
    $.ajax({
      type:"POST",    
      url:_url,
      dataType:"json",
      data:JSON.stringify(loadRoleData),
      success:function(jsonData){
        if(jsonData.ReturnType=="1001"){
          roleObj=$.fn.zTree.init($("#role_tree"), settingRole);
          roleObj.addNodes(null,jsonData.Data.children[0],false);
          for(var i=0;i<chids.length;i++){
            var node=roleObj.getNodeByParam("id",chids[i], null);
            roleObj.checkNode(node,true,false,false);
          }
          var allNodes=roleObj.getNodes();
          allNodes=roleObj.transformToArray(allNodes);
          for(var i=0;i<allNodes.length;i++){
            roleObj.setChkDisabled(allNodes[i],true);
          }
          var rootNode=roleObj.getNodeByParam("id","top01", null);
          rootNode.name="全部栏目";
          roleObj.updateNode(rootNode);
          if($(".ccr4_listdiv34").text()=="展开全部栏目"){
            var minheight=$(".ccr4_listdiv33").css("min-height");
            $(".ccr4_listdiv1,.ccr4_listdiv4").css({"line-height":minheight});
            $(".ccr4_listdiv3").css("height",minheight);
            var rootNode=roleObj.getNodeByParam("id","top01", null);
            roleObj.expandNode(rootNode,false,false,false);
          }else{
            var maxheight=$(".ccr4_listdiv33").css("max-height");
            $(".ccr4_listdiv1,.ccr4_listdiv4").css({"line-height":maxheight});
            $(".ccr4_listdiv3").css("height",maxheight);
            var rootNode=roleObj.getNodeByParam("id","top01", null);
            roleObj.expandNode(rootNode,true,false,false);
          }
        }
      },
      error:function(jqXHR){
        alert("加载栏目树发生错误" + jqXHR.status);
      }
    });
  }
  
  /*点击选中其他角色,获取其权限*/
  $(document).on("click",".ccl4_list",function(){
    if(otheropt==1){//otheropt=1默认此时正在新建或编辑,不允许进行其他操作
      if($(this).hasClass("f5")){
        return false;
      }else{
        alert("完成上一步的操作后,才能进行当前操作");
        return false;
      }
    }else{
      if($(".ccr4_listdiv34").text()=="收起全部栏目"){
        $(".ccr4_listdiv34").text("展开全部栏目");
        var minheight=$(".ccr4_listdiv33").css("min-height");
        $(".ccr4_listdiv1,.ccr4_listdiv4").css({"line-height":minheight});
        $(".ccr4_listdiv3").css("height",minheight);
        var rootNode=roleObj.getNodeByParam("id","top01", null);
        roleObj.expandNode(rootNode,false,false,false);
      }
      $(this).addClass("f5").siblings(".ccl4_list").removeClass("f5");
      var roleId=$(this).children(".ccl41").attr("id");
      var data4={};
      data4.UserId=userId;
      data4.PCDType="3";
      data4.RoleId=roleId;
      getRoleFunlist(data4);
    }
  });
  
  /*s--获取角色权限的栏目树*/
  var roleObj;
  //配置角色权限的栏目树的相关参数
  var settingRole={
    view:{
      selectedMulti:false//是否允许同时选中多个节点
    },
    data:{
      simpleData:{
        enable: true
      }
    },
    check:{
      enable:true,
      chkStyle:"checkbox",
      chkboxType:{ "Y": "", "N": "" }
    },
    treeNode:{
      chkDisabled:true,
      checked:true
    },
    callback:{
      onExpand:onExpand,
      onCollapse:onCollapse
    }
  };
  /*e--获取角色权限的栏目树*/
  
  /*s--点击设置权限,对角色设置权限*/
  var modalObj;
  //配置模态框上树的相关参数
  var settingModal={
    view:{
      selectedMulti:false//是否允许同时选中多个节点
    },
    data:{
      simpleData:{
        enable: true
      }
    },
    check:{
      enable:true,
      chkStyle:"checkbox",
      chkboxType:{"Y":"","N":""}
    },
    treeNode:{
      chkDisabled:true,
      checked:true
    },
    callback:{
      onCheck:modalonCheck  
    }
  };
  
  //加载模态框上面的树
  function loadModalTree(roleId){
    var _url=rootPath+"CM/baseinfo/getChannelTree4View.do";
    var loadModalData=[{ChannelId:"",TreeViewType:"zTree"}];
    $.ajax({
      type:"POST",    
      url:_url,
      dataType:"json",
      data:JSON.stringify(loadModalData),
      success:function(jsonData){
        if(jsonData.ReturnType=="1001"){
          modalObj=$.fn.zTree.init($("#modal_tree"), settingModal);
          modalObj.addNodes(null,jsonData.Data.children[0],false);
          var rootNode=modalObj.getNodeByParam("id","top01", null);
          rootNode.name="全部栏目";
          modalObj.updateNode(rootNode);
          if(typeof($(".power").attr("channelids"))!="undefined"){
            var channelids=$(".power").attr("channelids").split(",");
            for(var i=0;i<channelids.length;i++){
              var modalnode=modalObj.getNodeByParam("id",channelids[i], null);
              modalObj.checkNode(modalnode,true,false,false);
            }
          } 
        }
      },
      error:function(jqXHR){
        alert("加载栏目树发生错误" + jqXHR.status);
      }
    });
  }
  
  //选中模态框栏目树的checkbox
  function modalonCheck(event,treeId,treeNode){
    if(treeNode.level=='0'){//点击的是全部栏目的勾选框
      if(treeNode.checked==true) modalObj.checkAllNodes(true);
      else modalObj.checkAllNodes(false);
    }else{//点击的是非全部栏目的勾选框
      if(treeNode.checked==true) modalObj.checkNode(treeNode,true,false);
      else modalObj.checkNode(treeNode,false,false);
    }  
  }
  
  //点击配置权限
  $(document).on("click",".ccr4_listdiv4",function(){
    if(otheropt==1){
      alert("完成上一步的操作后,才能进行当前操作");
      return false;    
    }else{
      $("#modal_tree").html("");//清空模态框树的内容
      var roleid=$(".ccr3_div3").attr("roleid");
      var channelids=$(".ccr3_div3").attr("channelids");
      if(typeof(roleid)!="undefined") $(".power_mask").removeClass("dis").children(".power").attr({"roleid":roleid});
      if(typeof(channelids)!="undefined") $(".power_mask").children(".power").attr({"channelids":channelids});
      else $(".power_mask").children(".power").removeAttr("channelids");
      loadModalTree(roleid);
    }
  });
  
  //点击保存配置
  $(".power_footer1").on("click",function(){
    var roleId=$(this).parent(".power_footer").parent(".power").attr("roleid");
    var modalnodes=modalObj.getCheckedNodes(true);
    var channelIds='';//选中栏目id的集合
    for(var i=0;i<modalnodes.length;i++){
      if(channelIds=='') channelIds=modalnodes[i].id;
      else channelIds+=','+modalnodes[i].id;
    }
    var data5={};
    data5.UserId=userId;
    data5.PCDType="3";
    data5.RoleId=roleId;
    data5.FunName="栏目权限";
    data5.FunClass="1";
    data5.FunType="Channel-Add";
    data5.ObjId=channelIds;
    $.ajax({
      type:"POST",    
      url:rootPath+"CM/security/setRoleFun.do",
      dataType:"json",
      data:JSON.stringify(data5),
      beforeSend:function(){
        $(".power_footer").children(".power_footer1").css({"background":"#fff","color": "#000","border":"1px solid #dedede"}).attr("disabled","disabled");
      },
      success:function(returnData){
        if(returnData.ReturnType=="1001"){
          alert("设置角色的权限成功");
          otheropt=2;
          $(".power_mask,.ccr4_listdiv31").addClass("dis");
          var data4={};
          data4.UserId=userId;
          data4.PCDType="3";
          data4.RoleId=data5.RoleId;
          getRoleFunlist(data4);
        }else{
          alert("设置角色的权限失败:"+returnData.Message);
        }
        $(".power_footer").children(".power_footer1").css("background","#0077c7").removeAttr("disabled");
      },
      error:function(jqXHR){
        alert("设置角色的权限发生错误:" + jqXHR.status);
        $(".power_footer").children(".power_footer1").css("background","#0077c7").removeAttr("disabled");
      }
    });
  });
  
  //点击取消或关闭
  $(".power_footer2,.power_title2").on("click",function(){
    $(".power_mask").addClass("dis");
    otheropt=2;
  })
  /*e--点击设置权限,对角色设置权限*/

});
