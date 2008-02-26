
$(document).ready(function(){
    var validator = new MMBaseValidator();
    validator.validateHook = function(valid) {
        document.getElementById('submit').disabled = ! valid;
    }
    validator.prefetchNodeManager("xmlnews");
    validator.setup(window);

    $(window).bind("resize", function() {
	$("div#main").width($(window).width() - $("div#relations").width());
	$("div#main").height($(window).height());
	$("div#relations").height($(window).height());
    });
    var width = $(window).width();
    $("div#relations").width(width * 1 / 3);
    $("div#relations").height($(window).height());
    $("div#main").width(width * 2 / 3);
    $("div#relations").resizable({
	handles: "w",
	resize: function(e) {
	    $("div#main").width($(window).width() - $("div#relations").width());

	}
    });
    $("div#relations ul > li > div").slideUp("fast");
    $("div#relations ul > li > a").click(function(e) {
	$(e.target).parent().children("div").slideToggle("fast");
    });
});
