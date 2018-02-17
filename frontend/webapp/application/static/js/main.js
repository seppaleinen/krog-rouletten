$(document).ready(function() {
    'use strict';

    // get Current location
    function showPosition(position) {
        $("#latitude").val(position.coords.latitude);
        $("#longitude").val(position.coords.longitude);
        $("#hidden_submit").click();
    }
    function onError(error) {
        console.log(error.message);
        if(error.code == error.PERMISSION_DENIED) {
            document.location = "/error/" + encodeURIComponent('Tillstånd att kolla din GPS behövs för den funktionen');
        } else {
            document.location = "/error/" + encodeURIComponent('Det gick inte att hämta dina koordinater');
        }
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
            console.log("VAFAN?");
        }
    }

    $("[name='slumpaGPS']").click(function() {
        $("#searchtype").val('gps');
        getLocation();
    });

    $("#findLista").click(function() {
        $("#searchtype").val('list');
        getLocation();
    });

    $("#distance").change(function() {
        $("#hidden_distance").val($(this).val());
    });
});