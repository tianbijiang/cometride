$(document).ready(function() {

    $("#fade").hide();
    $("#light").hide();
    $("#login").click(function() {
        $("#fade").show();
        $("#light").show();
    });
    $("#close-login").click(function() {
        $("#fade").hide();
        $("#light").hide();
    });

    $("#actual-login").click(function(){
    	var username = $("#inputUsername").val();
    	var password = $("#inputPassword").val();
    	var dataString = xxxx + "username" + xxxx + "password";

        $.ajax({
            type: "POST",
            url: "blahblah/api/route",
            data: dataString,
            //dataType: "json",
            cache: false,
            // beforeSend: function() {
            //     $("#login").val('Connecting...');
            // },
            success: function(data) {
                if (data) {
                    //alert(data);
                    var jsondata = $.parseJSON(data);
                    var aaaa = jsondata[0];
                    if (aaaa == "xxxx") {
                        //window.location.href = "main/home.php";
                    }
                } else {
                    //Shake animation effect.
                    //alert(data);
                    $("#error").html("Invalid credentials!");
                }
            }
        });
        return false;
    });
});
