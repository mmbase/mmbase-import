


$(document).ready(function() {
    $("body").bind("resize", function() {
	$("div#relations").right(0);
	$("div#main").width($("body").width() - $("div#relations").width());
    });
    var width = $("body").width();
    $("div#relations").width(width * 1 / 3);
    $("div#main").width(width * 2 / 3);
    $("div#title").load('a.jspx');
    $("div#relations").resizable({
	handles: "w",
	resize: function(e) {
	    $("div#main").width($("body").width() - $("div#relations").width());

	}
    });
    $("div#relations ul").accordion();

});
