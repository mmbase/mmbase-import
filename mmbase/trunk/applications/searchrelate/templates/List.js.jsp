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
 * @author Michiel Meeuwissen
 * @version $Id: List.js.jsp,v 1.17 2008-07-24 14:22:25 michiel Exp $
 */


$(document).ready(function() {
    $(document).find("div.list").each(function() {
	if (this.list == null) {
	    this.list = new List(this);
	}
    });
    $(document).find("div.list:last").each(function() {
	List.seq = $(this).find("input[name = 'seq']")[0].value;
    });
});




function List(d) {
    this.div = d;
    var self = this;

    this.callBack = null; // called on delete and create

    this.type = this.find(this.div, "form.list").find("input[name = 'type']")[0].value;
    this.item = this.find(this.div, "form.list").find("input[name = 'item']")[0].value;
    this.source = this.find(this.div, "form.list").find("input[name = 'source']")[0].value;


    this.lastChange = null;
    this.lastCommit = null;

    this.defaultStale = 1000;

    this.valid = true;
    this.validator = new MMBaseValidator();
    this.validator.prefetchNodeManager(this.type);
    this.validator.setup(this.div);

    this.validator.validateHook = function(valid) {
	self.valid = valid;
	self.lastChange = new Date();
    }

    $.timer(1000, function(timer) {
	self.commit();
    });

    this.find(this.div, "a.create").each(function() {
	self.bindCreate(this);
    });
    this.find(this.div, "a.delete").each(function() {
	self.bindDelete(this);
    });


    //console.log($(this.div).find("div.list").remove().find("a.create").end());

    //$(this.div).find("*").not("div.list").not("div.list *").find("a.create").each(function() {
    //$(this.div).find("a.delete").each(function() { self.bindDelete(this); });

    $(window).bind("beforeunload",
		   function(ev) {
		       var result = self.commit(0, false);
		       if (!result) {
			   ev.returnValue = '<fmt:message key="invalid" />';
		       }
		       return result;
		   });

    // automaticly make the entries empty on focus if they evidently contain the default value only
    $(this.div).find("input[type='text']").filter(function() {
	return this.value.match(/^<.*>$/); }).one("focus", function() {
	    this.value = "";
	    self.validator.validateElement(this);
	});

    this.setTabIndices();
    $(this.div).trigger("mmsrRelatedNodesReady", [self]);
}



/**
 * Finds all elements with given node name and class, but ignores everything in a child div.list.
 */
List.prototype.find = function(el, selector, result) {
    if (result == null) {
	result = [];
    }
    var self = this;
    $(el).find("> *").each(function() {
	if ($(this).hasClass("list") && this.nodeName == 'div') {

	} else {
	    if ($(this).filter(selector).length > 0) {
		result[result.length] = this;
	    }
	    self.find(this, selector, result);

	}

    });
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
	params.item   = this.item;
	params.mm_list_sequence  = List.seq++;
	params.source = this.source;
	$.ajax({async: false, url: url, type: "GET", dataType: "xml", data: params,
		complete: function(res, status){
		    try {
			if ( status == "success" || status == "notmodified" ) {
			    var r = $(res.responseText)[0];
			    // remove default value on focus
			    $(r).find("input").one("focus", function() {
				this.value = "";
				a.list.validator.validateElement(this);
			    });
			    a.list.find(a.list.div, "ol").append(r);
			    a.list.validator.addValidation(r);
			    a.list.find(r, "a.delete").each(function() {
				a.list.bindDelete(this);
			    });
			    $(r).find("* div.list").each(function() {
				var div = this;
				if (div.list == null) {
				    div.list = new List(div);
			    }
			    });
			    a.list.executeCallBack("create", r);
			} else {
			    alert(status);

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
			    ol.removeChild(li);
			    a.list.executeCallBack("delete", li);
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
	(this.validator == null || this.validator.isChanged()) &&
	(this.lastCommit == null || this.lastCommit.getTime() < this.lastChange.getTime());
}

List.prototype.status = function(message, fadeout) {
    this.find(this.div, "span.status").each(function() {
	$(this).fadeIn("fast");
	$(this).empty();
	$(this).append(message);
	if (fadeout) {
	    var p = this;
	    $(this).fadeOut(4000, function() {$(p).empty()} );
	}
    });
}

/**
 * @param stale Number of millisecond the content may be aut of date. Defaults to 5 s. But on unload it is set to 0.
 */
List.prototype.commit = function(stale, async) {
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
		this.find(this.div, "input[checked], input[type='text'], input[type='hidden'], input[type='password'], option[selected], textarea")
		.each(function() {
		    params[this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;
		});


		var self = this;
		this.status("<img src='${mm:link('/mmbase/style/ajax-loader.gif')}' />");
		$.ajax({ type: "POST",
			 async: async == null ? true : async,
			 url: "${mm:link('/mmbase/searchrelate/list/save.jspx')}",
			 data: params,
			 complete: function(req, textStatus) {
			     self.status('<fmt:message key="saved" />', true);
			 }
		      });

		return true;
	    } else {
		// not stale enough
		return true;
	    }
	} else {
	    return false;
	}
    } else {
	return true;
    }
}




</mm:content>
</fmt:bundle>
