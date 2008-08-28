Ajax.InPlaceHtmlEditor = Class.create();


Object.extend(Ajax.InPlaceHtmlEditor.prototype, Ajax.InPlaceEditor.prototype);

Ajax.InPlaceHtmlEditor.prototype.__initialize = Ajax.InPlaceEditor.prototype.initialize;
Ajax.InPlaceHtmlEditor.prototype.__getText = Ajax.InPlaceEditor.prototype.getText;
Ajax.InPlaceHtmlEditor.prototype.__onComplete = Ajax.InPlaceEditor.prototype.onComplete;
Ajax.InPlaceHtmlEditor.prototype.__enterEditMode = Ajax.InPlaceEditor.prototype.enterEditMode;

Object.extend(Ajax.InPlaceHtmlEditor.prototype, {
  initialize: function(element, url, options){
    this.__initialize(element,url,options)
    this.setOptions(options);
    this._checkEmpty();
  },

  setOptions: function(options){
    this.options = Object.extend(Object.extend(this.options,{
      emptyText: '...',
      emptyClassName: 'inplaceeditor-empty'
    }),options||{});
  },

  _checkEmpty: function(){
    if( this.element.innerHTML.length == 0 ){
      this.element.appendChild(
        Builder.node('span',{className:this.options.emptyClassName},this.options.emptyText)
      );
    }
  },

  getText: function(){
    this.element.select('.' + this.options.emptyClassName).each(function(child){
      this.element.removeChild(child);
    }.bind(this));
    return this.__getText();
  },

  onComplete: function(transport){
    this._checkEmpty();
    this.__onComplete(transport);
  },
  
  enterEditMode: function(evt) {
    this.elementWidth = this.element.offsetWidth;
    this.elementHeight = this.element.offsetHeight;
    return this.__enterEditMode(evt);
  },
  
  createEditField: function() {
    var text;
    if(this.options.loadTextURL) {
      text = this.options.loadingText;
    } else {
      text = this.getText();
    }

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
      if (this.options.submitOnBlur)
        textField.onblur = this.onSubmit.bind(this);
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

      if (this.options.submitOnBlur)
        textArea.onblur = this.onSubmit.bind(this);
      this.editField = textArea;
    }
    
    if(this.options.loadTextURL) {
      this.loadExternalText();
    }
    this.form.appendChild(this.editField);
      if (this.options.htmlarea) {
        var xinha_plugins = ['CharacterMap','ContextMenu','TableOperations'];
        HTMLArea.loadPlugins(xinha_plugins, xinha_init);
        var xinha_config = createDefaultConfig();
        var editor = new HTMLArea(this.editField, HTMLArea.cloneObject(xinha_config));
        xinha_editors = HTMLArea.makeEditors(editor, xinha_config, xinha_plugins);
        editor.registerPlugins(xinha_plugins);
        editor.config.width = this.elementWidth;
        editor.config.height = this.options.minHeight > this.elementHeight ? this.options.minHeight: this.elementHeight;
        editor.config.sizeIncludesBars = false;
        editor.generate();
    }
  }
});
