$(document).ready(function() {

    $("#login").click(function() {
        alert("login clicked");
        $("#fade").style.display = "block";
        $("#light").css({
            "display": "block"
        });
    });

    // $("select").selectBoxIt({
    //     // Hides the first select box option from appearing when the drop down is opened
    //     showFirstOption: true

    // });

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
        navbarClass="navbar-pushup",
        pushClass = "push-push", //css class to add pushy capability
        createBtn = $('.create-btn, .create .hide-sb'), //css classes to toggle the menu
        editBtn =  $('.edit-btn, .edit .hide-sb'),
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

    // function openPushyFallback() {
    //     body.addClass(pushyActiveClass);
    //     pushy.animate({
    //         left: "0px"
    //     }, menuSpeed);
    //     container.animate({
    //         left: menuWidth
    //     }, menuSpeed);
    //     push.animate({
    //         left: menuWidth
    //     }, menuSpeed); //css class to add pushy capability
    // }

    // function closePushyFallback() {
    //     body.removeClass(pushyActiveClass);
    //     pushy.animate({
    //         left: "-" + menuWidth
    //     }, menuSpeed);
    //     container.animate({
    //         left: "0px"
    //     }, menuSpeed);
    //     push.animate({
    //         left: "0px"
    //     }, menuSpeed); //css class to add pushy capability
    // }

    //checks if 3d transforms are supported removing the modernizr dependency
    // cssTransforms3d = (function csstransforms3d() {
    //     var el = document.createElement('p'),
    //         supported = false,
    //         transforms = {
    //             'webkitTransform': '-webkit-transform',
    //             'OTransform': '-o-transform',
    //             'msTransform': '-ms-transform',
    //             'MozTransform': '-moz-transform',
    //             'transform': 'transform'
    //         };

    //     // Add it to the body to get the computed style
    //     document.body.insertBefore(el, null);

    //     for (var t in transforms) {
    //         if (el.style[t] !== undefined) {
    //             el.style[t] = 'translate3d(1px,1px,1px)';
    //             supported = window.getComputedStyle(el).getPropertyValue(transforms[t]);
    //         }
    //     }

    //     document.body.removeChild(el);

    //     return (supported !== undefined && supported.length > 0 && supported !== "none");
    // })();

    // if (cssTransforms3d) {
        //toggle menu
        createBtn.click(function() {
            toggleCreatePushy();
        });
        editBtn.click(function() {
            toggleEditPushy();
        });
        //close menu when clicking site overlay
        // siteOverlay.click(function() {
        //     toggleCreatePushy();
        // });
    // } else {
    //     //jQuery fallback
    //     pushy.css({
    //         left: "-" + menuWidth
    //     }); //hide menu by default
    //     container.css({
    //         "overflow-x": "hidden"
    //     }); //fixes IE scrollbar issue

    //     //keep track of menu state (open/close)
    //     var state = true;

    //     //toggle menu
    //     menuBtn.click(function() {
    //         if (state) {
    //             openPushyFallback();
    //             state = false;
    //         } else {
    //             closePushyFallback();
    //             state = true;
    //         }
    //     });

    //     //close menu when clicking site overlay
    //     siteOverlay.click(function() {
    //         if (state) {
    //             openPushyFallback();
    //             state = false;
    //         } else {
    //             closePushyFallback();
    //             state = true;
    //         }
    //     });
    // }
});
