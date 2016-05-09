$(function(){
	//打开链接时，自动播放对应声音资源
	$("#zjAudio")[0].play();
	$(".playControl").addClass("play");

	//播放控制面板的播放控制
	$(".playControl").on("click",function(){
		if($(".playControl").hasClass("play")){
			$(".playControl").removeClass("play");
			$("#zjAudio")[0].pause();
		}else{
			$(".playControl").addClass("play");
			$("#zjAudio")[0].play();
		}
	});

	//推荐声音列表的播放控制
	$(".ulBox").on("click",".audioLi",function(){

		//控制面板变化
		$.each($(".play_state"), function(i,val){
		    if($(val).hasClass("playing")){
				$(".playControl").removeClass("play");
		    }else{
		    	$(".playControl").addClass("play");
		    }
		});
		//声音列表变化及声音的控制
		if($(this).children("span").hasClass("playing")){
			$(this).children("span").removeClass("playing").addClass("play_pause");
			$(this).children("audio")[0].pause();
		}else if($(this).children("span").hasClass("play_pause")){
			$(this).children("span").removeClass("play_pause").addClass("playing");
			$(this).children("audio")[0].play();
		}else{
			$.each($(".play_state"), function(i,val){
			    if(val.className!="play_state"){
					$(val).siblings("audio")[0].pause();
			    }
			});

			$(".audioLi").children("span").removeClass("playing").removeClass("play_pause").end().children(".audioIntro").children("h3").css({"color":"#fea734"});
			$(this).children("span").addClass("playing").end().children(".audioIntro").children("h3").css({"color":"#e96600"});

			$(this).children("audio")[0].play();
			//alert("bb");
		}

		
	});
});