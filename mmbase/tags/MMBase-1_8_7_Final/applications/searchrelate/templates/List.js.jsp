// -*- mode: javascript; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:bundle basename="org.mmbase.searchrelate.resources.searchrelate">
<mm:content type="text/javascript" expires="0">

/**
 * This javascript binds to a div.list.
 *
 * This div is suppose to contain an <ol> with <a class="delete" />, and a <a class="create" />
 *
 * Items in the list can be added and deleted. They can also be edited (with validation).
 * The user does not need to push a commit button. All data is implicitely committed (after a few second of inactivity, or before unload).
 *
 * Custom events (called on the associated div)
 * -  mmsrRelatedNodesReady
 * -  mmsrCreated
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */


$(document).ready(function() {
    var l = List; // hoping to make IE a bit faster
    $(document).find("div.list").each(function() {
        if (this.list == null) {
            this.list = new l(this);
        }
    });
    $(document).find("div.list:last").each(function() {
        l.seq = $(this).find("input[name = 'seq']")[0].value;
    });
});




function List(d) {
    this.div = d;
    var self = this;

    this.callBack = null; // called on delete and create


    var listinfos  = this.find("listinfo");

    this.type      = listinfos.find("input[name = 'type']")[0].value;
    this.item      = listinfos.find("input[name = 'item']")[0].value;
    this.source    = listinfos.find("input[name = 'source']")[0].value;
    this.icondir   = listinfos.find("input[name = 'icondir']")[0].value;
    this.createpos = listinfos.find("input[name = 'createpos']")[0].value;

    this.lastCommit = null;

    this.defaultStale = 1000;

    this.valid = true;
    this.validator = new MMBaseValidator();
    this.validator.lang = "${requestScope['javax.servlet.jsp.jstl.fmt.locale.request']}";
    this.validator.prefetchNodeManager(this.type);
    this.validator.setup(this.div);
    this.validator.validateHook =  function(valid, element) {
        self.valid = valid;
        self.lastChange = new Date();
        if (self.lastCommit == null && element == null) {
            self.lastCommit = self.lastChange;
        }
    };
    $.timer(1000, function(timer) {
        self.commit();
    });

    this.find("create", "a").each(function() {
        self.bindCreate(this);
    });

    this.find("delete", "a").each(function() {
        self.bindDelete(this);
    });


    $(window).bind("beforeunload",
                   function(ev) {
                       var result = self.commit(0, true);
                       if (!result) {
                           ev.returnValue = '<fmt:message key="invalid" />';
                       }
                       if (result) {
                           return null;
                       } else {
                           return result;
                       }
                   });
    // automaticly make the entries empty on focus if they evidently contain the default value only
    $(this.div).find("input[type='text']").filter(function() {
        return this.value.match(/^<.*>$/); }).one("focus", function() {
            this.value = "";
            self.validator.validateElement(this);
        });
    this.setTabIndices();
    $(this.div).trigger("mmsrRelatedNodesReady", [self]);

    this.logEnabled = false;
}

List.prototype.leftPage = false;


List.prototype.log = function(msg) {
    if (this.logEnabled) {
        var errorTextArea = document.getElementById("logarea");
        if (errorTextArea) {
            errorTextArea.value = "LOG: " + msg + "\n" + errorTextArea.value;
        } else {
            // firebug console
	        if (typeof(console) != "undefined") {
		        console.log(msg);
	        }
        }
    }
};


List.prototype.find = function(clazz, elname, parent) {

    this.log("---------Finding " + clazz + " " + elname + " in " + parent);
    var result = [];
    var self = this;
    if (elname != null) elname = elname.toUpperCase();

    if (parent == null) parent = this.div;

    var t = parent.firstChild;
    while (t != null) {
        var cn = t.nodeName.toUpperCase();
        if (cn == '#TEXT' || cn == '#COMMENT' || (cn == 'DIV' && $(t).hasClass("list"))) {
            var c = t.nextSibling;
            while (c == null) {
                t = t.parentNode;
                if (t == parent) { c = null; break; }
                c = t.nextSibling;
            }
            t = c;

        } else {
            this.log(" - " + cn + " " + elname + " in " + $(t).hasClass(clazz) + " " + t.href);
            if ( (clazz == null || $(t).hasClass(clazz)) &&
                 (elname == null || cn == elname)) {
                result[result.length] = t;
                var c = t.nextSibling;
                while (c == null) {
                    t = t.parentNode;
                    if (t == parent) { c = null; break; }
                    c = t.nextSibling;
                }
                t = c;

            } else {
                var c = t.firstChild;
                if (c == null) {
                    c = t.nextSibling;
                }
                if (c == null) {
                    c = t.nextSibling;
                    while (c == null) {
                        t = t.parentNode;
                        if (t == parent) { c = null; break; }
                        c = t.nextSibling;
                    }
                }
                t = c;
            }
        }
    }
    return $(result);
}




/**
 * Effort to get the browsers tab-indices on a logical order
 * Not sure that this works nice.
 */
List.prototype.setTabIndices = function() {
    var i = 0;
    $(this.div).find("input").each(function() {
        this.tabIndex = i;
        i++;
    });
    $(this.div).find("a").each(function() {
        this.tabIndex = i;
        i++;
    });
}

List.prototype.bindCreate = function(a) {
    a.list = this;
    $(a).click(function(ev) {
        var url = a.href;
        var params = {};
        if (this.item != undefined) {
            params.item   = this.item;
        }
        if (this.source != undefined) {
            params.source = this.source;
        }
        params.createpos = this.parentNode.list.createpos;

        $.ajax({async: false, url: url, type: "GET", dataType: "xml", data: params,
                complete: function(res, status){
                    try {
                        if ( status == "success" || status == "notmodified" ) {
                            var r = null;
                            try {
                                r = document.importNode(res.responseXML.documentElement, true);
                            } catch (e) {
                                // IE 6 sucks.
                                r = $(res.responseText)[0];
                            }
                            // remove default value on focus
                            $(r).find("input").one("focus", function() {
                                this.value = "";
                                a.list.validator.validateElement(this);
                            });
                            if (params.createpos == 'top') {
                                a.list.find(null, "ol").prepend(r);
                            } else {
                                a.list.find(null, "ol").append(r);
                            }
                            a.list.validator.addValidation(r);
                            a.list.find("delete", "a", r).each(function() {
                                a.list.bindDelete(this);
                            });
                            $(r).find("* div.list").each(function() {
                                var div = this;
                                if (div.list == null) {
                                    div.list = new List(div);
                                }
                            });
                            a.list.executeCallBack("create", r); // I think this may be deprecated. Custom events are nicer
                            $(a.list.div).trigger("mmsrCreated", [r]);

                        } else {
                            alert(status + " with " + url);

                        }
                    } catch (ex) {
                        alert(ex);
                    }

                }
               });
        return false;
    });
}

List.prototype.bindDelete = function(a) {
    a.list = this;
    $(a).click(function(ev) {
        var really = true;
        if ($(a).hasClass("confirm")) {
            $($(a).parents("li")[0]).addClass("highlight");
            really = confirm('<fmt:message key="really" />');
            $($(a).parents("li")[0]).removeClass("highlight");
        }
        if (really) {
            var url = a.href;
            var params = {};

            $.ajax({async: true, url: url, type: "GET", dataType: "xml", data: params,
                    complete: function(res, status){
                        if ( status == "success" || status == "notmodified" ) {
                            var li = $(a).parents("li")[0];
                            a.list.validator.removeValidation(li);
                            var ol = $(a).parents("ol")[0];
                            if (ol != null) { // seems to happen in IE sometimes?
                                ol.removeChild(li);
                            }
                            a.list.executeCallBack("delete", li);
                        } else {
                            alert(status + " " + res);
                        }
                    }
                   });
        }
        return false;
    });

}

List.prototype.executeCallBack = function(type, element) {
    if (this.callBack != null) {
        this.callBack(self, type, element);
    } else {
    }

}

List.prototype.needsCommit = function() {
    return this.lastChange != null &&
        (this.lastCommit == null || this.lastCommit.getTime() < this.lastChange.getTime());
}

List.prototype.status = function(message, fadeout) {
    this.find("status", "span").each(function() {
        if (this.originalTextContent == null) this.originalTextContent = this.textContent;
        $(this).fadeTo("fast", 1);
        $(this).empty();
        $(this).append(message);
        if (fadeout) {
            var p = this;
            $(this).fadeTo(4000, 0.1, function() { $(p).empty(); $(p).append(p.originalTextContent); } );
        }
    });
}

/**
 * @param stale Number of millisecond the content may be aut of date. Defaults to 5 s. But on unload it is set to 0.
 */
List.prototype.commit = function(stale, leavePage) {
    var result;
    if(this.needsCommit()) {
        if (this.valid) {
            var now = new Date();
            if (stale == null) stale = this.defaultStale; //
            if (now.getTime() - this.lastChange.getTime() > stale) {
                this.lastCommit = now;
                var params = {};
                params.item   = this.item;
                params.seq    = this.seq;
                params.source = this.source;
                params.icondir = this.icondir;
                params.createpos = this.createpos;
                params.leavePage = leavePage ? true : false;

                $(this.find("listinfo", "div")[0]).find("input[type='hidden']").each(function() {
                    params[this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;
                });
                this.find(null, "input").each(function() {
                    if (this.checked || this.type == 'text' || this.type == 'hidden' || this.type == 'password') {
                        params[this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;
                    }
                });
                this.find(null, "option").each(function() {
                    if (this.selected) {
                        params[this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;
                    }
                });
                this.find(null, "textarea").each(function() {
                    params[this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;
                });


                var self = this;
                this.status("<img src='${mm:link('/mmbase/style/ajax-loader.gif')}' />");
                $.ajax({ type: "POST",
                         async: leavePage == null ? true : !leavePage,
                         url: "${mm:link('/mmbase/searchrelate/list/save.jspx')}",
                         data: params,
                         complete: function(req, textStatus) {
                             self.status('<fmt:message key="saved" />', true);
                         }
                       });

                result = true;
            } else {
                // not stale enough
                result = false;
            }
        } else {
            result = false;
        }
    } else {
        result = true;
    }
    if (leavePage && ! List.prototype.leftPage) {
        List.prototype.leftPage = true;
        $.ajax({ type: "GET", async: false, url: "${mm:link('/mmbase/searchrelate/list/leavePage.jspx')}" });
    }
    return result;
}




</mm:content>
</fmt:bundle>
