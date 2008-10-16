<%@taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@page language="java" contentType="text/html; charset=utf-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="java.util.*,java.text.*,java.io.*,org.mmbase.bridge.*,org.mmbase.util.logging.Logger,nl.leocms.util.*,nl.leocms.util.tools.HtmlCleaner" %>
<%@include file="includes/functions.jsp" %>
<mm:import externid="language" jspvar="language" vartype="String">nl</mm:import>
<mm:cloud jspvar="cloud">
<html>
  <head>
    <title>VAN HAM</title>
    <mm:node number="home" jspvar="dummy">
      <meta name="description" content="<%= LocaleUtil.getField(dummy,"omschrijving",language, "") %>" />
      <meta name="keywords" content="<%= LocaleUtil.getField(dummy,"kortetitel",language, "") %>" />
    </mm:node>
    <style>
      body {
         margin: 0px;
         padding: 0px;
         text-align: left;
         color: #333333;
         background-color: #FFFFFF;
         font-family: Arial, Univers, sans-serif;
      }
      table { 
         border-collapse: collapse;
      }
    </style>
    <script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
    </script>
    <script type="text/javascript">
      _uacct = "UA-495341-3";
      urchinTracker();
    </script>
  </head>
<body>
  <div align="right"><a href="#" onClick="self.print()">print</a></div>
  <table cellpadding="3" cellspacing="0" border="1">
  <tr>
      <td colspan="3">
      Curriculum Vitae
      <h1>Milou van Ham</h1>
      </td>
  </tr>
  <mm:listnodes type="projecttypes">
     <%
     String subject = "";
     %>
     <tr>
         <td colspan="3" style="padding-top:20px;">
            <mm:node jspvar="dummy">
               <h2><%= LocaleUtil.getField(dummy,"name",language) %></h2>
               <% subject = LocaleUtil.getField(dummy,"subtitle",language).toLowerCase(); %>
            </mm:node>
         </td>
     </tr>
     <mm:related path="posrel,projects" orderby="projects.begindate" directions="DOWN">
        <mm:node element="projects" jspvar="thisProject">
           <mm:import id="projectID" jspvar="projectID" vartype="String" reset="true"><mm:field name="number"/></mm:import>
           <%
              String projectDesc = LocaleUtil.getField(cloud.getNode(projectID),"omschrijving", language);
           %>
           <tr>
              <% String yearString = ""; %>
              <mm:field name="begindate" jspvar="beginDate" vartype="Long">
                 <mm:field name="enddate" jspvar="endDate" vartype="Long">
                    <% Calendar cal = Calendar.getInstance();
                       long now = cal.getTimeInMillis()/1000;
                       cal.setTimeInMillis(beginDate.longValue() * 1000);
                       int beginYear = cal.get(Calendar.YEAR);
                       cal.setTimeInMillis(endDate.longValue() * 1000);
                       int endYear = cal.get(Calendar.YEAR);
                       yearString += "" + beginYear;
                       if (beginYear != endYear && endDate.longValue() <= now) {
                             yearString += " - " + endYear;
                       }
                    %>
                    <td>
                      <%= yearString %>
                    </td>
                 </mm:field>
              </mm:field>
              <td>
                 <%= LocaleUtil.getField(thisProject,"titel",language) %>
              </td>
              <td>
                 <mm:relatednodes type="organisatie" path="readmore,organisatie" jspvar="dummy">
                    <%= subject %> <%= LocaleUtil.getField(dummy,"naam",language, "") %>
                 </mm:relatednodes>
              </td>
           </tr>
           <tr>
              <td colspan="3" style="padding-bottom:10px;">
                 <%= (!HtmlCleaner.cleanText(projectDesc,"<",">","").trim().equals("") ? projectDesc : "" ) %>
                 <mm:relatednodes type="items" path="posrel,items" jspvar="dummy" max="1">
                    <mm:field name="titel_zichtbaar" jspvar="titelFlag" vartype="String">
                       <% if (!"0".equals(titelFlag) && ! LocaleUtil.getField(dummy,"titel",language).equals(LocaleUtil.getField(thisProject,"titel",language)) ) { %>
                          <%= LocaleUtil.getField(dummy,"titel",language, "<br/>") %>
                       <% } %>
                    </mm:field>
                    <mm:field name="year"><mm:compare value="<%= yearString %>" inverse="true"><mm:write /><br/></mm:compare></mm:field>
                    <%= LocaleUtil.getField(dummy,"material",language, "<br/>") %>
                    <%= LocaleUtil.getField(dummy,"subtitle",language, "<br/>") %>
                    <mm:field name="piecesize"/><br/><br/>
                    <%= LocaleUtil.getField(dummy,"omschrijving",language, "<br/>") %>
                 </mm:relatednodes>
              </td>
           </tr>
        </mm:node>
     </mm:related>
  </mm:listnodes>
  </table>
</mm:cloud>
<%@include file="includes/templatefooter.jsp" %>