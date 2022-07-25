import React, { useState } from "react";
import { Text, View, TextInput, Button } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import Icon from 'react-native-vector-icons/FontAwesome';
import {
    Item,
    HeaderButton,
    HeaderButtons,
} from "react-navigation-header-buttons";



// @ts-ignore
const Home = (props) => {
    const [input, setInput] = useState("");
    return (
        <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
            <Text style={{ color: "#006600", fontSize: 30 }}>
                <Text>Välkommen till </Text>
                <Text style={{fontWeight: "bold"}}>Krogrouletten!</Text>
            </Text>
            <Text>Tryck på kugghjulet för sökinställningar eller tryck på slumpa för att börja direkt!</Text>
            <Icon name="arrow-down" size={80} />
            <Button
                title="Slumpa"
                color="#006600"
                onPress={() => {
                    clickRandom(props);
                    props.navigation.navigate("Bar", { username: "Hejhej" });
                }}
            />
        </View>
    );
};

// @ts-ignore
const clickRandom = (props) => {
    /**
     *     search_params = ''
     *     search_params += 'location=' + str(form.latitude.data) + ',' + str(form.longitude.data) + '&'
     *     search_params += 'radius=' + str(distance) + '&'
     *     search_params += 'type=bar'
     *
     *     GOOGLE_SEARCH = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?%s&key=%s'
     *
     *     search_response = requests.get(GOOGLE_SEARCH % (search_params, API_KEY)).json()
     */
    let location = '';
    let radius = '';
    let type = 'bar';
    let API_KEY = '';

    let url = `https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location}&radius=${radius}&type=${type}&key=${API_KEY}`;

    console.log("URL: " + url);
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
        headerTitle: "Home",
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
