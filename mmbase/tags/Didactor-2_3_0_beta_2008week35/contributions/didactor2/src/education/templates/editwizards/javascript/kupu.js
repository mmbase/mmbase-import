$(document).ready(function () {
    $("#kupu-save-button").bind("kupu-saved", function() {
	window.top.reloadMode();
    });
});
