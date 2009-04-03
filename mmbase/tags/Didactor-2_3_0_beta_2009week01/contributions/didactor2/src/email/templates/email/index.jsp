<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0" >
<di:html
    type="text/html"
    component="email"
    title_key="email.email">
  <!--
      This template shows the contents of a mailbox using the <di:table> tag.
      A link is created for every email to the 'email.jsp' page, where the user
      can view the email and do other actions.
  -->
  <mm:import externid="mailbox">-1</mm:import>
  <mm:import externid="sf" /> <!-- sort-order -->
  <mm:import externid="so" /> <!-- sort-field -->

  <!-- the hackery never stops -->
  <mm:node number="$user">
    <mm:relatednodes type="mailboxes" orderby="type" directions="up" id="mymailboxes">
      <mm:field id="mbox" name="number" write="false" />
      <mm:compare referid="mailbox" value="-1">
        <mm:first>
          <mm:remove referid="mailbox"/>
          <mm:import id="mailbox"><mm:field name="number"/></mm:import>
        </mm:first>
      </mm:compare>
      <mm:compare referid="mbox" referid2="mailbox">
        <mm:import id="mailboxname"><mm:field name="name"/></mm:import>
      </mm:compare>
    </mm:relatednodes>
  </mm:node>

  <mm:import externid="ids" vartype="list"/>
  <mm:present referid="ids">
    <mm:import externid="action_delete.x" from="parameters" id="action_delete"/>
    <mm:present referid="action_delete">
      <mm:redirect page="/email/deleteitems.jsp" referids="$referids,mailbox,so?,sf?,ids">
        <mm:param name="callerpage">/email/index.jsp</mm:param>
      </mm:redirect>
    </mm:present>

    <mm:import externid="action_move.x"  from="parameters" id="action_move"/>
    <mm:present referid="action_move">
      <mm:import id="currenttime"><mm:time>${_ * 1000}</mm:time></mm:import>
      <mm:redirect page="/email/moveitems.jsp" referids="$referids,mailbox,so?,sf?,ids,currenttime">
        <mm:param name="callerpage">/email/index.jsp</mm:param>
      </mm:redirect>
    </mm:present>
  </mm:present>



  <div class="rows">

    <div class="navigationbar">
      <div class="titlebar">
        <img src="${mm:treelink('/gfx/icon_email.gif', includePath)}" width="25" height="13" border="0"
             title="${di:translate('email.email')}"
             alt="${di:translate('email.email')}"  /> <di:translate key="email.email" />
      </div>
    </div>


    <div class="folders">
      <div class="folderHeader">
        <di:translate key="email.mailboxes" />
      </div>
      <div class="folderBody">
        <mm:treefile page="/email/createmailbox.jsp" objectlist="$includePath" referids="$referids,mailbox" write="false">
          <mm:param name="callerpage">/email/index.jsp</mm:param>
          <!-- 'maken' means 'to to make' -->
          <a href="${_}">
            <img src="${mm:treelink('/email/gfx/map maken.gif', includePath)}"
                 border="0"
                 title="${di:translate('email.createfolder')}"
                 alt="${di:translate('email.createfolder')}" />
          </a>
        </mm:treefile>
        <mm:treefile page="/email/changemailbox.jsp" objectlist="$includePath" referids="$referids,mailbox" write="false">
          <mm:param name="callerpage">/email/index.jsp</mm:param>
          <!-- 'hernoemen' means 'to rename' -->
          <a href="${_}">
            <img src="${mm:treelink('/email/gfx/map hernoemen.gif', includePath)}" border="0"
                 title="${di:translate('email.renamefolder')}"
                 alt="${di:translate('email.renamefolder')}"
                 />
          </a>
        </mm:treefile>

        <mm:treefile page="/email/deletemailbox.jsp" objectlist="$includePath" referids="$referids,mailbox" write="false">
          <mm:param name="callerpage">/email/index.jsp</mm:param>
          <a href="${_}">
            <!-- 'verwijder' means 'remove' -->
            <img src="${mm:treelink('/email/gfx/verwijder map.gif', includePath)}"
                 border="0"
                 title="${di:translate('email.deletefolder')}"
                 alt="${di:translate('email.deletefolder')}"
                 />
          </a>
        </mm:treefile>
        <br/><br/><!-- css? -->
        <di:include page="/email/mailboxes.jsp" />
      </div>
    </div>
    <mm:treefile page="/email/index.jsp" objectlist="$includePath" referids="$referids,so?,sf?" write="false">
      <form action="${_}" method="POST">
        <input type="hidden" name="mailbox" value="${mailbox}" />

        <div class="mainContent">
          <div class="contentHeader">
            <mm:present referid="mailboxname">
              <mm:node referid="mailbox">
                <jsp:directive.include file="mailboxname.jspx" />
              </mm:node>
            </mm:present>
            <mm:notpresent referid="mailboxname">
              User ${user} has no mailbox.
            </mm:notpresent>
          </div>
          <mm:present referid="mailboxname">
            <div class="contentSubHeader">
              <mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids,so?,sf?,mailboxname,mailbox" write="false">
                <a href="${_}">
                  <!-- how nice english are the gif-names. 'schrijven' means 'to write' -->
                  <img src="${mm:treelink('/gfx/icon_emailschrijven.gif', includePath)}"
                       width="50" height="28" border="0"
                       title="${di:translate('email.writenewemail')}"
                       alt="${di:translate('email.writenewemail')}" />
                </a>
              </mm:treefile>

              <!-- 'geselecteerde' means 'selected' -->
              <input type="image" src="${mm:treelink('/email/gfx/verplaats geselecteerde.gif', includePath)}"
                     border="0"
                     title="${di:translate('email.moveselected')}"
                     alt="${di:translate('email.moveselected')}"
                     name="action_move" value="move"/>

              <input type="image" src="${mm:treelink('/email/gfx/verwijder geselecteerde.gif', includePath)}"
                     border="0"
                     title="${di:translate('email.deleteselected')}"
                     alt="${di:translate('email.deleteselected')}"
                     name="action_delete" value="delete"/>
              <mm:treefile page="/email/mailrule.jsp" objectlist="$includePath" referids="$referids,so?,sf?" write="false">
                <a href="${_}">
                  <img src="${mm:treelink('/email/gfx/mail_rule.gif', includePath)}"
                       title="${di:translate('email.mailrules')}"
                       alt="${di:translate('email.mailrules')}"
                       border="0" />
                </a>
              </mm:treefile>
            </div>
            <div class="contentBody">
              <mm:treeinclude page="/email/mailbox/mailbox.jsp" objectlist="$includePath" />
            </div>
          </mm:present>
        </div>
      </form>
    </mm:treefile>
  </div>
</di:html>


</jsp:root>
