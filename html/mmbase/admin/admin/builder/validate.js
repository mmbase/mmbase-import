function validate() {
    var valid = true;
    var els = document.getElementsByTagName("input");
    var pattern = /\bvalidateable\b/;
    for (i = 0; i < els.length; i++) {
        if ( pattern.test(els[i].className) && els[i].style.display != "none") {
            var validateinfo = document.getElementById("validate_" + els[i].id);
            if (/\bregexp\b/.test(validateinfo.className)) {
                var regexp = new RegExp(validateinfo.innerHTML);
                if (/\binverse\b/.test(validateinfo.className)) {
                    if (regexp.test(els[i].value)) {
                        valid = false;
                        break;
                    }
                } else {
                    if (! regexp.test(els[i].value)) {
                        valid = false;
                        break;
                    }
                }
            }
        }
    }
    document.getElementById("submit").style.visibility = valid ? "visible" : "hidden";
}

