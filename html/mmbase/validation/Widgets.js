/**
 * Javascript to mold default html input widgets (e.g. as made by <mm:fieldinfo type="input" />) to things regularly requested by customers.
 *
 * @TODO It would be nice if some of these methods (like 'enumerationSuggestion' for non-enforces enumeration) be called automaticly.
 *        Currently the moldingprocess must be bootstrapped manually, per input box.

 * Supported are
 *  -  Widgets.prototype.enumerationSuggestion(selector):  Makes single selection only a suggestion, meaning that the value 'OTHER' gives the user the possibility to type a value herself
 *  -  Widgets.prototype.boxes(selector):  Makes select into a list of checkboxes (multiple) or radioboxes (single)
 *  -  Widgets.prototype.twoMultiples(selector):  Splits up multiple selection into 2 boxes, the left one containing the selected values, the right one the optiosn which are not selected.
 *
 * @version $Id: Widgets.js,v 1.1 2008-10-29 18:09:57 michiel Exp $   BETA
 * @author Michiel Meeuwissen

 */




function Widgets() {
}


/**
 * This function is used by {@link $enumerationSuggestion}.
 */

Widgets.prototype.switchEnumerationSuggestion = function(ev) {
    var target = ev.target;
    if ('OTHER' == target.value) {
        var text = $("<input type='text'> </input>");
        var t = $(target);
        t.after(text);
        text.attr('class', t.attr('class'));
        text.attr('id', t.attr('id'));
        text.attr('name', t.attr('name'));
        text.attr('value', $(target.options[target.selectedIndex]).text());
        t.remove();
        text[0].original = target;
        text.keyup(function(ev) {
            if (ev.target.value == '') {
                var t = ev.target;
                setTimeout(function() {
                    if (t.value == '') {
                        t.original.selectedIndex = 0;
                        $(t).after(t.original);
                        $(t).remove();
                        $(t.original).change(Widgets.prototype.swichEnumerationSuggestion);
                    }
                }, 2000);
            }
        });
    }
};


/**
 * Makes a select only a suggestion. If the user selects the option with value 'OTHER', the select is
 * automaticly changed into a text input box. (and back if this input box is made empty and left that way for 2 seconds).
 */
Widgets.prototype.enumerationSuggestion = function(selector) {
    $(document).ready(function() {
        $(selector).change(Widgets.prototype.switchEnumerationSuggestion);
    });
};


/**
 * Utility function to just convert an Object to a comma separated list
 */
Widgets.prototype.setToString = function(set) {
    var v = "";
    for (var i in set) {
        if (set[i] == true) {
            if (v.length > 0) v += ",";
            v += i;
        }
    }
    return v;
};

Widgets.prototype.singleBoxes = function(select) {
    var t = $(select);
    var text = $("<div class='mm_boxes'></div>");
    var first = true;
    $(select.options).each(function() {
        if (! $(this).hasClass("head")) {
            var nobr = $("<nobr />");
            var input = $("<input type='checkbox' value='" + this.value + "' />");
            nobr.append(input).append($(this).text());
            text.append(nobr);
            input.attr('name', t.attr('name'));
            if (this.selected) {
                input.attr('checked', 'checked');
                hidden[0].values[this.value] = true;
            }
            first = false;
        } else if ($(this).text() == "--") {
            if (! first) {
                text.append("<br />");
            }
        } else {
            var span = $("<span class='head' />");
            text.append(span);
            span.text($(this).text());
            first = false;

        }
    });
    t.after(text);
    t.remove();
}
Widgets.prototype.multipleBoxes = function(select) {
    var t = $(select);
    var text = $("<div class='mm_boxes'></div>");
    var hidden = $("<input type='hidden' />");
    text.append(hidden);
    hidden.attr("name", t.attr("name"));
    hidden[0].values = new Object();
    var first = true;
    var div = $("<div />");
    text.append(div);
    $(select.options).each(function() {
        if (! $(this).hasClass("head")) {
            var nobr = $("<nobr />");
            var input = $("<input type='checkbox' value='" + this.value + "' />");
            nobr.append(input).append($(this).text());
            div.append(nobr);
            input.attr('name', t.attr('name') + "___" + this.value);
            if (this.selected) {
                input.attr('checked', 'checked');
                hidden[0].values[this.value] = true;
            }
            input.change(function() {
                hidden[0].values[this.value] = this.checked;
                hidden[0].value = Widgets.prototype.setToString(hidden[0].values);

            });
            first = false;
        } else if ($(this).text() == "--") {
            if (! first) {
                div.append("<br />");
            }
        } else {
            if (! first) {
                div = $("<div />");
                text.append(div);
            }
            var span = $("<span class='head' />");
            div.append(span);
            span.text($(this).text());
            first = false;
        }
    });
    hidden.attr("value", Widgets.prototype.setToString(hidden[0].values));
    t.after(text);
    t.remove();
}

/**
 * Molds a select input to a list of checkboxes (for multiple selections) or radiobuttons (for single selections).
 */
Widgets.prototype.boxes = function(selector, multiple) {
    $(document).ready(function() {
        $(selector).each(function() {
            if (multiple || this.multiple) {
                Widgets.prototype.multipleBoxes(this);
            } else {
                Widgets.prototype.singleBoxes(this);
            }

        });
    });
};

/**
 * Sets up the dbl-click event on option to move it from a to b.
 */
Widgets.prototype.aToB = function(option, a, b) {
    $(option).dblclick(function() {
        var options = b[0].options;
        var appended = false;
        for(var i = 0; i < options.length; i++) {
            var o = options[i];
            if (o.originalPosition > option.originalPosition) {
                $(o).before(option);
                appended = true;
                break;
            }
        }
        if (! appended) {
            b.append(option);
        }
        Widgets.prototype.aToB(option, b, a);
    });
}

Widgets.prototype.twoMultiples = function(selector) {
    $(document).ready(function() {
        $(selector).each(function() {
            var t = $(this);
            var text  = $("<div class='mm_twomultiples'></div>");
            var left  = $("<select multiple='multiple' />");
            left.attr("name", t.attr("name"));
            left.attr("id", t.attr("id"));
            t.parents("form").submit(function() {
                $(left[0].options).each(function() {
                    this.selected = true;
                });
            });
            var right = $("<select multiple='multiple' />");
            $(this.options).each(function() {
                this.originalPosition = this.index;
            });
            $(this.options).each(function() {
                if (this.value == null || this.value == '') {
                } else if (this.selected) {
                    left.append(this);
                    Widgets.prototype.aToB(this, left, right);
                } else {
                    right.append(this);
                    Widgets.prototype.aToB(this, right, left);
                }
            });
            var nobr = $("<nobr />");
            var buttonToLeft  = $("<input type='button' value=' &lt; ' />")
            buttonToLeft.click(function() {
                $(right[0].options).each(function() {
                    if (this.selected) {
                        $(this).dblclick();
                    }
                });
            });
            var buttonToRight = $("<input type='button' value=' &gt; ' />")
            buttonToRight.click(function() {
                $(left[0].options).each(function() {
                    if (this.selected) {
                        $(this).dblclick();
                    }
                });
            });
            text.append(left).append(buttonToLeft).append(buttonToRight).append(right);
            t.after(text);
            t.remove();
        });

    });
};
