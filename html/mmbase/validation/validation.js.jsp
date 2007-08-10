// -*- mode: java; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content type="text/javascript"
      expires="3600">

/**
 * See test.jspx for example usage.

 * new MMBaseValidator(window, root): attaches events to all elements in root when loading window.
 * new MMBaseValidator(window): attaches events to all elements in window when loading window.
 * new MMBaseValidator():       attaches no events yet. You could replace some function first or so.
 *
 * @author Michiel Meeuwissen
 * @version $Id: validation.js.jsp,v 1.20 2007-08-10 21:35:16 michiel Exp $
 */


function MMBaseValidator(w, root) {

   /**
    * Whether the element is a 'required' form input
    */
   this.isRequired = function(el) {
       return "true" == "" + this.getDataTypeXml(el).selectSingleNode('//dt:datatype/dt:required/@value').nodeValue;
   }

   this.logEnabled = false;

   this.log = function (msg) {
       if (this.logEnabled) {
           // firebug console"
           console.log(msg);
       }
   }

   /**
    * Whether the value in the form element obeys the restrictions on length (minLength, maxLength, length)
    */
   this.lengthValid = function(el) {
       if (! this.isRequired(el) && el.value.length == 0) return true;
       var xml = this.getDataTypeXml(el);

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

   this.hasJavaClass = function(el, javaClass) {
       var pattern = new RegExp(javaClass);
       var xml = this.getDataTypeXml(el);
       var javaClassElement = xml.selectSingleNode('//dt:datatype/dt:class');
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
   this.isNumeric = function(el) {
       return this.hasJavaClass(el, "org\.mmbase\.datatypes\.NumberDataType");
   }
   this.isInteger = function(el) {
       return this.hasJavaClass(el, "(org\.mmbase\.datatypes\.IntegerDataType|org\.mmbase\.datatypes\.LongDataType)");
   }
   this.isFloat = function(el) {
       return this.hasJavaClass(el, "(org\.mmbase\.datatypes\.FloatDataType|org\.mmbase\.datatypes\.DoubleDataType)");
   }

   this.typeValid = function(el) {
       if (el.value == "") return true;

       if (this.isInteger(el)) {
           if (! /^[+-]?\d+$/.test(el.value)) return false;
       }
       if (this.isFloat(el)) {
           if (! /^[+-]?\d+(\.\d*|)(e[+-]?\d+|)$/.test(el.value)) return false;
       }
       return true;

   }

   /**
    * Small utility to just get the dom attribute 'value', but also parse to float, if 'numeric' is true.
    */
   this.getValueAttribute = function(numeric, el) {
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
   this.minMaxValid  = function(el) {
       this.log("validating : " + el);
       try {
           var xml = this.getDataTypeXml(el);

           var value = el.value;
           var numeric = this.isNumeric(el);
           if (numeric) {
               value = parseFloat(value);
           }

           {
               var minInclusive = xml.selectSingleNode('//dt:datatype/dt:minInclusive');
               var compare = this.getValueAttribute(numeric, minInclusive);
               if (compare != null && value <  compare) {
                   this.log("" + value + " < " + compare);
                   return false;
               }
           }

           {
               var minExclusive = xml.selectSingleNode('//dt:datatype/dt:minExclusive');
               var compare = this.getValueAttribute(numeric, minExclusive);
               if (compare != null && value <=  compare) {
                   this.log("" + value + " <= " + compare);
                   return false;
               }
           }
           {
               var maxInclusive = xml.selectSingleNode('//dt:datatype/dt:maxInclusive');
               var compare = this.getValueAttribute(numeric, maxInclusive);
               if (compare != null && value >  compare) {
                   this.log("" + value + " > " + compare);
                   return false;
               }
           }

           {
               var maxExclusive = xml.selectSingleNode('//dt:datatype/dt:maxExclusive');
               var compare = this.getValueAttribute(numeric, maxExclusive);
               if (compare != null && value >=  value) {
                   this.log("" + value + " >= " + compare);
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
    * Given a certain datatype element, this returns an XML representing its datatyp.
    * This will do a request to MMBase, unless this XML was cached already.
    */
   this.getDataTypeXml = function(el) {
       var dataType = el.dataType;
       if (dataType == null) {
           var id = this.getDataTypeId(el);
           var xmlhttp = new XMLHttpRequest();
           xmlhttp.open("GET", '<mm:url page="/mmbase/validation/datatype.jspx" />' + this.getDataTypeArguments(id), false);
           xmlhttp.send(null);
           dataType = xmlhttp.responseXML;
           try {
               dataType.setProperty("SelectionNamespaces", "xmlns:dt='http://www.mmbase.org/xmlns/datatypes'");
               dataType.setProperty("SelectionLanguage", "XPath");
           } catch (ex) {
               // happens in safari
           }
           el.dataType = dataType;
       }
       return dataType;
   }


   /**
    * All server side JSP's with which this javascript talks, can run in 2 modes. The either accept an
    * one 'datatype' parameter, or a 'field' and a 'nodemanager' parameter.
    * The result of {@link #getDataTypeId} servers as input, and returned is a query string which can
    * be appended to the servlet path.
    */
   this.getDataTypeArguments = function(id) {
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
   this.getDataTypeId = function(el) {
       if (el.dataTypeStructure == null) {
           this.log("getting datatype for " + el.className);
           var classNames = el.className.split(" ");
           var result = new Object();
           for (i = 0; i < classNames.length; i++) {
               var className = classNames[i];
               if (className.indexOf("mm_dt_") == 0) {
                   result.dataType = className.substring(6);
                   break;
               } else if (className.indexOf("mm_f_") == 0) {
                   result.field = className.substring(5);
               } else if (className.indexOf("mm_nm_") == 0) {
                   result.nodeManager = className.substring(6);
               }
               if (result.field != null && result.nodeManager != null) {
                   break;
               }

           }
           el.dataTypeStructure = result;
       }
       return el.dataTypeStructure;
   }

   /**
    * If it was determined that a certain form element was or was not valid, this function
    * can be used to set an appropriate css class, so that this status also can be indicated to the
    * user using CSS.
    */
   this.setClassName = function(el, valid) {
       this.log("Setting classname on " + el);
       if (el.originalClass == null) el.originalClass = el.className;
       el.className = el.originalClass + (valid ? " valid" : " invalid");
   }

   /**
    * Returns whether a form element contains a valid value. I.e. in a fast way, validation is done in
    * javascript, and therefore cannot be absolute.
    */
   this.valid = function(el) {
       if (this.isRequired(el) && el.value == "") return false;
       if (! this.typeValid(el)) return false;
       if (! this.lengthValid(el)) return false;
       if (! this.minMaxValid(el)) return false;

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
   this.serverValidation = function(el) {
       try {
           var id = this.getDataTypeId(el);
           var xmlhttp = new XMLHttpRequest();
           xmlhttp.open("GET", '<mm:url page="/mmbase/validation/valid.jspx" />' + this.getDataTypeArguments(id) + "&value=" + el.value, false);
           xmlhttp.send(null);
           return xmlhttp.responseXML;
       } catch (ex) {
           this.log(ex);
           throw ex;
       }
       }

   /**
    * The result of {@link #serverValidation} is parsed, and converted to a simple boolean
    */
   this.validResult = function(xml) {
       try {
           return "true" == "" + xml.selectSingleNode('/result/@valid').nodeValue;
       } catch (ex) {
           this.log(ex);
           throw ex;
       }
   }

   /**
    * Cross browser hack.
    */
   this.target = function(event) {
       return event.target || event.srcElement;
   }
   /**
    * The event handler which is linked to form elements
    * You may want to override this function or {@link #setClassName}.
    */
   this.validate = function(event) {
       var element = this.target(event);
       this.setClassName(element, this.valid(element));
   }

   /**
    * Validates al mm_validate form entries on the page
    */
   this.validatePage = function(server, el) {
       var v = true;
       if (el == null) {
           el = document.documentElement;
       }
       var els = getElementsByClass(el, "mm_validate");
       for (var  i = 0; i < els.length; i++) {
           var entry = els[i];
           if (server) {
               if (! this.validResult(this.serverValidation(entry))) {
                   v = false;
               }
           } else {
               if (! this.valid(entry)) {
                   v = false;
               }
           }
           this.setClassName(entry, v);
       }
       return v;
   }

   /**
    * Adds event handlers to all mm_validate form entries
    */
   this.addJavascriptValidation = function(el) {
       if (el == null) {
           el = document.documentElement;
       }
       this.log("Will validate " + el);

       var els = getElementsByClass(el, "mm_validate");
       for (i=0; i < els.length; i++) {
           var entry = els[i];
           if (entry.tagName.toUpperCase() == "TEXTAREA") {
               entry.value = entry.value.replace(/^\s+|\s+$/g, "");
           }
           addEventHandler(entry, "keyup", this.validate, this);
           this.setClassName(entry, this.valid(entry));

       }
   }
   this.onLoad = function(event) {
       if (this.root == null) {
           this.root = event.target || event.srcElement;
       }

       this.addJavascriptValidation(this.root);
       //validatePage(target);
   }


   this.setup = function(w) {

       if (w != null) {
           addEventHandler(w, "load", this.onLoad, this);
       }
   }
   this.setup(w);
   this.root = root;

  }

</mm:content>
