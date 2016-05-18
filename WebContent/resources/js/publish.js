var contentCount=0;
var rootPath=getRootPath();
var current_page=1;
var isSelect=false;
//获取查询条件列表，节目分类和来源
function getConditions(){
	$.ajax({
        type: "POST",    
        url:rootPath+"content/getConditions.do",
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
           alert("发生错误" + jqXHR.status);
        }     
    });
}

//公共ajax请求
function commonAjax(url,data,obj,callback){
	$.ajax({
		//async:false,
        type: "POST",
        url:url,
        dataType: "json",
        data:data,
        beforeSend:function(){obj.html("<div style='text-align:center;height:300px;line-height:200px;'>数据加载中...</div>")}, 
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
            	obj.html("<div style='text-align:center;height:300px;line-height:200px;'>"+ContentList.Message+"</div>");
            }  
        },
        error: function(jqXHR){  
           alert("发生错误" + jqXHR.status);
        }     
    });
}	
//从后台请求节目列表数据
function getContentList(current_page,flowFlag,isSelect){
	var url=rootPath+"content/getContents.do";
	var data={};
	//带专门查询条件的查询
	if(isSelect){
		data={
                UserId: "zhangsan", 
            	ContentFlowFlag:flowFlag,
            	CatalogsId:$(".catalogs option:selected").attr("catalogsId"),
            	SourceId:$(".source option:selected").attr("sourceId"),
                Page:current_page,
                PageSize:"10"
            };
	}else{
		//基础的按状态查询
		data={
	            UserId: "zhangsan", 
	            ContentFlowFlag:flowFlag,
	            Page:current_page,
	            PageSize:"10"
	        };
	}
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
	contentCount=actList.ContentCount;
	contentCount=(contentCount%10==0)?(contentCount/10):(Math.ceil(contentCount/10));
	$(".totalPage").text(contentCount);
	//翻页
    //$('.pagination').jqPagination({max_page  : contentCount});
    var actListLength=actList.ResultList.length;
    if(actListLength==0){
    	$(".actList").html("<div style='text-align:center;height:500px;line-height:300px;'>没有找到您要的节目,您可以更换查询条件试试哦！</div>");
    }else{
	    //声明下面需要创建的节点，以便添加内容和添加到文档中
	    var actListDiv,listDiv,checkDiv,checkInput,imgDiv,thumbImg,conDiv,conH,conHspan,conP1,conP2,conSpan1,conSpan2;
	    var sortDiv,sortInput,sortBtn;
	    //actListDiv=$("<div class='actList'></div>");
	    //循环加载列表
	    for(var i=0;i<actListLength;i++){
	        listDiv=$("<div class='listBox'></div>");
	        listDiv.attr(
	        		{actId:actList.ResultList[i].ContentId,
	        		 actType:actList.ResultList[i].MediaType,
	        		 columnId:actList.ResultList[i].Id
	        		 });
	        checkDiv=$("<div class='listCheck'>");
	        checkInput=$("<input type='checkBox' name='' />");
	        imgDiv=$("<div class='listImg'>");
	        thumbImg=$("<img alt='mage'>");
	        thumbImg.attr({'src':actList.ResultList[i].ContentImg});
	        conDiv=$("<div class='listCon'>");
	        conH=$("<h3></h3>");
	        conP1=$("<p class='secTitle'></p>");
	        conP1.html((actList.ResultList[i].ContentDesc=="null"?"暂无":(actList.ResultList[i].ContentDesc.replace(/\<br \/\>/g, ""))));
	        conP2=$("<p class='other'></p>");
	        conSpan1=$("<span></span>");
	        conSpan1.text("来源："+actList.ResultList[i].ContentSource);
	        conSpan2=$("<span></span>");
	        //alert(formatDate(new Date(actList.ResultList[i].ContentCTime)));
	        
	        conSpan2.text(formatDate(new Date(actList.ResultList[i].ContentCTime)));
	        
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
	    var hoverBar=$("<span class='hoverBar'></span>");
	    $(".actList").append(hoverBar);
	    //默认节目列表的第一条显示详情
	    $(".listBox").first().trigger("click");
	    
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
var subList=[];
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
     $(".vjName").html((conList.ContentDetail.ContentPersons==null)?"暂无":conList.ContentDetail.ContentPersons);
     $(".actDesn").html((conList.ContentDetail.ContentDesc).replace(/\<br \/\>/g, ""));
     $(".cloumn").html(conList.ContentDetail.ContentCatalogs);
     
     $(".pubDetail .conBox").css({"display":"block"});
     //创建单体列表DOM结构
     AudioListLoad(conList.SubList,false);
     //将专辑单体列表存储起来，以便排序使用 --对象深度复制
     subList = jQuery.extend(true, [], conList.SubList);
}
//创建单体资源列表DOM结构
//把内容列表单提出来一个方法，是为了正反排序时再调用此方法对DOM节点进行前置插入
function AudioListLoad(itemList,sort){
	$(".table").html("");
    var conListLength=itemList.length;
	//声明下面需要创建的节点，以便获取节目内的单体列表
	var tr,tdFirst,tdSpan,tdA,tdSecond;
    var tbody=$("<tbody></tbody>");
   //循环创建table行
    for(var i=0;i<conListLength;i++){
	  tr=$("<tr></tr>");
	  tr.attr({"contentId":itemList[i].ContentId,"contentURI":itemList[i].ContentURI});
	  tdFirst=$("<td></td>");
	  tdSpan=$("<span class='fa fa-youtube-play fa-lg'></span>")
	  tdA=$("<a href='javascript:;'></a>");
	  tdA.text(itemList[i].ContentName);
	  tdSecond=$("<td class='text-right'></td>");
	  tdSecond.text(itemList[i].ContentPubTime);
	  
	  tdFirst.append(tdSpan).append(tdA);
	  tr.append(tdFirst).append(tdSecond);
	  //根据是否有误sort参数判断插入行的方式，以实现正反序效果
	  if(sort){
		  tbody.prepend(tr);  //前置插入行
	  }else{
		  tbody.append(tr);   //后置追加行
	  }
    }
    $(".table").append(tbody);
}
function fy(event){
	var flowFlag=event.data.flowFlag;
	switch (event.target.getAttribute("data-action")) {
      case 'previous':
    	  current_page--;
    	  $(".toPage").val("");
        break;
      case 'next':
    	  current_page++;
    	  $(".toPage").val("");
    	  
        break;
      case 'toPage':
    	//跳至进行输入合理数字范围检测
    	  var reg = new RegExp("^[0-9]*$");
    	  if(!reg.test($(".toPage").val()) || $(".toPage").val()<1 || $(".toPage").val() > contentCount){  
    		  alert("请输入有效页码！");
    	        return;
    	    }
    	  current_page = $(".toPage").val();
    	  
        break;
      default:
    }
	//第一页或最后一页，置灰样式并使链接无效
	if (current_page < 1 || current_page > contentCount) {
		current_page=1;
		return false;
	}
	if (current_page > contentCount) {
		current_page=contentCount;
		return false;
	}
	$(".page").find("span").removeClass("disabled");
	if (current_page == 1) {
		$(".previous").addClass('disabled');
	}
	if (current_page == contentCount) {
		$(".next").addClass('disabled');
	}
	
	//当前页
	$(".currentPage").text(current_page);
	if(isSelect){
		getContentList(current_page,flowFlag,isSelect);
	}else{
		getContentList(current_page,flowFlag);
	}
}

//时间戳转日期
function   formatDate(now)   {     
    var   year=now.getFullYear();     
    var   month=now.getMonth()+1;     
    var   date=now.getDate();     
    var   hour=now.getHours();     
    var   minute=now.getMinutes();     
    var   second=now.getSeconds();     
    return   year+"年"+month+"月"+date+"日 ";     
}

