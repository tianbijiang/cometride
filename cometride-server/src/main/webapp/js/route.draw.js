/// <reference path="../typings/google.maps.d.ts" />
/// <reference path="site.ts" />
$(document).ready(function() {
    var locationsAdded = 1;
    var map;
    var center = new google.maps.LatLng(32.9860365, -96.7518621);
    var points = [];
    var markers = [];
    var directionsDisplay;

    var routeColor = "rgb(224, 102, 102)";
    var polylineOptions = {};

    //displayroute
    var marker1;
    var markerIncrementing = true;
    var marker2;
    var directionsService3 = new google.maps.DirectionsService();
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
    //displayroute

    $(".create #color").spectrum({
        color: "rgb(224, 102, 102)",
        showPalette: true,
        palette: [
            ["rgb(0, 0, 0)", "rgb(67, 67, 67)", "rgb(102, 102, 102)",
                "rgb(204, 204, 204)", "rgb(217, 217, 217)", "rgb(255, 255, 255)"
            ],
            ["rgb(152, 0, 0)", "rgb(255, 0, 0)", "rgb(255, 153, 0)", "rgb(255, 255, 0)", "rgb(0, 255, 0)",
                "rgb(0, 255, 255)", "rgb(74, 134, 232)", "rgb(0, 0, 255)", "rgb(153, 0, 255)", "rgb(255, 0, 255)"
            ],
            ["rgb(230, 184, 175)", "rgb(244, 204, 204)", "rgb(252, 229, 205)", "rgb(255, 242, 204)", "rgb(217, 234, 211)",
                "rgb(208, 224, 227)", "rgb(201, 218, 248)", "rgb(207, 226, 243)", "rgb(217, 210, 233)", "rgb(234, 209, 220)",
                "rgb(221, 126, 107)", "rgb(234, 153, 153)", "rgb(249, 203, 156)", "rgb(255, 229, 153)", "rgb(182, 215, 168)",
                "rgb(162, 196, 201)", "rgb(164, 194, 244)", "rgb(159, 197, 232)", "rgb(180, 167, 214)", "rgb(213, 166, 189)",
                "rgb(204, 65, 37)", "rgb(224, 102, 102)", "rgb(246, 178, 107)", "rgb(255, 217, 102)", "rgb(147, 196, 125)",
                "rgb(118, 165, 175)", "rgb(109, 158, 235)", "rgb(111, 168, 220)", "rgb(142, 124, 195)", "rgb(194, 123, 160)",
                "rgb(166, 28, 0)", "rgb(204, 0, 0)", "rgb(230, 145, 56)", "rgb(241, 194, 50)", "rgb(106, 168, 79)",
                "rgb(69, 129, 142)", "rgb(60, 120, 216)", "rgb(61, 133, 198)", "rgb(103, 78, 167)", "rgb(166, 77, 121)",
                "rgb(91, 15, 0)", "rgb(102, 0, 0)", "rgb(120, 63, 4)", "rgb(127, 96, 0)", "rgb(39, 78, 19)",
                "rgb(12, 52, 61)", "rgb(28, 69, 135)", "rgb(7, 55, 99)", "rgb(32, 18, 77)", "rgb(76, 17, 48)"
            ]
        ]
    });
    $(".edit #color").spectrum({
        color: "rgb(224, 102, 102)",
        showPalette: true,
        palette: [
            ["rgb(0, 0, 0)", "rgb(67, 67, 67)", "rgb(102, 102, 102)",
                "rgb(204, 204, 204)", "rgb(217, 217, 217)", "rgb(255, 255, 255)"
            ],
            ["rgb(152, 0, 0)", "rgb(255, 0, 0)", "rgb(255, 153, 0)", "rgb(255, 255, 0)", "rgb(0, 255, 0)",
                "rgb(0, 255, 255)", "rgb(74, 134, 232)", "rgb(0, 0, 255)", "rgb(153, 0, 255)", "rgb(255, 0, 255)"
            ],
            ["rgb(230, 184, 175)", "rgb(244, 204, 204)", "rgb(252, 229, 205)", "rgb(255, 242, 204)", "rgb(217, 234, 211)",
                "rgb(208, 224, 227)", "rgb(201, 218, 248)", "rgb(207, 226, 243)", "rgb(217, 210, 233)", "rgb(234, 209, 220)",
                "rgb(221, 126, 107)", "rgb(234, 153, 153)", "rgb(249, 203, 156)", "rgb(255, 229, 153)", "rgb(182, 215, 168)",
                "rgb(162, 196, 201)", "rgb(164, 194, 244)", "rgb(159, 197, 232)", "rgb(180, 167, 214)", "rgb(213, 166, 189)",
                "rgb(204, 65, 37)", "rgb(224, 102, 102)", "rgb(246, 178, 107)", "rgb(255, 217, 102)", "rgb(147, 196, 125)",
                "rgb(118, 165, 175)", "rgb(109, 158, 235)", "rgb(111, 168, 220)", "rgb(142, 124, 195)", "rgb(194, 123, 160)",
                "rgb(166, 28, 0)", "rgb(204, 0, 0)", "rgb(230, 145, 56)", "rgb(241, 194, 50)", "rgb(106, 168, 79)",
                "rgb(69, 129, 142)", "rgb(60, 120, 216)", "rgb(61, 133, 198)", "rgb(103, 78, 167)", "rgb(166, 77, 121)",
                "rgb(91, 15, 0)", "rgb(102, 0, 0)", "rgb(120, 63, 4)", "rgb(127, 96, 0)", "rgb(39, 78, 19)",
                "rgb(12, 52, 61)", "rgb(28, 69, 135)", "rgb(7, 55, 99)", "rgb(32, 18, 77)", "rgb(76, 17, 48)"
            ]
        ]
    });

    $(".create #color").change(function() {
        routeColor = $(".create #color").val();
    });
    $(".edit #color").change(function() {
        routeColor = $(".edit #color").val();
    });

    function initialize() {
        var mapOptions = {
            zoom: 16,
            center: center
        };
        map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);

        directionsDisplay = new google.maps.DirectionsRenderer();

        //displayroute
        directionsDisplay3 = new google.maps.DirectionsRenderer({
            polylineOptions: {
                strokeColor: "red"
            },
            suppressMarkers: true,
        });
        directionsDisplay3.setMap(map);

        directionsDisplay2 = new google.maps.DirectionsRenderer({
            polylineOptions: {
                strokeColor: "green"
            },
            suppressMarkers: true,
            preserveViewport: true
        });
        directionsDisplay2.setMap(map);

        var request3 = {
            origin: driveA,
            destination: driveA,
            waypoints: wayPts,
            travelMode: google.maps.TravelMode['DRIVING']
        };

        directionsService3.route(request3, function(response, status) {
            if (status == google.maps.DirectionsStatus.OK) {
                directionsDisplay3.setDirections(response);
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
        //displayroute
    }

    google.maps.event.addDomListener(window, 'load', initialize);

    $(".create-btn").click(function() {                
        //displayroute
        directionsDisplay2.setMap(null);
        directionsDisplay3.setMap(null);
        marker1.setMap(null);
        marker2.setMap(null);
    });

    $(".create #hide-sb").click(function(){
        directionsDisplay2.setMap(map);
        directionsDisplay3.setMap(map);
        marker1.setMap(map);
        marker2.setMap(map);
    });

    $("#btn-draw").click(function() {
        map.setOptions({
            draggableCursor: "crosshair"
        });
        google.maps.event.addListener(map, "click", function(location) {
            getLocationInfo(location.latLng, "Location " + locationsAdded);
            locationsAdded++;
        });
    });

    $("#btn-actual-draw").click(function() {
        polylineOptions = {
            strokeColor: routeColor
        };

        getDirections();

    });

    $("#btn-reverse").click(function() {
        removeRow(points.length - 1);
    });

    $("#btn-save").click(function() {
        sendRoute();
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
        } else {
            return Math.round(distance / 100) / 10 + " km";
        }
    }

    function sendRoute() {
        var name = $(".create #name").val();

        var color = "rgb(224, 102, 102)";
        if($(".create #color").val()!=""){
            color = $(".create #color").val();
        } //rgb(255,0,0)
        color = colorToHex(color);

        var status = $(".create #status").val();

        var waypoints = [];
        for (var i = 0; i < points.length; i++) {
            waypoints.push({
                lat: points[i].LatLng.lat(),
                lng: points[i].LatLng.lng()
            });
        }
        if (document.getElementById("roundTrip").checked) {
            waypoints[points.length] = waypoints[0];
        }

        var d = $(".create #day-picker button").html();
        var days = d.split(", ");

        var times = [];
        for (var i = 0; i < $(".create #time-picker > div").length; i++) {
            var start = $(".create #timepicker-set-" + (i + 1) + " .start-time").val();
            var end = $(".create #timepicker-set-" + (i + 1) + " .end-time").val();
            times.push({
                start: start,
                end: end
            });
        }

        var startDate;
        var endDate;
        if (!document.getElementById("temporary-flag").checked) {
            startDate = "";
            endDate = "";
        } else {
            startDate = $(".create #start-date").val();
            endDate = $(".create #end-date").val();
        }

        var dataString = {
            name: name,
            color: color,
            status: status,
            waypoints: waypoints,
            days: days,
            times: times,
            startDate: startDate,
            endDate: endDate
        };
        dataString = JSON.stringify(dataString);
        alert(dataString);

        $.ajax({
            contentType: 'application/json',
            type: "POST",
            url: "http://cometride.elasticbeanstalk.com/api/route",
            data: dataString,
            dataType: "json",
            cache: false,
            // beforeSend: function() {
            //     $("#login").val('Connecting...');
            // },
            success: function(data) {
                if (data) {
                    alert(data);
                } else {
                    alert("Server error.");
                }
            }
        });
        return false;
    }

    function colorToHex(color) {
        if (color.substr(0, 1) === '#') {
            return color;
        }
        var digits = /(.*?)rgb\((\d+), (\d+), (\d+)\)/.exec(color);

        var red = parseInt(digits[2]);
        var green = parseInt(digits[3]);
        var blue = parseInt(digits[4]);

        var rgb = blue | (green << 8) | (red << 16);
        return digits[1] + '#' + rgb.toString(16);
    };

    function createMarkers() {
        marker1 = new google.maps.Marker({
            position: new google.maps.LatLng(32.990709, -96.752627),
            map: map,
            title: 'Cab 1',
            icon: "img/cab_green.png"
        });

        marker2 = new google.maps.Marker({
            position: new google.maps.LatLng(32.987356, -96.746551),
            map: map,
            title: 'Cab 2',
            icon: "img/cab_red.png"
        });

        // setTimeout(function() {
        //     var lat = marker1.getPosition().lat();
        //     var lng = marker1.getPosition().lng();

        //     if (markerIncrementing) {
        //         lat += 0.00005;
        //     } else {
        //         lat -= 0.00005;
        //     }


        //     if (lat > 32.991771) {
        //         markerIncrementing = false;
        //     } else if (lat < 32.990709) {
        //         markerIncrementing = true;
        //     }

        //     marker1.setPosition(new google.maps.LatLng(lat, lng));
        //     setTimeout(arguments.callee, 300);
        // }, 300);
    }
});
