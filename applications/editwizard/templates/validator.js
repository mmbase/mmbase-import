//constructor
function Validator() {
	//properties

	//methods
}
Validator.prototype.attach = attach_validator;
Validator.prototype.detach = detach_validator;
Validator.prototype.validate = validate_validator;
Validator.prototype.validateElement = validateElement_validator;
Validator.prototype.getValue = getValue_validator;
Validator.prototype.setValue = setValue_validator;

function attach_validator(element) {
	if (!element) return;
	this.validateElement(element);
}

function detach_validator(element) {
	if (!element) return;
	this.validateElement(element, true);
}

function validate_validator(event, el) {
	// called from html: when user pressed a key or leaves a field.
	if (!el) var el = event.srcElement || event.target;
	validateElement_validator(el);
	doValidateAndUpdateButtons();
}

function validateElement_validator(el, silent) {
	var form = document.forms[0];
	var id = el.name;
	var v = getValue_validator(el);
	var err = "";
	
	if (el.dtpattern) {
		var re = new RegExp(el.dtpattern);
		if (!v.match(re)) err += "the value '" + v + "' does not match the required pattern\n";
	}
	

	var dttype = el.getAttribute("dttype");
	if (dttype==null || dttype=="") {
		// use ftype if dttype is not given. Useful for uploads, enums, dates
		dttype = el.getAttribute("ftype");
                // this is not allowed in mozilla 0.9.8 --> Permission denied exception
		// window.status=dttype;
	}
	switch (dttype) {
		case "string":
		case "html":
		    minlength=el.getAttribute("dtminlength");
			if ((minlength!=null) && (minlength!="") && (v.length < 1*minlength))
			    err += "value must be at least " + el.getAttribute("dtminlength") + " characters\n";
		    maxlength=el.getAttribute("dtmaxlength");
			if ((maxlength!=null) && (maxlength!="") && (v.length > 1*maxlength))
    			err += "value must be at most " + el.getAttribute("dtmaxlength") + " characters\n";
			break;
		case "int":
			if (isNaN(v) || parseInt(v)==null) err += "value '" + v + "' is not a valid integer number";
			else {
				if ((el.dtmin || (el.dtmin!=null)) && (parseInt(v) < el.dtmin)) err += "value must be at least " + el.dtmin + "\n";
				if ((el.dtmax || (el.dtmax!=null)) && (parseInt(v) > el.dtmax)) err += "value must be at most " + el.dtmax + "\n";
			}
			break;
		case "date":
			break;
		case "enum":
			if ((el.getAttribute("dtrequired")!=null) && (el.getAttribute("dtrequired")=="true")) {
				if (el.options[el.selectedIndex].value == "-") err += "value is required; please select a value\n";
			}
			break;
		case "date":
		case "year":
		case "month":
		case "day":
		case "hour":
		case "minutes":
			id = el.getAttribute("name").substring(el.name.indexOf("_")+1, el.name.lastIndexOf("_"));
			var dateel = document.getElementById(id);
			
			var months = new Array("january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december");
			//alert(dateel.getAttribute("dttype"));
			if ((dateel.getAttribute("dttype") == "datetime") || (dateel.getAttribute("dttype") == "date")) {
				var month = months[form.elements["internal_" + id + "_month"].selectedIndex];
				var day = form.elements["internal_" + id + "_day"].selectedIndex+1;
				var year = form.elements["internal_" + id + "_year"].value;
			} else {
				var month = "january";
				var day = 1;   
				var year = 1970;
			}
			if ((dateel.getAttribute("dttype") == "datetime") || (dateel.getAttribute("dttype") == "time")) {
				var hours = form.elements["internal_" + id + "_hours"].value;
				var minutes = form.elements["internal_" + id + "_minutes"].value;
			} else {
				var hours = 0;
				var minutes = 0;
			}

			var ms = Date.parse(day + " " + month + " " + year + " " + hours + ":" + minutes);
			
			if (!isNaN(ms)) {
				var d = new Date();
				d.setTime(ms);
			}
			if (isNaN(ms) || (d.getDate() != day)) {
				err += "date/time format is invalid\n";
			}
			if ((err.length == 0) && (dateel.dttype != "time") && (dateel.dtmin != null) && (ms < 1000*dateel.dtmin)) {
				var d = new Date();
				d.setTime(1000*dateel.dtmin);
				err += "date must be at least " + d.getDate() + " " + months[d.getMonth()] + " " + d.getUTCFullYear() + "\n";
			}
			if ((err.length == 0) && (dateel.dttype != "time") && (dateel.dtmax != null) && (ms > 1000*dateel.dtmax)) {
				var d = new Date();
				d.setTime(1000*dateel.dtmax);
				err += "date must be at most " + d.getDate() + " " + months[d.getMonth()] + " " + d.getUTCFullYear() + "\n";
			}

			if (err.length == 0) {
				form.elements[id].value = Math.round(ms/1000); // - (60*d.getTimezoneOffset()));
//alert(form.elements[id].value + " = " + day + " " + month + " " + year + " " + hours + ":" + minutes);
			}
			break;
			
		case "upload":
		
			alert(el.outerHTML);
			break;
	}

	var prompt = document.getElementById("prompt_" + id);
	if (prompt && !silent) {
		if (err.length > 0) {
			var t = prompt.title.split("\n");
			prompt.title = t[0] + "\n\nThis field is not valid:\n" + err.substring(0, err.length-1);
                        // this is not allowed in mozilla 0.9.8 --> Permission denied exception
			// window.status = "The '" + prompt.getAttribute("prompt") + "' is not valid: " + err;
                        alert("The '" + prompt.getAttribute("prompt") + "' is not valid: " + err);
			prompt.className = "notvalid";
		} else {
			prompt.className = "valid";
			var t = prompt.title.split("\n");
			prompt.title = t[0];
                        // this is not allowed in mozilla 0.9.8 --> Permission denied exception                        
			// window.status = "";
		}
	}

	return (err.length==0); // true == valid, false == invalid
}

function doValidateForm() {
	var invalid=false;
	form = document.forms["form"];
	for (var i=0; i<form.elements.length; i++) {
		var elem = form.elements[i];
		
		//handle complex data types
		var dttype = elem.getAttribute("dttype");
		invalid = invalid || (!validator.validateElement(elem, true));
	}
	return (!invalid);
}

function doValidateAndUpdateButtons() {
	// check if current form is valid.
	var valid = doValidateForm();
	var curform = document.forms[0].elements[0].value;
	var savebut=document.getElementById("bottombutton-save");
	var stepbut=document.getElementById("bottombutton-step-"+curform);
	var otherforms = savebut.getAttribute("otherforms");
	var allvalid = valid && otherforms=='valid';
	
	if (valid) {
		if (stepbut) {
			stepbut.className = "currentstepicon-valid";
			stepbut.title = "The current form is valid now.";
		}
	} else {
		if (stepbut) {
			stepbut.className = "currentstepicon";
			stepbut.title = "The current form is NOT valid. Correct red-marked fields and try again.";
		}
	}
	
	if (allvalid) {
		savebut.className = "bottombutton";
		savebut.title = "Store all changes.";
	} else {
		savebut.className = "bottombutton-disabled";
		savebut.title = "You cannot save because one or more forms are invalid. Please correct the errors and press Save.";
	}
	
	return allvalid;
}

function getValue_validator(el) {
	var tagname=el.tagName;
	if (!tagname) tagname = el.nodeName;
	var v;
	switch (tagname.toLowerCase()) {
		case "input":
			v = el.value;
			break;
		case "select":
			v = el.selectedIndex;
			break;
		case "textarea":
			v = el.value;
			break;
		default:
			v = el.innerHTML;
			break;
	}
	return v;
}

function setValue_validator(el, value) {
	var tagname=el.tagName;
	if (!tagname) tagname = el.nodeName;
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

