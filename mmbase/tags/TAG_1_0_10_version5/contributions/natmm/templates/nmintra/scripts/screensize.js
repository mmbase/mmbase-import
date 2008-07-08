function setScreenSize() {
    if (readCookie('screenWidth')==null || readCookie('screenHeight')==null ) {         
        var screenW = 0; var screenH = 0;
        if (parseInt(navigator.appVersion)>3) {
             screenW = screen.width;
             screenH = screen.height;
        }
        else if (navigator.appName == "Netscape" 
            && parseInt(navigator.appVersion)==3
            && navigator.javaEnabled()
           ) 
        {    var jToolkit = java.awt.Toolkit.getDefaultToolkit();
             var jScreenSize = jToolkit.getScreenSize();
             screenW = jScreenSize.width;
             screenH = jScreenSize.height;
        }
        saveCookie('screenWidth',screenW,1);
        saveCookie('screenHeight',screenH,1);
   }
}

