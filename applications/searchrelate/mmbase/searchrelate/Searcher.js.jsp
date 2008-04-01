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
 * @version $Id: Searcher.js.jsp,v 1.7 2008-04-01 13:09:30 michiel Exp $
 */


function MMBaseSearcher(d) {
    this.logEnabled   = false;
    this.traceEnabled = false;
    this.div = d;
    this.value = "";
    this.canUnrelate = $(d).hasClass("can_unrelate");
    this.transaction   = null;
    var self = this;
    $(d).find("span.transactioname").each(function() {
	this.transaction = this.nodeValue;
    });
    this.searchResults = {};
    this.related       = {};
    this.unrelated     = {};
}

MMBaseSearcher.prototype.log = function (msg) {
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


/**
 * This method is called if somebody clicks on a.search.
 * It depends on a jsp /mmbase/searchrelate/page.jspx to return the search results.
 * Feeded are 'id' 'offset' and 'search'.
 * The actual query is supposed to be on the user's session, and will be picked up in page.jspx.
 */
MMBaseSearcher.prototype.search = function(offset) {
    var newSearch = $(this.div).find("input.mm_relate_repository_search")[0].value;
    if (newSearch != this.value) {
	this.searchResults = {};
	this.value = newSearch;
    }
    var id = this.div.id;
    var rep = $(this.div).find("div.mm_relate_repository")[0]

    var url = "${mm:link('/mmbase/searchrelate/page.jspx')}";
    var params = {id: id, offset: offset, search: this.value};

    var result = this.searchResults["" + offset];
    var self = this;
    if (result == null) {
	$.ajax({url: url, type: "GET", dataType: "xml", data: params,
		complete: function(res, status){
		    if ( status == "success" || status == "notmodified" ) {
			var r = $(res.responseText)[0];
			$(rep).empty();
			$(rep).append(r);
			self.searchResults["" + offset] = r;
			self.bindEvents(rep);
		    }
		}
	       });
    } else {
	this.log("reusing " + offset);
	$(rep).empty();
	$(rep).append(result);
	self.bindEvents(rep);
    }
    return false;
}

MMBaseSearcher.prototype.bindEvents = function(rep) {
    var self = this;
    $(rep).find("a.navigate").each(function() {
	$(this).click(function() {
	    return self.search(this.name);
	})});
    $(rep).find("tr.click").each(function() {
	$(this).click(function() {
	    self.relate(this);
	    return false;
	})});
}
/**
 * Moves a node from the 'unrelated' repository to the list of related nodes.
 */
MMBaseSearcher.prototype.relate = function(el) {
    var number = $(el).find("td.node.number")[0].textContent;
    if (typeof(this.unrelated[number]) == "undefined") {
	this.related[number] = el;
    }
    this.unrelated[number] = null;
    $(el).parents("div.mm_related").find("table.relatednodes tbody").append(el);
    $(el).unbind();
    var searcher = this;
    $(el).click(function() {
	searcher.unrelate(this);
    });
}

/**
 * Moves a node from the list of related nodes to the 'unrelated' repository.
 */
MMBaseSearcher.prototype.unrelate = function(el) {
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

/**
 * Commits made changes to MMBase. Depends on a jsp /mmbase/searchrelate/relate.jsp to do the actual work.
*  This jsp, in turn, depends on the query in the user's session which defined what precisely must happen.
 */

MMBaseSearcher.prototype.commit = function(node) {
    this.log("Commiting changed relations of " + this.div.id);
    var id = this.div.id;
    var url = "${mm:link('/mmbase/searchrelate/relate.jspx')}";

    var relatedNumbers = "";
    $.each(this.related, function(key, value) {
	if (value != null) {
	    if (relatedNumbers.length > 0) relatedNumbers += ",";
	    relatedNumbers += key;
	}
    });
    var unrelatedNumbers = "";
    $.each(this.unrelated, function(key, value) {
	if (value != null) {
	    if (unrelatedNumbers.length > 0) unrelatedNumbers += ",";
	    unrelatedNumbers += key + ",";
	}
    });

    this.log("+ " + relatedNumbers);
    this.log("- " + unrelatedNumbers);
    var params = {id: id, related: relatedNumbers, unrelated: unrelatedNumbers};
    if (this.transaction != null) {
	params.transaction = this.transaction;
    }
    $.ajax({url: url, type: "GET", dataType: "xml", data: params,
	    complete: function(res, status){
		if (status == "success") {
		    //console.log("" + res);
		}
	    }
	   });
}


$(document).ready(function(){
    $("body").find("div.mm_related")
    .each(function() {
	var parent = this;
	var anchor = $(parent).find("> a.search")[0];
	anchor.searcher = new MMBaseSearcher(parent);
	$(anchor).click(function() {
	    return this.searcher.search(0);
	});
	if (anchor.search.canUnrelate) {
	    $(parent).find("tr.click").each(function() {
		$(this).click(function() {
		    anchor.searcher.unrelate(this);
		    return false;
		})});
	}
    });

});


</mm:content>
