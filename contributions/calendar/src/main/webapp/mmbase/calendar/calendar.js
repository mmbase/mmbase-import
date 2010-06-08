
function MMCalendar() {
    var self = this;
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
    $("table.mm_calendar td.same_month input[type=checkbox]").click(
	function(ev) {

	});

    $("table.mm_calendar td.same_month").click(
	function(ev) {
	    console.log(ev);
	});
};

var calendar = new MMCalendar();
