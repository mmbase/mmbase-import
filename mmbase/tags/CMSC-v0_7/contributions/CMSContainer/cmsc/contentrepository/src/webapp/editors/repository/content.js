   function callEditWizard(ce_id) {
      var url = '../WizardInitAction.do?objectnumber=' + ce_id;
      url += '&returnurl=' + escape(document.location);
      document.location = url;
   }

    function unpublish(channelnumber, objectnumber) {
       var url = "LinkToChannelAction.do";
       url += "?channelnumber=" + channelnumber;
       url += "&action=unlink";
       url += "&returnurl=" + escape(document.location);
       url += "&objectnumber=" + objectnumber;

       document.location.href = url;
    }

   function openPreview(url) {
      return openPopupWindow("preview", 800, 800, url);
   }

   function showChannels(objectnumber) {
      return openPopupWindow("showChannels", 500, 200, 'showchannels.jsp?objectnumber=' + objectnumber);
   }
   
   function showItem(objectnumber) {
      return openPopupWindow("showItem", 500, 500, 'showitem.jsp?objectnumber=' + objectnumber);
   }