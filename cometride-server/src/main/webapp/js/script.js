$(document).ready(function() {
    $("#btn-save").hide();
    $("#btn-reverse").hide();

    $("#login").click(function() {
        alert("login clicked");
        // $("#fade").style.display = "block";
        // $("#light").css({
        //     "display": "block"
        // });
    });

    $("#btn-draw").click(function() {
        $("#btn-save").show();
        $("#btn-reverse").show();
        $("#btn-draw").hide();
    });

    $(".create #hide-sb").click(function() {
        $("#btn-draw").show();
        $("#btn-save").hide();
        $("#btn-reverse").hide();
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
