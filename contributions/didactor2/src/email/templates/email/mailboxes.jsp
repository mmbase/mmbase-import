<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>

<mm:treeinclude page="/email/applyMailRules.jsp" objectlist="$includePath" referids="$referids"/>
<mm:import externid="mailbox">-1</mm:import>
<mm:node number="$user">
  <mm:relatednodes type="mailboxes" orderby="type, name" directions="up, up">

    <mm:field id="mboxtype" name="type" write="false" />

    <%-- show the first mailbox opened --%>
    <mm:compare referid="mailbox" value="-1">
      <mm:first>
        <mm:remove referid="mailbox"/>
        <mm:import id="mailbox"><mm:field name="number"/></mm:import>
      </mm:first>
    </mm:compare>

    <mm:compare referid="mboxtype" value="0">
      <img src="<mm:treefile page="/email/gfx/postvakin.gif" objectlist="$includePath" />" width="18" height="17" border="0" alt="" />
    </mm:compare>

    <mm:compare referid="mboxtype" value="1">
      <img src="<mm:treefile page="/email/gfx/verzondenitems.gif" objectlist="$includePath" />" width="18" height="17" border="0" alt="" />
    </mm:compare>

    <mm:compare referid="mboxtype" valueset="2">
      <img src="<mm:treefile page="/email/gfx/verwijderdeitems.gif" objectlist="$includePath" />" width="18" height="17" border="0" alt="" />
    </mm:compare>
    <mm:compare referid="mboxtype" valueset="3">
      <img src="<mm:treefile page="/email/gfx/persoonlijkemap.gif" objectlist="$includePath" />" width="18" height="17" border="0" alt="" />
    </mm:compare>

    <mm:field id="mailboxnumber" name="number" write="false" />

    <mm:remove referid="activemailbox"/>
    <mm:compare referid="mailbox" referid2="mailboxnumber">
      <mm:import id="activemailbox"><b><mm:field name="name" /></b></mm:import>
    </mm:compare>
    <mm:compare referid="mailbox" referid2="mailboxnumber" inverse="true">
      <mm:import id="activemailbox"><mm:field name="name" /></mm:import>
    </mm:compare>

    <mm:import id="newmails" reset="true">0</mm:import>
    <mm:relatednodescontainer type="emails">
        <mm:constraint field="type" value="2" operator="=" /> <%-- find new mails --%>
        <mm:import id="newmails" reset="true"><mm:size /></mm:import>
    </mm:relatednodescontainer>

    <mm:import id="mails" reset="true">0</mm:import>
    <mm:relatednodescontainer type="emails">
        <mm:import id="mails" reset="true"><mm:size /></mm:import>
    </mm:relatednodescontainer>


    <a href="<mm:treefile page="/email/index.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="mailbox"><mm:field name="number" /></mm:param>
    </mm:treefile>"><mm:write referid="activemailbox" /> (<mm:write referid="newmails"/>/<mm:write referid="mails"/>)</a> 
    
    <br />

  </mm:relatednodes>
</mm:node>
</mm:cloud>
