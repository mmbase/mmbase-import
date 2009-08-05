/**
 * @author nikko
 */
var myV = new MyValidator();

function MyValidator(){
    var options = {
        validateElement: function(el, silent){
            var err = "";
            var error = "";
            var v = getValue(el);
            var dttype = el.getAttribute("dttype");
            var ftype = el.getAttribute("ftype");
            
            minlength = el.getAttribute("dtminlength");
            required = el.getAttribute("dtrequired");
            if ((!isEmpty(minlength) && "0" != minlength) || (!isEmpty(required) && (required == "true"))) {
                if (ftype == "enum" || dttype == "enum") {
                    if (el.options[el.selectedIndex].value == "-") {
                        err += getToolTipValue(form, 'message_required', "value is required; please select a value");
                    }
                    
                }
                else {
                    if (isEmpty(v)) {
                        err += getToolTipValue(form, 'message_required', "value is required");
                    }
                }
            }
            
            if (!isEmpty(v)) {
                dtpattern = el.getAttribute("dtpattern");
                ajax = el.getAttribute("hide");
                if (!isEmpty(dtpattern)) {
                    var re = new RegExp(dtpattern);
                    var number = el.getAttribute("number");
                    if (!v.match(re)) {
                        err += getToolTipValue(form, 'message_pattern', "the value {0} does not match the required pattern", v);
                    }
                    else 
                        if (!isEmpty(ajax)) {
                            var url = "../../../../editors/site/SelectSiteName.do?siteName=" + v;
                            var ajaxUrl = isNumber(number) ? (url + "&number=" + el.getAttribute("number")) : url;
                            var req = loadXMLDoc(ajaxUrl, false);                            
                            var result = req.status;
                            if (result == 200) {
                                err += writeAreaInfo(req, el, err, silent);
                            }
                            req = null;
                        }
                }
                
                // determine datatype
                if (ftype == "enum") {
                    err += validateEnum(el, form, v);
                }
                else 
                    switch (dttype) {
                        case "string":
                            err += validateString(el, form, v);
                            break;
                        case "long":
                            ;
                        case "int":
                            err += validateInt(el, form, v);
                            break;
                        case "float":
                            ;
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
    };
    Object.extend(validator, options);
}


//********************************
// AJAX VALIDATIONS STUFF
//********************************
function writeAreaInfo(req, el, err, silent){
    var v = getValue(el);
    var areaInfo = req.responseText;
    if (areaInfo != "0") {
        err += getToolTipValue(form, 'message_create', "the {0} have been created", v);
    }
    updateHtml(el, err, silent);
    return err;
}

function isNumber(s){
    return (s.search(/^[+-]?[0-9.]*$/) >= 0);
}

function loadXMLDoc(url, async) {
    var req = false;
    // branch for native XMLHttpRequest object
    if(window.XMLHttpRequest && !(window.ActiveXObject)) {
        try {
            req = new XMLHttpRequest();
        } catch(e) {
            req = false;
        }
    // branch for IE/Windows ActiveX version
    } else if(window.ActiveXObject) {
        try {
            req = new ActiveXObject("Msxml2.XMLHTTP");
        } catch(e) {
            try {
                req = new ActiveXObject("Microsoft.XMLHTTP");
            } catch(e) {
                req = false;
            }
        }
    }
    if(req) {
        var as;
	if (async == undefined) {
	    as = true;
	} else {
            as = async;
        }		
        req.open("GET", url, as);
        req.send("");
    }
	return req;
}