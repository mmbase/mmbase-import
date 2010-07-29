
$(document).ready(function(){
    {
	// Set up validator, this marks input fields as incorrect and adds error-message if so.
	var validator = new MMBaseValidator();
	validator.validateHook = function(valid) {
	    // if invalid, then you can't submit the form
            document.getElementById('submit').disabled = ! valid;
	}
	// avoid some ajax, by fetching the information for all of the fields of this node manager in one go
	validator.prefetchNodeManager("wikiobjects");
	validator.setup(window);
    }


    {
	// The size of some of the areas are determined by js.
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
    }

    {
	// Dynamic stuff from the #relations frame
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
    }

    {
	// Dynamic sstuff from the #preview frame
	$("div#preview").draggable({handle: ">h1"});

	$("div#preview > a").click(function() {
	    // actual reloading or preview happens here\
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

    }

});
