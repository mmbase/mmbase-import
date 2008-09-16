<%@page  session="false" contentType="text/xml; charset=utf-8"
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
<!ELEMENT description (#PCDATA)>
<!ELEMENT id (#PCDATA)>
<!ELEMENT inhetkorts (#PCDATA | inhetkort)*>
<!ELEMENT inhetkort (#PCDATA)>
<!ATTLIST inhetkort name (algtoe | auto | bus | excur | fiets | horeca | infocen | kaart | opplig | park | parkhan | ruiter | toehond | toeghan | trein | uitkijk | vvv | vaar | wandel | wandelvr) #REQUIRED>
<!ELEMENT item (title, link, description, pubDate, id, inhetkorts, plaatjes?)>
<!ELEMENT link (#PCDATA)>
<!ELEMENT plaatjes (plaatje*)>
<!ELEMENT plaatje (#PCDATA)>
<!ELEMENT pubDate (#PCDATA)>
<!ELEMENT title (#PCDATA)>
<%@include file="entities.jsp" %>
]>
<mm:cloud
><mm:locale language="en"
><rss version="2.0">
      <mm:list nodes="natuurgebieden" path="pagina"
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
                  %><mm:field name="omschrijving" jspvar="omschrijving" vartype="String" write="false"
                     ><%= (omschrijving==null ? "" : HtmlCleaner.cleanText(omschrijving,"<",">"," ").trim() + " " ) 
                  %></mm:field
                  ><mm:related path="contentrel,artikel" orderby="contentrel.pos" max="1"
                     ><mm:node element="artikel"
                        ><mm:field name="intro" jspvar="intro" vartype="String" write="false"
                           ><%= (intro==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(intro,"<",">","")).trim()) 
                        %></mm:field
                        ><mm:field name="tekst" jspvar="tekst" vartype="String" write="false"
                           ><%= (tekst==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(tekst,"<",">","")).trim()) 
                        %></mm:field
                        ><mm:related path="posrel,paragraaf"
                           ><mm:field name="paragraaf.titel"
                              /> <mm:field name="paragraaf.tekst" jspvar="tekst" vartype="String" write="false"
                                 ><%= (tekst==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(tekst,"<",">","")).trim()) 
                           %></mm:field
                        ></mm:related
                      ></mm:node
                   ></mm:related
                ></description>
               <mm:listnodes type="natuurgebieden" orderby="naam"
                  ><item>
                     <title><%
                        %><mm:field name="naam" jspvar="naam" vartype="String" write="false"
                           ><%= HtmlCleaner.filterAmps(naam)
                        %></mm:field
                     ></title>
                     <link>http://<%= request.getServerName() %>/<%=sPageTemplateURL%>?n=<mm:field name="number"/></link>
                     <description><%
                        %><mm:related path="posrel1,artikel,posrel2,paragraaf" constraints="posrel2.pos='1'"
                           ><mm:field name="paragraaf.tekst" jspvar="tekst" vartype="String" write="false"
                              ><%= (tekst==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(tekst,"<",">","")).trim()) 
                           %></mm:field
                        ></mm:related
                     ></description>
                     <pubDate><%
                        %><mm:field name="datumlaatstewijziging" jspvar="datum" vartype="String" write="false"><mm:time time="<%= datum %>" format="<%= DATA_FORMAT %>"/></mm:field
                     ></pubDate>
                     <id><mm:field name="number"/></id>
                     <inhetkorts><%
                        %><mm:related path="readmore,paragraaf"
                           ><inhetkort name="<mm:field name="readmore.readmore"/>"><%
                              %><mm:field name="paragraaf.tekst" jspvar="tekst" vartype="String" write="false"
                                 ><%= (tekst==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(tekst,"<",">","")).trim()) 
                              %></mm:field
                           ></inhetkort><%
                        %></mm:related
                     ></inhetkorts><%
                     %><mm:related path="posrel,artikel,posrel,paragraaf,posrel,images"
                        ><mm:first><plaatjes></mm:first
                           ><plaatje>http://<%= request.getServerName() %>/mmbase/images/<mm:field name="images.number"/></plaatje><%
                        %><mm:last></plaatjes></mm:last
                     ></mm:related>
                  </item>
               </mm:listnodes
            ></mm:node
         ></channel>
      </mm:list
   ></rss>
</mm:locale
></mm:cloud>
</cache:cache>
