<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%><% response.setContentType("text/javascript"); %>
<mm:import externid="fragment" required="true" />
<mm:import externid="dir"      required="true" />

function detach() {
    window.open("<mm:write value="${dir}player.jsp?fragment=$fragment" />", 
                "player", 
                "menubaryes, scrollbars=yes, locationbar=yes, status=yes, width=300, height=300, zoptions=alwaysRaise"); 

    document.getElementById("player").style.visibility = "hidden";
}
