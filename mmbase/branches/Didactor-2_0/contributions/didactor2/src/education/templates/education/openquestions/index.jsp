<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content>
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<mm:import externid="question" required="true"/>
<mm:import externid="madetest">-1</mm:import>
<%@include file="/shared/setImports.jsp" %>
<mm:node number="$question">
   <mm:relatedcontainer path="givenanswers,madetests">
      <mm:constraint field="madetests.number" value="$madetest"/>
      <mm:related>
         <mm:node element="givenanswers">
            <mm:field name="text" id="answer" write="false"/>
         </mm:node>
      </mm:related>
   </mm:relatedcontainer>

  <mm:field name="showtitle">
    <mm:compare value="1">
      <h2><mm:field name="title"/></h2>
    </mm:compare>
  </mm:field>

  <p/>
  <mm:field name="text" escape="none"/>
  <p/>

  <mm:import id="layout"><mm:field name="layout"/></mm:import>

  <%-- Generate large input field --%>
  <mm:compare referid="layout" value="0">
    <textarea name="<mm:write referid="question"/>" cols="80" rows="5"><mm:present referid="answer"><mm:write referid="answer" escape="text/plain"/></mm:present></textarea>
    <br/>
  </mm:compare>

  <%-- Generate small input field --%>
  <mm:compare referid="layout" value="1">
    <input type="text" size="100" name="<mm:write referid="question"/>" value="<mm:present referid="answer"><mm:write referid="answer" escape="text/plain"/></mm:present>"/>
    <br/>
  </mm:compare>
  
  
</mm:node>
</mm:cloud>
</mm:content>
