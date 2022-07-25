import React from "react";
import { Text, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";

const Bar = () => {
    return (
        <View style={{ flex: 1, alignItems: "center",
            justifyContent: "center" }}>
            <Text style={{ color: "#006600", fontSize: 40 }}>
                User Screen!
            </Text>
            <Ionicons name="ios-person-circle-outline"
                      size={80} color="#006600" />
        </View>
    );
};

// @ts-ignore
Bar.navigationOptions = (navData) => {
    return {
        headerTitle: navData.navigation.getParam("username"),
    };
};

export default Bar;
