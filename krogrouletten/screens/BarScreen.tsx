import React, { useState, useEffect } from "react";
import { Text, View, Image, Dimensions, ScrollView } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import axios from 'axios';
import { HeaderButton, HeaderButtons, Item } from 'react-navigation-header-buttons';
import haversine from 'haversine-distance'
// @ts-ignore
import { GOOGLE_API_KEY } from 'react-native-dotenv';
import Carousel from 'react-native-sideswipe';


// @ts-ignore
const Bar = (navData) => {
    let location = navData.navigation.getParam("location");
    const [currentIndex, setCurrentIndex] = useState<number>(0);
    const [data, setData] = useState<Place[]>([])

    useEffect(() => {
        findNearbies(location, 0)
            .then(nearbyResp => {
                nearbyResp.data.results
                    .filter((result: any) => result.business_status === 'OPERATIONAL')
                    .forEach((result: any) => {
                        getDetails(result.place_id)
                            .then(detailsResp => {
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

                                    address: detailsResp.formatted_address,
                                    open: detailsResp.opening_hours?.weekday_text,
                                    photo_refs: detailsResp.photos ? detailsResp.photos
                                            .map((photo: {}) => getPhotoUrl(photo.photo_reference))
                                        : undefined,
                                    reviews: detailsResp.reviews ? detailsResp.reviews
                                        .map((review: {}) => {
                                            let rev: Review = {
                                                "name": review.author_name,
                                                "rating": review.rating,
                                                "text": review.text
                                            };
                                            return rev;
                                        }) : [],
                                    types: detailsResp.types ? detailsResp.types
                                        .filter((type: string) => type !== 'point_of_interest' && type !== 'establishment')
                                        .map((type: string) => type) : []
                                }

                                setData(prevState => {
                                    console.log("PLACE: " + JSON.stringify(place));
                                    return [...prevState, place];
                                });
                            })
                    })
            })
            .catch(error => {
                console.log("ERROR: " + error);
                const noPlace = {
                    name: "No place found",
                    location: {
                        latitude: "123",
                        longitude: "123"
                    },
                    distance: '',
                    place_id: "placeid",
                    types: []
                };
                setData([noPlace]);
            })
    }, [])

    const {width} = Dimensions.get('window');

    return (
        <View style={{
            flex: 1,
            alignItems: "center",
            justifyContent: "center"
        }}>
            <Carousel
                data={data}
                style={{width}}
                itemWidth={width}
                threshold={120}
                contentOffset={0}
                index={currentIndex}
                onIndexChange={index =>
                    setCurrentIndex(index)
                }
                renderItem={({item}) => (
                    <View style={{
                        width: width, paddingHorizontal: 10, flex: 1,
                        alignItems: "center",
                        justifyContent: "center"
                    }}>
                        {item.photo_refs ?
                            <Image style={{width: width, height: 200}} source={{uri: item.photo_refs[0]}}></Image> :
                            <Image style={{width: width, height: 200}}
                                   source={{uri: 'https://unsplash.com/photos/_RBcxo9AU-U/download?ixid=MnwxMjA3fDB8MXxzZWFyY2h8Mnx8cGxhY2V8ZW58MHx8fHwxNjU5NTQzMzk1&force=true&w=640'}}/>}
                        <Text style={{color: "#006600", fontSize: 40}}>{item.name}</Text>
                        {item.open_now !== undefined ? <Text>Open now: {String(item.open_now)}</Text> : <Text/>}
                        {item.open !== undefined ? <Text>Open: {'\n'}{item.open?.join("\n")}</Text> : <Text/>}
                        <Text>Distance to: {item.distance}</Text>
                    </View>
                )}
            />
        </View>
    );
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
    types: string[];
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
