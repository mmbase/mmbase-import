<%--
  This template shows a chatlog. In the left column, a list of all chatlogs
  is shown (they are related to the current class). The selected chatlog
  is shown in the right column.

--%>

<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="chatlog">-1</mm:import>
<%@include file="/shared/setImports.jsp" %>


<%@ page import="java.text.SimpleDateFormat,
                 java.text.ParseException,
                 java.util.Date,
                 java.util.Calendar"%>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="chat.chatlogtitle" /></title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_chatlog.gif" objectlist="$includePath" />" width="25" height="13" border="0" title="chatverslag" alt="chatverslag" /> <di:translate key="chat.chatlogtitle" />
  </div>
</div>

<div class="folders">

  <div class="folderHeader">
    <di:translate key="chat.chatlogtitlelowercase" />
  </div>

  <div class="folderBody">
    
    <mm:node number="$class" notfound="skip">
        <mm:relatednodes type="chatchannels">
	    Chatlogs van <mm:field name="name"/><br>
	    <%@include file="relatedchatloglist.jsp"%>
	</mm:relatednodes>
    </mm:node>
    
    <mm:node number="$user">
	Prive chatlogs<br>
	<%@include file="relatedchatloglist.jsp"%>
    </mm:node>

  </div>

</div>

<div class="mainContent">

  <div class="contentHeader">
    <mm:node number="$chatlog" notfound="skip">
      <mm:remove referid="tempday"/>
      <mm:import id="tempday" jspvar="tempDay"><mm:field name="date"/></mm:import>
		  <%
		     SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		     Date d = null;
		     long temp1 = 0;
		     try {
		     			d = fmt.parse( tempDay );
		     			temp1 = d.getTime() / 1000;
		     		} catch (ParseException e) {
		     			e.printStackTrace();
		  		}
          %>
      <mm:remove referid="date"/>
      <mm:import id="date"><%=temp1%></mm:import>
      <mm:write referid="date"><mm:time format="dd/MM/yyyy"/></mm:write>
    </mm:node>
  </div>

  <div class="contentSubHeader">
    <mm:node number="component.workspace" notfound="skip">
    <a href="<mm:treefile page="/workspace/addchatlog.jsp" objectlist="$includePath" referids="$referids">
	  	            <mm:param name="currentchatlog"><mm:write referid="chatlog"/></mm:param>
		            <mm:param name="callerpage">/chat/chatlog.jsp</mm:param>
		            <mm:param name="typeof">1</mm:param>
		     </mm:treefile>">
      <img src="<mm:treefile page="/chat/gfx/mydocs.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="chat.addchatlogpersonal" />" alt="<di:translate key="chat.addchatlogpersonal" />"/></a>
    <a href="<mm:treefile page="/workspace/addchatlog.jsp" objectlist="$includePath" referids="$referids">
 	  	            <mm:param name="currentchatlog"><mm:write referid="chatlog"/></mm:param>
  		            <mm:param name="callerpage">/chat/chatlog.jsp</mm:param>
  		            <mm:param name="typeof">2</mm:param>
  		     </mm:treefile>">
      <img src="<mm:treefile page="/chat/gfx/shareddocs.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="chat.addchatlogpublic" />" alt="<di:translate key="chat.addchatlogpublic" />"/></a>
    </mm:node>
  </div>

  <div class="contentBodywit">
    <mm:node number="$chatlog" notfound="skip">
            <mm:field name="text" escape="p" />
    </mm:node>
  </div>

</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>
