<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%-- -*- java -*- --%>
<mm:import externid="didactor_keptalive" from="request" />
<mm:notpresent referid="didactor_keptalive">
  <script  type="text/javascript">

    function getUrl(url){
    var i = new Image();
    i.src = url;
      i = null;
      }

      function keepalive(){
      getUrl("<mm:treefile page="/shared/onlineReporter.jsp" objectlist="$includePath"
      escape="js-double-quotes" referids="$referids"
           escapeamps="${empty param.escapeamps ?  false : param.escapeamps}" />");
          setTimeout("keepalive();",1000  * 60 * 2); // keep alive every 2 minutes
      }
      addEventHandler(window, "load", keepalive);

  </script>
  <mm:write request="didactor_keptalive" value="just_to_protect_against_duplicate_import" />
</mm:notpresent>
