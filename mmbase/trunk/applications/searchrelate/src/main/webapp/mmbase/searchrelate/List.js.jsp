// -*- mode: javascript; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:bundle basename="org.mmbase.searchrelate.resources.searchrelate">
<mm:content type="text/javascript">

/**
 * This javascript binds to a div.list.
 *
 * This div is supposed to contain an <ol> with <a class="delete" />, and a <a class="create" />
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

    if (this.formtag.length > 0) {
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
                    self.valid = valid;
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
        this.find("mm_related", "div").each(function() {
                this.relater = new MMBaseRelater(this, self.validator);
            });

        this.find("mm_related", "div").bind("mmsrRelate", function (e, relate, relater) {
                self.relate(e, relate, relater);
                relater.repository.searcher.dec();
                $(relate).addClass("removed");
                relater.repository.searcher.resetTrClasses();
            });
    }


    $.timer(1000, function(timer) {
            self.commit();
        });


    this.find("create", "a").each(function() {
            self.bindCreate(this);
    });

    this.find("delete", "a").each(function() {
            self.bindDelete(this);
    });

    this.checkForSize();

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
    this.find(null, "input").filter(function() {
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

    if ($(this.div).hasClass("POST")) {
        $(this.div).trigger("mmsrRelatedNodesPost", [self]);
        this.afterPost();
    }
}

List.prototype.leftPage = false;

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



/**
 * This methods does not find anything in _nested_ lists.
 */
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
    if (this.createposition == 'top') {
        ol.prepend(r);
        r = ol.find("li:first")[0];
    } else {
        ol.append(r);
        r = ol.find("li:last")[0];
    }
    if (cleanOnFocus == null || cleanOnFocus) {
        // remove default value on focus
        $(r).find("input").one("focus", function() {
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
 * @param stale Number of millisecond the content may be aut of date. Defaults to 5 s. But on unload it is set to 0.
 */
List.prototype.commit = function(stale, leavePage) {
    var result;
    var self = this;
    if(this.needsCommit()) {
        this.find(null, "input").each(function() {
                if (this.type == 'file') {
                    if ($(this).val().length > 0) {
                        //console.log("Uploading " + this.id);
                        self.upload(this.id);
                    }
                }
            });

        if (this.valid) {
            var now = new Date();
            if (stale == null) stale = this.defaultStale; //
            if (now.getTime() - this.lastChange.getTime() > stale) {
                this.lastCommit = now;
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
                    params[this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;
                });




                var self = this;
                this.loader();
                $(self.div).trigger("mmsrStartSave", [self]);
                $.ajax({ type: "POST",
                         async: leavePage == null ? true : !leavePage,
                         url: "${mm:link('/mmbase/searchrelate/list/save.jspx')}",
                         data: params,
                            complete: function(req, textStatus) {
                            self.status('<fmt:message key="saved" />', self.uploadingSize == 0);
                            $(self.div).trigger("mmsrFinishedSave", [self]);
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
        $(self.div).trigger("mmsrLeavePage", [self]);
        $.ajax({ type: "GET", async: false, data: this.getListParameters(), url: "${mm:link('/mmbase/searchrelate/list/leavePage.jspx')}" });

    }
    return result;
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
    $(ol).find("li").each(function() {
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
    return $(li).attr("id").substring(("node_" + this.rid + "_").length);
}

List.prototype.getOriginalPosition  = function(li) {
    var classes = $(li).attr("class").split(' ');
    for (var i in classes) {
        var cl = classes[i];
        if (cl.indexOf("origPos-") == 0) {
            return cl.substring("origPos-".length);
        }
    }
    alert(li);
}

List.prototype.afterPost = function() {
    //console.log("posted!" + this.order);
    if (this.sortable) {
        var order = "";
        var originalOrder = "";
        var self = this;
        self.find(null, "li").each(function() {
                if (order != "") {
                    order += ",";
                    originalOrder += ",";
                }
                order += self.getNodeForLi(this);
                originalOrder += self.getOriginalPosition(this);
            });
        var params = this.getListParameters();
        params.order = order;
        params.originalOrder = originalOrder;
        var self = this;
        this.loader();
        $.ajax({ type: "POST",
                    async: false,
                    url: "${mm:link('/mmbase/searchrelate/list/submitOrder.jspx')}",
                    data: params,
                    complete: function(req, textStatus) {
                    self.status('<fmt:message key="saved" />', true);
                }
            });
    }
}

</mm:content>
</fmt:bundle>
