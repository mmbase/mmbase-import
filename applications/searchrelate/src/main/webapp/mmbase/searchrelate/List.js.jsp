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


    var listinfos       = this.find("listinfo");
    $(listinfos).find("input[type=hidden]").each(function() {
            self[this.name] = $(this).val();
        });
    this.max = parseInt(this.max);
    this.cursize = parseInt(this.cursize);

    this.sortable   = listinfos.find("input[name = 'sortable']")[0].value == 'true';
    this.autosubmit = listinfos.find("input[name = 'autosubmit']")[0].value == 'true';

    if (this.sortable) {
        if (! this.autosubmit) {
            if (this.order != "") {
                var o = this.order.split(",");
                for (node in o) {
                    var nodeli = self.getLiForNode(o[node]);
                    var ol = $(this.div).find("ol")[0];
                    if (nodeli.length > 0) {
                        $(nodeli[0]).addClass("pos_" + node);
                        ol.appendChild(nodeli[0]);
                    }
                }
            }
        }
        $(this.div).find("ol").sortable({
                update: function(event, ui) { self.saveOrder(event, ui); }
            });
    }

    this.search   = listinfos.find("input[name = 'search']")[0].value == 'true';

    if (this.search) {
        //console.log("Searchable" + $(this.div).find("div.mm_related"));
        $(this.div).find("div.mm_related").bind("mmsrRelate", function (e, relate, relater) {
                self.relate(e, relate, relater);
                relater.repository.searcher.dec();
                $(relate).addClass("removed");
                relater.repository.searcher.resetTrClasses();
            });
    }

    this.lastCommit = null;

    this.defaultStale = 1000;

    this.valid = true;
    this.validator = typeof(MMBaseValidator) != "undefined" ?  new MMBaseValidator() : null;
    if (this.validator != null) {
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
    $(this.div).find("input[type='text']").filter(function() {
        return this.value.match(/^<.*>$/); }).one("focus", function() {
            this.value = "";
            if (self.validator != null) {
                self.validator.validateElement(this);
            }
        });
    this.setTabIndices();
    $(this.div).trigger("mmsrRelatedNodesReady", [self]);

    this.logEnabled = false;

    if ($(this.div).hasClass("POST")) {
        $(this.div).trigger("mmsrRelatedNodesPost", [self]);
        this.afterPost();
    }
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
    var r = null;
    try {
        r = document.importNode(res.responseXML.documentElement, true);
    } catch (e) {
        // IE 6 sucks.
        r = $(res.responseText)[0];
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
    if (this.createposition == 'top') {
        list.find(null, "ol").prepend(r);
    } else {
        list.find(null, "ol").append(r);
    }
    if (list.validator != null) {
        list.validator.addValidation(r);
    }
    list.find("delete", "a", r).each(function() {
            list.bindDelete(this);
        });
    $(r).find("* div.list").each(function() {
            var div = this;
            if (div.list == null) {
                div.list = new List(div);
            }
        });

    this.incSize();
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

List.prototype.checkForSize = function() {
    $(this.find("listinfo")).find("input[name=cursize]").val(this.cursize);
    var createVisible = this.cursize < this.max;
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


List.prototype.getListParameters = function() {
    var params = {};
    params.rid          = this.rid;
    return params;
}



List.prototype.upload = function(fileid) {
    var self = this;
    /*
    $.ajaxFileUpload ({
            url: "${mm:link('/mmbase/searchrelate/upload.jspx')}" + "?id=" + self.id + "&n=" + $("#" + fileid).attr("name"),
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
                }
            },
            error: function (data, status, e) {
                alert(e);
            }
        }
        )
    */
    return false;

}

/**
 * @param stale Number of millisecond the content may be aut of date. Defaults to 5 s. But on unload it is set to 0.
 */
List.prototype.commit = function(stale, leavePage) {
    var result;
    var self = this;
    if(this.needsCommit()) {
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
                    if (this.type == 'file') {
                        if ($(this).val().length > 0) {
                            self.upload(this.id);
                        }
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
        $.ajax({ type: "GET", async: false, data: this.getListParameters(), url: "${mm:link('/mmbase/searchrelate/list/leavePage.jspx')}" });

    }
    return result;
}


List.prototype.saveOrder = function(event, ui) {
    var order = "";
    var self = this;
    $(event.target).find("li").each(function() {
            if (order != "") {
                order += ",";
            }
            order += self.getNodeForLi(this);
        });
    var params = this.getListParameters();
    params.order = order;
    var self = this;
    this.status("<img src='${mm:link('/mmbase/style/ajax-loader.gif')}' />");
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

List.prototype.afterPost = function() {
    //console.log("posted!" + this.order);
    if (this.sortable) {
        var order = "";
        var self = this;
        $(self.div).find("li").each(function() {
                if (order != "") {
                    order += ",";
                }
                order += self.getNodeForLi(this);
            });
        var params = this.getListParameters();
        params.order = order;
        params.post = "true";
        var self = this;
        this.status("<img src='${mm:link('/mmbase/style/ajax-loader.gif')}' />");
        $.ajax({ type: "POST",
                    async: true,
                    url: "${mm:link('/mmbase/searchrelate/list/order.jspx')}",
                    data: params,
                    complete: function(req, textStatus) {
                    self.status('<fmt:message key="saved" />', true);
                }
            });
    }
}

</mm:content>
</fmt:bundle>
