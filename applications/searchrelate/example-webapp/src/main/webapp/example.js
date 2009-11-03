$().ready(function() {
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

        if (! $("body").hasClass("search")) {
            MMBaseSearcher.prototype.implicitsAreHidden();
        }

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

        if ($("body").hasClass("tinymce")) {

            var tinyMceConfiguration = {
                theme : "simple",
                save_callback: function(ed) {
                    $("#" + ed).trigger("paste");
                },
                onchange_callback : function(ed) {
                    if (ed.isDirty()) {
                        ed.save();
                    }
                }
            }

            $(".mm_validate.mm_nm_news.mm_f_body").tinymce(tinyMceConfiguration);
            $("body").bind("mmsrCreated", function(ev, el) {
                    $(el).find(".mm_validate.mm_nm_news.mm_f_body").tinymce(tinyMceConfiguration);

                });

            // I'm starting to hate tinyMCE. It's a bit mistery when which function works, and how.
            $(document).bind("sortstart", function(ev) {
                    var el = ev.target;
                    console.log(ev);
                    tinyMCE.triggerSave();
                    var t = tinyMCE.editors;
                    for (var i in t){
                        console.log($(el).find("#" + i));
                        if ($(el).find("#" + i).length > 0) {
                            console.log(t[i]);
                            t[i].remove();
                            tinyMCE.remove(t[i]);
                        }
                    }
                });
        }


    });
