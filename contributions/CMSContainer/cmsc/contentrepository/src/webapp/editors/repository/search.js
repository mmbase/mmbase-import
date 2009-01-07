 function setOffset(offset) {
    document.forms[0].offset.value = offset;
    document.forms[0].submit();
 }

 function orderBy(orderColumn) {
    var oldOrderColumn = document.forms[0].order.value;
    
    if (oldOrderColumn == orderColumn) {
       // order column is not changed so change direction
       var oldDirection = document.forms[0].direction.value;
       if (oldDirection == '1') {
          document.forms[0].direction.value = '2';
       }
       else {
          document.forms[0].direction.value = '1';
       }
    }
    else {
       document.forms[0].order.value = orderColumn;
       document.forms[0].direction.value = '1';
    }
    
    document.forms[0].submit();
 }

 function selectContenttype(initUrl) {
    // parentchannel is only there when linking is active...
    if(document.forms[0].parentchannel){
        try {
            document.forms[0].parentchannel.value='';
        } catch (e) {
            ;
        }
    }
    // This doesn't work in IE...
    // document.forms[0].action=initUrl;
    document.forms[0].submit();
 }

 function selectTab(mode) {
    document.forms[0].mode.value=mode;
    document.forms[0].search.value='false';
    document.forms[0].submit();
 }

function selectAll(value, formName, elementPrefix) {
   var elements = document.forms[formName].elements;
   for (var i = 0; i < elements.length; i++) {
      if (elements[i].name.indexOf(elementPrefix) == 0) {
          elements[i].checked = value;
      }
   }
}

function selectChannel(channel, path) {
   document.forms[0].parentchannel.value=channel;
   document.forms[0].parentchannelpathdisplay.value=path;
}

function selectElement(element, title, url, width, height, description) {
   window.top.opener.selectElement(element, title, url, width, height, description);
   window.top.close();
}

function selectAssettype(initUrl) {
    // parentchannel is only there when linking is active...
    if(document.forms[0].parentchannel){
        try {
            document.forms[0].parentchannel.value='';
        } catch (e) {
            ;
        }
    }
    // This doesn't work in IE...
    // document.forms[0].action=initUrl;
    document.forms[0].submit();
 }
