/**
 * Implicitely and temporary checks/uploads binaries.
 * Based on ajax-fileupload
 * (jquery-form is too hard to use, because it requires an entire
 * form to be submitted).
 *  Useage:
 *   Create an instance, and call it when the upload must happen.
 *   - in the mmValidate event if you use validation, or otherwise in the the change event
 */
/*
<%@page contentType="text/javascript; charset=UTF-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><fmt:bundle basename="org.mmbase.resources.resources">
<mm:content type="text/javascript" expires="0">
*/


function MMUploader() {
    // Currently running uploads
    this.uploading = {};
    this.uploadingSize = 0;
    this.statusElement = null;
    this.uid = "";
    this.transaction = null;
    this.validator = null;

}

MMUploader.prototype.status = function(message, fadeout) {
    var el = this.statusElement;
    if (el != null) {
        if (el.originalTextContent == null) el.originalTextContent = el.textContent;
        $(el).fadeTo("fast", 1);
        $(el).empty();
        $(el).append(message);
        if (fadeout) {
            var p = el;
            $(el).fadeTo(4000, 0.1,
                         function() {
                             $(p).empty(); $(p).append(p.originalTextContent); }
                        );
        }
  }
};


MMUploader.prototype.uploadProgress = function(fileid) {
    if (this.statusElement != null) {
        if (this.uploading[fileid]) {
            $(this.statusElement).load("${mm:link('/mmbase/upload/progress.jspx')}");
        }
    }
};

/**
 * Given an input[type=file], returns the node number which is represented in it.
 */
MMUploader.prototype.getNodeForInput  = function(input) {
    var classesString = $(input).attr("class");
    if (classesString != null) {
        var classes = classesString.split(' ');
        for (var i in classes) {
            var cl = classes[i];
            if (cl.indexOf("mm_n_") == 0) {
                return cl.substring("mm_n_".length);
            }
        }
    }
    return null;
};


MMUploader.prototype.upload = function(fileid) {
    var self = this;
    if (self.uploading[fileid]) {
        // uploading already
        return;
    }

    var fileItem = $("#" + fileid);
    if (fileItem.length == 0) {
        throw "No fileitem " + fileid;
    }
    if (fileItem[0].type != 'file') {
        // not a fileitem
        return;
    }
    if (fileItem.val().length == 0) {
        // no file selected;
        return;
    }

    self.uploading[fileid] = true;
    self.uploadingSize++;

    // Remember event-handlers.
    var events = fileItem.data('events');

    var node = self.getNodeForInput(fileItem[0]);
    var progress = function() {
        self.uploadProgress(fileid);
        if (self.uploading[fileid]) {
            setTimeout(progress, 1000);
        }
    };
    //progress();
    if (self.validator != null) {
        self.validator.removeValidationFromElement(fileItem[0]);
    } else {
        //console.log("Validator not set");
    }
    var classes = $(fileItem).attr("class").split(' ');
    for (var i in classes) {
        var clazz = classes[i];
        if (clazz.indexOf("mm_length_") == 0) {
            $(fileItem).removeClass(clazz);
            break;
        }
    }

    $.ajaxFileUpload ({
            url: "${mm:link('/mmbase/upload/upload.jspx')}" + "?uid=" + self.uid + "&name=" + fileItem.attr("name") + "&n=" + node + "&transaction=" + self.transaction,
            secureuri: false,
            fileElementId: fileid,
            dataType: 'xml',
            success: function (data, status) {
                if(typeof(data.error) != 'undefined') {
                    if(data.error != '') {
                        alert(data.error);
                    } else {
                        alert(data.msg);
                    }
                } else {
                    try {
                        var fileItem = $("#" + fileid);
                        fileItem.val(null);
                        fileItem.prevAll(".mm_gui").remove();
                        fileItem.prevAll("input[type=hidden]").remove();

                        var newLength = $(data).find("div.length").text();
                        $(fileItem).addClass("mm_length_" + newLength);

                        var created = $(data).find("div.fieldgui .mm_gui, div.fieldgui input[type=hidden]");
                        fileItem.before(created);
                        var name = $(fileItem).attr("name");
                        if (! name.indexOf("MMU_") == 0) {
                            name = "MMU_" + name;
                            $(fileItem).attr("name", name);
                        }


                        // Rebind  event handlers:
                        for (var event in events) {
                            for (var key in events[event]) {
                                $(fileItem).bind(event, events[event][key]);
                            }
                        }
                        if (self.validator != null) {
                            self.validator.addValidationForElements(fileItem);
                        }


                    } catch (e) {
                        alert(e);
                    }

                }
                delete self.uploading[fileid];
                self.uploadingSize--;
                self.status('<fmt:message key="uploaded" />', true);
            },
            error: function (data, status, e) {
                alert(e);
                delete self.uploading[fileid];
                self.uploadingSize--;
            }
        }
        );
    return;
};
/*
</mm:content></fmt:bundle>
*/
