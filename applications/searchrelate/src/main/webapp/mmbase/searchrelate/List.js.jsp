// -*- mode: javascript; -*-
<%@page contentType="text/javascript; charset=UTF-8" %><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:bundle basename="org.mmbase.searchrelate.resources.searchrelate">
<mm:content type="text/javascript" expires="0">

/**
 * This javascript binds to a div.list. It happens on document.ready on every div.list in the document. You can also call {@link #init} manually, e.g. after an AJAX load.
 *
 * This div is supposed to contain an <ol> with <a class="delete" />, and a <a class="create" />
 *
 * Items in the list can be added and deleted. They can also be edited (with validation).
 * The user does not need to push a commit button. All data is implicitely committed (after a few second of inactivity, or before unload).
 *
 * Custom events (called on the associated div)
 * -  mmsrRelatedNodesReady
 * -  mmsrRelatedNodesPost
 * -  mmsrCreated
 * -  mmsrValidateHook
 * -  mmsrStartSave
 * -  mmsrFinishedSave
 * -  mmsrLeavePage
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */


$(document).ready(function() {
        List.prototype.init(document, false);
});




function List(d) {
    this.div = d;
    var self = this;

    this.callBack = null; // called on delete and create


    var listinfos       = this.find("listinfo");

    $(listinfos).find("input[type=hidden]").each(function() {
            self[this.name] = $(this).val();
        });
    // fix integers
    this.max        = parseInt(this.max);
    this.cursize    = parseInt(this.cursize);
    // and booleans
    this.sortable   = this.sortable   == 'true';
    this.autosubmit = this.autosubmit == 'true';
    this.search     = this.search     == 'true';

    if (this.formtag.length > 0 || this.parentformtag.length > 0) {
        this.form = $(this.div).parents("form")[0];
        this.form.valids = {};
    }

    if (this.sortable) {
        if (! this.autosubmit) {
            if (this.order != "") {
                var o = this.order.split(",");
                for (node in o) {
                    var nodeli = self.getLiForNode(o[node]);
                    var ol = $(this.div).find("ol")[0];
                    if (nodeli.length > 0) {
                        $(nodeli[0]).addClass("pos-" + node);
                        ol.appendChild(nodeli[0]);
                    }
                }
            }
        }
        var sortables = this.find(null, "ol");
        sortables.sortable({
                update: function(event, ui) {
                    self.saveOrder(self.getOrder(event.target));
                }
            });
    }



    this.lastCommit = new Date(); // now
    this.lastChange = new Date(0); // long time ago

    this.defaultStale = 1000;

    this.valid = true;
    this.validator = typeof(MMBaseValidator) != "undefined" ?  new MMBaseValidator() : null;
    if (this.validator != null) {
        this.validator.lang = "${requestScope['javax.servlet.jsp.jstl.fmt.locale.request']}";
        this.validator.prefetchNodeManager(this.type);
        this.validator.setup(this.div);
        var validator = this.validator;

        // Bind the event handler on document, so we don't have to bind on creation of new items and so on.
        $(document).bind("mmValidate", function(ev, validator, valid) {
            var element = ev.target;
            // only do something if the event is on _our_ mm_validate's.
            if ($(element).closest("div.list").filter(function() {
                        return this.id == self.div.id;}).length > 0) {
                self.valid = validator.invalidElements == 0;
                if (element.lastChange != null && element.lastChange.getTime() > self.lastChange.getTime()) {
                    self.lastChange = element.lastChange;
                }
                if (self.form != null) {
                    self.form.valids[self.rid] = valid;
                    self.triggerValidateHook();
                }
            }
        }
			);
        this.validator.validatePage(false);
    }

    if (this.search) {

        this.find("mm_related", "div").bind("mmsrRelate", function (e, relate, relater) {
                self.relate(e, relate, relater);
                relater.repository.searcher.dec();
                $(relate).addClass("removed");
                relater.repository.searcher.resetTrClasses();
            });
    }


    this.saving = false;

    $.timer(1000, function(timer) {
            if (List.prototype.leftPage) {
                timer.stop();
            } else {
                self.commit();
            }

        });


    this.find("create", "a").each(function() {
            self.bindCreate(this);
    });

    this.find("delete", "a").each(function() {
            self.bindDelete(this);
    });

    this.checkForSize();

    this.submitted = false;
    $(this.form).submit(function() {
            self.submitted = true;
        });

    $(window).bind("beforeunload",
                   function(ev) {
                       var result = self.commit(0, true);
                       if (result != null) {
                           ev.returnValue = confirm(result); //'<fmt:message key="invalid" />';
                           return ev.returnValue;
                       }
                       return result;

                   });
    // automaticly make the entries empty on focus if they evidently contain the default value only
    this.find("mm_validate", "input").filter(function() {
        return this.type == 'text' && this.value.match(/^<.*>$/); }).one("focus", function() {
            this.value = "";
            if (self.validator != null) {
                self.validator.validateElement(this);
            }
        });
    this.setTabIndices();
    $(this.div).trigger("mmsrRelatedNodesReady", [self]);

    this.logEnabled = false;

    this.uploading = {};
    this.uploadingSize = 0;

    this.leftPage = false;

    List.prototype.instances[this.rid] = this;

    if ($(this.div).hasClass("POST")) {
        $(this.div).trigger("mmsrRelatedNodesPost", [self]);
        this.afterPost();
    }

}

/**
 * Initializes every div.list in the given element to be a List
 */
List.prototype.init = function(el, initSearchers) {
    if (typeof initSearchers == "undefined" || initSearchers) {
        if (typeof MMBaseRelater == "function") {
            $(el).find("div.mm_related").each(function() {
                    if (this.relater == null) {
                        this.relater = new MMBaseRelater(this);
                    }
                });
        }
    }

    var l = List; // hoping to make IE a bit faster
    $(el).find("div.list").each(function() {
        if (this.list == null) {
            this.list = new l(this);
        }
    });
    $(el).find("div.list:last").each(function() {
        l.seq = $(this).find("input[name = 'seq']")[0].value;
    });
}

List.prototype.wasResetSequence = false;
List.prototype.instances = {};

List.prototype.triggerValidateHook = function() {
    var reason = "";
    var self = this;
    var valid = true;
    if (self.form != null) {
        for (var rid in self.form.valids) {
            if (! self.form.valids[rid] ) {
                valid = false;
                reason += rid;
            }
        }
    }
    if (this.cursize < this.min) {
        reason += " list too short";
        valid = false;
    }
    if (this.cursize > this.max) {
        reason += " list too long";
        valid = false;
    }
    if (valid) {
        $(this.div).removeClass("invalid");
    } else {
        $(this.div).addClass("invalid");
    }
    if (this.form != null) {
        $(this.form).trigger("mmsrValidateHook", [self, valid, reason, self.form]);
    } else {
        // console.log("No form");
        //console.log(this);
    }
}

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



// I'd say it should be possbile with jquery.
List.prototype._find = function(clazz, elname, parent) {
    if (elname == null) elname = "";
    if (parent == null) parent = this.div;
    console.log($(parent).find("*"));
    console.log($(parent).find("*").find("not(.list"));
    if (clazz == null) {
        return $(parent).find("not(.list").find(elname);
    } else {
        return $(parent).find("not(.list").find(elname + "." + clazz);
    }
}

/**
 * This methods does not find anything in _nested_ lists.
 */
List.prototype.find = function(clazz, elname, parent) {

    //    this.log("---------Finding " + clazz + " " + elname + " in " + parent);
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
            //this.log(" - " + cn + " " + elname + " in " + $(t).hasClass(clazz) + " " + t.href);
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
    $(this.div).find("input[type != hidden]").each(function() {
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


        $.ajax({async: false, url: url, type: "GET", dataType: "xml", data: params,
                complete: function(res, status){
                    try {
                        if ( status == "success" || status == "notmodified" ) {
                            a.list.addItem(res);
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


List.prototype.addItem = function(res, cleanOnFocus) {
    var list = this;
    //console.log(res.responseText);
    var r = $(res.responseText);
    // This seems nicer, but it would give problems if the content types don't match
    // And anyway, it of course never works in IE.
    //r = document.importNode(res.responseXML.documentElement, true);

    var ol = list.find(null, "ol");
    if (this.addposition == 'top') {
        ol.prepend(r);
        r = ol.find("li:first")[0];
    } else {
        ol.append(r);
        r = ol.find("li:last")[0];
    }
    if (cleanOnFocus == null || cleanOnFocus) {
        // remove default value on focus
        $(r).find("input.mm_validate").one("focus", function() {
                this.value = "";
                if (list.validator != null) {
                    list.validator.validateElement(this);
                }
            });
    }

    if (list.validator != null) {
        list.validator.addValidation(r);
    }
    list.find("delete", "a", r).each(function() {
            list.bindDelete(this);
        });
    $(r).find("div.list").each(function() {
            var div = this;
            if (div.list == null) {
                div.list = new List(div);
            }
        });

    this.incSize();
    if (this.sortable) {
        this.saveOrder(this.getOrder());
    }
    list.executeCallBack("create", r); // I think this may be deprecated. Custom events are nicer

    $(list.div).trigger("mmsrCreated", [r]);
}

List.prototype.incSize = function() {
    this.cursize++;
    this.checkForSize();
}

List.prototype.decSize = function() {
    this.cursize--;
    this.checkForSize();
}


List.prototype.getMessage = function(key, p) {

    var result;
    var params = {};
    params.key = key;
    for (var param in p) {
        params[param] = p[param];
    }
    $.ajax({async: false,
                url: "${mm:link('/mmbase/searchrelate/message.jspx')}",

                type: "GET", dataType: "xml",
                data: params,
                complete: function(res, status) {
                    result = res.responseText;
                }
        });
    return $(result);
}

List.prototype.checkForSize = function() {
    $(this.find("listinfo")).find("input[name=cursize]").val(this.cursize);
    var createVisible = this.cursize < this.max;
    var self = this;
    this.find("create", "a").each(function() {
            if (createVisible) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    this.find("mm_related", "div").each(function() {
            if (createVisible) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    this.find("errors", "span").each(function() {
            var span = $(this);
            span.empty();
            if (self.cursize > self.max) {
                span.append(self.getMessage('listtoolong', {i0:self.max, i1:self.cursize}));
            }
            if (self.cursize < self.min) {
                span.append(self.getMessage('listtooshort', {i0:self.min, i1:self.cursize}));
            }
        });
    this.triggerValidateHook();
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
                                if (a.list.validator != null) {
                                    a.list.validator.removeValidation(li);
                                }
                                var ol = $(a).parents("ol")[0];
                                if (ol != null) { // seems to happen in IE sometimes?
                                    ol.removeChild(li);
                                }
                                a.list.decSize();
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
    //console.log("lch " + this.lastChange + " lc: " + this.lastCommit);
    return this.lastCommit.getTime() < this.lastChange.getTime();
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

List.prototype.loader = function() {
    this.status("<img class='loader icon' src='${mm:link('/mmbase/style/ajax-loader.gif')}' />");
}


List.prototype.getListParameters = function() {
    var params = {};
    params.rid          = this.rid;
    return params;
}

List.prototype.uploadProgress = function(fileid) {
    if (this.uploading[fileid]) {
        this.find("status", "span").load("${mm:link('/mmbase/upload/progress.jspx')}");
    }
}

List.prototype.upload = function(fileid) {
    var self = this;
    if (self.uploading[fileid]) {
        // uploading already
        return;
    }
    self.uploading[fileid] = true;
    self.uploadingSize++;
    var fileItem = $("#" + fileid);
    var li = fileItem.parents("li");
    var node = self.getNodeForLi(li);
    var progress = function() {
        self.uploadProgress(fileid);
        if (self.uploading[fileid]) {
            setTimeout(progress, 1000);
        }
    };
    progress();
    $.ajaxFileUpload ({
            url: "${mm:link('/mmbase/searchrelate/list/upload.jspx')}" + "?rid=" + self.rid + "&name=" + fileItem.attr("name") + "&n=" + node,
            secureuri: false,
            fileElementId: fileid,
            dataType: 'xml',
            success: function (data, status) {
                if(typeof(data.error) != 'undefined') {
                    if(data.error != '') {
                        alert(data.error);
                    } else {
                        alert(data.msg);
                    }
                } else {
                    try {
                        var fileItem = $("#" + fileid);
                        fileItem.val(null);
                        fileItem.prev(".mm_gui").remove();
                        var created = $(data).find("div.fieldgui .mm_gui");
                        fileItem.before(created);
                    } catch (e) {
                        alert(e);
                    }

                }
                delete self.uploading[fileid];
                self.uploadingSize--;
                self.status('<fmt:message key="uploaded" />', true);
            },
            error: function (data, status, e) {
                alert(e);
                delete self.uploading[fileid];
                self.uploadingSize--;
            }
        }
        )
    return false;
}

/**
 * @param stale Number of millisecond the content may be out of date. Defaults to 5 s. But on unload it is set to 0.
 */
List.prototype.commit = function(stale, leavePage) {
    var result;
    var self = this;
    if(this.needsCommit() && ! List.prototype.leftPage) {
        this.find(null, "input").each(function() {
                if (this.type == 'file') {
                    if ($(this).val().length > 0 && ! $(this).hasClass("invalid")) {
                        //console.log("Uploading " + this.id);
                        self.upload(this.id);
                    }
                }
            });

        if (this.valid) {
            var now = new Date();
            if (stale == null) stale = this.defaultStale; //
            if (now.getTime() - this.lastChange.getTime() > stale) {
                var params = this.getListParameters();
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
                        params[this.name || this.id || this.parentNode.name || this.parentNode.id ] = $(this).val();
                });




                var self = this;
                this.loader();
                $(self.div).trigger("mmsrStartSave", [self]);
                result = null;
                self.saving = true;
                $.ajax({ type: "POST",
                         async: leavePage == null ? true : !leavePage,
                         url: "${mm:link('/mmbase/searchrelate/list/save.jspx')}",
                         data: params,
                            complete: function(req, textStatus) {
                               self.status('<fmt:message key="saved" />', self.uploadingSize == 0);
                               $(self.div).trigger("mmsrFinishedSave", [self]);
                               if (textStatus != "success") {
                                   result = textStatus;
                               }
                               self.saving = false;
                        }
                       });
                this.lastCommit = now;
            } else {
                // not stale enough
                result = "not stale";
            }
        } else {
            result = "not valid (" + reason + ")";
        }
    } else {
        result = null;
    }
    if (leavePage) {
        if (self.saving) {
            return "Cannot leave because still saving " + result;
        }
        if (result != null) {
            return "Cannot leave because save failed: " + result;
        }
        if (self.submitted) {
            // no need leaving, because just 'submitted'
            return result;
        }

        this.leavePage();

    }
    return result;
}


List.prototype.getRids = function() {
    var rids = "";
    for (r in List.prototype.instances) {
        if (rids.length > 0) {
            rids += ",";
        }
        rids += r;
    }
    return rids;
}
List.prototype.getRequestIds = function() {
    var map = {};
    for (r in List.prototype.instances) {
        map[List.prototype.instances[r].requestid] = true;
    }

    var requestids = "";
    for (r in map) {
        if (requestids.length > 0) {
            requestids += ",";
        }
        requestids += r;
    }
    return requestids;
}

List.prototype.allInstancesLeftPage = function() {
    for (r in List.prototype.instances) {
        if (!List.prototype.instances[r].leftPage) {
            return false;
        }
    }
    return true;
}

List.prototype.leavePage = function() {
    $(self.div).trigger("mmsrLeavePage", [self]);
    this.leftPage = true;

    if (List.prototype.allInstancesLeftPage()) {
        var params = {};
        params.rids = List.prototype.getRids();
        params.requestids = List.prototype.getRequestIds();
        $.ajax({ type: "GET", async: false, data: params, url: "${mm:link('/mmbase/searchrelate/list/leavePage.jspx')}" });
    }
    $(self.div).trigger("mmsrAfterLeavePage", [self]);
}




/**
 * The order of li's as currently visible by the user, returned as a comma seperated list of node numbers
 */
List.prototype.getOrder = function(ol) {
    if (ol == null) {
        ol = this.find(null, "ol")[0];
    }
    var order = "";
    var self = this;
    $(ol).find("> li").each(function() {
            if (order != "") {
                order += ",";
            }
            order += self.getNodeForLi(this);
        });
    return order;
}



 List.prototype.saveOrder = function(order) {
    var self = this;
    var params   = this.getListParameters();
    params.order = order;
    var self = this;
    this.loader();
    $.ajax({ type: "POST",
                async: true,
                url: "${mm:link('/mmbase/searchrelate/list/order.jspx')}",
                data: params,
                complete: function(req, textStatus) {
                self.status('<fmt:message key="saved" />', true);
            }
        });

    //console.log(order);
}

List.prototype.relate = function(event, relate, relater) {
    var list = this;
    var params = this.getListParameters();
    var url = "${mm:link('/mmbase/searchrelate/list/relate.jspx')}";
    params.destination = relater.getNumber(relate);
    params.order = this.getOrder(event.target);
    $.ajax({async: false, url: url, type: "GET", dataType: "xml", data: params,
            complete: function(res, status){
                try {
                    if ( status == "success" || status == "notmodified" ) {
                        list.addItem(res, false);
                    } else {
                        alert(status + " with " + url);
                    }
                } catch (ex) {
                    alert(ex);
                }

            }
        });

}

List.prototype.getLiForNode = function(nodenumber) {
    try {
        return $("#node_" + this.rid + "_" + nodenumber);
    } catch (ex) {
        console.log(ex);
    }
}

List.prototype.getNodeForLi  = function(li) {
    var id = $(li).attr("id");
    if (id != null) {
	return id.substring(("node_" + this.rid + "_").length);
    } else {
	return null;
    }

}

List.prototype.getOriginalPosition  = function(li) {
    var classes = $(li).attr("class").split(' ');
    for (var i in classes) {
        var cl = classes[i];
        if (cl.indexOf("origPos-") == 0) {
            return parseInt(cl.substring("origPos-".length));
        }
    }
    alert(li);
}

List.prototype.afterPost = function() {
    this.log("posted!" + this.order);
    if (this.sortable) {
        // Submit the new order seperately
        var order = "";
        var originalOrder = "";
        var self = this;
        var expectedOriginal = 0;
        var needsSave = false;
        self.find("ui-sortable", "ol").each(function() {
	    $(this).find(">li").each(function() {
		if (order != "") {
                    order += ",";
                    originalOrder += ",";
		}
                var nodeNumber = self.getNodeForLi(this);
		order += nodeNumber;
                if (nodeNumber[0] === "-") {
                    needsSave = true;
                }
                var originalPos =  self.getOriginalPosition(this);
                if (originalPos != expectedOriginal) {
                    needsSave = true;
                }
		originalOrder += originalPos;
                expectedOriginal++;
	    });
        });
        var params = this.getListParameters();
        params.order = order;
        params.originalOrder = originalOrder;
        var self = this;
        if (needsSave) {
            this.loader();
            //console.log("Submitting order for " + this.rid + " " + params.originalOrder + "-> " + params.order );
            $.ajax({ type: "POST",
                        async: false,
                        url: "${mm:link('/mmbase/searchrelate/list/submitOrder.jspx')}",
                        data: params,
                        complete: function(req, textStatus) {
                        self.status('<fmt:message key="saved" />', true);
                }
                });
        } else {
            //console.log("No need to save order for " + order + " " + originalOrder);
        }
    }
}

/**
 * The method is meant to be used in the 'setup' configuration handler ot tinyMCE.
 * It keeps track of the 'active' editor. The can be null.
 * All other editors are shown as plain HTML in a div.
 */
List.prototype.setupTinyMCE = function(ed) {
    // the current active editor
    var activeEditor = null;

    // the method is 'saves' the editor, and replaces it with
    // plain HTML
    var remove = function(ed) {
        if (ed.isDirty()) {
            ed.save();
        }
        var textarea = $("#" + ed.editorId);
        var prev = textarea.prev();
        ed.remove();
        textarea.hide();
        prev.empty().append(textarea.val());
        prev.css("display", "inline-block");

    }
    // called on entrance of an editor.
    // removes the possibly previous active editor, and set activeEditor
    var activate = function(ed) {
        if (activeEditor != null && activeEditor != ed) {
            remove(activeEditor);
        } else {

            activeEditor = ed;
        }
    }

    // tinyMCE does not provide an actual blur event.
    // It is emulated by 'mousedown' on the entire page to detect blur
    // and a bunch of other events are used to detect entrance into the editor.

    $("body").mousedown(function(ev) {
            if ($(ev.target).parents("span.mceEditor").length > 0) {
		// clicked in an editor, ignore that.
            } else {
                if (activeEditor != null) {
                    remove(activeEditor);
                    activeEditor = null;
                }
            }
        });

    ed.onActivate.add(activate);
    ed.onNodeChange.add(activate);
    ed.onMouseDown.add(activate);
    ed.onSaveContent.add(function(ed) {
	//This triggers MMBaseValidator
        $("#" + ed.editorId).trigger("paste");
    });
}

/**
 * Binds the events to a text-area to make it editable via tinyMCE.
*/
List.prototype.tinymce = function(el, tinyMceConfiguration) {
     var self = $(el);
     self.originalDisplay = self.css("display");
     var val = $("<div class='mm_tinymce' />");
     val.append(self.val());
     val.height(self.height());


     self.before(val).hide();

     val.click(function(ev) {
             self.css("display", val.css("display"));
             val.hide();
             self.tinymce(tinyMceConfiguration);

         });
 }
 </mm:content>
</fmt:bundle>
