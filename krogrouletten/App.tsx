import React from "react";
import { createAppContainer } from "react-navigation";
import { createStackNavigator } from "react-navigation-stack";

import HomeScreen from "./screens/HomeScreen";
import BarScreen from "./screens/BarScreen";
import SettingScreen from "./screens/SettingScreen";

const AppNavigator = createStackNavigator(
    {
        Home: HomeScreen,
        Bar: BarScreen,
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
