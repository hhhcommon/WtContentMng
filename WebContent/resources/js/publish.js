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






    /*添加class类
    $("p").addClass("selected1 selected2");  多个用空格隔开
    $('ul li:last').addClass(function() {
      return 'item-' + $(this).index();
    });
    */


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
               