<%@page session="false" contentType="text/xml; charset=utf-8"
%><% response.setContentType("text/xml; charset=UTF-8"); 
%><%@page import="java.util.Date,nl.leocms.util.tools.HtmlCleaner" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache"
%><cache:cache time="<%= 3600*24 %>" scope="application"><%

String sPageTemplateURL = "";
String DATA_FORMAT ="E, dd MMM yyyy, H:mm";
%><?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE rss [
<!ELEMENT rss (channel)>
<!ATTLIST rss version CDATA #REQUIRED>
<!ELEMENT channel (title, link, description, item+)>
<!ELEMENT alinea (titel, tekst, plaatje?)>
<!ELEMENT alineas (alinea+)>
<!ELEMENT description (#PCDATA)>
<!ELEMENT id (#PCDATA)>
<!ELEMENT item (title, link, description, pubDate, id, alineas)>
<!ELEMENT link (#PCDATA)>
<!ELEMENT plaatje (#PCDATA)>
<!ELEMENT pubDate (#PCDATA)>
<!ELEMENT tekst (#PCDATA)>
<!ELEMENT title (#PCDATA)>
<!ELEMENT titel (#PCDATA)>
<%@include file="entities.jsp" %>
]>
<mm:cloud
><mm:locale language="en"
><rss version="2.0">
      <mm:list nodes="routes" path="pagina"
         ><channel>
            <mm:node element="pagina" jspvar="thisPage"
               ><title><mm:field name="titel"/></title>
               <link><%
                  %><mm:related path="gebruikt,paginatemplate"
                     ><mm:field name="paginatemplate.url" jspvar="sTmp" vartype="String"
                        ><%sPageTemplateURL = sTmp;
                        %>http://<%= request.getServerName() %>/<%=sTmp%>?p=<%= thisPage.getStringValue("number") 
                     %></mm:field
                  ></mm:related
               ></link>
               <description><% 
                  String omschrijving = thisPage.getStringValue("omschrijving");
                  if(omschrijving!=null) { %><%= HtmlCleaner.filterAmps(HtmlCleaner.cleanText(omschrijving,"<",">","")).trim() %><% } 
               %></description>
               <mm:list path="natuurgebieden,rolerel,artikel" orderby="artikel.titel"
                  ><mm:node element="artikel"
                     ><item>
                        <title><%
                           %><mm:field name="titel" jspvar="titel" vartype="String" write="false"
                              ><%= (titel==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(titel,"<",">","")).trim()) 
                           %></mm:field
                        ></title>
                        <link>http://<%= request.getServerName() %>/route_pop.jsp?id=<mm:field name="number"/></link>
                        <description><%
                           %><mm:field name="intro" jspvar="intro" vartype="String" write="false"
                              ><%= (intro==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(intro,"<",">","")).trim()) 
                           %></mm:field
                           ><mm:field name="tekst" jspvar="tekst" vartype="String" write="false"
                              ><%= (tekst==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(tekst,"<",">","")).trim()) 
                           %></mm:field
                        ></description>
                        <pubDate><mm:field name="datumlaatstewijziging" jspvar="datum" vartype="String" write="false"><mm:time time="<%= datum %>" format="<%= DATA_FORMAT %>"/></mm:field></pubDate>
                        <id><mm:field name="number" /></id>
                        <alineas>
                           <mm:related path="posrel,paragraaf" orderby="posrel.pos"
                              ><mm:node element="paragraaf"
                                 ><alinea>
                                    <titel><%
                                       %><mm:field name="titel" jspvar="titel" vartype="String" write="false"
                                          ><%= (titel==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(titel,"<",">","")).trim()) 
                                       %></mm:field
                                    ></titel>
                                    <tekst><%
                                       %><mm:field name="tekst" jspvar="tekst" vartype="String" write="false"
                                          ><%= (tekst==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(tekst,"<",">","")).trim()) 
                                       %></mm:field
                                    ></tekst>
                                    <mm:related path="posrel,images"
                                       ><plaatje>http://<%= request.getServerName() %>/mmbase/images/<mm:field name="images.number"/></plaatje>
                                    </mm:related
                                 ></alinea>
                              </mm:node
                           ></mm:related
                        ></alineas>
                     </item>
                  </mm:node
               ></mm:list
            ></mm:node
         ></channel>
      </mm:list
   ></rss>
</mm:locale
></mm:cloud
></cache:cache>
