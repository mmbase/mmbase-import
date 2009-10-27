$(document).ready(function() {
        {
            // prevent default of gui of images in search results
            $(document).bind("mmsrPaged",
                             function (e, status, relater) {
                                 $(relater.div).find("a.mm_gui").removeAttr("onclick");
                                 $(relater.div).find("a.mm_gui").click(function(ev) {
                                         ev.preventDefault();
                                     });
                             }
                             );
        }

        MMBaseSearcher.prototype.implicitsAreHidden();

        {
            $(document).bind("mmsrStartSave",
                             function (e, list) {
                                 //console.log("Starting save for " + list.rid);
                             }
                             );

            $(document).bind("mmsrFinishedSave",
                             function (e, list) {
                                 //console.log("Finished saving for " + list.rid);
                             }
                             );
        }

    });
