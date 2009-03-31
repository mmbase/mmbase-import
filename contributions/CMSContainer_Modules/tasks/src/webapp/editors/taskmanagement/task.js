function callEditWizard(objectNumber) {
    var url = '../WizardInitAction.do?objectnumber=' + objectNumber;
    url += '&returnurl=' + escape(document.location);
    document.location.href = url;
}

function deleteTask(objectNumber) {
    var url = './DeleteTaskAction.do?objectnumber=' + objectNumber;
    document.location.href = url;
}

function massDeleteTask(message) {
	if (confirm(message)) {
		document.forms['taskForm'].submit();
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
