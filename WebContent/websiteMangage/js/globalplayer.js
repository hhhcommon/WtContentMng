$(function(){
  /*s--全局播放器*/
  $(document).on("click",".glp_mini",function(){//全局播放器面板的展开与折叠
    $(this).css({"left":'-60px',"transition" :"all 0.1s ease 0s"});
    $(this).siblings(".glp_block").css({"left":'0px',"transition" :"all 0.1s ease 0s"});
  });
  $(document).on("click",".fold_panel_img",function(){//全局播放器面板的展开与折叠
    $(this).parents(".glp_block").css({"left":'-1080px',"transition" :"all 0.1s ease 0s"});
    $(this).parents(".glp_block").siblings(".glp_mini").css({"left":'0px',"transition" :"all 0.1s ease 0s"});
  });
  /*e--全局播放器*/
})
