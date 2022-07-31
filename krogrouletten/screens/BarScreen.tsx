import React, { useState, useEffect } from "react";
import { Text, View, Image } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import axios from 'axios';
import { HeaderButton, HeaderButtons, Item } from 'react-navigation-header-buttons';
import Home from './HomeScreen';
// @ts-ignore
import haversine from 'haversine-distance'


// @ts-ignore
const Bar = (navData) => {
    let location = navData.navigation.getParam("location");
    const [data, setData] = useState<Place>({
        name: "name",
        open_now: false,
        open: [],
        location: {
            latitude: "123",
            longitude: "123"
        },
        distance: '',
        price_level: 1,
        rating: 1,
        place_id: "placeid",
        address: "address",
        photo_refs: [],
        reviews: [],
        types: []
    })
    useEffect(() => {
        clickRandom(location, 0)
            .then(nearbyResp => {
                const resp1 = nearbyResp[0];
                getDetails(resp1.place_id)
                    .then(detailsResp => {
                        const placeLoc = {
                            "latitude": resp1.geometry.location.lat,
                            "longitude": resp1.geometry.location.lng
                        };
                        let place: Place = {
                            location: placeLoc,
                            distance: calculateDistance(location, placeLoc),
                            place_id: resp1.place_id,
                            price_level: resp1.price_level,
                            rating: resp1.rating,
                            name: resp1.name,
                            open_now: resp1.opening_hours.open_now,

                            address: detailsResp.formatted_address,
                            open: detailsResp.opening_hours.weekday_text,
                            photo_refs: detailsResp.photos
                                .map((photo: {}) => getPhotoUrl(photo.photo_reference)),
                            reviews: detailsResp.reviews
                                .map((review: {}) => {
                                    let rev: Review = {
                                        "name": review.author_name,
                                        "rating": review.rating,
                                        "text": review.text
                                    };
                                    return rev;
                                }),
                            types: detailsResp.types
                                .filter((type: string) => type !== 'point_of_interest' && type !== 'establishment')
                                .map((type: string) => type)
                        }

                        setData(place);
                    })
            })
    }, [])
    return (
        <View style={{
            flex: 1, alignItems: "center",
            justifyContent: "center"
        }}>
            <Image style={{width: 200, height: 200}} source={{uri: data.photo_refs[0]}}></Image>
            <Text style={{color: "#006600", fontSize: 40}}>{data.name}</Text>
            <Text>Open now: {String(data.open_now)}</Text>
            <Text>Open: {'\n'}{data.open.join("\n")}</Text>
            <Text>Distance to: {data.distance}</Text>
        </View>
    );
};

const clickRandom = (location: Location, count: number) => {
    let radius = 500 * (count + 1);
    let type = 'bar';
    let API_KEY = process.env.GOOGLE_API_KEY;
    let loc = `${location.latitude},${location.longitude}`;

    if (count > 5) {
        throw new Error("Could not find any bars nearby");
    }
    let url = `https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${loc}&radius=${radius}&type=${type}&key=${API_KEY}`;
    return axios.get(url,)
        .then(response => {
            if (response.data.status === 'OK') {
                return response.data.results;
            } else if (response.data.status === 'ZERO_RESULTS') {
                console.log("No results. Trying again");
                clickRandom(location, count + 1);
            } else {
                console.log("Wrong statuscode %s raising as error", response.data.status)
                throw new Error(response.data.error_message);
            }
        })
        .catch(error => console.error("Could not get nearby places due to error.", error));
}

const getDetails = async (placeId: string) => {
    let API_KEY = process.env.GOOGLE_API_KEY;
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
    let API_KEY = process.env.GOOGLE_API_KEY;
    return `https://maps.googleapis.com/maps/api/place/photo?maxheight=414&photoreference=${photoId}&key=${API_KEY}`;
}

const calculateDistance = (userLoc: Location, placeLoc: Location) => {
    const user = { latitude: Number(userLoc.latitude), longitude: Number(userLoc.longitude) }
    const place = { latitude: Number(placeLoc.latitude), longitude: Number(placeLoc.longitude) }
    let distance = Number(haversine(user, place).toFixed(1));
    return distance > 1000 ? (distance / 1000) + 'km' : distance + 'm';
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
    open_now: boolean;
    location: Location;
    distance: string;
    price_level: number;
    rating: number;
    place_id: string;

    address: string;
    open: string[];
    photo_refs: string[];
    reviews: Review[];
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
