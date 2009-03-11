xinha_editors = null;
xinha_init    = null;


xinha_init = xinha_init ? xinha_init : function() {
  var xinha_plugins = [
   'CharacterMap',
   'ContextMenu',
//   'ListType',
//   'FullScreen',
//   'SpellChecker',
//   'Stylist',
//   'SuperClean',
   'TableOperations'
  ];
  // THIS BIT OF JAVASCRIPT LOADS THE PLUGINS, NO TOUCHING  :)
  if(!HTMLArea.loadPlugins(xinha_plugins, xinha_init)) return;
  xinha_config = createDefaultConfig();
  xinha_editors = HTMLArea.makeEditors(xinha_editors, xinha_config, xinha_plugins);
  HTMLArea.startEditors(xinha_editors);
}

createDefaultConfig = function() {
  var xinha_config = xinha_config ? xinha_config() : new HTMLArea.Config();
  xinha_config.registerButton({
    id        : "my-validatesave",
    tooltip   : HTMLArea._lc("Controleer de html"),
    image     : _editor_url + xinha_config.imgURL +  "ed_validate_save.gif",
    textMode  : true,
    action    : myValidateSaveAction
  });
  xinha_config.registerButton({
    id        : "insertimage",
    tooltip   : HTMLArea._lc("Insert Image"),
    image     : _editor_url + xinha_config.imgURL + "ed_image.gif",
    textMode  : false,
    action    : function(e) {e._insertImage();}
  });
  xinha_config.registerButton({
    id        : "createlink",
    tooltip   : HTMLArea._lc("Insert/Modify Link"),
    image     : _editor_url + xinha_config.imgURL +  "ed_link.gif",
    textMode  : false,
    action    : function(e) {e._insertInlineLink();}
  });

  xinha_config.toolbar = [
    ['bold', 'italic', 'underline', "strikethrough", 'separator',
     'superscript', 'subscript','separator',
     'formatblock', 'separator',
//     'justifyleft','justifycenter','justifyright', 'separator',
     'insertorderedlist', 'insertunorderedlist', 'separator',
     'cut', 'copy', 'paste', 'separator', 'undo', 'redo'
    ],
    ['createlink', 'insertimage', 'inserttable', 'separator',
     'htmlmode', 'separator', 'my-validatesave', "separator", "showhelp", "popupeditor"
    ]
  ];

   xinha_config.formatblock = ({
                "Heading 1": "h1",
                "Heading 2": "h2",
                "Heading 3": "h3",
                "Heading 4": "h4",
                "Normal": "p"
   });

  xinha_config.pageStyle="body, td {font-family: Verdana, Geneva, Arial, Helvetica, sans-serif;color: #0000;font-size: 90%;}";
  xinha_config.pageStyle+="p {font-size: 100%;}";
  xinha_config.pageStyle+="h1 {font-weight: bold;font-size: 100%;}";
  xinha_config.pageStyle+="h2 {font-weight: bold;	color:#2222CC; font-size: 100%;	}";
  xinha_config.pageStyle+="h3 {font-weight: normal; color:#0000AA;	font-size: 100%; }";
  xinha_config.pageStyle+="h4 {font-weight: normal;	color:#000055;	font-size: 100%;}";
  xinha_config.pageStyle+="a {color: #0000FF; }";

  return xinha_config;
}


myValidateSaveAction = function(editor) {
  updateValue(editor);
  // editwizard validation
  validator.validate(editor._textArea);
}

// overrides editwizard.jsp
function doCheckHtml() {
  if (HTMLArea.checkSupportedBrowser()) {
    for (var editorname in xinha_editors) {
      editor = xinha_editors[editorname];
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
  // cancel on view only editor
  if(editor._doc == null) {
	  return;
  }
  if(editor != null && editor.getHTML) {
	  setWidthForTables(editor);
	  value = editor.outwardHtml(editor.getHTML());
	  // These two lines could cause editors to complain about responsetime
	  // when they leave a form with many large htmlarea fields.
	  // this is the case when doCheckHtml() is called by the editwizard.jsp with
	  // doSave, doSaveOnly, gotoForm and doStartWizard
	  value = wizardClean(value);
	  value = clean(value);

	  editor._textArea.value = value;

	  if (editor._editMode == "wysiwyg") {
	      var html = editor.inwardHtml(value);
	      editor.deactivateEditor();
	      editor.setHTML(html);
	      editor.activateEditor();
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
  value = value.replace(/&nbsp;/gi, " " );

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

function setWidthForTables(editor) {
	if(editor._doc != null) {
		var tables = editor._doc.getElementsByTagName('table');
		for (var i = 0 ; i < tables.length ; i++) {
			var table = tables[i];
			if (table.style.width)
				table.width = table.style.width;
		}
	}
}


HTMLArea.prototype._insertInlineLink = function(link) {
	var editor = this;
	var outparam = null;
	if (typeof link == "undefined") {
      	link = this.getParentElement();
      	while (link) {
            	if (/^a$/i.test(link.tagName)) break; //Search for the enclosing A tag, if found: continue and use it.
            	if (/^body$/i.test(link.tagName)) { link = null; break } //Stop searching when Body-tag is found, don't go too deep.
            	link = link.parentNode;
      	}
	}
	var sel = editor._getSelection();
	var sel_value = sel;
	var range = this._createRange(sel);
	if(HTMLArea.is_ie) sel_value = range.text;
	if (link){
         	outparam = {
               	f_href   : HTMLArea.is_ie ? editor.stripBaseURL(link.href) : link.getAttribute("href"),
                f_destination : link.destination ,
               	f_title   : link.name?link.name: sel_value,
				f_tooltip : link.title,
               	f_target : link.target,
               	f_usetarget : editor.config.makeLinkShowsTarget
         	};
	}
	else{
         	outparam = {
               	f_href   : "Click \"New Url\" to enter URL",
                f_destination : null,
               	f_title   : sel_value?sel_value:'',
				f_tooltip : '',
               	f_target : '',
               	f_usetarget : editor.config.makeLinkShowsTarget
         	};
	}
	this._popupDialog( "insertinline_link.html", function(param) {
      	if (!param) { return false; } //user must have pressed cancel
		var a = link;
		if ( !a ){
                  try
			{
                        editor._doc.execCommand("createlink", false, param.f_href);
                        a = editor.getParentElement();
                        var sel = editor._getSelection();
                        var range = editor._createRange(sel);
                        if(editor._selectionEmpty(sel))
                        {
                              editor.insertHTML("<a href='" + param.f_href + "' title='" + param.f_tooltip + "' name='"+param.f_title+"' destination='"+ param.f_destination + "'>" + param.f_title+ "</a>");
                        }
                        else{
                              if ( !HTMLArea.is_ie )
                              {
                                    a = range.startContainer;
                                    if ( ! ( /^a$/i.test(a.tagName) ) )
                                    {
                                          a = a.nextSibling;
                                          if ( a === null )
                                          {
                                                a = range.startContainer.parentNode;
                                          }
                                    }
                                    a.innerHTML = param.f_title.trim();
                              }
                        }
			} catch(ex) {}
		}
		else
		{
			var href = param.f_href.trim();
			editor.selectNodeContents(a);
			if ( href === '' )
			{
      			  editor._doc.execCommand("unlink", false, null);
      			  editor.updateToolbar();
      			  return false;
			}
			else
			{
      			  a.href = href;
                  a.innerHTML = param.f_title.trim();
			}
		}
		a.target = param.f_target.trim();
		a.title = param.f_tooltip.trim();

            if (HTMLArea.is_ie) {
                  a.destination = param.f_destination.trim();
                  if (!a.destination && a.relationID) {
                        a.relationID = "";
                  }
            }
            else {
                  a.setAttribute("destination", param.f_destination.trim());
                  if (!a.getAttribute("destination") && a.getAttribute("relationID")) {
                        a.removeAttribute("relationID");
                  }
            }
		editor.selectNodeContents(a);
		editor.updateToolbar();
	},
	outparam);
};

HTMLArea.prototype._insertImage = function(image) {
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
                            case "f_height"  :
                              if (HTMLArea.is_ie) {
                                if (!value) {
                                  img.height = "100";
                                }
                                else {
                                  img.height = parseInt(value);
                                }
                              }
                              else {
                                if (!value) {
                                  img.removeAttribute("height");
                                }
                                else {
                                  img.setAttribute("height", parseInt(value));
                                }
                              }
                              break;
                            case "f_destination"  : HTMLArea.is_ie ? img.destination = value : img.setAttribute("destination", value); break;
                        }
                }
        }, outparam);
};

// Called when the user clicks the Insert Table button
HTMLArea.prototype._insertTable = function()
{
  var sel = this._getSelection();
  var range = this._createRange(sel);
  var editor = this;	// for nested functions
  this._popupDialog(
    editor.config.URIs.insert_table,
    function(param)
    {
      // user must have pressed Cancel
      if ( !param )
      {
        return false;
      }
      var doc = editor._doc;
      // create the table element
      var table = doc.createElement("table");
      // assign the given arguments

      for ( var field in param )
      {
        var value = param[field];
        if ( !value )
        {
          continue;
        }
        switch (field)
        {
          case "f_width":
            table.width = value + param.f_unit;
          break;
          case "f_align":
            table.align = value;
          break;
          case "f_border":
            table.border = parseInt(value, 10);
          break;
          case "f_spacing":
            table.cellSpacing = parseInt(value, 10);
          break;
          case "f_padding":
            table.cellPadding = parseInt(value, 10);
          break;
        }
      }
      var cellwidth = 0;
      if ( param.f_fixed )
      {
        cellwidth = Math.floor(100 / parseInt(param.f_cols, 10));
      }
      var tbody = doc.createElement("tbody");
      table.appendChild(tbody);
      for ( var i = 0; i < param.f_rows; ++i )
      {
        var tr = doc.createElement("tr");
        tbody.appendChild(tr);
        for ( var j = 0; j < param.f_cols; ++j )
        {
          var td = doc.createElement("td");
          // @todo : check if this line doesnt stop us to use pixel width in cells
          if (cellwidth)
          {
            td.style.width = cellwidth + "%";
          }
          tr.appendChild(td);
          // Browsers like to see something inside the cell (&nbsp;).
          td.appendChild(doc.createTextNode('\u00a0'));
        }
      }
      if ( HTMLArea.is_ie )
      {
        range.pasteHTML(table.outerHTML);
      }
      else
      {
        // insert the table
        editor.insertNodeAtSelection(table);
      }
      return true;
    },
    null
  );
};

HTMLArea.prototype._insertTable = function()
{
  var sel = this._getSelection();
  var range = this._createRange(sel);
  var editor = this;	// for nested functions
  this._popupDialog(
    editor.config.URIs.insert_table,
    function(param)
    {
      // user must have pressed Cancel
      if ( !param )
      {
        return false;
      }
      var doc = editor._doc;
      // create the table element
      var table = doc.createElement("table");
      // assign the given arguments

      for ( var field in param )
      {
        var value = param[field];
        if ( !value )
        {
          continue;
        }
        switch (field)
        {
          case "f_width":
            table.width = value + param.f_unit;
          break;
          case "f_align":
            table.align = value;
          break;
          case "f_border":
            table.border = parseInt(value, 10);
          break;
          case "f_spacing":
            table.cellSpacing = parseInt(value, 10);
          break;
          case "f_padding":
            table.cellPadding = parseInt(value, 10);
          break;
        }
      }
      var cellwidth = 0;
      if ( param.f_fixed )
      {
        cellwidth = Math.floor(100 / parseInt(param.f_cols, 10));
      }
      var tbody = doc.createElement("tbody");
      table.appendChild(tbody);
      for ( var i = 0; i < param.f_rows; ++i )
      {
        var tr = doc.createElement("tr");
        tbody.appendChild(tr);
        for ( var j = 0; j < param.f_cols; ++j )
        {
          var td = doc.createElement("td");
          // @todo : check if this line doesnt stop us to use pixel width in cells
          if (cellwidth)
          {
            td.style.width = cellwidth + "%";
          }
          tr.appendChild(td);
          // Browsers like to see something inside the cell (&nbsp;).
          td.appendChild(doc.createTextNode('\u00a0'));
        }
      }
      if ( HTMLArea.is_ie )
      {
        range.pasteHTML(table.outerHTML);
      }
      else
      {
        // insert the table
        editor.insertNodeAtSelection(table);
      }
      return true;
    },
    null
  );
};