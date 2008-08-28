function addParameter(href, parameter) {
    if (href.indexOf('?') > 0) {
        return  href + '&' + parameter;
    } else {
        return  href + '?' + parameter;
    }
}

function printThis() {
    window.print();
}


function focusFirstForm() {
    var form = document.forms[0];
    if (form != null) {
        for (var i=0; i < form.elements.length; i++) {
            var elem = form.elements[i];
            // find first editable field
            var hidden = elem.getAttribute("type"); //.toLowerCase();
            if (hidden != "hidden") {
                elem.focus();
                break;
            }
        }
    }
}

function check_passwords(loginId, passwordId) {
    if (loginId == null) loginId = "loginUsername";
    if (passwordId == null) passwordId = "loginPassword";
    return document.getElementById(loginId).value.length > 0 &&
	document.getElementById(passwordId).value.length > 0;
}

