var selected;
function selectMenu(item) {
   if(selected != undefined && selected != item) {
      selected.className = '';
   }
   item.className = 'active';
   selected = item;
}

function initMenu() {

		var menuFrame = parent.frames[1];
		if (menuFrame == undefined) {
			menuFrame = document;
		}
		var framehref = menuFrame.location.href;
      selectMenuByUrl(framehref);
}

function selectMenuByUrl(url) {
   var cnavElement = document.getElementById('menu');
   if (cnavElement) {

      var listItems = cnavElement.getElementsByTagName('li');
      var numberOflistItems = listItems.length;

      var listItemFound = false;
      for(var i=0; i < numberOflistItems; i++) {
         var listItem = listItems[i];
         var ahref = listItem.getElementsByTagName('a')[0].href;
         if (url.indexOf(ahref) != -1) {
            selectMenu(listItem);
            listItemFound = true;
            break;
         }
      }
      if (!listItemFound) {
         var listItem = listItems[0];
         selectMenu(listItem);
      }
   }
   else {
      alert('listItem div not found');
   }
}