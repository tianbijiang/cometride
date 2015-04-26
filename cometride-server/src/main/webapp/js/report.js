$(document).ready(function() {

    var API_ROUTE_GET = "api/route";
    var METRICS_MONTH_DAILY_RIDERS = "api/admin/metrics/monthlyriders";

//    var selectRouteList = $("#route-list");
//
//    $.getJSON(API_ROUTE_GET, function(data) {
//        console.log( "CCCCCC" );
//        if (data.length == 0) {
//            selectRouteList.append($("<option></option>")
//                .text("No Route Available"));
//        } else {
//            for (var i = 0; i < data.length; i++) {
//                var route = data[i];
//                var name = route.name;
//                var id = route.id;
//                selectRouteList.append($("<option></option>")
//                    .attr("value", id)
//                    .text(name));
//            }
//
//            $.ajax({
//                url: API_METRICS_RIDER_HOUR,
//                success: function(data) {
//                    $('#all-hourly-riders-table').dynatable({
//                        dataset: {
//                            records: data
//                        }
//                    });
//                }
//            });
//        }
//
//        $('#route-list').multiselect({
//            numberDisplayed: 1,
//            onChange: function(option, checked, select) {
//                var selectedOptions = [];
//                $('#selectRouteList :selected').each(function(i, selected) {
//                    selectedOptions.push($(selected).val());
//                })
//
//                $.ajax({
//                        url: METRICS_MONTH_DAILY_RIDERS + '/' + selectedOptions[0] ,
//                        success: function(data) {
//                            $('#route-hourly-riders-table').dynatable({
//                                features: {
//                                    paginate: false,
//                                    recordCount: false,
//                                    sorting: false,
//                                    search: false
//                                },
//
//                                dataset: {
//                                    records: data
//                                }
//                            });
//                        }
//                    });
//            },
//            buttonClass: ''
//        });
//    });

    $.ajax({
        url: METRICS_MONTH_DAILY_RIDERS,
        success: function(data) {
            $('#all-hourly-riders-table').dynatable({
                features: {
                    paginate: false,
                    recordCount: false,
                    sorting: false,
                    search: false
                },

                dataset: {
                    records: data
                }
            });
        }
    });

});
