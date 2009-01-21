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
