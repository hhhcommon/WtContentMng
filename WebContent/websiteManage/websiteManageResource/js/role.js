$(function(){
  var rootPath=getRootPath();
  var userId='0579efbaf9a9';//用户Id
  var pageSize='0';//0默认获取全部
  var current_page='0';//0默认获取全部
  var checkDiabled=false;//在新建角色或者编辑角色的时候不允许选中勾选框
  var saveType=1;//saveType=1默认新建角色的保存,saveType=2默认编辑角色的保存

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
      url:rootPath+"security/getRoleList.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(dataParam),
      beforeSend:function(){
        $(".ccl4").html("<div class='labels'>正在加载角色列表...</div>");
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          loadRoleList(resultData);
        }else{
          $(".ccl4").html("<div class='labels'>没有角色列表</div>");
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
  }
  
  /*点击新建,新建角色*/
  $(".ccl12_div").on("click",function(){
    saveType=1//默认新建角色的保存
    checkDiabled=true;
    if($(this).hasClass("disabledstatus")){//当前处于不可新建的状态
      return;
    }else{
      $(this).addClass("disabledstatus");
      $(this).children(".ccl13_div").removeClass("ccl13_div1");
      $(this).children(".ccl14_div").removeClass("ccl14_div1")
      $(".ccl4 .ccl4_list .ccl43").attr("disabled","disabled").css({"color":"#ccc"});
      if($(".ccl4").children(".labels").length>0){//新建的是第一个角色
        $(".ccl4").html(" ");
      }
      $(document).find(".ccl4_list").each(function(){
        $(this).removeClass("f5");
      });
      var rolelist= '<li class="ccl4_list f5">'+
                      '<img src="../websiteManageResource/img/checkbox1.png" alt="" class="ccl4_img checkbox1 fl"/>'+
                      '<div class="ccl41 fl ellipsis dis"></div>'+
                      '<input type="text" class="ccl42 fl" value=""/>'+
                      '<button class="ccl43 fr dis">编辑</button>'+
                      '<div class="ccl44 fr">'+
                        '<button class="ccl45 fl">保存</button>'+
                        '<button class="ccl46 fl">取消</button>'+
                      '</div>'+
                    '</li>';
      $(".ccl4").append(rolelist);
    }
  });
  
  /*点击编辑按钮*/
  $(document).on("click",".ccl43",function(){
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
    $(this).parent(".ccl4_list").siblings().children(".ccl43").attr("disabled","disabled").css({"color":"#ccc"});
    $(".ccl12_div").addClass("disabledstatus");
    $(".ccl12_div").children(".ccl13_div").removeClass("ccl13_div1");
    $(".ccl12_div").children(".ccl14_div").removeClass("ccl14_div1");
  });
  
  /*点击保存按钮*/
  $(document).on("click",".ccl45",function(){
    debugger;
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
                 "roleId":roleId,
                 "RoleName":txt
      };
      modRole(data3,_this);
    }
  });
  
  /*新增角色*/
  function addRole(dataParam,_this){
    $.ajax({
      type:"POST",
      url:rootPath+"security/addRole.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(dataParam),
      beforeSend:function(){
        $(_this).parent(".ccl44").children("button").attr("disabled","disabled").css({"background":"#ccc"});
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          checkDiabled=false;
          $(".ccl12_div").removeClass("disabledstatus");
          $(".ccl12_div").children(".ccl13_div").addClass("ccl13_div1");
          $(".ccl12_div").children(".ccl14_div").addClass("ccl14_div1");
          getRoleList(data0);//刷新角色列表
        }else{
          $(_this).parent(".ccl44").children("button").removeAttr("disabled").css({"background":"##0077c7"});
        }
      },
      error:function(jqXHR){
        $(_this).parent(".ccl44").children("button").removeAttr("disabled").css({"background":"##0077c7"});
      }
    });
  }
  
  /*修改角色*/
  function modRole(dataParam,_this){
    $.ajax({
      type:"POST",
      url:rootPath+"security/updateRole.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(dataParam),
      beforeSend:function(){
        $(_this).parent(".ccl44").children("button").attr("disabled","disabled").css({"background":"#ccc"});
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          checkDiabled=false;
          $(".ccl12_div").removeClass("disabledstatus");
          $(".ccl12_div").children(".ccl13_div").addClass("ccl13_div1");
          $(".ccl12_div").children(".ccl14_div").addClass("ccl14_div1");
          getRoleList(data0);//刷新角色列表
        }else{
          $(_this).parent(".ccl44").children("button").removeAttr("disabled").css({"background":"##0077c7"});
        }
      },
      error:function(jqXHR){
        $(_this).parent(".ccl44").children("button").removeAttr("disabled").css({"background":"##0077c7"});
      }
    });
  }
  
  /*点击取消按钮*/
  $(document).on("click",".ccl46",function(){
    checkDiabled=false;
    $(document).find(".ccl4_list").each(function(){
      $(this).removeClass("f5");
    });
    var txt=$.trim($(this).parent(".ccl44").siblings(".ccl42").val());
    if(typeof($(this).parent(".ccl44").siblings(".ccl41").attr("id"))=="undefined"){//取消正在新建的角色
      $(this).parent(".ccl44").parent(".ccl4_list").remove();
    }else{//取消编辑已有的角色
      
    }
    $(this).parent(".ccl44").parent(".ccl4_list").addClass("f5");
    $(this).parent(".ccl44").removeClass("dis");
    $(this).parent(".ccl44").siblings(".ccl41").removeClass("dis").text(txt);
    $(this).parent(".ccl44").siblings(".ccl42").addClass("dis");
    $(this).parent(".ccl44").siblings(".ccl43").removeClass("dis");
    $(this).parent(".ccl44").addClass("dis");
    $(".ccl4 .ccl4_list .ccl43").removeAttr("disabled").css({"color":"#0077C7"});
    $(".ccl12_div").removeClass("disabledstatus");
    $(".ccl12_div").children(".ccl13_div").addClass("ccl13_div1");
    $(".ccl12_div").children(".ccl14_div").addClass("ccl14_div1");
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
          $(".ccl22_div").removeAttr("disabled").css({"background":"#0077C7","color":"#fff"});
          $(document).find(".ccl4_list").each(function(){
            $(this).children(".ccl43").attr("disabled","disabled").css({"color":"#ccc"});
          });
          $(".ccl12_div").addClass("disabledstatus");
          $(".ccl12_div").children(".ccl13_div").removeClass("ccl13_div1");
          $(".ccl12_div").children(".ccl14_div").removeClass("ccl14_div1");
        }else{
          $(this).attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");
          $(".ccl4 .ccl4_list .ccl4_img").attr({"src":"../websiteManageResource/img/checkbox1.png"}).addClass("checkbox1");
          $(".ccl22_div").attr("disabled","disabled").css({"background":"#f5f5f5","color":"#ccc"});
          $(document).find(".ccl4_list").each(function(){
            $(this).children(".ccl43").removeAttr("disabled").css({"color":"#0077C7"});
          });
          $(".ccl12_div").removeClass("disabledstatus");
          $(".ccl12_div").children(".ccl13_div").addClass("ccl13_div1");
          $(".ccl12_div").children(".ccl14_div").addClass("ccl14_div1");
        }
      }
    }else{
      alert("完成上一步的操作后,才能进行当前操作");
      return;
    }
  });
  
  //点击单个勾选框
  $(document).on("click",".ccl4_img",function(){
    if(!checkDiabled){//此时允许选中角色进行删除
      var num=0;//记录当前选中的勾选框数量
      var l=$(".ccl4 .ccl4_list .ccl4_img").length;
      if($(this).hasClass("checkbox1")){
        $(this).attr({"src":"../websiteManageResource/img/checkbox2.png"}).removeClass("checkbox1");
        $(".ccl22_div").removeAttr("disabled").css({"background":"#0077C7","color":"#fff"});
        $(document).find(".ccl4_list").each(function(){
          $(this).children(".ccl43").attr("disabled","disabled").css({"color":"#ccc"});
        });
        $(".ccl12_div").addClass("disabledstatus");
        $(".ccl12_div").children(".ccl13_div").removeClass("ccl13_div1");
        $(".ccl12_div").children(".ccl14_div").removeClass("ccl14_div1");
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
          $(".ccl22_div").attr("disabled","disabled").css({"background":"#f5f5f5","color":"#ccc"});
          $(document).find(".ccl4_list").each(function(){
            $(this).children(".ccl43").removeAttr("disabled").css({"color":"#0077C7"});
          });
          $(".ccl12_div").removeClass("disabledstatus");
          $(".ccl12_div").children(".ccl13_div").addClass("ccl13_div1");
          $(".ccl12_div").children(".ccl14_div").addClass("ccl14_div1");
        }
      }
    }else{
      alert("完成上一步的操作后,才能进行当前操作");
      return;
    }
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
      url:rootPath+"security/delRole.do",
      dataType:"json",
      cache:false, 
      data:JSON.stringify(data2),
      beforeSend:function(){
        $(".ccl22_div").attr("disabled","disabled").css({"background":"#f5f5f5","color":"#ccc"});
      },
      success:function(resultData){
        if(resultData.ReturnType=="1001"){
          getRoleList(data0);//刷新角色列表
          $(".ccl22_div").attr("disabled","disabled").css({"background":"#f5f5f5","color":"#ccc"});
        }else{
          alert("删除角色失败"+resultData.Message);
          $(".ccl22_div").removeAttr("disabled").css({"background":"##0077C7","color":"#fff"});
        }
      },
      error:function(jqXHR){
        alert("删除角色发生错误"+jqXHR.status);
        $(".ccl22_div").removeAttr("disabled").css({"background":"##0077C7","color":"#fff"});
      }
    });
  });


});
