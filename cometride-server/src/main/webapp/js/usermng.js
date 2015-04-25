$(document).ready(function() {

    var API_USER_ADMIN = "api/admin/users";

    var thead = $("#usertable thead");
    var tbody = $("#usertable tbody");
    var newRoles = $('#newRoles');
    var newUserName = $("#newUserName");
    var newPw = $("#newPw");
    var createNew = $("#createNewUser");

    var selectedRoles = [];
    var currentDeletingUserName;

    getUser();

    newRoles.multiselect({
        numberDisplayed: 1,
        onChange: function(option, checked, select) {
            selectedRoles = [];
            $('#newRoles :selected').each(function(i, selected) {
                selectedRoles.push($(selected).val());
            });
        },
        buttonClass: '',
        templates: {
            button: '<span class="multiselect dropdown-toggle" data-toggle="dropdown">Please select <b class="caret"></b></span>'
        }
    });

    createNew.click(function() {
        var dataString_user = {
            userName: newUserName.val(),
            userPassword: newPw.val(),
            userRoles: selectedRoles
        };
        dataString_user = JSON.stringify(dataString_user);

        if (formValidation_user()) {
            $.ajax({
                contentType: 'application/json',
                type: "POST",
                url: API_USER_ADMIN,
                data: dataString_user,
                dataType: "text",
                cache: false,
                success: function(data) {
                    //TODO
//                    alert("CREATED");
                    //window.location.reload(true);
                    clearUser();
                    getUser();
                }
            });
        }
    });

    //TODO
    function formValidation_user() {
        return true;
    }

    function getUser() {
        $.getJSON(API_USER_ADMIN, function(data) {
            if (data.length > 0) {
                for (var i = 0; i < data.length; i++) {
                    var content = "<tr id='" + data[i].userName + "'><td>" + data[i].userName + "</td><td>";
                    for (var k = 0; k < data[i].userRoles.length; k++) {
                        if (data[i].userRoles.length <= 1 || k == data[i].userRoles.length - 1) {
                            content += data[i].userRoles[k]
                        } else {
                            content += data[i].userRoles[k] + ", ";
                        }
                    }
                    content += "</td><td class='text-center'><a href='#' id='del-user-btn' class='btn btn-danger btn-xs'><span class='glyphicon glyphicon-remove'></span> Delete</a></td></tr>";
                    tbody.append(content);
                }
                enableAfterGet();
            }
        });
    }

    function clearUser() {
        var rows = $("#usertable tbody tr");
        rows.remove();
    }

    function enableAfterGet() {
        var deleteBtn = $("#usertable tbody tr td #del-user-btn");
        deleteBtn.click(function() {
            currentDeletingUserName = $(this).parent().parent().attr('id');
            deleteUser();
        });
    }

    function deleteUser() {
        $.ajax({
            type: "DELETE",
            url: API_USER_ADMIN + "/" + currentDeletingUserName,
            success: function(data) {
                //TODO
//                alert("DELETED");
                //window.location.reload(true);
                clearUser();
                getUser();
            }
        });
    }


});
