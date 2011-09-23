$(document).ready(function() {
        $(".sublistcreate").click(function() {
                var url = this.href;
                var ol = $(this).parents(".list").children("ol")[0];
                $.ajax({async: false, url: url, type: "GET", dataType: "xml", data: {},
                            complete: function(res, status) {
                            if ( status == "success" || status == "notmodified" ) {
                                var r = null;
                                try {
                                    r = document.importNode(res.responseXML.documentElement, true);
                                } catch (e) {
                                    // IE 6 sucks.
                                    r = $(res.responseText)[0];
                                }
                                var li = $("<li />");
                                $(li).append(r);
                                $(ol).children("li:last").after(li);
                            }
                        }
                    });
                return false;
            });
    });
