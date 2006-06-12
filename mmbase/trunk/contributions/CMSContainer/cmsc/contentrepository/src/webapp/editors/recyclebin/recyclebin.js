   function restore(objectnumber) {
       var url = "RestoreAction.do";
       url += "?objectnumber=" + objectnumber;
       url += "&returnurl=" + escape(document.location);

       document.location.href = url;
    }
    
    function permanentDelete(objectnumber, message) {
       if (confirm(message)) {
          var url = "DeleteAction.do";
          url += "?objectnumber=" + objectnumber;
          url += "&returnurl=" + escape(document.location);

          document.location.href = url;
       }
    }
    
    function deleteAll(message) {
       return confirm(message);
    }