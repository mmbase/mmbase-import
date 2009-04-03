<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
   <title>MMBob</title>
   <script language="JavaScript1.1" type="text/javascript" src="js/smilies.js"></script>
</head>
<mm:import externid="forumid" />
<mm:import externid="postareaid" />
<mm:import externid="postingid" />
<mm:import externid="postthreadid" />
<mm:import externid="page" />

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<center>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <mm:node number="$postingid">
     <mm:import id="body"><mm:field name="body"/></mm:import>
     <mm:import id="subject"><mm:field name="subject" /></mm:import>
  </mm:node>
  <tr><th colspan="3"><di:translate key="mmbob.editmessage" /></th></tr>
  <form action="<mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid,postingid,page?"
                        />" method="post" enctype="multipart/form-data" name="posting">
    <tr><th><di:translate key="mmbob.name" /></th><td colspan="2">
        <mm:compare referid="posterid" value="-1" inverse="true">
        <mm:node number="$posterid">
          <mm:field name="account" /> (<di:person />)
        <input name="poster" type="hidden" value="<mm:field name="account" />" >
        </mm:node>
        </mm:compare>
        <mm:compare referid="posterid" value="-1">
        <input name="poster" size="32" value="gast" >
        </mm:compare>
    </td></tr>
    <tr><th width="150"><di:translate key="mmbob.subject" /></th><td colspan="2"><input name="subject" style="width: 100%" value="<mm:write referid="subject" />" ></td></th>
    <tr>
        <th valign="top"><di:translate key="mmbob.message" /><center><table><tr><th width="100"><%@ include file="includes/smilies.jsp" %></th></tr></table></center></th>
        <td colspan="2">
           <textarea name="body" rows="20" style="width: 100%"><mm:formatter xslt="xslt/posting2textarea.xslt"><mm:write referid="body" /></mm:formatter></textarea>
           <table width="100%" border="0">
              <tr><td colspan="2" style="border-width:0px"><b><di:translate key="mmbob.adddocument" /></b></td></tr>
              <mm:fieldlist nodetype="attachments" fields="title,handle">
                 <tr>
                    <td width="80" style="border-width:0px"><mm:fieldinfo type="guiname"/></td>
                    <td style="border-width:0px">
                       <mm:fieldinfo type="name">
                          <mm:compare value="handle">
                             <mm:node number="$postingid">
                                <mm:relatednodes type="attachments">
                                   <di:translate key="mmbob.download" />:
                                   <a href="<mm:attachment/>"> 
                                      <mm:field name="title" write="true">
                                         <mm:compare value="">
                                            <di:translate key="mmbob.file" />
                                         </mm:compare>
                                      </mm:field>
                                   </a>
                                </mm:relatednodes>
                             </mm:node>
                          </mm:compare>
                       </mm:fieldinfo>
                       <mm:fieldinfo type="input"/>
                    </td>
                 </tr>
              </mm:fieldlist> 
           </table>
        </td>
    </tr>
    <tr><th>&nbsp;</th><td>
    <input type="hidden" name="action" value="editpost">
    <center><input type="submit" value="<di:translate key="mmbob.commit" />">
    </form>
    </td>
    <td>
    
    <form action="<mm:url page="thread.jsp">
    <mm:param name="forumid" value="$forumid" />
    <mm:param name="postareaid" value="$postareaid" />
    <mm:param name="postthreadid" value="$postthreadid" />
    </mm:url>"
    method="post">
    <p />
    <center>
    <input type="submit" value="<di:translate key="mmbob.cancel" />">
    </form>
    </td>
    </tr>

</table>
</center>
</html>
</mm:cloud>
