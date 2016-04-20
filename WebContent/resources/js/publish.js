<<<<<<< HEAD
<<<<<<< HEAD
function actListLoad(actList){
     var actListLength=actList.resultList.length;
     //alert(actListLength);
    //声明下面需要创建的节点，以便添加内容和添加到文档中
    var firstA,listDiv,checkDiv,checkInput,imgDiv,thumbImg,conDiv,conH,conHspan,conP1,conP2,conSpan1,conSpan2;
    var outDiv=$("<div></div>");
    //循环加载列表
    for(var i=0;i<actListLength;i++){
        firstA=$("<a href='javascript:void(0)'></a>");
        listDiv=$("<div class='listBox'></div>");
        checkDiv=$("<div class='listCheck'>");
        checkInput=$("<input type='checkBox' name='' />");
        imgDiv=$("<div class='listImg'>");
        thumbImg=$("<img alt='mage'>");
        thumbImg.attr({'src':actList.resultList[i].actThunb});
        conDiv=$("<div class='listCon'>");
        conH=$("<h3></h3>");
        //conH.text(actList[i].title);
        //conHspan=$("<span></span>");
        //conHspan.text("专辑");
        conP1=$("<p class='secTitle'></p>");
        conP1.text(actList.resultList[i].actType);
        conP2=$("<p class='other'></p>");
        conSpan1=$("<span></span>");
        conSpan1.text("来源："+actList.resultList[i].id);
        conSpan2=$("<span></span>");
        conSpan2.text(actList.resultList[i].cTime);
=======
/*
<a href="javascript:void(0)">
    <div class="listBox">
        <div class="listCheck">
            <input type="checkBox" name="" />
        </div>
        <div class="listImg">
            <img alt="image" src="../resources/images/a2.jpg">
        </div>
        <div class="listCon">
            <h3>滚石乐队为何古巴开唱</h3>
            <p class="secTitle">秒！上海迪士尼门票瞬间售罄/滚石乐队古巴</p>
            <p class="other"><span>from 新闻最前沿</span><span>来源:蜻蜓</span><span>2016年4月13</span></p>
        </div>
    </div>
</a>
*/
=======
/*
<a href="javascript:void(0)">
    <div class="listBox">
        <div class="listCheck">
            <input type="checkBox" name="" />
        </div>
        <div class="listImg">
            <img alt="image" src="../resources/images/a2.jpg">
        </div>
        <div class="listCon">
            <h3>滚石乐队为何古巴开唱</h3>
            <p class="secTitle">秒！上海迪士尼门票瞬间售罄/滚石乐队古巴</p>
            <p class="other"><span>from 新闻最前沿</span><span>来源:蜻蜓</span><span>2016年4月13</span></p>
        </div>
    </div>
</a>
*/


>>>>>>> refs/remotes/origin/master


<<<<<<< HEAD
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
        checkDiv.append(checkInput);
        imgDiv.append(thumbImg);
        conDiv.append(conH);
        conH.html(actList.resultList[i].actTitle+"<span>专辑</span>");
        conDiv.append(conP1);
        conP2.append(conSpan1);
        conP2.append(conSpan2);
        conDiv.append(conP2);
        listDiv.append(checkDiv);
        listDiv.append(imgDiv);
        listDiv.append(conDiv);
        outDiv.append(firstA.append(listDiv));   
    }
    $(".pubList").prepend(outDiv);
}
=======

=======
>>>>>>> refs/remotes/origin/master


    /*添加class类
    $("p").addClass("selected1 selected2");  多个用空格隔开
    $('ul li:last').addClass(function() {
      return 'item-' + $(this).index();
    });
    */
<<<<<<< HEAD
>>>>>>> refs/remotes/origin/master
=======
>>>>>>> refs/remotes/origin/master


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
               