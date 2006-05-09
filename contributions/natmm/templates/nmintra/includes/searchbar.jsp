<script language="JavaScript">
<!--
function startSearch() {
    var href = document.searchform.action;
    var search = escape(document.searchform.elements["search"].value);
    if(search != '') {
        href += "&search=" + search;
    }
    var adv = escape(document.searchform.elements["adv"].value);
	 if(adv != '') {
        href += "&t=" + adv;
    }
	 document.location =  href;
    return false; 
}
function startPhone() {
    var href = document.phoneform.action;
    var name = escape(document.phoneform.elements["name"].value);
    if(name != '') {
        href += "&name=" + name;
    }
    document.location =  href;
    return false; 
}
//-->
</script><tr>
<%-- *************************************** natuurmonumenten logo ******************************* --%>
<td rowspan="3"><a href="http://www.natuurmonumenten.nl" target="_blank"><img src="media/<%= logoName %>_logojub.gif" border="0" title=""></a></td>
<td style="width:70%;"><img src="media/spacer.gif" width="1" height="12"></td>
<%-- *************************************** natuurmonumenten intranet ******************************* --%>
<td class="header" style="padding-right:10px;padding-top:5px;text-align:right;width:251px;">Natuurmonumenten <a href="index.jsp" target="_top"><span class="red"><mm:node number="<%= rootId %>"
            ><mm:field name="naam" jspvar="website_title" vartype="String" write="false"
                ><%= website_title.toUpperCase()
            %></mm:field
            ></mm:node></span></a>
</td>
</tr>
<tr>
<td style="width:70%;">
<%-- *************************************** zoek box ******************************* --%>
<table border=0 cellspacing="0" cellpadding="0">
    <form name="searchform" action="search.jsp?p=search" onSubmit="return startSearch();">
    <tr>
    <td><input type="text" name="search" value="<% if(searchId.equals("")){ %>ik zoek op ...<% } else { %><%= searchId %><% } 
        %>" class="<%= cssClassName %>" style="text-align:left;width:110px;" <% if(searchId.equals("")){ %>onClick="this.value='';"<% } %> /></td>
    <td><img src="media/spacer.gif" width="7" height="1"></td>
    <td><img src="media/spacer.gif" width="7" height="1"></td>
    <td><img src="media/spacer.gif" width="1" height="1"><br>
        <input type="submit" name="Submit" value="Zoek" class="<%= cssClassName %>" style="text-align:center;font-weight:bold;"></td>
	 <td><img src="media/spacer.gif" width="7" height="1"></td>
    <td><img src="media/spacer.gif" width="7" height="1"></td>
    <td><img src="media/spacer.gif" width="1" height="1"><br>
	 	  <input type="hidden" name="adv">
        <input type="submit" name="AdvSubmit" value="Uitgebreid Zoeken" class="<%= cssClassName %>" style="text-align:center;font-weight:bold;width:110px;" onClick="document.searchform.adv.value='adv_search';"></td>
    </tr>
    </form>
</table>
</td>
<td style="padding-right:10px;width:251px;">
<%-- *************************************** phone box ******************************* --%>
<% if((isPreview)
	&&templateTitle.indexOf("smoelenboek")==-1
	&&!cssClassName.equals("bibliotheek")) { 
    %><table border=0 cellspacing="0" cellpadding="0" align="right">
        <form name="phoneform" action="smoelenboek.jsp?p=wieiswie" onSubmit="return startPhone();">
        <tr>
        <td><img src="media/telefoon.gif"></td>
        <td><input type="text" name="name" value="<% if(nameId.equals("")){ %><%= nameEntry %><% } else { %><%= nameId %><% } 
            %>" class="<%= cssClassName %>" style="text-align:left;width:166px;" <% if(searchId.equals("")){ %>onClick="this.value='';"<% } %> /></td>
        <td><img src="media/spacer.gif" width="7" height="1"></td>
        <td><img src="media/spacer.gif" width="1" height="1"><br>
            <input type="submit" name="phone" value="Zoek"  class="<%= cssClassName %>" style="text-align:center;font-weight:bold;"></td>
        </tr>
        </form>
    </table><%
} else {
    %>&nbsp;<%
} %>
</td>
</tr>
<tr>
<td style="width:70%;"><img src="media/spacer.gif" width="1" height="12"></td>
<td style="width:251px;"><img src="media/spacer.gif" width="251" height="12"></td>
</tr>
