function callEditWizard(objectNumber) {
    var url = '../WizardInitAction.do?objectnumber=' + objectNumber;
    url += '&returnurl=' + escape(document.location);
    document.location.href = url;
}

function deleteTask(message) {
	if (confirm(message)) {
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
