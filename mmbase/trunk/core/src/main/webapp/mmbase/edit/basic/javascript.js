
var validator;
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

    var nt = $("html head meta[name='MMBase-NodeType']");
    if (nt) {
        validator.prefetchNodeManager(nt.attr("content"));
    }
    validator.onLoad(ev);
});

