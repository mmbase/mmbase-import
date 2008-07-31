/* -*- mode: java -*- */
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><mm:content  expires="0" type="text/javascript">
    <mm:cloud>

// hmm

function toggle(number) {
    $(".toggle_div" + number + ",#toggle_div" + number).toggle();
    var img = $("#toggle_image" + number)[0];
    var org = img.src;
    var src = img.src;
    src = src.replace("minus", "plus");
    if (src == org) {
	src = src.replace("plus", "minus");
    }
    img.src = src;

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
    } else {
	conf = true;
    }
    return conf;
}
</mm:cloud>
</mm:content>
