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
//   'TableOperations'
  ];
  // THIS BIT OF JAVASCRIPT LOADS THE PLUGINS, NO TOUCHING  :)
  if(!Xinha.loadPlugins(xinha_plugins, xinha_init)) return;
  xinha_config = createDefaultConfig();
  xinha_editors = Xinha.makeEditors(xinha_editors, xinha_config, xinha_plugins);
  Xinha.startEditors(xinha_editors);
}

createDefaultConfig = function() {

  var xinha_config = xinha_config ? xinha_config() : new Xinha.Config();
/*
  xinha_config.registerButton({
    id        : "my-createlink",
    tooltip   : Xinha._lc("Insert Web Link"),
    image     : _editor_url + xinha_config.imgURL +  "ed_link.gif",
    textMode  : false,
    action    : myCreateLinkAction
  });
*/
  xinha_config.registerButton({
    id        : "my-validatesave",
    tooltip   : Xinha._lc("Validate The Form"),
    image     : _editor_url + xinha_config.imgURL +  "ed_validate_save.gif",
    textMode  : true,
    action    : myValidateSaveAction
  });
  xinha_config.toolbar = [
    ['bold', 'italic', 'underline', 'separator',
     'insertorderedlist', 'insertunorderedlist', 'separator',
     'cut', 'copy', 'paste', 'separator',
     'undo', 'redo', 'separator',
     'createlink', 'separator',
     'htmlmode', 'separator',
     'my-validatesave'
    ]
  ];
  xinha_config.width = ['98%'];
  
  return xinha_config;
}

myCreateLinkAction = function(editor) {
  var parentElement;
  var selection = editor._getSelection();
  var range = editor._createRange(selection);
  selectedHTML = editor.getSelectedHTML();
  var editExisting = false;
  var href = "http://";
  var description;
  if (HTMLArea.is_ie) {
    description = range.text;
  } else {
    description = selection;
  }
  var descriptionEditable = true;
  var target = "current";
  if (description == "") {
    var firstParent = true;
    parentElement = editor.getParentElement();
    while (parentElement && parentElement.nodeName.toLowerCase() != "a" && parentElement.nodeName.toLowerCase() != "body") {
      parentElement = parentElement.parentNode;
      firstParent = false;
    }
    if (parentElement.nodeName.toLowerCase() == "a") {
      editExisting = true;
      href = parentElement.attributes["href"].nodeValue;
      description = parentElement.firstChild.nodeValue;
      if (!firstParent) {
        descriptionEditable = false;
      }
      if (parentElement.attributes["target"] != null) {
        target = parentElement.attributes["target"].nodeValue;
      }
    }
  } else {
    if (selectedHTML.indexOf('<') != -1) {
      descriptionEditable = false;
    }
  }
  if (target == "") {
    target = "current";
  }
  var param = new Object();
  param["editor"] = editor;
  param["parentElement"] = parentElement;
  param["range"] = range;
  param["selectedHTML"] = selectedHTML;
  param["href"] = href;
  param["description"] = description;
  param["descriptionEditable"] = descriptionEditable;
  param["target"] = target;
  param["editExisting"] = editExisting;
  editor._popupDialog("insert_link.html", popupDialogAction, param, "width=398,height=220");
}

popupDialogAction = function(param) {
  if (!param) {
    return false;
  }
  var editor = param["editor"];
  if (param["editExisting"]) {
    var parentElement = param["parentElement"];
    if (parentElement.nodeName.toLowerCase() == "a") {
      parentElement.attributes["href"].nodeValue = param["href"];
      parentElement.firstChild.nodeValue = param["description"];
      if (param["target"] == "current") {
        if (parentElement.attributes.getNamedItem("target") != null) {
          parentElement.attributes.removeNamedItem("target");
        }
      } else {
        parentElement.target = param["target"];
      }
    }
  } else {
    var doc = editor._doc;
    var link = doc.createElement("a");
    var textNode;
    if (param["descriptionEditable"]) {
        textNode = doc.createTextNode(param["description"]);
        link.appendChild(textNode);
        link.href = param["href"];
        if (param["target"] != "current") {
          link.target = param["target"];
        }
        if (HTMLArea.is_ie) {
          range = param["range"];
          range.pasteHTML(link.outerHTML);
        } else {
            editor.insertNodeAtSelection(link);
        }
    } else {
        var startTag = "<a href=\"" + param["href"] + "\"";
        if (param["target"] != "current") {
          startTag = startTag + " target=\"" + param["target"] + "\"";
        }
        startTag = startTag + ">";
        editor.surroundHTML(startTag, "</a>");
    }
  }
  return true;
}

myValidateSaveAction = function(editor) {
  updateValue(editor);
  // editwizard validation
  validator.validate(editor._textArea);
}

// overrides editwizard.jsp
function doCheckHtml() {
  if (Xinha.checkSupportedBrowser()) {
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
    if(!Xinha.is_ie) {
      setDimensionForImages(editor);
    }

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
  value = value.replace(/<BR>/gi, "<br />");
  value = value.replace(/<br>/gi, "<br />");

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

function setDimensionForImages(editor) {
	if(editor._doc != null) {
		var images = editor._doc.getElementsByTagName('img');
		for (var i = 0 ; i < images.length ; i++) {
			var image = images[i];
			if (image.style.width) {
				image.width = image.style.width;
         }
         if (image.style.height)
         {
            image.height = image.style.height;
         }
		}
	}
}
