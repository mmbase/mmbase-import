function editRemark(id, oldRemark) {
   var remark = prompt("Opmerking ?",oldRemark);
   if(remark != null) {
	   var form = document.forms[0];
	   checkAllBoolean(false, ''); 
	   form["check_"+id].checked = true;
	   form["remark"].value = remark;
	   form["actionvalue"].value = 'rename';
	   form.submit();
   }
}

function selectTab(val, orderby, laststatus) {
   document.forms[0].orderby.value = orderby;
   document.forms[0].status.value = val;
   if(laststatus==null ||laststatus=="")
		document.forms[0].laststatus.vlaue="true";
   else
   		document.forms[0].laststatus.value=laststatus;
   document.forms[0].submit();
}

function returnOrderBy() {
  return document.forms[0].orderby.value;
}
var isAction = false;

function checkAllElement(element, type) {
   var what = element.checked;
   checkAllBoolean(what, type);
}

function checkAllBoolean(what, type) {
   var namesub = 6 + type.length;

   var el=document.forms[0].elements;
   for (i=0; i<el.length; i++) {
      var e = el[i];
      if (e.name.substr(0,namesub)=="check_" + type) {
         e.checked = what;

         if (what == false) {
            var theElement = document.getElementById(e.name);
            if (theElement != null) {
               theElement.value = null;
            }
         }
      }
   }
}

function setActionValue(value, status, remark) {
   if (submitValid(true)) {
      document.forms[0].actionvalue.value=value;
      if(status) {
        document.forms[0].status.value=status;
      }
      if (value == 'reject') {
         var comment = prompt(remark,"");
         if (comment == null) {
            return false;
         }

         if (comment != ""){ // OK pressed
            document.getElementById("remark").value = comment;
         }
      }
   }
   isAction = true;
   return true;
}

function submitValid(silent) {
 if (isAction || silent) {
      var el=document.forms[0].elements;
      for (i=0; i < el.length; i++) {
         var e=el[i];
         if (e.name.substr(0,6) == "check_") {
            if (e.checked) {
               if (isAction) {
                  sayWait();
               }
               return true;
            }
            if (e.type=="hidden" && e.value == "on") {
               if (isAction) {
                  sayWait();
               }
               return true;
            }
         }
      }
      if (!silent) {
         alert("Geen workflow item geselecteerd");
      }
      isAction = false;
      return false;
   }
   return true;
}

function sayWait() {
    document.getElementById("workflow-canvas").style.display="none";
    document.getElementById("workflow-wait").style.display="block";
}
