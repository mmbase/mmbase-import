<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/edit/list" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/edit/form" %>
<list:wizard  title="Plaats van Herinnering" >

    <edit:path name="Plaatsen" session="plaatsen" />
    <edit:sessionpath/>

    <list:add text="maak een plaats van herinnering aan" wizardfile="plaats" />

    <list:search nodetype="memorylocation" wizardfile="plaats" >
    <list:searchfields fields="title,intro" defaultmaxage="365"/>
        <list:parentsearchlist >
            <list:searchrow  fields="title,intro"   />
        </list:parentsearchlist>
    </list:search>
</list:wizard>

