// -*- mode: JavaScript; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
    %><mm:content type="text/javascript">



var commitDifficulties = function () {
    // Save difficulties first.
    var params = {};

    params['submit'] = true;

    $("#difficulties input[type='hidden']").each(function() {
        params[this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;

    });
    $("#difficulties input").each(function() {
        if (this.checked || this.type == 'text' || this.type == 'hidden' || this.type == 'password') {
            params[this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;
        }
    });
    $("#difficulties option").each(function() {
        if (this.selected) {
            params[this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;
        }
    });
    $("#difficulties textarea").each(function() {
        params[this.name || this.id || this.parentNode.name || this.parentNode.id ] = this.value;
    });

    $.ajax({ type: "POST",
             async: false,
             url: "${mm:link('/assessment/stage2-editdifficulties.jspx')}",
             data: params,
             complete: function(req, textStatus) {

             }
           });
}

var commitTest = function () {
    // Then the test
    if (document.forms.questionform) {
        document.forms.questionform.command.value = 'done';
        postContent(document.forms.questionform.action, document.forms.questionform, false);
    }
}

// Using beforeunload rather then unload to avoid synchorinization issues. If e.g. you click back to overview, this must not be started before caches are invalidated
$(window).bind('beforeunload', function () {
    commitDifficulties();
    commitTest();
});

</mm:content>
