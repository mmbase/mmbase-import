function addParameter(href, parameter) {
    if (href.indexOf('?') > 0) {
        return  href + '&' + parameter;
    } else {
        return  href + '?' + parameter;
    }
}

function getElementsByClass(node, searchClass, tag) {
    if (tag == null) tag ="*";
    var classElements = new Array();
    var els = node.getElementsByTagName(tag);
    var elsLen = els.length;
    var pattern = new RegExp("(^|\\s)" + searchClass + "(\\s|$)");
    for (i = 0, j = 0; i < elsLen; i++) {
        if ( pattern.test(els[i].className) ) {
            classElements[j] = els[i];
            j++;
        }
    }
    return classElements;
}