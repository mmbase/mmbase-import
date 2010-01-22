<%--
    use this tag inside the list:searchfields fields fragment attribute to create 
    custom input fields.
    the input attribute is a fragment itself, it should contian the html for the input field.
--%>
<%@ attribute name="label" %>
<%@ attribute name="input" fragment="true" %>
<div class="inputBlock">
    <div class="fieldName">${label}</div>
    <div class="fieldValue"><jsp:invoke fragment="input"/></div>
</div>
