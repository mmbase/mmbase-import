/**
 * Javascript for wiki-areas.
 *  - arrange a preview for the mantioned object below the cursor
 *  - arrange that search/related tool pastes the found object in understood wiki-syntax into the current
 *    wiki area
 */


function MMWiki(div) {
    this.div = div;
    var self = this;
    if (this.relater == null) {
        $(this.div).bind("mmsrRelaterReady", function (e, r) {
            self.relater = r;
            self.ready();
        });
    } else {
        this.relater = div.relater;
        self.ready();
    }
}
MMWiki.prototype.setup = false;
MMWiki.prototype.lastTouched = null;


MMWiki.prototype.findMmxfFields = function() {
    var self = this;
    if (!MMWiki.prototype.setup) {
        $("body").find("textarea.mm_validate").each(function() {
            if (self.relater.validator.isXml(this)) {
                self.setupTextAreaHooks(this);
                self.setupPreview(this);
            }

        });
        MMWiki.prototype.setup = true;
    }
}

MMWiki.prototype.setupPreview = function(textarea) {

    // This function shows the  object under the cursor
    var view = function() {
        var begin = this._selectionStart;
        var end = begin;
        var c = this.value.charAt(begin);

        //alert(" " + begin + " " + this.value.charAt(begin));
        while (begin > 0 && c >= 0 && c <= 9) {
            begin--;
            c = this.value.charAt(begin);
        }
        begin++;

        c = this.value.charAt(end);
        while (end < this.value.length && c >= '0' && c <= '9') {
            end++;
            c = this.value.charAt(end);
        }
        if (end > begin) {
            var number = this.value.substring(begin, end);
            $("body").find(".show_node").each(function() {
                if (this.number != number) {
                    this.number = number;
                    if (this.originalContent == null) {
                        this.originalContent = $(this).text();

                    }
                    $(this).load($("#previewUrl")[0].href, {objectnumber: number});

                }
            });

        } else {
            //alert("begin: " + begin + " end" + end);
            $("body").find(".show_node").each(function() {
                if (this.number != number) {
                    this.number = number;
                    var content = "<p>" + this.originalContent + "</p>";
                    $(this).empty();
                    $(this).append($(content));
                }
            });
        }

    };

    $(textarea).keyup(view);
    $(textarea).click(view);

}

MMWiki.prototype.setupTextAreaHooks = function (textarea) {

    var self = this;
    var setstartend = function() {

        //Lots of hackery to get a working selectionStart, selectionEnd.
        //Use _selectionStart, _selectionEnd, which shoudl always contain the correct values.
        if (this.selectionStart) {
            this._selectionStart = this.selectionStart;
            this._selectionEnd   = this.selectionEnd;
        } else {
            this._selectionStart = self.getSelectionStart(this);
            this._selectionEnd   = self.getSelectionEnd(this);
        }
        MMWiki.prototype.lastTouched = this;
        //alert(this._selectionStart + ":" + this._selectionEnd);

    };
    if (MMWiki.prototype.lastTouched == null) MMWiki.prototype.lastTouched = textarea; // Default to the first appearing one

    $(textarea).keyup(setstartend);
    $(textarea).click(setstartend);
    $(textarea).dblclick(setstartend);

};


MMWiki.prototype.getSelectionStart = function(input) {
    if (input.selectionStart) {
        return input.selectionStart;
    } else {
        input.focus();
        if (document.selection) {
            var range = document.selection.createRange();
            var drange = range.duplicate();
            drange.moveToElementText(input);
            drange.setEndPoint("EndToEnd", range);
            input._currentPosition = drange.text.length - range.text.length;
            return input._currentPosition;
        }
    }
}

MMWiki.prototype.getSelectionEnd = function(input) {
    if (input.selectionEnd) {
        return input.selectionEnd;
    } else {
        input.focus();
        var range = document.selection.createRange();
        return this.getSelectionStart(input) + range.text.length;
    }
}

MMWiki.prototype.setupRelateCallBack = function() {
    var self = this;
    $(this.div).bind("mmsrRelate", function(ev, tr) {
        if ($(tr).parents("ul.idrels").length > 0) {
            var nodeNumber = this.relater.getNumber(tr);
            var lastTouched = MMWiki.prototype.lastTouched;
            // now paste this into the wiki-area
            var selectionStart = lastTouched._selectionStart;
            var selectionEnd = lastTouched._selectionEnd;
            //alert(selectionStart + ":" + selectionEnd);
            var before = lastTouched.value.substring(0, selectionStart);
            var after = lastTouched.value.substring(selectionEnd, lastTouched.value.length);
            var selection = lastTouched.value.substring(selectionStart, selectionEnd);
            lastTouched.value = before + "[" + nodeNumber + (selection.length > 0 ? ":" : "") + selection + "]" + after;
        }
    });
}

MMWiki.prototype.setupTabs = function() {
    // set up the tabs
    $("div.relations.tabs ul > li:first > a.toggle").addClass("current");
    $("div.relations.tabs ul > li:gt(0) > div").hide();

    $("div.relations.tabs ul > li > a.toggle").click(function(e) {
        $("div.relations.tabs ul > li > div").hide();
        $("div.relations.tabs ul > li > a.toggle").removeClass("current");
        $(e.target).parent().children("div").show();
        $(e.target).addClass("current");
    });
}

MMWiki.prototype.ready = function() {
    this.findMmxfFields();
    this.setupRelateCallBack();
    this.setupTabs();
}



$(document).ready(function() {
    $("body").find("div.mm_related").each(function() {
        var wiki = new MMWiki(this);
    });
});
