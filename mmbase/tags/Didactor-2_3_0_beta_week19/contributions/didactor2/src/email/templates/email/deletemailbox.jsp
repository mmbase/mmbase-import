<%--
  This template deletes a existing mailbox.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="email.deletefolder" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="mailbox"/>
<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>

<%-- Check if the yes button is pressed --%>
<mm:import id="action1text"><di:translate key="email.deleteyes" /></mm:import>
<mm:compare referid="action1" referid2="action1text">

  <%-- Retrieve the mailbox, delete the node with relations and delete mailitems --%>
  <mm:node number="$mailbox">
    <mm:field name="type" id="mailboxtype" write="false"/>
    <%-- Do not delete mailbox in, sent items and deleted items --%>
    <mm:compare referid="mailboxtype" valueset="0,1,2" inverse="true">
      <mm:relatednodes type="emails">
        <mm:deletenode deleterelations="true"/>
      </mm:relatednodes>
      <mm:deletenode deleterelations="true"/>
    </mm:compare>
  </mm:node>

  <%-- Remove the reference to the mailbox --%>
  <mm:remove referid="mailbox"/>
  <mm:import id="mailbox">-1</mm:import>

  <%-- Show the previous page --%>
  <mm:redirect referids="$referids,mailbox" page="$callerpage"/>

</mm:compare>


<%-- Check if the no button is pressed --%>
<mm:import id="action2text"><di:translate key="email.deleteno" /></mm:import>
<mm:compare referid="action2" referid2="action2text">
  <mm:redirect referids="$referids,mailbox" page="$callerpage"/>
</mm:compare>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_email.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="email.email" />" alt="<di:translate key="email.email" />"/>
    <di:translate key="email.email" />
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>

<div class="mainContent">

  <div class="contentHeader">
    <di:translate key="email.deletefolder" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="deletemailboxform" method="post" action="<mm:treefile page="/email/deletemailbox.jsp" objectlist="$includePath" referids="$referids"/>">

      <di:translate key="email.deletefolderyesno" />
      <p/>

      <table class="Font">
      <mm:node referid="mailbox">
        <mm:fieldlist fields="name">
          <tr>
          <td><mm:fieldinfo type="guiname"/></td>
          <td><mm:fieldinfo type="value"/></td>
          </tr>
        </mm:fieldlist>
      </mm:node>
      </table>
      <p/>

      <input type="hidden" name="mailbox" value="<mm:write referid="mailbox"/>"/>
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="email.deleteyes" />"/>
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="email.deleteno" />"/>

    </form>

  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>
