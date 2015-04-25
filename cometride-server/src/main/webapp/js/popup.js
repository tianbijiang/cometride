$(document).ready(function() {
    $("#usermng-btn").click(function() {
        $('#usermng-popup')[0].style.display = 'block';
    });

    $('#usermng-popup-close-btn').click(function() {
        $('#usermng-popup')[0].style.display = 'none';
    });

    $("#cabmng-btn").click(function() {
        $('#cabmng-popup')[0].style.display = 'block';
    });

    $('#cabmng-popup-close-btn').click(function() {
        $('#cabmng-popup')[0].style.display = 'none';
    });
});
