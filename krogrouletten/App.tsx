import React from "react";
import { createAppContainer } from "react-navigation";
import { createStackNavigator } from "react-navigation-stack";

import HomeScreen from "./screens/HomeScreen";
import UserScreen from "./screens/UserScreen";
import SettingScreen from "./screens/SettingScreen";

const AppNavigator = createStackNavigator(
    {
        Home: HomeScreen,
        User: UserScreen,
        Setting: SettingScreen,
    },
    {
        defaultNavigationOptions: {
            headerStyle: {
                backgroundColor: "#006600",
            },
            headerTitleStyle: {
                fontWeight: "bold",
                color: "#FFF",
            },
            headerTintColor: "#FFF",
        },
    },
    {
        initialRouteName: "Home",
    }
);

const Navigator = createAppContainer(AppNavigator);

export default function App() {
    return (
        <Navigator>
            <HomeScreen />
        </Navigator>
    );
}
