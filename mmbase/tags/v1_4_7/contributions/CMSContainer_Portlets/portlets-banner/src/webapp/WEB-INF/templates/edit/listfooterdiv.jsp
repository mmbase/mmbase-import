<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<c:if test="${empty orderby}">
	<script type="text/javascript">
	function <portlet:namespace />reorder() {
		var sortOptions = {
			tag: 'div',
			handle:'handle',
			onUpdate : function() { 
				var ajaxOptions = {
					onComplete : function(request) { new Effect.Highlight('<portlet:namespace />list',{}); },
					parameters : 'action=reorderpartial&parent=${contentchannel}&offset=${offset}&direction=${direction}&'
								 + Sortable.serialize('<portlet:namespace />list', {name: 'ids'} ),
					evalScripts : true,
					asynchronous : true
				};
				new Ajax.Request('<mm:url page="/editors/repository/ReorderAction.do" />', ajaxOptions );
			}
		};
		Position.includeScrollOffsets = true;
		Sortable.create('<portlet:namespace />list', sortOptions );
	}
	<portlet:namespace />reorder();
	</script>
</c:if>