function callEditWizard(objectNumber) {
    var url = '../WizardInitAction.do?objectnumber=' + objectNumber;
    url += '&returnurl=' + escape(document.location);
    document.location = url;
}

function unpublish(channelnumber, objectnumber) {
    var url = "LinkToChannelAction.do";
    url += "?channelnumber=" + channelnumber;
    url += "&action=unlink";
    url += "&returnurl=" + escape(document.location + "&refreshchannel=true");
    url += "&objectnumber=" + objectnumber;

    document.location.href = url;
}

function openPreview(url) {
    return openPopupWindow("preview", 750, 550, url);
}

function showItem(objectnumber) {
    return openPopupWindow("showItem", 500, 500, 'showitem.jsp?objectnumber=' + objectnumber);
}

function info(objectNumber) {
    openPopupWindow("info", 500, 500, "../repository/showitem.jsp?objectnumber=" + objectNumber);
}

function moveUp(objectNumber, channel) {
    move("up", objectNumber, channel);
}

function moveDown(objectNumber, channel) {
    move("down", objectNumber, channel);
}

function move(direction, objectNumber, channel) {
    var url = 'MoveContent.do?direction=' + direction + '&objectnumber=' + objectNumber + '&parentchannel=' + channel;
    document.location = url;
}


   
function sortBy(orderColumn, channel) {
    var offset = document.forms[0].offset.value;
    var oldOrderColumn = document.forms[0].order.value;
    
    if (oldOrderColumn == orderColumn) {
       // order column is not changed so change direction
       var oldDirection = document.forms[0].direction.value;
       
       if (oldDirection == 'down') {
          document.forms[0].direction.value = 'up';
       }
       else {
          document.forms[0].direction.value = 'down';
       }
    }
    else {
       document.forms[0].order.value = orderColumn;
       document.forms[0].direction.value = 'down';
    }
    newDirection=document.forms[0].direction.value;
    type=document.forms[0].order.value;
    var url = 'Content.do?orderby='+type+'&parentchannel=' + channel+'&direction='+newDirection+'&offset='+offset;
    
    document.location = url;
    
 }   


var moveContentNumber;
var moveParentChannel;
function moveContent(objectNumber, parentChannel) {
    moveContentNumber = objectNumber;
    moveParentChannel = parentChannel;
    openPopupWindow('selectchannel', 340, 400);
}

function selectChannel(channel, path) {
    var newDirection=document.forms[0].direction.value;
    var type=document.forms[0].order.value;
    var offset = document.forms[0].offset.value;
    document.location = "../Content.do?action=moveContentToChannel&parentchannel=" + moveParentChannel + "&newparentchannel=" + channel + "&objectnumber=" + moveContentNumber+"&orderby="+type+"&direction="+newDirection+'&offset='+offset;;
}

function refreshChannels() {
    refreshFrame('channels');
    if (window.opener) {
        window.close();
    }
}

function deleteContent(objectnumber, confirmmessage) {
    if (confirmmessage) {
        if (confirm(confirmmessage)) {
           if(objectnumber == 'massdelete'){
                 var checkboxs = document.getElementsByTagName("input");
                 var objectnumbers = '';
                 for(i = 0; i < checkboxs.length; i++) {
                    if(checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
                       objectnumbers += checkboxs[i].value+",";
                    }
                 }
                 if(objectnumbers == ''){
                    return ;
                 }
                 objectnumbers = objectnumbers.substr(0,objectnumbers.length - 1);
                 document.forms[0].deleteContentRequest.value = "massDelete:"+objectnumbers;
                 document.forms[0].submit();
           }
           else {
              document.forms[0].deleteContentRequest.value = "permanentDelete:" + objectnumber;
              document.forms[0].submit();
           }
        }
    }
    else {
        document.forms[0].deleteContentRequest.value = "moveToRecyclebin:" + objectnumber;
        document.forms[0].submit();
    }
}

function selectAll(value, formName, elementPrefix) {
   var elements = document.forms[formName].elements;
   for (var i = 0; i < elements.length; i++) {
      if (elements[i].name.indexOf(elementPrefix) == 0) {
          elements[i].checked = value;
      }
   }
}

function massMove( parentChannel,url) {
   var checkboxs = document.getElementsByTagName("input");
   var objectnumbers = '';
   for(i = 0; i < checkboxs.length; i++) {
      if(checkboxs[i].type == 'checkbox' && checkboxs[i].name.indexOf('chk_') == 0 && checkboxs[i].checked) {
         objectnumbers += checkboxs[i].value+",";
      }
   }
   if(objectnumbers == ''){
      return ;
   }
    moveContentNumber = objectnumbers.substr(0,objectnumbers.length - 1);
    moveParentChannel = parentChannel;
    openPopupWindow('selectchannel', 340, 400,url);
}
