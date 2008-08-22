<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content>
<mm:cloud method="delegate" jspvar="cloud">

  <%@include file="/shared/setImports.jsp" %>

  <mm:import externid="popcmd">no</mm:import>

  <mm:compare referid="popcmd" value="sendfeedback" inverse="true">
    <mm:redirect page="/pop/index.jsp" referids="$referids"/>
  </mm:compare>

  <mm:import externid="nfeedback"/>
  <mm:import jspvar="feedback1" externid="feedback1"/>
  <mm:import jspvar="feedback2" externid="feedback2"/>
  <mm:import jspvar="rating" externid="rating">-1</mm:import>
  <% boolean isSuccess = false; %>
  <mm:node number="$nfeedback" notfound="skip">
    <mm:field name="status">
      <mm:compare value="0">
        <% isSuccess = true; %>
        <mm:setfield name="status">-1</mm:setfield>
        <mm:setfield name="rank"><mm:write referid="feedback1"/></mm:setfield>
        <mm:setfield name="text"><mm:write referid="feedback2"/></mm:setfield>
        <mm:related path="people">
          <mm:import id="inviteefname"><mm:field name="people.firstname"/> <mm:field name="people.lastname"/></mm:import>
          <mm:import id="from"><mm:field name="people.email"/></mm:import>
        </mm:related>
        <% boolean isNoRating = true; %>
        <mm:related path="related,ratings">
          <mm:node element="related">
            <mm:maydelete>
              <mm:deletenode deleterelations="true"/>
            </mm:maydelete>
          </mm:node>
        </mm:related>
        <mm:node number="$rating" notfound="skip">
          <mm:createrelation role="related" source="nfeedback" destination="rating" />
        </mm:node>
        <mm:related path="pop,people">
          <mm:import id="userfname"><mm:field name="people.firstname"/> <mm:field name="people.lastname"/></mm:import>
          <mm:import id="to"><mm:field name="people.email"/></mm:import>
        </mm:related>
        <mm:related path="competencies">
          <mm:import id="compname"><mm:field name="competencies.name"/></mm:import>
        </mm:related>
      </mm:compare>
    </mm:field>
  </mm:node>

<% if (isSuccess) { %>

<%-- some sending email code--%>
<mm:import id="ratingmsg"></mm:import>
<mm:node number="rating" notfound="skip">
  <mm:import id="ratingmsg" reset="true"><di:translate key="pop.sendfeedbackpart6" /> <mm:field name="name"/><br/></mm:import>
</mm:node>

<mm:import id="subject"><di:translate key="pop.sendfeedbacksubject" /></mm:import>
<mm:import id="htmlbody"><HTML>
<di:translate key="pop.sendfeedbackpart1" /> <b><mm:write referid="userfname"/></b>,<br/>
<br/>
<di:translate key="pop.sendfeedbackpart2" /> <b><mm:write referid="compname"/></b> <di:translate key="pop.sendfeedbackpart3" /> <b><mm:write referid="inviteefname"/></b> <di:translate key="pop.sendfeedbackpart4" />
<br/>
<di:translate key="pop.sendfeedbackpart5" /> "<%= feedback1.replaceAll("\\n", "<br/>\n") %>"<br/>
<%= feedback2.replaceAll("\\n", "<br/>\n") %><br/>
<br/>
<mm:write referid="ratingmsg" jspvar="ratingmsg" vartype="String" write="false">
  <%= ratingmsg.replaceAll("\\n", "<br/>\n") %>
</mm:write>
<br/>
</HTML></mm:import>
<mm:import id="body">
<di:translate key="pop.sendfeedbackpart1" /> <mm:write referid="userfname"/>,

<di:translate key="pop.sendfeedbackpart2" /> <mm:write referid="compname"/> <di:translate key="pop.sendfeedbackpart3" /> <mm:write referid="inviteefname"/> <di:translate key="pop.sendfeedbackpart4" /> 
<di:translate key="pop.sendfeedbackpart5" /> "<%= feedback1 %>"
<%= feedback2 %>

<mm:write referid="ratingmsg" escape="text/plain"/>
</mm:import>
<%@include file="sendmail.jsp" %>

<mm:import id="msg"><p><di:translate key="pop.msgsendfeedbackdonepart1" /> <b><mm:write referid="compname"/></b> <di:translate key="pop.msgsendfeedbackdonepart2" /> <b><mm:write referid="userfname"/></b><di:translate key="pop.msgsendfeedbackdonepart3" /></p></mm:import>

<% } %>

  <mm:present referid="msg">
    <mm:redirect page="/pop/index.jsp" referids="$referids">
      <mm:param name="msg"><mm:write referid="msg" escape="text/plain"/></mm:param>
    </mm:redirect>
  </mm:present>

  <mm:redirect page="/pop/index.jsp" referids="$referids"/>

</mm:cloud>
</mm:content>
