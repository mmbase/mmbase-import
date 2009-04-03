

function Reloader() {
}

Reloader.prototype.reloadMode = function () {
    var url = '${_}';
    var self = this;
    $.ajax({async: true, url: url, type: "GET", dataType: "xml",
            complete: function(res, status){
		if (status == "success") {
		    document.getElementById('mode-${mode}').innerHTML = res.responseText;
		    restoreTree();
		    storeTree();
		    self.afterReload();
		}
	    }
	   });
    return false;
}

Reloader.prototype.afterReload = function () {
}


var reloader = new Reloader();


function reloadMode() {
    reloader.reloadMode();
}
