$(document).ready(function() {
    'use strict';

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

    function search_adress_datalist() {
        var val = $(this).val();
        if (val === "") return;
        if (val.length < 10) return;
        if (val === last_adress) return;
        if (val.indexOf(' ') < 0) return;
        var url = "http://maps.googleapis.com/maps/api/geocode/json?address=" + encodeURIComponent(val) + "&sensor=false";
        $.get(url, function(res) {
            var dataList = $("#search_adress_list");
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

    function user_krog_adress_datalist() {
        var val = $(this).val();
        if (val === "") return;
        if (val.length < 10) return;
        if (val === last_adress) return;
        if (val.indexOf(' ') < 0) return;
        var url = "http://maps.googleapis.com/maps/api/geocode/json?address=" + encodeURIComponent(val) + "&sensor=false";
        $.get(url, function(res) {
            var dataList = $("#user_krog_adress_list");
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

    function admin_ny_krog_adress_datalist() {
        var val = $(this).val();
        if (val === "") return;
        if (val.length < 10) return;
        if (val === last_adress) return;
        if (val.indexOf(' ') < 0) return;
        var url = "http://maps.googleapis.com/maps/api/geocode/json?address=" + encodeURIComponent(val) + "&sensor=false";
        $.get(url, function(res) {
            var dataList = $("#admin_ny_krog_adress_list");
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

    function admin_update_krog_adress_datalist() {
        var val = $(this).val();
        if (val === "") return;
        if (val.length < 10) return;
        if (val === last_adress) return;
        if (val.indexOf(' ') < 0) return;
        var url = "http://maps.googleapis.com/maps/api/geocode/json?address=" + encodeURIComponent(val) + "&sensor=false";
        $.get(url, function(res) {
            var dataList = $("#admin_update_krog_adress_list");
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

    $("#slumpaGPS").click(function() {
        getLocation();
    });
    var last_adress;

    if (document.createElement("datalist").options) {
        $('#sok_adress').on('input', search_adress_datalist);
        $('#user_krog_adress').on('input', user_krog_adress_datalist);
        $('#admin_ny_krog_adress').on('input', admin_ny_krog_adress_datalist);
        $('#admin_update_krog_adress').on('input', admin_update_krog_adress_datalist);
    }
});