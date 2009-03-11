function moveUp() {
   var select = document.forms[0].channels;
   var options = select.options;
   for (i=0; i<options.length; i++) {
     if (select.options[i].selected) {
       var elementToMove = options[i].index;
          if (elementToMove>0 && !options[elementToMove-1].selected) {
             swap(elementToMove,elementToMove-1);
             if(options[elementToMove-1].selected) {
                options[elementToMove].selected = true;
             }
             else {
                options[elementToMove].selected = false;
             }
             options[elementToMove-1].selected = true;
         }
      }
   }
   fillHidden();
}

function moveDown() {
   var select = document.forms[0].channels;
   var options = select.options;
   for (i=options.length-1; i > -1; i--) {
     if (options[i].selected) { 
       var elementToMove = options[i].index;
          if (elementToMove!=-1 && elementToMove<document.forms[0].channels.options.length-1 && !options[elementToMove+1].selected) {
             swap(elementToMove+1,elementToMove);
             if(options[elementToMove+1].selected) {
                options[elementToMove].selected = true;
             }
             else {
                options[elementToMove].selected = false;
             }
             options[elementToMove+1].selected = true;
          }
      }
   }
    fillHidden();
}

function swap(index1, index2) {
   var select = document.forms[0].channels;
   var options = select.options;

   var oldValue = values[index2];
   values[index2]=values[index1];
   values[index1]=oldValue;
   var t = options[index1].text;
   options[index1].text = options[index2].text;
   options[index2].text = t;

}

function fillHidden() {
   var value="";
   for (var i=0;i<values.length;i++) { 
      value = value +  values[i];
      if (i<values.length-1) {
         value=value+",";
      }
   }
   document.forms[0].ids.value=value;
}