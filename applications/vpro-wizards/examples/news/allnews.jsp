<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<list:wizard  title="Nieuws berichten" >

    <edit:path name="Magazines" session="mags" />
    <edit:sessionpath/>

    <list:add text="maak een nieuws bericht aan" wizardfile="newsitem" />

    <list:search nodetype="news" wizardfile="newsitem" >
    <list:searchfields fields="title,subtitle,intro" defaultmaxage="365"/>
        <list:parentsearchlist >
            <list:searchrow  fields="title,subtitle,intro" >
                <jsp:attribute name="headerfragment" >
                    <th>Publicatie</th>
                    <th>Magazine</th>
                </jsp:attribute>
                <jsp:attribute name="rowfragment" >
                    <td>
                        <mm:include page="item-publishedstatus.jsp">
                            <mm:param name="nodenr" value="${nodenrrow}" />
                            <mm:param name="detail" value="false" />
                        </mm:include>
                    </td>
                    <td>
                        <mm:node number="${nodenrrow}">
                            <mm:relatednodes type="mags" max="1">
                                <mm:field name="title" />
                            </mm:relatednodes>
                        </mm:node>
                    </td>
                </jsp:attribute>
            </list:searchrow>
        </list:parentsearchlist>
    </list:search>
</list:wizard>
