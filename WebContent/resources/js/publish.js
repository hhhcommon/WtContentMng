//根据审核状态加载专辑或单体列表
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
        thumbImg.attr({'src':actList.resultList[i].thumbImg});
        conDiv=$("<div class='listCon'>");
        conH=$("<h3></h3>");
        conP1=$("<p class='secTitle'></p>");
        conP1.text(actList.resultList[i].actDesc);
        conP2=$("<p class='other'></p>");
        conSpan1=$("<span></span>");
        conSpan1.text("来源："+actList.resultList[i].source);
        conSpan2=$("<span></span>");
        conSpan2.text(actList.resultList[i].cTime);

        checkDiv.append(checkInput);
        imgDiv.append(thumbImg);
        //根据类型显示不同的标记
        switch(actList.resultList[i].actType){
        case '专辑':
        	conH.html(actList.resultList[i].actTitle+"<span style='background-color:#f9be36'>专辑</span>");
        	break;
        case '单体':
        	conH.html(actList.resultList[i].actTitle+"<span style='background-color:#61b0e8'>单体</span>");
        	break;
        case '电台':
        	conH.html(actList.resultList[i].actTitle+"<span style='background-color:#ccc'>电台</span>");
        	break;
        default:
        
        }
        conP2.append(conSpan1);
        conP2.append(conSpan2);
        conDiv.append(conH).append(conP1).append(conP2);
        listDiv.append(checkDiv).append(imgDiv).append(conDiv);
        outDiv.append(firstA.append(listDiv));   
    }
    $(".pubList").prepend(outDiv);
}

//根据专辑或单体Id获取其详细信息及其下列表
function itemListLoad(conList){
     var conListLength=conList.ResultList.ItemList.length;
     alert(conListLength);
     //下面是获取专辑详情
     $(".actThumb").attr({'src':conList.ResultList.ActThumb});
     $(".actTitle").text(conList.ResultList.ActTitle);
     $(".typeTag").text(conList.ResultList.ActType);
     $(".actSource").text("来源："+conList.ResultList.ActSource);
     $(".actPubTime").text(conList.ResultList.ActPubTime);
     $(".vjName").text(conList.ResultList.VjName);
     $(".actDesn").text(conList.ResultList.ActDesn);
     //$(".cloumn").text(itemList.ResultList.Cloumn);  栏目？标签？数组类型

	//声明下面需要创建的节点，以便获取专辑内的单体列表
	 var tr,tdFirst,tdSpan,tdA,tdSecond;
     var tbody=$("<tbody></tbody>");
    //循环加载列表
     for(var i=0;i<conListLength;i++){
	  tr=$("<tr></tr>");
	  tdFirst=$("<td></td>");
	  tdSpan=$("<span class='fa fa-youtube-play fa-lg'></span>")
	  tdA=$("<a href='#'></a>");
	  tdA.text(conList.ResultList.ItemList[i].ItemName);
	  tdSecond=$("<td class='text-right'></td>");
	  tdSecond.text(conList.ResultList.ItemList[i].ItemPubName);
	  
	  tdFirst.append(tdSpan).append(tdA);
	  tbody.append(tr.append(tdFirst).append(tdSecond))
     }
     $(".table").append(tbody);
}

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
               