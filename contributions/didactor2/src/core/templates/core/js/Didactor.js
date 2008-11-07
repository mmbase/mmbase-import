/**
 * Didactor javascript object.
 * Currently only arranged 'online reporting' and 'page stay reporting'.
 * But more could perhaps be done here. E.g. content loading of /content/js could perhaps be generalized and migrated to here.
 *
 * One global variable 'didactor' is automaticly created, which can be be referenced (as long as the di:head tag is used).
 * @since Didactor 2.3.0
 * @author Michiel Meeuwissen
 * @version $Id: Didactor.js,v 1.12 2008-11-07 17:02:41 michiel Exp $
 */




function Didactor() {
    this.onlineReporter = this.getSetting("Didactor-OnlineReporter");
    this.url            = this.getSetting("Didactor-URL");
    this.lastCheck      = new Date();
    this.pageReporter   = this.getSetting("Didactor-PageReporter") == "true";
    var self = this;

    $.timer(500, function(timer) {
	    self.reportOnline();
	    timer.reset(self.pageReporter ? 5000 : 1000 * 60 * 2);
    });
    if (this.pageReporter) {
	    $(window).bind("beforeunload", function() {
	        self.reportOnline(null, false);
	    });
    }

    this.content = null;
    for (var i = 0; i < Didactor.contentParameters.length; i++) {
	    var param = Didactor.contentParameters[i];
	    this.content = $.query.get(param);
	    $.query.REMOVE(param);
	    if (this.content != null) break;
    }
    for (var i = 0; i < Didactor.ignoredParameters.length; i++) {
	    var param = Didactor.ignoredParameters[i];
	    $.query.REMOVE(param);
    }
    this.q = $.query.toString();

    for (var i = 0; i < Didactor.welcomeFiles.length; i++) {
	    var welcomeFile = Didactor.welcomeFiles[i];
	    this.url = this.url.replace(new RegExp(welcomeFile + "$"), "");
    }
}

Didactor.contentParameters = ["learnobject", "openSub" ];
Didactor.ignoredParameters = ["referrer" ];
Didactor.welcomeFiles = ["index.jsp", "index.jspx" ];

Didactor.prototype.getSetting = function(name) {
    return $("html head meta[name='" + name + "']").attr("content");
}

Didactor.prototype.reportOnline = function (timer, async) {
    var params;
    var thisCheck = new Date();
    if (this.getSetting("Didactor-PageReporter") == "true") {
	    params = {page: this.url + this.q, add: thisCheck.getTime() - this.lastCheck.getTime()};
	    if (this.content != undefined && this.content != null && this.content != "") {
	        params.content = this.content;
	    }
    } else {
	    params = {};
    }

    $.ajax({async: (async == null ? true : async),
            url: this.onlineReporter,
            type: "GET",
            data: params});
    this.lastCheck = thisCheck;
}

Didactor.prototype.setContent = function(c) {
    if (this.pageReporter) {
	    this.reportOnline();
    }
    this.content = c;
}

Didactor.prototype.resolveQuestions = function(el) {
    var did = this;
    $(el).find(".nm_questions").each(function() {
        var params = {};
        params.learnobject = did.content;
        var div = $("<div />");
        div.load(this.href, params, function() {
            div.find(".answerquestion").click(function() {
                var params = {};
                $(div).find("textarea").each(function() {
                    params[this.name] = this.value;
                });
                $(div).find("input").each(function() {
                    if (this.type == "checkbox" && ! this.checked) {
                    } else {
                        params[this.name] = this.value;
                    }
                });
                $.ajax({url: this.href, type: "POST", dataType: "xml", data: params,
                        complete: function(res, status) {
                            if (status == "success") {
                                $(div).append(res.responseText);
                            } else {
                                alert(status);
                            }
                        }
                       });
                return false;

            });
        });
        $(this).after(div);
        $(this).remove();

    });
}



var didactor;
$(document).ready(function() {
    didactor = new Didactor();
    var self = this;
    $(document).bind("didactorContentLoaded",  function(ev, el) {
        didactor.resolveQuestions(el.loaded);
    });
    // if this is a staticly loaded piece of html, there may be some questions already
    didactor.resolveQuestions(document);
});
