function getCataTree(data){
    //请求加载列表
    var rootPath=getRootPath();
    $.ajax({
      type: "POST",    
      url:rootPath+"common/getCataTreeWithSelf.do",
      dataType: "json",
      data:data,
      success: function(catalogsList){
        if(catalogsList.jsonType=="1"){
          var catLength=catalogsList.data.children.length;
          //动态传建树形节点
          var firstListLi,firstListA,firstListDiv,liChild,ulChild,imgIcon1,imgIcon2,ccLength,secondListLi;
          for(var i=0;i<catLength;i++){
          	  firstListLi=$("<li class='list-group-item node-treeview12'></li>");
              firstListA=$("<a href='#' class='inactive'>"+catalogsList.data.children[i].name+"</a>");
              firstListDiv=$("<div class='toggle'></div>");
              $(firstListLi).attr("data-nodeid",i);
              $(firstListDiv).append(firstListA);
              if(catalogsList.data.children[i].children){
                var ccLength=catalogsList.data.children[i].children.length;
                imgIcon1=$("<span class='fa fa-plus'></span>");
                $(firstListDiv).append(imgIcon1);
                ulChild=$('<ul style="display: none"></ul>');
                for(var j=0;j<ccLength;j++){
                  liChild=$("<li ><a href='#'>"+catalogsList.data.children[i].children[j].name+"</a></li>");
                  $(liChild).children("a").attr("nodeid",j);
                  $(ulChild).append(liChild);
                } 
                $(firstListLi).append(firstListDiv); 
                $(firstListLi).append(ulChild);
                $(".list-group").append(firstListLi);
              }else{
              	$(firstListLi).append(firstListDiv); 
                $(".list-group").append(firstListLi);
              } 
          }
          //展开折叠
          $(".toggle").each(function(){
              $(this).on("click",function(){
                  index=$(this).parent().attr("data-nodeid");
                  //没有二级分类的情况，直接出现一级分类详情
                  if(!catalogsList.data.children[index].children){
                      //出现一级分类详情
                      index=$(this).parent().attr("data-nodeid");
                      var Fname,Fchecked,Fchildern,Fnames;
                      Fname=catalogsList.data.children[index].name;
                      Fchecked=catalogsList.data.children[index].checked;
                      Fchildern=0;
                      $(".claDetailArea .detail").html("");
                      Fnames=$("<li class='Fname'><span class='span1'>名字：</span><span class='span2'>"+Fname+"</span></li>");
                      Fchecked=$("<li class='Fname'><span class='span1'>是否选中：</span><span class='span2'>"+Fchecked+"</span></li>");
                      Fchildern=$("<li class='Fname'><span class='span1'>子节点数：</span><span class='span2'>"+Fchildern+"</span></li>");
                      $(".claDetailArea .detail").append(Fnames);
                      $(".claDetailArea .detail").append(Fchecked);
                      $(".claDetailArea .detail").append(Fchildern);
                  }else{
                      //有二级分类，判断二级菜单是否展开
                      if($(this).siblings('ul').css('display')=='none'){
                        //展开二级分类
                         $(this).children('span').removeClass('fa fa-plus');
                         $(this).children('span').addClass("fa fa-minus");
                         $(this).siblings('ul').slideDown(100).children('li');
                         //出现一级分类详情
                         var Fname,Fchecked,Fchildern,Fnames;
                         Fname=catalogsList.data.children[index].name;
                         Fchecked=catalogsList.data.children[index].checked;
                         Fchildern=catalogsList.data.children[index].children.length;
                         $(".claDetailArea .detail").html("");
                         Fnames=$("<li class='Fname'><span class='span1'>名字：</span><span class='span2'>"+Fname+"</span></li>");
                         Fchecked=$("<li class='Fname'><span class='span1'>是否选中：</span><span class='span2'>"+Fchecked+"</span></li>");
                         Fchildern=$("<li class='Fname'><span class='span1'>子节点数：</span><span class='span2'>"+Fchildern+"</span></li>");
                         $(".claDetailArea .detail").append(Fnames);
                         $(".claDetailArea .detail").append(Fchecked);
                         $(".claDetailArea .detail").append(Fchildern);
                      }else{
                          //隐藏二级分类
                          $(this).children('span').removeClass('fa fa-minus');
                          $(this).children('span').addClass("fa fa-plus");
                          $(this).siblings('ul').slideUp(100);
                          //隐藏一级分类详情
                          $(".claDetailArea .detail").html("");
                      }
                  }
              })
          })
          //点击二级分类，出现二级分类详情
          $("a[nodeid]").each(function(){
            $(this).on("click",function(){
              indexs=$(this).attr("nodeid");
              var Sname,Schecked,ScTime,Snames;
              Sname=catalogsList.data.children[index].children[indexs].name;
              Schecked=catalogsList.data.children[index].children[indexs].checked;
              ScTime=catalogsList.data.children[index].children[indexs].attributes.cTime;
              $(".claDetailArea .detail").html("");
              Snames=$("<li class='Sname'><span class='span1'>名字：</span><span class='span2'>"+Sname+"</span></li>");
              Schecked=$("<li class='Sname'><span class='span1'>是否选中：</span><span class='span2'>"+Schecked+"</span></li>");
              ScTime=$("<li class='Sname'><span class='span1'>创建时间：</span><span class='span2'>"+ScTime+"</span></li>");
              $(".claDetailArea .detail").append(Snames);
              $(".claDetailArea .detail").append(Schecked);
              $(".claDetailArea .detail").append(ScTime);   
            })
          })
                  
        }
       }     
     })
    }

