<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
   <%@include file="/shared/setImports.jsp" %>
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/loginpage.css" objectlist="$includePath" referids="$referids" />" />      
    <p>
       <h1><di:translate key="core.welcome" /></h1>
    </p>
    <br />
    <p>
       <h3>Bij Didactor, de elektronische leeromgeving.</h3>
    </p>
    <br />
    <p>
       Didactor versie 2.0 Beta
    </p>
    <p>
    <mm:node number="component.portfolio" notfound="skipbody">
    <a href="<mm:treefile write="true" page="/portfolio/listall.jsp" objectlist="$includePath" />"><di:translate key="core.listallportfolios" /></a>
    </mm:node>
    </p>
</mm:cloud>
</mm:content>

						    