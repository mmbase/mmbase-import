// -*- mode: javascript; -*-
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<mm:content type="text/javascript" language="${param.locale}">
<fmt:bundle basename="org.mmbase.searchrelate.resources.searchrelate">
/**
 * Generic mmbase search & relate tool. Javascript part.
 *
 *
 * Usage: It is sufficient to use the mm:relate tag. This tag wil know whether this javascript is
 * already loaded, and if not, will arrange for that. It is required to load jquery, though.
 *
 * On ready, the necessary javascript will then be connected to .mm_related a.search
 *
 * Custom events
 * - mmsrRelate            (use   $("div.mm_related").bind("mmsrRelate", function (e, tr, relater) ) )
 * - mmsrUnrelate          (use   $("div.mm_related").bind("mmsrUnrelate", function (e, tr, relater) ) )
 * - mmsrPaged             (use   $("div.mm_related").bind("mmsrPaged", function (e, status, relater) ) )
 * - mmsrRelaterReady      (use   $("div.mm_related").bind("mmsrRelaterReady", function (e, relater) ) )
 * - mmsrCommitted         (use   $("div.mm_related").bind("mmsrCommitted", function (e, submitter, status, relater, relatedNumbers, unrelatedNumbers) ) )
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */



/**
 * Logger, a bit like org.mmbase.util.logging.Logger. Logs to firebug console or a dedicated area.
 *
 */
function MMBaseLogger(area) {
    this.logEnabled   = false;
    /*this.traceEnabled = false;*/
    this.logarea      = area;
}

MMBaseLogger.prototype.debug = function (msg) {
    if (this.logEnabled) {
        var errorTextArea = document.getElementById(this.logarea);
        if (errorTextArea) {
            errorTextArea.value = "LOG: " + msg + "\n" + errorTextArea.value;
        } else {
            // firebug console
            console.log(msg);

        }
    }
}

/*
 * ************************************************************************************************************************
 */

/**
 * The 'relater' encapsulated 1 or 2 'searchers', and is responsible for moving elements from one to the other.
 */
function MMBaseRelater(d) {
    this.div           = d;
    this.related       = {};
    this.unrelated     = {};
    this.logger        = new MMBaseLogger();
    this.logger.debug(d);
    this.logger.debug("setting up current");
    this.current       = $(d).find(".mm_relate_current")[0];
    this.canUnrelate   = $(d).hasClass("can_unrelate");
    this.canEditrelations = $(d).hasClass("can_editrelations");
    if (this.current != null) {
        this.addSearcher(this.current, "current");
    } else {
        this.logger.debug("No current rep found");
    }

    if (typeof MMBaseValidator == "function") {
        this.validator = new MMBaseValidator(this.div);
    }
    this.logger.debug("setting up repository");
    this.repository    = $(d).find(".mm_relate_repository")[0];
    this.logger.debug("found " + this.repository + " in ");
    if (this.repository != null) this.addSearcher(this.repository, "repository");
    this.relateCallBack = null;
    for (var i = 0; i < MMBaseRelater.readyFunctions.length; i++) {
        var fun =  MMBaseRelater.readyFunctions[i];
        fun(this);
    }
    this.sessionName = null;
    var self = this;
    $(this.div).trigger("mmsrRelaterReady", [self]);
}

/**
 *  This Searcher.js.jsp is normally loaded implicetly by the first mm-sr:relate. Using the 'ready'
 *  function, you can do something immediately after the MMBaseRealter is ready. E.g. you can add a
 *  'relateCallBack' function.
 *  @todo I think jquery provides something with user defined events.
 */

MMBaseRelater.readyFunctions = [];

MMBaseRelater.ready = function(fun) {
    if (console != undefined) {
        console.log("WARNING using deprecated function. This will be removed soon. Use mmsrRelate event in stead.");
    }
    MMBaseRelater.readyFunctions[MMBaseRelater.readyFunctions.length] = fun;
}



MMBaseRelater.prototype.addSearcher = function(el, type) {
    var relater = this;
    if ($(el).hasClass("searchable")) {
        var searcher =  new MMBaseSearcher(el, this, type, this.logger);
        $(el).find("a.search").each(function() {
            var anchor = this;
            anchor.searcher = searcher;
            $(anchor).click(function(el) {
                var id = anchor.href.substring(anchor.href.indexOf("#") + 1);
                return this.searcher.search(document.getElementById(id), 0);
            });
        });

        $(this.repository).find("form.searchform").each(function() {
            var form = this;
            form.searcher = searcher;
            $(form).submit(function(el) {
                return this.searcher.search(form, 0);
            });
        });
        $(el).find("a.create").each(function() {
            var anchor = this;
            anchor.searcher = searcher;
            $(anchor).click(function(el) {
                return this.searcher.create(anchor);
            });
        });
        if (this.canUnrelate && this.current) {
            $(this.current).find("tr.click").each(function() {
                $(this).click(function(tr) {
                    relater.unrelate(this);
                    return false;
                })});
        }
    }
}


MMBaseRelater.prototype.needsCommit = function() {
    var relatedNumbers   = this.getNumbers(this.related);
    var unrelatedNumbers = this.getNumbers(this.unrelated);
    return relatedNumbers != "" || unrelatedNumbers != "";
}


/**
 * Commits made changes to MMBase. Depends on a jsp /mmbase/searchrelate/relate.jsp to do the actual work.
 * This jsp, in turn, depends on the query in the user's session which defined what precisely must happen.
 */
MMBaseRelater.prototype.commit = function(ev) {
    var relatedNumbers   = this.getNumbers(this.related);
    var unrelatedNumbers = this.getNumbers(this.unrelated);

    if (relatedNumbers != "" || unrelatedNumbers != "") {
        var a = ev.target;
        $(a).addClass("submitting");
        $(a).removeClass("failed");
        $(a).removeClass("succeeded");

        this.logger.debug("Commiting changed relations of " + this.div.id);
        var id = this.div.id;
        var url = "${mm:link('/mmbase/searchrelate/relate.jspx')}";

        this.logger.debug("+ " + relatedNumbers);
        this.logger.debug("- " + unrelatedNumbers);
        var params = {id: id, related: relatedNumbers, unrelated: unrelatedNumbers};
        if (this.transaction != null) {
            params.transaction = this.transaction;
        }
        var self = this;
        $.ajax({async: false, url: url, type: "GET", dataType: "xml", data: params,
                complete: function(res, status){
                    $(a).removeClass("submitting");
                    if (status == "success") {
                        //console.log("" + res);
                        $(a).addClass("succeeded");
                        if (self.canEditrelations && relatedNumbers != "") { // create tr's in which to edit relations
                            var nrs = relatedNumbers.split(",");
                            $(nrs).each(function(i) {
                                var nr = this;
                                var trr = self.getNewRelationTr(nr);
                                $('#' + id + ' div.mm_relate_current tr.click').each(function() {
                                    if (self.getNumber(this) == nr) {
                                        $(trr).insertAfter(this);
                                    }
                                });
                            });
                        }
                        self.related = {};
                        self.unrelated = {};
                        if (self.canEditrelations) self.bindSaverelation(this.div);
                        $(self.div).trigger("mmsrCommitted", [a, status, self, relatedNumbers, unrelatedNumbers]);
                        return true;
                    } else {
                        $(a).addClass("failed");
                        $(self.div).trigger("mmsrCommitted", [a, status, self]);
                        return false;
                    }
                }
               });
    } else {
        this.logger.debug("No changes, no need to commit");
        $(this.div).trigger("mmsrCommitted", [a, "nochanges", this]);
        $(a).addClass("succeeded");
    }
}

/**
 * Gets a the relation tr for a newly created relation in which the relation can be edited.
 *
 */
MMBaseRelater.prototype.getNewRelationTr = function(nodenr) {
    var self = this;
    var url = "${mm:link('/mmbase/searchrelate/relations.tr.jspx')}";
    var queryid = this.repository.searcher.getQueryId();
    queryid = queryid.replace(/repository/i, "current");
    self.logger.debug(url + ", id: " + queryid + ", node: " + nodenr +  ", fields: " + this.repository.searcher.fields);

    var params = {id: queryid, node: nodenr, fields: this.repository.searcher.fields};
    var result;
    $.ajax({async: false, url: url, type: "GET", dataType: "xml", data: params,
        error: function(res, status) {
            var tr = $("<tr />");
            tr.append('<td colspan="3" class="failed">Error: ' + res.status + ' - ' + res.statusText + '</td>');
            result = tr;
        },
        complete: function(res, status) {
            result = res.responseText;
        }
    });

    return result;
}

MMBaseRelater.prototype.getNumbers = function(map) {
    var numbers = "";
    $.each(map, function(key, value) {
        if (value != null) {
            if (numbers.length > 0) numbers += ",";
            numbers += key;
        }
    });
    return numbers;
}

MMBaseRelater.prototype.bindSaverelation = function(div) {
    var self = this;
    self.logger.debug("unbinding and binding relation forms");
    $(div).find('form.relation').unbind('submit');
    $(div).find("form.relation").each(function() {
        $(this).submit(function(ev) {
            self.saverelation(ev);
        });
    });
}

MMBaseRelater.prototype.bindEvents = function(rep, type) {
    var self = this;
    if (type == "repository") {
        $(rep).find("tr.click").each(function() {
            $(this).click(function() {
                self.relate(this);
                return false;
            })});
    }
    if (type == "current") {
        $(rep).find("tr.click").each(function() {
            if ($(this).hasClass("new") || (self != null && self.canUnrelate)) {
                $(this).click(function() {
                    self.unrelate(this);
                    return false;
                })
            }
        });

        if (self.canEditrelations) self.bindSaverelation(rep);
    }
}


MMBaseRelater.prototype.resetTrClasses  = function() {
    if (this.current != null) {
        this.current.searcher.resetTrClasses();
    }
    this.repository.searcher.resetTrClasses();

}

MMBaseRelater.prototype.getNumber = function(tr) {
    return  $(tr).find("td.node.number").text();
}


/**
 * Moves a node from the 'unrelated' repository to the list of related nodes.
 */
MMBaseRelater.prototype.relate = function(tr) {
    var number = this.getNumber(tr);
    this.logger.debug("Found number to relate " + number + "+" + this.getNumbers(this.related));
    // Set up HTML
    if (this.current != null) {
        if (this.current.searcher.maxNumber > 0 && (this.current.searcher.totalSize() + 1) > this.current.searcher.maxNumber) {
            return alert('<fmt:message key="relatemax" />'.replace('{0}', this.current.searcher.maxNumber));
        }
        // Set up data
        if (typeof(this.unrelated[number]) == "undefined") {
            this.related[number] = tr;
        }

        this.unrelated[number] = null;


        var currentList =  $(this.current).find("div.searchresult table tbody");
        this.logger.debug(currentList[0]);
        currentList.append(tr);

        this.current.searcher.inc();
        this.repository.searcher.dec();

        // Classes
        if ($(tr).hasClass("removed")) {
            $(tr).removeClass("removed");
        } else {
            $(tr).addClass("new");
        }
        this.resetTrClasses();

        // Events
        $(tr).unbind();

        var self = this;
        $(tr).click(function() {
            self.unrelate(this);
        });
    }
    if (this.relateCallBack != null) {
        this.relateCallBack(tr);
    }
    $(this.div).trigger("mmsrRelate", [tr, this]);
}

/**
 * Returns the relation tr('s) that belong to a node.
 */
MMBaseRelater.prototype.getRelationTrs = function(number) {
    return $(this.div).find("tr.node_" + number);
}

/**
 * Moves a node from the list of related nodes to the 'unrelated' repository.
 */
MMBaseRelater.prototype.unrelate = function(tr) {
    var number = this.getNumber(tr);
    this.logger.debug("Unrelating " + number);

    // relation tr's
    var relationTrs = this.getRelationTrs(number);
    this.logger.debug("+ relations: " + relationTrs.length);

    // Set up data
    if (typeof(this.related[number]) == "undefined") {
        this.unrelated[number] = tr;
    }
    this.related[number] = null;

    // Set up HTML
    var repository =  $(this.div).find("div.mm_relate_repository div.searchresult table tbody");
    repository.prepend(tr);
    $(relationTrs).remove();    // remove the tr's to edit relations

    this.current.searcher.dec();
    this.repository.searcher.inc();

    // Classes
    if ($(tr).hasClass("new")) {
        $(tr).removeClass("new");
    } else {
        $(tr).addClass("removed");
    }
    this.resetTrClasses();

    // Events
    $(tr).unbind();
    var searcher = this;
    $(tr).click(function() {
        searcher.relate(this)
    });
    $(this.div).trigger("mmsrUnrelate", [tr, this]);
}

/**
 * Saves a modified relations values.
 */
MMBaseRelater.prototype.saverelation = function(ev) {
    ev.preventDefault();
    var form = ev.target;

    var params = {};
    if (this.transaction != null) {
        params.transaction = this.transaction;
    }
    var inputs = $(form).find(":input");
    for (var i = 0; i < inputs.length; i++) {
        var name = $(inputs[i]).attr("name");
        name = name.substr(name.lastIndexOf("_") + 1);
        params[name] = $(inputs[i]).attr("value");
        this.logger.debug("name: " + name + ", value: " + $(inputs[i]).attr("value"));
    }

    var url = "${mm:link('/mmbase/searchrelate/saverelation.jspx')}";
    $.ajax({
        type: "POST",
        url: url,
        data: params,
        complete: function(res, status) {
            if (status == "success") {
                var msg = $(res.responseXML.documentElement).text();
                if ($(form).find('div.succeeded').length == 0) {
                    $(form).prepend('<div class="succeeded" />');
                } else {
                   $(form).find('div.succeeded').show();
                }
                $(form).find('div.succeeded').text(msg);
                $(form).find('div.succeeded').fadeOut(2000, function() {
                    $(this).parents('tr.relation').toggle();
                });
            } else {
                $(form).prepend('<div class="failed" />');
                $(form).find('div.failed').text("Error: " + res.status + " - " + res.statusText);
            }
        }
     });
}

/**
 * Set mmbase context for new objects
 */
MMBaseRelater.prototype.setContext = function(context) {
    if (this.current != null) {
        this.current.searcher.context = context;
    }
    if (this.repository != null) {
        this.repository.searcher.context = context;
    }
}

MMBaseRelater.prototype.setSessionName = function(sessionName) {
    if (this.current != null) {
        this.current.searcher.sessionName = sessionName;
    }
    if (this.repository != null) {
        this.repository.searcher.sessionName = sessionName;
    }
}

MMBaseRelater.prototype.setFields = function(fields) {
    if (this.current != null) {
        this.current.searcher.setFields(fields);
    }
    if (this.repository != null) {
        this.repository.searcher.setFields(fields);
    }
}

MMBaseRelater.prototype.setCustomizedir = function(customizedir) {
    if (this.current != null) {
        this.current.searcher.setCustomizedir(customizedir);
    }
    if (this.repository != null) {
        this.repository.searcher.setCustomizedir(customizedir);
    }
}

MMBaseRelater.prototype.setPageSize = function(pagesize) {
    if (this.current != null) {
        this.current.searcher.setPageSize(pagesize);
    }
    if (this.repository != null) {
        this.repository.searcher.setPageSize(pagesize);
    }
}

MMBaseRelater.prototype.setMaxPages = function(maxpages) {
    if (this.current != null) {
        this.current.searcher.maxpages = maxpages;
    }
    if (this.repository != null) {
        this.repository.searcher.maxpages = maxpages;
    }
}

/*
 * ***********************************************************************************************************************
 */


function MMBaseSearcher(d, r, type, logger) {
    this.div = d;
    this.div.searcher = this;
    this.relater = r;
    this.type    = type;
    this.fields = "";
    this.customizedir = "";
    this.pagesize = 10;
    this.maxpages = 20;
    this.logger  = logger != null ? logger : new MMBaseLogger();
    this.value = "";
    this.offset = 0;
    this.transaction   = null;
    this.canEditrelations = $(r.div).hasClass("can_editrelations");
    var self = this;
    $(d).find("span.transactioname").each(function() {
        this.transaction = this.nodeValue;
    });
    this.searchResults = {};
    this.bindEvents();
    // Arrange that pressing enter in the search-area works:
    $(this.div).find("input.search").keypress(function(ev) {
        if (ev.which == 13) {
            self.search(this.value, 0);
            return false;
        }
    });
    this.validator = this.relater.validator;
    this.searchUrl = $(this.div).find("form.searchform").attr("action");
    this.context   = "";
    this.totalsize = -1;
    this.last = -1;
    if (this.searchUrl == undefined) {
        this.searchUrl = "${mm:link('/mmbase/searchrelate/page.jspx')}";
    }
    this.logger.debug("found url to use: " + this.searchUrl);
    this.maxNumber = -1;

}

MMBaseSearcher.prototype.setCustomizedir = function(customizedir) {
    this.customizedir = customizedir;
}

MMBaseSearcher.prototype.setFields = function(fields) {
    this.fields = fields;
}

MMBaseSearcher.prototype.setPageSize = function(pagesize) {
    this.pagesize = pagesize;
}

MMBaseSearcher.prototype.getQueryId = function() {
    var searchAnchor = $(this.div).find("a.search")[0];
    var id = searchAnchor.href.substring(searchAnchor.href.indexOf("#") + 1);
    return id;
}

MMBaseSearcher.prototype.getId = function() {
    var qid = this.getQueryId().substring("mm_related_".length);
    qid = qid.substring(0, qid.indexOf("_"));
    return qid;
}

MMBaseSearcher.prototype.getResultDiv = function() {
    return $(this.div).find("div.searchresult")[0]
}

/**
 * This method is called if somebody clicks on a.search.
 * It depends on a jsp /mmbase/searchrelate/page.jspx to return the search results.
 * Feeded are 'id' 'offset' and 'search'.
 * The actual query is supposed to be on the user's session, and will be picked up in page.jspx.
 */
MMBaseSearcher.prototype.search = function(val, offset) {
    if (val != null) {
        $(this.div).find("input.search").val(val);
    }
    val = $(this.div).find("input.search").val();

    var newSearch = val;
    if (newSearch != this.value) {
        this.searchResults = {};
        this.value = newSearch;
        this.totalsize = -1;
        this.last = -1;
    }
    if (this.offset != offset) {
        this.last = -1;
        this.offset = offset;
    }

    var rep = this.getResultDiv();
    var params = { id: this.getQueryId(),
                   offset: offset,
                   search: "" + this.value,
                   fields: this.fields,
                   pagesize: this.pagesize,
                   maxpages: this.maxpages,
                   customizedir: this.customizedir,
                   editrelations: this.canEditrelations };

    var result = this.searchResults["" + offset];
    this.logger.debug("Searching " + this.searchUrl + " " + params);

    $(rep).empty();
    if (result == null) {
        var self = this;
        $.ajax({ url: this.searchUrl, type: "GET", dataType: "xml", data: params,
                 beforeSend: function() {
                    $(self.div).find("input.search").addClass("searching");
                    $(rep).append($('<p><fmt:message key="searching" /></p>'));
                 },
                 complete: function(res, status) {
                    if ( status == "success" || status == "notmodified" ) {
                        result = res.responseText;
                        $(rep).empty();
                        $(rep).attr("class", $(result).attr("class"));
                        $(self.div).find("input.search").removeClass("searching");
                        //console.log($(result).find("*").length);
                        $(rep).append($(result).find("> *"));
                        self.searchResults["" + offset] = result;
                        self.addNewlyRelated(rep);
                        self.deleteNewlyRemoved(rep);
                        self.bindEvents(rep);
                        $(self.relater.div).trigger("mmsrPaged", [status, self.relater]);
                    }
                }
               });

    } else {
        this.logger.debug("reusing " + offset);
        this.logger.debug(rep);
        var self = this;
        $(rep).append($(result).find("> *"));
        this.addNewlyRelated(rep);
        self.deleteNewlyRemoved(rep);
        this.bindEvents(rep);
        $(self.relater.div).trigger("mmsrPaged", [status, self.relater]);
    }
    return false;
}

MMBaseSearcher.prototype.totalSize = function(size) {
    var span = $(this.div).find("caption span.size")[0];
    if (size == null) {
        if (this.totalsize == -1) {
            this.totalsize = span == null ? 0 : parseInt($(span).text());
        }
    } else {
        if (span != null) $(span).text(size);
        this.totalsize = size;
    }
    return this.totalsize;
}

MMBaseSearcher.prototype.lastIndex = function(size) {
    var span = $(this.div).find("caption span.last")[0];
    if (span == null) return;
    if (size == null) {
        if (this.last == -1) {
            this.last = parseInt($(span).text());
        }
    } else {
        $(span).text(size);
        this.last = size;
    }
    return this.last;
}

MMBaseSearcher.prototype.inc = function() {
    this.logger.debug("inc");
    this.totalSize(1 + this.totalSize());
    this.lastIndex(1 + this.lastIndex());
}
MMBaseSearcher.prototype.dec = function() {
    this.logger.debug("dec");
    this.totalSize(-1 + this.totalSize());
    this.lastIndex(-1 + this.lastIndex());
}


MMBaseSearcher.prototype.create = function () {
    var rep = this.getResultDiv();
    $(rep).removeClass("implicit");
    var url = "${mm:link('/mmbase/searchrelate/create.jspx')}";
    var params = {
        queryid: this.getQueryId(),
        id: this.getId(),
        context: this.context,
        sessionname : this.sessionName
    };

    var self = this;
    $.ajax({url: url, type: "GET", data: params,
            complete: function(res, status){
                if (status == "success") {
                    var result = res.responseText;
                    $(rep).empty();
                    $(rep).append($(result));
                    var nodeManager = $(result).find("input[name='nodemanager']")[0].value;
                    self.logger.debug(nodeManager);
                    if (self.validator) {
                        self.validator.prefetchNodeManager(nodeManager);
                        self.validator.addValidation(rep);
                    }
                    $(rep).find('input[type="submit"]').attr("disabled", "disabled");
                    self.validator.validateHook = function(valid) {
                        if (valid) $(rep).find('input[type="submit"]').removeAttr("disabled");
                        else $(rep).find('input[type="submit"]').attr("disabled", "disabled");
                    }
                    var options = {
                        url: "${mm:link('/mmbase/searchrelate/create.jspx')}",
                        target:     null,
                        success:    function(subres, substatus) {
                            self.logger.debug(substatus + ": " + subres);
                            var newNodeSpan = $(subres).find("span.newnode");
                            if (newNodeSpan.length > 0) {
                                var newNode = newNodeSpan[0].firstChild.nodeValue;
                                var tr = self.getTr(newNode);
                                self.search(newNode, 0);
                            }
                        }
                    };
                    $(rep).find("form.mm_form").ajaxForm(options);

                }
            }
           });
    $(rep).append($("<p>Creating</p>"));
}


MMBaseSearcher.prototype.getTr = function(node) {
    var url = "${mm:link('/mmbase/searchrelate/node.tr.jspx')}";
    var params = {id: this.getQueryId(), node: node, fields: this.fields, customizedir: this.customizedir, editrelations: this.canEditrelations};
    var result;
    $.ajax({async: false, url: url, type: "GET", dataType: "xml", data: params,
            complete: function(res, status){
                if ( status == "success" || status == "notmodified" ) {
                    result = res.responseText;
                }
            }
           });
    return result;
}

MMBaseSearcher.prototype.deleteNewlyRemoved = function(rep) {
    this.logger.debug("Deleting newly removed");
    var self = this;
    var deleted = false;
    if (this.relater != null && this.type == "repository") {
        $(rep).find("tr.click").each(function() {
            if (self.filter(this)) {
                document.createElement("removed").appendChild(this);
                deleted = true;
            }
        });
    }
    if (deleted) {
        this.resetTrClasses();
    }

}


MMBaseSearcher.prototype.filter = function(tr) {
    if (this.type == "repository" && this.relater != null) {
        var number = this.relater.getNumber(tr);
        return this.relater.related[number] != null; // if already related, don't show again
    } else {
        return false;
    }
}

MMBaseSearcher.prototype.addNewlyRelated = function(rep) {
    if (this.relater != null && this.type == "current") {
        this.logger.debug("adding newly related");
        this.logger.debug(this.relater.related);
        this.logger.debug("Appending related " + $(rep).find("table tbody")[0]);
        $.each(this.relater.related, function(key, value) {
            $(rep).find("table tbody").append(value);
        });
    }
}

MMBaseSearcher.prototype.bindEvents = function() {
    if (this.relater != null) {
        this.relater.bindEvents(this.div, this.type);
    }
    var self = this;
    this.logger.debug("binding to "+ $(this.div).find("a.navigate"));


    $(this.div).find("a.navigate").click(function(ev) {
        var anchor = ev.target;
        self.logger.debug("navigating " + anchor);

        var id = anchor.href.substring(anchor.href.indexOf("#") + 1, anchor.href.lastIndexOf("_"));
        return self.search(document.getElementById(id), anchor.name);
    });
}

MMBaseSearcher.prototype.resetTrClasses = function() {
    this.logger.debug("Resetting tr's");
    $(this.div).find("div.searchresult table tbody tr.click").each(function(i) {
        $(this).removeClass("odd");
        $(this).removeClass("even");
        $(this).addClass(i % 2 == 0 ? "even" : "odd");
    });
}

</fmt:bundle>
</mm:content>
