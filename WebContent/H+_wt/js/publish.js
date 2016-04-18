
//动态创建二级菜单树，传入的参数为一个数组对象
function menuTreeLoad(data){
    alert(data.ReturnType);
    alert(data.menuList[0].itemList[0].menuListName);
    var menuTreeLen=data.menuList.length;

    //声明下面需要创建的节点，以便添加内容和添加到文档中
    var mainLi,firstA,firstI,labelSpan,iconSpan,secondUl,secondLi,secondA;
    //外层循环加载一级菜单内容
    for(var i=0;i<menuTreeLen;i++){
        mainLi=$("<li></li");

        firstA=$("<a href='javascript:;'></a>");
        firstI=$("<i class='fa fa-home'></i>");
        labelSpan=$("<span class='nav-label'></span>");
        labelSpan.text(data.menuList[i].menuGroupName);
        iconSpan=("<span class='fa arrow'></span>");

        firstA.append(firstI);
        firstA.append(labelSpan);
        firstA.append(iconSpan);
        mainLi.append(firstA);

        secondUl=$("<ul class='nav nav-second-level collapse'></ul>");
        var menuItemLen=data.menuList[i].itemList.length;
        //内层菜单加载一级菜单对应的二级菜单列表
        for(var j=0;j<menuItemLen;j++){
            var conItem=data.menuList[i].itemList[j].menuListName;
            //var conUrl=data[i].itemList[j].url;

            secondLi=$("<li></li");
            secondA=$("<a class='J_menuItem' href='#'></a>");
           // secondA.attr({"href":"二级菜单链接获取处"});
            secondA.text(conItem);

            secondUl.append(secondLi.append(secondA));
        }
        mainLi.append(secondUl);
    //整个for循环结束
    }
    //将创建好的节点添加到对应位置
    $("#side-menu").append(mainLi);


    /*添加class类
    $("p").addClass("selected1 selected2");  多个用空格隔开
    $('ul li:last').addClass(function() {
      return 'item-' + $(this).index();
    });
    */
}












 /*                
    //发布管理页面列表区和详情区左右拖拽效果
    var xDown=0,xMove=0,xUp=0;
    $(".drag").css("left",$(".pubList").width()+"px");
    var l=$(".pubList").width();
    var d=$(".pubDetail").width();
    $(".drag").bind("mousedown",function(ev){
        var myEvent=ev || event;
        xDown=myEvent.clientX;
        
        $(document).bind("mousemove",function(ev){
            var myEvent=ev || event;
            xMove=myEvent.clientX;
            var x=xMove-xDown;
            //控制到列表的最小宽度后不能再缩小
            if(l+x<=360){
                x=360-l;
            }
            $(".pubList").width(l+x);
            $(".drag").css("left",$(".pubList").width()+"px");
            $(".pubDetail").width(d-x);
            
            
            //鼠标抬起时，将鼠标移动事件清空；
            $(document).bind("mouseup",function(){
                $(document).unbind("mouseup");
                $(document).unbind("mousemove");   
            });
            
        });
    });
*/
               