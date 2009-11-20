<%--
    Use this tag to create input fields inside the 'extrafields' attribute fragment
    of the searchfields tag.
    It creates the right html structure so the field integrates nicely
    Just put the input element in the body.
--%>
<%@ attribute name="label" required="true" %>
<div class="inputBlock">
    <div class="fieldName">${label}</div>
    <div class="fieldValue">
        <jsp:doBody/>
    </div>
</div>
