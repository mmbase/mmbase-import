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