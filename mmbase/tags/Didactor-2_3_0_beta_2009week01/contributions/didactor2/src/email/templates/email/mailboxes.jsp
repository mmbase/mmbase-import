<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud method="delegate" >
<%@include file="/shared/setImports.jsp"%>
<mm:locale language="$language">
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
      <%-- show mailbox Drafts --%>
      <mm:notpresent referid="draftshowed">
        <mm:import id="draftshowed">true</mm:import>
        <mm:node number="$user">
          <mm:relatednodes type="mailboxes" constraints="type=11">
            <img src="<mm:treefile page="/email/gfx/persoonlijkemap.gif" objectlist="$includePath" />" width="18" height="17" border="0" alt="" />
            <%@include file="mailboxesrow.jsp"%>
          </mm:relatednodes>
        </mm:node>
        
      </mm:notpresent>
      <img src="<mm:treefile page="/email/gfx/verzondenitems.gif" objectlist="$includePath" />" width="18" height="17" border="0" alt="" />
    </mm:compare>

    <mm:compare referid="mboxtype" valueset="2">
      <img src="<mm:treefile page="/email/gfx/verwijderdeitems.gif" objectlist="$includePath" />" width="18" height="17" border="0" alt="" />
    </mm:compare>
    <mm:compare referid="mboxtype" valueset="3">
      <img src="<mm:treefile page="/email/gfx/persoonlijkemap.gif" objectlist="$includePath" />" width="18" height="17" border="0" alt="" />
    </mm:compare>

    <mm:locale language="$language">
    <mm:compare referid="mboxtype" value="11" inverse="true">
      <%@include file="mailboxesrow.jsp"%>     
    </mm:compare>
    </mm:locale>
  </mm:relatednodes>
</mm:node>
</mm:locale>
</mm:cloud>
