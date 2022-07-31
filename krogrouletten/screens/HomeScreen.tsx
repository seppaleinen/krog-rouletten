import React, { useState } from "react";
import { Text, View, TextInput, Button } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import Icon from 'react-native-vector-icons/FontAwesome';
import {
    Item,
    HeaderButton,
    HeaderButtons,
} from "react-navigation-header-buttons";
import axios from 'axios';
import * as Location from 'expo-location';

// @ts-ignore
const Home = (props) => {
    const [input, setInput] = useState("");
    return (
        <View style={{flex: 1, alignItems: "center", justifyContent: "center"}}>
            <Text style={{color: "#006600", fontSize: 30}}>
                <Text>Välkommen till </Text>
                <Text style={{fontWeight: "bold"}}>Krogrouletten!</Text>
            </Text>
            <Text>Tryck på kugghjulet för sökinställningar eller tryck på slumpa för att börja direkt!</Text>
            <Icon name="arrow-down" size={80}/>
            <Button
                title="Slumpa"
                color="#006600"
                onPress={async () => {
                    //let data = "59.4496733,17.932673";
                    getLocation()
                        .then(location => {
                            const loc = {
                                latitude: location.coords.latitude,
                                longitude: location.coords.longitude
                            }
                            props.navigation.navigate("Bar", {location: loc});
                        });
                }}
            />
        </View>
    );
};

const getLocation = () => {
    return Location.requestForegroundPermissionsAsync()
        .then(status => {
            if (status.status !== 'granted') {
                console.error('Permission to access location was denied');
                return;
            }
            return Location.getCurrentPositionAsync({});
        });

    //return location.coords.latitude +  ',' + location.coords.longitude;
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

export default Home;
