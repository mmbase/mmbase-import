<%--
  This template shows the contents of a mailbox using the <di:table> tag.
  A link is created for every email to the 'email.jsp' page, where the user
  can view the email and do other actions.

  TODO:
  - open the 'inbox' always if there is no mailbox given
  - clickthrough to the 'show the email' page
  - finish other functionality mentioned in FO
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- mm:content postprocessor="reducespace"  expires="0" --%>
<mm:cloud method="delegate">
<jsp:directive.include file="/shared/setImports.jsp" />
<mm:locale language="$language">
<mm:import externid="mailbox">-1</mm:import>
<mm:import externid="sf" />
<mm:import externid="so" />

<mm:compare referid="mailbox" value="-1">
  <mm:node number="$user">
    <mm:relatednodes id="inbox" type="mailboxes" max="1" orderby="type">
      <mm:remove referid="mailbox"/>
      <mm:import id="mailbox"><mm:field name="number"/></mm:import>
    </mm:relatednodes>
  </mm:node>
</mm:compare>

<mm:node number="$mailbox" notfound="skip">
  <mm:field name="type" id="mailboxtype" write="false" />
  <mm:relatednodescontainer type="emails">
    <mm:import id="gfx_attachment"><mm:treefile page="/email/gfx/attachment.gif" objectlist="$includePath" referids="$referids" /></mm:import>

    <hr />
    <di:table maxitems="10">
      <di:row>
        <di:headercell><input type="checkbox" onclick="selectAllClicked(this.form,this.checked)"></input></di:headercell>
        <di:headercell><img src="${gfx_attachment}" /></di:headercell>
        <di:headercell sortfield="subject"><di:translate key="email.subject" /></di:headercell>
        <%-- show recipient if sentbox --%>
        <mm:compare referid="mailboxtype" value="1">
          <di:headercell sortfield="to"><di:translate key="email.recipient" /></di:headercell>
        </mm:compare>
        <mm:compare referid="mailboxtype" value="1" inverse="true">
          <di:headercell sortfield="from"><di:translate key="email.sender" /></di:headercell>
        </mm:compare>
        <di:headercell sortfield="date" default="true"><di:translate key="email.date" /></di:headercell>
      </di:row>

      <mm:relatednodes>
        <mm:remove referid="isnew"/>
        <mm:field name="type" write="false">
          <mm:compare value="2">
            <mm:import id="isnew">true</mm:import>
          </mm:compare>
        </mm:field>
          
        <mm:compare referid="mailboxtype" value="11" inverse="true">
          <mm:treefile page="/email/mailbox/email.jsp" objectlist="$includePath" referids="$referids,mailbox?,sf?,so?,_node@email" id="link"  write="false" />
        </mm:compare>
        <mm:compare referid="mailboxtype" value="11" >
          <mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids,mailbox?,sf?,so?,_node@id" id="link" write="false" />
        </mm:compare>
        <di:row>
          <di:cell><input type="checkbox" name="ids" value="<mm:field name="number"/>"></input></di:cell>
          <di:cell>
            <mm:relatednodes type="attachments">
              <mm:first><img src="<mm:write referid="gfx_attachment"/>"/></mm:first>
            </mm:relatednodes>
          </di:cell>

          <%-- very ugly code, code duplication, only because of the <b's>. CSS class should have been used on the rows!! --%>
          <mm:present referid="isnew">
            <di:cell><a href="${link}"><b><mm:field name="subject" /></b></a></di:cell>
            <mm:compare referid="mailboxtype" value="1">
              <di:cell><a href="${link}"><b><mm:field name="to" /></b></a></di:cell>
            </mm:compare>
            <mm:compare referid="mailboxtype" value="1" inverse="true">
              <di:cell><a href="${link}"><b><mm:field name="from" /></b></a></di:cell>
            </mm:compare>
            <di:cell><a href="${link}"><b><mm:field name="date"><mm:time format=":LONG.SHORT" /></mm:field></b></a></di:cell>
          </mm:present>

          <mm:notpresent referid="isnew">
            <di:cell><a href="${link}"><mm:field name="subject" /></a></di:cell>
            <mm:compare referid="mailboxtype" value="1">
              <di:cell><a href="${link}"><mm:field name="to" /></a></di:cell>
            </mm:compare>
            <mm:compare referid="mailboxtype" value="1" inverse="true">
              <di:cell><a href="${link}"><mm:field name="from" /></a></di:cell>
            </mm:compare>
            <di:cell><a href="${link}"><mm:field name="date"><mm:time format=":LONG.SHORT" /></mm:field></a></di:cell>
          </mm:notpresent>

        </di:row>
        <mm:remove referid="link" />
      </mm:relatednodes>
    </di:table>
  </mm:relatednodescontainer>
</mm:node>
<script>
  function selectAllClicked(frm, newState) {
      if (frm.elements['ids'].length) {
          for(var count =0; count < frm.elements['ids'].length; count++ ) {
              var box = frm.elements['ids'][count];
              box.checked=newState;
          }
      } else {
          frm.elements['ids'].checked=newState;
      }
 }
</script>
</mm:locale>
</mm:cloud>
<%-- </mm:content> --%>
