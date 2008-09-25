   function restore(objectnumber, offset, url) {
       url = "RestoreAction.do";
       url += "?objectnumber=" + objectnumber;
       url += "&returnurl=" + escape(document.location);
       url += "&offset=" + offset;

       document.location.href = url;
    }
    
   function info(objectNumber) {
      var url = "../repository/showitem.jsp";
      url += "?objectnumber=" + objectNumber;
      
      var options = 'width=500,height=500,scrollbars=yes,resizable=yes'
      var w = window.open(url, 'viewItem', options);
      w.focus();
    }
    
    function permanentDelete(objectnumber, message, offset) {
       if (confirm(message)) {
          var url = "DeleteAction.do";
          url += "?objectnumber=" + objectnumber;
          url += "&returnurl=" + escape(document.location);
	      url += "&offset=" + offset;

          document.location.href = url;
       }
    }
    
    function deleteAll(message) {
       if(confirm(message)) {
       		document.forms["deleteForm"].submit();
       }
    }    
	function refreshChannels() {
		refreshFrame('channels');
		if (window.opener) {
			window.close();
		}
	}