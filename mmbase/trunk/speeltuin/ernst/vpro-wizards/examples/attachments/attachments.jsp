<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/edit" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/edit/list" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/edit/form" %>
<list:wizard  title="Plaats van Herinnering" >

    <edit:path name="bijlagen" session="attachments" />
   <edit:sessionpath/>

    <list:add text="maak een bijlage aan" wizardfile="attachment" />

    <list:search nodetype="attachments" wizardfile="attachment">
    <list:searchfields fields="title,description,number" defaultmaxage="365"/>
        <list:parentsearchlist >
            <list:searchrow  edit="true" fields="title,description"   />
        </list:parentsearchlist>
    </list:search>
</list:wizard>

