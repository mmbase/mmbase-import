
$(document).ready(function(){
    var validator = new MMBaseValidator();
    validator.validateHook = function(valid) {
        document.getElementById('submit').disabled = ! valid;
    }
    validator.prefetchNodeManager("wikiobjects");
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
    $("div#relations ul > li > a.toggle").click(function(e) {
	$(e.target).parent().children("div").slideToggle("fast");
    });

    $("div#preview").draggable({handle: ">h1"});
    $("div#preview > a").click(function() {
	var params = {};
	params['objectnumber'] = this.id.substring(7);
	$("form#transaction").find("input[@checked], input[@type='text'], input[@type='hidden'], input[@type='password'], option[@selected], textarea")
	.each(function() {
	    params[ this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;
	});

	$.post("preview.jspx", params, function(data, textStatus) {
	    $("div#preview > div").replaceWith(data.documentElement);
	});
	//$("div#preview > div").load("preview.jspx", null, function() { })
	return false;
    });
});
