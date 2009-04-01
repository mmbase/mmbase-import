<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <link rel="icon" href="images/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" /><%@
taglib uri="http://www.mmbase.org/mmbase-taglib-2.0"  prefix="mm"
%><%@include file="page_base_functionality.jsp"
%><mm:import id="style">
    <link rel="StyleSheet" type="text/css" href="css/<mm:write referid="config.style_sheet" />"/>
    <jsp:directive.include file="/mmbase/validation/javascript.jspxf" />
  <script type="text/javascript">
    if (typeof(MMBaseValidator) != "undefined") {
    var validator = new MMBaseValidator();
    validator.logEnabled = false;
    validator.traceEnabled = false;
    validator.sessionName = '${config.session}';
    validator.validateHook = function() {
       var okbutton = document.getElementById('okbutton');
       if (okbutton != null) okbutton.disabled = this.invalidElements != 0;
       var savebutton = document.getElementById('savebutton');
       if (savebutton != null) savebutton.disabled = this.invalidElements != 0;
    }
    validator.lang = '${config.lang}';
    validator.setup(document);
    }
  </script>
</mm:import>
