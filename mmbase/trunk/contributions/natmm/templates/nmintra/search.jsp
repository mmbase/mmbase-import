<%@include file="includes/templateheader.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/header.jsp" 
%><%@include file="includes/calendar.jsp" 
%><%@include file="includes/searchfunctions.jsp" %><%

boolean debug = false;
//boolean debug = true;

String defaultSearchId = "ik zoek op ...";
searchId = HtmlCleaner.filterUTFChars(searchId);
TreeMap nodePaths = new TreeMap();

// page: translate alias back into number 
%><mm:node number="<%= pageId %>"
    ><mm:field name="number" jspvar="page_number" vartype="String" write="false"
        ><% pageId = page_number; 
    %></mm:field
></mm:node><%

//String allCategoryIds = categoryId; // *** categoryId is a rubriek or a site

String searchedPages = ""; // *** look for those pages that belong to one of the nodes in allCategoryIds ***
String sConstraints = ""; %>
<mm:node number="<%= categoryId %>">
	<mm:aliaslist>
		<mm:write jspvar="alias" vartype="String" write="false">
			<% if (!alias.equals("home")) {
				sConstraints = "rubriek2.number = '" + categoryId + "'";
				}%>
		</mm:write>
	</mm:aliaslist>
</mm:node>
<% // *** normal page *** %>
<mm:list nodes="<%= websiteId %>" path="rubriek1,parent,rubriek2,posrel,pagina" constraints="<%= sConstraints %>"
    ><mm:field name="pagina.number" jspvar="page1_number" vartype="String" write="false"
        ><% searchedPages += "," + page1_number; 
    %></mm:field
></mm:list><%
// *** subpages ***
if (!sConstraints.equals("")){ sConstraints += " AND ";}
sConstraints += "rubriek1.number != rubriek3.number";
%><mm:list nodes="<%= websiteId %>" path="rubriek1,parent1,rubriek2,parent2,rubriek3,posrel,pagina" constraints="<%= sConstraints %>"
    ><mm:field name="pagina.number" jspvar="page1_number" vartype="String" write="false"
        ><% searchedPages += "," + page1_number; 
    %></mm:field
></mm:list><% 

if(!searchedPages.equals("")) { searchedPages = searchedPages.substring(1); }

%><%@include file="includes/searchconfig.jsp" 
%><%@include file="includes/searchresultsget.jsp" 

%><td><%@include file="includes/pagetitle.jsp" %></td>
<td><% String rightBarTitle = "";
    %><%@include file="includes/rightbartitle.jsp" 
%></td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>">
<table border="0" cellpadding="0" cellspacing="0">
    <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
    <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
        <td>
<mm:list nodes="iframe_template,popup_template,newwin_template" path="paginatemplate,gebruikt,pagina,posrel,rubriek"
   constraints="<%= "rubriek.number='"+ categoryId + "'" %>">
   <p><strong>Dit systeem zoekt niet in de <mm:field name="pagina.titel" />.<br/><br/>
   Wil je in de <mm:field name="pagina.titel" /> zoeken, klik dan eerst op "<mm:field name="pagina.titel" />" (linkerzijde van de pagina).</strong></p>
</mm:list><% 
if(!searchId.equals(defaultSearchId)&&!searchId.equals("")) {
    %><p>Je hebt gezocht op "<%= searchId %>" <mm:node number="<%= categoryId %>" notfound="skipbody">in "<mm:field name="naam" />" </mm:node>.</p><%

    int listSize = searchResultMap.size();
    if(listSize==0) { 
        %><p>Je zoekopdracht heeft geen resultaten opgeleverd.</p>
        <p><a href="search.jsp?p=wieiswie&name=<%= searchId %>">Klik hier om op "<%= searchId %>" te zoeken in de Wie-is-wie applicatie.</a></p><% 
    } else {  
        int thisOffset = 0;
        try{
            if(!offsetId.equals("")){
                thisOffset = Integer.parseInt(offsetId);
                offsetId ="";
            }
        } catch(Exception e) {} 

        if(false&&listSize>10) { 
            int toIndex =  thisOffset*10 + 10;
            if(toIndex>listSize) { toIndex = listSize; }
            %><p><strong>Je zoekopdracht heeft <%= listSize %> resultaten opgeleverd. Resultaat <%= thisOffset*10 %> - <%= toIndex %> :</strong></p><%
        } else {
            %><p><strong>Je zoek opdracht heeft de volgende resultaten opgeleverd.</strong></p><%

        } %><%@include file="includes/searchresultsshow.jsp" %><%

        if(false&&listSize>10) {  
            %><div align="center"><%
                    if(thisOffset>0) { 
                        %><a target="_top" href="<mm:url page="<%= "search.jsp?p=search&search=" + searchId + "&offset=" + (thisOffset-1)  %>" />"" class="contentreadmore">
                            [<< previous ]</a>&nbsp;&nbsp;<%
                    }
                    for(int i=0; i < (listSize/10 + 1); i++) {  
                        if(i==thisOffset) {
                            %><%= i+1 %>&nbsp;&nbsp;<%
                        } else { 
                            %><a target="_top" href="<mm:url page="<%= "search.jsp?p=search&search=" + searchId + "&offset=" + i  %>" />"" class="contentreadmore">
                                <%= i+1 %></a>&nbsp;&nbsp;<%
                        } 
                    }
                    if(thisOffset+1<(listSize/10 + 1)) { 
                        %><a  target="_top" href="<mm:url page="<%= "search.jsp?p=search&search=" + searchId + "&offset=" + (thisOffset+1)  %>" />"" class="contentreadmore">
                            [next >>]</a><%
                    } 
            %></div><%
        }
    }
} else { 

    // ***  searchId is empty ***
    %><table border=0 cellspacing="0" cellpadding="0">
    <tr><td>
        <div class="pageheader">Je hebt geen woord(en) ingevuld om op te zoeken.</div><br><br>
        <ul>
        <li>Vul een woord in in het veld "ik zoek op ...",</li>
        <li>selecteer de website, rubriek of subwebsite waar je in wilt zoeken,</li>
        <li>en klik op de zoek knop.</li>
        </ul>
        </td>
    </tr>
    </table><% 

} 
%><br><br></td>
    <td><img src="media/spacer.gif" width="10" height="1"></td>
</tr>
</table>
</div>
</td>
<td><% 

// *********************************** right bar *******************************
%><img src="media/spacer.gif" width="10" height="1"></td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>
