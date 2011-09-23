/**
 * validator.js
 * Routines for validating the edit wizard form
 *
 * @since    MMBase-1.6
 * @version  $Id: validator.js,v 1.43 2008-02-13 13:18:54 pierre Exp $
 * @author   Kars Veling
 * @author   Pierre van Rooden
 * @author   Michiel Meeuwissen
 * @author   Nico Klasens
 * @author   Robin van Meteren
 */

var validator = new Validator();

//constructor
function Validator() {
        this.invalidFields = new Array();
}

function start_validator() {
    //find the forms to change
    var forms = window.document.forms;
    //loop all forms
    for (var i=0; i<forms.length; i++) {
        //loop elements of this form
        for (var j=0; j<forms[i].elements.length; j++) {
            //get the element
            var el = forms[i].elements[j];
            if(requiresValidation(el)) {
                validator.attach(el);
                validator.validate(el);
            }
        }
    }
    updateButtons(validator.isValidWizard());
}

// attach events
Validator.prototype.attach = function (element) {
    if (!element) return;

    var self=this;
    switch(element.type) {
        case "text":
        case "textarea":
            addEvent(element, "keyup", function(ev) { self.validateEvent(ev) });
            addEvent(element, "change", function(ev) { self.validateEvent(ev) });
            addEvent(element, "blur", function(ev) { self.validateEvent(ev) });
            // IE calls this when the user does a right-click paste
            addEvent(element, "paste", function(ev) { self.validateIEPasteEvent(ev) });
            // FireFox calls this when the user does a right-click paste
            addEvent(element, "input", function(ev) { self.validateEvent(ev) });
            break;
        case "radio":
        case "checkbox":
            addEvent(element, "click", function(ev) { self.validateEvent(ev) });
            addEvent(element, "blur", function(ev) { self.validateEvent(ev) });
            break;
        case "select-one":
        case "select-multiple":
        default:
            addEvent(element, "change", function(ev) { self.validateEvent(ev) });
            addEvent(element, "blur", function(ev) { self.validateEvent(ev) });
    }
    // Fix for memory leak in IE and possible FF. The page garbage collector has issues with
    // circular reference detection and old objects.
    element = null;
}

addEvent = function(el, evname, func) {
    if (navigator.userAgent.toLowerCase().indexOf("msie") != -1) {
        if (navigator.appVersion.indexOf('Mac') != -1) {
            if (evname == 'blur') {
                el.onblur = func;
            }
            if (evname == 'change') {
                el.onchange = func;
            }
            if (evname == 'keyup') {
                el.onkeyup = func;
            }
            if (evname == 'click') {
                el.onclick = func;
            }
        }
        else {
                el.attachEvent("on" + evname, func);
        }
    } else {
        el.addEventListener(evname, func, true);
    }
}

Validator.prototype.validateIEPasteEvent = function (evt) {
    evt = (evt) ? evt : ((window.event) ? window.event : "")

    if (evt) {
        var elem = getTargetElement(evt)
        if (elem) {
            var self=this;
            // Internet Explorer seems to fire onpaste before the actual paste of
            // text occurs in some circumstances. The one millisecond delay is
            // enough to force Internet Explorer to run the function after the
            // actual text has been pasted.
            setTimeout(function() { self.validate(elem) }, 1);
        }
    }

}

Validator.prototype.validateEvent = function (evt) {
    evt = (evt) ? evt : ((window.event) ? window.event : "")

    if (evt) {
        var elem = getTargetElement(evt)
        if (elem) {
            this.validate(elem);
        }
    }

}

Validator.prototype.validate = function (el) {
    var element = el;
    var superId = el.getAttribute("super");
    if (superId != null) {
        var form = document.forms[0];
        element = form[superId];
    }

    var valid = this.validateElement(element);

    if (valid) {
        if (this.isInvalidField(element)) {
            this.removeInvalidField(element);
        }
    }
    else {
        if (!this.isInvalidField(element)) {
            this.addInvalidField(element);
        }
    }
    updateStep(this.isValidForm());
    updateButtons(this.isValidWizard());
}

Validator.prototype.isValidWizard = function() {
    var editform = document.forms[0];
    var otherforms = editform.getAttribute("otherforms")  == 'valid';
    var invalidlist = editform.getAttribute("invalidlist").length == 0;
    return this.isValidForm() && otherforms && invalidlist;
}

Validator.prototype.isValidForm = function() {
    if (this.invalidFields.length > 0) {
        return this.invalidFields[0] == null;
    }
    return true;
}

Validator.prototype.isInvalidField = function(element) {
    for (var i=0; i<this.invalidFields.length; i++) {
        if (this.invalidFields[i] == element.name) {
            return true
        }
    }
    return false;
}

Validator.prototype.addInvalidField = function(element) {
    for (var i=0; i<this.invalidFields.length; i++) {
        if (this.invalidFields[i] == null) {
            this.invalidFields[i] = element.name;
            return;
        }
    }
    this.invalidFields[this.invalidFields.length] = element.name;
}

Validator.prototype.removeInvalidField = function(element) {
    var removePos = 0
    for (var i=0; i<this.invalidFields.length; i++) {
        if (this.invalidFields[i] == element.name) {
            this.invalidFields[i] = null;
            removePos = i;
            break;
        }
    }
    // move last invalidfield to the position of the removed field.
    for (var i=this.invalidFields.length-1; i>removePos; i--) {
        if (this.invalidFields[i] != null) {
            this.invalidFields[removePos] = this.invalidFields[i];
            this.invalidFields[i] = null;
            break;
        }
    }
    // re-initialise arrays with only null values.
    if (this.invalidFields[0] == null) {
        this.invalidFields = new Array();
    }
}

Validator.prototype.validateElement = function (el, silent) {
    var err = "";
    var v = getValue(el);
    var dttype = el.getAttribute("dttype");
    var ftype = el.getAttribute("ftype");

    minlength = el.getAttribute("dtminlength");
    required = el.getAttribute("dtrequired");
    if ((!isEmpty(minlength) &&  "0" != minlength) || (!isEmpty(required) && (required == "true"))) {
        if (ftype=="enum" || dttype=="enum") {
            if (el.options[el.selectedIndex].value == "-") {
                err += getToolTipValue(form,'message_required',
                   "value is required; please select a value");
            }

        }
        else {
            if (isEmpty(v)) {
                err += getToolTipValue(form,'message_required',
                   "value is required");
            }
        }
    }

    if (!isEmpty(v)) {
        dtpattern = el.getAttribute("dtpattern");
        if (!isEmpty(dtpattern)) {
            var re = new RegExp(dtpattern);
            if (!v.match(re)) {
               err += getToolTipValue(form,'message_pattern', "the value {0} does not match the required pattern", v);
            }
        }

        // determine datatype
        if (ftype=="enum") {
            err += validateEnum(el, form, v);
        } else switch (dttype) {
            case "string":
                err += validateString(el, form, v);
                break;
            case "long":;
            case "int":
                err += validateInt(el, form, v);
                break;
            case "float":;
            case "double":
                err += validateFloat(el, form, v);
                break;
            case "enum":
                err += validateEnum(el, form, v);
                break;
            case "binary":
                err += validateBinary(el, form, v);
                break;
            case "datetime":
                err += validateDatetime(el, form, v);
                break;
            case "boolean":
                err += validateBoolean(el, form, v);
                break;
        }
        err += validateUnknown(el, form, v);
    }

    updateHtml(el, err, silent);
    return err.length == 0; // true == valid, false == invalid
}

//********************************
// DTTYPE VALIDATIONS STUFF
//********************************
function requiresValidation(element) {
    if(element == null) {
        return false;
    }
    var form = document.forms[0];
    var superId = element.getAttribute("super");
    if (superId != null) {
        element = form[superId];
    }

    dtpattern = element.getAttribute("dtpattern");
    if (!isEmpty(dtpattern)) {
        return true;
    }

    required = element.getAttribute("dtrequired");
    if (!isEmpty(required) && (required == "true")) {
        return true;
    }

    var validationRequired = false;

    // determine datatype
    var dttype = element.getAttribute("dttype");
    switch (dttype) {
        case "string":
            validationRequired = !isEmpty(element.getAttribute("dtminlength")) ||
                       !isEmpty(element.getAttribute("dtmaxlength"));
            break;
        case "int":;
        case "long":;
        case "float":;
        case "double":
            validationRequired = !isEmpty(element.getAttribute("dtmin")) ||
                       !isEmpty(element.getAttribute("dtmax"));
            break;
        case "datetime":
        // Validation should always happen because the hidden date field
        // will be updated when the input boxes are valid.
                validationRequired = true;
            break;
        case "enum":
            break;
        default:
            validationRequired = requiresUnknown(element, form);
            break;
    }

    return validationRequired;
}

function requiresUnknown(el, form) {
        return false;
}

//********************************
// DTTYPE VALIDATIONS STUFF
//********************************

function validateString(el, form, v) {
    minlength = el.getAttribute("dtminlength");
    if (!isEmpty(minlength) && (v.length < minlength)) {
        return getToolTipValue(form,'message_minlength', "value must be at least {0} characters", minlength);
    }
    maxlength = el.getAttribute("dtmaxlength");
    if (!isEmpty(maxlength) && (v.length > maxlength)) {
        return getToolTipValue(form,'message_maxlength', "value must be at most {0} characters", maxlength);
    }
    return "";
}

function validateInt(el, form, v) {
    required = el.getAttribute("dtrequired");
    if (!isEmpty(required) && (required == "true")) {
        if (v=="")
            return getToolTipValue(form,'message_required',
                   "value is required");
    }
    if (isNaN(v) || parseInt(v) == null) {
       return "value '" + v + "' is not a valid integer number";
    }
    else {
        minvalue = el.getAttribute("dtmin");
        if (!isEmpty(minvalue) && (parseInt(v) < minvalue))
           return getToolTipValue(form,'message_min',
                   "value must be at least {0}", minvalue);

        maxvalue = el.getAttribute("dtmax");
        if (!isEmpty(maxvalue) && (parseInt(v) > maxvalue))
            return getToolTipValue(form,'message_max',
                   "value must be at most {0}", maxvalue);
    }
    return "";
}

function validateFloat(el, form, v) {
    required = el.getAttribute("dtrequired");
    if (!isEmpty(required) && (required == "true")) {
        if (v=="")
            return getToolTipValue(form,'message_required',
                   "value is required");
    }
    if (isNaN(v) || parseFloat(v) == null) {
       return "value '" + v + "' is not a valid number";
    }
    else {
        minvalue = el.getAttribute("dtmin");
        if (!isEmpty(minvalue) && (parseFloat(v) < minvalue))
           return getToolTipValue(form,'message_min',
                   "value must be at least {0}", minvalue);

        maxvalue = el.getAttribute("dtmax");
        if (!isEmpty(maxvalue) && (parseFloat(v) > maxvalue))
            return getToolTipValue(form,'message_max',
                   "value must be at most {0}", maxvalue);
    }
    return "";
}

function validateEnum(el, form, v) {
    return "";
}

function validateBinary(el, form, v) {
    required = el.getAttribute("dtrequired");
    if (!isEmpty(required) && (required == "true")) {
        if (v=="")
            return getToolTipValue(form,'message_required',
                   "value is required; please select a value");
    }
    return "";
}

function validateDatetime(el, form, v) {
    var errormsg = "";
    var id = el.name;
    ftype = el.getAttribute("ftype");

    var month = 0;
    var day = 1;
    var year = 1970;
    var hours = 0;
    var minutes = 0;
    var seconds = 0;
    if ((ftype == "datetime") || (ftype == "date")) {
        month = form.elements["internal_" + id + "_month"].selectedIndex;
        day = form.elements["internal_" + id + "_day"].selectedIndex+1;
        if (isNaN(day)) {
          day = form.elements["internal_" + id + "_day"].value;
        } 
        year = form.elements["internal_" + id + "_year"].value;
    }
    if ((ftype == "datetime") || (ftype == "time")) {
        hours = form.elements["internal_" + id + "_hours"].selectedIndex;
        minutes = form.elements["internal_" + id + "_minutes"].selectedIndex;
        seconds = 0;
    } else if (ftype == "duration") {
        hours = form.elements["internal_" + id + "_hours"].selectedIndex;
        minutes = form.elements["internal_" + id + "_minutes"].selectedIndex;
        seconds = form.elements["internal_" + id + "_seconds"].selectedIndex;
    }

    // We don't want -1 = 2 BC, 0 = 1 BC,  -1 = 2 BC but
    //               0 -> error, -1 = 1 BC   1 = 1 AC
    if (year == 0) {
        errormsg += getToolTipValue(form,"message_dateformat", "date/time format is invalid (year may not be 0)");
    }
    if (year < 0 ) year++;

    /* Validation leap-year / february / day */
    if (LeapYear(year)) {
        leap = 1;
    } else {
        leap = 0;
    }

    if ((month < 0) || (month > 11)) {
        errormsg += getToolTipValue(form,"message_dateformat", "date/time format is invalid (wrong month)");
    }

    if (day < 1) {
        errormsg += getToolTipValue(form,"message_dateformat", "date/time format is invalid (day < 1)");
    }

    /* Validation of february */
    if (month == 1 && day > 28 + leap) {
        errormsg += getToolTipValue(form,"message_dateformat", "date/time format is invalid (february has " + 28 + leap + " days)");
    }
    /* Validation of other months */
    if ((day > 31) && ((month == 0) || (month == 2) || (month == 4) || (month == 6) || (month == 7) || (month == 9) || (month == 11))) {
        errormsg += getToolTipValue(form,"message_dateformat", "date/time format is invalid (month has 31 days)");
    }
    if ((day > 30) && ((month == 3) || (month == 5) || (month == 8) || (month == 10) )) {
        errormsg += getToolTipValue(form,"message_dateformat", "date/time format is invalid (month has 30 days)");
    }

    if (errormsg.length == 0) {
            var date = new Date();
            date.setFullYear(year);
            date.setMonth(month, day);
            if (ftype == "duration") {
                date.setUTCHours(hours, minutes, seconds, 0);
            } else {
                date.setHours(hours, minutes, seconds, 0);
            }

            var ms = date.getTime();

            /* Date is lenient which means that it accepts a wider range of values than it produces.
             * January 32 = February 1
             * This check should always fail
             */
            if (date.getDate() != day) {
                errormsg += getToolTipValue(form,"message_dateformat", "date/time format is invalid");
            } else {
                  // Validation on min and max values in milliseconds from the epoch (1 january 1970) could
                  // lead to invalid fields on the client when they are valid on the server or the other way around.
                  // The server could be in a different timezone and have a different milliseconds from the epoch with
                  // the same day, month, year values. For example a dutch client will have a difference of 3600000
                  // or 7200000 from a UTC server.
                minvalue = el.getAttribute("dtmin");
                // checks min/max. note: should use different way to determine outputformat (month)
                if ((ftype != "time") && (ftype != "duration") && (!isEmpty(minvalue)) && (ms < 1000*minvalue)) {
                    var d = new Date();
                    d.setTime(1000*minvalue);
                    errormsg += getToolTipValue(form,"message_datemin",
                           "date must be at least {0}",
                           d.getDate() + " " + (d.getMonth()+1) + " " + d.getUTCFullYear());
                }
                else {
                    maxvalue = el.getAttribute("dtmax");
                    if ((ftype != "time") && (ftype != "duration") && (!isEmpty(maxvalue)) && (ms > 1000*maxvalue)) {
                        var d = new Date();
                        d.setTime(1000*maxvalue);
                        errormsg += getToolTipValue(form,"message_datemax",
                               "date must be at most {0}",
                               d.getDate() + " " + (d.getMonth()+1) + " " + d.getUTCFullYear());
                    }
                }
            }
        }
    return errormsg;
}

function validateBoolean(el, form, v) {
    if (el.value != "1" && el.getAttribute("dtrequired") == "true") {
        return "verplicht veld";
    }
}

function validateUnknown(el, form, v) {
    return "";
}

//********************************
// UPDATE ELEMENTS STUFF
//********************************

function updateHtml(el, err, silent) {
    updatePrompt(el, err, silent);
}

function updatePrompt(el, err, silent) {
    var prompt = document.getElementById("prompt_" + el.name);
    if (prompt && !silent) {
        var orgprompt = prompt.getAttribute("prompt");
        var description = prompt.getAttribute("description");
        if (err.length > 0) {
            prompt.title = description+" \n\n"+getToolTipValue(form,"message_thisnotvalid",
                                  "This field is not valid")+":\n "+err;
            try {
                window.status = getToolTipValue(form,"message_notvalid",
                                "The {0} is not valid",orgprompt)+": "+err;
            } catch(e) {}

            prompt.className = "notvalid";
        } else {
            prompt.className = "valid";
            prompt.title = description;
            try {
                window.status = "";
            } catch(e) {}
        }
    }
}

function updateStep(valid) {
    var curform = document.forms[0].elements['curform'].value;
    var stepbut = document.getElementById("step-" + curform);
    if (valid) {
        if (stepbut) {
            stepbut.className = "valid";
            var usetext = getToolTipValue(stepbut,"titlevalid",
                              "The current form is valid.");
            stepbut.title = usetext;
        }
    } else {
        if (stepbut) {
            stepbut.className = "notvalid";
            var usetext = getToolTipValue(stepbut,"titlenotvalid",
                              "The current form is NOT valid. Correct red-marked fields and try again.");
            stepbut.title = usetext;
        }
    }
}

function updateButtons(allvalid) {
    var savebut = document.getElementById("bottombutton-save");
    var saveonlybut = document.getElementById("bottombutton-saveonly");
    if (allvalid) {
        setSaveInactive("false");
        enableButton(savebut,"titlesave", "Stores all changes.");
        if (saveonlybut != null) {
           enableButton(saveonlybut,"titlesave", "Stores all changes.");
        }
    } else {
        setSaveInactive("true");
        disableButton(savebut,"titlenosave", "You cannot save because one or more forms are invalid.");
        if (saveonlybut != null) {
           disableButton(saveonlybut,"titlenosave", "You cannot save because one or more forms are invalid.");
        }
    }
}

function enableButton(button, textAttr, textDefault) {
   if (button != null) {
        button.className = "bottombutton";
        var usetext = getToolTipValue(button,textAttr, textDefault);
        button.title = usetext;
        button.disabled = false;
   }
}

function disableButton(button, textAttr, textDefault) {
   if (button != null) {
        button.className = "bottombutton-disabled";
        var usetext = getToolTipValue(button,textAttr, textDefault);
        button.title = usetext;
        button.disabled = true;
   }
}

//********************************
// UTILITY  LIKE STUFF
//********************************

function getValue(el) {
    var tagname = el.tagName;
    if (!tagname) {
       tagname = el.nodeName;
    }
    switch (tagname) {
        case "TEXTAREA":
            return  el.value;
        case "INPUT":
            return  el.value;
        case "SELECT":
            return  el.selectedIndex;
        default:
            return  el.innerHTML;
    }
}

function setValue(el, value) {
    var tagname=el.tagName;
    if (!tagname) {
       tagname = el.nodeName;
    }
    switch (tagname.toLowerCase()) {
        case "input","textarea":
            el.value = value;
            break;
        case "select":
            el.selectedIndex = value;
            break;
        default:
            el.innerHTML = value;
            break;
    }
}

function getToolTipValue(el,attribname,defaultvalue,param) {
    var value = el.getAttribute(attribname);
    if (value==null || value=="") {
       value=defaultvalue;
    }
    if (param) {
        return value.replace(/(\{0\})/g, param);
    }
    return value;
}

function isEmpty(value) {
        return (value == null) || (value == "");
}

function getTargetElement(evt) {
    var elem
    if (evt.target) {
        elem = (evt.target.nodeType == 3) ? evt.target.parentNode : evt.target;
    } else {
        elem = evt.srcElement;
    }
    return elem;

}
