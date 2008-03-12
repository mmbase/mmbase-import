
$(document).ready(function(){
    var validator = new MMBaseValidator();
    validator.validateHook = function(valid) {
        document.getElementById('submit').disabled = ! valid;
    }
    validator.prefetchNodeManager("xmlnews");
    validator.setup(window);

    var resizer =  function() {
	var minHeight = $("div#title").height() + $("div#commit").height();
	$("div#main").width($(window).width() - $("div#relations").width());
	$("div#main").height($(window).height() - minHeight);
	$("div#relations").height($(window).height() - minHeight);
	$("div#relations").offset().right = 0;

    }

    $(window).bind("resize", resizer);

    var width = $(window).width();
    $("div#relations").width(width * 1 / 3);
    resizer();
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
