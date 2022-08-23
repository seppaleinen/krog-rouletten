import React from "react";
import "react-native-gesture-handler";
import { createAppContainer } from "react-navigation";
import { createStackNavigator } from "react-navigation-stack";
import * as Sentry from 'sentry-expo';

import HomeScreen from "./screens/HomeScreen";
import BarScreen from "./screens/BarScreen";
import DetailsScreen from "./screens/DetailsScreen";

import { getSentryDSN } from './app.config';

Sentry.init({
    dsn: getSentryDSN(),
    enableInExpoDevelopment: true,
    beforeBreadcrumb(breadcrumb, hint) {
        if (breadcrumb.category === 'console') {
            return null
        }
        return breadcrumb
    },
    maxBreadcrumbs: 50,
    debug: true, // If `true`, Sentry will try to print out useful debugging information if something goes wrong with sending the event. Set it to `false` in production
});

const AppNavigator = createStackNavigator(
    {
        Home: HomeScreen,
        Bar: BarScreen,
        Details: DetailsScreen,
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
        // @ts-ignore
        <Navigator>
            <HomeScreen />
        </Navigator>
    );
}
