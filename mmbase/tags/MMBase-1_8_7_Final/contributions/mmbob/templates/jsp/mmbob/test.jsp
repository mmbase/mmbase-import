<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h3>parameters</h3>
<table>
    <tr>
        <th>name</th>
        <th>value</th>
    </tr>
    <c:forEach items="${param}" var="e">
        <tr>
            <td>${e.key}</td>
            <td>${e.value}</td>
        </tr>
    </c:forEach>
</table>

<h3>request headers</h3>
<table>
    <tr>
        <th>name</th>
        <th>value</th>
    </tr>
    <c:forEach items="${header}" var="e">
        <tr>
            <td>${e.key}</td>
            <td>${e.value}</td>
        </tr>
    </c:forEach>
</table>

