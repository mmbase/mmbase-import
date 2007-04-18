AddressBar = Class.create();
Object.extend(Object.extend(AddressBar.prototype, Autocompleter.Base.prototype), {
  initialize: function(element, update, url, options) {
    this.baseInitialize(element, update, options);
    this.options.asynchronous  = true;
    this.options.onComplete    = this.onComplete.bind(this);
    this.options.defaultParams = this.options.parameters || null;
    this.options.array         = new Array(0);
    this.url                   = url;
  },

  getUpdatedChoices: function() {
      if (this.getToken().indexOf('/') < 0 || this.getToken().substring(this.getToken().length - '/'.length) == '/') {
	    entry = encodeURIComponent(this.options.paramName) + '=' + 
	      encodeURIComponent(this.getToken());

	    this.options.parameters = this.options.callback ?
	      this.options.callback(this.element, entry) : entry;
	
	    if(this.options.defaultParams) 
	      this.options.parameters += '&' + this.options.defaultParams;
	
	    new Ajax.Request(this.url, this.options);
	}
	else {
	    this.updateChoices(this.options.selector(this));
	}
  },
  onComplete: function(request) {
  	try {
		this.options.array = new Array(0);
		if (request.responseXML) {
			var optionsXml = request.responseXML.getElementsByTagName("options")[0];
			var options = optionsXml.childNodes;
			for (var i = 0 ; i < options.length ; i++) {
				var optionXml = options[i];
				if (optionXml.tagName == "option") {
					this.options.array[this.options.array.length] = optionXml.firstChild.nodeValue;
				}
			}
	    	this.updateChoices(this.options.selector(this));
    	}
	}
	catch(e) {
		alert("ERROR: AddressBar.onComplete ("+request.responseText+")");
	}
  },
  setOptions: function(options) {
    this.options = Object.extend({
      choices: 10,
      partialSearch: true,
      partialChars: 2,
      ignoreCase: true,
      fullSearch: false,
      selector: function(instance) {
        var ret       = []; // Beginning matches
        var partial   = []; // Inside matches
        var entry     = instance.getToken();
        var count     = 0;

        for (var i = 0; i < instance.options.array.length &&  
          ret.length < instance.options.choices ; i++) { 

          var elem = instance.options.array[i];
          var foundPos = instance.options.ignoreCase ? 
            elem.toLowerCase().indexOf(entry.toLowerCase()) : 
            elem.indexOf(entry);

          while (foundPos != -1) {
            if (foundPos == 0 && elem.length != entry.length) { 
              ret.push("<li><strong>" + elem.substr(0, entry.length) + "</strong>" + 
                elem.substr(entry.length) + "</li>");
              break;
            } else if (entry.length >= instance.options.partialChars && 
              instance.options.partialSearch && foundPos != -1) {
              if (instance.options.fullSearch || /\s/.test(elem.substr(foundPos-1,1))) {
                partial.push("<li>" + elem.substr(0, foundPos) + "<strong>" +
                  elem.substr(foundPos, entry.length) + "</strong>" + elem.substr(
                  foundPos + entry.length) + "</li>");
                break;
              }
            }

            foundPos = instance.options.ignoreCase ? 
              elem.toLowerCase().indexOf(entry.toLowerCase(), foundPos + 1) : 
              elem.indexOf(entry, foundPos + 1);

          }
        }
        if (partial.length)
          ret = ret.concat(partial.slice(0, instance.options.choices - ret.length))
        return "<ul>" + ret.join('') + "</ul>";
      }
    }, options || {});
  }
});

function showAllProperties(el, values) {
    var s = "";
    for (e in el) {
        s += e;
        if (values) s += "["+el[e]+"]";
        s += ", ";
    }
    alert(s);
}
