
function MMCalendar() {
    var self = this;
    this.lastClick = null;
    $(document).ready(function() {
			  self.init();
		      });
}

MMCalendar.prototype.getDay = function(td) {
    var classNames = td.className.split(" ");
    for (var i = 0; i < classNames.length; i++) {
	if (classNames[i].indexOf("day_") == 0) {
	    return parseInt(classNames[i].substring(4));
	}
    }
    return null;
};

MMCalendar.prototype.init = function() {
    var self = this;
    $("table.mm_calendar td.same_month input[type=checkbox]").each(
	function() {
	    if ($(this).is(":checked")) {
		$(this).parent("td").addClass("checked");
	    }
	}
    );
    $("table.mm_calendar td.same_month input[type=checkbox]").click(
	function(ev) {
	    self.toggle($(this).parent("td")[0]);
	    var newClickValue = parseInt($(ev.target).val());

	    // every input representing the same value, should also be changed:
	    if (ev.target.checked) {
		$("td.day_" + newClickValue + " input[type=checkbox]").attr("checked", "checked");
	    } else {
		$("td.day_" + newClickValue + " input[type=checkbox]").removeAttr("checked");
	    }

	    if (ev.shiftKey) {
		if (self.lastClick != null) {
		    var newSelected = self.lastClick.checked;
		    var lastClickValue = parseInt($(self.lastClick).val());
		    var step = newClickValue > lastClickValue ? 1 : -1;
		    for (var i = lastClickValue; i <= newClickValue; i += step) {
			if (newSelected) {
			    $("td.day_" + i + " input[type=checkbox]").attr("checked", "checked");
			    $("td.day_" + i).addClass("checked");
			} else {
			    $("td.day_" + i + " input[type=checkbox]").removeAttr("checked");
			    $("td.day_" + i).removeClass("checked");
			}
		    }
		}
		if (self.lastClick != null) {
		    $(self.lastClick).parents("td").removeClass("selected");
		}
		self.lastClick = null;
	    } else {
		if (self.lastClick != null) {
		    $(self.lastClick).parents("td").removeClass("selected");
		}
		self.lastClick = ev.target;
		$(ev.target).parents("td").addClass("selected");
	    }
	});

    $("table.mm_calendar td.same_month").click(
	function(ev) {
	    $(this).find("input[type=checkbox]").click();
	});
};

MMCalendar.prototype.toggle = function(td) {
    if ($(td).hasClass("checked")) {
	$(td).removeClass("checked");
    } else {
	$(td).addClass("checked");
    }
};

var calendar = new MMCalendar();
