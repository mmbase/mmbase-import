<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<mm:cloud>
<mm:import externid="channel" required="true" />
<html>
<head>
<script language="JavaScript">
    window.setInterval("call()", 10000, "JavaScript");
    function call() {
        location="chatwho.jsp?channel=<mm:write referid="channel"/>";
    }
</script>
</head>
<body>
Who's online:<br>
<mmcommunity:who channel="${channel}" >
 <mm:field name="gui()" /><br />
</mmcommunity:who>
</mm:cloud>
</body>
</html>
