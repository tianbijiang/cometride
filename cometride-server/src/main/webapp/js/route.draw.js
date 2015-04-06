/// <reference path="../typings/google.maps.d.ts" />
/// <reference path="site.ts" />
$(document).ready(function() {
    var locationsAdded = 1;
    var map;
    var center = new google.maps.LatLng(32.9860365, -96.7518621);
    var points = [];
    var markers = [];
    var directionsDisplay;

    var routeColor;
    var polylineOptions = {};

    $("#color").change(function() {
        routeColor = $("#color").val();
    });

    function initialize() {
        var mapOptions = {
            zoom: 16,
            center: center,
        };
        map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);

        directionsDisplay = new google.maps.DirectionsRenderer();
    }
    google.maps.event.addDomListener(window, 'load', initialize);

    $("#btn-draw").click(function() {
        map.setOptions({
            draggableCursor: "crosshair"
        });
        google.maps.event.addListener(map, "click", function(location) {
            getLocationInfo(location.latLng, "Location " + locationsAdded);
            locationsAdded++;
        });
    });

    $("#btn-save").click(function() {
        polylineOptions = {
            strokeColor: routeColor,
            strokeOpacity: 0.8
        };

        getDirections();
    });

    $("#btn-reverse").click(function() {
        removeRow(points.length - 1);
    });

    $(".create #hide-sb").click(function() {
        map.setOptions({
            draggableCursor: "default"
        });
        clearPolyLine();
        google.maps.event.clearListeners(map, 'click');
    });

    function getLocationInfo(latlng, locationName) {
        if (latlng != null) {
            var point = {
                LatLng: latlng,
                LocationName: locationName
            };
            points.push(point);
            buildPoints();
        }
    }

    function clearMarkers() {
        for (var i = 0; i < markers.length; i++) {
            markers[i].setMap(null);
        }
        markers = [];
    }

    function buildPoints() {
        clearMarkers();
        //var html = "";
        for (var i = 0; i < points.length; i++) {
            var marker = new google.maps.Marker({
                position: points[i].LatLng,
                title: points[i].LocationName
            });
            markers.push(marker);
            marker.setMap(map);
        }
    }

    function clearPolyLine() {
        points = [];
        buildPoints();
        clearRouteDetails();
    }

    function clearRouteDetails() {
        directionsDisplay.setMap(null);
        //directionsDisplay.setPanel(null);
        //$("#distance").html("");
        //$("#duration").html("");
    }

    function removeRow(index) {
        points.splice(index, 1);
        buildPoints();
        clearRouteDetails();
    }

    function moveRowDown(index) {
        var item = points[index];
        points.splice(index, 1);
        points.splice(index + 1, 0, item);
        buildPoints();
        clearRouteDetails();
    }

    function moveRowUp(index) {
        var item = points[index];
        points.splice(index, 1);
        points.splice(index - 1, 0, item);
        buildPoints();
        clearRouteDetails();
    }

    function getDirections() {
        //var directionsDiv = document.getElementById("directions");
        var directions = new google.maps.DirectionsService();
        directionsDisplay.setMap(map);
        // build array of waypoints (excluding start and end)
        var waypts = [];
        var end = points.length - 1;
        var dest = points[end].LatLng;
        if (document.getElementById("roundTrip").checked) {
            end = points.length;
            dest = points[0].LatLng;
        }
        for (var i = 1; i < end; i++) {
            waypts.push({
                location: points[i].LatLng
            });
        }

        var travelMode = google.maps.TravelMode.DRIVING;

        var request = {
            origin: points[0].LatLng,
            destination: dest,
            waypoints: waypts,
            travelMode: travelMode,
        };
        directions.route(request, function(result, status) {
            if (status === google.maps.DirectionsStatus.OK) {
                //directionsDiv.innerHTML = "";
                var options = {};
                options.directions = result;
                options.map = map;
                options.polylineOptions = polylineOptions;
                //directionsDisplay.setDirections(result);
                directionsDisplay.setOptions(options);
            } else {
                // var statusText = getDirectionStatusText(status);
                // warning.innerHTML = "An error occurred - " + statusText;
            }
        });
    }

    function getDistance(distance) {
        if ($("#directionUnits").val() === "Miles") {
        return Math.round((distance * 0.621371192) / 100) / 10 + " miles";
        }
        else {
            return Math.round(distance / 100) / 10 + " km";
        }
    }

});

/*
<script>
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
    <!--var lotG = new google.maps.LatLng(32.984627, -96.745398);-->
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
    <!--wayPts2.push( { location: lotG, stopover: true } );-->
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
            <!--preserveViewport: true-->
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
    </script>
*/
