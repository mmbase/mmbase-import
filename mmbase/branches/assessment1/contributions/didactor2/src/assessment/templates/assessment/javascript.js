      function toggle(number) {
      if( document.getElementById("toggle_div" + number).style.display=='none' ){
      document.getElementById("toggle_div" + number).style.display = '';
         document.getElementById("toggle_image" + number).src = "<mm:treefile page="/assessment/gfx/minus.gif"
         objectlist="$includePath" />";
         } else {
         document.getElementById("toggle_div" + number).style.display = 'none';
         document.getElementById("toggle_image" + number).src = "<mm:treefile page="/assessment/gfx/plus.gif"
         objectlist="$includePath" />";
         }
         }

         function toggleAll(image,number) {
         var toggles = number.split(",");
      if( document.getElementById("toggle_div" + toggles[0]).style.display=='none' ){
        for (i=0;i<toggles.length;i++) {
          document.getElementById("toggle_div" + toggles[i]).style.display = '';
        }
        document.getElementById("toggle_image" + image).src = "<mm:treefile page="/assessment/gfx/minus.gif"
                                  objectlist="$includePath" />";
      } else {
        for (i=0;i<toggles.length;i++) {
          document.getElementById("toggle_div" + toggles[i]).style.display = 'none';
        }
        document.getElementById("toggle_image" + image).src = "<mm:treefile page="/assessment/gfx/plus.gif"
                                  objectlist="$includePath" />";
      }
    }
    function doAction(prompt) {
    var conf;
    if (prompt && prompt!="") {
       conf = confirm(prompt);
    }
    else
      conf=true;
      return conf;
    }
