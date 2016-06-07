
$(document).ready(function () {
    'use strict';
    $(".fancybox")
        .attr('rel', 'gallery')
        .fancybox({
            maxWidth: 800,
            maxHeight: 400,
            fitToView: false,
            width: '70%',
            height: '70%',
            autoSize: false,
            padding: 0,
            margin: [20, 60, 20, 60],
            afterClose: function () {
                location.reload();
                return;
            }
        });

    var closePopup, x;
    closePopup = $("#closePopup").text();
    if (closePopup !== "") {
        parent.jQuery.fancybox.close();
    }

    function showPosition(position) {
        var requestParameter = 'longitude=' + position.coords.longitude + ',latitude=' + position.coords.latitude + ',distance=8';
        if(navigator.userAgent.match(/Android/i)) {
            document.location= "/krog/random?" + requestParameter;
        }
        else {
            window.location.href = "/krog/random?" + requestParameter;
        }
        return false;
    }
    function getLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(showPosition);
        } else {
            x.innerHTML = "ERROR";
        }
    }
    $("#slumpaGPS").click(function () {
            getLocation();
        }
    );
});