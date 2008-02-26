// -*- mode: javascript; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"  %>
<mm:content type="text/javascript" expires="0">

function MMBaseSearcher(id, offset, search) {
    $("#" + id).load("${mm:link('/mmbase/taglib/page.jspx?id=')}" + id + (offset != null ? ("&offset=" + offset) : "") + (search != null ? ("&search=" + search) : "") + " div");
    //$("#" + id).load("${mm:link('/wiki/edit/a.jspx')} div");
    //console.log($("#"+id));
    return false;
}


</mm:content>
