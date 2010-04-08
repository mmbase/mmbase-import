/**
 * wysiwyg.js
 *
 * wysiwyg component, by Q42 (http:q42.nl)
 * (c) Q42, 2001
 *
 * @since    MMBase-1.6
 * @version  $Id: wysiwyg.js,v 1.1 2003-12-19 11:09:39 nico Exp $
 * @author   Kars Veling
 * @author   Nico Klasens
 * @author   Peter Reitsma
 * @author   Kolja van der Vaart
 *
 */
function BrowserUtils() {
    // browser checks
    this.ie5xwin = ((navigator.appVersion.toLowerCase().indexOf("msie 5")   != -1) && (navigator.appVersion.toLowerCase().indexOf("win") != -1));
    this.ie50win = ((navigator.appVersion.toLowerCase().indexOf("msie 5.0") != -1) && (navigator.appVersion.toLowerCase().indexOf("win") != -1));
    this.ie55win = ((navigator.appVersion.toLowerCase().indexOf("msie 5.5") != -1) && (navigator.appVersion.toLowerCase().indexOf("win") != -1));
    this.ie60win = ((navigator.appVersion.toLowerCase().indexOf("msie 6.0") != -1) && (navigator.appVersion.toLowerCase().indexOf("win") != -1));
    this.ie5560win = this.ie55win || this.ie60win;
}
var browserutils=new BrowserUtils();

//IE5/Windows attachEvent, uniqueID will not work on IE5/Mac

if (browserutils.ie5560win) {
  window.attachEvent("onload",start_wysiwyg);
}

var imagedir = "../media/wysiwyg/";

// overrides editwizard.jsp
function doCheckHtml() {
    try {
        if (wysiwyg) wysiwyg.blur();
    } catch (e) {}
}

function start_wysiwyg() {
    wysiwyg=new Wysiwyg();
    wysiwyg.scan();
}

/** Wysiwyg object reference */
var wysiwyg = null;

/** Wysiwyg object constructor */
function Wysiwyg() {
    this.currentEditElement = null;
    this.editBar = null;
	this.refreshtimer = 0;
    this.goingToHideEditBar = 0;
    
    this.editBarButtons=[["createLink", "create a link (ctrl-k)", "createLink.gif"],
                         [false, false],
                         ["bold", "bold (ctrl-b)", "bold.gif"],
                         ["italic", "italic (ctrl-i)", "italic.gif"],
                         ["underline", "underline (ctrl-u)", "underline.gif"],
                         [false, false],
                         ["insertUnorderedList", "bullet list", "insertUnorderedList.gif"],
                         ["insertOrderedList", "numbered bullet list", "insertOrderedList.gif"],
                         [false, false],
                         ["toggle", "toggle between Source and HTML editing", "toggle.gif"]];

	this.preloadEditBarButtons();
    this.createEditBar();
}

Wysiwyg.prototype.preloadEditBarButtons = function() {
    //initialize the buttons
    for (var i=0; i<this.editBarButtons.length; i++) {
        var imagesrc = this.editBarButtons[i][2];
        image = new Image();
        image.src = imagedir + imagesrc;
	}
}

Wysiwyg.prototype.scan = function() {
    //find the forms to change
    var forms = window.document.forms;

    //loop all forms
    for (var i=0; i<forms.length; i++) {
        //loop elements of this form
        for (var j=0; j<forms[i].elements.length; j++) {
            //get the element
            var el = forms[i].elements[j];

            //check element type
			if (el.type.toLowerCase() == "textarea" && el.ftype == "html") {
                //make sure the field has an id
                if (!el.id) {
                    el.id = el.uniqueID;
                }
                //get the id
                var id = el.id;
                //get the class
                var className = el.className;
                //get the current value of this element
                var value = el.value;

                //create the wysiwyg element
                var el_wysiwyg = window.document.createElement("<iframe id='if_" + id + "' WIDTH='100%' HEIGHT='" + el.rows*15 + "' style='display:block'></iframe>");
                //store the original id
                el_wysiwyg.oldID = id;
				//disable functions buttons
				el_wysiwyg.hide=el.hide;

                //insert it before the textarea
                el.parentNode.insertBefore(el_wysiwyg, el);                                        
                //hide the textarea
                el.style.position = "absolute";
                el.style.visibility = "hidden";

                var curFrame = frames["if_"+id];
                curFrame.document.designMode = "On";
                curFrame.hspace = 0;
                curFrame.vspace = 0;

                //set its 'value'
                curFrame.document.open();
                curFrame.document.write(value);
                curFrame.document.close();  

                curFrame.document.body.topMargin = 0;  
                curFrame.document.body.leftMargin = 0;
                curFrame.document.body.rightMargin = 0;  
                curFrame.document.body.bottomMargin = 0;
                     
                //the modus variable is set to HTML at first                 
                curFrame.modus = 'HTML';                                   
                    
                curFrame.document.body.style.fontFamily = el.currentStyle.fontFamily;
                curFrame.document.body.style.fontSize = el.currentStyle.fontSize;
                curFrame.document.body.style.fontWeight = el.currentStyle.fontWeight;

                // attach events
                var self=this;
                el_wysiwyg.attachEvent("onfocus", function() {self.handleFocus();} );
                el_wysiwyg.attachEvent('onblur', function() {self.handleBlur(event.srcElement);} );
                el_wysiwyg.attachEvent('onmouseup', function() {self.checkButtons(); });
            }
        }
    }
}

Wysiwyg.prototype.handleFocus = function () {
    var el = event.srcElement;
    this.currentEditElement = el;
    this.showEditBar(el);
    this.refreshtimer = setInterval(updatetimer,2000);
}

Wysiwyg.prototype.handleBlur = function (el) {
    this.hideEditBar();
// this.currentEditElement = null; I put this comment in here 
// to explicitly show that currentEditElement shouldn't be cleaned
// hideEditBar has a delay to stop flickering the editbarbuttons when clicked
    this.updateValue(el);
    clearInterval(this.refreshtimer);
}

function updatetimer() {
    wysiwyg.updateValue(wysiwyg.currentEditElement);
}

//////////////////////////////////// Toolbar

Wysiwyg.prototype.createEditBar = function () {
    var s='<table class="wysiwyg"><tr>';
    for (var i=0; i<this.editBarButtons.length; i++) {
        var cmd = this.editBarButtons[i][0];
        var hint = this.editBarButtons[i][1];
        if (!cmd) {
            s += '<td><div class="separator"><img src="' + imagedir + 'pixel.gif" width="1" alt=""></div></td>';
            continue;
        }

        if (!hint) {
            hint = cmd;
        }

		var href;
		var imgsrc = this.editBarButtons[i][2];
		href = 'javascript:doExecCommand(\''+cmd+'\')';
        s += '<td><a href="'+href+'" class="barbutton" id="'+cmd+'button"><img src="'+imagedir+imgsrc+'" alt="'+hint+'" /></a></td>';
    }
    s += "</tr></table>";
    var div = document.createElement("div");
    div.id="wysiwyg_editbar";
    div.innerHTML = s;
    document.body.appendChild(div);

    this.editBar = div;
    this.editBar.style.visibility = 'hidden';
    this.editBar.style.position = 'absolute';
    this.editBar.modus = 'HTML';
}

Wysiwyg.prototype.showEditBar = function (editElement) {
    if (this.goingToHideEditBar) {
        clearTimeout(this.goingToHideEditBar);
    }
    var rect = editElement.getBoundingClientRect();
    this.editBar.style.top = rect.top - this.editBar.offsetHeight + document.body.scrollTop;
    this.editBar.style.left = rect.right - this.editBar.offsetWidth + document.body.scrollLeft;
    this.checkButtons();
    this.editBar.style.visibility = 'visible';
}

Wysiwyg.prototype.hideEditBar = function () {
    var self = this;
    this.goingToHideEditBar = setTimeout(function() {self.hideEditBarHelper();}, 500);
}

Wysiwyg.prototype.hideEditBarHelper = function () {
    this.editBar.style.visibility = 'hidden';
}

Wysiwyg.prototype.checkButtons = function () {
    var r = document.selection.createRange();
    
    for (var i=0; i<this.editBarButtons.length; i++) {
        var cmd = this.editBarButtons[i][0];
        if (!cmd) {
            continue;
        }
        var button = document.getElementById(cmd+'button');
		if (this.allowed(cmd)) {
			button.className='barbutton';
		} else {
			button.className='disabledbarbutton';
		}
    }
}

Wysiwyg.prototype.allowed = function(cmd) {
	var modus = frames[this.currentEditElement.id].modus;      
    //in text modus, the other buttons should not work
	if (modus == 'TEXT' && cmd != 'toggle') {
	    return false;
	}
	
	// add hide + ","
	var hide = this.currentEditElement.hide + ",";
	if (hide.indexOf(cmd + ',') > -1) {
	    return false;
	}
	else {
        return true;
    }
}

//////////////////////////////////// Execute Command

function doExecCommand(cmd) {
//    try {
        if (!wysiwyg.allowed(cmd)) {
            return;
        }

    	if(cmd == 'toggle') {
            wysiwyg.toggleModus();	
        }
		else {
            if (cmd.indexOf("javascript:") == 0) {
                wysiwyg.doJavaScript(cmd.substring(11));
            }
            else {
                wysiwyg.doExecCommand(cmd);
            }
        }
        wysiwyg.checkButtons();
//    } catch (e) {}
}

Wysiwyg.prototype.doExecCommand = function (cmd) {
    this.currentEditElement.focus();
         
    var curFrame = frames[this.currentEditElement.id];    
    var r = curFrame.document.selection.createRange();
    
    if (cmd == 'createLink'){
        r.execCommand(cmd,true);
    }
    else {
        r.execCommand(cmd, false, null);
    }
    r.select();        
    curFrame.focus();              
}

Wysiwyg.prototype.doJavaScript = function (cmd) {
	eval(cmd);
}

// this function is for the HTML/Source toggle button
Wysiwyg.prototype.toggleModus = function () {
    var curFrame = frames[this.currentEditElement.id];    
    	 
    if (curFrame.modus == 'HTML') {
        curFrame.document.body.innerText = this.cleanUpValue(curFrame.document.body.innerHTML);        
        curFrame.document.body.style.fontFamily = "monospace";
        curFrame.modus = 'TEXT';
    }
    else {
        curFrame.document.body.innerHTML = this.cleanUpValue(curFrame.document.body.innerText);
        curFrame.document.body.style.fontFamily = this.currentEditElement.currentStyle.fontFamily;
        curFrame.modus = 'HTML';
    }

    curFrame.focus();
    var s = curFrame.document.body.createTextRange();
    s.collapse(false);
    s.select();	
}

//////////////////////////////////// Value functions

Wysiwyg.prototype.updateValue = function (el) {
    var curFrame = frames[el.id];

    //get the original element that should contain the new value
    var el = window.document.all[el.oldID];

    //get the current modus
    var modus = curFrame.modus; 
    var curValue;
    if(modus == 'HTML') {
       curValue = curFrame.document.body.innerHTML;
    }
    else {
       curValue = curFrame.document.body.innerText;	
    }

    //clean up the value
    curValue = this.cleanUpValue(curValue);
    if (el.value != curValue) {
        //store the value
        el.value = curValue;             
        el.fireEvent("onkeyup");
    }
}

Wysiwyg.prototype.cleanUpValue = function (v) {
    //replace <EM> by <i>
    v = v.replace(/<([\/]?)EM>/gi, "<$1i>");

    //remove empty font tags (resultig from color removal or so)
    v = v.replace(/<font>([^(<\/font>)]*)<\/font>/gi, "$1");

    //(a little dirty, but we cannot remove them earlier since that would destroy the current selection)
    //all fonts are illegal until further notice (this has no problem with nested <font> tags)
    v = v.replace(/<font.*>/gi, "");
    v = v.replace(/<\/font>/gi, "");

	//replace <STRONG> by <b>
	v = v.replace(/<([\/]?)STRONG>/gi, "<$1b>");

	//replace <BR> by <BR/>
	v = v.replace(/<BR>/gi, "<br/>");
	//remove invalid link tag
	v = v.replace(/class=link/gi, "");
	//and class is first....
	//create a regexp?
	v = v.replace(/class=first/gi, "");

	if (v == "<P>&nbsp;</P>") {
		v = "";
	}

    return v;
}