function getToolTip(id, defaultValue) {
   if (typeof MyHTMLArea_I18N != "undefined") {
     return MyHTMLArea_I18N.tooltips[id];
   } else {
     return defaultValue;
   }
}

function customize(editor, editorURL) {
  var config = editor.config;
  config.editorURL = editorURL;
  _editor_url = editorURL;
  config.registerButton({
    id        : "my-validatesave",
    tooltip   : getToolTip("validatesave", "Validate The Form"),
    image     : editorURL + config.imgURL + "ed_validate_save.gif",
    textMode  : true,
    action    : myValidateSaveAction
  });
  config.registerButton({
    id        : "insertinlineimage",
    tooltip   : getToolTip("insertinlineimage","Insert Inline Image"),
    image     : editorURL + config.imgURL + "ed_image.gif",
    textMode  : false,
    action    : function(e) {e._insertInlineImage();}
  });
  config.registerButton({
    id        : "insertinlinelink",
    tooltip   : getToolTip("insertinlinelink","Insert Inline link"),
    image     : editorURL + config.imgURL +  "ed_link.gif",
    textMode  : false,
    action    : function(e) {e._insertInlineLink();}
  });

  config.toolbar = [
    ['bold', 'italic', 'underline', 'separator',
     'superscript', 'subscript','separator', 
     'formatblock', 'separator',
     'insertorderedlist', 'insertunorderedlist', 'separator',
     'cut', 'copy', 'paste', 'separator',
     'undo', 'redo', 'separator', 
     'insertinlineimage', 'inserttable', 'separator',
     'insertinlinelink', 'separator',
     'htmlmode', 'separator',
     'my-validatesave' 
    ]
  ];
  
   config.formatblock = ({
                "Heading 1": "h1",
                "Heading 2": "h2",
                "Heading 3": "h3",
                "Heading 4": "h4",
                "Normal": "p"
   });
  
  config.pageStyle="body, td {font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;color: #000000;font-size: 70%;}";
  config.pageStyle+="p {font-size: 100%;}";
  config.pageStyle+="h1 {font-weight: bold;font-size: 100%;}";
  config.pageStyle+="h2 {font-weight: bold;	color:#2222CC; font-size: 100%;	}";
  config.pageStyle+="h3 {font-weight: normal; color:#0000AA;	font-size: 100%; }";
  config.pageStyle+="h4 {font-weight: normal;	color:#000055;	font-size: 100%;}";
  config.pageStyle+="a {color: #0000FF; }";
}

myValidateSaveAction = function(editor) {
  updateValue(editor);
  // editwizard validation
  validator.validate(editor._textArea);
}

// overrides editwizard.jsp
function doCheckHtml() {
  if (HTMLArea.checkSupportedBrowser()) {
    for (var i = 0; i < htmlAreas.length; i++) {
      var editor = htmlAreas[i];
      updateValue(editor);
      // editwizard validation
      // It is possible to save a wizard when multiple htmlareas are not validated yet.
      if (requiresValidation(editor._textArea)) {
        validator.validate(editor._textArea);
      }
    }
  }
}

function updateValue(editor) {
  value = editor.getHTML()
  // These two lines could cause editors to complain about responsetime
  // when they leave a form with many large htmlarea fields.
  // this is the case when doCheckHtml() is called by the editwizard.jsp with 
  // doSave, doSaveOnly, gotoForm and doStartWizard
  value = wizardClean(value);
  value = clean(value);

  editor._textArea.value = value;

  if (editor._editMode == "wysiwyg") {
	if (HTMLArea.is_gecko) {
      // disable design mode before changing innerHTML
      try {
        editor._doc.designMode = "off";
      } catch(e) {}
	}
	editor._doc.body.innerHTML = value;
	if (HTMLArea.is_gecko) {
      // we need to refresh that info for Moz-1.3a
      try {
        editor._doc.designMode = "on";
      } catch(e) {}
	}
  }
}

function wizardClean(value) {
// editors in IE will maybe complain that it is very messy with 
// <strong> and <b> tags mixed when they edit, but without this function
// they would also do when others would use Gecko browsers.
// Now we are backwards compatible with the old editwizard wysiwyg and the
// frontend only has to deal with <b> and <i>

  //replace <EM> by <i>
  value = value.replace(/<([\/]?)EM>/gi, "<$1i>");
  value = value.replace(/<([\/]?)em>/gi, "<$1i>");
  //replace <STRONG> by <b>
  value = value.replace(/<([\/]?)STRONG>/gi, "<$1b>");
  value = value.replace(/<([\/]?)strong>/gi, "<$1b>");
  //replace <BR> by <BR/>
  value = value.replace(/<BR>/gi, "<br/>");
  value = value.replace(/<br>/gi, "<br/>");
  
  return value;
}

function clean(value) {
  // Remove all SPAN tags
  value = value.replace(/<\/?SPAN[^>]*>/gi, "" );
  value = value.replace(/<\/?span[^>]*>/gi, "" );
  // Remove Class attributes
  value = value.replace(/<(\w[^>]*) class=([^ |>]*)([^>]*)/gi, "<$1$3");
  // Remove Style attributes
  value = value.replace(/<(\w[^>]*) style="([^"]*)"([^>]*)/gi, "<$1$3");
  // Remove Lang attributes
  value = value.replace(/<(\w[^>]*) lang=([^ |>]*)([^>]*)/gi, "<$1$3");
  // Remove XML elements and declarations
  value = value.replace(/<\\?\?xml[^>]*>/gi, "");
  // Remove Tags with XML namespace declarations: <o:p></o:p>
  value = value.replace(/<\/?\w+:[^>]*>/gi, "");
  // Replace the &nbsp;
  value = value.replace(/&nbsp;/, " " );
  
  return value;
}

function plainText(text) {
  var text = HTMLEncode(text);
  text = text.replace(/\n/g,'<BR>');
  return text;
}

function HTMLEncode(text) {
  text = text.replace(/&/g, "&amp;") ;
  text = text.replace(/"/g, "&quot;") ;
  text = text.replace(/</g, "&lt;") ;
  text = text.replace(/>/g, "&gt;") ;
  text = text.replace(/'/g, "&#146;") ;

  return text ;
}

// Called when the user clicks the Insert Table button
HTMLArea.prototype._insertTable = function() {
        var sel = this._getSelection();
        var range = this._createRange(sel);
        var editor = this;	// for nested functions
        this._popupDialog("insert_table.html", function(param) {
                if (!param) {	// user must have pressed Cancel
                        return false;
                }
                var doc = editor._doc;
                // create the table element
                var table = doc.createElement("table");
                table.border = 1; // Is removed by WordFilter, but handy for first entering content!
                // assign the given arguments
                //for (var field in param) {
                  //      var value = param[field];
                    //    if (!value) {
                      //          continue;
                        //}
                        
                       // cjr: don't want any attributes in tables
                        /* switch (field) {                        
                            case "f_width"   : table.style.width = value + param["f_unit"]; break;
                            case "f_align"   : table.align	 = value; break;
                            case "f_border"  : table.border	 = parseInt(value); break;
                            case "f_spacing" : table.cellspacing = parseInt(value); break;
                            case "f_padding" : table.cellpadding = parseInt(value); break;
                        }*/
                         
               // }
                table.border=1;
                // Write head row
                if (param["f_caption"]) {
	                var caption = doc.createElement("caption");
    	                table.appendChild(caption);
        	        caption.appendChild(doc.createTextNode(param["f_caption"]));
                  }
        	    if (param["f_header"]) {
	                var thead = doc.createElement("thead");
    	            table.appendChild(thead);
        	        var tr = doc.createElement("tr");
            	    thead.appendChild(tr);   
                	for (var j = 0; j < param["f_cols"]; ++j) {
                    	var td = doc.createElement("th");
                     	tr.appendChild(td);
                     	// Mozilla likes to see something inside the cell.
                     	(HTMLArea.is_gecko) && td.appendChild(doc.createElement("br"));
                	}
                }
                var tbody = doc.createElement("tbody");
                table.appendChild(tbody);
                for (var i = 0; i < param["f_rows"]; ++i) {
                        var tr = doc.createElement("tr");
                        tbody.appendChild(tr);
                        for (var j = 0; j < param["f_cols"]; ++j) {
                                var td = doc.createElement("td");
                                tr.appendChild(td);
                                // Mozilla likes to see something inside the cell.
                                (HTMLArea.is_gecko) && td.appendChild(doc.createElement("br"));
                        }
                }
                if (HTMLArea.is_ie) {
                        range.pasteHTML(table.outerHTML);
                } else {
                        // insert the table
                        editor.insertNodeAtSelection(table);
                }
                return true;
        }, null);
};

HTMLArea.prototype._insertInlineLink = function(link) {
        var editor = this;
        var outparam = null;
        if (typeof link == "undefined") {
                link = this.getParentElement();
                if (link && !/^a$/i.test(link.tagName))
                        link = null;
        }
        if (link) outparam = {
                f_href   : HTMLArea.is_ie ? editor.stripBaseURL(link.href) : link.getAttribute("href"),
                f_destination : HTMLArea.is_ie ? link.destination : link.getAttribute("destination"),
                f_title  : link.title,
                f_target : link.target
        };
        this._popupDialog("insertinline_link.html", function(param) {
                if (!param)
                        return false;
                var a = link;
                if (!a) {
                        editor._doc.execCommand("createlink", false, param.f_href);
                        a = editor.getParentElement();
                        var sel = editor._getSelection();
                        var range = editor._createRange(sel);
                        if (!HTMLArea.is_ie) {
                                a = range.startContainer;
                                if ( ! ( /^a$/i.test(a.tagName) ) ) {
                                      a = a.nextSibling;
                                      if ( a === null ) {
                                            a = range.startContainer.parentNode;
                                      }
                                }
                        }
                } else a.href = param.f_href.trim();

                a.title = param.f_title.trim();
                HTMLArea.is_ie ? a.destination = param.f_destination.trim() : a.setAttribute("destination", param.f_destination.trim());
                a.target = param.f_target.trim();
                editor.selectNodeContents(a);
                editor.updateToolbar();
  }, outparam, "width=398,height=220");
};

HTMLArea.prototype._insertInlineImage = function(image) {
        var editor = this;	// for nested functions
        var outparam = null;
        if (typeof image == "undefined") {
                image = this.getParentElement();
                if (image && !/^img$/i.test(image.tagName))
                        image = null;
        }
        if (image) outparam = {
                f_url    : HTMLArea.is_ie ? editor.stripBaseURL(image.src) : image.getAttribute("src"),
                f_alt    : image.alt,
                f_border : image.border,
                f_align  : image.align,
                f_width  : image.width,
                f_height : image.height,
                f_destination : HTMLArea.is_ie ? image.destination : image.getAttribute("destination")
        };
        this._popupDialog("insertinline_image.html", function(param) {
                if (!param) {	// user must have pressed Cancel
                        return false;
                }
                var img = image;
                if (!img) {
                  if ( HTMLArea.is_ie ) {
                    var sel = editor._getSelection();
                    var range = editor._createRange(sel);
                    editor._doc.execCommand("insertimage", false, param.f_url);
                    img = range.parentElement();
                    // wonder if this works...
                    if ( img.tagName.toLowerCase() != "img" ) {
                      img = img.previousSibling;
                    }
                  }
                  else {
                    img = document.createElement('img');
                    img.src = param.f_url;
                    editor.insertNodeAtSelection(img);
                    if ( !img.tagName ) {
                      // if the cursor is at the beginning of the document
                      img = range.startContainer.firstChild;
                    }
                  }
                } else {
                  img.src = param.f_url;
                }
                for (field in param) {
                        var value = param[field];
                        switch (field) {
                            case "f_alt"    :  img.alt = value; img.title = value; break;
                            case "f_border" :  HTMLArea.is_ie ? img.border = parseInt(value || "0") : img.setAttribute("border", parseInt(value || "0")); break;
                            case "f_align"  :  HTMLArea.is_ie ? img.align = value : img.setAttribute("align", value); break;
                            case "f_width"  :  HTMLArea.is_ie ? img.width = parseInt(value || "100") : img.setAttribute("width", parseInt(value || "100")); break;
                            case "f_height"  :  HTMLArea.is_ie ? img.height = parseInt(value || "100") : img.setAttribute("height", parseInt(value || "100")); break;
                            case "f_destination"  : HTMLArea.is_ie ? img.destination = value : img.setAttribute("destination", value); break;
                        }
                }
        }, outparam);
};
