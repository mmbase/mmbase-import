
$(document).ready(
    function() {
        $("form.mm_form").bind("mmsrValidateHook",
                               function(ev, list, valid, reason) {
                                   var formInvalid = $(ev.target).find("div.list.invalid");
                                   if (valid) {
                                       $(".info").text("Form is valid");
                                   } else {
                                       $(".info").text("Form is invalid: " + reason);
                                   }
                                   if (formInvalid.length == 0) {
                                       $("input[name=submit]").removeAttr("disabled");
                                   } else {
                                       $("input[name=submit]").attr("disabled", "disable");
                                   }
                               });
        $(document).bind("mmsrValidateHook",
                         function(ev) {
                             //                             console.log(ev);
                         });
    });
