<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="channelrefresh.title" /></title>
		<link href="../style.css" type="text/css" rel="stylesheet"/>
		<script type="text/javascript" src="../utils/window.js"></script>
		<script type="text/javascript">
			function refreshChannels() {
				refreshFrame('channels');
				if (window.opener) {
					window.close();
				}
			}
		</script>
	</head>
	<body onload="refreshChannels()"></body>
</html:html>