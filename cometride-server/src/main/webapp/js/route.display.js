$(document).ready(function() {

    var map;
    var center = new google.maps.LatLng(32.9860365, -96.7518621);

    var marker1;
    var markerIncrementing = true;
    var marker2;

    var directionsDisplay;
    var directionsService = new google.maps.DirectionsService();
    var directionsService2 = new google.maps.DirectionsService();

    var driveA = new google.maps.LatLng(32.985559, -96.749478);
    var driveA2 = new google.maps.LatLng(32.985642, -96.749430);
    var loopRd = new google.maps.LatLng(32.991806, -96.753607);
    var wayPts = [];
    wayPts.push({
        location: driveA2,
        stopover: true
    });
    wayPts.push({
        location: loopRd,
        stopover: true
    });

    var lotA = new google.maps.LatLng(32.990111, -96.743875);
    var lotA2 = new google.maps.LatLng(32.989424, -96.745462);
    var library = new google.maps.LatLng(32.987391, -96.747009);
    //var lotG = new google.maps.LatLng(32.984627, -96.745398);
    var outsideLibrary = new google.maps.LatLng(32.987716, -96.746244);
    var wayPts2 = [];
    wayPts2.push({
        location: lotA2,
        stopover: true
    });
    wayPts2.push({
        location: library,
        stopover: true
    });
    //wayPts2.push( { location: lotG, stopover: true } );
    wayPts2.push({
        location: outsideLibrary,
        stopover: true
    });

    function initialize() {
        var mapOptions = {
            zoom: 16,
            center: center,
            disableDefaultUI: true
        };
        map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

        directionsDisplay = new google.maps.DirectionsRenderer({
            polylineOptions: {
                strokeColor: "red"
            },
            suppressMarkers: true,
        });
        directionsDisplay.setMap(map);

        directionsDisplay2 = new google.maps.DirectionsRenderer({
            polylineOptions: {
                strokeColor: "green"
            },
            suppressMarkers: true,
            preserveViewport: true
        });
        directionsDisplay2.setMap(map);


        var request = {
            origin: driveA,
            destination: driveA,
            waypoints: wayPts,
            travelMode: google.maps.TravelMode['DRIVING']
        };

        directionsService.route(request, function(response, status) {
            if (status == google.maps.DirectionsStatus.OK) {
                directionsDisplay.setDirections(response);
            }
        });

        var request2 = {
            origin: lotA,
            destination: lotA,
            waypoints: wayPts2,
            travelMode: google.maps.TravelMode['DRIVING']
        };

        directionsService2.route(request2, function(response, status) {
            if (status == google.maps.DirectionsStatus.OK) {
                directionsDisplay2.setDirections(response);
            }
        });

        createMarkers();
    }

    google.maps.event.addDomListener(window, 'load', initialize);
    
    function createMarkers() {
        marker1 = new google.maps.Marker({
            position: new google.maps.LatLng(32.990709, -96.752627),
            map: map,
            title: 'Cab 1'
        });

        marker2 = new google.maps.Marker({
            position: new google.maps.LatLng(32.987356, -96.746551),
            map: map,
            title: 'Cab 2'
        });

        setTimeout(function() {
            var lat = marker1.getPosition().lat();
            var lng = marker1.getPosition().lng();

            if (markerIncrementing) {
                lat += 0.00005;
            } else {
                lat -= 0.00005;
            }


            if (lat > 32.991771) {
                markerIncrementing = false;
            } else if (lat < 32.990709) {
                markerIncrementing = true;
            }

            marker1.setPosition(new google.maps.LatLng(lat, lng));
            setTimeout(arguments.callee, 300);
        }, 300);
    }
});