// -*- mode: java; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content type="text/javascript"
      expires="3600">

/**
 * See test.jspx for example usage.
 *
 * @author Michiel Meeuwissen
 * @version $Id: validation.js.jsp,v 1.13 2007-08-10 15:21:56 michiel Exp $
 */

var dataTypeCache   = new Object();


/**
 * Whether the element is a 'required' form input
 */
function isRequired(el) {
    return "true" == "" + getDataTypeXml(getDataTypeId(el)).selectSingleNode('//dt:datatype/dt:required/@value').nodeValue;
}


/**
 * Whether the value in the form element obeys the restrictions on length (minLength, maxLength, length)
 */
function lengthValid(el) {

    if (! isRequired(el) && value.length == 0) return true;
    var xml = getDataTypeXml(getDataTypeId(el));

    var minLength = xml.selectSingleNode('//dt:datatype/dt:minLength');
    if (minLength != null && el.value.length < minLength.getAttribute("value")) {
        return false;
    }
    var maxLength = xml.selectSingleNode('//dt:datatype/dt:maxLength');
    if (maxLength != null && el.value.length > maxLength.getAttribute("value")) {
        return false;
    }

    var length = xml.selectSingleNode('//dt:datatype/dt:length');
    if (length != null && el.value.length != length.getAttribute("value")) {
        return false;
    }
    return true;
}

/**
 * Whether the form element represents a numeric value. There is made no difference between float,
 * double, integer and long. This means that we don't care about loss of precision only.
 */
function isNumeric(el) {
    var xml = getDataTypeXml(getDataTypeId(el));
    var javaClass = xml.selectSingleNode('//dt:datatype/dt:class');
    var name = javaClass.getAttribute("name");
    if (name == "org.mmbase.datatypes.NumberDataType") {
        return true;
    }
    var ex = javaClass.getAttribute("extends");
    var javaClasses = ex.split(",");
    for (i = 0; i < javaClasses.length; i++) {
        if (javaClasses[i] == "org.mmbase.datatypes.NumberDataType") {
            return true;
        }
    }
    //console.log("" + el + " is not numeric");
    return false;
}

/**
 * Small utility to just get the dom attribute 'value', but also parse to float, if 'numeric' is true.
 */
function getValueAttribute(numeric, el) {
    if (el == null) return null;
    var value = el.getAttribute("value");
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
function minMaxValid(el) {
    //console.log("validating : " + el);
    try {
        var xml = getDataTypeXml(getDataTypeId(el));

        var value = el.value;
        var numeric = isNumeric(el);
        if (numeric) {
            //console.log("numeric");
            value = parseFloat(value);
        }

        {
            var minInclusive = xml.selectSingleNode('//dt:datatype/dt:minInclusive');
            var compare = getValueAttribute(numeric, minInclusive);
            if (compare != null && value <  compare) {
                //console.log("" + value + " < " + compare);
                return false;
            }
        }

        {
            var minExclusive = xml.selectSingleNode('//dt:datatype/dt:minExclusive');
            var compare = getValueAttribute(numeric, minExclusive);
            if (compare != null && value <=  compare) {
                //console.log("" + value + " <= " + compare);
                return false;
            }
        }
        {
            var maxInclusive = xml.selectSingleNode('//dt:datatype/dt:maxInclusive');
            var compare = getValueAttribute(numeric, maxInclusive);
            if (compare != null && value >  compare) {
                //console.log("" + value + " > " + compare);
                return false;
            }
        }

        {
            var maxExclusive = xml.selectSingleNode('//dt:datatype/dt:maxExclusive');
            var compare = getValueAttribute(numeric, maxExclusive);
            if (compare != null && value >=  value) {
                //console.log("" + value + " >= " + compare);
                return false;
            }
        }
    } catch (ex) {
        //console.log(ex);
        throw ex;
    }
    return true;

}


/**
 * Given a certain datatype id object, this returns an XML representing it.
 * This will do a request to MMBase, unless this XML was cached already.
 * The argument is a structure which is the result of {@link #getDataTypeId}.
 */
function getDataTypeXml(id) {
  var dataType = dataTypeCache[id];
  if (dataType == null) {
      var xmlhttp = new XMLHttpRequest();
      xmlhttp.open("GET", '<mm:url page="/mmbase/validation/datatype.jspx" />' + getDataTypeArguments(id), false);
      xmlhttp.send(null);
      dataType = xmlhttp.responseXML;
      try {
          dataType.setProperty("SelectionNamespaces", "xmlns:dt='http://www.mmbase.org/xmlns/datatypes'");
          dataType.setProperty("SelectionLanguage", "XPath");
      } catch (ex) {
          // happens in safari
      }
      dataTypeCache[id] = dataType;
  }
  return dataType;
}


/**
 * All server side JSP's with which this javascript talks, can run in 2 modes. The either accept an
 * one 'datatype' parameter, or a 'field' and a 'nodemanager' parameter.
 * The result of {@link #getDataTypeId} servers as input, and returned is a query string which can
 * be appended to the servlet path.
 */
function getDataTypeArguments(id) {
    if (id.dataType != null) {
        return "?datatype=" + id.dataType;
    } else {
        return "?field=" + id.field + "&nodemanager=" + id.nodeManager;
    }
}

/**
 * Given an element, returns the associated MMBase DataType as a structutre. This structure has three fields:
 * field, nodeManager and dataType. Either dataType is null or field and nodeManager are null. They
 * are all null of the given element does not contain the necessary information to identify an
 * MMBase DataType.
 */
function getDataTypeId(el) {
    //console.log("getting datatype for " + el.className);
    var classNames = el.className.split(" ");
    var result = new Object();
    for (i = 0; i < classNames.length; i++) {
        var className = classNames[i];
        if (className.indexOf("mm_dt_") == 0) {
            result.dataType = className.substring(6);
            return result;
        } else if (className.indexOf("mm_f_") == 0) {
            result.field = className.substring(5);
        } else if (className.indexOf("mm_nm_") == 0) {
            result.nodeManager = className.substring(6);
        }
        if (result.field != null && result.nodeManager != null) {
            return result;
        }

    }
    return "field";
}

/**
 * If it was determined that a certain form element was or was not valid, this function
 * can be used to set an appropriate css class, so that this status also can be indicated to the
 * user using CSS.
 */
function setClassName(el, valid) {
    //console.log("Setting classname on " + el);
    if (el.originalClass == null) el.originalClass = el.className;
    el.className = el.originalClass + (valid ? " valid" : " invalid");
}

/**
 * Returns whether a form element contains a valid value. I.e. in a fast way, validation is done in
 * javascript, and therefore cannot be absolute.
 */
function valid(el) {
    if (isRequired(el) && el.value == "") return false;
    if (! lengthValid(el)) return false;
    if (! minMaxValid(el)) return false;

    // @todo of course we can go a bit further here.
    // regexp patterns: if the regexp syntaxes of javascript and java are sufficiently similar),

    // enumerations: but must of the time those would have given dropdowns and such, so it's hardly
    // possible to entry wrongly.
    //


    return true;
}

/**
 * Returns wether a form element contains a valid value. It is asked back to the server.
 * Returns an XML containing the reasons why it would not be valid.
 */
function serverValidation(el) {
    try {
        var id = getDataTypeId(el);
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("GET", '<mm:url page="/mmbase/validation/valid.jspx" />' + getDataTypeArguments(id) + "&value=" + el.value, false);
        xmlhttp.send(null);
        return xmlhttp.responseXML;
    } catch (ex) {
        //console.log(ex);
        throw ex;
    }
}

/**
 * The result of {@link #serverValidation} is parsed, and converted to a simple boolean
 */
function validResult(xml) {
    try {
        return "true" == "" + xml.selectSingleNode('/result/@valid').nodeValue;
    } catch (ex) {
        //console.log(ex);
        throw ex;
    }
}

/**
 * The event handler which is linked to form elements
 */
function validate(event) {
    var target = event.target || event.srcElement;
    setClassName(target, valid(target));
}

/**
 * Validates al mm_validate form entries on the page
 */
function validatePage(el) {
    var v = true;
    if (el == null) {
        el = document.documentElement;
    }
    var els = getElementsByClass(el, "mm_validate");
    for (i=0; i < els.length; i++) {
        var entry = els[i];
        //   console.log("validating " + entry);
        if (! valid(entry)) {
            v = false;
        }
        if (! validResult(serverValidation(entry)) ) {
            v = false;
        }
        //console.log("hoi " + v);
        setClassName(entry, v);

    }
    return v;
}

/**
 * Adds event handlers to all mm_validate form entries
 */
function addJavascriptValidation(el) {
    if (el == null) {
        el = document.documentElement;
    }
    var els = getElementsByClass(el, "mm_validate");
    for (i=0; i < els.length; i++) {
        var entry = els[i];
        addEventHandler(entry, "keyup", validate);
        //console.log("Will validate " + entry);
    }
}


addEventHandler(window, "load", function (event) {
        var target = event.target || event.srcElement;
        addJavascriptValidation(target);
        validatePage(target);
    });



</mm:content>
