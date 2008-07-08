<%@page session="false" contentType="text/xml; charset=utf-8"
%><% response.setContentType("text/xml; charset=UTF-8");
%><%@page import="java.util.Date,nl.leocms.evenementen.*,nl.leocms.util.tools.HtmlCleaner,nl.leocms.evenementen.Evenement"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache"
%><mm:cloud jspvar="cloud"
><%

String sPageTemplateURL = "";
String DATA_FORMAT ="E, dd MMM yyyy, HH:mm";

%><%@include file="../includes/time.jsp" %><%


int iSearchNumberOfDays = 7;
String pSearchNumberOfDays = request.getParameter("dagen");
pSearchNumberOfDays = (pSearchNumberOfDays == null ? "" : HtmlCleaner.filterAmps(pSearchNumberOfDays).trim());

// SearchNumberOfDays is default 7 and cannot be more than 365
if (!"".equals(pSearchNumberOfDays)) {
   try {
      iSearchNumberOfDays = Integer.valueOf(pSearchNumberOfDays).intValue();
   } 
   catch (NumberFormatException nfe) {
      System.out.print("xmlactiviteiten.jsp: value of parameter 'dagen' (" + pSearchNumberOfDays + ") is not a number!");
   }  
   if (iSearchNumberOfDays > 365) iSearchNumberOfDays = 365;
}


long lDateSearchFrom = nowSec;
long lDateSearchTill = lDateSearchFrom + iSearchNumberOfDays*24*60*60;

//if(application.getAttribute("events_till")==null){
//   EventNotifier.updateAppAttributes(cloud);
//}
//if(application.getAttribute("events_till")!=null) {
//   lDateSearchTill = ((Long) application.getAttribute("events_till")).longValue();
//}

String sChildConstraints = Evenement.getEventsConstraint(lDateSearchFrom,lDateSearchTill);

String pProvincie = request.getParameter("provincie");
pProvincie = (pProvincie == null ? "" : HtmlCleaner.filterAmps(pProvincie).trim());
boolean isProvincieMatch = false;


%><?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE rss [
<!ELEMENT rss (channel)>
<!ATTLIST rss version CDATA #REQUIRED>
<!ELEMENT channel (title, link, description, item+)>
<!ELEMENT item (title, link, description, pubDate, id, activiteitstypen, natuurgebieden, provincie, kosten, aantaldeelnemers, vertrekpunt, bezoekerscentrum, districtscommissie, routebeschrijving, aanmelding, activiteitdata)>
<!ELEMENT aanmelding (#PCDATA)>
<!ELEMENT aantaldeelnemers (#PCDATA)>
<!ELEMENT activiteitdata (activiteitdatum+)>
<!ELEMENT activiteitdatum (begindatum, einddatum)>
<!ELEMENT activiteitstype (#PCDATA)>
<!ELEMENT activiteitstypen (#PCDATA | activiteitstype)*>
<!ELEMENT begindatum (#PCDATA)>
<!ELEMENT bezoekerscentrum (#PCDATA)>
<!ELEMENT description (#PCDATA)>
<!ELEMENT districtscommissie (#PCDATA)>
<!ELEMENT einddatum (#PCDATA)>
<!ELEMENT id (#PCDATA)>
<!ELEMENT kosten (#PCDATA)>
<!ELEMENT link (#PCDATA)>
<!ELEMENT natuurgebied (#PCDATA)>
<!ELEMENT natuurgebieden (#PCDATA | natuurgebied)*>
<!ELEMENT provincie (#PCDATA)>
<!ELEMENT pubDate (#PCDATA)>
<!ELEMENT routebeschrijving (#PCDATA)>
<!ELEMENT title (#PCDATA)>
<!ELEMENT vertrekpunt (#PCDATA)>
<%@include file="entities.jsp" %>
]>
<mm:locale language="en"
><rss version="2.0">
   <mm:list nodes="agenda" path="pagina"
      ><channel>
         <mm:node element="pagina" jspvar="thisPage"
            ><title><mm:field name="titel" jspvar="naam" vartype="String" write="false"
                      ><%= (naam==null ? "" : HtmlCleaner.filterAmps(naam).trim())
                   %></mm:field
            ></title>
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
            <mm:listnodes type="evenement" constraints="<%=sChildConstraints%>" jspvar="thisEvent" orderby="begindatum" directions="UP"
               ><%               
               String parent_number = Evenement.findParentNumber(thisEvent.getStringValue("number"));
               %><mm:node number="<%= parent_number %>"
               ><mm:related path="related,natuurgebieden,pos4rel,provincies"
                ><mm:field name="provincies.naam" jspvar="naam" vartype="String" write="false"><%
                     naam = (naam==null ? "" : HtmlCleaner.filterAmps(naam).trim());
                     isProvincieMatch = false;
                     if (pProvincie.toLowerCase().equals(naam.toLowerCase())) isProvincieMatch = true; 
                %></mm:field
               ></mm:related
               ></mm:node><% 
               if ((pProvincie == "") || (isProvincieMatch)) { 
                %><item>
                  <title><mm:field name="titel" jspvar="naam" vartype="String" write="false"
                            ><%= (naam==null ? "" : HtmlCleaner.filterAmps(naam).trim())
                         %></mm:field
                  ></title>
                  <link>http://<%= request.getServerName() %>/<%=sPageTemplateURL%>?p=<%= thisPage.getStringValue("number") %>&amp;e=<mm:field name="number"/></link>
                  <mm:node number="<%= parent_number %>"
                     ><description><%
                        %><mm:field name="tekst" jspvar="tekst" vartype="String" write="false"
                           ><%= (tekst==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(tekst,"<",">","")).trim())
                        %></mm:field
                     ></description>
                     <pubDate><mm:field name="datumlaatstewijziging" jspvar="datum" vartype="String" write="false"><mm:time time="<%= datum %>" format="<%= DATA_FORMAT %>"/></mm:field></pubDate>
                  </mm:node
                  ><id><mm:field name="number"/></id>
                  <mm:node number="<%= parent_number %>"
                     ><activiteitstypen><%
                        %><mm:related path="related,evenement_type"
                           ><activiteitstype><mm:field name="evenement_type.naam" jspvar="naam" vartype="String" write="false"
                              ><%= (naam==null ? "" : HtmlCleaner.filterAmps(naam).trim())
                           %></mm:field
                           ></activiteitstype><%
                        %></mm:related
                     ></activiteitstypen>
                     <natuurgebieden><%
                        %><mm:related path="related,natuurgebieden"
                           ><natuurgebied><mm:field name="natuurgebieden.naam" jspvar="naam" vartype="String" write="false"
                              ><%= (naam==null ? "" : HtmlCleaner.filterAmps(naam).trim())
                           %></mm:field
                           ></natuurgebied><%
                        %></mm:related
                     ></natuurgebieden>
                     <provincie><%
                        %><mm:related path="related,natuurgebieden,pos4rel,provincies"
                           ><mm:first inverse="true">, </mm:first><mm:field name="provincies.naam" jspvar="naam" vartype="String" write="false"
                              ><%= (naam==null ? "" : HtmlCleaner.filterAmps(naam).trim())
                           %></mm:field
                           ></mm:related
                     ></provincie>
                     <kosten><%
                        %><mm:related path="posrel,deelnemers_categorie" orderby="deelnemers_categorie.naam"
                           ><mm:field name="deelnemers_categorie.naam" jspvar="naam" vartype="String" write="false"
                              ><%= (naam==null ? "" : HtmlCleaner.filterAmps(naam).trim())
                           %></mm:field
                           > &#8364; <mm:field name="posrel.pos" jspvar="price" vartype="String" write="false"><%=priceFormating(price)%></mm:field
                         >; </mm:related
                     ></kosten>
                     <aantaldeelnemers><%
                        %><mm:field name="max_aantal_deelnemers" jspvar="tmp" vartype="String" write="false"
                           ><%= (tmp.equals("-1") ? "" : tmp )
                        %></mm:field
                     ></aantaldeelnemers>
                     <vertrekpunt><%
                        %><mm:related path="posrel,vertrekpunten"
                           ><mm:first inverse="true">, </mm:first><mm:field name="vertrekpunten.titel" jspvar="naam" vartype="String" write="false"
                              ><%= (naam==null ? "" : HtmlCleaner.filterAmps(naam).trim())
                           %></mm:field
                        ></mm:related
                     ></vertrekpunt>
                     <bezoekerscentrum><%
                        %><mm:related path="readmore,afdelingen" constraints="afdelingen.naam LIKE '%Bezoekerscentrum%'"
                           ><mm:first inverse="true">, </mm:first><mm:field name="afdelingen.naam" jspvar="naam" vartype="String" write="false"
                              ><%= (naam==null ? "" : HtmlCleaner.filterAmps(naam).trim())
                           %></mm:field
                        ></mm:related
                     ></bezoekerscentrum>
                     <districtscommissie><%--
                         disctricscommissie is a page not directly related to the evenement, natuurgebied or provincie
                     --%></districtscommissie>
                     <routebeschrijving><%
                         %><mm:related path="posrel,vertrekpunten"
                           ><mm:first inverse="true">; </mm:first
                           ><mm:size jspvar="size" vartype="Integer" write="false"
                           ><%
                              if(size.intValue() > 1)
                              {
                                 %><mm:field name="vertrekpunten.titel" jspvar="naam" vartype="String" write="false"
                                       ><%= (naam==null ? "" : HtmlCleaner.filterAmps(naam).trim())
                                 %></mm:field> : <%
                              }
                           %></mm:size
                           ><mm:field name="vertrekpunten.tekst" jspvar="tekst" vartype="String" write="false"
                              ><%= (tekst==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(tekst,"<",">","")).trim())
                           %></mm:field
                        ></mm:related
                     ></routebeschrijving>
                     <aanmelding><%
                        %><mm:related path="readmore,paragraaf" constraints="readmore.readmore='2'"
                           ><mm:field name="paragraaf.tekst" jspvar="tekst" vartype="String" write="false"
                              ><%= (tekst==null ? "" : HtmlCleaner.filterAmps(HtmlCleaner.cleanText(tekst,"<",">","")).trim())
                           %></mm:field
                        ></mm:related
                     ></aanmelding>
                  </mm:node
                  ><activiteitdata>
                     <activiteitdatum>
                        <begindatum><mm:field name="begindatum" jspvar="datum" vartype="String" write="false"><mm:time time="<%=datum%>" format="<%= DATA_FORMAT %>"/></mm:field></begindatum>
                        <einddatum><mm:field name="einddatum" jspvar="datum" vartype="String" write="false"><mm:time time="<%=datum%>" format="<%= DATA_FORMAT %>"/></mm:field></einddatum>
                     </activiteitdatum>
                  </activiteitdata>
               </item><% } 
           %></mm:listnodes
         ></mm:node
      ></channel>
   </mm:list
></rss>
</mm:locale>
</mm:cloud>
<%!
private String priceFormating(String sPrice)
{
   if(sPrice.equals("-1")) return "";
   int iPrice = Integer.parseInt(sPrice);

   String sResult = iPrice/100 + ",";
   if(iPrice%100 < 9) sResult += "0";
   sResult += iPrice%100;

   return sResult;
}
%>