
function   isEmail(s) {
   s = trim(s);
   var p = /^[_\.0-9a-zA-Z-]+@([0-9a-z][0-9a-z-]+\.){1,4}[a-z]{2,3}$/i;  
   return p.test(s);
}

function trim(str) {
   var s = str.replace(/^(\s)*/, '');
   s = s.replace(/(\s)*$/, '');
   return s;
}