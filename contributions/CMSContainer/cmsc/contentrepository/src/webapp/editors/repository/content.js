   function callEditWizard(objectNumber) {
      var url = '../WizardInitAction.do?objectnumber=' + objectNumber;
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
      return openPopupWindow("preview", 750, 550, url);
   }

   function showItem(objectnumber) {
      return openPopupWindow("showItem", 500, 500, 'showitem.jsp?objectnumber=' + objectnumber);
   }
   
   function info(objectNumber) {
      openPopupWindow("info", 500, 500, "../repository/showitem.jsp?objectnumber=" + objectNumber);
   }
   
   function moveUp(objectNumber, channel) {
      move("up", objectNumber, channel);
   }
   
   function moveDown(objectNumber, channel) {
      move("down", objectNumber, channel);
   }
   
   function move(direction, objectNumber, channel) {
      var url = 'MoveContent.do?direction='+direction+'&objectnumber=' + objectNumber+'&parentchannel='+channel;
      document.location = url;
   }
   