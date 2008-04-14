// -*- mode: javascript; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"  %>
<mm:content type="text/javascript" expires="0">
/**
 * Generic mmbase search & relate tool. Javascript part.
 *
 *
 * Usage: It is sufficient to use the mm:relate tag. This tag wil know wether this javascript is already loaded, and if not, will arrange for that. It is required to load jquery, though.

 * On ready, the necessary javascript will then be connected to .mm_related a.search

 *
 * @author Michiel Meeuwissen
 * @version $Id: Searcher.js.jsp,v 1.3 2008-04-14 15:45:15 michiel Exp $
 */

$(document).ready(function(){
    $("body").find("div.mm_related").each(function() {
	this.relater = new MMBaseRelater(this);
    });
});


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



function MMBaseRelater(d) {
    this.div          = d;
    this.related       = {};
    this.unrelated     = {};
    this.logger        = new MMBaseLogger();
    this.logger.debug(d);
    this.logger.debug("setting up current");
    this.current       = $(d).find(".mm_relate_current")[0];
    this.canUnrelate = $(d).hasClass("can_unrelate");
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
    if (this.repository != null) this.addSearcher(this.repository, "repository");

}


MMBaseRelater.prototype.addSearcher = function(el, type) {
    var relater = this;
    if ($(el).hasClass("searchable")) {
	var searcher =  new MMBaseSearcher(el, this, type, this.logger);
	$(el).find("a.search").each(function() {
	    var anchor = this;
	    anchor.searcher = searcher;
	    $(anchor).click(function(el) {
		return this.searcher.search(anchor, 0);
	    });
	});
	$(el).find("a.create").each(function() {
	    var anchor = this;
	    anchor.searcher = searcher;
	    $(anchor).click(function(el) {
		return this.searcher.create(anchor);
	    });
	});
	if (this.canUnrelate) {
	    $(el).find("tr.click").each(function() {
		$(this).click(function(tr) {
		    relater.unrelate(this);
		    return false;
		})});
	}
    }
}


/**
 * Commits made changes to MMBase. Depends on a jsp /mmbase/searchrelate/relate.jsp to do the actual work.
*  This jsp, in turn, depends on the query in the user's session which defined what precisely must happen.
 */

MMBaseRelater.prototype.commit = function(el) {
    var relatedNumbers   = this.getNumbers(this.related);
    var unrelatedNumbers = this.getNumbers(this.unrelated);

    $(a).addClass("submitting");
    $(a).removeClass("failed");
    $(a).removeClass("succeeded");
    if (relatedNumbers != "" || unrelatedNumbers != "") {
	var a = el.target;
	this.logger.debug("Commiting changed relations of " + this.div.id);
	var id = this.div.id;
	var url = "${mm:link('/mmbase/searchrelate/relate.jspx')}";

	this.logger.debug("+ " + relatedNumbers);
	this.logger.debug("- " + unrelatedNumbers);
	var params = {id: id, related: relatedNumbers, unrelated: unrelatedNumbers};
	if (this.transaction != null) {
	    params.transaction = this.transaction;
	}
	$.ajax({async: false, url: url, type: "GET", dataType: "xml", data: params,
		complete: function(res, status){
		    $(a).removeClass("submitting");
		    if (status == "success") {
			//console.log("" + res);
			$(a).addClass("succeeded");
			return true;
		    } else {
			$(a).addClass("failed");
			return false;
		    }
		}
	       });
    } else {
	this.logger.debug("No changes, no need to commit");
	$(a).addClass("succeeded");
    }
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
	    if ($(this).hasClass("new") || (self.relater != null && self.relater.canUnrelate)) {
		$(this).click(function() {
		    self.unrelate(this);
		    return false;
		})
	    }
	});
    }
}


MMBaseRelater.prototype.resetTrClasses  = function() {
    $(this.div).find("div.mm_relate_current")[0].searcher.resetTrClasses();
    $(this.div).find("div.mm_relate_repository")[0].searcher.resetTrClasses();

}

MMBaseRelater.prototype.getNumber = function(tr) {
    return  $(tr).find("td.node.number")[0].textContent;
}


/**
 * Moves a node from the 'unrelated' repository to the list of related nodes.
 */
MMBaseRelater.prototype.relate = function(tr) {
    var number = this.getNumber(tr);


    // Set up data
    if (typeof(this.unrelated[number]) == "undefined") {
	this.related[number] = tr;
    }
    this.logger.debug("Found number to relate " + number + "+" + this.getNumbers(this.related));
    this.unrelated[number] = null;

    // Set up HTML
    var current =  $(this.div).find("div.mm_relate_current div.searchresult table tbody");
    this.logger.debug(current[0]);
    current.append(tr);

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



/**
 * Moves a node from the list of related nodes to the 'unrelated' repository.
 */
MMBaseRelater.prototype.unrelate = function(tr) {
    var number = this.getNumber(tr);

    // Set up data
    if (typeof(this.related[number]) == "undefined") {
	this.unrelated[number] = tr;
    }
    this.related[number] = null;

    // Set up HTML
    var repository =  $(this.div).find("div.mm_relate_repository div.searchresult table tbody");
    repository.append(tr);

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
}




function MMBaseSearcher(d, r, type, logger) {
    this.div = d;
    this.div.searcher = this;
    this.relater = r;
    this.type    = type;
    this.pagesize = 10;
    this.maxpages = 20;
    this.logger  = logger != null ? logger : new MMBaseLogger();
    this.value = "";
    this.offset = 0;
    this.transaction   = null;
    var self = this;
    $(d).find("span.transactioname").each(function() {
	this.transaction = this.nodeValue;
    });
    this.searchResults = {};
    this.bindEvents();
    this.validator = this.relater.validator;

}


MMBaseSearcher.prototype.getQueryId = function() {
    var searchAnchor = $(this.div).find("a.search")[0];
    var id = searchAnchor.href.substring(searchAnchor.href.indexOf("#") + 1);
    return id;
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
MMBaseSearcher.prototype.search = function(el, offset) {
    var newSearch = $(this.div).find("input")[0].value;
    if (newSearch != this.value) {
	this.searchResults = {};
	this.value = newSearch;
    }
    this.offset = offset;

    var rep = this.getResultDiv();
    var url = "${mm:link('/mmbase/searchrelate/page.jspx')}";
    var params = {id: this.getQueryId(), offset: offset, search: this.value, pagesize: this.pagesize, maxpages: this.maxpages};

    var result = this.searchResults["" + offset];
    $(rep).empty();
    if (result == null) {
	var self = this;
	$.ajax({url: url, type: "GET", dataType: "xml", data: params,
		complete: function(res, status){
		    if ( status == "success" || status == "notmodified" ) {
			result = res.responseText;
			self.logger.debug(rep);
			$(rep).empty();
			$(rep).append($(result).find("> *"));
			self.searchResults["" + offset] = result;
			self.addNewlyRelated(rep);
			self.deleteNewlyRemoved(rep);
			self.bindEvents(rep);

		    }
		}
	       });
	$(rep).append($("<p>Searching</p>"));
    } else {
	this.logger.debug("reusing " + offset);
	this.logger.debug(rep);
	var self = this;
	$(rep).append($(result).find("> *"));
	this.addNewlyRelated(rep);
	self.deleteNewlyRemoved(rep);
	this.bindEvents(rep);
    }
    return false;
}

MMBaseSearcher.prototype.create = function () {
    var rep = this.getResultDiv();
    var url = "${mm:link('/mmbase/searchrelate/create.jspx')}";
    var params = {id: this.getQueryId()};
    var self = this;
    $.ajax({url: url, type: "GET", data: params,
	    complete: function(res, status){
		if ( status == "success") {
		    result = res.responseText;
		    $(rep).empty();
		    $(rep).append($(result).find("> *"));
		    console.log($(result).find("input[name='nodemanager']")[0]);
		    self.validator.prefetchNodeManager($(result).find("input[name='nodemanager']")[0].value);
		    self.validator.addValidation(rep);
		    var options = {
			url: "${mm:link('/mmbase/searchrelate/create.jspx')}",
			target:     null,
			success:    function(res) {
			    var newNode = $(res).find("span.newnode")[0].firstChild.nodeValue;
			    var tr = self.getTr(newNode);
			    self.relater.relate(tr);
			    self.search(null, self.offset);
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
    var params = {id: this.getQueryId(), node: node};
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

    $(this.div).find("a.navigate").click(function(el) {
	self.logger.debug("navigating " );
	var anchor = el.target;
	return self.search(anchor, anchor.name);
    });
}

MMBaseSearcher.prototype.resetTrClasses = function() {
    var i = 0;
    $(this.div).find("div.searchresult table tbody tr").each(function() {
	$(this).removeClass("odd");
	$(this).removeClass("even");
	$(this).addClass(i % 2 == 0 ? "even" : "odd");
	i++;
    });
}

</mm:content>
