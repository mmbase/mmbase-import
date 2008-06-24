<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc"%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<portlet:defineObjects />
<cmsc:portlet-preferences />

<mm:cloud>
	<mm:node number="${contentchannel}" jspvar="m" notfound="skip">
		<h2><mm:field name="name" /></h2>
	</mm:node>
</mm:cloud>
