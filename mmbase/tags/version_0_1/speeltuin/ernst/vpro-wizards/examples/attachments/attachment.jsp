<%@ taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/vpro-wizards/form" %>
<%@ taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"  %>

<form:wizard title="Bijlage" wizardfile="attachment" >

    <edit:path name="plaats" node="${nodenr}" session="attachment"/>
    <edit:sessionpath/>



    <form:container nodetype="attachments" multipart="true">
        <form:textfield field="title"/>
        <form:textareafield size="small" field="description"/>
        <form:filefield field="handle"/>



        <form:showfield field="mimetype"/>
        <form:showfield field="filename"/>
        <form:showfield field="size"/>
    </form:container>
</form:wizard>



