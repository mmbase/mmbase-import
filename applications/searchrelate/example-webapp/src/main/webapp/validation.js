
$(document).ready(function() {
        $("form.mm_form").bind("mmsrValidateHook", function(ev, list, valid, reason) {
                if (valid) {
                    $(".info").text("Form is valid");
                } else {
                    $(".info").text("Form is invalid: " + reason);
                }
                if (valid) {
                    $("input[name=submit]").removeAttr("disabled");
                } else {
                    $("input[name=submit]").attr("disabled", "disable");
                }
            });
    });
