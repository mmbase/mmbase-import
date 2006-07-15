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
    document.forms[0].parentchannel.value='';
    document.forms[0].action=initUrl;
    document.forms[0].submit();
 }
