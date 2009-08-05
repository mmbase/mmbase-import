function showInfo(assetType, objectnumber) {
      var infoURL;
      infoURL = '../resources/';
      infoURL += assetType.toLowerCase().substring(0,assetType.length-1);
      infoURL += 'info.jsp?objectnumber=';
      infoURL += objectnumber;
      openPopupWindow(assetType.toLowerCase()+'info', '900', '500', infoURL);
}

function unpublish(parentchannel, objectnumber) {
    var url = "AssetDeleteAction.do";
    url += "?channelnumber=" + parentchannel;
    url += "&action=unlink";
    url += "&returnurl=" + escape(document.location + "&refreshchannel=true");
    url += "&objectnumber=" + objectnumber;

    document.location.href = url;
}

function selectChannel(channel, path) {
    var newDirection=document.forms['initForm'].direction.value;
    var type=document.forms['initForm'].order.value;
    var offset = document.forms['initForm'].offset.value;
    document.location = "../Asset.do?action=moveAssetToChannel&parentchannel=" + moveParentChannel
    + "&newparentchannel=" + channel + "&objectnumber="
            + moveContentNumber + "&orderby=" + type + "&direction="
            + newDirection + '&offset=' + offset;
}

function permanentDelete(objectnumber, message, offset) {
    if (confirm(message)) {
       var url = "DeleteAction.do";
       url += "?objectnumber=" + objectnumber;
       url += "&returnurl=" + escape(document.location);
      url += "&offset=" + offset;

       document.location.href = url;
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

function deleteAsset(objectnumber, confirmmessage) {
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
                 document.forms[0].deleteAssetRequest.value = "massDelete:"+objectnumbers;
                 document.forms[0].submit();
           }
           else {
              document.forms[0].deleteAssetRequest.value = "permanentDelete:" + objectnumber;
              document.forms[0].submit();
           }
        }
    }
    else {
        document.forms[0].deleteAssetRequest.value = "moveToRecyclebin:" + objectnumber;
        document.forms[0].submit();
    }
}