function check(el, checked) {
  var pattern = new RegExp('\\bselected\\b');
  if (! checked && pattern.test(el.className)) {
    el.className = el.className.replace(pattern, "");

  } else if (checked && ! pattern.test(el.className)) {
    el.className += " selected";
  }
}

