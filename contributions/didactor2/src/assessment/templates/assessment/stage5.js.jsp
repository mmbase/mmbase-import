// -*- mode: JavaScript; -*-
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
    %><mm:content type="text/javascript">

$(window).unload(function() {

    // Save difficulties first.
    var params = {};

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
             url: "${mm:link('/assessment/stage5-editdifficulties.jspx')}",
             data: params,
             complete: function(req, textStatus) {

             }
           });

    // Then the test
    document.forms.questionform.command.value = 'done';

    postContent(document.forms.questionform.action, document.forms.questionform, false);

});

</mm:content>
