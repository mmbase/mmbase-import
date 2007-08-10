// -*- mode: java; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content type="text/javascript"
      expires="3600">

/**
 * See test.jspx for example usage.
 *
 * @author Michiel Meeuwissen
 * @version $Id: validation.js.jsp,v 1.7 2007-08-10 10:12:30 michiel Exp $
 */

var dataTypeCache   = new Object();


/**
 * Whether the element is a 'required' form input
 */
function isRequired(el) {
    return "true" == "" + getDataTypeXml(getDataTypeId(el)).selectSingleNode('//dt:datatype/dt:required/@value').nodeValue;
}

function minLength(el) {

}




/**
 * Given a certain MMBase datatype id, this returns an XML representing it.
 * This will do a request to MMBase, unless this XML was cached already.
 */
function getDataTypeXml(id) {
  var dataType = dataTypeCache[id];
  if (dataType == null) {
      var xmlhttp = new XMLHttpRequest();
      xmlhttp.open("GET", '<mm:url page="/mmbase/validation/datatype.jspx?datatype=" />' + id, false);
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
 * Given an element, returns the associated MMBase DataType (as an XML)
 */
function getDataTypeId(el) {
    //console.log("getting datatype for " + el.className);
    var classNames = el.className.split(" ");
    for (i = 0; i < classNames.length; i++) {
        var className = classNames[i];
        if (className.indexOf("mm_f_") == 0) {
            return className.substring(5);
        }
    }
    return "field";
}

/**
 * If it was determined that a certain fomr element was or was not valid, this function
 * can be used to set an appropriate css class, so that this status also can be indicated to the
 * user using CSS.
 */
function setClassName(el, valid) {
    //console.log("Setting classname on " + el);
    if (el.originalClass == null) el.originalClass = el.className;
    el.className = el.originalClass + (valid ? " valid" : " invalid");
}

/**
 * Returns wether a form element contains a valid value. I.e. in a fast way, validation is done in
 * javascript, and therefore cannot be absolute.
 */
function valid(el) {
    if (isRequired(el) && el.value == "") return false;

    // @todo of course we can go a bit further here.

    // min/max length: very simple
    // minimum/maximum: very simple
    // regexp patterns: if the regexp syntaxes of javascript and java are sufficiently similar),

    // enumerations: but must of the time those would have given dropdowns and such, so it's hardly
    // possible to entry wrongly.
    //


    return true;
}

/**
 * Returns wether a form element contains a valid value. It is asked back to the server.
 * Returns an XML containing the reasons which it would not be valid.
 */
function serverValidation(el) {
    try {
        var id = getDataTypeId(el);
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("GET", '<mm:url page="/mmbase/validation/valid.jspx?datatype=" />' + id + "&value=" + el.value, false);
        xmlhttp.send(null);
        return xmlhttp.responseXML;
    } catch (ex) {
        //console.log(ex);
        throw ex;
    }
}

/**
 * The result of {@link #serverValidation} is parsed, and converted in a simple boolean
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
