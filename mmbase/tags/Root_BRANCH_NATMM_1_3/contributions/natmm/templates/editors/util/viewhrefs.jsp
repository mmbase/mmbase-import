<%@include file="/taglibs.jsp" %>
<%@page import="java.util.*" %>
<%! public String viewHrefs(String title, String number, String object, String field, String text) {
    // find hrefs and see if they are external or internal
    String hrefs = "";
    String textUC = text.toUpperCase();
    int aPosOpen = textUC.indexOf("<A ");
    while(aPosOpen>-1) {
        int aPosClose = textUC.indexOf("</A>",aPosOpen);
        if(aPosClose>-1) {
            if(hrefs.equals("")) {
               hrefs = "<br><br><li><a href=\"/mmbase/edit/wizard/jsp/wizard.jsp?wizard=config/" 
                  + object + "/" + object 
                  + "&nodepath=" + object
                  + "&objectnumber=" + number 
                  + "&amp;referrer=%2Feditors%2Futil%2Fviewhrefs.jsp\">" 
                  + number + "." + object + ": " + title + " field: " + field + "</a>";
            }
            hrefs += "<br>&lt;" 
               + text.substring(aPosOpen+1,aPosClose).replace('?','@').replaceAll(".asp@","<span style='color:red'>.asp?</span>") 
               + "&lt/A&gt;: " + text.substring(aPosOpen,aPosClose) + "</A>";
        }
        aPosOpen = textUC.indexOf("<A ",aPosClose);
    }
    return hrefs;
}
%><mm:cloud>

<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<style>
a:link {                            text-decoration: none; COLOR: #0011FF; }
a:visited {                         text-decoration: none; COLOR: #0011FF; }
a:active {                          text-decoration: none; COLOR: #0011FF; }
a:hover {                           text-decoration: none; COLOR: #0011FF; }
</style>
</head>
<body style="overflow:auto;">
<h3>Overview of all inline hyperlinks</h3>
<b>Click on the title of an article or paragraph to edit.</b><br>
<b>Click on the link next to &lt;A&gt;...&lt;/A&gt; to test the link.</b><br>
<% Vector articles = new Vector(); %>
<mm:listnodes type="artikel" orderby="number" jspvar="tA"><% 
   if((tA.getStringValue("tekst").toUpperCase().indexOf("<A ")>-1)
      ||(tA.getStringValue("intro").toUpperCase().indexOf("<A ")>-1)){
      articles.add(tA.getStringValue("number"));
   } 
%></mm:listnodes>
<% Vector paragraphs = new Vector(); %>
<mm:listnodes type="paragraaf" orderby="number" jspvar="pA"><% 
   if(pA.getStringValue("tekst").toUpperCase().indexOf("<A ")>-1){
      paragraphs.add(pA.getStringValue("number"));
   } 
%></mm:listnodes>
<mm:import jspvar="offsetID" externid="offset" id="offset">0</mm:import>
<%
 
int listSize = articles.size() + paragraphs.size();
int pageSize = 50;
int thisOffset = 0;
try{
   if(!offsetID.equals("")){
     thisOffset = Integer.parseInt(offsetID);
     offsetID ="";
   }
} catch(Exception e) {} 

if(thisOffset>0) { 
  %><a href="viewhrefs.jsp?offset=<%= thisOffset-1 %>"><<</a>  <%
} 
for(int i=0; i < (listSize/pageSize + 1); i++) { 
     if((i>0)&&((i+1)%30==1)) { %><br/><% } 
     if(i==thisOffset) {
         %>&nbsp;<span style="color:red;"><%= i+1 %></span>  <%
     } else { 
         %>&nbsp;<a href="viewhrefs.jsp?offset=<%= i %>"><%= i+1 %></a>  <%
     }
}
if(thisOffset+1<(listSize/pageSize + 1)) { 
  %>&nbsp;<a href="viewhrefs.jsp?offset=<%= thisOffset+1 %>">>></a><%
}

int startPage = thisOffset*pageSize;
int articlesSize = articles.size();
for(int i=startPage;i<startPage + pageSize;i++){
   if(i<articlesSize) {
      %><mm:node number="<%= (String) articles.get(i) %>">
         <mm:field name="titel" jspvar="titel" vartype="String" write="false">
         <mm:field name="number" jspvar="number" vartype="String" write="false">
            <mm:field name="tekst" jspvar="tekst" vartype="String" write="false"><%= viewHrefs(titel, number, "artikel", "tekst", tekst) %></mm:field>
            <mm:field name="intro" jspvar="intro" vartype="String" write="false"><%= viewHrefs(titel, number, "artikel", "intro", intro) %></mm:field>
         </mm:field>
         </mm:field>
      </mm:node><br/>
		<a href="usedinitems.jsp?ID=<%= (String) articles.get(i) %>" target="_blank">used in items</a><% 
   } else if (i < listSize) {
   // as we move through "articles + paragraphs" we need to adjust paragraphs index by removing the articles size in i
       %><mm:node number="<%= (String) paragraphs.get(i - articlesSize) %>">
         <mm:field name="number" jspvar="number" vartype="String" write="false">
         <mm:field name="titel" jspvar="titel" vartype="String" write="false">
            <mm:field name="tekst" jspvar="tekst" vartype="String" write="false"><%= viewHrefs(titel, number, "paragraaf", "tekst", tekst) %></mm:field>
         </mm:field>
         </mm:field>
      </mm:node><br/><a href="usedinitems.jsp?ID=<%= (String) paragraphs.get(i - articlesSize) %>" target="_blank">used in items</a><% 
   }
}%>
</body>
</mm:cloud>
