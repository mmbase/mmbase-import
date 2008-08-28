

function Didactor() {
    this.onlineReporter = this.getSetting("Didactor-OnlineReporter");
    this.url            = this.getSetting("Didactor-URL");
    this.lastCheck      = new Date();
    var self = this;
    $.timer(2000, function(timer) {
	self.reportOnline();
	timer.reset(self.getSetting("Didactor-PageReporter") == "true" ? 5000 : 1000 * 60 * 2);
    });
    this.content = $.query.get("learnobject");
    $.query.REMOVE("learnobject");

}

Didactor.prototype.getSetting = function(name) {
    return $("html head meta[name='" + name + "']").attr("content");
}

Didactor.prototype.reportOnline = function (timer) {
    console.log(window.location);

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

    $.get(this.onlineReporter, params);
    this.lastCheck = thisCheck;
}

Didactor.prototype.setContent = function(c) {
    this.content = c;
}

var didactor;
$(document).ready(function() {
    didactor = new Didactor();
});

