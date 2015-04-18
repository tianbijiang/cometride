//todo
//increase point limit
//login.html prettify
//report.html prettify
//draw safepoints
//create cabtypes
//manage users
$(document).ready(function() {

    /* Some Constants */
    var API_ROUTE_GET = "api/route";
    var API_ROUTE_POST = "api/admin/route";
    var API_CAB = "api/cab";
    var GREEN_CAB_IMG = "img/cab_green.png";
    var ORANGE_CAB_IMG = "img/cab_yellow.png";
    var RED_CAB_IMG = "img/cab_red.png";
    var RETRY_DELAY = 500;

    /* Some Element Names */
    var selectRouteList = $("#navHeaderCollapse #navs #selectRoute #selectRouteList");
    var editRouteList = $("#navHeaderCollapse #navs #editRoute #editRouteList");
    var routeListPlaceholder = $(".noroute");
    var colorBtns = $('.create #color, .edit #color');
    var colorBtnCreate = $('.create #color');
    var colorBtnEdit = $('.edit #color');
    var createBtn = $('.create-btn');
    var startDrawingBtns = $(".create #btn-draw, .edit #btn-draw");
    var reverseBtns = $(".create #btn-reverse, .edit #btn-reverse");
    var finishBtnCreate = $(".create #btn-save");
    var finishBtnEdit = $(".edit #btn-save");
    var closeBtns = $(".create #hide-sb, .edit #hide-sb");

    var roundTripBoxCreate = $(".create #roundTrip");
    var tempFlagCreate = $(".create #temporary-flag");
    var nameFieldCreate = $(".create #name");
    var statusOptionCreate = $(".create #status");
    var activeDaysOptionCreate = $(".create #day-picker button");
    var startDateFieldCreate = $(".create #start-date");
    var endDateFieldCreate = $(".create #end-date");

    var roundTripBoxEdit = $(".edit #roundTrip");
    var tempFlagEdit = $(".edit #temporary-flag");
    var nameFieldEdit = $(".edit #name");
    var statusOptionEdit = $(".edit #status");
    var statusOptionEditOption = $(".edit #status option");
    var activeDaysOptionEdit = $(".edit #day-picker button");
    var startDateFieldEdit = $(".edit #start-date");
    var endDateFieldEdit = $(".edit #end-date");

    /* Some Variables for Drawing */
    var map;
    var center = new google.maps.LatLng(32.9860365, -96.7518621);
    var points = [];
    var markers = [];
    var locationsAdded = 1;
    var directionsDisplay;
    var routeColor = "rgb(224, 102, 102)";
    var polylineOptions = {};
    var refreshTrigger = false;

    /* Some Variables for Displaying */
    var datalength = 0;
    var numberOfCabs = 0;
    var dirsDisplay = [];
    var dirsService = [];
    var dirsRequest = [];
    var colors = [];
    var ids = [];
    var cab_ids = [];
    var selectedRoutesTemp = [];
    var routeObjects = [];

    google.maps.event.addDomListener(window, 'load', initialize);

    colorBtns.spectrum({
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

    colorBtnCreate.change(function() {
        routeColor = colorBtnCreate.val();
    });

    colorBtnEdit.change(function() {
        routeColor = colorBtnEdit.val();
    });

    createBtn.click(function() {
        hideRoute();
    });

    startDrawingBtns.click(function() {
        map.setOptions({
            draggableCursor: "crosshair"
        });
        google.maps.event.addListener(map, "click", function(location) {
            drawRoute(location.latLng, "Location " + locationsAdded);
            locationsAdded++;
        });
    });

    reverseBtns.click(function() {
        removeRow(points.length - 1);
    });

    finishBtnCreate.click(function() {
        sendCreatedRoute();
        refreshTrigger = true;
    });
    finishBtnEdit.click(function() {
        sendEditedRoute();
        refreshTrigger = true;
    });

    closeBtns.click(function() {
        map.setOptions({
            draggableCursor: "default"
        });
        google.maps.event.clearListeners(map, 'click');

        clearPolyLine();

        if (refreshTrigger == true) {
            window.location.reload(true);
        } else {
            if (selectedRoutesTemp.length == 0) {
                getRoute();
            } else {
                showSelectedRoutes(selectedRoutesTemp);
            }
        }
    });

    function initialize() {
        var mapOptions = {
            zoom: 16,
            center: center
        };
        map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);

        directionsDisplay = new google.maps.DirectionsRenderer();

        getRoute();
        getCab();
        getRouteList();
    }

    function getRouteList() {
        $.getJSON(API_ROUTE_GET, function(data) {
            if (data.length == 0) {
                selectRouteList.append($("<option></option>")
                    .text("No Route Available"));
                routeListPlaceholder.show();
            } else {
                routeListPlaceholder.hide();
                for (var i = 0; i < data.length; i++) {
                    var route = data[i];
                    var name = route.name;
                    var id = route.id;
                    selectRouteList.append($("<option></option>")
                        .attr("value", id)
                        .text(name));
                    editRouteList.append("<li class='edit-btn' id='" + id + "'><a href='#'>" + name + "</a></li>");
                }
            }
            displayRouteList();
            enableEditAfterLoading();
        });
        return false;
    }

    function displayRouteList() {
        $('#selectRouteList').multiselect({
            includeSelectAllOption: true,
            numberDisplayed: 1,
            onChange: function(option, checked, select) {
                var selectedRoutes = [];
                $('#selectRouteList :selected').each(function(i, selected) {
                    selectedRoutes.push($(selected).val());
                });
                selectedRoutesTemp = selectedRoutes;
                showSelectedRoutes(selectedRoutes);
            },
            buttonClass: '',
            templates: {
                button: '<span class="multiselect dropdown-toggle" data-toggle="dropdown"><i class="fa fa-taxi"></i> Select a Route <b class="caret"></b></span>'
            }
        });
    }

    function drawRoute(latlng, locationName) {
        if (latlng != null) {
            var point = {
                LatLng: latlng,
                LocationName: locationName
            };
            points.push(point);
            buildPoints();
        }
    }

    function buildPoints() {
        for (var i = 0; i < points.length; i++) {
            getDirections();
        }
    }

    function clearPolyLine() {
        points = [];
        buildPoints();
        clearDrawingRoute();
    }

    function clearDrawingRoute() {
        directionsDisplay.setMap(null);
    }

    function removeRow(index) {
        points.splice(index, 1);
        buildPoints();
        //clearDrawingRoute();
    }

    function moveRowDown(index) {
        var item = points[index];
        points.splice(index, 1);
        points.splice(index + 1, 0, item);
        buildPoints();
        clearDrawingRoute();
    }

    function moveRowUp(index) {
        var item = points[index];
        points.splice(index, 1);
        points.splice(index - 1, 0, item);
        buildPoints();
        clearDrawingRoute();
    }

    function getDirections() {
        var directions = new google.maps.DirectionsService();
        directionsDisplay.setMap(map);

        var waypts = [];
        var end = points.length - 1;
        var dest = points[end].LatLng;
        if (roundTripBoxCreate.is(":checked")) {
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
                var options = {
                    directions: result,
                    map: map,
                    polylineOptions: {
                        strokeColor: routeColor
                    },
                    //suppressMarkers: true,
                    preserveViewport: true
                };

                directionsDisplay.setOptions(options);
            } else {
                //alert("Something wrong with Google API. Cannot draw routes now.")
            }
        });
    }

    function sendCreatedRoute() {
        var dataString = collectCreateFormData();
        sendRoute(dataString);
    }

    function sendEditedRoute() {
        var dataString = collectEditFormData();
        sendRoute(dataString);
    }

    function sendRoute(dataString) {
        if (formValidation()) {
            $.ajax({
                contentType: 'application/json',
                type: "POST",
                url: API_ROUTE_POST,
                data: dataString,
                dataType: "text",
                cache: false,
                success: function(data) {
                    //TODO
                    alert(dataString);
                    if (data) {
                        //alert(data);
                    } else {
                        alert("Server error. Please try saving again.");
                    }
                },
                //TODO
                error: function(xhr, textStatus, error) {
                    console.log(xhr.statusText);
                    console.log(textStatus);
                    console.log(error);
                }
            });
        }
        return false;
    }

    function collectCreateFormData() {
        var name = nameFieldCreate.val();

        var color = "rgb(224, 102, 102)";
        if (colorBtnCreate.val() != "") {
            color = colorBtnCreate.val();
        } //rgb(255,0,0)
        color = colorToHex(color);

        var status = statusOptionCreate.val();

        var waypoints = [];
        for (var i = 0; i < points.length; i++) {
            waypoints.push({
                lat: points[i].LatLng.lat(),
                lng: points[i].LatLng.lng()
            });
        }
        if (roundTripBoxCreate.is(":checked")) {
            waypoints[points.length] = waypoints[0];
        }

        var d = activeDaysOptionCreate.html();
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
        if (!tempFlagCreate.is(":checked")) {
            startDate = "";
            endDate = "";
        } else {
            startDate = startDateFieldCreate.val();
            endDate = endDateFieldCreate.val();
        }

        var dataString = {
            color: color,
            name: name,
            status: status,
            waypoints: waypoints,
            days: days,
            times: times,
            startDate: startDate,
            endDate: endDate
        };
        dataString = JSON.stringify(dataString);

        return dataString;
    }

    function collectEditFormData() {
        var name = nameFieldEdit.val();

        var color = "rgb(224, 102, 102)";
        if (colorBtnEdit.val() != "") {
            color = colorBtnEdit.val();
        } //rgb(255,0,0)
        color = colorToHex(color);

        var status = statusOptionEdit.val();

        var waypoints = [];
        for (var i = 0; i < points.length; i++) {
            waypoints.push({
                lat: points[i].LatLng.lat(),
                lng: points[i].LatLng.lng()
            });
        }
        if (roundTripBoxEdit.is(":checked")) {
            waypoints[points.length] = waypoints[0];
        }

        var d = activeDaysOptionEdit.html();
        var days = d.split(", ");

        var times = [];
        for (var i = 0; i < $(".edit #time-picker-edit > div").length; i++) {
            var start = $(".edit #timepicker-set-" + (i + 1) + " .start-time").val();
            var end = $(".edit #timepicker-set-" + (i + 1) + " .end-time").val();
            times.push({
                start: start,
                end: end
            });
        }

        var startDate;
        var endDate;
        if (!tempFlagEdit.is(":checked")) {
            startDate = "";
            endDate = "";
        } else {
            startDate = startDateFieldEdit.val();
            endDate = endDateFieldEdit.val();
        }

        var dataString = {
            color: color,
            name: name,
            status: status,
            waypoints: waypoints,
            days: days,
            times: times,
            startDate: startDate,
            endDate: endDate
        };
        dataString = JSON.stringify(dataString);

        return dataString;
    }

    //TODO
    function formValidation() {
        return true;
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
        return '#' + (0x1000000 | rgb).toString(16).substring(1);
    }

    function getRoute() {
        hideRoute();
        $.getJSON(API_ROUTE_GET, function(data) {
            routeObjects = data;
            datalength = data.length;
            for (var i = 0; i < datalength; i++) {
                var route = data[i];

                var id = route.id;
                ids.push(id);

                var name = route.name;

                var color = route.color;
                colors.push(color);

                var wps = [];
                var startPoint;
                var endPoint;
                for (var k = 0; k < route.waypoints.length; k++) {
                    var newlatlng = new google.maps.LatLng(route.waypoints[k].lat, route.waypoints[k].lng);
                    wps.push({
                        location: newlatlng
                    });
                    if (k == 0) {
                        startPoint = newlatlng;
                    }
                    if (k == route.waypoints.length - 1) {
                        endPoint = newlatlng;
                    }
                }

                dirsRequest.push({
                    origin: startPoint,
                    destination: endPoint,
                    waypoints: wps,
                    travelMode: google.maps.TravelMode['DRIVING']
                });

                displayRoute(i);
            }
        });
        return false;
    }

    function getCab() {
        $.getJSON(API_CAB, function(data) {

            numberOfCabs = data.length;
            console.log("numberOfCabs" + numberOfCabs);
            for (var m = 0; m < numberOfCabs; m++) {
                var cab = data[m];
                var cab_id = cab.cabId;

                var route_id = cab.routeId;
                cab_ids.push(route_id);

                var passengerCount = cab.passengerCount;
                var capacity = cab.maxCapacity;
                var cab_img = checkCapacity(passengerCount, capacity);

                var cabStatus = cab.status;

                var lat = cab.location.lat;
                var lng = cab.location.lng;

                markers[m] = new google.maps.Marker({
                    position: new google.maps.LatLng(lat, lng),
                    map: map,
                    title: 'Cab ' + (m + 1),
                    icon: cab_img
                });

                displayCab(m);
            }
            window.setInterval(function() {
                updateCab();
            }, 3000);
        });
        return false;
    }

    function checkCapacity(passengerCount, capacity) {
        var cab_img;
        if (passengerCount / capacity < 0.6) {
            cab_img = GREEN_CAB_IMG;
        } else if (passengerCount / capacity >= 1) {
            cab_img = RED_CAB_IMG;
        } else {
            cab_img = ORANGE_CAB_IMG;
        }
        return cab_img;
    }

    function updateCab() {
        $.getJSON(API_CAB, function(data) {
            for (var m = 0; m < numberOfCabs; m++) {
                var cab = data[m];

                var passengerCount = cab.passengerCount;
                var capacity = cab.maxCapacity;
                var cab_img = checkCapacity(passengerCount, capacity);

                var cabStatus = cab.status;

                var lat = cab.location.lat;
                var lng = cab.location.lng;

                markers[m].setPosition(new google.maps.LatLng(lat, lng));
                markers[m].setIcon(cab_img);
            }
        });
    }

    function showSelectedRoutes(selectedId) {
        hideRoute();
        for (var k = 0; k < selectedId.length; k++) {
            //console.log("k"+k+" selectedId[k]"+selectedId[k]);
            for (var i = 0; i < datalength; i++) {
                if (ids[i] == selectedId[k]) {
                    //console.log("i"+i+" ids[i]"+ids[i]);
                    displayRoute(i);
                }
            }
        }
    }

    function displayRoute(i) {
        dirsService[i] = new google.maps.DirectionsService();
        dirsService[i].route(dirsRequest[i], function(response, status) {

            if (status == google.maps.DirectionsStatus.OK) {
                dirsDisplay[i] = new google.maps.DirectionsRenderer();
                dirsDisplay[i].setOptions({
                    polylineOptions: {
                        strokeColor: colors[i]
                    },
                    suppressMarkers: true,
                    preserveViewport: true
                });
                dirsDisplay[i].setMap(map);
                dirsDisplay[i].setDirections(response);

                displayCab(i);

            } else {
                if (status == "OVER_QUERY_LIMIT") {
                    setTimeout(function() {
                        displayRoute(i);
                        console.log("delayed" + i);
                    }, RETRY_DELAY);
                }
            }
        });
    }

    function displayCab(m) {
        if (m < numberOfCabs) {
            //console.log("cab" + m);
            markers[m].setMap(map);
        }
    }

    function hideRoute() {
        for (var i = 0; i < datalength; i++) {
            dirsDisplay[i].setMap(null);
        }
        for (var m = 0; m < numberOfCabs; m++) {
            //console.log(markers[m]);
            markers[m].setMap(null);
        }
    }

    function getDistance(distance) {
        if ($("#directionUnits").val() === "Miles") {
            return Math.round((distance * 0.621371192) / 100) / 10 + " miles";
        } else {
            return Math.round(distance / 100) / 10 + " km";
        }
    }

    function enableEditAfterLoading() {
        enableEditSidebar();
        enableEditRoute();
    }

    function enableEditRoute() {
            //TODO
            $('.edit-btn').click(function() {
                hideRoute();
                var id = $(this).attr("id");
                for (var i = 0; i < ids.length; i++) {
                    if (ids[i] == id) {
                        //displayRoute(i);
                        loadEditInfo(i);
                        //TODO
                        //deleteOldRoute(i);
                    }
                }
            });
        }
    //TODO
    function loadEditInfo(i) {
        var oldName = routeObjects[i].name;
        var oldColor = routeObjects[i].color;
        var oldStatus = routeObjects[i].status;

        nameFieldEdit.val(oldName);
        colorBtnEdit.spectrum("set", oldColor);
        // statusOptionEditOption.each(function() {
        //     alert(this.text);
        //     this.selected = (this.val() == oldStatus);
        // });

        //if(routeObjects[i].waypoints[points.length] == routeObjects[i].waypoints[0]) {
        //}
        // var daysString;
        // for(var k = 0; k<routeObjects[i].days.length; k++) {
        //     daysString+=routeObjects[i].days[k];
        // }
        // activeDaysOptionEdit.html(daysString);
    }
});
