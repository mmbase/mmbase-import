<%--
  This template moves a mail from one mailbox to another.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>

<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="email.moveitems" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="mailbox"/>
<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>
<mm:import externid="mailboxname"/><!-- actually a number -->
<mm:import externid="so" />
<mm:import externid="sf" />

<mm:import externid="idCount"/>


<mm:import externid="ids"/>
<mm:import id="list" jspvar="list" vartype="list"><mm:write referid="ids"/></mm:import>

<mm:node number="$mailbox" id="mymailbox"/>

<mm:import externid="submitted"/>
<mm:present referid="submitted">
<%-- Check if the back button is notpressed --%>
 <mm:notpresent referid="action2">

  <%-- Delete the content from the current mailbox --%>
  <mm:node referid="mymailbox">

    <mm:relatednodescontainer type="emails">
      <mm:constraint field="number" referid="list" operator="IN"/>
      <mm:relatednodes>
        <mm:listrelations type="mailboxes">
          <mm:deletenode/>
        </mm:listrelations>
      </mm:relatednodes>
    </mm:relatednodescontainer>

  </mm:node>

  <%-- Move the content to the new mailbox --%>
  <mm:node number="$user">
    <mm:relatednodescontainer type="mailboxes">
      <mm:constraint field="number" referid="mailboxname"/>

      <mm:relatednodes id="mynewmailbox">
        <%
           java.util.Iterator it = list.iterator();
           while ( it.hasNext() ) {
        %>
           <mm:remove referid="itemno"/>
           <mm:remove referid="mynewitems"/>
           <mm:import id="itemno"><%=it.next()%></mm:import>
           <mm:node number="$itemno" id="mynewitems"/>

           <mm:createrelation role="related" source="mynewmailbox" destination="mynewitems"/>
        <%
           }
        %>
      </mm:relatednodes>

    </mm:relatednodescontainer>
  </mm:node>

  <mm:redirect referids="$referids,mailbox,so?,sf?" page="$callerpage"/>

 </mm:notpresent>
</mm:present>


<%-- Check if the back button is pressed --%>
<mm:import id="action2text"><di:translate key="email.back" /></mm:import>
<mm:compare referid="action2" referid2="action2text">
  <mm:redirect referids="$referids,mailbox,so?,sf?" page="$callerpage"/>
</mm:compare>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_email.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" title="<di:translate key="email.email" />" alt="<di:translate key="email.email" />" />
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
    <di:translate key="email.moveselected" />
  </div>

  <div class="contentBodywit">


    <%-- Show the form --%>
    <form name="moveitems" method="post" action="<mm:treefile page="/email/moveitems.jsp" objectlist="$includePath" referids="$referids"/>">
    <input type="hidden" name="submitted" value="true">


      <table class="Font">

      <tr>
      <td>
      <di:translate key="email.movesingle" /> <mm:write referid="idCount"/> <di:translate key="email.filesto" />
      </td>
      </tr>

      <tr>
      <td>
        <mm:node number="$user">
          <mm:relatednodes type="mailboxes">
            <mm:first>
              <select name="mailboxname">
            </mm:first>

            <mm:field id="mailboxnumber" name="number" write="false" />

            <%-- Ignore the current folder of the items --%>
            <mm:compare referid="mailbox" value="$mailboxnumber" inverse="true">
              <option value="<mm:field name="number" />">
                <mm:remove referid="mboxdisplayname" />
                <mm:field name="type">
                  <mm:compare value="0">
                    <mm:import id="mboxdisplayname"><di:translate key="email.inbox" /></mm:import>
                  </mm:compare>
                  <mm:compare value="1">
                    <mm:import id="mboxdisplayname"><di:translate key="email.sent" /></mm:import>
                  </mm:compare>
                  <mm:compare value="11">
                    <mm:import id="mboxdisplayname"><di:translate key="email.drafts" /></mm:import>
                  </mm:compare>
                  <mm:compare value="2">
                    <mm:import id="mboxdisplayname"><di:translate key="email.trash" /></mm:import>
                  </mm:compare>
                  <mm:compare value="3">
                    <mm:field name="name">
                      <mm:compare value="Persoonlijke map">
                        <mm:import id="mboxdisplayname"><di:translate key="email.personal" /></mm:import>
                      </mm:compare>
                      <mm:compare value="Persoonlijke map" inverse="true">
                        <mm:import id="mboxdisplayname"><mm:field name="name" /></mm:import>
                      </mm:compare>
                    </mm:field>
                  </mm:compare>
                </mm:field>
                <mm:write referid="mboxdisplayname" />
              </option>
            </mm:compare>

            <mm:last>
              </select>
            </mm:last>

         </mm:relatednodes>
        </mm:node>
      </td>
      </tr>

      <table>

      <input type="hidden" name="ids" value="<mm:write referid="ids"/>"/>
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="mailbox" value="<mm:write referid="mailbox"/>"/>
      <mm:present referid="so">
        <input type="hidden" name="so" value="${so}" />
      </mm:present>
      <mm:present referid="sf">
        <input type="hidden" name="sf" value="${sf}" />
      </mm:present>
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="email.move" />"/>
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="email.back" />"/>
    </form>

  </div>
</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>
