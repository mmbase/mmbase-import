
var validator;

if (typeof($) != 'undefined') {
    Widgets.instance.enumerationSuggestion("body.config select[name=mmjspeditors_uri]");
    $(document).ready(function(ev) {
            validator = new MMBaseValidator();
            validator.logEnabled = false;
            validator.traceEnabled = false;
            validator.validateHook = function() {
                var okbutton = document.getElementById('okbutton');
                if (okbutton != null) okbutton.disabled = this.invalidElements != 0;
                var savebutton = document.getElementById('savebutton');
                if (savebutton != null) savebutton.disabled = this.invalidElements != 0;
            }
            validator.lang = $("html head meta[name='MMBase-Language']").attr("content");
            validator.sessionName = $("html head meta[name='MMBase-SessionName']").attr("content");

            var nt = $("html head meta[name='MMBase-NodeType']").attr("content");
            if (nt != null && nt.length > 0) {
                validator.prefetchNodeManager(nt);
            }
            validator.onLoad(ev);

        });

}
