// Code to customize the htmlarea toolbar for the editwizards (less buttons,
// a createlink with a target dropdown and a validate button).
// Author: Jaco de Groot.

function customize(editor, editorURL) {
  var config = editor.config;
  config.editorURL = editorURL;
  config.registerButton({
    id        : "my-createlink",
    tooltip   : "Insert Web Link",
    image     : "images/ed_link.gif",
    textMode  : false,
    action    : myCreateLinkAction
  });
  config.registerButton({
    id        : "my-validatesave",
    tooltip   : "Validate The Form",
    image     : "images/ed_validate_save.gif",
    textMode  : true,
    action    : myValidateSaveAction
  });
  config.toolbar = [
    ['bold', 'italic', 'underline', 'separator',
     'insertorderedlist', 'insertunorderedlist', 'separator',
     'cut', 'copy', 'paste', 'separator',
     'undo', 'redo', 'separator',
     'my-createlink', 'separator',
     'htmlmode', 'separator',
     'my-validatesave'
    ]
  ];
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
    while (parentElement.nodeName.toLowerCase() != "a" && parentElement.nodeName.toLowerCase() != "body") {
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
  editor._popupDialog("insert_link.html", popupDialogAction, param);
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
  doCheckHtml();
}

// overrides editwizard.jsp
function doCheckHtml() {
// editors in IE will maybe complain that it is very messy with 
// <strong> and <b> tags mixed when they edit, but without this function
// they would also do when others would use Gecko browsers.
// Now we are backwards compatible with the old editwizard wysiwyg and the
// frontend only has to deal with <b> and <i>
    for (var i = 0; i < htmlAreas.length; i++) {
      var editor = htmlAreas[i];
      value = editor.getHTML();
      
      //replace <EM> by <i>
      value = value.replace(/<([\/]?)EM>/gi, "<$1i>");
      value = value.replace(/<([\/]?)em>/gi, "<$1i>");
      //replace <STRONG> by <b>
      value = value.replace(/<([\/]?)STRONG>/gi, "<$1b>");
      value = value.replace(/<([\/]?)strong>/gi, "<$1b>");
      //replace <BR> by <BR/>
      value = value.replace(/<BR>/gi, "<br/>");
      value = value.replace(/<br>/gi, "<br/>");
      
      editor._textArea.value = value;

	  if (editor._editMode == "wysiwyg") {
		if (HTMLArea.is_gecko) {
			// disable design mode before changing innerHTML
			editor._doc.designMode = "off";
		}
		editor._doc.body.innerHTML = value;
		if (HTMLArea.is_gecko) {
			// we need to refresh that info for Moz-1.3a
			editor._doc.designMode = "on";
		}
      }

      // editwizard validation
      validator.validate(editor._textArea);
    }
}