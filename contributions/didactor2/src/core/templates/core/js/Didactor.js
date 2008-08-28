

function Didactor() {
    this.onlineReporter = this.getSetting("Didactor-OnlineReporter");
    this.url            = this.getSetting("Didactor-URL");
    this.lastCheck      = new Date();
    var self = this;
    $.timer(500, function(timer) {
	self.reportOnline();
	timer.reset(self.getSetting("Didactor-PageReporter") == "true" ? 5000 : 1000 * 60 * 2);
    });
    $(window).bind("beforeunload", function() {
	self.reportOnline(null, false);
    });
    this.content = $.query.get("learnobject");
    $.query.REMOVE("learnobject");

}

Didactor.prototype.getSetting = function(name) {
    return $("html head meta[name='" + name + "']").attr("content");
}

Didactor.prototype.reportOnline = function (timer, async) {
    var params;
    var thisCheck = new Date();
    if (this.getSetting("Didactor-PageReporter") == "true") {
	params = {page: this.url + $.query.toString(), add: thisCheck.getTime() - this.lastCheck.getTime()};
	if (this.content != null) {
	    params.content = this.content;
	}
    } else {
	params = {};
    }

    $.ajax({async: (async == null ? true : async), url: this.onlineReporter, type: "GET", data: params});
    this.lastCheck = thisCheck;
}

Didactor.prototype.setContent = function(c) {
    reportOnline(null, false);
    this.content = c;
}

var didactor;
$(document).ready(function() {
    didactor = new Didactor();
});

