/**
 * 基于jquery，基于??
 */
//初始化title文字
$(function() {
  $("div .pagination>.first").attr("title", "首页");
  $("div .pagination>.previous").attr("title", "上页");
  $("div .pagination>.next").attr("title", "下页");
  $("div .pagination>.last").attr("title", "尾页");
});