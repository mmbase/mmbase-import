<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<list:wizard  title="Magazines" >

    <edit:path name="Magazines" session="mags" />
    <edit:sessionpath/>

    <list:add text="maak een magazine aan" wizardfile="magazine" />

    <list:search nodetype="mags" wizardfile="plaats" >
    <list:searchfields fields="title,subtitle,intro" defaultmaxage="365"/>
        <list:parentsearchlist >
            <list:searchrow  fields="title,subtitle,intro"   />
        </list:parentsearchlist>
    </list:search>
</list:wizard>

