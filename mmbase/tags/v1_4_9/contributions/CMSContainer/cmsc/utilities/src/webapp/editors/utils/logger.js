/*

Requires: Prototype Javascript library (http://prototype.conio.net/)

Copyright (c) 2005 Corey Johnson (probablyCorey@gmail.com) 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

// ------------
// Custom Event
// ------------

CustomEvent = Class.create()
CustomEvent.prototype = {
  initialize : function() {
  	this.listeners = []
  },

	addListener : function(method) {
		this.listeners.push(method)
	},

	removeListener : function(method) {
		var foundIndexes = this._findListenerIndexes(method)

		for(var i = 0; i < foundIndexes.length; i++) {
			this.listeners.splice(foundIndexes[i], 1)
		}
	},

	dispatch : function(handler) {
		for(var i = 0; i < this.listeners.length; i++) {
			try {
				this.listeners[i](handler)
			}
			catch (e) {
				alert("Could not run the listener " + this.listeners[i] + ". " + e)
			}
		}
	},

	// Private Methods
	// ---------------
	_findListenerIndexes : function(method) {
		var indexes = []
		for(var i = 0; i < this.listeners.length; i++) {			
			if (this.listeners[i] == method) {
				indexes.push(i)
			}
		}

		return indexes
	}
}

// ------
// Cookie
// ------

var Cookie = {
	set : function(name, value, expirationInDays, path) {
		var cookie = escape(name) + "=" + escape(value)

		if (expirationInDays) {
			var date = new Date()
			date.setDate(date.getDate() + expirationInDays)
			cookie += "; expires=" + date.toGMTString()
		} 

		if (path) {
			cookie += ";path=" + path
		}

		document.cookie = cookie

		if (value && (expirationInDays == undefined || expirationInDays > 0) && !this.get(name)) {
			Logger.error("Cookie (" + name + ") was not set correctly... The value was " + value.toString().length + " charachters long (This may be over the cookie limit)");
		}
	},

	get : function(name) {
		var pattern = "(^|;)\\s*" + escape(name) + "=([^;]+)"

		var m = document.cookie.match(pattern)
		if (m && m[2]) {			
			return unescape(m[2])
		}
		else return null
	},

	getAll : function() {
		var cookies = document.cookie.split(';')
		var cookieArray = []				

		for (var i = 0; i < cookies.length; i++) {			
			try {
				var name = unescape(cookies[i].match(/^\s*([^=]+)/m)[1])
				var value = unescape(cookies[i].match(/=(.*$)/m)[1])
			}
			catch (e) {
				continue
			}

			cookieArray.push({name : name, value : value})

			if (cookieArray[name] != undefined) {
				Logger.waring("Trying to retrieve cookie named(" + name + "). There appears to be another property with this name though.");
			}			

			cookieArray[name] = value
		}

		return cookieArray
	},

	clear : function(name) {
		this.set(name, "", -1)
	},

	clearAll : function() {
		var cookies = this.getAll()

		for(var i = 0; i < cookies.length; i++) {
			this.clear(cookies[i].name)
		}

	}
}   

// ------
// Logger
// -----        

Logger = {
	logEntries : [],

	onupdate : new CustomEvent(),
	onclear : new CustomEvent(),
    

	// Logger output    
  log : function(message, tag) {
	  var logEntry = new LogEntry(message, tag || "info")
		this.logEntries.push(logEntry)
		this.onupdate.dispatch(logEntry)
	},

	info : function(message) {
		this.log(message, 'info')
	}, 

	debug : function(message) {
		this.log(message, 'debug')
	},  

	warn : function(message) {
	  this.log(message, 'warning')
	},

	error : function(message, error) {
	  this.log(message + ": \n" + error, 'error')
	},

	clear : function () {
		this.logEntries = []
		this.onclear.dispatch()
	}
}  

LogEntry = Class.create()
LogEntry.prototype = {  
    initialize : function(message, tag) {
      this.message = message
      this.tag = tag
    }
}

LogConsole = Class.create()
LogConsole.prototype = {  

  // Properties
  // ----------
  commandHistory : [],
  commandIndex : 0,

  // Methods
  // -------

  initialize : function() {
    this.outputCount = 0
    this.tagPattern = Cookie.get('tagPattern') || ".*"
  
  	// I hate writing javascript in HTML... but what's a better alternative
    this.logElement = document.createElement('div')
    document.body.appendChild(this.logElement)
    Element.hide(this.logElement)

		this.logElement.style.position = "absolute"
    this.logElement.style.left = '0px'
    this.logElement.style.width = '100%'

    this.logElement.style.textAlign = "left"
    this.logElement.style.fontFamily = "lucida console"
    this.logElement.style.fontSize = "100%"
    this.logElement.style.backgroundColor = 'darkgray'      
    this.logElement.style.opacity = 0.9 
    this.logElement.style.zIndex = 2000 

    // Add toolbarElement
    this.toolbarElement = document.createElement('div')
    this.logElement.appendChild(this.toolbarElement)     
    this.toolbarElement.style.padding = "0 0 0 2px"

    // Add buttons        
    this.buttonsContainerElement = document.createElement('span')
    this.toolbarElement.appendChild(this.buttonsContainerElement) 

    this.buttonsContainerElement.innerHTML += '<button onclick="logConsole.toggle()" style="float:right;color:black">close</button>'
    this.buttonsContainerElement.innerHTML += '<button onclick="Logger.clear()" style="float:right;color:black">clear</button>'        


		//Add Tag Filter
		this.tagFilterContainerElement = document.createElement('span')
    this.toolbarElement.appendChild(this.tagFilterContainerElement) 
    this.tagFilterContainerElement.style.cssFloat = 'left'
    this.tagFilterContainerElement.appendChild(document.createTextNode("Log Filter"))
    
    this.tagFilterElement = document.createElement('input')
    this.tagFilterContainerElement.appendChild(this.tagFilterElement)  
    this.tagFilterElement.style.width = '200px'                    
    this.tagFilterElement.value = this.tagPattern    
    this.tagFilterElement.setAttribute('autocomplete', 'off') // So Firefox doesn't flip out
    
    Event.observe(this.tagFilterElement, 'keyup', this.updateTags.bind(this))
    Event.observe(this.tagFilterElement, 'click', function() {this.tagFilterElement.select()}.bind(this))    
    
    // Add outputElement
    this.outputElement = document.createElement('div')
    this.logElement.appendChild(this.outputElement)  
    this.outputElement.style.overflow = "auto"              
    this.outputElement.style.clear = "both"
    this.outputElement.style.height = "200px"
    this.outputElement.style.backgroundColor = 'black' 
          
    this.inputContainerElement = document.createElement('div')
    this.inputContainerElement.style.width = "100%"
    this.logElement.appendChild(this.inputContainerElement)      
    
    this.inputElement = document.createElement('input')
    this.inputContainerElement.appendChild(this.inputElement)  
    this.inputElement.style.width = '100%'                    
    this.inputElement.style.borderWidth = '0px' // Inputs with 100% width always seem to be too large (I HATE THEM) they only work if the border, margin and padding are 0
    this.inputElement.style.margin = '0px'
    this.inputElement.style.padding = '0px'
    this.inputElement.value = 'Type command here' 
    this.inputElement.setAttribute('autocomplete', 'off') // So Firefox doesn't flip out

    Event.observe(this.inputElement, 'keyup', this.handleInput.bind(this))
    Event.observe(this.inputElement, 'click', function() {this.inputElement.select()}.bind(this))    

		window.setInterval(this.repositionWindow.bind(this), 500)
		this.repositionWindow()
		
    // Listen to the logger....
    Logger.onupdate.addListener(this.logUpdate.bind(this))
    Logger.onclear.addListener(this.clear.bind(this))		

    // Preload log element with the log entries that have been entered
		for (var i = 0; i < Logger.logEntries.length; i++) {
  		this.logUpdate(Logger.logEntries[i])
  	}   
  	
  	// Feed all errors into the logger (For some unknown reason I can only get this to work
  	// with an inline event declaration)
  	Event.observe(window, 'error', function(msg, url, lineNumber) {Logger.error("Error in (" + (url || location) + ") on line "+lineNumber+"", msg)})

    // Allow acess key link          
    var accessElement = document.createElement('span')
    accessElement.innerHTML = '<button style="position:absolute;top:-100px" onclick="javascript:logConsole.toggle()" accesskey="d"></button>'
  	document.body.appendChild(accessElement)

  	if (Cookie.get('ConsoleVisible') == 'true') {
		  this.toggle()
		}
	},

	toggle : function() {
	  if (this.logElement.style.display == 'none') {
		  this.show()
		}
		else {
			this.hide()
		}
	}, 
	
	show : function() {
	  Element.show(this.logElement)
	  this.outputElement.scrollTop = this.outputElement.scrollHeight // Scroll to bottom when toggled
	  Cookie.set('ConsoleVisible', 'true')
 	  this.inputElement.select()
	}, 
	
	hide : function() {
	  Element.hide(this.logElement)
	  Cookie.set('ConsoleVisible', 'false')
	},  
	
	output : function(message, style) {
			// If we are at the bottom of the window, then keep scrolling with the output			
			var shouldScroll = (this.outputElement.scrollTop + (2 * this.outputElement.clientHeight)) >= this.outputElement.scrollHeight
	
			this.outputCount++
	  	style = (style ? style += ';' : '')	  	
	  	style += 'padding:1px;margin:0 0 5px 0'	     
		  
		  if (this.outputCount % 2 == 0) style += ";background-color:#101010"
	  	
	  	message = message || "undefined"
	  	message = message.toString().escapeHTML()
	  	
	  	this.outputElement.innerHTML += "<pre style='" + style + "'>" + message + "</pre>"
	  	
	  	if (shouldScroll) {				
				this.outputElement.scrollTop = this.outputElement.scrollHeight
			}
	},
	
	updateTags : function() {
		var pattern = this.tagFilterElement.value
	
		if (this.tagPattern == pattern) return

		try {
			new RegExp(pattern)
		}
		catch (e) {
			return
		}
		
		this.tagPattern = pattern
		Cookie.set('tagPattern', this.tagPattern)

		this.outputElement.innerHTML = ""
		
		// Go through each log entry again
		this.outputCount = 0;
		for (var i = 0; i < Logger.logEntries.length; i++) {
  		this.logUpdate(Logger.logEntries[i])
  	}  
	},
	
	repositionWindow : function() {
		var offset = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
		var pageHeight = self.innerHeight || document.documentElement.clientHeight || document.body.clientHeight
		this.logElement.style.top = (offset + pageHeight - Element.getHeight(this.logElement)) + "px"
	},

	// Event Handlers
	// --------------

	logUpdate : function(logEntry) {
		if (logEntry.tag.search(new RegExp(this.tagPattern, 'igm')) == -1) return
		var style = ''
	  if (logEntry.tag.search(/error/) != -1) style += 'color:red'
	  else if (logEntry.tag.search(/warning/) != -1) style += 'color:orange'
	  else if (logEntry.tag.search(/debug/) != -1) style += 'color:green'
 	  else if (logEntry.tag.search(/info/) != -1) style += 'color:white'
	  else style += 'color:yellow'

		this.output(logEntry.message, style)
	},

	clear : function(e) {
		this.outputElement.innerHTML = ""
	},

	handleInput : function(e) {
		if (e.keyCode == Event.KEY_RETURN ) {      
	  	var command = this.inputElement.value
	  	
	  	switch(command) {
	    	case "clear":
	      		Logger.clear()  
	      		break
		    	
	    	default:        
	      	var consoleOutput = "" 
	      	
	      	try {
	        	consoleOutput = eval(this.inputElement.value)	        	
	      	}
	      	catch (e) {  
	        	Logger.error("Problem parsing input <" + command + ">", e)	        	
	        	break
					}
					
					Logger.log(consoleOutput)	      	
	      	break
			}        
	
	  	if (this.inputElement.value != "" && this.inputElement.value != this.commandHistory[0]) {
	    	this.commandHistory.unshift(this.inputElement.value)
	  	}
	  
	  	this.commandIndex = 0 
	  	this.inputElement.value = ""                                                        
		}
    else if (e.keyCode == Event.KEY_UP && this.commandHistory.length > 0) {
    	this.inputElement.value = this.commandHistory[this.commandIndex]

			if (this.commandIndex < this.commandHistory.length - 1) {
      	this.commandIndex += 1
      }
    }     
    else if (e.keyCode == Event.KEY_DOWN && this.commandHistory.length > 0) {
    	if (this.commandIndex > 0) {                                      
      	this.commandIndex -= 1
	    }                       

			this.inputElement.value = this.commandHistory[this.commandIndex]
	  } 
 		else {
    		this.commandIndex = 0
    }
	}
}         

// Load the Console when the window loads
Event.observe(window, "load", function() {logConsole = new LogConsole()}) 

// -------------------------
// Helper Functions And Junk
// -------------------------
function inspect(element, hideProperties, hideMethods) {
	var properties = []
	var methods = [] 
	
	element = $(element)

	for(var internal in element) {
		try {
			if (element[internal] instanceof Function) {
				if (!hideMethods) methods.push(internal + ":\t" + element[internal] )
			}
			else {
				if (!hideProperties) properties.push(internal + ":\t" + element[internal] )
			}
		}
		catch (e) {
			Logger.error("Excetion thrown while inspecting object.", e)
		}
	}

	properties.sort()
	methods.sort()

	var internals = properties.concat(methods)
	var output = ""
	for (var i = 0; i < internals.length; i++) {
		output += (internals[i] + "\n")
	}
		
	return output
}   

Array.prototype.contains = function(object) {
	for(var i = 0; i < this.length; i++) {
		if (object == this[i]) return true
	}

	return false
}  

// Helper Alias for simple logging
var puts = function() {return Logger.log(arguments[0], arguments[1])}      
