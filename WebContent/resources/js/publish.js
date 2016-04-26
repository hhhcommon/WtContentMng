var contentCount=50;
//获取查询条件列表，节目分类和来源
function getConditions(){
	$.ajax({
        type: "POST",    
        url:"http://localhost:908/CM/content/getConditions.do",
        dataType: "json",
        data:{UserId: "zhangsan"},
        success: function(ConditionsList) {
            if (ConditionsList.ReturnType=="1001") {
                ConditionsListLoad(ConditionsList);
            } else {
                alert("获取数据出现问题la:"+ConditionsList.Message);
            }  
        },
        error: function(jqXHR){   
           alert("发生错误：" + jqXHR.status);
        }     
    });
}

//公共ajax请求
function commonAjax(url,data,obj,callback){
	$.ajax({
        type: "POST",
        url:url,
        dataType: "json",
        data:data,
        beforeSend:function(){obj.html("<div style='text-align:center;height:500px;line-height:300px;'>数据加载中...</div>")},
        
        success: function(ContentList) {
            if (ContentList.ReturnType=="1001") {
            	obj.html(""); //再重新创建新的数据集时，先清空之前的
            	//判断是查询还是修改操作，调用不同的方法
            	if(data.OpeType){
            		callback(1,data.ContentFlowFlag);
            	}else{
            		callback(ContentList);
            	}
            } else {
            	obj.html("<div style='text-align:center;height:300px;line-height:300px;'>"+ContentList.Message+"</div>");
            }  
        },
        error: function(jqXHR){  
           alert("发生错误：" + jqXHR.status);
        }     
    });
}	
//从后台请求节目列表数据
function getContentList(page,flowFlag){
	var url="http://localhost:908/CM/content/getContentInfo.do";
	var data={
            UserId: "zhangsan", 
            ContentFlowFlag:flowFlag,
            Page:page,
            PageSize:"10"
        };
	commonAjax(url,data,$(".pubList>.actList"),ContentListLoad);
}
//创建查询条件DOM元素
function ConditionsListLoad(ConditionsList){
	var calalogsLen=ConditionsList.Catalogs.length;
	var sourceLen=ConditionsList.Source.length;
	var catalogsOption,sourceOption;
	
	for(var i=0;i<calalogsLen;i++){
		catalogsOption=$("<option></option>");
		catalogsOption.attr({"catalogsId":ConditionsList.Catalogs[i].CatalogsId});
		catalogsOption.text(ConditionsList.Catalogs[i].CatalogsName);
		$(".operate .catalogs").append(catalogsOption);
	}
	for(var j=0;j<sourceLen;j++){
		sourceOption=$("<option></option>");
		sourceOption.attr({"sourceId":ConditionsList.Source[j].SourceId});
		sourceOption.text(ConditionsList.Source[j].SourceName);
		$(".operate .source").append(sourceOption);
	}
}

//创建节目列表DOM树
function ContentListLoad(actList){
	//alert(actList.ContentCount);
    var actListLength=actList.ResultList.length;
    //声明下面需要创建的节点，以便添加内容和添加到文档中
    var actListDiv,listDiv,checkDiv,checkInput,imgDiv,thumbImg,conDiv,conH,conHspan,conP1,conP2,conSpan1,conSpan2;
    var sortDiv,sortInput,sortBtn;
    contentCount=actList.ContentCount;
    //actListDiv=$("<div class='actList'></div>");
    //循环加载列表
    for(var i=0;i<actListLength;i++){
        listDiv=$("<div class='listBox'></div>");
        listDiv.attr(
        		{actId:actList.ResultList[i].ContentId,
        		 actType:actList.ResultList[i].MediaType,
        		 id:actList.ResultList[i].Id
        		 });
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
        
        checkDiv.append(checkInput);
        imgDiv.append(thumbImg);
        //根据类型显示不同的标记
        conH.html(actList.ResultList[i].ContentName);
        /*
        switch(actList.ResultList[i].MediaType){
	        case 'wt_SeqMediaAsset':
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
        */
        conP2.append(conSpan1);
        conP2.append(conSpan2);
        conDiv.append(conH).append(conP1).append(conP2);
        listDiv.append(checkDiv).append(imgDiv).append(conDiv);
        //只在已审核界面创建排序号DOM
        if(actList.ResultList[i].ContentFlowFlag=="2"){
        	sortDiv=$("<div class='sortUpdate'></div>");
            sortInput=$("<input type='text' class='sortNum'></input>");
            sortInput.attr({"value":actList.ResultList[i].ContentSort});
            sortBtn=$("<button class='sortUpdateBtn'></button>");
            sortBtn.text("OK");
            sortDiv.append(sortInput).append(sortBtn);
            listDiv.append(sortDiv);
        }
        
        $(".actList").append(listDiv);
    }
    //$(".pubList").append(actListDiv);
    //创建分页节点
    /*
     * <div class="page">
	    <div class="gigantic pagination">
		    <a href="javascript:;" class="first" data-action="first">&laquo;</a>
		    <a href="javascript:;" class="previous" data-action="previous">&lsaquo;</a>
		    <input type="text" readonly="readonly" value=""/>
		    <a href="javascript:;" class="next" data-action="next">&rsaquo;</a>
		    <a href="javascript:;" class="last" data-action="last">&raquo;</a>
		</div>
	   </div>
    
    var pageDiv,gipa,firstPage,prePage,inputPage,nextPage,lastPage;
    pageDiv=$("<div class='page'></div>");
    gipa=$("<div class='gigantic pagination'></div>");
    firstPage=$("<a href='javascript:;' class='first' data-action='first'>&laquo;</a>");
    prePage=$("<a href='javascript:;' class='previous' data-action='previous'>&lsaquo;</a>");
    inputPage=$("<input type='text' readonly='readonly' />");
    inputPage.attr({"data-max-page":actList.ContentCount});
    nextPage=$("<a href='javascript:;' class='next' data-action='next'>&rsaquo;</a>");
    lastPage=$("<a href='javascript:;' class='last' data-action='last'>&raquo;</a>");
    gipa.append(firstPage).append(prePage).append(inputPage).append(nextPage).append(lastPage);
    pageDiv.append(gipa);
    $(".pubList").append(pageDiv);
    */
}

//根据节目ID展示节目详情及其下单体列表
function ContentInfoLoad(conList){
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
     //$(".cloumn").text(conList.ContentDetail.ContentCatalogs);
     
     $(".pubDetail .conBox").css({"display":"block"});
     //创建单体列表DOM结构
     AudioListLoad(conList.SubList);
}
//创建单体资源列表DOM结构
//把内容列表单提出来一个方法，是为了正反排序时再调用此方法对DOM节点进行前置插入
function AudioListLoad(itemList,sort){
    var conListLength=itemList.length;
	//声明下面需要创建的节点，以便获取节目内的单体列表
	var tr,tdFirst,tdSpan,tdA,tdSecond;
    var tbody=$("<tbody></tbody>");
   //循环创建table行
    for(var i=0;i<conListLength;i++){
	  tr=$("<tr></tr>");
	  tdFirst=$("<td></td>");
	  tdSpan=$("<span class='fa fa-youtube-play fa-lg'></span>")
	  tdA=$("<a href='javascript:;'></a>");
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

