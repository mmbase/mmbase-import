<%@include file="../../layout/demo/includes/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<mm:content type="text/plain" encoding="UTF-8">
  <cmsc:location var="cur" sitevar="site" />
	<html xmlns="http://www.w3.org/1999/xhtml" lang="${site.language}" xml:lang="${site.language}">
	<cmsc:screen>
		<head>
		<title>${cur.title}</title>
		      <cmscf:editresources />
		</head>
		<body>
<cmsc:insert-portlet layoutid="column1_1" />
<cmsc:insert-portlet layoutid="column1_2" />
<cmsc:insert-portlet layoutid="column1_3" />
		</body>
	</cmsc:screen>
	</html>
</mm:content>
