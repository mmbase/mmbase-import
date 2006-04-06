/*
  Validation scripts


*/

/*
 * Return true if the input has had input (a radio was selected, text has been entered etc.)
 * throw in the formitem itself, not it's value
 *
 * !!! Only input type="text",input type="checkbox", input type="radio", select and textarea
 * !!! have been implemented so far.
 *
 */
function hasInput(formulier) {
    if (formulier) {
        if (formulier.type) {
            if (formulier.type.toUpperCase()=="TEXT" || formulier.type.toUpperCase()=="TEXTAREA") {
                if (formulier.value&&formulier.value.length>=1) {
                    return true;
                }
            } else if (formulier.nodeName.toUpperCase() == "SELECT") {
                switch (formulier.selectedIndex) {
                case -1:
                    return false;
                case 0:
                    if (formulier.options[formulier.selectedIndex].value) {
                        return true;
                    }
                    break;
                default:
                    return true;
                }
            }
        } else {
            return checkRadio(formulier);
        }
    }
    return false;
}

function checkRadio(radioGroup) {
    // Return true if one of the radiobuttons is checked
    if (radioGroup.length>0) {
        for (i=0;i<radioGroup.length; i++) {
            if (radioGroup[i].checked) {
                return true;
            }
        }
    } else {
        alert("checkRadio: wrong formfield sent to function.");
    }
    return false;
}

/*
 * Return true als een postcode voldoet aan de
 * Nederlandse samenstelling voor een postcode
 */
function checkPostcode(str) {
    reg = /^[0-9]{4} ?[A-Z]{2}$/i;
    return reg.test(str);
}

/*
 * Controleer of een tekst is opgemaakt volgens
 * het patroon van een e-mail adres
 */
function emailCheck(str) {
    /*
     * Regels voor controle van e-mail adressen
     *
     * Het adres moet beginnen met tenminste 1 teken voor de apenstaart (@)
     *
     * Een punt (.) en koppelteken (-) mogen niet naast elkaar staan en ook
     * niet naast de apenstaart (@)
     *
     * Er moet 1 apenstaart (@) in het adres staan
     *
     * Er moeten tenminste twee tekens na de apenstaart (@) staan
     *
     * Het adres moet eindigen op een gekwalificeerde TLD (Top Level Domain)
     * vooraf gegaan door een punt (.)
     *
     */

    var domStr = "";
    // Well known official top level domains
    domStr += "com|edu|gov|int|mil|net|org|arpa|nato|info|aero|biz|coop|museum|name|pro|";
    // Alphabetically split top level domains
    domStr += "ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|";
    domStr += "ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|";
    domStr += "ca|cc|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cs|cu|cv|cx|cy|cz|";
    domStr += "de|dj|dk|dm|do|dz|";
    domStr += "ec|ee|eg|eh|er|es|et|";
    domStr += "fi|fj|fk|fm|fo|fr|fx|";
    domStr += "ga|gb|gd|ge|gf|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|";
    domStr += "hk|hm|hn|hr|ht|hu|";
    domStr += "id|ie|il|in|io|iq|ir|is|it|";
    domStr += "jm|jo|jp|";
    domStr += "ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|";
    domStr += "la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|";
    domStr += "ma|mc|md|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|";
    domStr += "na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|";
    domStr += "om|";
    domStr += "pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|pt|pw|py|";
    domStr += "qa|";
    domStr += "re|ro|ru|rw|";
    domStr += "sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|sv|sy|sz|";
    domStr += "tc|td|tf|tg|th|tj|tk|tm|tn|to|tp|tr|tt|tv|tw|tz|";
    domStr += "ua|ug|uk|um|us|uy|uz|";
    domStr += "va|vc|ve|vg|vi|vn|vu|";
    domStr += "wf|ws|";
    domStr += "ye|yt|yu|";
    domStr += "za|zm|zr|zw";
    // custom domains for testing purposes
    //domStr += "mad|local";

    var re = new RegExp("^[A-Z0-9_]+([\.-]?[A-Z0-9_])*@([A-Z0-9_-]{2,}[\.]{1})+("+ domStr +")$","i");

    return re.test(str);
}
