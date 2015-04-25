$(document).ready(function() {

    var API_CABTYPE_ADMIN = "api/admin/cabtypes";
    var API_CABTYPE = "api/cabtypes";

    var thead = $("#cabtable thead");
    var tbody = $("#cabtable tbody");
    var newTypeId = $('#newTypeId');
    var newTypeName = $('#newTypeName');
    var newCapacity = $("#newCapacity");
    var createNewCab = $("#createNewCab");

    var currentDeletingCabId;

    getCabType();

    createNewCab.click(function() {
        var dataString_cabtype = {
            typeId: newTypeId.val(),
            typeName: newTypeName.val(),
            maximumCapacity: newCapacity.val()
        };
        dataString_cabtype = JSON.stringify(dataString_cabtype);

        if (formValidation_cabtype()) {
            $.ajax({
                contentType: 'application/json',
                type: "POST",
                url: API_CABTYPE_ADMIN,
                data: dataString_cabtype,
                dataType: "text",
                cache: false,
                success: function(data) {
                    //TODO
                    alert("CREATED");
                    //window.location.reload(true);
                    clearCabType();
                    getCabType();
                }
            });
        }
    });

    //TODO
    function formValidation_cabtype() {
        return true;
    }

    function getCabType() {
        $.getJSON(API_CABTYPE, function(data) {
            if (data.length > 0) {
                for (var i = 0; i < data.length; i++) {
                    var content = "<tr id='" + data[i].typeId + "'><td>" + data[i].typeId + "</td>";
                    content += "<td>" + data[i].typeName + "</td>";
                    content += "<td>" + data[i].maximumCapacity + "</td>";
                    content += "<td class='text-center'><a href='#' id='del-cabtype-btn' class='btn btn-danger btn-xs'><span class='glyphicon glyphicon-remove'></span> Delete</a></td></tr>";
                    tbody.append(content);
                }
                enableAfterGetCabType();
            }
        });
    }

    function clearCabType() {
        var rows = $("#cabtable tbody tr");
        rows.remove();
    }

    function enableAfterGetCabType() {
        var deleteBtn = $("#cabtable tbody tr td #del-cabtype-btn");
        deleteBtn.click(function() {
            currentDeletingCabId = $(this).parent().parent().attr('id');
            deleteCabType();
        });
    }

    function deleteCabType() {
        $.ajax({
            type: "DELETE",
            url: API_CABTYPE_ADMIN + "/" + currentDeletingCabId,
            success: function(data) {
                //TODO
                alert("DELETED");
                //window.location.reload(true);
                clearCabType();
                getCabType();
            }
        });
    }


});
