import React, { useState, useEffect } from "react";
import { Text, View, Dimensions, StyleSheet, Alert } from "react-native";
// @ts-ignore
import { GOOGLE_API_KEY } from 'react-native-dotenv';

import Carousel from 'react-native-sideswipe';
import MapViewDirections from 'react-native-maps-directions';
import MapView, { Marker, Circle, PROVIDER_GOOGLE, Callout } from 'react-native-maps';
import { findNearbies, getGoogleApiKey } from './Service';
import { Place, Location } from './Types';
import { Card } from "@rneui/themed";


function findNearbiesAndSetData(location: Location,
                                setData: (value: (((prevState: Place[]) => Place[]) | Place[])) => void,
                                setLoaded: (value: (((prevState: boolean) => boolean) | boolean)) => void,
                                count: number,
                                setCount: (value: (((prevState: number) => number) | number)) => void) {
    findNearbies(location, count)
        .then((nearbyResp: Place[]) => {
            setCount(count);
            nearbyResp?.slice(0, 20)?.forEach(place => {
                setData(prevState => {
                    if (prevState.some(elem => elem.place_id === place.place_id)) {
                        return [...prevState];
                    } else {
                        return [...prevState, place];
                    }
                });
            })
        })
        .catch((error: any) => {
            console.log("ERROR: " + error);
        })
        .finally(() => {
            setLoaded(true);
        });
}

// @ts-ignore
const Bar = (navData) => {
    let location = navData.navigation.getParam("location");
    const [currentIndex, setCurrentIndex] = useState<number>(0);
    const [data, setData] = useState<Place[]>([])
    const [loaded, setLoaded] = useState<boolean>(false)
    const [count, setCount] = useState<number>(0)

    useEffect(() => {
        setLoaded(false);
        findNearbiesAndSetData(location, setData, setLoaded, count, setCount);
    }, []);

    const {width, height} = Dimensions.get('window');
    const ASPECT_RATIO = width / height;
    const LATITUDE_DELTA = 0.0252;
    const LONGITUDE_DELTA = LATITUDE_DELTA * ASPECT_RATIO;

    const createMessageAlert = (title: string, text: string) => {
        return (
            Alert.alert(title, text, [{text: 'OK', onPress: () => console.log("OKAYYYY")}])
        );
    };

    if (loaded) {
        return (
            <View style={{
                flex: 1,
                alignItems: "center",
                justifyContent: "center"
            }}>
                <MapView style={{...StyleSheet.absoluteFillObject, flex: 1}}
                         initialRegion={{
                             latitude: location.latitude,
                             longitude: location.longitude,
                             latitudeDelta: LATITUDE_DELTA,
                             longitudeDelta: LONGITUDE_DELTA
                         }}
                         customMapStyle={googleMapStyle}
                         provider={PROVIDER_GOOGLE}
                >
                    <Marker coordinate={{
                        latitude: data[currentIndex].location.latitude,
                        longitude: data[currentIndex].location.longitude
                    }}/>

                    <MapViewDirections
                        origin={{
                            latitude: location.latitude,
                            longitude: location.longitude
                        }}
                        destination={{
                            latitude: data[currentIndex].location.latitude,
                            longitude: data[currentIndex].location.longitude
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
                    onEndReached={() => {
                        findNearbiesAndSetData(location, setData, setLoaded, (count + 1), setCount);
                    }}
                    renderItem={({item}) => (
                        <View style={{
                            width: width,
                            height: height,
                            flex: 1,
                            alignItems: "center",
                            justifyContent: "center"
                        }}>
                            <Callout>
                                <View style={{width: 400, height: 150, bottom: -190}}>
                                    <Card>
                                        <Card.Title>{item.name}</Card.Title>
                                        <Card.Divider />
                                        {item.photo_refs ? <Card.Image style={{width: "100%", height: 100}} source={{uri: item.photo_refs[0]}}></Card.Image> : <Text/>}
                                    </Card>
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

// @ts-ignore
Bar.navigationOptions = (navData) => {
    return {
        headerTitle: "Krogrouletten",
    };
};

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
