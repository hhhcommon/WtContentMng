$(function(){
	//控制播放按钮的播与停
	$(".play_btn").on("click",function(){
		var audio=$(this).children("audio")[0];
		if($(this).hasClass("play")){
			$(this).removeClass("play");
			audio.pause();
		}else{
			//停止其他播放资源，并改变其他按钮状态
			//$("audio").hasClass("play")[0].stop();
			$(".play_btn").removeClass("play");
			//播放当前资源，并改变当前按钮状态
			$(this).addClass("play");
			audio.play();
		}
	});
});