$(function(){
	var page=2;
	var nextPage="false";
	//控制播放按钮的播与停
	var audio=$("<audio></audio>")[0];
	$(".audioBox").on("click",".playBtn",function(){
		if($(this).hasClass("play")){
			$(this).removeClass("play");
			audio.pause();
		}else{
			if($(".playBtn").hasClass("play")){
				audio.pause();
				$(".playBtn").removeClass("play");
			}
			audio.src=$(this).parent(".audioLi").attr("data-src");
			//播放当前资源，并改变当前按钮状态
			$(this).addClass("play");
			audio.play();
		}
	});
	/*
	var resultData={
	  "ResultList": [
	    {
	      "ContentName": "杭州公交纵火案监控曝光 嫌犯瞬间变火球",
	      "ContentPubTime": null,
	      "ContentCTime": 1459456342000,
	      "ContentSource": "喜马拉雅",
	      "ContentURI": "http://audio.xmcdn.com/group14/M06/2D/FD/wKgDZFWKtc3CkdYjAAkkVrStxqE953.m4a",
	      "ContentPersons": null,
	      "ContentCatalogs": "时事要闻,资讯,社会新闻,民生话题,社会热点",
	      "MediaType": "wt_MediaAsset",
	      "ContentId": "6972f1f6f6ad49118144cb64d08cf3aa",
	      "ContentDesc": "",
	      "ContentImg": "http://fdfs.xmcdn.com/group4/M00/08/D5/wKgDtFMis6jBzfP4AAELuXQHSVw558_web_large.jpg",
	      "ContentTimes": 74000
	    },
	    {
	      "ContentName": "国务院印发促进市场公平竞争维护市场正常秩序若干意见",
	      "ContentPubTime": null,
	      "ContentCTime": 1459459971000,
	      "ContentSource": "喜马拉雅",
	      "ContentURI": "http://audio.xmcdn.com/group10/M04/4E/28/wKgDZ1WyWqOiIux6AAjPtoUA0w0001.m4a",
	      "ContentPersons": null,
	      "ContentCatalogs": "时事要闻,社会新闻,资讯,社会热点,民生话题",
	      "MediaType": "wt_MediaAsset",
	      "ContentId": "2a9b873577be4d07b21706a4fa1be9b6",
	      "ContentDesc": "",
	      "ContentImg": "http://fdfs.xmcdn.com/group4/M00/08/D5/wKgDtFMis6jBzfP4AAELuXQHSVw558_web_large.jpg",
	      "ContentTimes": 71000
	    }
	  ],
	  "ReturnType": "1001",
	  "NextPage": true
	}
*/
	
	
	function loadMore(resultData){
		var actListLength=resultData.ResultList.length;
		 //声明下面需要创建的节点
	    var liEle,listDiv,titleA,titleH,titleP,playA;
	    //循环加载列表
	    for(var i=0;i<actListLength;i++){
	        liEle=$("<li class='audioLi'></li>");
	        liEle.attr({"data-src":resultData.ResultList[i].ContentURI});
	        listDiv=$("<div class='audioIntro'></div>");
	        titleA=$("<a href='javascript:;'></a>");
	        titleH=$("<h3></h3>");
	        titleH.text(resultData.ResultList[i].ContentName);
	        titleP=$("<p></p>");
	        titleP.text(resultData.ResultList[i].ContentPubTime);
	        titleA.append(titleH);
	        listDiv.append(titleA).append(titleP);
	        playA=$("<a href='javascript:;' class='playBtn'></a>");

	        liEle.append(listDiv).append(playA);
	        $(".audioBox").append(liEle);
	    }
	}
	
	//添加滚动条事件
	window.onscroll=function(){
		//当滚动到最底部以上60像素时， 加载新内容  
		if($(document).height() - $(this).scrollTop() - $(this).height()<60){
			$.ajax({
		        type: "POST",
		        url:"http://www.wotingfm.com:908/CM/content/getZJSubPage.do",
		        dataType: "json",
		        data:{Page:page,ContentId:$(".zjIntro").attr("contentId")},
		        success: function(resultData) {
		            if (resultData.ReturnType=="1001"){
		            	loadMore(resultData);
		            	if(resultData.NextPage=="true"){
		            		page++;
		            	}
		            }else{
		            	return;
		            }
		        },
		        error: function(jqXHR){  
		           alert("发生错误" + jqXHR.status);
		        }     
		    });
		}
	}

});