import React, { useState, useEffect } from "react";
import { Text, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import axios from 'axios';
import { HeaderButton, HeaderButtons, Item } from 'react-navigation-header-buttons';
import Home from './HomeScreen';
// @ts-ignore
import {GOOGLE_MAPS_API_KEY} from 'react-native-dotenv';


// @ts-ignore
const Bar = (navData) => {
    let location = navData.navigation.getParam("location");
    const [data, setData] = useState({})
    useEffect(() => {
        clickRandom(location, 0)
            .then(response => setData(response[0]))
    }, [])
    return (
        <View style={{
            flex: 1, alignItems: "center",
            justifyContent: "center"
        }}>
            <Text style={{color: "#006600", fontSize: 40}}>
                User Screen! {data.name}
            </Text>
            <Ionicons name="ios-person-circle-outline"
                      size={80} color="#006600"/>
        </View>
    );
};

const clickRandom = (location: string, count: number) => {
    let radius = 500 * (count + 1);
    let type = 'bar';
    let API_KEY = GOOGLE_MAPS_API_KEY;

    console.log('Count: ' + count);
    if (count > 5) {
        throw new Error("Could not find any bars nearby");
    }
    let url = `https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location}&radius=${radius}&type=${type}&key=${API_KEY}`;
    return axios(url,)
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
Home.navigationOptions = (navData) => {
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

export default Bar;
