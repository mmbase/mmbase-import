 function alphaImages(){
// alert("doing alpha!");
   var rslt = navigator.appVersion.match(/MSIE (\d+\.\d+)/, '');
   var itsAllGood = (rslt != null && Number(rslt[1]) >= 5.5);

   if (itsAllGood) {
     for (i=0; i<document.all.length; i++){
       var el = document.all[i];
       var bg = el.currentStyle.backgroundImage;
       if(bg){
         if (bg.match(/\.png/i) != null){
           var mypng = bg.substring(5,bg.length-2);
           el.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+mypng+"', sizingMethod='crop')";
           el.style.backgroundImage = "URL('"+mypng.substring(0,mypng.lastIndexOf("/")+1)+"empty.gif')";
         }
       }
       var mypng = el.src;
       if(mypng && mypng.indexOf(".png") != -1 && mypng.indexOf("/icons/") != -1) {
         el.src = mypng.substring(0,mypng.lastIndexOf("/")+1)+"empty.gif";
         el.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+mypng+"', sizingMethod='crop')";
       }
     }
   }
}


window.onload = alphaImages;