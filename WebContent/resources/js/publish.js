//根据审核状态加载专辑或单体列表
function actListLoad(actList){
     var actListLength=actList.ResultList.length;
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
        thumbImg.attr({'src':actList.ResultList[i].ActThumb});
        conDiv=$("<div class='listCon'>");
        conH=$("<h3></h3>");
        conP1=$("<p class='secTitle'></p>");
        conP1.text(actList.ResultList[i].ActDesc);
        conP2=$("<p class='other'></p>");
        conSpan1=$("<span></span>");
        conSpan1.text("来源："+actList.ResultList[i].Source);
        conSpan2=$("<span></span>");
        conSpan2.text(actList.ResultList[i].CTime);

        checkDiv.append(checkInput);
        imgDiv.append(thumbImg);
        //根据类型显示不同的标记
        switch(actList.ResultList[i].ActType){
        case '专辑':
        	conH.html(actList.ResultList[i].ActTitle+"<span style='background-color:#f9be36'>专辑</span>");
        	break;
        case '单体':
        	conH.html(actList.ResultList[i].ActTitle+"<span style='background-color:#61b0e8'>单体</span>");
        	break;
        case '电台':
        	conH.html(actList.ResultList[i].ActTitle+"<span style='background-color:#ccc'>电台</span>");
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
     //下面是获取节目详情
     $(".actThumb").attr({'src':conList.ActDetail.ActThumb});
   //根据类型显示不同的标记
     switch(conList.ActDetail.ActType){
     case 'wt_SeqMediaAsset':
    	 $(".itemCount").text("专辑里的声音("+conList.ItemCount+")");
    	 $(".actTitle").html(conList.ActDetail.ActTitle+"<span style='background-color:#f9be36'>专辑</span>");
     	break;
     case '单体':
    	 $(".actTitle").html(conList.ActDetail.ActTitle+"<span style='background-color:#61b0e8'>单体</span>");
     	break;
     case '电台':
    	 $(".actTitle").html(conList.ActDetail.ActTitle+"<span style='background-color:#ccc'>电台</span>");
     	break;
     default:
     
     }
     $(".actSource").text("来源："+conList.ActDetail.ActSource);
     $(".actPubTime").text(conList.ActDetail.ActPubTime);
     $(".vjName").text(conList.ActDetail.ActVjName);
     $(".actDesn").text(conList.ActDetail.ActDesn);
     //$(".cloumn").text(itemList.ResultList.Cloumn);  栏目？标签？数组类型
     
     getItemList(conList.ItemList);
}

//把内容列表单提出来一个方法，是为了正反排序时再调用此方法对DOM节点进行前置插入
function getItemList(itemList,sort){
    var conListLength=itemList.length;
	//声明下面需要创建的节点，以便获取节目内的单体列表
	var tr,tdFirst,tdSpan,tdA,tdSecond;
    var tbody=$("<tbody></tbody>");
   //循环创建table行
    for(var i=0;i<conListLength;i++){
	  tr=$("<tr></tr>");
	  tdFirst=$("<td></td>");
	  tdSpan=$("<span class='fa fa-youtube-play fa-lg'></span>")
	  tdA=$("<a href='#'></a>");
	  tdA.text(itemList[i].ItemName);
	  tdSecond=$("<td class='text-right'></td>");
	  tdSecond.text(itemList[i].ItemPubTime);
	  
	  tdFirst.append(tdSpan).append(tdA);
	  tr.append(tdFirst).append(tdSecond);
	  //根据是否有误sort参数判断插入行的方式，以实现正反序效果
	  if(sort!=null){
		  tbody.prepend(tr);  //前置插入行
	  }else{
		  tbody.append(tr);   //后置追加行
	  }
    }
    $(".table").append(tbody);
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
               