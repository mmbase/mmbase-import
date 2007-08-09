function giveInfo(infoIndex, urlPopup)
{
  var popupWindow;
  switch (infoIndex) {
    case 0: wwidth = 560; wheight = 136; break;
    case 1: wwidth = 560; wheight = 136; break;
    case 2: wwidth = 560; wheight = 124; break;
    case 3: wwidth = 466; wheight = 38; break;
  }
  wleft = (screen.width - wwidth) / 2;
  wtop = (screen.height - wheight) / 2;

  if (wleft < 0) {
    wleft = 0;
  }
  if (wtop < 0) {
    wtop = 0;
  }
  
  var props = 'scrollBars=no,resizable=no,toolbar=no,status=0,minimize=no,statusbar=0,menubar=no,directories=no,width='+wwidth+',height='+wheight+',top='+wtop+',left='+wleft;
  urlPopup += '/nmintra/includes/vastgoed/vastgoed_alert.jsp?messageNumber='+infoIndex;
  popupWindow = window.open(urlPopup, 'Informatie', props);
  popupWindow.focus();
}