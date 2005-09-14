var divids = 0;

/**
 * This tool is to create 'blocks'
 */

function DivsTool() {
    /* tool to add 'divs' */

    this.initialize = function(editor) {
        /* attach the event handlers */
        this.editor = editor;
        this.editor.logMessage(_("Div tool initialized"));
    };


    this.createDiv = function(divclass) {
        /* create a div */
        var currnode = this.editor.getSelectedNode();

        var doc = this.editor.getInnerDocument();
        var div = doc.createElement('div');
        div.id = "createddiv_" + (divids++);
        if (divclass) {
            div.className = divclass;
        };
        var text = this.editor.getSelection();
        var child;
        if (! text || text == '') {
            child = doc.createElement("p");
            child.appendChild(doc.createTextNode(".")); // should not be empty, otherwise doesn't work well in firefox
        } else {
            child = doc.createTextNode(text);
        }        
        var marker = doc.createTextNode(""); 
        div.appendChild(child);
        var currp = this.editor.getNearestParentOfType(currnode, 'p');
        if (currp) {
            this.editor.logMessage(_("Found paragraph"));
            currp.parentNode.insertBefore(div, currp);
            this.editor.insertNodeAtSelection(doc.createTextNode(""), 1); 
        } else {
            this.editor.logMessage(_("Didn't find paragraph"));
            div = this.editor.insertNodeAtSelection(div, 1);
        }
        this.editor.insertNodeAtSelection(marker, 1);

        this.editor.logMessage(_("Div inserted"));
        this.editor.updateState();
        return div;
    };

    this.setDivClass = function(divclass) {
        /* set the class of the selected image */
        var currnode = this.editor.getSelectedNode();
        var currdiv = this.editor.getNearestParentOfType(currnode, 'div');
        if (currdiv) {
            currdiv.className = divclass;
        };
    };

}

DivsTool.prototype = new KupuTool;

function DivsToolBox(insertbuttonid, classselectid, toolboxid, plainclass, activeclass) {

    this.insertbutton = getFromSelector(insertbuttonid);
    this.classselect  = getFromSelector(classselectid);
    this.toolboxel    = getFromSelector(toolboxid);
    this.plainclass   = plainclass;
    this.activeclass  = activeclass;

    this.initialize = function(tool, editor) {
        this.tool = tool;
        this.editor = editor;
        addEventHandler(this.classselect, "change", this.setDivClass, this);
        addEventHandler(this.insertbutton, "click", this.addDiv, this);
    };

    this.updateState = function(selNode, event) {
        /* update the state of the toolbox element */
        var divel = this.editor.getNearestParentOfType(selNode, 'div');
        if (divel) {
            // check first before setting a class for backward compatibility
            if (this.toolboxel) {
                this.toolboxel.className = this.activeclass;
                var divclass = divel.className ? divel.className : 'note';
                selectSelectItem(this.classselect, divclass);
            };
        } else {
            if (this.toolboxel) {
                this.toolboxel.className = this.plainclass;
            };
        };
    };

    this.addDiv = function() {
        /* add an div */
        var sel_class = this.classselect.options[this.classselect.selectedIndex].value;
        this.tool.createDiv(sel_class);
        this.editor.focusDocument();
    };

    this.setDivClass = function() {
        /* set the class for the current image */
        var sel_class = this.classselect.options[this.classselect.selectedIndex].value;
        this.tool.setDivClass(sel_class);
        this.editor.focusDocument();
    };
};

ImageToolBox.prototype = new KupuToolBox;

