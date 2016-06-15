
$(document).ready(function () {
    'use strict';

    function showPosition(position) {
        $("#latitude").val(position.coords.latitude);
        $("#longitude").val(position.coords.longitude);
        $("#hidden_submit").click()
    }
    function onError(error){
        document.location= "/error";
    }
    var options = {
      enableHighAccuracy: true,
      timeout: 5000,
      maximumAge: 0
    };

    function getLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(showPosition, onError, options);
        } else {
            alert("VAFAN?");
        }
    }

    $("#slumpaGPS").click(function () {
            getLocation();
        }
    );
});