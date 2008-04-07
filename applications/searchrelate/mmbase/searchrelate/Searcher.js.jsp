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
 * @version $Id: Searcher.js.jsp,v 1.10 2008-04-07 15:03:18 michiel Exp $
 */

$(document).ready(function(){
    $("body").find("div.mm_related")
    .each(function() {
	this.relater = new MMBaseRelater(this);
    });
});


function MMBaseLogger() {
    this.logEnabled   = false;
    this.traceEnabled = false;
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
    this.logger.debug("setting up current");
    this.current       = $(d).find(".mm_relate_current")[0];
    if (this.current != null) this.addSearcher(this.current, "current");
    this.logger.debug("setting up repository");
    this.repository    = $(d).find(".mm_relate_repository")[0];
    if (this.repository != null) this.addSearcher(this.repository, "repository");
    this.canUnrelate = $(d).hasClass("can_unrelate");


}


MMBaseRelater.prototype.addSearcher = function(el, type) {
    var relater = this;
    if ($(el).hasClass("searchable")) {
	$(el).find("a.search").each(function() {
	    console.log(el);
	    console.log("found "+ this);
	    var anchor = this;
	    anchor.searcher = new MMBaseSearcher(el, relater, type, relater.logger);
	    $(anchor).click(function(el) {
		return this.searcher.search(anchor, 0);
	    });
	    if (relater.canUnrelate) {
		$(parent).find("tr.click").each(function() {
		    $(this).click(function() {
			anchor.searcher.unrelate(this);
			return false;
		    })});
	    }
	});
    }
}


/**
 * Commits made changes to MMBase. Depends on a jsp /mmbase/searchrelate/relate.jsp to do the actual work.
*  This jsp, in turn, depends on the query in the user's session which defined what precisely must happen.
 */

MMBaseRelater.prototype.commit = function(el) {
    var a = el.target;
    $(a).addClass("submitting");
    $(a).removeClass("failed");
    $(a).removeClass("succeeded");
    this.logger.debug("Commiting changed relations of " + this.div.id);
    var id = this.div.id;
    var url = "${mm:link('/mmbase/searchrelate/relate.jspx')}";

    var relatedNumbers   = this.getNumbers(this.related);
    var unrelatedNumbers = this.getNumbers(this.unrelated);

    this.logger.debug("+ " + relatedNumbers);
    this.logger.debug("- " + unrelatedNumbers);
    var params = {id: id, related: relatedNumbers, unrelated: unrelatedNumbers};
    if (this.transaction != null) {
	params.transaction = this.transaction;
    }
    $.ajax({async: true, url: url, type: "GET", dataType: "xml", data: params,
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
}


/**
 * Moves a node from the list of related nodes to the 'unrelated' repository.
 */
MMBaseRelater.prototype.unrelate = function(el) {
    var number = $(el).find("td.node.number")[0].textContent;
    if (typeof(this.related[number]) == "undefined") {
	this.unrelated[number] = el;
    }
    this.related[number] = null;
    $(el).parents("div.mm_related").find("table.searchresult tbody").append(el);
    $(el).unbind();
    var searcher = this;
    $(el).click(function() {
	searcher.relate(this)
    });
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
    $(rep).find("tr.click").each(function() {
	$(this).click(function() {
	    self.relate(this);
	    return false;
	})});
}
/**
 * Moves a node from the 'unrelated' repository to the list of related nodes.
 */
MMBaseRelater.prototype.relate = function(el) {
    var number = $(el).find("td.node.number")[0].textContent;
    if (typeof(this.unrelated[number]) == "undefined") {
	this.related[number] = el;
    }
    this.logger.debug("Found number to relate " + number + "+" + this.getNumbers(this.related));
    this.unrelated[number] = null;
    var current =  $(el).parents("div.mm_related").find("div.mm_relate_current table.searchresult tbody");
    this.logger.debug(current[0]);
    $(el).parents("div.mm_related").find("div.mm_relate_current table.searchresult tbody").append(el);
    $(el).unbind();
    var searcher = this;
    $(el).click(function() {
	searcher.unrelate(this);
    });
}





function MMBaseSearcher(d, r, type, logger) {
    this.div = d;
    this.relater = r;
    this.type    = type;
    this.logger  = logger != null ? logger : new MMBaseLogger();
    this.value = "";
    this.transaction   = null;
    var self = this;
    $(d).find("span.transactioname").each(function() {
	this.transaction = this.nodeValue;
    });
    this.searchResults = {};
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
    var id = el.href.substring(el.href.indexOf("#") + 1);
    var rep = $(this.div).find("> div")[0]

    var url = "${mm:link('/mmbase/searchrelate/page.jspx')}";
    var params = {id: id, offset: offset, search: this.value};

    var result = this.searchResults["" + offset];
    var self = this;
    if (result == null) {
	$.ajax({url: url, type: "GET", dataType: "xml", data: params,
		complete: function(res, status){
		    if ( status == "success" || status == "notmodified" ) {
			var r = $(res.responseText)[0];
			self.logger.debug(rep);
			$(rep).empty();
			$(rep).append($(r).find("> *"));
			self.searchResults["" + offset] = r;
			self.bindEvents(rep);

		    }
		}
	       });
    } else {
	this.logger.debug("reusing " + offset);
	$(rep).empty();
	$(rep).append(result);
	self.bindEvents(rep);
    }
    return false;
}

MMBaseSearcher.prototype.bindEvents = function(el) {
    if (this.relater != null) {
	this.relater.bindEvents(el, this.type);
    }
    var self = this;
    $(el).find("a.navigate").click(function(el) {
	var anchor = el.target;
	return self.search(anchor, anchor.name);
    });
}


</mm:content>
