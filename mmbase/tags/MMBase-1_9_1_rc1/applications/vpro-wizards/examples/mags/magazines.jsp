<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<list:wizard  title="Magazines" >

    <edit:path name="Magazines" session="mags" />
    <edit:sessionpath/>

    <list:add text="maak een magazine aan" wizardfile="magazine" />

    <list:search nodetype="mags" wizardfile="magazine" >
    <list:searchfields fields="title,subtitle,intro" defaultmaxage="365"/>
        <list:parentsearchlist >
            <list:searchrow  fields="title,subtitle,intro"   >
                <jsp:attribute name="headerfragment"><th> aantal berichten</th></jsp:attribute>
                <jsp:attribute name="rowfragment"><td>
                    <mm:node number="${nodenrrow}">
                        <mm:relatednodescontainer type="news" ><mm:size/></mm:relatednodescontainer>
                    </mm:node>
                </td></jsp:attribute>
            </list:searchrow>
        </list:parentsearchlist>
    </list:search>
</list:wizard>
