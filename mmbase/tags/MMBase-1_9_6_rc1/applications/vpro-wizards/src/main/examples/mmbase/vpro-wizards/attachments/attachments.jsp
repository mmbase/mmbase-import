<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="list" tagdir="/WEB-INF/tags/vpro-wizards/list" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
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

