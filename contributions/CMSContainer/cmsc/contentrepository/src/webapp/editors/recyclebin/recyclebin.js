   function restore(objectnumber, offset, type, url) {
       if(type=="Attachment"||type=="Image"||type=="URL"){
          url = "RestoreAssetAction.do";
       }
       else{
          url = "RestoreAction.do";
       }
       url += "?objectnumber=" + objectnumber;
       url += "&returnurl=" + escape(document.location);
       url += "&offset=" + offset;
       document.location.href = url;
    }
    
   function info(objectNumber, type) {
      var url;
	   if(type=="Attachment"||type=="Image"||type=="URL"){  
		   url = '../resources/';
		   url += type.toLowerCase();
		   url += 'info.jsp?objectnumber=';
		   url += objectNumber;
		   openPopupWindow('imageinfo', '900', '500', url);
	   }
	   else{
	      url = "../repository/showitem.jsp";
	      url += "?objectnumber=" + objectNumber;
	      
	      var options = 'width=500,height=500,scrollbars=yes,resizable=yes'
	      var w = window.open(url, 'viewItem', options);
	      w.focus();
	   }
    }
    
    function permanentDelete(objectnumber, message, offset, type) {
       if (confirm(message)) {
          var url = "DeleteAction.do";
          url += "?objectnumber=" + objectnumber;
          url += "&returnurl=" + escape(document.location);
	      url += "&offset=" + offset;
	      url += "&type=" + type;

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