function addToGroup(name, message, event) {
   var checkboxs = Event.element(event).getInputs('checkbox', name)
   var selected = false;

   for (var i = 0; i < checkboxs.length; i++) {
      if (checkboxs[i].checked) {
         selected = true;
         break;
      }
   }

   if (!selected) {
      alert(message);
      Event.stop(event);
   }
}

function search(){
   document.forms[0].page.value = "0";
   document.forms[0].submit(); 
}
function create() {
   document.forms[0].method.value = "addInit";
   document.forms[0].submit();
}
function selectState(url,ele){
   surl=url+"&state="+ele.value
   //	alert(surl);
	window.location.href =surl;
}
function onState(){
    var sels = document.getElementsByTagName("select");
    for (i = 0; i < sels.length; i++) {
        var sel = sels[i];
        var options = sel.getElementsByTagName("option");
        var v = options[0];
        if ("" == v.value) {
            options[0].parentNode.removeChild(options[0]);
        }
        for (j = 1; j < options.length; j++) {
            if (v.value.toUpperCase() == options[j].value.toUpperCase()) {
                options[j].selected=true;
                options[0].parentNode.removeChild(options[0]);
            }
        }
    }
}
