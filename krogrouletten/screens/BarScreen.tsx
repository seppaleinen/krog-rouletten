import React, { useState, useEffect } from "react";
import { Text, View, Image, Dimensions, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { HeaderButton, HeaderButtons, Item } from 'react-navigation-header-buttons';
// @ts-ignore
import { GOOGLE_API_KEY } from 'react-native-dotenv';

import Carousel from 'react-native-sideswipe';
import MapViewDirections from 'react-native-maps-directions';
import MapView, { Marker, PROVIDER_GOOGLE, Callout } from 'react-native-maps';
import axios from 'axios';
import haversine from 'haversine-distance'


// @ts-ignore
const Bar = (navData) => {
        let location = navData.navigation.getParam("location");
        const [currentIndex, setCurrentIndex] = useState<number>(0);
        const [data, setData] = useState<Place[]>([])
        const [loaded, setLoaded] = useState<boolean>(false)

        useEffect(() => {
            findNearbies(location, 0)
                .then(nearbyResp => {
                    nearbyResp.data.results
                        .filter((result: any) => result.business_status === 'OPERATIONAL')
                        .forEach((result: any) => {
                            const placeLoc = {
                                "latitude": result.geometry.location.lat,
                                "longitude": result.geometry.location.lng
                            };
                            let place: Place = {
                                location: placeLoc,
                                distance: calculateDistance(location, placeLoc),
                                place_id: result.place_id,
                                price_level: result.price_level,
                                rating: result.rating,
                                name: result.name,
                                open_now: result.opening_hours?.open_now,
                                photo_refs: result.photos ? result.photos
                                        // @ts-ignore
                                        .map((photo: {}) => getPhotoUrl(photo.photo_reference))
                                    : undefined
                            }

                            setData(prevState => {
                                return [...prevState, place];
                            });
                        })
                })
                .catch((error: any) => {
                    console.log("ERROR: " + error);
                    const noPlace = {
                        name: "No place found",
                        location: {
                            latitude: "123",
                            longitude: "123"
                        },
                        distance: '',
                        place_id: ''
                    };
                    setData([noPlace]);
                })
                .finally(() => {
                    setLoaded(true);
                })
        }, []);

        const {width, height} = Dimensions.get('window');
        const ASPECT_RATIO = width / height;
        const LATITUDE_DELTA = 0.0252;
        const LONGITUDE_DELTA = LATITUDE_DELTA * ASPECT_RATIO;

        if (loaded) {
            return (
                <View style={{
                    flex: 1,
                    alignItems: "center",
                    justifyContent: "center"
                }}>
                    <MapView style={StyleSheet.absoluteFill}
                             initialRegion={{
                                 latitude: Number(location.latitude),
                                 longitude: Number(location.longitude),
                                 latitudeDelta: LATITUDE_DELTA,
                                 longitudeDelta: LONGITUDE_DELTA
                             }}
                             customMapStyle={googleMapStyle}
                        //provider={PROVIDER_GOOGLE}
                    >
                        <Marker coordinate={{
                            latitude: Number(data[currentIndex].location.latitude),
                            longitude: Number(data[currentIndex].location.longitude)
                        }}/>

                        <MapViewDirections
                            origin={{
                                latitude: Number(location.latitude),
                                longitude: Number(location.longitude)
                            }}
                            destination={{
                                latitude: Number(data[currentIndex].location.latitude),
                                longitude: Number(data[currentIndex].location.longitude)
                            }}
                            mode={'WALKING'}
                            apikey={getGoogleApiKey()}
                            strokeWidth={3}
                            strokeColor="hotpink"
                        />
                    </MapView>
                    <Carousel
                        data={data}
                        style={{width}}
                        itemWidth={width}
                        threshold={120}
                        contentOffset={0}
                        index={currentIndex}
                        onIndexChange={index => setCurrentIndex(index)}
                        renderItem={({item}) => (
                            <View style={{
                                width: width, height: height, flex: 1,
                                alignItems: "center",
                                justifyContent: "center"
                            }}>
                                <Callout>
                                    <View style={{bottom: 100}}>
                                        <Text style={{color: "#006600", fontSize: 40}}>{item.name}</Text>
                                        {item.photo_refs ? <Image source={{uri:item.photo_refs[0]}}></Image> : <Image style={{width:200}} source={{uri:"https://pixabay.com/get/g201317c887247393a5470e324a8b0e4b1bd2f4015653d90eb0dcfa75c608f5d4d7ad82b02530267b0d797b2504517a4453e5019e1cae49ccdee4abedd1dba4d2108f3239c190d29d19e11d7124b240e1_640.jpg"}}/>}
                                    </View>
                                </Callout>
                            </View>
                        )}
                    />
                </View>
            );
        } else {
            return <View><Text>Loading</Text></View>
        }
    };

const findNearbies = async (location: Location, count: number): Promise<any> => {
    let radius = 500 * ((count + 1) ** 2);
    let type = 'bar';
    let API_KEY = getGoogleApiKey();
    let loc = `${location.latitude},${location.longitude}`;
    if (count > 5) {
        throw new Error("Could not find any bars nearby");
    }
    let url = `https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${loc}&radius=${radius}&type=${type}&key=${API_KEY}`;

    return axios.get(url,)
        .then(response => {
            if (response.data.status === 'OK') {
                return response;
            } else if (response.data.status === 'ZERO_RESULTS') {
                console.log("No results. Trying again: " + 500 * ((count + 2) ** 2));
                return findNearbies(location, count + 1);
            } else {
                console.log("Wrong statuscode %s raising as error", response.data.status)
                throw new Error(response.data.error_message);
            }
        })
        .catch(error => console.error("Could not get nearby places due to error.", error));
}

/**
const getDetails = async (placeId: string) => {
    let API_KEY = getGoogleApiKey();
    let url = `https://maps.googleapis.com/maps/api/place/details/json?placeid=${placeId}&language=sv&key=${API_KEY}`;
    return axios(url,)
        .then(response => {
            if (response.data.status === 'OK') {
                return response.data.result
            } else {
                throw new Error(`Wrong statuscode ${response.data.status}, throwing error ${response.data.error_message}`);
            }
        })
        .catch(error => console.error("Could not get details due to error.", error))
}
**/

const getPhotoUrl = (photoId: string) => {
    let API_KEY = getGoogleApiKey();
    return `https://maps.googleapis.com/maps/api/place/photo?maxheight=414&photoreference=${photoId}&key=${API_KEY}`;
}

const calculateDistance = (userLoc: Location, placeLoc: Location) => {
    const user = {latitude: Number(userLoc.latitude), longitude: Number(userLoc.longitude)}
    const place = {latitude: Number(placeLoc.latitude), longitude: Number(placeLoc.longitude)}
    let distance = Number(haversine(user, place).toFixed(1));
    return distance > 1000 ? (distance / 1000).toFixed(1) + 'km' : distance.toFixed(1) + 'm';
}

// @ts-ignore
const HeaderButtonComponent = (props) => (
    <HeaderButton
        IconComponent={Ionicons}
        iconSize={23}
        color="#FFF"
        iconName="settingsButton"
        {...props}
    />
);

const getGoogleApiKey = () => {
    const processKey = process.env.GOOGLE_API_KEY;
    if (processKey) {
        return processKey;
    }
    const dotEnvKey = GOOGLE_API_KEY;
    if (dotEnvKey) {
        return dotEnvKey;
    } else {
        throw new Error("No google api key found in environment");
    }
}

// @ts-ignore
Bar.navigationOptions = (navData) => {
    return {
        headerTitle: "Krogrouletten",
        headerRight: () => (
            <HeaderButtons HeaderButtonComponent={HeaderButtonComponent}>
                <Item
                    title="Setting"
                    iconName="ios-settings-outline"
                    onPress={() => navData.navigation.navigate("Setting")}
                />
            </HeaderButtons>
        ),
    };
};

interface Place {
    name: string;
    location: Location;
    distance: string;
    place_id: string;

    address?: string;
    open_now?: boolean;
    rating?: number;
    price_level?: number;
    open?: string[];
    photo_refs?: string[];
    reviews?: Review[];
    types?: string[];
}

export interface Location {
    latitude: string;
    longitude: string;
}

interface Review {
    name: string;
    rating: number;
    text: string;
}

export default Bar;

const googleMapStyle = [
    {
        "elementType": "geometry",
        "stylers": [
            {
                "color": "#1d2c4d"
            }
        ]
    },
    {
        "elementType": "labels.text.fill",
        "stylers": [
            {
                "color": "#8ec3b9"
            }
        ]
    },
    {
        "elementType": "labels.text.stroke",
        "stylers": [
            {
                "color": "#1a3646"
            }
        ]
    },
    {
        "featureType": "administrative",
        "elementType": "geometry",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "administrative.country",
        "elementType": "geometry.stroke",
        "stylers": [
            {
                "color": "#4b6878"
            }
        ]
    },
    {
        "featureType": "administrative.land_parcel",
        "elementType": "labels",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "administrative.land_parcel",
        "elementType": "labels.text.fill",
        "stylers": [
            {
                "color": "#64779e"
            }
        ]
    },
    {
        "featureType": "administrative.province",
        "elementType": "geometry.stroke",
        "stylers": [
            {
                "color": "#4b6878"
            }
        ]
    },
    {
        "featureType": "landscape.man_made",
        "elementType": "geometry.stroke",
        "stylers": [
            {
                "color": "#334e87"
            }
        ]
    },
    {
        "featureType": "landscape.natural",
        "elementType": "geometry",
        "stylers": [
            {
                "color": "#023e58"
            }
        ]
    },
    {
        "featureType": "poi",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "poi",
        "elementType": "geometry",
        "stylers": [
            {
                "color": "#283d6a"
            }
        ]
    },
    {
        "featureType": "poi",
        "elementType": "labels.text",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "poi",
        "elementType": "labels.text.fill",
        "stylers": [
            {
                "color": "#6f9ba5"
            }
        ]
    },
    {
        "featureType": "poi",
        "elementType": "labels.text.stroke",
        "stylers": [
            {
                "color": "#1d2c4d"
            }
        ]
    },
    {
        "featureType": "poi.park",
        "elementType": "geometry.fill",
        "stylers": [
            {
                "color": "#023e58"
            }
        ]
    },
    {
        "featureType": "poi.park",
        "elementType": "labels.text.fill",
        "stylers": [
            {
                "color": "#3C7680"
            }
        ]
    },
    {
        "featureType": "road",
        "elementType": "geometry",
        "stylers": [
            {
                "color": "#304a7d"
            }
        ]
    },
    {
        "featureType": "road",
        "elementType": "labels.icon",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "road",
        "elementType": "labels.text.fill",
        "stylers": [
            {
                "color": "#98a5be"
            }
        ]
    },
    {
        "featureType": "road",
        "elementType": "labels.text.stroke",
        "stylers": [
            {
                "color": "#1d2c4d"
            }
        ]
    },
    {
        "featureType": "road.highway",
        "elementType": "geometry",
        "stylers": [
            {
                "color": "#2c6675"
            }
        ]
    },
    {
        "featureType": "road.highway",
        "elementType": "geometry.stroke",
        "stylers": [
            {
                "color": "#255763"
            }
        ]
    },
    {
        "featureType": "road.highway",
        "elementType": "labels.text.fill",
        "stylers": [
            {
                "color": "#b0d5ce"
            }
        ]
    },
    {
        "featureType": "road.highway",
        "elementType": "labels.text.stroke",
        "stylers": [
            {
                "color": "#023e58"
            }
        ]
    },
    {
        "featureType": "road.local",
        "elementType": "labels",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "transit",
        "stylers": [
            {
                "visibility": "off"
            }
        ]
    },
    {
        "featureType": "transit",
        "elementType": "labels.text.fill",
        "stylers": [
            {
                "color": "#98a5be"
            }
        ]
    },
    {
        "featureType": "transit",
        "elementType": "labels.text.stroke",
        "stylers": [
            {
                "color": "#1d2c4d"
            }
        ]
    },
    {
        "featureType": "transit.line",
        "elementType": "geometry.fill",
        "stylers": [
            {
                "color": "#283d6a"
            }
        ]
    },
    {
        "featureType": "transit.station",
        "elementType": "geometry",
        "stylers": [
            {
                "color": "#3a4762"
            }
        ]
    },
    {
        "featureType": "water",
        "elementType": "geometry",
        "stylers": [
            {
                "color": "#0e1626"
            }
        ]
    },
    {
        "featureType": "water",
        "elementType": "labels.text.fill",
        "stylers": [
            {
                "color": "#4e6d70"
            }
        ]
    }
];
