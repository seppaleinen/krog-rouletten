'use strict';

var coords;
function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    }
}
function showPosition(position) {
    coords = position.coords;
    console.log("Latitude: " + coords.latitude + ", Longitude: " + coords.longitude);
}

//getLocation();