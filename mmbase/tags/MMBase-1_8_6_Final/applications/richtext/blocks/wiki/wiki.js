
$(document).ready(function(){
    {
	// Set up validator, this marks input fields as incorrect and adds error-message if so.
	var validator = new MMBaseValidator();
	validator.validateHook = function(valid) {
	    // if invalid, then you can't submit the form
            document.getElementById('submit').disabled = ! valid;
	}
	// avoid some ajax, by fetching the information for all of the fields of this node manager in one go
	$(document).find("input[name='objectype']").each(function() {
	    validator.prefetchNodeManager(this.value);
	    validator.setup(window);
	});
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
	// remove the default contents of the comments input box.
	$(document).find("input[name = 'comments']").one("focus", function() {
	    this.value = "";
	});
    }

    {
	// Dynamic stuff from the #relations frame
	/*
	$("div#relations").resizable({
	    handles: "w",
	    resize: function(e) {
		$("div#main").width($(window).width() - $("div#relations").width());

	    }
	});
*/
	$("div#relations ul > li > div").hide();
	$("div#relations ul > li > a.toggle").click(function(e) {
	    $(e.target).parent().children("div").slideToggle("fast");
	});
    }

    {
	// Dynamic sstuff from the #preview frame
	//$("div#preview").draggable({handle: ">h1"});

	$("div#preview > a").click(function() {
	    // actual reloading or preview happens here\
	    var params = {};
	    params['objectnumber'] = this.id.substring(7);
	    $("form#transaction").find("input[@checked], input[type='text'], input[type='hidden'], input[type='password'], option[@selected], textarea")
	    .each(function() {
		params[ this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;
	    });
	    params['transaction'] = 'transaction';
	    $.post("preview.jspx", params, function(data, textStatus) {
		$("div#preview > div").replaceWith(data.documentElement);
	    });
	    //$("div#preview > div").load("preview.jspx", null, function() { })
	    return false;
	});
    }

    MMBaseRelater.ready(function(relater) {
	relater.relateCallBack = function(tr) {
	    var nodeNumber = relater.getNumber(tr);
	    // now paste this into the wiki-area
	    $("body").find("table.wiki textarea.mm_f_body").each(function() {
		var before = this.value.substring(0, this.selectionStart);
		var after = this.value.substring(this.selectionEnd, this.value.length);
		var selection = this.value.substring(this.selectionStart, this.selectionEnd);
		this.value = before + "[" + nodeNumber + (selection.length > 0 ? ":" : "") + selection + "]" + after;
	    });


	}
    });
    var view = function() {
	var begin = this.selectionStart;
	var end = begin;
	var c = this.value.charAt(begin);
	while (begin > 0 && c >= '0' && c <= '9') {
	    begin--;
	    c = this.value.charAt(begin);
	}
	begin++;
	c = this.value.charAt(end);
	while (end < this.value.length && c >= '0' && c <= '9') {
	    end++;
	    c = this.value.charAt(end);
	}
	if (end > begin) {
	    var number = this.value.substring(begin, end);
	    $("body").find(".show_node").each(function() {
		if (this.number != number) {
		    this.number = number;
		    if (this.originalContent == null) {
			this.originalContent = this.textContent;

		    }
		    $(this).load("preview.jspx", {objectnumber: number});

		}
	    });

	} else {
	    $("body").find(".show_node").each(function() {
		if (this.number != number) {
		    this.number = number;
		    var content = "<p>" + this.originalContent + "</p>";
		    $(this).empty();
		    $(this).append($(content));
		}
	    });
	}

    };
    $("body").find("table.wiki textarea.mm_f_body").keyup(view);
    $("body").find("table.wiki textarea.mm_f_body").click(view);

    $("body").find("#submitTest").click(function(el) {
	$("body").find(".mm_related").each(function() {
	    this.relater.commit(el);
	});
    });

});
