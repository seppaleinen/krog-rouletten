
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

    x = document.getElementById("location");
    function showPosition(position) {
        x.innerHTML = "LAT:" + position.coords.latitude + "," + "LON:" + position.coords.longitude;
    }
    function getLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(showPosition);
        } else {
            x.innerHTML = "ERROR";
        }
    }
});