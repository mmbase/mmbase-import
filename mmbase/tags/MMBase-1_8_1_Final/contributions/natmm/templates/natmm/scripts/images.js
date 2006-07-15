<SCRIPT language=JavaScript>
<!--
function MM_preloadimages() { //v3.0
  var d=document; if(d.media/images/ngb){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadmedia/images/ngb.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}

function simgr() {
  if (document.simgdata != null)
    for (var i=0; i<(document.simgdata.length-1); i+=2)
      document.simgdata[i].src = document.simgdata[i+1];
}

function simages() {
  var i,target,j=0,sarray=new Array,oarray=document.simgdata;
  for (i=0; i < (simages.arguments.length-2); i+=3) {
    target = eval(simages.arguments[(navigator.appName == 'Netscape')?i:i+1])
    if (target != null) {
      sarray[j++] = target;
      sarray[j++] = (oarray==null || oarray[j-1]!=target)?target.src:oarray[j];
      target.src = simages.arguments[i+2];
  } }
  document.simgdata = sarray;
}

//-->
</SCRIPT>