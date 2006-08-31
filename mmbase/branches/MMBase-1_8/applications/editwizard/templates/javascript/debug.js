var selectedTab = 0;

function init() {
   selectedTab = 0;
   var initTab = readCookie_general("selectedTab", 1);
   if (initTab == 0) {
     initTab = 1;
   }
   changeVisibility(initTab);
}

function changeVisibility(tabno) {
   var obj;
   if (selectedTab > 0) {
      obj = document.getElementById("tab" + selectedTab);
      obj.className = "tab";
      // set object to text-body and hide it 
      obj = document.getElementById("panel" + selectedTab);
      obj.className = "panel_hidden";
   }
   if (tabno != selectedTab) {
      // make tabno hot
      obj = document.getElementById("tab" + tabno);
      if (obj) {
         obj.className = "tab_hot";
      }
      // set object to text-body and show it 
      obj = document.getElementById("panel" + tabno); 
      if (obj) {
         obj.className = "panel_visible";
         selectedTab = tabno;
      }
   }
   else {
      selectedTab = 0;
   }
   writeCookie_general("selectedTab", selectedTab)
}