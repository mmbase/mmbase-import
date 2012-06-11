<%--
    a very simple wrapper for an info contianer on the page. using this tag
    your content is shown in a container that fits with the rest of the page
--%>
<%@ attribute name="title" %>
<div class="info">
    <h5>${title}</h5>
    <div class="content">
        <jsp:doBody/>
    </div>
</div>
