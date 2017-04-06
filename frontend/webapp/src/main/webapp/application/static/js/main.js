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

    // Googlemaps datalist stuff
    var last_adress;

    function generic_datalist_function(val, datalist_id) {
        if (val === "") return;
        if (val.length < 10) return;
        if (val === last_adress) return;
        if (val.indexOf(' ') < 0) return;
        var url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodeURIComponent(val) + "&sensor=false";
        $.get(url, function(res) {
            var dataList = $(datalist_id);
            dataList.empty();
            var len = res.results.length
            if (res.results.length) {
                for (var i = 0, len = res.results.length; i < len; i++) {
                    var adress = res.results[i].formatted_address;
                    last_adress = adress;
                    var opt = $("<option></option>").attr("value", adress);
                    dataList.append(opt);
                }
            }
        }, "json");
    };
});