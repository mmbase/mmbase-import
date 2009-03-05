/**
 * Didactor javascript object.
 * Currently only arranged 'online reporting' and 'page stay reporting'.
 * But more could perhaps be done here. E.g. content loading of /content/js could perhaps be generalized and migrated to here.
 *
 * One global variable 'didactor' is automaticly created, which can be be referenced (as long as the di:head tag is used).
 * @since Didactor 2.3.0
 * @author Michiel Meeuwissen
 * @version $Id: Didactor.js,v 1.18 2009-03-05 08:28:03 michiel Exp $
 */


function Didactor() {
    var self = this;
    $(document).ready(function() {
        self.onlineReporter = self.getSetting("Didactor-OnlineReporter");
        self.url            = self.getSetting("Didactor-URL");
        self.root           = self.getSetting("Didactor-Root");
        self.lastCheck      = new Date();
        self.pageReporter   = self.getSetting("Didactor-PageReporter") == "true";
        self.lastPage       = self.getSetting("Didactor-LastPage"); // not currently used
        self.usedFrames     = {};
        self.questions      = {};

        self.fragments      = [];
        {
            var url = document.location.href;
            var fragmentIndex = url.indexOf('#');
            if (fragmentIndex > 0) {
                self.fragments = url.substring(fragmentIndex + 1).split('_');
            }
            if (self.fragments.length > 0) {
                self.openContent(self.fragments[0]);
            }
        }


        $.timer(500, function(timer) {
	        self.reportOnline();
            var interval = self.getSetting("Didactor-PageReporterInterval");
            interval = interval == "" ? 10000 : interval * 1000;
            if (interval < 10000) interval = 10000;
	        timer.reset(self.pageReporter ? interval : 1000 * 60 * 2);
        });
        if (self.pageReporter) {
	        $(window).bind("beforeunload", function() {
	            self.reportOnline(null, false);
	        });
        }

        self.content = null;
        for (var i = 0; i < Didactor.contentParameters.length; i++) {
	        var param = Didactor.contentParameters[i];
	        self.content = $.query.get(param);
	        $.query.REMOVE(param);
	        if (self.content != null) break;
        }
        self.block = self.content; // This is the content as defined by the URL. 'block' will no be changed.
        for (var i = 0; i < Didactor.ignoredParameters.length; i++) {
	        var param = Didactor.ignoredParameters[i];
	        $.query.REMOVE(param);
        }
        self.q = $.query.toString();


        for (var i = 0; i < Didactor.welcomeFiles.length; i++) {
	        var welcomeFile = Didactor.welcomeFiles[i];
	        self.url = self.url.replace(new RegExp(welcomeFile + "$"), "");
        }

        $(document).bind("didactorContentLoaded",  function(ev, data) {
            self.setContent(data.number);
            self.resolveQuestions(data.loaded);
        });
        $(document).bind("didactorContent",  function(ev, data) {
            self.setContent(data.number);
            self.setUpQuestionEvents(data.loaded);
        });

        $(document).bind("didactorContentBeforeUnload",  function(ev, el) {
        self.saveQuestions();
        });
        $(document).bind("beforeunload", function() {
            self.saveQuestions();
        });
        // if this is a staticly loaded piece of html, there may be some questions already
        self.resolveQuestions(document);



        $(document).bind("didactorContent", function(ev, data) {
            var url = document.location.href;
            var fragmentIndex = url.indexOf('#');
            var fragment = url.substring(fragmentIndex);
            var i = fragment.indexOf('learnblock_');
            if (i > -1) {
                learnblock = fragment;
            }
            $(".subnavigationPage  ul.navigation li").each(function() {
                var href = $(this).find("a")[0].href;
                var i = href.indexOf('#');
                var anchor = href.substring(i) + "_block";
                if (learnblock == null) {
                    learnblock = anchor;
                }

                $(this).click(function() {
                    $(".subnavigationPage  ul.navigation li").removeClass("active");
                    $(this).addClass("active");
                    $(learnblock).hide();
                    learnblock = anchor;
                    $(learnblock).show();
                    document.location.href = href;
                    return false;
                });

            });
        });

    });
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
	    params = {
            page: this.url + this.q,
            add: thisCheck.getTime() - this.lastCheck.getTime()
        };
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

Didactor.prototype.setUpQuestionEvents = function(div) {
    var did = this;
    $(div).find("div.question").each(function() {
        var qdiv = this;
        var a = qdiv.a;
        $(qdiv).find("textarea").keyup(function() {
            did.questions[a][0] = true;
        });
        $(qdiv).find("input").change(function() {
            did.questions[a][0] = true;
        });
        $(qdiv).find(".answerquestion").click(function() {
            var params = {};
            $(qdiv).find("textarea").each(function() {
                params[this.name] = this.value;
            });
            $(qdiv).find("input").each(function() {
                if (this.type == "checkbox" && ! this.checked) {
                } else {
                    params[this.name] = this.value;
                }
            });
            $.ajax({url: this.href, async: false, type: "POST", dataType: "xml", data: params,
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
}

Didactor.prototype.resolveQuestions = function(el) {
    var did = this;
    $(el).find(".nm_questions").each(function() {
        var div = $("<div  />");
        var d = div[0];
        var a = this;
        if (did.questions[a] == null) {
            did.questions[a] = [false, d];
        }
        div.load(a.href + "&learnobject=" + did.content, null, function() {
            div.find("div.question")[0].a = a;
            did.setUpQuestionEvents(d);
        });
        $(this).after(div);
        $(this).remove();
    });

}

Didactor.prototype.saveQuestions = function() {
    var didactor = this;
    for (key in didactor.questions) {
        var status = didactor.questions[key];
        var changed = status[0];
        var div = status[1];
        if (changed) {
            $(div).find(".answerquestion").click();
            didactor.questions[key][0] = false;
        }
    }
}


/**
 * Request content using AJAX from the server
 */
Didactor.prototype.requestContent = function(href, number) {
    var contentEl = document.getElementById('contentFrame');
    $(document).trigger("didactorContentBeforeUnload",  { unloaded: contentEl });
    var self = this;
    var content = this.usedFrames[href];
    if (content == null) {
        loadIconOn();
        $.ajax({async: true, url: href, type: "GET", dataType: "xml", data: null,
                    complete: function(res, status){
                    loadIconOff();
                    if (status == "success") {
                        $(contentEl).empty();
                        $(document).trigger("didactorContentBeforeLoaded",  { response: res, number: number });
                        $(contentEl).append(res.responseText);
                        // console.log("updating " + contentEl + "with" + xmlhttp.responseXML);
                        contentEl.validator = new MMBaseValidator();
                        //contentEl.validator.logEnabled = true;
                        //contentEl.validator.traceEnabled = true;
                        contentEl.validator.validateHook = function(valid) {
                            var buttons = $(contentEl).find("input.formbutton");
                            for (i = 0; i < buttons.length; i++) {
                                var disabled = (contentEl.validator.invalidElements > 0);
                                buttons[i].disabled = disabled;
                                // just because IE does not recognize input[disabled]
                                // IE SUCKS
                                buttons[i].className = "formbutton " + (disabled ? "disabled" : "enabled");
                            }
                        };
                        contentEl.validator.validatePage(false, contentEl);
                        contentEl.validator.addValidation(contentEl);
                        check(res.responseXML.documentElement.getAttribute('class'));
                        document.href_frame = href;
                        var array = [];
                        // in case it is more than one element (e.g. comments or so), store all childnodes.

                        try {
                            for (var i = 0; i < contentEl.childNodes.length; i++) {
                                array.push(contentEl.childNodes[i]);
                            }
                        } catch (ex) {
                            alert(ex);
                        }
                        self.usedFrames[href] = array;
                        if ($.browser.msie) {
                            if ($.browser.version.substr(0, 3) <= 6.0) {
                                // alert("IE 6 is a horrible browser which cannot do this correctly at once
                                setTimeout(function() {
                                        $(contentEl).empty();
                                        for (var i=0; i < array.length; i++) {
                                            contentEl.appendChild(array[i]);
                                        }
                                        $(document).trigger("didactorContentLoaded",  { loaded: contentEl, number: number });
                                        $(document).trigger("didactorContent",  { loaded: contentEl, number: number });
                                    }, 500);
                            }
                        } else {
                            $(document).trigger("didactorContentLoaded",  { loaded: contentEl, number: number });
                            $(document).trigger("didactorContent",  { loaded: contentEl, number: number });
                        }

                    }
                }
           });
   } else {
       $(contentEl).empty();
       for (var i = 0; i < content.length; i++) {
           contentEl.appendChild(content[i]);
       }
       document.href_frame = href;
       $(document).trigger("didactorContent",  { loaded: contentEl, number: number });
   }
    //scrollToTop();
};




/**
 * Opens content with a certain number
 * @param type (optional, is supposed to be absent if first argument numeric). The type of the content.
 * @param number MMBase object number as an integer.
 * @param navigationElement (option) The element which was used to open this content. It'll receive a class
 * 'active'
 */
Didactor.prototype.openContent = function(type, number, navigationElement) {
    // The 'type' argument is optional.
    // So, of the first argument is numeric. Interpret that has the 'number".
    if (/^[+-]?\d+$/.test(type)) {
        navigationElement = number;
        number = type;
        type = null;
    }
    if (this.currentNavigationElement != null) {
        $(this.currentNavigationElement).removeClass("active");
    }

    if ( number > 0 ) {
        currentnumber = number;
    }

    var href = addParameter(this.root + 'content/', 'object=' + number);
    if (type != null && type != '') {
        href = addParameter(href, 'type=' + type);
    }
    this.requestContent(href, number);
    this.currentNavigationElement = navigationElement;
    if (this.currentNavigationElement != null) {
        $(this.currentNavigationElement).addClass("active");
    }

};


var didactor = new Didactor();
