function verifyModerationCheckedTopics()
{
	var f = document.form1.topic_id;
	
	if (f.length == undefined)	 {
		if (f.checked) {
			return true;
		}
	}

	for (var i = 0; i < f.length; i++) {
		if (f[i].checked) {
			return true;
		}
	}
	
	alert("${I18n.getMessage("Moderation.SelectTopics")}");
	return false;
}

function validateModerationDelete()
{
	if (verifyModerationCheckedTopics() && confirm("${I18n.getMessage("Moderation.ConfirmDelete")}")) {
		return true;
	}
	
	return false;
}

var oldClasses = {}

function changeTrClass(from, trIndex)
{
	var tr = from.parentNode.parentNode;
	trIndex = trIndex.toString();

	if (from.checked) {
		tr.className = "moderation_highlight";
		oldClasses[trIndex] = new Array();

		for (var i = 0; i < tr.childNodes.length; i++) {
			var node = tr.childNodes[i];

			if (node.nodeName.toUpperCase() == "TD") {
				oldClasses[trIndex].push(node.className);
				node.className = "";
			}
		}
	}
	else {
		tr.className = "";

		for (var i = tr.childNodes.length - 1; i >= 0; i--) {
			var node = tr.childNodes[i];

			if (node.nodeName.toUpperCase() == "TD") {
				node.className = oldClasses[trIndex].pop();
			}
		}
	}	
}