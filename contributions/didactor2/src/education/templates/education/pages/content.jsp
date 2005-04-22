<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
 
<mm:import externid="learnobject" required="true"/>

<%@include file="/shared/setImports.jsp" %>

<%-- remember this page --%>
<mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="learnobject"><mm:write referid="learnobject"/></mm:param>
    <mm:param name="learnobjecttype">pages</mm:param>
</mm:treeinclude>




<mm:node number="$learnobject">

<mm:import id="layout"><mm:field name="layout"/></mm:import>
<mm:import id="imagelayout"><mm:field name="imagelayout"/></mm:import>

<mm:import externid="suppresstitle"/>

<mm:notpresent referid="suppresstitle">
  <h2> <mm:field name="name"/></h2>
</mm:notpresent>


    <mm:import jspvar="text" reset="true"><mm:field name="text" escape="none"/></mm:import>
  

  <table width="100%" border="0" class="Font">
  
  <mm:compare referid="layout" value="0">
  <tr><td width="50%"><%@include file="/shared/cleanText.jsp"%></td></tr>
  <tr><td><%@include file="images.jsp"%></td></tr>
  </mm:compare>
  <mm:compare referid="layout" value="1">
  <tr><td  width="50%"><%@include file="images.jsp"%></td></tr>
  <tr><td><%@include file="/shared/cleanText.jsp"%></td></tr>
  </mm:compare>
  <mm:compare referid="layout" value="2">
  <tr><td><%@include file="/shared/cleanText.jsp"%></td>
      <td><%@include file="images.jsp"%></td></tr>
  </mm:compare>
  <mm:compare referid="layout" value="3">
  <tr><td><%@include file="images.jsp"%></td>
      <td><%@include file="/shared/cleanText.jsp"%></td></tr>
  </mm:compare>
 
  </table>
 
    <mm:relatednodes type="attachments" role="posrel" orderby="posrel.pos">
    <p>
      <a href="<mm:attachment/>"><mm:field name="title"/></a>
      <br/>
      <mm:field name="description" escape="p"/>
    </p>
    </mm:relatednodes>

  <div class="audiotapes">
    <mm:relatednodes type="audiotapes" role="posrel" orderby="posrel.pos">
      <mm:field name="title"/>
      <br/>
      <mm:field name="subtitle"/>
      <br/>
      <mm:field name="playtime"/>
      <br/>
      <mm:field name="intro"/>
      <br/>
      <mm:field name="body"/>
      <br/>
      <a href="<mm:field name="url" />"><mm:field name="title" /></a>
      <br/>
    </mm:relatednodes>
  </div>

  <div class="videotapes">
    <mm:relatednodes type="videotapes" role="posrel" orderby="posrel.pos">
      <mm:field name="title"/>
      <br/>
      <mm:field name="subtitle"/>
      <br/>
      <mm:field name="playtime"/>
      <br/>
      <mm:field name="intro"/>
      <br/>
      <mm:field name="body"/>
      <br/>
      <a href="<mm:field name="url" />"><mm:field name="title" /></a>
      <br/>
    </mm:relatednodes>
  </div>

  <div class="urls">
    <mm:relatednodes type="urls" role="posrel" orderby="posrel.pos">
      <a href="<mm:field name="url" />"><mm:field name="name"/></a>
      <br/>
      <mm:field name="description"/>
      <br/>
    </mm:relatednodes>
  </div>
</mm:node>
</mm:cloud>
</mm:content>
