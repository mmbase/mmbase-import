/* Close window */
function closeYAMMeditor() {
	window.opener.location.reload();
	window.close();
	window.opener.focus();
}
/* Toggle visibility */
function toggle(targetId){
  if (document.getElementById){
  		target = document.getElementById(targetId);
  			if (target.style.display == "none"){
  				target.style.display = "";
  			} else {
  				target.style.display = "none";
  			}
  	}
}
