<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<%
// This page assumes:
// 1. mmbaseusers are related to users, users are related to deelnemers_categorie
// 2. the urls of the newsletters are of the form /index.jsp?p=page_number
// 3. the pagina which are candidates for newsletters are related to the template with alias newsteaser_template
// 4. the newsletters only contain absolute urls e.g. http://www.submarine.nl/media/spacer.gif instead of media/spacer.gif

String message = "";
String newsLetterFrom = NatMMConfig.getFromEmailAddress();
String newsLetterUri = "";
String newsLetterUrl = HttpUtils.getRequestURL(request).toString();
newsLetterUrl = newsLetterUrl.substring(0,newsLetterUrl.substring(7).indexOf("/")+7); 
String newsLetterSubject = "";

PaginaHelper ph = new PaginaHelper(cloud);
%>
<mm:import externid="newsletter">-1</mm:import>
<mm:node number="$newsletter" notfound="skipbody">
   <mm:field name="titel" jspvar="pagina_title" vartype="String" write="false">
      <% newsLetterSubject = pagina_title; %>
   </mm:field>
   <mm:field name="number" jspvar="pagina_number" vartype="String" write="false">
      <% newsLetterUri = ph.createPaginaUrl(pagina_number,request.getContextPath()); %>
   </mm:field>
</mm:node>
<mm:import externid="emailaddress" jspvar="emailaddress">you@yourprovider.com</mm:import>
<mm:listnodes type="users" constraints="<%= "[account]='" + cloud.getUser().getIdentifier() + "'" %>" max="1" id="thisuser">
  <mm:compare referid="emailaddress" value="you@yourprovider.com">
      <mm:remove referid="emailaddress" />
      <mm:import id="emailaddress" jspvar="emailaddress"><mm:field name="emailadres" /></mm:import>
  </mm:compare>
</mm:listnodes>

<mm:import externid="test">no</mm:import>
<mm:compare referid="test" value="">
    <% log.info("Sending test email to " + emailaddress + "; reading from " + newsLetterUrl + newsLetterUri ); %>
    <mm:createnode id="thismail" type="email">
       <mm:setfield name="from"><%= newsLetterFrom %></mm:setfield>
       <mm:setfield name="to"><mm:write referid="emailaddress" /></mm:setfield>
       <mm:setfield name="subject">Test: <%= newsLetterSubject %></mm:setfield>
       <mm:setfield name="body">
          <multipart id="plaintext" type="text/plain" encoding="UTF-8">
             View our newsletter at: <%= newsLetterUrl + newsLetterUri %>
          </multipart>
          <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
             <mm:include page="<%= newsLetterUrl + newsLetterUri %>" />
          </multipart>
       </mm:setfield>
    </mm:createnode>
    <mm:node referid="thismail">
       <mm:field name="mail(oneshot)" />
    </mm:node>
   <% message = "A test email has been sent to your email address."; %>
   <mm:remove referid="test" />
   <mm:import id="test">yes</mm:import>
</mm:compare>

<mm:import externid="selectgroup">no</mm:import>
<mm:compare referid="selectgroup" value="">
   <mm:remove referid="selectgroup" />
   <mm:import id="selectgroup">yes</mm:import>
</mm:compare>
<%
String selectedDeelnemers_categorie = "";
String selectedDeelnemers_categorieName = "";
boolean isFirst = true; 
%>
<mm:listnodes type="deelnemers_categorie" orderby="naam" directions="UP">
   <mm:field name="number" jspvar="deelnemers_categorie_number" vartype="String" write="false">
   <mm:field name="naam" jspvar="deelnemers_categorie_name" vartype="String" write="false">
   <mm:import externid="<%= "g" + deelnemers_categorie_number %>">no</mm:import>
   <mm:compare referid="<%= "g" + deelnemers_categorie_number %>" value="yes">
      <%
      if(!isFirst) {
         selectedDeelnemers_categorieName += ", ";
         selectedDeelnemers_categorie += ",";
      }
      selectedDeelnemers_categorieName += "\"" + deelnemers_categorie_name +  "\"";
      selectedDeelnemers_categorie +=  deelnemers_categorie_number;
      isFirst = false;
      %>
   </mm:compare>
   </mm:field>
   </mm:field>
</mm:listnodes><%

if(isFirst) { 
   %><mm:remove referid="selectgroup" /><mm:import id="selectgroup">no</mm:import><%
} %>

<mm:import externid="sendmail">no</mm:import>
<mm:compare referid="sendmail" value="">
   <mm:createnode id="thismail" type="email">
      <mm:setfield name="from"><%= newsLetterFrom %></mm:setfield>
      <mm:setfield name="subject"><%= newsLetterSubject %></mm:setfield>
   </mm:createnode>
   <mm:list nodes="<%= selectedDeelnemers_categorie %>" path="deelnemers_categorie,related,deelnemers" fields="deelnemers.email" distinct="true">
      <mm:field name="deelnemers.email" jspvar="emailaddress" vartype="String" write="false">
         <% log.info("Sending email to " + emailaddress + "; reading from " + newsLetterUrl + newsLetterUri); %>
      </mm:field>
      <mm:import id="email"><mm:field name="deelnemers.email" /></mm:import>
      <mm:import id="title">Beste <mm:field name="deelnemers.firstname" />,</mm:import>
      <mm:node referid="thismail">
         <mm:setfield name="body">
            <multipart id="plaintext" type="text/plain" encoding="UTF-8">
               View our newsletter at: <%= newsLetterUrl + newsLetterUri %>
            </multipart>
            <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
               <mm:import id="newsteaser" />
               <mm:include page="<%= newsLetterUrl + newsLetterUri %>">
                  <mm:param name="title"><mm:write referid="title"/></mm:param>
               </mm:include>
            </multipart>
         </mm:setfield>
         <mm:setfield name="to"><mm:write referid="email"/></mm:setfield>
         <mm:field name="startmail(oneshot)" />
       </mm:node>
   </mm:list>
   <% message = "The newsleter has been sent."; %>
   <mm:remove referid="newsletter" />
   <mm:import id="newsletter">-1</mm:import>
</mm:compare>

<html>
<head>
   <title>MMBase Mailer</title>
   <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css" />
   <link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
   <link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
   <style>
   input.next {   
      background: url(<mm:url page="/mmbase/style/images/next.gif" />) no-repeat right;
      border: none;
      padding-right: 20px;
   }
   input.ok {  
         background: url(<mm:url page="/mmbase/style/images/ok.gif" />) no-repeat right;
         border: none;
         padding-right: 20px;
   }
   </style>
   <script language="javascript">
   <!--
   var cancelClick = false;
   function doConfirm(prompt) {
               var conf;
               if (prompt && prompt!="") {
                  conf = confirm(prompt);
               } else conf=true;
               cancelClick=true;
               return conf;
            }
   //-->
   </script>
</head>
<body>
<form method="post" action="index.jsp">
<mm:compare referid="test" value="yes"><input type="hidden" name="test" value="yes"></mm:compare>
<mm:compare referid="selectgroup" value="yes"><input type="hidden" name="selectgroup" value="yes"></mm:compare>
<table>
   <tr>
      <th class="main" colspan="3"><div align="center">MMBase Mailer</div></th>
   </tr>
   <tr>
      <td colspan="3"><%
         if(!message.equals("")) { 
            %><div align="center"><span class="title"><%= message %></span></div><%
         } else {
            %>&nbsp;<% 
         } %>
      </td>
   </tr> 
   <tr>
      <th class="main" colspan="3">Step 1: Select a newsteaser</th>
   </tr>
   <tr>
      <td>
    <mm:node number="newsteaser_template" notfound="skipbody">
      <select name="newsletter" class="currentmenuitem" style="width:150px;">
        <option value="-1"<mm:compare referid="newsletter" value="-1"> SELECTED</mm:compare>>...
          <mm:related path="gebruikt,pagina" orderby="pagina.titel" directions="UP">
            <mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
              <option value="<%= pagina_number %>"<mm:compare referid="newsletter" value="<%= pagina_number %>"> SELECTED</mm:compare>><mm:field name="pagina.titel" />
            </mm:field>
          </mm:related>
      </select>
      <mm:import id="newsteaser_template_exists" />
    </mm:node>
    <mm:notpresent referid="newsteaser_template_exists">
      There is no pagina_template with object alias "newsteaser_template"
    </mm:notpresent>
      </td>
      <td>
         <mm:node number="$newsletter" notfound="skipbody">
            Preview: <span class="link"><a href="<%= newsLetterUrl + newsLetterUri %>" target="_blank"><mm:field name="titel" /></a></span>
         </mm:node>
      </td>
      <td><div align="right"><input class="next" type="submit" name="selectmail" value="" /></div></td>
   </tr>
<mm:compare referid="newsletter" value="-1" inverse="true">
   <tr>
      <th class="main" colspan="3">Step 2: Send a test mail to yourself</th>
   </tr>
   <tr>
      <td colspan="2">
      <input type="text" name="emailaddress" class="currentmenuitem" value="<mm:write referid="emailaddress" />" style="width:150px;text-align:left;">
      </td>
      <td><div align="right"><input class="next" type="submit" name="test" value="" /></div></td>
   </tr>
<mm:compare referid="test" value="yes">
   <tr>
      <th class="main" colspan="3">Step 3:  Select one or more groups to send the email</th>
   </tr>
   <tr>
      <td>
      <mm:listnodes type="deelnemers_categorie" orderby="naam" directions="UP">
      <mm:remove referid="has_related_deelnemers" />
      <mm:related path="related,deelnemers">
        <mm:import id="has_related_deelnemers" />
      </mm:related>
      <mm:present referid="has_related_deelnemers">
        <mm:field name="number" jspvar="deelnemers_categorie_number" vartype="String" write="false">
          <input type="checkbox" name="<%= "g" + deelnemers_categorie_number %>" value="yes" class="currentmenuitem" 
            <mm:compare referid="<%= "g" + deelnemers_categorie_number %>" value="yes"> CHECKED</mm:compare>
            ><mm:field name="externid"><mm:isnotempty><mm:write />&nbsp;-&nbsp;</mm:isnotempty></mm:field><mm:field name="naam" /><br>
        </mm:field>
      </mm:present>
      </mm:listnodes>
      </td>
      <td>
         <% if(!selectedDeelnemers_categorie.equals("")) { 
            %>View: <span class="link"><a href="<mm:url page="distrolist.jsp"><mm:param name="groups"><%= selectedDeelnemers_categorie %></mm:param></mm:url>" target="_blank">emailaddresses in <%= selectedDeelnemers_categorieName %></a></span><% 
         } %>
      </td>
      <td><div align="right"><input class="next" type="submit" name="selectgroup" value="" /></div></td>
   </tr>
<mm:compare referid="selectgroup" value="yes">
   <tr>
      <th class="main" colspan="3">Step 4: Send the newsletter</th>
   </tr>
   <tr>
      <td colspan="2">
      Click the <span style="color:#00CC33;">green</span> button<br>
      <li>to send the newsletter "<mm:node number="$newsletter"><mm:field name="titel" /></mm:node>",<br>
      <li>to <%= selectedDeelnemers_categorieName %>.
      </td>
      <td><div align="right">
         <input class="ok" type="submit" name="sendmail" value=""
            onclick="return doConfirm('Are you sure you want to send the newsletter ?');" onmousedown="cancelClick=true;" /></div></td>
   </tr>
</mm:compare>
</mm:compare>
</mm:compare>
</table>
</form>
<div class="link">
<a target="_top" href="<mm:url page=".." />"><img alt="back to MMBase editors" src="<mm:url page="/mmbase/style/images/back.gif" />" /></a>
</div>
</body>
</html>
</mm:log>
</mm:cloud>
