/* <%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content type="text/javascript">
 Heartbeat
*/

function HeartBeat() {
    this.wait = ${pageContext.session.maxInactiveInterval * 500};
    var self = this;
    setTimeout(self.callBack, self.wait);
}

HeartBeat.prototype.callBack = function() {
    var self = this;
    var callBack = HeartBeat.prototype.callBack;
    $.get("${mm:link('/mmbase/searchrelate/heartbeat.jspx')}",
          function(data){
              self.wait = $(data).text() * 500;
              setTimeout(callBack, self.wait);
        });
};

var heartBeat = new HeartBeat();

/*
</mm:content>
*/
