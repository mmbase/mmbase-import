   function objClick(el) {
      var href = el.parentNode.getAttribute("href")+"";
      if (href.length<10) 
         return;
   	  if (href.indexOf('javascript:') == 0) {
   	  	eval(href.substring('javascript:'.length, href.length));
   	  	return false;
   	  }

      document.location=href;
   }
   
   function objClickPopup(el, width, height) {
      var href = el.parentNode.getAttribute("href")+"";
      if (href.length<10) 
         return;
      if (width == undefined) {
        width = 500;
      }
      if (height == undefined) {
        height = 500;
      }

      var options = 'width='+width+',height='+height+',scrollbars=yes,resizable=yes'
      var w = window.open(href, 'viewItem', options);
      w.focus();
   }
   
   function objMouseOver(el) {
      objMouseOver(el, false);
   }

   function objMouseOver(el, detail) {
      el.className="itemrow-hover";
      if (detail) {
         el.previousSibling.className="itemrow-hover";
      }
   }

   function objMouseOut(el) {
     objMouseOut(el, false);
   }

   function objMouseOut(el, detail) {
      el.className="itemrow";
      if (detail) {
         el.previousSibling.className="itemrow";
      }
   }