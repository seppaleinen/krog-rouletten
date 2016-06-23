$(document).ready(function() {
    'use strict';

    // get Current location
    function showPosition(position) {
        $("#latitude").val(position.coords.latitude);
        $("#longitude").val(position.coords.longitude);
        $("#hidden_submit").click()
    }
    function onError(error) {
        document.location = "/error";
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

    $("#slumpaGPS").click(function() {
        getLocation();
    });

    // Googlemaps datalist stuff
    var last_adress;

    function generic_datalist_function(val, datalist_id) {
        if (val === "") return;
        if (val.length < 10) return;
        if (val === last_adress) return;
        if (val.indexOf(' ') < 0) return;
        var url = "http://maps.googleapis.com/maps/api/geocode/json?address=" + encodeURIComponent(val) + "&sensor=false";
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

    if (document.createElement("datalist").options) {
        $('#sok_adress').on('input', function() {
            var val = $(this).val();
            generic_datalist_function(val, "#search_adress_list")
        });
        $('#user_krog_adress').on('input', function() {
            var val = $(this).val();
            generic_datalist_function(val, "#user_krog_adress_list")
        });
        $('#admin_ny_krog_adress').on('input', function() {
            var val = $(this).val();
            generic_datalist_function(val, "#admin_ny_krog_adress_list")
        });
        $('#admin_update_krog_adress').on('input', function() {
            var val = $(this).val();
            generic_datalist_function(val, "#admin_update_krog_adress_list")
        });
    }
});