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
                verify_html : true,
                setup : function(ed) {
                    var activeEditor = null;

                    var remove = function(ed) {
                        if (ed.isDirty()) {
                            ed.save();
                        }
                        var textarea = $("#" + ed.editorId);
                        var prev = textarea.prev();
                        ed.remove();
                        textarea.hide();
                        prev.empty().append(textarea.val());
                        prev.css("display", "inline-block");

                    }
                    $("body").mousedown(function(ev) {
                            if ($(ev.target).parents("span.mceEditor").length > 0) {

                            } else {
                                if (activeEditor != null) {
                                    remove(activeEditor);
                                    activeEditor = null;
                                }
                            }
                        });
                    var activate = function(ed) {
                        if (activeEditor != null && activeEditor != ed) {
                            remove(activeEditor);
                        } else {

                            activeEditor = ed;
                        }
                    }
                    ed.onActivate.add(function(ed) { activate(ed); });
                    ed.onNodeChange.add(function(ed) { activate(ed); });
                    ed.onMouseDown.add(function(ed) { activate(ed); });
                    ed.onSaveContent.add(function(ed) {
                            $("#" + ed.editorId).trigger("paste");
                        });
                }
            }

            $(".mm_validate.mm_nm_news.mm_f_body").each(function() {
                    var self = $(this);
                    self.originalDisplay = self.css("display");
                    var val = $("<div class='mm_tinymce' />");
                    val.append(self.val());
                    val.height(self.height());


                    self.before(val).hide();

                    val.click(function(ev) {
                            self.css("display", val.css("display"));
                            val.hide();
                            self.tinymce(tinyMceConfiguration);

                        });
                });

        }


    });
