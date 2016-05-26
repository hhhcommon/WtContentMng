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
        	obj.html("<div style='text-align:center;height:300px;line-height:200px;'>获取数据发生错误："+jqXHR.status+"</div>");
        }     
    });
}	