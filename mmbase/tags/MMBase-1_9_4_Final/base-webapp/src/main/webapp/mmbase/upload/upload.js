$(document).ready(function() {
    var progressUrl = $("head meta[name=ContextRoot]").attr("content") + "/mmbase/upload/progress.jspx";
    $("form[enctype=multipart/form-data]").each(function() {

        $(this).submit(function() {
            var form = this;
            var progress = function() {
                $(form).find(".progressInfo").each(function() {
                    $(this).load(progressUrl);
                });
                setTimeout(progress, 1000);
            };
            progress();
        });
    });
});
