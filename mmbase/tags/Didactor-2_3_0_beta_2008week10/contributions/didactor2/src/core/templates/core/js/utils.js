function addParameter(href, parameter) {
    if (href.indexOf('?') > 0) {
        return  href + '&' + parameter;
    } else {
        return  href + '?' + parameter;
    }
}
