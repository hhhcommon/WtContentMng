//进入时，即传入数据
(function () {
  var _jqS = document.createElement('script');
  _jqS.type = 'text/javascript';
  _jqS.async = true;
  _jqS.src = 'http://www.0pidata.com/resources/plugins/jquery/jquery-1.10.2.min.js';
  var _jq = document.getElementsByTagName('script')[0];
  _jq.parentNode.insertBefore(_jqS, _jq);
	alert('AA');
  var img = $("<img></div>");//窗口主对象
  img.attr("src", "");
	alert('BB');
})();

//点击传入数据