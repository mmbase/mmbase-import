<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Maak een nieuwe resource aan</title>
</head>
<body>

<form method="post" action="nieuwe_resource_action.jsp" enctype="multipart/form-data">
	<table>
		<tr>
			<td>naam</td>
			<td><input type="text" name="key" size="40" value="jsp.user." /></td>
		</tr>

		<tr>
			<td>inhoud</td>
			<td><textarea name="value" cols="40" rows="20"></textarea></td>
		</tr>
		<tr>
			<td colspan="2">
			<input type="submit" />
			</td>
		</tr>

	</table>
</form>


</body>
</html>