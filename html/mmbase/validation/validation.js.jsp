// -*- mode: java; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content type="text/javascript" expires="0">
/*

@author Michiel Meeuwissen
@version $Id: validation.js.jsp,v 1.3 2007-08-09 19:19:56 michiel Exp $
*/

var dataTypeCache   = new Object();


function isRequired(el) {
    return getDataTypeXml(getDataTypeId(el)).selectSingleNode('//dt:datatype/dt:required/@value').nodeValue;
}


function getDataTypeXml(id) {
  var dataType = dataTypeCache[id];
  if (dataType == null) {
      var xmlhttp = new XMLHttpRequest();
      xmlhttp.open("GET", '<mm:url page="/mmbase/validation/datatype.jspx?datatype=" />' + id, false);
      xmlhttp.send(null);
      dataType = xmlhttp.responseXML;
      dataType.setProperty("SelectionNamespaces", "xmlns:dt='http://www.mmbase.org/xmlns/datatypes'");
      dataType.setProperty("SelectionLanguage", "XPath");
      dataTypeCache[id] = dataType;
  }
  return dataType;
}

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

function setClassName(el, valid) {
    //console.log("Setting classname on " + el);
    if (el.originalClass == null) el.originalClass = el.className;
    el.className = el.originalClass + (valid ? " valid" : " invalid");
}

function valid(el) {
    if (isRequired(el) && el.value == "") return false;
    return true;
}

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

function validResult(xml) {
    try {
        return "true" == "" + xml.selectSingleNode('/result/@valid').nodeValue;
    } catch (ex) {
        //console.log(ex);
        throw ex;
    }
}

function validate(event) {
    var target = event.target || event.srcElement;
    setClassName(target, valid(target));
}

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
