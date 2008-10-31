// contains arrays of banners for a carrousel
var carrouselBanners = new Array();
// contains the positions of the carrousels from carrouselBannes
var carrouselPositions = new Array();
// contains arrays of intervals matching the banners from carrouselBanners
var carrouselIntervals = new Array();

function initBannerCarrousel() {
    // alert("initBannerCarrousel");
    // initialize all carrousels
    var oldwindowtitle = document.title;
    for(var i = 0; i < carrouselBanners.length; i++) {
        carrousel(i,0,oldwindowtitle);
    }     
}

addLoadEvent(initBannerCarrousel);
    
function carrousel(index, c, windowtitleparam) {
    
    // get the array of banners for this carrousel
    var banners = carrouselBanners[index]
    // get the position of this carrousel
    var position = carrouselPositions[index];
    // get the array of intervals matching the banners
    var intervals = carrouselIntervals[index];
    var counter = c;
    
    // reset the counter if the last banner is reached
    if (counter==banners.length) counter=0;

    // set the html of the bannerposition to the proper banner  
    document.getElementById(position).innerHTML=banners[counter];

    // get the interval for this banner
    var interval = intervals[counter];
    
    counter++;
    
    if (banners.length > 1) {
        // call this method again for the next banner and a new interval
        setTimeout("carrousel(" + index + "," + counter + ",'" + windowtitleparam + "')",interval)
    }
    
    // fix for LCM-193, caused by a known bug in IE: it replaces  the window title with # when loading a flash movie
    if ( banners.length > 0) {    	
    	document.title  = windowtitleparam; 
    }
}
