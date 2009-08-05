/**
 * wysiwyg.jsp
 *
 * wysiwyg component, by Q42 (http:q42.nl)
 * (c) Q42, 2001
 *
 * @since    MMBase-1.6
 * @version  $Id: wysiwyg.js,v 1.1 2003-12-06 14:24:53 nico Exp $
 * @author   Kars Veling
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


var wysiwyg=null;


function start_wysiwyg() {
    wysiwyg=new Wysiwyg();
    wysiwyg.scan();
}

function Wysiwyg() {
    this.fields = new Array();
    this.currentEditElement = null;
    this.goingToHideEditBar = 0;
    this.editBarButtons=["createLink", false, "bold", "italic", false, "insertUnorderedList", "insertOrderedList"];
    this.editBarButtonHints=["create a link (ctrl-k)", false, "bold (ctrl-b)", "italic (ctrl-i)", false, "bullet list", "numbered bullet list"];
}

Wysiwyg.prototype.scan = function() {
    this.createEditBar();

    //find the forms to change
    var forms = window.document.forms;

    //loop all forms
    for (var i=0; i<forms.length; i++) {
        //loop elements of this form
        for (var j=0; j<forms[i].elements.length; j++) {
            //get the element
            var el = forms[i].elements[j];

            //check element type
            switch (el.type.toLowerCase()) {
                case "textarea":
                    if (el.ftype!="html") {
                        break;
                    }
                    //make sure the field has an id
                    if (!el.id) el.id = el.uniqueID;

                    //get the id
                    var id = el.id;

                    //store the field id
                    this.fields.push(id);

                    //get the class
                    var className = el.className;

                    //get the current value of this element
                    var value = el.value;

                    var rect = el.getBoundingClientRect();

                    //create the wysiwyg element
                    //make it inherit some style attributes of the original textarea (using el.currentStyle)
                    var s = "border-style:solid;border-color:#000000;border-width:1;font-family:"+el.currentStyle.fontFamily+";font-size:"+el.currentStyle.fontSize+";background-color:"+el.currentStyle.backgroundColor+";";
                    s+="overflow:auto;height:"+(rect.bottom-rect.top)+";width:"+(rect.right-rect.left);
                    var el_wysiwyg = window.document.createElement("<div id=\"" + id + "_formeditor\" class=\"wysiwyg_textarea\" contentEditable=\"true\" style=\""+s+";\"></div>");

                    //insert it before the textarea
                    el.parentNode.insertBefore(el_wysiwyg, el);

                    //hide the textarea
                    el.style.position = "absolute";
                    el.style.visibility = "hidden";

                    //set its 'value'
                    el_wysiwyg.innerHTML = value;

                    //store the original id
                    el_wysiwyg.oldID = id;

                    // attach events
                    var self=this;
                    el_wysiwyg.attachEvent("onfocus", function() {self.handleFocus();} );
                    el_wysiwyg.attachEvent('onblur', function() {self.handleBlur(event.srcElement);} );
                    el_wysiwyg.attachEvent('onmouseup', function() {self.checkButtons(); });
                    break;
            }
        }
    }
}

Wysiwyg.prototype.handleFocus = function () {
    this.showEditBar(event.srcElement);
}

Wysiwyg.prototype.handleBlur = function (el) {
    this.hideEditBar();

//	try {
        //get the element whose value should be updated
        var el_wysiwyg = el;

        //get the original element that should contain the new value
        var el = window.document.all[el_wysiwyg.oldID];

        //get the current 'value'
        var curValue = el_wysiwyg.innerHTML;

        //clean up the value
        curValue = this.cleanUpValue(curValue);

        //store the value
        el.value = curValue;
//	} catch (e) {
//	}
}

Wysiwyg.prototype.cleanUpValue = function (v) {
    //replace <EM> by <i>
    v = v.replace(/<([\/]?)EM>/gi, "<$1i>");


    //remove empty font tags (resultig from color removal or so)
    v = v.replace(/<font>([^(<\/font>)]*)<\/font>/gi, "$1");

    //remove <font size=3> references since they are unnecessary
    //(a little dirty, but we cannot remove them earlier since that would destroy the current selection)
    //ERROR: this will fail in case of nested <font> tags!!!!
    v = v.replace(/<font size=[\"]?3[\"]?>([^(<\/font>)]*)<\/font>/gi, "$1");

    return v;
}
//////////////////////////////////// Toolbar

Wysiwyg.prototype.showEditBar = function (ta) {
    this.currentEditElement = ta;
    if (this.goingToHideEditBar) clearTimeout(this.goingToHideEditBar);
    var rect=ta.getBoundingClientRect();
    this.editBar.style.top = rect.top- this.editBar.offsetHeight+document.body.scrollTop;
    this.editBar.style.left = rect.right - this.editBar.offsetWidth+document.body.scrollLeft;
    this.checkButtons();
    this.editBar.style.visibility = 'visible';
}

Wysiwyg.prototype.hideEditBar = function () {
    var self=this;
    this.goingToHideEditBar = setTimeout(function() {self.hideEditBar2();}, 500);
}

Wysiwyg.prototype.hideEditBar2 = function () {
    this.editBar.style.visibility = 'hidden';
}

Wysiwyg.prototype.blur = function () {
    try{this.handleBlur(this.currentEditElement);} catch (e) {}
}


function doExecCommand(cmd) {
    try {
        wysiwyg.doExecCommand(cmd);
    } catch (e) {}
}



Wysiwyg.prototype.doExecCommand = function (cmd) {
    this.currentEditElement.focus();
    var r=document.selection.createRange();
    r.execCommand(cmd);
    this.checkButtons();
}

Wysiwyg.prototype.checkButtons = function () {
    var r=document.selection.createRange();
    for (var i=0;i<this.editBarButtons.length;i++) {
        var cmd = this.editBarButtons[i]; if (!cmd) continue;
        var button = document.getElementById(cmd+'button');
        if (r.queryCommandState(cmd)) {
            button.className='enabledbarbutton';
        } else if (!r.queryCommandEnabled(cmd)) {
            button.className='disabledbarbutton';
        } else button.className='barbutton';
    }
}

Wysiwyg.prototype.createEditBar = function () {
    var s='<table class="wysiwyg"><tr>';
    for (var i=0;i<this.editBarButtons.length;i++) {
        var cmd = this.editBarButtons[i];
        var hint = this.editBarButtonHints[i];
        if (!hint) hint=cmd;
        if (!cmd) {
            s+='<td><div class="separator"><img src="../media/wysiwyg/pixel.gif" width="1" height="19" alt=""></div></td>';
            continue;
        }
        s+='<td><a href="javascript:doExecCommand(\''+cmd+'\')" class="barbutton" id="'+cmd+'button"><img src="../media/wysiwyg/'+cmd+'.gif" width="23" height="22" alt="'+hint+'" /></a></td>';
    }
    s+="</tr></table>";
    var div = document.createElement("div");
    div.id="wysiwyg_editbar";
    div.innerHTML=s;
    document.body.appendChild(div);
    this.editBar=div;
    this.editBar.style.visibility = 'hidden';
    this.editBar.style.position = 'absolute';
}
