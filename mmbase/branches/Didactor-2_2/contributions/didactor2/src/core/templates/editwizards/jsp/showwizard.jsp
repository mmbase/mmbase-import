<%--
  Little wrapper around 'wizard.jsp' from the editwizard application.
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<mm:import id="wizard" externid="wizard" jspvar="wizard"></mm:import>
<mm:import id="objectnumber" externid="objectnumber" jspvar="objectnumber"></mm:import>
<mm:import id="origin" externid="origin" jspvar="origin"></mm:import>

<mm:cloud method="delegate" jspvar="cloud">

<%--
  String url = "/mmapps/editwizard/jsp/wizard.jsp?wizard=data/didactor/lib/"; //+ wizard;
  url += "&objectnumber=" + objectnumber;
  url += "&referrer=/providers/educations/courses/dwizard/refresh.jsp";
  url += "&origin=" + origin;
  url += "&templates=/providers/educations/courses/dwizard/xsl";
--%>

<HTML>
<HEAD>
</HEAD>
<BODY>
  <script type="text/javascript" src="/education/wizards/mtmtrack.js">
  </script>
  <script>
    parent.frames['text'].location = "bla";
  </script>
  <img src="/providers/educations/courses/gfx/dwizard/loading.gif">
</BODY>
</HTML>
</mm:cloud>
