<%@ tag body-content="empty"  %>
<%@ attribute name="title" %>
<%@ attribute name="buttons" fragment="true" description="should contain a list of <a> elements" %>
<%@ attribute name="items" fragment="true" description="should contain an <ul> element with list elements. Each <li> can
have a nested <div> with class 'icons' " %>
<%--
    this should be used by the view tag. it is to create an interface item just like it.
    it can be used by other stuff too, if you want to create a contianer that looks just like the view contianer.
--%>
<div class="relatedItem">
    <h5>${title}</h5>
    <div class="addItem">
        <jsp:invoke fragment="buttons"/>
    </div>
    <jsp:invoke fragment="items"/>

</div>
