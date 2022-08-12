import React from "react";
import { Text, View, Button, StyleSheet } from "react-native";
import Icon from 'react-native-vector-icons/FontAwesome';
import * as Location from 'expo-location';

const style = StyleSheet.create({
    view: {
        flex: 1,
        alignItems: "center",
        justifyContent: "center"
    },
    welcomeDiv: {
        color: "#006600",
        fontSize: 30
    },
    bold: {
        fontWeight: "bold"
    }
});

const Home = (props: any) => {
    return (
        <View style={style.view}>
            <Text style={style.welcomeDiv}>
                <Text>Välkommen till </Text>
                <Text style={style.bold}>Krogrouletten!</Text>
            </Text>
            <Text>Tryck på kugghjulet för sökinställningar eller tryck på slumpa för att börja direkt!</Text>
            <Icon name="arrow-down" size={80}/>
            <Button
                title="Slumpa"
                color="#006600"
                onPress={async () => {
                    getLocation()
                        .then(location => {
                            const loc = {
                                latitude: Number(location?.coords.latitude),
                                longitude: Number(location?.coords.longitude)
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
}

// @ts-ignore
Home.navigationOptions = (navData) => {
    return {
        headerTitle: "Krogrouletten",
    };
};

export default Home;
