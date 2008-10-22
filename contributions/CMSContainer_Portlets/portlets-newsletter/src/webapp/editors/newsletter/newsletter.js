function massDelete(confirmmessage) {
    if (confirmmessage) {
        if (confirm(confirmmessage)) {
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
                 document.forms[0].deleteRequest.value = objectnumbers;
                 document.forms[0].method.value = "delete";
                 document.forms[0].submit();
        }
    }   
}

function setOffset(offset) {
    document.forms[0].offset.value = offset;
    document.forms[0].submit();
 }
 function showItem(objectnumber) {
    openPopupWindow("showItem", 500, 500, 'NewsletterBounceAction.do?method=getItem&objectnumber=' + objectnumber);
}
 function sortBy(orderColumn){
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
				 document.forms[0].submit();
            }