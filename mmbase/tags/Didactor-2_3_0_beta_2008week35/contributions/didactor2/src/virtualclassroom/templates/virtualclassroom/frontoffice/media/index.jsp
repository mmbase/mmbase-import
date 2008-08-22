<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>  
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/virtualclassroom/css/base.css" objectlist="$includePath" referids="$referids" />" />  
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
  <mm:node number="$user" notfound="skipbody">
    <!-- b>&nbsp;<di:translate key="virtualclassroom.sessionfiles"/></b><br/><br/-->
    <%//first we try direct relation with education  %>
    <mm:relatednodescontainer type="educations">
      <mm:constraint operator="equal" field="number" referid="education"/> 
      <mm:relatednodes>    
        <mm:import id="educationrelation" reset="true"/>
        <%@include file="presentation.jsp"%>
      </mm:relatednodes>
    </mm:relatednodescontainer>
    <mm:notpresent referid="educationrelation">
      <%//than we try relation with class if relation with education is not present, this is to avoid duplicate occurences%>
      <mm:relatednodes type="classes">
        <mm:relatednodescontainer type="educations">
          <mm:constraint operator="equal" field="number" referid="education"/> 
          <mm:relatednodes>          
            <%@include file="presentation.jsp"%>
          </mm:relatednodes>
        </mm:relatednodescontainer>
      </mm:relatednodes>
    </mm:notpresent>
  </mm:node>
</mm:cloud>
</mm:content>                 
