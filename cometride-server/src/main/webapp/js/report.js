$(document).ready(function() {

    var API_ROUTE_ADMIN = "api/admin/route";
    var API_CAB = "api/cab";

    $.ajax({
        url: API_CAB,
        success: function(data) {
            $('#my-final-table').dynatable({
                dataset: {
                    records: data
                }
            });
        }
    });

    $.ajax({
        url: API_ROUTE_ADMIN,
        success: function(data) {
            $('#my-route').dynatable({
                features: {
                    paginate: false,
                    recordCount: false,
                    sorting: false
                },

                dataset: {
                    records: data
                }
            });
        }

    });
});
