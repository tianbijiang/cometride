$(document).ready(function() {

    var API_ROUTE_GET = "api/route";
    var API_METRICS_RIDER_HOUR = "api/admin/route";

    console.log( "AAAAA" );
    var selectRouteList = $("#route-list");

    console.log( "BBBBB" );
    $.getJSON(API_ROUTE_GET, function(data) {
        console.log( "CCCCCC" );
        if (data.length == 0) {
            selectRouteList.append($("<option></option>")
                .text("No Route Available"));
        } else {
            for (var i = 0; i < data.length; i++) {
                var route = data[i];
                var name = route.name;
                var id = route.id;
                selectRouteList.append($("<option></option>")
                    .attr("value", id)
                    .text(name));



                $.ajax({
                    url: API_METRICS_RIDER_HOUR,
                    success: function(data) {
                        $('#hourly-riders-table').dynatable({
                            dataset: {
                                records: data
                            }
                        });
                    }
                });
            }
        }

        $('#route-list').multiselect({
            includeSelectAllOption: true,
            numberDisplayed: 1,
            onChange: function(option, checked, select) {
                // DO SOMETHING YO
            },
            buttonClass: ''
        });
    });

//    var API_ROUTE_ADMIN = "api/admin/route";
//    var API_CAB = "api/cab";
//
//    $.ajax({
//        url: API_CAB,
//        success: function(data) {
//            $('#my-final-table').dynatable({
//                dataset: {
//                    records: data
//                }
//            });
//        }
//    });
//
//    $.ajax({
//        url: API_ROUTE_ADMIN,
//        success: function(data) {
//            $('#my-route').dynatable({
//                features: {
//                    paginate: false,
//                    recordCount: false,
//                    sorting: false
//                },
//
//                dataset: {
//                    records: data
//                }
//            });
//        }
//
//    });
});
