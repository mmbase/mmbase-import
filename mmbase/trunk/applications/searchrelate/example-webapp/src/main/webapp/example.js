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


        if ($("body").hasClass("tinymce")) {

            var tinyMceConfiguration = {
                theme : "simple",
                remove_trailing_nbsp : true,
                entity_encoding : "raw",
                verify_html : true,
                setup : List.prototype.setupTinyMCE
            }

            $(".mm_validate.mm_nm_news.mm_f_body").each(function() {
                    List.prototype.tinymce(this, tinyMceConfiguration);
                });
            $(document).bind("mmsrCreated", function(ev, el) {
                    $(el).find(".mm_validate.mm_nm_news.mm_f_body").each(function() {
                            List.prototype.tinymce(this, tinyMceConfiguration);
                        });
                });

        }


    });
