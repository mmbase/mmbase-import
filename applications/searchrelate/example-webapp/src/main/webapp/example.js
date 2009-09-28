$(document).ready(function() {

        { // prevent default of gui of images in search results

            // TODO I doubt if this works in sub-items.
            $("div.mm_related").bind("mmsrPaged",
                                     function (e, status, relater) {
                                         $(relater.div).find("a.mm_gui").removeAttr("onclick");
                                         $(relater.div).find("a.mm_gui").click(function(ev) {
                                                 ev.preventDefault();
                                             });
                                     }
                                     );
        }
    });
