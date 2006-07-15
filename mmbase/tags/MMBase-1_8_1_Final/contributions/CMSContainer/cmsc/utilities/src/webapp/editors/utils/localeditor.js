var InPlaceEditor = {}

InPlaceEditor.Local = Class.create();
InPlaceEditor.Local.defaultHighlightColor = "#FFFF99";
InPlaceEditor.Local.prototype = {
  initialize: function(element, options) {
    this.element = $(element);

    this.options = Object.extend({
      cancelLink: true,
      cancelText: "cancel",
      clickToEditText: "Click to edit",
      rows: 1,
      minHeight: 100,
      handleLineBreaks: true,
      formClassName: 'inplaceeditor-form',
      highlightcolor: InPlaceEditor.Local.defaultHighlightColor,
      highlightendcolor: "#FFFFFF",
      externalControl: null,
      emptyText: '...',
      emptyClassName: 'inplaceeditor-empty'
    }, options || {});

    if(this.options.formId && $(this.options.formId)) {
      var htmlForm = $(this.options.formId);

      if (htmlForm.onsubmit) {
        var oldOnSubmit = htmlForm.onsubmit;
        var newOnSubmit = this.onSubmit.bind(this);
        htmlForm.onsubmit = function() {
          if (newOnSubmit()) {
            return oldOnSubmit();
          }
          return false;
        };
      }
      else {
        htmlForm.onsubmit = this.onSubmit.bind(this);
      }
    }
    
    if (this.options.externalControl) {
      this.options.externalControl = $(this.options.externalControl);
    }
    
    this.originalBackground = Element.getStyle(this.element, 'background-color');
    if (!this.originalBackground) {
      this.originalBackground = "transparent";
    }
    
    this.element.title = this.options.clickToEditText;
    
    this.onclickListener = this.enterEditMode.bindAsEventListener(this);
    this.mouseoverListener = this.enterHover.bindAsEventListener(this);
    this.mouseoutListener = this.leaveHover.bindAsEventListener(this);
    Event.observe(this.element, 'click', this.onclickListener);
    Event.observe(this.element, 'mouseover', this.mouseoverListener);
    Event.observe(this.element, 'mouseout', this.mouseoutListener);
    if (this.options.externalControl) {
      Event.observe(this.options.externalControl, 'click', this.onclickListener);
      Event.observe(this.options.externalControl, 'mouseover', this.mouseoverListener);
      Event.observe(this.options.externalControl, 'mouseout', this.mouseoutListener);
    }
    
    this._checkEmpty();
  },
    _checkEmpty: function(){
        if( this.element.innerHTML.length == 0 ){
            this.element.appendChild(
                Builder.node('span',{className:this.options.emptyClassName},this.options.emptyText));
        }
    },
  enterEditMode: function(evt) {
    this.elementWidth = this.element.offsetWidth;
    this.elementHeight = this.element.offsetHeight;
  
    if (this.editing) return;
    this.editing = true;
    this.onEnterEditMode();
    if (this.options.externalControl) {
      Element.hide(this.options.externalControl);
    }
    Element.hide(this.element);
    this.createForm();
    this.element.parentNode.insertBefore(this.form, this.element);
    Field.scrollFreeActivate(this.editField);
    // stop the event to avoid a page refresh in Safari
    if (evt) {
      Event.stop(evt);
    }
    return false;
  },
  createForm: function() {
    this.form = document.createElement("div");
    Element.addClassName(this.form, this.options.formClassName)

    this.createEditField();

    if (this.options.textarea) {
      var br = document.createElement("br");
      this.form.appendChild(br);
    }

    if (this.options.cancelLink) {
      cancelLink = document.createElement("a");
      cancelLink.href = "#";
      cancelLink.appendChild(document.createTextNode(this.options.cancelText));
      cancelLink.onclick = this.onclickCancel.bind(this);
      cancelLink.className = 'editor_cancel';      
      this.form.appendChild(cancelLink);
    }
  },
  hasHTMLLineBreaks: function(string) {
    if (!this.options.handleLineBreaks) return false;
    return string.match(/<br/i) || string.match(/<p>/i);
  },
  convertHTMLLineBreaks: function(string) {
    return string.replace(/<br>/gi, "\n").replace(/<br\/>/gi, "\n").replace(/<\/p>/gi, "\n").replace(/<p>/gi, "");
  },
  createEditField: function() {
    var text = this.getText();
    var obj = this;
    
    if (this.options.rows == 1 && !this.hasHTMLLineBreaks(text)) {
      this.options.textarea = false;
      var textField = document.createElement("input");
      textField.obj = this;
      textField.type = "text";
      textField.name = "value";
      textField.value = text;
      textField.style.backgroundColor = this.options.highlightcolor;
      textField.className = 'editor_field';
      var size = this.options.size || this.options.cols || 0;
      if (size != 0) textField.size = size;
      this.editField = textField;
    } else {
      this.options.textarea = true;
      var textArea = document.createElement("textarea");
      textArea.obj = this;
      textArea.name = "value";
      if (this.options.htmlarea) {
	      textArea.value = text;
      } else {
	      textArea.value = this.convertHTMLLineBreaks(text);
	  }
      textArea.rows = this.options.rows;
      textArea.cols = this.options.cols || 40;
      textArea.className = 'editor_field';      
      this.editField = textArea;
    }

	this.editField.name = this.element.id;

    this.form.appendChild(this.editField);
    
    if (this.options.htmlarea) {
      var editor = new HTMLArea(this.editField);
      customize(editor, _editor_url);
      editor.config.width = this.elementWidth;
      editor.config.height = this.options.minHeight > this.elementHeight ? this.options.minHeight: this.elementHeight;
      editor.config.sizeIncludesToolbar = false;
      editor.generate();
      
      this.editor = editor;
    }
  },
  getText: function() {
    document.getElementsByClassName(this.options.emptyClassName,this.element).each(function(child){
        this.element.removeChild(child);
    }.bind(this));
    return this.element.innerHTML;
  },
  onclickCancel: function() {
    this.leaveEditMode();
    return false;
  },
  onSubmit: function() {
    if (HTMLArea.checkSupportedBrowser() && this.editor) {
      updateValue(this.editor);
	}
    return true;
  },
  removeForm: function() {
    if(this.form) {
      if (this.form.parentNode) Element.remove(this.form);
      this.form = null;
    }
  },
  enterHover: function() {
    this.element.style.backgroundColor = this.options.highlightcolor;
    if (this.effect) {
      this.effect.cancel();
    }
    Element.addClassName(this.element, this.options.hoverClassName)
  },
  leaveHover: function() {
    if (this.options.backgroundColor) {
      this.element.style.backgroundColor = this.oldBackground;
    }
    Element.removeClassName(this.element, this.options.hoverClassName)
    this.effect = new Effect.Highlight(this.element, {
      startcolor: this.options.highlightcolor,
      endcolor: this.options.highlightendcolor,
      restorecolor: this.originalBackground
    });
  },
  leaveEditMode: function() {
    Element.removeClassName(this.element, this.options.savingClassName);
    this.removeForm();
    this.leaveHover();
    this.element.style.backgroundColor = this.originalBackground;
    Element.show(this.element);
    if (this.options.externalControl) {
      Element.show(this.options.externalControl);
    }
    this.editing = false;
    this.oldInnerHTML = null;
    this.onLeaveEditMode();
  },
  onEnterEditMode: function() {},
  onLeaveEditMode: function() {},
  dispose: function() {
    if (this.oldInnerHTML) {
      this.element.innerHTML = this.oldInnerHTML;
    }
    this.leaveEditMode();
    Event.stopObserving(this.element, 'click', this.onclickListener);
    Event.stopObserving(this.element, 'mouseover', this.mouseoverListener);
    Event.stopObserving(this.element, 'mouseout', this.mouseoutListener);
    if (this.options.externalControl) {
      Event.stopObserving(this.options.externalControl, 'click', this.onclickListener);
      Event.stopObserving(this.options.externalControl, 'mouseover', this.mouseoverListener);
      Event.stopObserving(this.options.externalControl, 'mouseout', this.mouseoutListener);
    }
  }
};
