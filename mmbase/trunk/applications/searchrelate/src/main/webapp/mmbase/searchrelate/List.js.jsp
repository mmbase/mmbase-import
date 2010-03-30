/**
<%@page contentType="text/javascript; charset=UTF-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><fmt:bundle basename="org.mmbase.searchrelate.resources.searchrelate">
<mm:content type="text/javascript">
 *
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


$(document).ready(
    function() {
        List.prototype.init(document, false);

        // we allow mm-sr:search to be used for your own implementation of searcher.
        // This arranges the version arranged by mm-sr:relatednodes itself
        // (recognizable by the class).
        $("div.mm_related.relatednodes_search_simple").
            live("mmsrRelate",
                 function (e, tr, relater) {
                     var number = relater.getNumber(tr);
                     relater.repository.searcher.dec();
                     $(tr).addClass("removed");
                     relater.repository.searcher.resetTrClasses();
                     $(e.target).trigger("mmsrAddToList", [number]);
                 });

        // i.e, we bind to the 'mmsrRelate' event to put the selected
        // tr in the list as a new item.
        $("div.mm_related").
            live("mmsrAddToList",
                 function (e, number) {
                     var div = $(e.target).closest("div.list")[0];
                     var list = div.list;
                     list.relate(e, number);
                 });

    }
);





function List(d) {
    this.div = d;
    var self = this;
    this.callBack = null; // called on delete and create (deprecated)

    // Collect some configuration which was supplied to mm-sr:relatednodes (and communicated using hidden form entries).
    var listinfos       = this.find("listinfo");
    // Genericly set all of them:
    $(listinfos).find("input[type=hidden]").each(function() {
            self[this.name] = $(this).val();
        });
    // but:
    // fix integers
    this.max        = parseInt(this.max);
    this.cursize    = parseInt(this.cursize);
    // and booleans
    this.sortable   = this.sortable   == 'true';
    this.autosubmit = this.autosubmit == 'true';
    this.search     = this.search     == 'true';


    // Whether every user input is currently valid (whith respect of both the list's length and  MMBaseValidator information).
    this.valid = true;
    // The reason(s) why it would not be valid.
    this.reason = "";

    // If this used in an mm:form, then maintain also a 'valids' member in that.
    // valids is a map rid->valid (this corresponds to the validation status of the MMBaseValidator only).
    // The form itself may contains _more_ List instances.
    if (this.formtag.length > 0 || this.parentformtag.length > 0) {
        this.form = $(this.div).parents("form")[0];
        this.form.valids = {};
    }

    if (this.sortable) {
        if (! this.autosubmit) {
            //console.log("Found order " + this.rid + " '" + this.order + "'");
            if (this.order != "") {
                var o = this.order.split(",");
                for (node in o) {
                    var nodeli = self.getLiForNode(o[node]);
                    if (nodeli != null) {
                        var ol = $(this.div).find("ol")[0];
                        if (nodeli.length > 0) {
                            $(nodeli[0]).addClass("pos-" + node);
                            ol.appendChild(nodeli[0]);
                        } else {
                            //console.log("No li for node " + o[node]);
                        }
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

    // Every list maintains it's own validator.
    this.validator = typeof(MMBaseValidator) != "undefined" ?  new MMBaseValidator(null, this.rid + "/" + new Date().getTime()) : null;



    if (this.validator != null) {

        // Set up the uploader (bound to mmValidate event)
        if (typeof(MMUploader) != "undefined") {
            this.uploader               = new MMUploader();
            this.uploader.uid           = this.rid;
            this.uploader.statusElement = this.find("status", "span")[0];
            this.uploader.transaction   = this.formtag;
            this.uploader.validator     = this.validator;
        } else {
            this.uploader = null;
        }


        this.validator.lang = "${requestScope['javax.servlet.jsp.jstl.fmt.locale.request']}";
        this.validator.prefetchNodeManager(this.type);
        this.validator.addValidationForElements(this.find("mm_validate"));

        // Bind the event handler on document, so we don't have to bind on creation of new items and so on.
        $(document).bind("mmValidate", function(ev, validator, valid) {
                var element = ev.target;
                if (self.isMyElement(element)) {

                    if (element.lastChange != null && element.lastChange.getTime() > self.lastChange.getTime()) {
                        self.lastChange = element.lastChange;
                    }
                    self.setValidInForm();
                    self.triggerValidateHook();
                    self.commit();
                    if (self.uploader != null) {
                        if (element.type == 'file' && valid) {
                            // start uploading the new file
                            self.uploader.upload(element.id);
                        }
                    }
                }
            }
            );
        this.validator.validatePage(false);
    }



    // Whether at this moment a save is performed.
    this.saving = false;

    // Regulary automaticly save changes (note that saving is not the same as committing if we use mm:form).
    if (this.validator != null) {
        $.timer(1000, function(timer) {
                if (List.prototype.leftPage) {
                    timer.stop();
                } else {
                    self.commit();
                }
            });
    }

    // Set up the create button
    this.find("create", "a").each(function() {
            self.bindCreate(this);
    });

    //console.log("Init " + this.rid);

    // And the delete and unlink buttons.
    this.find("delete", "a").each(function() {
            //console.log("Found");
            self.bindDelete(this);
    });

    // It may be the case that the current list is already too short or too long.
    this.checkForSize();

    // Whether the submit buttons was used.
    this.submitted = false;
    $(this.form).submit(function() {
            self.submitted = true;
        });

    // Before the page is left, we need to save. Arrange that here.
    $(window).bind("beforeunload",
                   function(ev) {
                       List.prototype.leftPage = true;
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

    // experimental:
    this.setTabIndices();

    this.logEnabled = false;


    // Whether the user already left the page (this is during the short time between that, and handling the consequences
    // for that)
    this.leftPage = false;

    // Store this instance in the static map for that.
    List.prototype.instances[this.rid] = this;


    if ($(this.div).hasClass("POST")) {
        $(this.div).trigger("mmsrRelatedNodesPost", [self]);
        //console.log("POSTED");
    } else {
        //console.log("not posted");
    }

    // Notify that we are ready with initialization.
    $(this.div).trigger("mmsrRelatedNodesReady", [self]);

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


};

/**
 * All List instances are collected staticly
 */
List.prototype.instances = {};


List.prototype.isMyElement = function(element) {
    var self = this;
    // only do something if the event is on _our_ mm_validate's.
    return $(element).closest("div.list").filter(function() {
            return this.id == self.div.id;}).length > 0;

};
/**
 * Recalculates this.valid and calls mmsrValidateHook on the form (if there is a form)
 */
List.prototype.triggerValidateHook = function() {
    var self = this;
    var totalReason = "";
    var totalValid = true;
    self.reason = "";
    self.valid = true;
    if (self.form != null) {
        for (var rid in self.form.valids) {
            if (! self.form.valids[rid] ) {
                totalReason += rid;
                totalValid = false;
                if (self.rid == rid) {
                    self.valid = false;
                }
            }
        }
    }
    if (this.cursize < this.min) {
        self.reason += " list too short";
        self.valid = false;
        totalValid = false;
    }
    if (this.cursize > this.max) {
        self.reason += " list too long";
        self.valid = false;
        totalValid = false;
    }
    totalReason += self.reason;
    if (self.valid) {
        $(self.div).removeClass("invalid");
    } else {
        $(self.div).addClass("invalid");
    }
    if (self.form != null) {
        $(self.form).trigger("mmsrValidateHook", [self, totalValid, totalReason, self.form]);
    } else {
    }
    //    $(self.div).trigger("mmsrValidateHookDiv", [self, totalValid, totalReason, self.form]);
};

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



// I'd say it should be possbile with jquery (See next method for current implementation)
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
};

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
};




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
};


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
};

/**
 * Adds an item to the list. This item can be found using a searcher, or created using the create button.
 */
List.prototype.addItem = function(res, cleanOnFocus) {
    var list = this;
    var r = $(res.responseText);
    // This seems nicer, but it would give problems if the content types don't match
    // And anyway, it of course never works in IE.
    //r = document.importNode(res.responseXML.documentElement, true);

    var ol = list.find(null, "ol");
    if (this.addposition == 'top') {
        ol.prepend(r);
        r = ol.find(">li:first")[0];
    } else {
        ol.append(r);
        r = ol.find(">li:last")[0];
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
        // This new item should also be validated
        list.validator.addValidation(r);
        $(r).find("input.mm_validate").each(function() {
                list.validator.validateElement(this);
            });
    }

    // The new item can also be deleted again.
    list.find("delete", "a", r).each(function() {
            list.bindDelete(this);
        });

    // Arrange sub-lists in this new item.
    $(r).find("div.list").each(
        function() {
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
    $(list.div).trigger("mmsrCreated", [r]); // like this.
};

List.prototype.incSize = function() {
    this.cursize++;
    this.checkForSize();
};

List.prototype.decSize = function() {
    this.cursize--;
    this.checkForSize();
};


/**
 * i18n implementation for dynamic messages. That is, sometimes a message in a resource bundle has parameters which we
 * would like to provide using variables from javascript. We do an ajax call for that here, and return the i18ned text.
 */
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
};


List.prototype.sizeValid = function() {
    return this.cursize <= this.max && this.cursize >= this.min;
};

List.prototype.setValidInForm = function() {
    if (this.form != null) {
        var validationValid = this.validator.invalidElements == 0;
        this.form.valids[this.rid] = validationValid;
    }
};

/**
 * Arrange some stuff after the change of list changed.
 * - show/hide buttons (e.g. the create button is hidden if the list reached the maximum size)
 * - Update error messages about that ('list too long')
 * - mark
 */
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

};


/**
 * What must happend if a user clicks a delete button
 * - optionally confirm that
 * - Use the associated href to do an ajax call (unlink.jspx or delete.jspx)
 * - remove validation hooks from the removed item
 */
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
                                a.list.decSize();
                                var ol = $(a).parents("ol")[0];
                                if (ol != null) { // seems to happen in IE sometimes?
                                    ol.removeChild(li);
                                }

                                a.list.executeCallBack("delete", li);
                                a.list.setValidInForm();
                                a.list.triggerValidateHook();
                            } else {
                                alert(status + " " + res);
                            }
                        }
                    });
            }
            return false;
        });

};

List.prototype.executeCallBack = function(type, element) {
    if (this.callBack != null) {
        this.callBack(self, type, element);
    } else {
    }

};


List.prototype.needsCommit = function() {
    //console.log("lch " + this.lastChange + " lc: " + this.lastCommit);
    return this.lastCommit.getTime() < this.lastChange.getTime();
};

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
};

List.prototype.loader = function() {
    this.status("<img class='loader icon' src='${mm:link('/mmbase/style/ajax-loader.gif')}' />");
};


List.prototype.getListParameters = function() {
    var params = {};
    params.rid          = this.rid;
    return params;
};


/**
 * @param stale Number of millisecond the content may be out of date. Defaults to 5 s. But on unload it is set to 0.
 */
List.prototype.commit = function(stale, leavePage) {
    var result;
    var self = this;
    if(this.needsCommit() && (! List.prototype.leftPage || leavePage)) {
        // if triggered unload prototype.leftPage is true, so normal commits are cancelled. But the last 'leavePage' (which will happen asynchronously)
        // does proceed. This is because we want this last commit to delay actually leaving the page.

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

                this.loader();
                $(self.div).trigger("mmsrStartSave", [self]);
                result = null;
                self.saving = true;
                console.log("ASYNC " + (leavePage == null ? true : !leavePage));
                $.ajax({ type: "POST",
                         async: leavePage == null ? true : !leavePage,
                         url: "${mm:link('/mmbase/searchrelate/list/save.jspx')}",
                         data: params,
                         complete: function(res, textStatus) {
                             self.status('<fmt:message key="saved" />', self.uploadingSize == 0);
                             $(self.div).trigger("mmsrFinishedSave", [self]);
                             if (textStatus != "success") {
                                 result = textStatus;
                             }
                             var r = $(res.responseText);
                             r.find(".mm_check_error").each(function() {
                                 var errorId = $(this).attr("id");
                                 var errorDiv = $("#" + errorId);
                                 if (errorDiv.length > 0) {
                                     errorDiv.empty();
                                     errorDiv.append($(this).find("*"));
                                 }
                                 if (self.validator != null) {
                                     var elementId = "mm_" + errorId.substring(9);
                                     var element = $("#" + elementId)[0];
                                     self.validator.updateValidity(element, false);
                                 }
                             });
                             self.saving = false;
                         }
                       });
                this.lastCommit = now;
            } else {
                // not stale enough
                result = "not stale";
            }
        } else {
            result = "not valid (" + self.reason + ")";
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
};

/**
 * All registered 'rid's as a comma seperated list, probably for use as an url parameter.
 * 'rid' is the 'requestid' followed with a sequence number for every list.
 */
List.prototype.getRids = function() {
    var rids = "";
    for (r in List.prototype.instances) {
        if (rids.length > 0) {
            rids += ",";
        }
        rids += r;
    }
    return rids;
};


/**
 * All registered 'requestids's as a comma seperated list, probably for use as an url parameter.  This may well be a
 * list of one entry, but seperate requests (e.g. with the 'create' button), may add to this.
 * 'requestid's  are used as a base for 'rid's.
 */
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
};

List.prototype.allInstancesLeftPage = function() {
    for (r in List.prototype.instances) {
        if (!List.prototype.instances[r].leftPage) {
            return false;
        }
    }
    return true;
};

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
};




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
};



 List.prototype.saveOrder = function(order) {
    var self = this;
    var params   = this.getListParameters();
    params.order = order;
    var self = this;
    this.loader();
    //console.log("Saving order for " + self.rid);
    $.ajax({ type: "POST",
                async: true,
                url: "${mm:link('/mmbase/searchrelate/list/order.jspx')}",
                data: params,
                complete: function(req, textStatus) {
                self.status('<fmt:message key="saved" />', true);
            }
        });

    //console.log(order);
};

List.prototype.relate = function(event, number) {
    var list = this;
    var params = this.getListParameters();
    var url = "${mm:link('/mmbase/searchrelate/list/relate.jspx')}";
    params.destination = number;
    params.order = this.getOrder(event.target);
    $.ajax({async: false,
            url: url, type: "GET", dataType: "xml", data: params,
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
};


/**
 * Given a nodenumber, returns the used 'li' (in this list) to represent it
 */
List.prototype.getLiForNode = function(nodenumber) {
    try {
        return $("#node_" + this.rid + "_" + nodenumber);
    } catch (ex) {
        this.log(ex);
    }
};


/**
 * Given a li, returns the node number which is represented in it.
 */
List.prototype.getNodeForLi  = function(li) {
    var id = $(li).attr("id");
    if (id != null) {
	return id.substring(("node_" + this.rid + "_").length);
    } else {
	return null;
    }

};

/**
 * The 'original' position of an item in a list - so the position before the user made any changes - is stored in a css
 * class origPos-<positon>.
 * This method reads, parses and returns that.
 */
List.prototype.getOriginalPosition  = function(li) {
    var classes = $(li).attr("class").split(' ');
    for (var i in classes) {
        var cl = classes[i];
        if (cl.indexOf("origPos-") == 0) {
            return parseInt(cl.substring("origPos-".length));
        }
    }
    // No original position found? That's an error.
    throw("No original position found for " + $(li).text());
};


/**
 * The method is meant to be used in the 'setup' configuration handler ot tinyMCE.
 * It keeps track of the 'active' editor. Which can be null.
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

    };
    // called on entrance of an editor.
    // removes the possibly previous active editor, and set activeEditor
    var activate = function(ed) {
        if (activeEditor != null && activeEditor != ed) {
            remove(activeEditor);
        } else {

            activeEditor = ed;
        }
    };

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
};

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
 };
/*
 </mm:content>
</fmt:bundle>
*/