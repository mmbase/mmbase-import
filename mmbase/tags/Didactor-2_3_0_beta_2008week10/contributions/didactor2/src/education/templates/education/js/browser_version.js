   function browserVersion()
   {
      var browser = new Array ();

//      alert(navigator.appName);
//      alert(navigator.userAgent);
//      alert(navigator.appVersion);

      if (navigator.appName.indexOf("Netscape") != -1)
      {
         //Mozilla or Netscape Navigator
         if (navigator.userAgent.indexOf("Gecko") != -1)
         {
            if (navigator.userAgent.match("Netscape[0-9]") || navigator.userAgent.match("Netscape/[0-9]"))
            {
               //Netscape
               browser[0] = "NN";
               var version = navigator.userAgent.split('/');
               var tmp = version[3];
               version = tmp.split('.');
               browser[1] = version[0];
               browser[2] = version[1].substr(0,1);
            }
            else
            {
               //Mozilla
               browser[0] = "Mozilla";
               var version = navigator.userAgent.split(';');
               var tmp = version[4];
               version = tmp.split(')');
               tmp = version[0];
               version = tmp.split('.');
               version[0] = version[0].substr(4);
               browser[1] = version[0];
               browser[2] = version[1];
            }
         }
         else
         {
            //Old Netscape Nvagator, =< 4.8
            browser[0] = "NN";
            var version = navigator.appVersion;
            var tmp = version.split('.');
            tmp2 = tmp[1].split(' ');
            browser[1] = tmp[0];
            browser[2] = tmp2[0];
         }
      }
      if (navigator.appName.indexOf("Internet Explorer") != -1)
      {
         //IE
         browser[0] = "IE";
         var version = navigator.userAgent.split(';');
         var tmp = version[1].substr(6);
         tmp = tmp.split('.');
         browser[1] = tmp[0];
         browser[2] = tmp[1];
      }
      if (navigator.userAgent.indexOf("Opera") != -1)
      {
         //Opera
         browser[0] = "Opera";
         var version = navigator.userAgent.search("[oO]pera ");
         version = navigator.userAgent.substr(version + 6);
         var tmp = version.search(" ");
         version = version.substr(0,tmp);
         tmp = version.split('.');
         browser[1] = tmp[0];
         browser[2] = tmp[1];
      }

      return browser;
   }
