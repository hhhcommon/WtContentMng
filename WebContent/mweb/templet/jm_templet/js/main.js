$(function(){
	//控制播放的节目的顺序号
	var listNum=0;
	//获取播放元素容器
	var audio=$("audio")[0];
	//打开链接时，自动播放对应声音资源
	$("#jmAudio")[0].play();
	$(".playControl").addClass("play");

	//播放控制面板的播放控制
	$(".playControl").on("click",function(){
		if($("#jmAudio")[0]){
			if($(".playControl").hasClass("play")){
				$(".playControl").removeClass("play");
				$("#jmAudio")[0].pause();
			}else{
				$(".playControl").addClass("play");
				$("#jmAudio")[0].play();
			}
		}else{
			if($(".playControl").hasClass("play")){
				$.each($(".play_state"), function(i,val){
				    if(val.className!="play_state"){
				    	$(val).removeClass("playing").addClass("play_pause");
				    	return false;
				    }
				});
				$(".playControl").removeClass("play");
				audio.pause();
			}else{
				$.each($(".play_state"), function(i,val){
				    if(val.className!="play_state"){
				    	$(val).removeClass("play_pause").addClass("playing");
				    	return false;
				    }
				});
				$(".playControl").addClass("play");
				audio.play();
			}
		}
		
	});

	//资源准备就绪后，获取声音长度，否则NaN
	$(audio).on("canplay",function(){
		$(".fullTime").text(formatTime($(this)[0].duration));
	});
	
	//实时获取播放时长和改变进度条进度
	audio.addEventListener("timeupdate",function(){
	    if (!isNaN(this.duration)) {
	        //播放进度条
	        var progressValue = this.currentTime/this.duration*($(".currentMusicBar").width());
	        $('.currentMusicBarRound')[0].style.left = parseInt(progressValue) + 'px';

	        //播放时长
	    	$(".playTime").text(formatTime(Math.round(this.currentTime)));
	    };
	},false);

	//推荐声音列表的播放控制
	$(".ulBox").on("click",".audioLi",function(){
		//声音列表变化及声音的控制
		if($(this).children("span").hasClass("playing")){
			audioPause($(this),audio);
		}else if($(this).children("span").hasClass("play_pause")){
			audioPlay($(this),audio);
		}else{
			$.each($(".play_state"), function(i,val){
			    if(val.className!="play_state"){
					audio.pause();
			    }
			});
			$(".audioLi").children("span").removeClass("playing").removeClass("play_pause").end().children(".audioIntro").children("h3").css({"color":"#fea734"});
			$(this).children("span").addClass("playing").end().children(".audioIntro").children("h3").css({"color":"#e96600"});
			audio.src=$(this).attr("data-src");
			audio.play();
		}

		//从推荐列表选择声音时，播放面板的控制变化
		if($(this).children("span").hasClass("playing")){
			$(".playControl").addClass("play");
		}else{
			//播放当前资源，并改变播放按钮状态
			$(".playControl").removeClass("play");
		}
	});
	//下一个声音
	var isList=false;
	$(".next").on("click",function(){
		if(isList){
			$.each($(".play_state"), function(i,val){
				//如果有正在播放的列表，那么播放下一条，如果没有，则播放第一条
			    if(val.className!="play_state"){
			    	//判断是否为最后一个
			    	if(i>=$(".audioLi").length-1){
		    			$(".next").css({"color":"#afafaf","cursor":"default"});
		    			return false;
		    		}
		    		$(".audioLi").children("span").removeClass("playing").removeClass("play_pause").end().children(".audioIntro").children("h3").css({"color":"#fea734"});
		    		listNum=i+1;
					audio.src=$(".audioLi").eq(listNum).data("src");
			    	audioPlay($(".audioLi").eq(listNum),audio,true);
			    	$(".playControl").addClass("play");
			    	return false;
			    }
			});
		}else{
			audio.src=$(".audioLi").eq(listNum).data("src");
			audioPlay($(".audioLi").eq(listNum),audio,true);
			$(".playControl").addClass("play");
			$(audio).removeAttr("id");
			isList=true;
		}
	});
	//上一个声音
	$(".previous").on("click",function(){
			$.each($(".play_state"), function(i,val){
			    if(val.className!="play_state"){
			    	//判断是否为第一个
		    		if(i<=0){
		    			$(".previous").css({"color":"#afafaf","cursor":"default"});
		    			return false;
		    		}
		    		$(".audioLi").children("span").removeClass("playing").removeClass("play_pause").end().children(".audioIntro").children("h3").css({"color":"#fea734"});
		    		listNum=i-1;
					audio.src=$(".audioLi").eq(listNum).data("src");
			    	audioPlay($(".audioLi").eq(listNum),audio,true);
			    	$(".playControl").addClass("play");
			    	return false;
			    }
			});
	});
// callback:"callbackAction",
/*
	//请求推荐资源列表
	var searchStr=$(".palyCtrlBox").children("h4").text();
	$.jsonp({
        url: "http://192.168.1.13:808/wt/searchByText.do",
        data:{IMEI:"3279A27149B24719991812E6ADBA5583",PCDType:"3",SearchStr:searchStr,ResultType:"0",PageType:"0"},
        success: function(resultData) {
        	alert("成功");
            if (resultData.ReturnType=="1001"){
            	loadRecomList(resultData);
            }
        },
        error: function(jqXHR){  
           alert("发生错误" + jqXHR.status);
        }    
    });
*/
	var resultData={"ResultList":{"AllCount":4,
		                              "List":[
												{"ContentURI":"content\/getContentInfo.do?MediaType=AUDIO&ContentId=4924063","ContentPersons":"资讯早班车","ContentCatalogs":null,"CTime":null,"PlayCount":null,"ContentKeyWord":null,"ContentSubjectWord":null,"ContentTimes":"81000","ContentName":"75亿元资金离场：昨日近14亿元大单抢筹10股","ContentPubTime":null,"ContentPub":"喜马拉雅FM","ContentPlay":"http:\/\/audio.xmcdn.com\/group8\/M03\/29\/E3\/wKgDYFWGIpORmvcoAAoCHbieq9k694.m4a","MediaType":"AUDIO","ContentId":"4924063","ContentDesc":"时事","ContentImg":"http:\/\/fdfs.xmcdn.com\/group4\/M03\/C5\/37\/wKgDs1PzCQ6QbbJvAAKFQf2Cz5Y834_web_large.jpg"},
												{"ContentURI":"content\/getContentInfo.do?MediaType=AUDIO&ContentId=4517714","ContentPersons":"资讯速递","ContentCatalogs":null,"CTime":null,"PlayCount":null,"ContentKeyWord":null,"ContentSubjectWord":null,"ContentTimes":"81000","ContentName":"75亿元资金离场：昨日近14亿元大单抢筹10股[财经中间站]","ContentPubTime":null,"ContentPub":"喜马拉雅FM","ContentPlay":"http:\/\/audio.xmcdn.com\/group14\/M09\/45\/9D\/wKgDY1WmmvmjDTOhAAoToCqLZrg152.m4a","MediaType":"AUDIO","ContentId":"4517714","ContentDesc":"头条","ContentImg":"http:\/\/fdfs.xmcdn.com\/group4\/M09\/03\/8E\/wKgDs1QOtymSWfeyAAT2LsA7-2c346_web_large.jpg"},
												{"ContentURI":"content\/getContentInfo.do?MediaType=AUDIO&ContentId=11978405","ContentPersons":null,"ContentCatalogs":null,"CTime":null,"PlayCount":null,"ContentKeyWord":null,"ContentSubjectWord":null,"ContentTimes":"1942000","ContentName":"田小米原著都市言情广播剧《所有的深爱都是秘密》第三期","ContentPubTime":"2016-01-31","ContentPub":"喜马拉雅FM","SeqInfo":{"ContentSubCount":"2","ContentURI":"content\/getContentInfo.do?MediaType=SEQU&ContentId=2739093","ContentPersons":null,"ContentCatalogs":null,"CTime":null,"PlayCount":null,"ContentKeyWord":null,"ContentSubjectWord":null,"ContentName":"《【个人剧】田小米原著都市言情广播剧《所有的深爱都是秘密》》","ContentPub":"喜马拉雅FM","MediaType":"SEQU","ContentId":"2739093","ContentDesc":null,"ContentImg":"http:\/\/fdfs.xmcdn.com\/group14\/M0B\/0B\/52\/wKgDZFbMCDODcD1vAABXw6okoXw301_web_large.jpg"},"ContentPlay":"http:\/\/audio.xmcdn.com\/group10\/M07\/F5\/73\/wKgDaVas64GyPmn0AO-wAp8KOBI307.m4a","MediaType":"AUDIO","ContentId":"11978405","ContentDesc":null,"ContentImg":"http:\/\/fdfs.xmcdn.com\/group7\/M02\/F7\/C1\/wKgDX1as67KQu7stAAIertr0XuY311_web_large.jpg"},
												{"ContentURI":"content\/getContentInfo.do?MediaType=AUDIO&ContentId=6463705","ContentPersons":null,"ContentCatalogs":null,"CTime":null,"PlayCount":null,"ContentKeyWord":null,"ContentSubjectWord":null,"ContentTimes":"151000","ContentName":"01.娘子军连歌","ContentPubTime":"2015-04-21","ContentPub":"喜马拉雅FM","SeqInfo":{"ContentSubCount":"2","ContentURI":"content\/getContentInfo.do?MediaType=SEQU&ContentId=390411","ContentPersons":null,"ContentCatalogs":null,"CTime":null,"PlayCount":null,"ContentKeyWord":null,"ContentSubjectWord":null,"ContentName":"《天音老唱片5 银屏笙歌》","ContentPub":"喜马拉雅FM","MediaType":"SEQU","ContentId":"390411","ContentDesc":null,"ContentImg":"http:\/\/s1.xmcdn.com\/css\/img\/common\/album_180.jpg?todo"},"ContentPlay":"http:\/\/audio.xmcdn.com\/group9\/M0A\/29\/2E\/wKgDZlWFTUrT1xxcABK_ZPR-vzc940.m4a","MediaType":"AUDIO","ContentId":"6463705","ContentDesc":null,"ContentImg":"http:\/\/s1.xmcdn.com\/css\/img\/common\/track_180.jpg?v=20160428142650"}
											]
									 },
						"TestDuration":3126,
						"ResultType":0,
						"ReturnType":"1001",
						"SessionId":"d8c3ccf8116b"
						}
	loadRecomList(resultData);
});

    function audioPause(obj,audio){
    	obj.children("span").removeClass("playing").addClass("play_pause");
		audio.pause();
    }
    function audioPlay(obj,audio,listPlay){
		if(listPlay){
			obj.children("span").addClass("playing").end().children(".audioIntro").children("h3").css({"color":"#e96600"});
		}else{
			obj.children("span").removeClass("play_pause").addClass("playing");
		}
		audio.play();
    }
//毫秒转时分秒格式
function formatTime(longTime){
	var time=parseFloat(longTime);
	if(time!=null && time !=""){
		if(time<60){
			var s=time;
			if(s<10){
				time="00:0"+s;
			}else{
				time="00:"+s;
			}
		}else if(time>60 && time<3600){
			var m=parseInt(time / 60);
			var s=parseInt(time %60);
			time=m+":"+s;
		}else if(time>=3600 && time<86400){
			var h=parseInt(time / 3600);
			var m=parseInt(time % 3600 /60);
			var s=parseInt(time %3600 %60 %60);
			time=h+":"+m+":"+s;
		}
	}
	return time;
}

//创建相关推荐资源列表
function loadRecomList(resultData){
	var actListLength=resultData.ResultList.List.length;
	 //声明下面需要创建的节点
	var liEle,audioEle,imgDiv,imgOne,imgTwo,imgThree,introDiv,introH,introP,playState;
	//循环加载列表
	for(var i=0;i<actListLength;i++){
	    liEle=$("<li class='audioLi'></li>");
	    liEle.attr({"data-src":resultData.ResultList.List[i].ContentPlay});
	    imgDiv=$("<div class='audioImg'></div>");
	    imgOne=$("<div class='boxF'></div>");
	    imgTwo=$("<div class='boxS'></div>");
	    imgThree=$("<div class='boxT'></div>");
	    imgThree.css({"background-image":"url("+resultData.ResultList.List[i].ContentImg+")"});
	    imgDiv.append(imgOne.append(imgTwo.append(imgThree)));
	    introDiv=$("<div class='audioIntro'>");
	    introH=$("<h3></h3");
	    introH.text(resultData.ResultList.List[i].ContentName);
	    introP=$("<p></p>");
	    introP.text(resultData.ResultList.List[i].ContentDesc);
	    introDiv.append(introH).append(introP);
	    playState=$("<span class='play_state'></span>");
	    
	    liEle.append(imgDiv).append(introDiv).append(playState);
	    $(".ulBox").append(liEle);
	}
}
	