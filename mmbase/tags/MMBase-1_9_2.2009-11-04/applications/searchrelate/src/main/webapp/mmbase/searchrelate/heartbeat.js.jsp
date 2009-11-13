// -*- mode: javascript; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"  %>
<mm:content type="text/javascript">
function HeartBeat() {
    this.wait = ${pageContext.session.maxInactiveInterval * 500};
    var self = this;
    setTimeout(function() { self.function() }, this.wait);
}
HeartBeat.prototype.function = function () {
    var self = this;
    $.get("${mm:link('/mmbase/searchrelate/heartbeat.jspx')}", function(data){
            self.wait = $(data).text() * 500;
            setTimeout(function() { self.function() }, self.wait);
        });};
var heartBeat = new HeartBeat();
</mm:content>
