function reloadMode() {
    var url = '${_}';
    $.ajax({async: true, url: url, type: "GET", dataType: "xml",
            complete: function(res, status){
		if (status == "success") {
		    document.getElementById('mode-${mode}').innerHTML = res.responseText;
		    restoreTree();
		    storeTree();
		}
	    }
	   });
    return false;
}
