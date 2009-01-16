function insertsmiley(id) {
    var txtarea = document.posting.body;
    id = ' ' + id + ' ';
    if (txtarea.createTextRange && txtarea.caretPos) {
        var caretPos = txtarea.caretPos;
        caretPos.text = caretPos.text.charAt(caretPos.text.length - 1) == ' ' ? caretPos.text + id + ' ' : caretPos.text + id;
        txtarea.focus();
     } else {
        txtarea.value  += id;
        txtarea.focus();
    }
}
