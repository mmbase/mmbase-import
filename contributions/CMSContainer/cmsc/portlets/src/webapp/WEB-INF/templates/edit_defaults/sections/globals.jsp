<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<script type="text/javascript">

	function selectElement(contentelement, title) {
		document.forms['<portlet:namespace />form'].contentelement.value = contentelement;
		document.forms['<portlet:namespace />form'].contentelementtitle.value = title;
	}

	function selectPage(page, path, positions) {
		document.forms['<portlet:namespace />form'].page.value = page;
		document.forms['<portlet:namespace />form'].pagepath.value = path;
		
		var selectWindow = document.forms['<portlet:namespace />form'].window;
		for (var i = selectWindow.options.length -1 ; i >=0 ; i--) {
			selectWindow.options[i] = null;
		}
		for (var i = 0 ; i < positions.length ; i++) {
			var position = positions[i];
			selectWindow.options[selectWindow.options.length] = new Option(position, position);
		}
	}

   function selectRelatedpage(relatedPage, relatedPagepath, positions) {
      document.forms['<portlet:namespace />form'].relatedPage.value = relatedPage;
      document.forms['<portlet:namespace />form'].relatedPagepath.value = relatedPagepath;
      
      var selectWindow = document.forms['<portlet:namespace />form'].relatedWindow;
      for (var i = selectWindow.options.length -1 ; i >=0 ; i--) {
         selectWindow.options[i] = null;
      }
      for (var i = 0 ; i < positions.length ; i++) {
         var position = positions[i];
         selectWindow.options[selectWindow.options.length] = new Option(position, position);
      }
   }

	function erase(field) {
		document.forms['<portlet:namespace />form'][field].value = '';
	}

	function eraseList(field) {
		document.forms['<portlet:namespace />form'][field].selectedIndex = -1;
	}

	function selectChannel(channel, path) {
		document.forms['<portlet:namespace />form'].contentchannel.value = channel;
		document.forms['<portlet:namespace />form'].contentchannelpath.value = path;
	}

	var repositoryUrl = "<cmsc:staticurl page='/editors/repository/index.jsp'/>";

	function openRepositoryWithChannel() {
		contentchannel = document.forms['<portlet:namespace />form'].contentchannel.value;
		if(contentchannel == undefined || contentchannel == '') {
			alert('<fmt:message key="edit_defaults.preview.noChannel"/>');
		}
		else {
			if(confirm('<fmt:message key="edit_defaults.preview.loseChanges"/>')) {
				window.top.bottompane.location = repositoryUrl + '?channel=' + contentchannel;
			}
		}
	}

</script>