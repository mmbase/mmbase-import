$().ready(function() {
        {   // prevent default of gui of images in search results
            $(document).bind("mmsrPaged",
                             function (e, status, relater) {
                                 $(relater.div).find("a.mm_gui").removeAttr("onclick");
                                 $(relater.div).find("a.mm_gui").click(function(ev) {
                                         ev.preventDefault();
                                     });
                             }
                             );
        }

        {   // default 'implicit' search results are not shown,
            // except on the 'search' example page itself
            if (! $("body").hasClass("search")) {
                if (typeof MMBaseSearcher == "function") {
                    MMBaseSearcher.prototype.implicitsAreHidden();
                }
            }
        }


        {  // Setup tinymcy on the example page for that.
            if ($("body").hasClass("tinymce")) {


                var tinyMceConfiguration = {
                    theme : "simple",
                    content_css: "style.css",
                    entity_encoding : "raw", /* needed when XHTML */
                    setup : List.prototype.setupTinyMCE
                }

                // bind tinymce to news bodies already displayed:
                $(".mm_validate.mm_nm_news.mm_f_body").each(function() {
                        List.prototype.tinymce(this, tinyMceConfiguration);
                    });
                // and of course, also to new ones.
                $(document).bind("mmsrCreated", function(ev, el) {
                        $(el).find(".mm_validate.mm_nm_news.mm_f_body").each(function() {
                                List.prototype.tinymce(this, tinyMceConfiguration);
                            });
                    });

            }
        }


    });
