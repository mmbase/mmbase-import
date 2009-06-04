function initializeElement(elem, dttype, ftype) {
    if (ftype == "my_ftype") {
        elem.checked = true;
    }
}

function requiresUnknown(el, form) {
    return (el.getAttribute("ftype") == "my_ftype");
}

function validateUnknown(el, form, v) {
    if (el.getAttribute("ftype") == "my_ftype" && el.checked) {
        return "Checkbox is still checked";
    }
    return "";
}