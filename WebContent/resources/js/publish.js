//从后台请求节目列表数据
function getActListAjax(page){
	$.ajax({
        type: "POST",    
        url: "http://localhost:908/wt/content/listquery/query.do",
        dataType: "json",
        data: {
            UserId: "zhangsan", 
            ContentFlowFlag:"1",
            Page:page,
            PageSize:"10"
        },
        //beforeSend:function(){$(".pubList").html("数据加载中...")},
        success: function(actList) {
            if (actList.ReturnType=="1001") {
                //console.log("获取到数据了！");
                //加载列表
            	if($(".actList")!=undefined){
            		$(".pubList>.actList").empty(); //再重新创建新的数据集时，先清空之前的
            	}
                actListLoad(actList);
            } else {
                alert("获取数据出现问题lou:"+actList.Message);
            }  
        },
        error: function(jqXHR){     
           alert("发生错误：" + jqXHR.status);
        }     
    });
}
				

//根据审核状态创建节目列表DOM树
function actListLoad(actList){
     var actListLength=actList.ResultList.length;
     //alert(actListLength);
    //声明下面需要创建的节点，以便添加内容和添加到文档中
    var firstA,listDiv,checkDiv,checkInput,imgDiv,thumbImg,conDiv,conH,conHspan,conP1,conP2,conSpan1,conSpan2;
    var sortDiv,sortInput,sortBtn;
    var outDiv=$("<div class='actList'></div>");
    //循环加载列表
    for(var i=0;i<actListLength;i++){
        listDiv=$("<div class='listBox'></div>");
        listDiv.attr(
        		{actId:actList.ResultList[i].ContentId,
        		 actType:actList.ResultList[i].MediaType,
        		 id:actList.ResultList[i].Id
        		 } );
        checkDiv=$("<div class='listCheck'>");
        checkInput=$("<input type='checkBox' name='' />");
        imgDiv=$("<div class='listImg'>");
        thumbImg=$("<img alt='mage'>");
        thumbImg.attr({'src':actList.ResultList[i].ContentImg});
        conDiv=$("<div class='listCon'>");
        conH=$("<h3></h3>");
        conP1=$("<p class='secTitle'></p>");
        conP1.text(actList.ResultList[i].ContentDesc);
        conP2=$("<p class='other'></p>");
        conSpan1=$("<span></span>");
        conSpan1.text("来源："+actList.ResultList[i].ContentSource);
        conSpan2=$("<span></span>");
        conSpan2.text(actList.ResultList[i].ContentCTime);
        sortDiv=$("<div class='sortUpdate'></div>");
        sortInput=$("<input type='text' class='sortNum'></input>");
        sortInput.attr({"value":actList.ResultList[i].ContentSort});
        sortBtn=$("<button class='sortUpdateBtn'></button>");
        sortBtn.text("OK");
        sortDiv.append(sortInput).append(sortBtn);

        checkDiv.append(checkInput);
        imgDiv.append(thumbImg);
        //根据类型显示不同的标记
        switch(actList.ResultList[i].ActType){
	        case 'wt_SeqMediaAsset':
	        	//alert(actList.ResultList[i].ActType);
	        	conH.html(actList.ResultList[i].ContentName+"<span style='background-color:#f9be36'>专辑</span>");
	        	break;
	        case 'wt_MediaAsset':
	        	conH.html(actList.ResultList[i].ContentName+"<span style='background-color:#61b0e8'>单体</span>");
	        	break;
	        case 'wt_Broadcast':
	        	conH.html(actList.ResultList[i].ContentName+"<span style='background-color:green'>电台</span>");
	        	break;
	        default:
	        
        }
        conP2.append(conSpan1);
        conP2.append(conSpan2);
        conDiv.append(conH).append(conP1).append(conP2);
        listDiv.append(checkDiv).append(imgDiv).append(conDiv).append(sortDiv);
        outDiv.append(listDiv);
    }
    $(".pubList").prepend(outDiv);
}
//根据节目ID从后台获取节目详情及其下单体列表数据
function getItemListAjax(ev){
	$.ajax({
        type: "POST",    
        url: "http://localhost:908/wt/content/listquery/detailquery.do",
        dataType: "json",
        data: {
            UserId: "zhangsan", 
            FlowFlag:"1",
            Page:"1",
            PageSize:"10",
            Id:ev.currentTarget.getAttribute("ContentId"),
            ActType:ev.currentTarget.getAttribute("MediaType")
        },
        //beforeSend:function(){$(".conBox").html("数据加载中...")},
        success: function(itemList) {
        	//$(".conBox").html("");
            if (itemList.ReturnType=="1001") {
                console.log("获取到数据了！");
                //加载列表
                $(".table").html("");
                itemListLoad(itemList);
                console.log(itemList);
            } else {
                console.log("获取数据出现问题la:"+itemList.Message);
            }  
        },
        error: function(jqXHR){     
           alert("发生错误：" + jqXHR.status);
        }    
    });
}

//根据节目ID创建页面DOM结构，展示节目详情及其下单体列表
function itemListLoad(conList){
     //下面是获取节目详情
     $(".actThumb").attr({'src':conList.ContentDetail.ContentImg});
   //根据类型显示不同的标记
     switch(conList.ContentDetail.MediaType){
     case 'wt_SeqMediaAsset':
    	 $(".itemCount").text("专辑里的声音("+conList.ContentCount+")");
    	 $(".actTitle").html(conList.ContentDetail.ContentName+"<span style='background-color:#f9be36'>专辑</span>");
     	break;
     case 'wt_MediaAsset':
    	 $(".actTitle").html(conList.ContentDetail.ContentName+"<span style='background-color:#61b0e8'>单体</span>");
     	break;
     case 'wt_Broadcast':
    	 $(".actTitle").html(conList.ContentDetail.ContentName+"<span style='background-color:green'>电台</span>");
     	break;
     default:
     
     }
     $(".actSource").text("来源："+conList.ContentDetail.ContentSource);
     $(".actPubTime").text(conList.ContentDetail.ContentPubTime);
     $(".vjName").text(conList.ContentDetail.ContentPersons);
     $(".actDesn").text(conList.ContentDetail.ContentDesc);
     //$(".cloumn").text(conList.ContentDetail.ContentCatalogs);  栏目？标签？数组类型
     //创建节目下列表DOM结构
     getItemList(conList.SubList);
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
	  tdA.text(itemList[i].ContentName);
	  tdSecond=$("<td class='text-right'></td>");
	  tdSecond.text(itemList[i].ContentPubTime);
	  
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