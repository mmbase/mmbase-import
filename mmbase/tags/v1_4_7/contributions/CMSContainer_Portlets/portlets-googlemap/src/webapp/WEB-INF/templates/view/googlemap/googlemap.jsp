<div id="map" style="width: ${width}; height: ${height};"></div>

<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=${key}"
        type="text/javascript"></script>
<script type="text/javascript">
    //<![CDATA[

    var map;
    var address = '${address}';
    var info = '${info}';

    function load() {
        if (GBrowserIsCompatible()) {
            map = new GMap2(document.getElementById("map"));
            map.addControl(new GSmallMapControl());
            map.setCenter(new GLatLng(52.376619, 4.887438), 13);
            geocoder = new GClientGeocoder();

            geocoder.getLatLng(            
                    address,
                    function(point) {
                        if (point) {
                            map.setCenter(point, 13);
                            var marker = new GMarker(point);
                            map.addOverlay(marker);
                            marker.openInfoWindowHtml(info);
                        }
                    }
                    );
        }
    }

    //]]>

</script>