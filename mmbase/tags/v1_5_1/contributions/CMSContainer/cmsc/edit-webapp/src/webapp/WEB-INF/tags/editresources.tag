<%@taglib uri="http://finalist.com/cmsc" prefix="cmsc"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cmsc:protected>
<c:set var="clientLocale" value="${pageContext.request.locale}" />
	<script type="text/javascript" src="<cmsc:staticurl page='/js/window.js' />"></script>
	<script type="text/javascript" src="<cmsc:staticurl page='/js/prototype.js'/>"></script>
	<script type="text/javascript" src="<cmsc:staticurl page='/editors/site/portletediting.js.jsp'/>"></script>
	<script type="text/javascript" src="<cmsc:staticurl page='/js/scriptaculous/scriptaculous.js'/>"></script>
	<script type="text/javascript">
		_editor_url = '<cmsc:staticurl page="/mmbase/edit/wizard/xinha/" />';
		_editor_lang = '<c:out value="${clientLocale.language}" />';
	</script>

	<script type="text/javascript" src="<cmsc:staticurl page='/mmbase/edit/wizard/xinha/htmlarea.js' />"></script>
	<script type="text/javascript" src="<cmsc:staticurl page='/mmbase/edit/wizard/xinha/my-htmlarea.js' />"></script>
	<script type="text/javascript">
		function createHTMLArea(name) {
          var editor_names = [ name ];
          var xinha_plugins = createDefaultPlugins;
          var xinha_config = createDefaultConfig();
		  var editor = HTMLArea.makeEditors(editor_names , xinha_config, xinha_plugins);
          HTMLArea.startEditors(editor);
		}
	</script>

	<script type="text/javascript" src="<cmsc:staticurl page='/editors/utils/localeditor.js'/>"></script>

	<link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/editors/site/portaledit.css'/>" />
	<link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/mmbase/edit/wizard/xinha/htmlarea.css'/>" />
</cmsc:protected>