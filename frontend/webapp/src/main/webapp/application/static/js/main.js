
$(document).ready(function () {
    'use strict';

    function showPosition(position) {
        $("#latitude").val(position.coords.latitude);
        $("#longitude").val(position.coords.longitude);
        $("#hidden_submit").click()
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