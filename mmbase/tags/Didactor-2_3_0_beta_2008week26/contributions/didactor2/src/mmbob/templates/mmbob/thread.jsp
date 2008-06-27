<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
   <title>MMBob - THREAD</title>
   <script language="JavaScript1.1" type="text/javascript" src="js/smilies.js"></script>
</head>
<mm:import externid="forumid" />
<mm:import externid="postareaid" />
<mm:import externid="postthreadid" />
<mm:import externid="page">1</mm:import>
<mm:import id="showreplyform">false</mm:import>

<mm:escaper id="paramsmilies" type="smilies">
  <mm:param name="themeid">MMBaseTools</mm:param>
  <mm:param name="imagecontext" referid="imagecontext" />
</mm:escaper>

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<!-- search check -->
<mm:import externid="fromsearch">false</mm:import>
<mm:compare referid="fromsearch" value="true">
   <mm:import externid="postingid" />
   <mm:nodefunction set="mmbob" name="getPageNumber" referids="forumid,postareaid,postthreadid,postingid">
      <mm:import id="page" reset="true"><mm:field name="page" /></mm:import>
   </mm:nodefunction>
   <mm:import id="showreplyform" reset="true">true</mm:import>
</mm:compare>
<!-- end action check -->


<center>
<mm:include page="path.jsp?type=postthread" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
  <tr><th colspan="2" align="left">
    <mm:compare referid="image_logo" value="" inverse="true">
      <center><img src="<mm:write referid="image_logo" />" width="100%" ></center>
    </mm:compare>
  </th>
</tr>
</table>
<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
    <tr><td align="left"><b><di:translate key="mmbob.pages" />
          <mm:nodefunction set="mmbob" name="getPostThreadNavigation" referids="forumid,postareaid,postthreadid,page">
             (<mm:field name="pagecount" />)
             <mm:field name="navline" />
             <mm:import id="lastpage"><mm:field name="lastpage" /></mm:import>
             <mm:compare referid="showreplyform" value="false">
                <mm:import id="showreplyform" reset="true"><mm:write referid="lastpage"/></mm:import>
             </mm:compare>
          </mm:nodefunction>
      </b>
    </td></tr>
</table>



<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px;" width="95%">
   <mm:nodelistfunction set="mmbob" name="getPostings" referids="forumid,postareaid,postthreadid,posterid,page">
      <mm:first>
         <tr><th width="25%" align="left">
           <di:translate key="mmbob.poster" />
         </th>
         <th align="left">
           <di:translate key="mmbob.topic" /> : <mm:field name="subject" />
         </th>
         </tr>
      </mm:first>

      <mm:remove referid="tdvar" />

      <mm:even><mm:import id="tdvar"></mm:import></mm:even>
      <mm:odd><mm:import id="tdvar">listpaging</mm:import></mm:odd>

      <tr>
         <td class="<mm:write referid="tdvar" />" align="left">
         <mm:import id="dummyposterid" reset="true"><mm:field name="posterid"/></mm:import>
         <mm:isnotempty referid="dummyposterid">
           <mm:list nodes="$dummyposterid" path="posters,people" fields="posters.number">
             <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
               <mm:param name="contact"><mm:field name="people.number"/></mm:param>
               </mm:treefile>" target="_top">
               <b><di:person element="posters" /></b>
             </a>
           </mm:list>
         </mm:isnotempty>
         <br />
         <di:translate key="mmbob.on" /> <mm:field name="posttime"><mm:time format="${timeFormat}" /></mm:field>
       </td>
         <td class="<mm:write referid="tdvar" />" align="right">
            <mm:remove referid="postingid" />
            <mm:remove referid="toid" />
            <mm:import id="toid"><mm:field name="posterid" /></mm:import>
            <mm:import id="postingid"><mm:field name="id" /></mm:import>

            <a name="<mm:write referid="postingid"/>" />
            <%-- hh
               <a href="<mm:url page="newprivatemessage.jsp" referids="forumid,postareaid,postthreadid,postingid,toid" />"><img src="<mm:write referid="image_privatemsg" />"  border="0" /></a>
               <a href="<mm:url page="posting.jsp" referids="forumid,postareaid,postthreadid,posterid,postingid" />"><img src="<mm:write referid="image_quotemsg" />"  border="0" /></a>
            --%>
            <mm:list nodes="$postingid" path="postings,attachments">
               <mm:node element="attachments">
                  <a href="<mm:attachment/>"><img src="<mm:treefile write="true" page="/mmbob/images/download.gif" objectlist="$includePath" />" title="<mm:field name="title"/>" alt="<mm:field name="title"/>" border="0"/></a>
               </mm:node>
            </mm:list>
            <mm:field name="ismoderator">
               <mm:compare value="true">
                 <a href="<mm:url page="editpost.jsp"
                                   referids="forumid,postareaid,postthreadid,postingid,page?" />"><img src="<mm:write referid="image_medit" />"  border="0" />
                  </a>

                  <a href="<mm:url page="removepost.jsp"
                                   referids="forumid,postareaid,postthreadid,postingid,page?" />"><img src="<mm:write referid="image_mdelete" />"  border="0" />
                  </a>

               </mm:compare>
            </mm:field>
            &nbsp;
            <mm:field name="isowner">
               <mm:compare value="true">
                  <mm:remove referid="postingid" />
                  <mm:import id="postingid"><mm:field name="id" /></mm:import>
                  <a href="<mm:url page="editpost.jsp"
                                   referids="forumid,postareaid,postthreadid,postingid,page?" />"><img src="<mm:write referid="image_editmsg" />"  border="0" />
                  </a>
               </mm:compare>
            </mm:field>
         </td>
      </tr>


      <tr>
         <td class="${tdvar}" valign="top" align="left">
           <mm:field name="posterid">
             <mm:isnotempty>
               <mm:node number="${_}">
                 <mm:relatednodes type="people">
                   <mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids,_node@contact" write="false">
                     <a href="${_}" target="_top">
                       <mm:relatednodes type="images">
                         <mm:image template="s(80x80)" mode="img" />
                       </mm:relatednodes>
                     </a>
                   </mm:treefile>
                 </mm:relatednodes>
               </mm:node>
             </mm:isnotempty>
           </mm:field>
           <p /> <!-- wtf -->


           <mm:field name="guest">
             <mm:compare value="true" inverse="true">
               <di:translate key="mmbob.numberofposts" /> : <mm:field name="accountpostcount" /><br />
               <di:translate key="mmbob.membersince" /> : <mm:field name="firstlogin"><mm:time format="${timeFormat}" /></mm:field><br />
               <di:translate key="mmbob.lastvisit" /> : <mm:field name="lastseen"><mm:time format="${timeFormat}" /> </mm:field><br />
             </mm:compare>
           </mm:field>
           <br /><br /><br /><br /><br /><!-- wtf, wtf -->
         </td>


      <td class="<mm:write referid="tdvar" />" valign="top" align="left">
      <mm:field name="edittime"><mm:compare value="-1" inverse="true"><di:translate key="mmbob.lasttimemodify" /> : <mm:field name="edittime"><mm:time format="${timeFormat}" /></mm:field></mm:compare><p /></mm:field>
      <mm:node referid="postingid">
        <mm:formatter xslt="xslt/posting2xhtml.xslt" escape="paramsmilies">
          <mm:param name="wrote">Wrote:</mm:param>
          <mm:field name="body" />
        </mm:formatter>
      </mm:node>

      <br /><br /><br /><br /><br />
      </td>
      </tr>
   </mm:nodelistfunction>
</table>





<table cellpadding="0" cellspacing="0" style="margin-top : 2px;" width="95%">
    <tr><td align="left"><b><di:translate key="mmbob.pages" />
          <mm:nodefunction set="mmbob" name="getPostThreadNavigation" referids="forumid,postareaid,postthreadid,page">
            <mm:field name="navline" />
          </mm:nodefunction>
      </b>
    </td></tr>
</table>


<mm:compare referid="showreplyform" value="true">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="85%">
   <a name="reply" />
  <tr><th colspan="3"><di:translate key="mmbob.quickresponse" /></th></tr>
  <form action="<mm:url page="thread.jsp" referids="forumid,postareaid,postthreadid,page" />#reply" method="post" enctype="multipart/form-data" name="posting">
    <tr><th width="25%"><di:translate key="mmbob.name" /></th><td>

        <mm:compare referid="posterid" value="-1" inverse="true">
          <mm:node number="$posterid">
            <mm:field name="account" /> (<di:person  />)
            <input name="poster" type="hidden" value="<mm:field name="account" />" >
          </mm:node>
        </mm:compare>
        <mm:compare referid="posterid" value="-1">
          <input name="poster" style="width: 100%" value="gast"  />
        </mm:compare>

        </td></tr>
    <tr>
        <th><di:translate key="mmbob.response" /> <center><table width="100"><tr><th><%@ include file="includes/smilies.jsp" %></th></tr></table></center> </th>
        <td>
           <textarea name="body" rows="5" style="width: 100%"></textarea>
           <table width="100%" border="0">
              <tr><td colspan="2" style="border-width:0px"><b><di:translate key="mmbob.adddocument" /></b></td></tr>
              <mm:fieldlist nodetype="attachments" fields="title,handle">
                 <tr>
                    <td width="80" style="border-width:0px"><mm:fieldinfo type="guiname"/></td>
                    <td style="border-width:0px"><mm:fieldinfo type="input"/></td>
                 </tr>
              </mm:fieldlist>
           </table>
        </td>
    </tr>
    <tr><td colspan="3"><input type="hidden" name="action" value="postreply">
    <center><input type="submit" value="<di:translate key="mmbob.placeresponse" />"></center>
    </td></tr>
  </form>
</table>
</mm:compare>
<p />
<p />
<p />
<p />
</html>
</mm:cloud>
