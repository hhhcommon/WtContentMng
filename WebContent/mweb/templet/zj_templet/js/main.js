$(function(){
	var page=2;
	var nextPage="false";
	//控制播放按钮的播与停
	$(".playBtn").on("click",function(){
		var audio=$(this).children("audio")[0];
		if($(this).hasClass("play")){
			$(this).removeClass("play");
			audio.pause();
		}else{
			if($(".playBtn").hasClass("play")){
				$.each($(".playBtn"), function(i,val){
				    if(val.className!="playBtn"){
						$(val).children("audio")[0].pause();
				    }
				});
				$(".playBtn").removeClass("play");
			}
			//播放当前资源，并改变当前按钮状态
			$(this).addClass("play");
			audio.play();
		}
	});
	function loadMore(resultData){
		var actListLength=resultData.ResultList.length;
		 //声明下面需要创建的节点
	    var liEle,listDiv,titleA,titleH,titleP,playA,playAudio;
	    //循环加载列表
	    for(var i=0;i<actListLength;i++){
	        liEle=$("<li></li>");
	        listDiv=$("<div class='audioIntro'></div>");
	        titleA=$("<a href='javascript:(0)'></a>");
	        titleH=$("<h3></h3>");
	        titleH.text(resultData.ResultList[i].ContentName);
	        titleP=$("<p></p>");
	        titleP.text(resultData.ResultList[i].ContentPubTime);
	        titleA.append(titleH);
	        listDiv.append(titleA).append(titleP);
	        playA=$("<a href='javascript:(0)' class='playBtn'></a>");
	        playAudio=$("<audio loop='loop'></audio>");
	        playAudio.attr({src:resultData.ResultList[i].ContentURI});
	        playA.append(playAudio);

	        liEle.append(listDiv).append(playA);
	        $(".audioBox").append(liEle);
	    }
	}
	//加载更多
	$("button").on("click",function(){
		$.ajax({
	        type: "POST",
	        url:"http://localhost:908/CM/content/getZJSubPage.do",
	        dataType: "json",
	        data:{Url:"/mweb/zj",Page:page,ContentId:$(".zjIntro").attr("contentId")},
	        success: function(resultData) {
	            if (resultData.ReturnType=="1001"){
	            	loadMore(resultData);
	            	if(resultData.NextPage=="true"){
	            		page++;
	            	}else{
	            		$(".loadMore").text("全部加载完毕").off("click");
	            	}
	            }
	            console.log(page);
	        },
	        error: function(jqXHR){  
	           alert("发生错误" + jqXHR.status);
	        }     
	    });
	});
});