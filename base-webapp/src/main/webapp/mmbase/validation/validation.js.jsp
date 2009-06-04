// -*- mode: javascript; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"  %>
<mm:content type="text/javascript" expires="300">
/**
 * See test.jspx for example usage.

 * new MMBaseValidator(el): attaches events to all elements in elwhen ready.
 * new MMBaseValidator():       attaches no events yet. You could replace some functions, add hooks, set settings first or so.
 *                              then call validator.setup(el).
 *
 * @author Michiel Meeuwissen
 * @version $Id: validation.js.jsp,v 1.61 2009-04-28 14:44:06 michiel Exp $
 */



function MMBaseValidator(root) {

    this.logEnabled   = false;
    this.traceEnabled = false;


    this.dataTypeCache   = new Object();
    this.invalidElements = 0;
    //this.changedElements  = 0;
    this.elements        = [];
    this.validateHook;
    this.root = root;
    this.setup();
    this.lang          = null;
    this.sessionName   = null;
    this.id = MMBaseValidator.validators.push(this);
    if (MMBaseValidator.validators.length == 1) {
	    setTimeout(MMBaseValidator.watcher, 500);
    }
    this.activeElement = null;
    this.checkAfter    = 600;
    this.logArea       = "logarea";

}

MMBaseValidator.validators = [];


MMBaseValidator.watcher = function() {
    for (var i = 0; i < MMBaseValidator.validators.length; i++) {
	var validator = MMBaseValidator.validators[i];
	var el = validator.activeElement;
	var now = new Date().getTime();
        if (el != null) {
            if (! el.serverValidated) {
		        if (new Date(validator.checkAfter + el.lastChange.getTime()) < now) {
                    MMBaseValidator.validators[i].validateElement(MMBaseValidator.validators[i].activeElement, true);
		        }
            }
        }
    }
    setTimeout(MMBaseValidator.watcher, 150);

}



MMBaseValidator.prototype.setup = function(el) {
    if (el != null) {
	    this.root = el;
	    if (this.root == window) this.root = this.root.document;
    }
    if (this.root != null) {
	    var self = this;
	    $(document).ready(function(event) {
	        self.onLoad(event);
	    });
    }
}


MMBaseValidator.prototype.onLoad = function(event) {
    if (this.root == null && event != null) {
        this.root = event.target || event.srcElement;
    }
    //console.log("Root" + this.root);
    this.addValidation(this.root);
    //validatePage(target);
}



MMBaseValidator.prototype.log = function (msg) {
    if (this.logEnabled) {
        var errorTextArea = document.getElementById(this.logarea);
        if (errorTextArea) {
            errorTextArea.value = "LOG: " + msg + "\n" + errorTextArea.value;
        } else {
            // firebug console
	        if (typeof(console) != "undefined") {
		        console.log(msg);
	        }
        }
    }
}

MMBaseValidator.prototype.trace = function (msg) {
    if (this.traceEnabled && this.logEnabled) {
        var errorTextArea = document.getElementById(this.logarea);
        if (errorTextArea) {
            errorTextArea.value = "TRACE: " + msg + "\n" + errorTextArea.value;
        } else {
            // firebug console
	        if (typeof(console) != "undefined") {
		        console.log(msg);
	        }
        }
    }
}

/**
* Returns the mmbase node number associated with the given input element. Or null, if there is
 * no such node, or the node is not yet created.
*/
MMBaseValidator.prototype.getNode = function(el) {
    return this.getDataTypeKey(el).node;
}

/**
 * Whether a restriction on a certain input element mus be enforced.
 */
MMBaseValidator.prototype.enforce = function(el, enf) {
    this.trace("Enforce " + enf);
    if (enf == 'never') return false;
    if (enf == 'always') return true;
    if (enf == 'absolute') return true;
    if (enf == 'oncreate') return  this.getNode(el) == null;
    if (enf == 'onchange') return  this.getNode(el) == null || this.isChanged(el);
}

MMBaseValidator.prototype.isChanged = function(el) {
    if (el != null) {
	    return this.getValue(el) != el.originalValue;
    } else {
	    var els = this.elements;
	    for (var  i = 0; i < els.length; i++) {
            var entry = els[i];
	        if (this.isChanged(entry)) return true;
	    }
	    return false;
    }
}

/**
 * Work around http://dev.jquery.com/ticket/155
 * Actually, or course, it's a bug in that horrible browser IE.
*/
MMBaseValidator.prototype.find = function(el, path, r) {
    if (r == null) r = [];
    if (typeof(path) == "string") path = path.split(/\s+/);

    var tagName = path.shift();
    var tag = el == null ? null : el.firstChild;
    while (tag != null) {
	    if (tag.nodeType == 1) {
	        var name = tag.nodeName.replace(/^.*:/,'');
	        if (name == tagName) {
		        if (path.length == 0) {
		            r.push(tag);
		        } else {
		            this.find(tag, path, r);
		        }
	        }
	    }
	    tag = tag.nextSibling;
    }
    return r;
}


/**
 * Whether the element is a 'required' form input
 */
MMBaseValidator.prototype.isRequired = function(el) {
    if (el.mm_isrequired != null) return el.mm_isrequired;
    var re = this.find(this.getDataTypeXml(el), 'datatype required')[0];
    el.mm_isrequired = re != null && ("true" == "" + re.getAttribute("value"));
    el.mm_isrequired_enforce = re != null && re.getAttribute("enforce");
    return el.mm_isrequired;
}

/**
 * Whether the value in the form element obeys the restrictions on length (minLength, maxLength, length)
 */
MMBaseValidator.prototype.lengthValid = function(el) {
    if (! this.isRequired(el) && this.enforce(el, el.mm_isrequired_enforce) && this.getValue(el).length == 0) return true;
    var xml = this.getDataTypeXml(el);

    if (el.mm_minLength_set == null) {
        var ml =  this.find(xml, 'datatype minLength')[0];
        if (ml != null) {
            el.mm_minLength = ml.getAttribute("value");
            el.mm_minLength_enforce = ml.getAttribute("enforce");
        }
        el.mm_minLength_set = true;
    }
    if (el.mm_minLength != null && el.value != null && el.value.length < el.mm_minLength) {
        return false;
    }

    if (el.mm_maxLength_set == null) {
        var ml =  this.find(xml, 'datatype maxLength')[0];
        if (ml != null) {
            el.mm_maxLength = ml.getAttribute("value");
            el.mm_maxLength_enforce = ml.getAttribute("enforce");
        }
        el.mm_maxLength_set = true;
    }

    if (el.mm_maxLength != null && el.value != null && el.value.length > el.mm_maxLength) {
        return false;
    }

    if (el.mm_length_set == null) {
        var l =  this.find(xml, 'datatype length')[0];
        if (l != null) {
            el.mm_length = l.getAttribute("value");
            el.mm_length_enforce = l.getAttribute("enforce");
        }
        el.mm_length_set = true;
    }

    if (el.mm_length != null && el.value.length != el.mm_length) {
        return false;
    }
    return true;
}

// much much, too simple
MMBaseValidator.prototype.javaScriptPattern = function(javaPattern) {
    try {
        var flags = "";
        if (javaPattern.indexOf("(?i)") == 0) {
            flags += "i";
            javaPattern = javaPattern.substring(4);
        }
        if (javaPattern.indexOf("(?s)") == 0) {
            //this.log("dotall, not supported");
            javaPattern = javaPattern.substring(4);
            // I only hope this is always right....
            javaPattern = javaPattern.replace(/\./g, "(.|\\n)");
        }
        javaPattern = javaPattern.replace(/\\A/g, "\^");
        javaPattern = javaPattern.replace(/\\z/g, "\$");

        var reg = new RegExp(javaPattern, flags);
        return reg;
    } catch (ex) {
        this.log(ex);
        return null;
    }
}

MMBaseValidator.prototype.patternValid = function(el) {
    if (this.isString(el)) {
        var xml = this.getDataTypeXml(el);
        if (el.mm_pattern == null) {
            var javaPattern = this.find(xml, 'datatype pattern')[0].getAttribute("value");
            el.mm_pattern = this.javaScriptPattern(javaPattern);
            if (el.mm_pattern == null) return true;
            this.trace("pattern : " + el.mm_pattern + " " + el.value);
        }
        return el.mm_pattern.test(el.value);
    } else {
        return true;
    }
}

MMBaseValidator.prototype.hasJavaClass = function(el, javaClass) {
    var pattern = new RegExp(javaClass);
    var xml = this.getDataTypeXml(el);
    var javaClassElement = this.find(xml, 'datatype class')[0];
    if (! javaClassElement) return false;
    var name = javaClassElement.getAttribute("name");
    if (pattern.test(name)) {
        return true;
    }
    var ex = javaClassElement.getAttribute("extends");
    var javaClasses = ex.split(",");
    for (i = 0; i < javaClasses.length; i++) {
        if (pattern.test(javaClasses[i])) {
            return true;
        }
    }
    //this.log("" + el + " is not numeric");
    return false;
}

/**
 * Whether the form element represents a numeric value. There is made no difference between float,
 * double, integer and long. This means that we don't care about loss of precision only.
 */
MMBaseValidator.prototype.isNumeric = function(el) {
    if (el.mm_isnumeric != null) return el.mm_isnumeric;
    el.mm_isnumeric = this.hasJavaClass(el, "org\.mmbase\.datatypes\.NumberDataType");
    return el.isnumeric;
}
MMBaseValidator.prototype.isInteger = function(el) {
    if (el.mm_isinteger != null) return el.mm_isinteger;
    el.mm_isinteger = this.hasJavaClass(el, "(org\.mmbase\.datatypes\.IntegerDataType|org\.mmbase\.datatypes\.LongDataType)");
    return el.mm_isinteger;
}
MMBaseValidator.prototype.isFloat = function(el) {
    if (el.mm_isfloat != null) return el.mm_isfloat;
    el.mm_isfloat = this.hasJavaClass(el, "(org\.mmbase\.datatypes\.FloatDataType|org\.mmbase\.datatypes\.DoubleDataType)");
    return el.mm_isfloat;
}
MMBaseValidator.prototype.isString = function(el) {
    if (el.mm_isstring != null) return el.mm_isstring;
    el.mm_isstring =  this.hasJavaClass(el, "org\.mmbase\.datatypes\.StringDataType");
    return el.mm_isstring;
}

MMBaseValidator.prototype.isDateTime = function(el) {
    if (el.mm_isdatetime != null) return el.mm_isdatetime;
    el.mm_isdatetime = this.hasJavaClass(el, "org\.mmbase\.datatypes\.DateTimeDataType");
    return el.mm_isdatetime;
}
MMBaseValidator.prototype.isBinary = function(el) {
    if (el.mm_isbinary != null) return el.mm_isbinary;
    el.mm_isbinary = this.hasJavaClass(el, "org\.mmbase\.datatypes\.BinaryDataType");
    return el.isbinary;
}
MMBaseValidator.prototype.isCheckEquality = function(el) {
    if (el.mm_ischeckequality != null) return el.mm_ischeckequality;
    el.mm_ischeckequality = this.hasJavaClass(el, "org\.mmbase\.datatypes\.CheckEqualityDataType");
    return el.ischeckequality;
}

MMBaseValidator.prototype.isXml = function(el) {
    if (el.mm_isxml != null) return el.mm_isxml;
    el.mm_isxml= this.hasJavaClass(el, "org\.mmbase\.datatypes\.XmlDataType");
    return el.mm_isxml;
}

MMBaseValidator.prototype.INTEGER = /^[+-]?\d+$/;

MMBaseValidator.prototype.FLOAT   = /^[+-]?(\d+|\d+\.\d*|\d*\.\d+)(e[+-]?\d+|)$/i;

MMBaseValidator.prototype.typeValid = function(el) {
    if (el.value == "") return true;

    if (this.isInteger(el)) {
        if (! this.INTEGER.test(el.value)) return false;
    }
    if (this.isFloat(el)) {
        if (! this.FLOAT.test(el.value)) return false;
    }
    return true;

}



/**
 * Small utility to just get the dom attribute 'value', but also parse to float, if 'numeric' is true.
 */
MMBaseValidator.prototype.getValueAttribute = function(numeric, el) {
    if (el == null) return null;
    var value = el.getAttribute("value");
    var eval = el.getAttribute("eval");
    if (! eval == "") value = eval;

    if (numeric) {
        if (value == "") return null;
        return parseFloat(value);
    } else {
        return value;
    }
}

/**
 * Whether the value of the given form element satisfies possible restrictions on minimal and
 * maximal values. This takes into account whether it is a numeric value, which is quite important
 * for this.
 */
MMBaseValidator.prototype.minMaxValid  = function(el) {
    this.trace("validating : " + el);
    try {
        var xml   = this.getDataTypeXml(el);
        var value = this.getValue(el);
        var numeric = this.isNumeric(el);
        {
            if (el.mm_minInc_set == null) {
                var minInclusive = this.find(xml, 'datatype minInclusive')[0];
                el.mm_minInc = this.getValueAttribute(numeric, minInclusive);
                el.mm_minInc_enforce = minInclusive != null ? minInclusive.getAttribute("enforce") : null;
                el.mm_minInc_set = true;
            }
            this.trace("" + value + " < " + el.mm_minInc  + " " + this.enforce(el, el.mm_minInc_enforce));
            if (el.mm_minInc != null && this.enforce(el, el.mm_minInc_enforce) && value <  el.mm_minInc) {

                return false;
            }
        }

        {
            if (el.mm_minExcl_set == null) {
                var minExclusive = this.find(xml, 'datatype/ minExclusive')[0];
                el.mm_minExcl = this.getValueAttribute(numeric, minExclusive);
                el.mm_minExcl_enforce = minExclusive != null ? minExclusive.getAttribute("enforce") : null;
                el.mm_minExcl_set = true;
            }
            if (el.mm_minExcl != null && this.enforce(el, el.mm_minExcl_enforce) && value <=  el.mm_minExcl) {
                this.trace("" + value + " <= " + el.mm_minInc);
                return false;
            }
        }
        {
            if (el.mm_maxInc_set == null) {
                var maxInclusive = this.find(xml, 'datatype maxInclusive')[0];
                el.mm_maxInc = this.getValueAttribute(numeric, maxInclusive);
                el.mm_maxInc_enforce = maxInclusive != null ? maxInclusive.getAttribute("enforce") : null;
                el.mm_maxInc_set = true;
            }
            if (el.mm_maxInc != null && this.enforce(el, el.mm_maxInc_enforce) && value >  el.mm_maxInc) {
                this.trace("" + value + " > " + el.mm_maxInc);
                return false;
            }
        }

        {
            if (el.mm_maxExcl_set == null) {
                var maxExclusive = this.find(xml, 'datatype maxExclusive')[0];
                el.mm_maxExcl = this.getValueAttribute(numeric, maxExclusive);
                el.mm_maxExcl_enforce = maxExclusive != null ? maxExclusive.getAttribute("enforce") : null;
                el.mm_maxExcl_set = true;
            }
            if (el.mm_maxExcl != null && this.enforce(el, el.mm_maxExcl_enforce) && value >=  el.mm_maxExcl) {
                this.trace("" + value + " >= " + el.mm_maxExcl);
                return false;
            }
        }
    } catch (ex) {
        this.log(ex);
        throw ex;
    }
    return true;

}


/**
 * Given a certain form element, this returns an XML representing its mmbase Data Type.
 * This will do a request to MMBase, unless this XML was cached already.
 */
MMBaseValidator.prototype.getDataTypeXml = function(el) {
    var key = this.getDataTypeKey(el);
    if (el.mm_key == null) {
        el.mm_key = key.string();
    }
    var dataType = this.dataTypeCache[el.mm_key];
    if (dataType == null) {

	    var url = '<mm:url page="/mmbase/validation/datatype.jspx" />';
	    var params = this.getDataTypeArguments(key);
	    var self = this;
	    $.ajax({async: false, url: url, type: "GET",
                dataType: "xml", data: params,
		        complete: function(res, status){
		            if (status == "success" || res.status == '404') {
			            dataType = res.responseXML;
			            self.dataTypeCache[el.mm_key] = dataType;
                    }
		        }
	           });
	    this.log("Found " + dataType);


    } else {
	    this.trace("Found in cache " + dataType);
    }
    return dataType;
}


function Key() {
    this.node = null;
    this.nodeManager = null;
    this.field = null;
    this.datatype = null;
}
Key.prototype.string = function() {
    return this.dataType + "," + this.field + "," + this.nodeManager;
}

/**
 * Given an element, returns the associated MMBase DataType as a structutre. This structure has three fields:
 * field, nodeManager and dataType. Either dataType is null or field and nodeManager are null. They
 * are all null if the given element does not contain the necessary information to identify an
 * MMBase DataType.
 */
MMBaseValidator.prototype.getDataTypeKey = function(el) {
    if (el == null) return;
    if (el.mm_dataTypeStructure == null) {
        var classNames = el.className.split(" ");
        var result = new Key();
        for (var i = 0; i < classNames.length; i++) {
            var className = classNames[i];
            if (className.indexOf("mm_dt_") == 0) {
                result.dataType = className.substring(6);
            } else if (className.indexOf("mm_f_") == 0) {
                result.field = className.substring(5);
            } else if (className.indexOf("mm_nm_") == 0) {
                result.nodeManager = className.substring(6);
            } else if (className.indexOf("mm_n_") == 0) {
                result.node = className.substring(5);
            }

        }
        this.trace("got " + result);
        el.mm_dataTypeStructure = result;
    }
    return el.mm_dataTypeStructure;
}


/**
 * Fetches all fields of a certain nodemanager at once (with one http request), and fills the cache
 * of 'getDataTypeXml'. The intention is that you call this method if you're sure that all (or a lot
 * of) the fields of a certain nodemanager will be on the page.  Otherwise a new http request will
 * be done for every field.
 *
 */
MMBaseValidator.prototype.prefetchNodeManager = function(nodemanager) {

    var url = '<mm:url page="/mmbase/validation/datatypes.jspx" />';
    var params = {nodemanager: nodemanager };
    var self = this;
    $.ajax({async: false, url: url, type: "GET", dataType: "xml", data: params,
	    complete: function(res, status){
		if (status == "success") {
		    var dataTypes = res.responseXML;

		    var fields = dataTypes.documentElement.childNodes;
		    for (var i = 0; i < fields.length; i++) {
			var key = new Key();
			key.nodeManager = nodemanager;
			key.field = fields[i].getAttribute("name");
			self.dataTypeCache[key.string()] = fields[i];
		    }
		    //console.log("" + res);
		}
	    }
	   });

}


/**
 * All server side JSP's with which this javascript talks, can run in 2 modes. They either accept the
 * one 'datatype' parameter, or a 'field' and a 'nodemanager' parameters.
 * The result of {@link #getDataTypeKey} serves as input, and returned is a query string which can
 * be appended to the servlet path.
 */
MMBaseValidator.prototype.getDataTypeArguments = function(key) {
    if (key.dataType != null) {
        return {datatype: key.dataType};
    } else {
        return {field: key.field, nodemanager: key.nodeManager};
    }
}


/**
 * If it was determined that a certain form element was or was not valid, this function
 * can be used to set an appropriate css class, so that this status also can be indicated to the
 * user using CSS.
 */
MMBaseValidator.prototype.setClassName = function(valid, el) {
    this.trace("Setting classname on " + el);
    if (el.originalClass == null) el.originalClass = el.className;
    el.className = el.originalClass + (valid ? " valid" : " invalid");
}

MMBaseValidator.prototype.hasClass = function(el, searchClass) {
    var pattern = new RegExp("(^|\\s)" + searchClass + "(\\s|$)");
    return pattern.test(el.className);
}

MMBaseValidator.prototype.getValue = function(el) {
    if (this.isDateTime(el)) {
        return  this.getDateValue(el);
    } else {
        var value = el.value;
        if( this.isNumeric(el)) {
            value = parseFloat(value);
        }

        return el.value;
    }
}

MMBaseValidator.prototype.getDateValue = function(el) {
    if (this.hasClass(el, "mm_datetime")) {
        var year = 0;
        var month = 0;
        var day = 0;
        var hour = 0;
        var minute = 0;
        var second = 0;
        var els = el.childNodes;
        for (var  i = 0; i < els.length; i++) {
            var entry = els[i];
            if (this.hasClass(entry, "mm_datetime_year")) {
                year = entry.value;
            } else if (this.hasClass(entry, "mm_datetime_month")) {
                month = entry.value;
            } else if (this.hasClass(entry, "mm_datetime_day")) {
                day = entry.value;
            } else if (this.hasClass(entry, "mm_datetime_hour")) {
                hour = entry.value;
            } else if (this.hasClass(entry, "mm_datetime_minute")) {
                minute = entry.value;
            } else if (this.hasClass(entry, "mm_datetime_second")) {
                second = entry.value;
            }

        }
        var date = new Date(year, month - 1, day, hour , minute, second, 0);
        this.trace("date " + date);
        return date.getTime() / 1000;
    } else {
        return el.value;
    }
}

/**
 * Returns whether a form element contains a valid value. I.e. in a fast way, validation is done in
 * javascript, and therefore cannot be absolute.
 */
MMBaseValidator.prototype.valid = function(el) {
    var value = this.getValue(el);

    if (typeof(value) == 'undefined') {
        this.log("Unsupported element " + el);
        return true; // not yet supported
    }
    if (this.isBinary(el)) {
        return true; // not yet supported
    }
    if (this.isCheckEquality(el)) {
        return true; // not yet supported
    }

    if (this.isRequired(el) && this.enforce(el, el.mm_isrequired_enforce)) {
        if (value == "") {
            return false;
        }
    } else {
        if (value == "") return true;
    }
    if (! this.typeValid(el)) return false;
    if (! this.lengthValid(el)) return false;
    if (! this.minMaxValid(el)) return false;
    if (! this.patternValid(el)) return false; // not perfect yet
    // @todo of course we can go a bit further here.

    // datetime validation is still broken. (those can have more fields and so on)

    // enumerations: but must of the time those would have given dropdowns and such, so it's hardly
    // possible to enter wrongly.
    //


    return true;
}

/**
 * Determins whether a form element contains a valid value, according to the server.
 * Returns an XML containing the reasons why it would not be valid.
 * @todo make asynchronous.
 */
MMBaseValidator.prototype.serverValidation = function(el) {
    if (el == null) return;
    try {
        if (this.isBinary(el)) {
		    el.serverValidated = true;
            return $("<result valid='true' class='implicit_binary' />")[0];
        }
        if (this.isCheckEquality(el)) { // Not yet supported
		    el.serverValidated = true;
            return $("<result valid='true' class='implicit_checkequality' />")[0];
        }


        var key = this.getDataTypeKey(el);
        var value = this.getDateValue(el);

        var validationUrl = '<mm:url page="/mmbase/validation/valid.jspx" />';
            this.getDataTypeArguments(key) +
            (this.lang != null ? "&lang=" + this.lang : "") +
	        (this.sessionName != null ? "&sessionname=" + this.sessionName : "") +
            "&value=" + value +
            (key.node != null && key.node > 0 ? ("&node=" + key.node) : "") +
            "&changed=" + this.isChanged(el);
	    var params = this.getDataTypeArguments(key);
	    if (this.lang != null) params.lang = this.lang;
	    if (this.sessionName != null) params.sessionname = this.sessionName;
	    params.value = value;
	    if (key.node != null && key.node > 0) params.node = key.node;
	    params.changed = this.isChanged(el);
	    var result;
	    $.ajax({async: false, url: validationUrl, type: "GET", dataType: "xml", data: params,
	            complete: function(res, status){
		            if (status == "success") {
		                el.serverValidated = true;
		                result = res.responseXML;
		                //console.log("" + res);
		            } else {
		                el.serverValidated = true;
		                result = $("<result valid='false' />")[0];
		            }
	            }
	           });
	    return result;
    } catch (ex) {
        this.log(ex);
        throw ex;
    }
}

/**
 * The result of {@link #serverValidation} is parsed, and converted to a simple boolean
 */
MMBaseValidator.prototype.validResult = function(xml) {
    try {
        if (xml.documentElement) {
            return "true" == xml.documentElement.getAttribute("valid");
        } else {
            return "true" == "" + $(xml).attr("valid");
        }
    } catch (ex) {
        this.log(ex);
        throw ex;
    }
}

/**
 * Cross browser hack. We hate all browsers. Especially IE.
 */
MMBaseValidator.prototype.target = function(event) {
    return event.target || event.srcElement;
}
/**
 * The event handler which is linked to form elements
 * A 'validateHook' is called in this function, which you may want to set, in stead of
 * overriding this function.
 */
MMBaseValidator.prototype.validate = function(event, server) {
    this.log("event" + event + " on " + this.target(event));
    var target = this.target(event);
    if (this.hasClass(target, "mm_validate")) {
        this.validateElement(target, server);
    } else if (this.hasClass(target.parentNode, "mm_validate")) {
        this.validateElement(target.parentNode, server);
    }
}

MMBaseValidator.prototype.serverValidate = function(event) {
    this.validate(event, true);
}


MMBaseValidator.prototype.validateElement = function(element, server) {
    var valid;
    this.log("Validating " + element);
    this.activeElement = element;
    element.lastChange = new Date();
    if (server) {
        var serverXml = this.serverValidation(element);
        valid = this.validResult(serverXml);
        if (element.id) {
            var errorDiv = document.getElementById("mm_check_" + element.id.substring(3));
	        if (errorDiv != null) {
		        errorDiv.className = valid ? "mm_check_noerror mm_check_updated" : "mm_check_error mm_check_updated";
		        if (errorDiv) {
                    $(errorDiv).empty();
                    var errors = serverXml.documentElement ? serverXml.documentElement.childNodes : [];
                    this.log("errors for " + element.id + " " +  serverXml + " " + errors.length);


                    for (var  i = 0; i < errors.length; i++) {
			            var span = document.createElement("span");
			            span.innerHTML = errors[i].childNodes[0].nodeValue; // IE does not support textContent
			            errorDiv.appendChild(span);
                    }
		        }
	        }
        }
    } else {
        element.serverValidated = false;
        valid = this.valid(element);
    }
    if (valid != element.prevValid) {
        if (valid) {
            this.invalidElements--;
        } else {
            this.invalidElements++;
        }
    }
    element.prevValid = valid;
    this.setClassName(valid, element);
    if (this.validateHook) {
        this.validateHook(valid, element);
    }
}

/**
 * Validates al mm_validate form entries which were marked for validation with addValidation.
 */
MMBaseValidator.prototype.validatePage = function(server) {
    var els = this.elements;
    for (var  i = 0; i < els.length; i++) {
        var entry = els[i];
        this.validateElement(entry, server);
    }
    return this.invalidElements == 0;
}

MMBaseValidator.prototype.removeValidation = function(el) {
    if (el == null) {
        el = document.documentElement;
    }
    var self = this;
    var els = $(el).find(".mm_validate *").each(function() {
	var entry = this;
	if ($.inArray(entry, self.elements)) {
	    if (! entry.prevValid) self.invalidElements--;
	    $(entry).unbind();
	    var newElements = [];
	    $(self.elements).each(function() {
		if (this != entry) {
		    newElements.push(this);
		}
	    });
	    self.elements = newElements;
	}
    });

}


/**
 * Adds event handlers to all mm_validate form entries
 */
MMBaseValidator.prototype.addValidation = function(el) {
    if (el == null) {
        el = document.documentElement;
    }
    var els = $(el).find(".mm_validate");
    this.log("Will validate elements in " + el + " (" + els.length + " elements)");

    for (var i = 0; i < els.length; i++) {
        var entry = els[i];
        if (entry.type == "textarea") {
            entry.value = entry.value.replace(/^\s+|\s+$/g, "");
        }
	var self = this;
        // switch stolen from editwizards, not all cases are actually supported already here.
        switch(entry.type) {
        case "text":
        case "password":
        case "textarea":
            $(entry).bind("keyup",  function(ev) { self.validate(ev); });
            $(entry).bind("change", function(ev) { self.validate(ev); });
            $(entry).bind("blur",   function(ev) { self.serverValidate(ev); });
            // IE calls this when the user does a right-click paste
            $(entry).bind("paste", function(ev) { self.validate(ev); });
            // FireFox calls this when the user does a right-click paste
            $(entry).bind("input", function(ev) { self.validate(ev); });
            break;
        case "radio":
        case "checkbox":
            $(entry).bind("click", function(ev) { self.validate(ev); });
            $(entry).bind("blur",   function(ev) { self.serverValidate(ev); });
            break;
        case "select-one":
        case "select-multiple":
        default:
            this.log("Adding eventhandler to " + entry + " (" + entry.type + ")");
            this.log(entry);
            $(entry).bind("change", function(ev) { self.validate(ev); });
            $(entry).bind("blur",   function(ev) { self.serverValidate(ev); });
        }

        entry.originalValue = this.getValue(entry);
        var valid = this.valid(entry);
        entry.prevValid = valid;
        this.elements.push(entry);
        this.setClassName(this.valid(entry), entry);
        if (!valid) {
            this.invalidElements++;
        }
        if (this.validateHook) {
            this.validateHook(valid, entry);
        }

    }
    if (els.length == 0) {
        if (this.validateHook) {
            this.validateHook(this.invalidElements == 0);
        }
    }
    el = null;
}


</mm:content>
