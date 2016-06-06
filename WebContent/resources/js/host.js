//var rootPath=getRootPath();
var userId="123",mediaType,dataParam={}
//公共ajax请求
function getContentList(obj) {
	$.ajax({
	  type : "POST",
	  url : obj.url,
	  dataType : "json",
	  data : JSON.stringify(obj),
	  beforeSend : function() {
		  if(!obj.opeType){
			  $(".actList").html("<div style='text-align:center;height:300px;line-height:200px;'>数据加载中...</div>");
		  }
	  },
	  success : function(resultData) {
	    if (resultData.ReturnType == "1001") {
	    	if(!obj.opeType){
	    		$(".actList").html(""); //再重新创建新的数据集时，先清空之前的
		        $(".totalCount").text(resultData.ResultList.AllCount);
		        ContentListLoad(resultData);
	    	}else{
	    		switch(obj.opeType){
	    			case 'conAdd':
	    				alert("添加成功");
	    			break;
	        		case 'conUpdate':
	        			break;
	        		case 'conDel':
	        			alert("删除成功");
	        			//删除成功后，再次请求列表
	        			dataParam.MediaType="SEQU";
	        			dataParam.url="http://localhost:908/CM/content/getHostContents.do";
	        			delete dataParam["opeType"];
	        			getContentList(dataParam);
	              break;
	        		default:
	    		}
	    	}
	    } else {
	      $(".actList").html("<div style='text-align:center;height:300px;padding:20px;padding-top:140px;'>"+ resultData.Message)+ ")";
	    }
	  },
	  error : function(jqXHR) {
	    $(".actList").html("<div style='text-align:center;height:300px;line-height:200px;'>获取数据发生错误："+ jqXHR.status+ "</div>");
	  }
	});
}
//创建节目列表DOM树
function ContentListLoad(actList) {
  var actListLength = actList.ResultList.AllCount;
  var mediaType=actList.ResultList.List[0].MediaType;
  if (actListLength == 0) {
    $(".actList").html("<div style='text-align:center;height:500px;line-height:300px;'>还没有创建节目哦！</div>");
  } else {
    //声明下面需要创建的节点，以便添加内容和添加到文档中
    var actListDiv,listDiv,imgDiv,imgA,thumbImg,imgShade,conUpdate,conShare,conDel,subAdd,infoDiv,infoH,infHA,infoP1,infoP2;
    //循环加载列表
    for (var i = 0; i < actListLength; i++) {
      listDiv = $("<div class='listBox'></div>");
      listDiv.attr({
      	"contentId": actList.ResultList.List[i].ContentId,
      	"contentName": actList.ResultList.List[i].ContentName,
      	"contentDesc": actList.ResultList.List[i].ContentDesc,
      	"contentImg": actList.ResultList.List[i].ContentImg,
      	"contentCatalogs": actList.ResultList.List[i].ContentCatalogs,
      	"subjectWords": actList.ResultList.List[i].SubjectWords});
      imgDiv = $("<div>");
      imgA=$("<a href='javascript:;'></a>");
      thumbImg = $("<img alt='节目封面图片''>");
      thumbImg.attr({'src' : actList.ResultList.List[i].ContentImg});
      imgShade=$("<div class='imgShade'></div>");
      conUpdate=$("<i class='fa fa-pencil' opeType='conUpdate'></i>");
      conShare=$("<i class='fa fa-external-link' opeType='conShare'></i>");
      conDel=$("<i class='fa fa-trash-o' opeType='conDel'></i>");
      subAdd=$("<i class='fa fa-plus-square-o' opeType='subAdd'></i>");
      infoDiv = $("<div class='infoBox'>");
      infoH = $("<h4></h4>");
      infoHA=$("<a href='javascript:;'></a>");
      imgShade.append(conUpdate).append(conShare).append(conDel).append(subAdd);
      infoHA.text(actList.ResultList.List[i].ContentName);
      infoP2 = $("<p class='lastTime'></p>");
      infoP2.text((actList.ResultList.List[i].CTime).slice(0,10));
      if(mediaType=="SEQU"){
    	  imgDiv.addClass("imgBox");
    	  infoP1 = $("<p class='subCount'></p>");
          //infoP1.text(actList.ResultList.List[i].SubCount+"个声音");
          infoP1.text("12个声音");
          infoDiv.append(infoH.append(infoHA)).append(infoP1).append(infoP2);
      }else{
    	  imgDiv.addClass("subImg");
    	  infoDiv.append(infoH.append(infoHA)).append(infoP2);
      }
      imgDiv.append(imgA.append(thumbImg)).append(imgShade);
      listDiv.append(imgDiv).append(infoDiv);
      $(".actList").append(listDiv);
    }
  }
}
//获取分类列表
function getCatalogs(){
    $.ajax({
      type: "POST",    
      url:"http://localhost:908/CM/common/getCataTreeWithSelf.do",
      dataType: "json",
      data:{cataId: "3"},
      success: function(catalogsList) {
        if (catalogsList.jsonType=="1") {
          catalogsListLoad(catalogsList);
        } else {
            //alert("获取数据出现问题:"+ConditionsList.Message);
        }  
      },
      error: function(jqXHR){
         //alert("发生错误" + jqXHR.status);
      }     
    });
  }
  function catalogsListLoad(catalogsList){
    var listLength=catalogsList.data.children.length;
    var opt;
    for(var i=0;i<listLength;i++){
      opt=$("<option></option>");
      opt.val(catalogsList.data.children[i].id);
      opt.text(catalogsList.data.children[i].name);
      $("#ContentCatalogs").append(opt);
    }
  }