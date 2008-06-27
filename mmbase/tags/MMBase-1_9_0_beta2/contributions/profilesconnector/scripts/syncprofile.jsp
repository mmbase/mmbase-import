<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-2.0"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.mmbase.applications.profilesconnector.*"%>
<%--
    dit script heeft als doel om de velden uit het mmbob profiel te synchroniseren met de velden uit
    het entree profiel.
    Per poster worden een aantal velden gelezen, en worden diezelfde velden gelezen uit het externe profiel.
    wanneer een van de velden niet overeen komen wordt het mmbob profiel aangepast.
--%>

<%--geen updates als dit waar is.--%>
<c:set var="testmode" value="true" />
<%
    ProfilesConnector connector = new ProfilesConnector();
    String dienstId = "187388721";
%>

<style type="text/css">
body{
    font-size: 10px;
    font-family: verdana,arial;
}
.off td{
    background-color: red;
}
.forum{
    font-size: 20px;
    text-align: center;
    background-color: green;
    padding: 10px 0px 10px 0px;
    border: 1px solid black;
    color: white;
}

.poster > td{
    border-bottom: 1px solid #333333;
}
</style>
<mm:cloud method="http" rank="administrator">
    <table>
        <mm:listnodes type="forums" >
            <mm:first>
                <tr>
                    <th>extern profiel</th>
                    <th>mmbob profiel</th>
                </tr>
            </mm:first>
            <tr>
                <td colspan="2" class="forum">
                <mm:field name="name" />
                </td>
            </tr>
            <mm:field name="number" id="forumid" write="false" />
            <mm:relatednodes type="posters" role="forposrel">

                <mm:field name="number" write="false" id="posterid" />
                <tr>
                    <td colspan="2"> <b><mm:field name="account" />(<mm:write referid="posterid"/>)</b></td>
                </tr>
                <%--haal de waardes voor deze poster uit het externe profiel--%>
                <mm:field name="account" jspvar="account" vartype="String" id="account">
                    <c:set var="remote_firstname"><%=connector.getValue(account ,"187388721:BS:voornaam")%></c:set>
                    <c:set var="remote_lastname"><%=connector.getValue(account ,"187388721:BS:tussenvoegsel")%> <%=connector.getValue(account ,"187388721:BS:achternaam")%></c:set>
                    <c:set var="remote_emailaddress"><%=connector.getValue(account ,"187388721:BS:emailadres")%></c:set>
                    <c:set var="remote_nick"><%=connector.getValue(account ,"187388721:BS:nickname")%></c:set>
                </mm:field>
                <c:set var="local_firstname" ><mm:field name="firstname" /></c:set>
                <c:set var="local_lastname" ><mm:field name="lastname" /></c:set>
                <c:set var="local_emailaddress" ><mm:field name="email" /></c:set>
                <c:set var="test" ><mm:write referid="forumid"/>:<mm:write referid="posterid"/></c:set>
                <c:set var="local_nick" >
                    <mm:nodefunction name="getPosterInfo" set="mmbob" referids="forumid,posterid">
                        <mm:field name="nick" />
                    </mm:nodefunction>
                </c:set>

                <c:set var="m1">
                    <c:choose>
                        <c:when test="${remote_firstname != local_firstname}">off</c:when>
                        <c:otherwise>match</c:otherwise>
                    </c:choose>
                </c:set>

                <c:set var="m2">
                    <c:choose>
                        <c:when test="${remote_lastname != local_lastname}">off</c:when>
                        <c:otherwise>match</c:otherwise>
                    </c:choose>
                </c:set>

                <c:set var="m3">
                    <c:choose>
                        <c:when test="${remote_emailaddress != local_emailaddress}">off</c:when>
                        <c:otherwise>match</c:otherwise>
                    </c:choose>
                </c:set>

                <c:set var="m4">
                    <c:choose>
                        <c:when test="${remote_nick != local_nick}">off</c:when>
                        <c:otherwise>match</c:otherwise>
                    </c:choose>
                </c:set>

                <tr class="poster">
                    <%--remote values--%>
                    <td>
                        <table>
                            <tr class="${m1}">
                                <td>voornaam</td>
                                <td>${remote_firstname}</td>
                            </tr>
                            <tr class="${m2}">
                                <td>achternaam</td>
                                <td>${remote_lastname}</td>
                            </tr>
                            <tr class="${m3}">
                                <td>email</td>
                                <td>${remote_emailaddress}</td>
                            </tr>
                            <tr class="${m4}">
                                <td>nickname</td>
                                <td>${remote_nick}</td>
                            </tr>
                        </table>
                    </td>
                    <%--local values--%>
                    <td>
                        <table>
                            <tr class="${m1}">
                                <td>voornaam</td>
                                <td>${local_firstname}</td>
                            </tr >
                            <tr class="${m2}">
                                <td>achternaam</td>
                                <td>${local_lastname}</td>
                            </tr >
                            <tr class="${m3}">
                                <td>email</td>
                                <td>${local_emailaddress}</td>
                            </tr>
                            <tr class="${m4}">
                                <td>nickname</td>
                                <td>${local_nick}</td>
                            </tr>
                        </table>
                    </td>
                </tr>
                </mm:relatednodes>
            </mm:listnodes>
    </table>

</mm:cloud>

