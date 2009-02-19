/**
 * Some utils, stolen from all over the place.
 *
 * @todo needing some concept on how and where javascript libraries must be distributed.
 */

var b = new Array();
b.push('hoi');

function getElementsByClass(node, searchClass, tag) {
    if (tag == null) tag ="*";
    var classElements = new Array();
    var els = node.getElementsByTagName(tag);
    var elsLen = els.length;
    var pattern = new RegExp("(^|\\s)" + searchClass + "(\\s|$)");
    for (i = 0, j = 0; i < elsLen; i++) {
        if ( pattern.test(els[i].className) ) {
            classElements[j] = els[i];
            j++;
        }
    }
    return classElements;
}

function addEventHandler(element, event, method, context) {
    /* method to add an event handler for both IE and Mozilla */
    var wrappedmethod = new ContextFixer(method, context);
    var args = new Array(null, null);
    for (var i=4; i < arguments.length; i++) {
        args.push(arguments[i]);
    };
    wrappedmethod.args = args;
    try {
        if (element.addEventListener) {
            element.addEventListener(event, wrappedmethod.execute, false);
        } else if (element.attachEvent) {
            element.attachEvent("on" + event, wrappedmethod.execute);
        } else {
            throw "Unsupported browser!";
        };
        return wrappedmethod.execute;
    } catch(e) {
        alert('exception ' + e.message + ' while registering an event handler ' +
              'for element ' + element + ', event ' + event + ', method ' + method);
};
};

/* ContextFixer, fixes a problem with the prototype based model

    When a method is called in certain particular ways, for instance
    when it is used as an event handler, the context for the method
    is changed, so 'this' inside the method doesn't refer to the object
    on which the method is defined (or to which it is attached), but for
    instance to the element on which the method was bound to as an event
    handler. This class can be used to wrap such a method, the wrapper
    has one method that can be used as the event handler instead. The
    constructor expects at least 2 arguments, first is a reference to the
    method, second the context (a reference to the object) and optionally
    it can cope with extra arguments, they will be passed to the method
    as arguments when it is called (which is a nice bonus of using
    this wrapper).
*/

function ContextFixer(func, context) {
    /* Make sure 'this' inside a method points to its class */
    this.func = func;
    this.context = context;
    this.args = arguments;
    var self = this;

    this.execute = function() {
        /* execute the method */
        var a = new Array();
        // the first arguments will be the extra ones of the class
        for (var i=0; i < self.args.length - 2; i++) {
            a.push(self.args[i + 2]);
        };
        // the last are the ones passed on to the execute method
        for (var i=0; i < arguments.length; i++) {
            a.push(arguments[i]);
        };
        return self.func.apply(self.context, a);
    };

};
