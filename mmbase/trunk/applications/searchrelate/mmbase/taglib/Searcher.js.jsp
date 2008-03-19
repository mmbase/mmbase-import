// -*- mode: javascript; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"  %>
<mm:content type="text/javascript" expires="0">

function MMBaseSearcher(d) {
    this.div = d;
    this.value = "";
    this.searchResults = {};
    this.related       = {};
    this.unrelated     = {};
}

MMBaseSearcher.prototype.search = function(offset) {
    var newSearch = $(this.div).find("input.mm_relate_repository_search")[0].value;
    if (newSearch != this.value) {
	this.searchResults = {};
	this.value = newSearch;
    }
    var id = this.div.id;
    var rep = $(this.div).find("div.mm_relate_repository")[0]
    var url = "${mm:link('/mmbase/taglib/page.jspx')}";
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
		}
	       });
    } else {
	console.log("resing " + offset);
	$(rep).empty();
	$(rep).append(result);
    }



    return false;
}
MMBaseSearcher.prototype.relate = function(el) {
    this.related[el] = el;
    this.unrelated[el] = null;
    $(el).parents("div.mm_related").find("table.relatednodes tbody").append(el);
    $(el).unbind();
    var searcher = this;
    $(el).click(function() {
	searcher.unrelate(this);
    });
}

MMBaseSearcher.prototype.unrelate = function(el) {
    this.unrelated[el] = el;
    this.related[el] = null;
    $(el).parents("div.mm_related").find("table.searchresult tbody").append(el);
    $(el).unbind();
    var searcher = this;
    $(el).click(function() {
	searcher.relate(this)
    });
}

MMBaseSearcher.prototype.commit = function(node) {

}

$(document).ready(function(){
    $("body").find(".mm_related a.search")
    .each(function() {
	var parent = $(this).parent("div.mm_related");
	this.searcher = new MMBaseSearcher(parent[0]);
	$(this).click(function() {
	    return this.searcher.search(0);
	});
    });
});


</mm:content>
