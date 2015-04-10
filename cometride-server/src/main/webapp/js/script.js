$(document).ready(function() {
    var timepickers = 1;
    var timepickers_edit = 1;
    
    $(".create #plus-timepicker").click(function() {
        timepickers++;
        $(".create #time-picker").append("<div class='timepicker-set' id='timepicker-set-" + timepickers + "'><input class='form-control timepicker start-time' placeholder='Start Time' /><p> to </p><input class='form-control timepicker end-time' placeholder='End Time' /></div>");
        $(".create .timepicker").timepicker({
            defaultTime: false,
            showMeridian: false
        });
    });
    $(".create #minus-timepicker").click(function() {
        $(".create #time-picker div:last").remove();
    });

    $(".create .timepicker").timepicker({
        defaultTime: false,
        showMeridian: false
    });

    $('.create #date-picker').hide();

    $(".create #temporary-flag").click(function() {
        $('.create #date-picker').toggle();
    });

    if ($(".create #temporary-flag").is(":checked")) {
        alert("1");
        $('.create #date-picker').show();
    }

    $('.create #date-picker input').datepicker({
        keyboardNavigation: false,
        autoclose: true,
        todayHighlight: true
    });

    $(".create #btn-actual-draw").hide();
    $(".create #btn-reverse").hide();
    $(".create #btn-save").hide();

    $(".create #btn-draw").click(function() {
        $(".create #btn-actual-draw").show();
        $(".create #btn-reverse").show();
        $(".create #btn-save").show();
        $(".create #btn-draw").hide();
    });

    $(".create #hide-sb").click(function() {
        $(".create #btn-draw").show();
        $(".create #btn-actual-draw").hide();
        $(".create #btn-reverse").hide();
        $(".create #btn-save").hide();
    });

    $(".edit #plus-timepicker").click(function() {
        timepickers_edit++;
        $(".edit #time-picker").append("<div class='timepicker-set' id='timepicker-set-edit-" + timepickers_edit + "'><input class='form-control timepicker start-time' placeholder='Start Time' /><p> to </p><input class='form-control timepicker end-time' placeholder='End Time' /></div>");
        $(".edit .timepicker").timepicker({
            defaultTime: false
        });
    });
    $(".edit #minus-timepicker").click(function() {
        $(".edit #time-picker div:last").remove();
    });

    $(".edit .timepicker").timepicker({
        defaultTime: false
    });

    $('.edit #date-picker').hide();

    $(".edit #temporary-flag").click(function() {
        $('.edit #date-picker').toggle();
    });

    if ($(".edit #temporary-flag").is(":checked")) {
        alert("1");
        $('.edit #date-picker').show();
    }

    $('.edit #date-picker input').datepicker({
        keyboardNavigation: false,
        autoclose: true,
        todayHighlight: true
    });

    $(".edit #btn-actual-draw").hide();
    $(".edit #btn-reverse").hide();
    $(".edit #btn-save").hide();

    $(".edit #btn-draw").click(function() {
        $(".edit #btn-save").show();
        $(".edit #btn-reverse").show();
        $(".edit #btn-actual-draw").show();
        $(".edit #btn-draw").hide();
    });

    $(".edit #hide-sb").click(function() {
        $(".edit #btn-draw").show();
        $(".edit #btn-save").hide();
        $(".edit #btn-reverse").hide();
        $(".edit #btn-actual-draw").hide();
    });


    var createPushy = $('.create'), //menu css class
        editPushy = $('.edit'),
        body = $('body'),
        container = $('#container'), //container css class
        navbar = $('.navbar'),
        push = $('.push'), //css class to add pushy capability
        //siteOverlay = $('.site-overlay'), //site overlay
        pushyClass = "pushy-left pushy-open", //menu position & menu open class
        pushyActiveClass = "pushy-active", //css class to toggle site overlay
        containerClass = "container-push", //container open class
        navbarClass = "navbar-pushup",
        pushClass = "push-push", //css class to add pushy capability
        createBtn = $('.create-btn, .create #hide-sb'), //css classes to toggle the menu
        editBtn = $('.edit-btn, .edit #hide-sb'),
        menuSpeed = 200, //jQuery fallback menu speed
        menuWidth = createPushy.width() + "px"; //jQuery fallback menu width

    function toggleCreatePushy() {
        body.toggleClass(pushyActiveClass); //toggle site overlay
        createPushy.toggleClass(pushyClass);
        container.toggleClass(containerClass);
        navbar.toggleClass(navbarClass);
        push.toggleClass(pushClass); //css class to add pushy capability
    }

    function toggleEditPushy() {
        body.toggleClass(pushyActiveClass); //toggle site overlay
        editPushy.toggleClass(pushyClass);
        container.toggleClass(containerClass);
        navbar.toggleClass(navbarClass);
        push.toggleClass(pushClass); //css class to add pushy capability
    }

    createBtn.click(function() {
        toggleCreatePushy();
    });
    editBtn.click(function() {
        toggleEditPushy();
    });

});
