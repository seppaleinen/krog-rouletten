import React from "react";
import { Button, Text, View } from "react-native";
import RNPickerSelect from "react-native-picker-select";

// @ts-ignore
const Settings = (props) => {
    return (
        <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
            <Text style={{ color: "#006600", fontSize: 40 }}>Sök inställningar</Text>
            <Text style={{fontWeight: "bold"}}>Slumpa inom radien av:</Text>
            <RNPickerSelect
                onValueChange={(value) => console.log(value)}
                items={[
                    { label: "100m", value: "100" },
                    { label: "200m", value: "200" },
                    { label: "300m", value: "300" },
                    { label: "500m", value: "500" },
                    { label: "1000m", value: "1000" },
                    { label: "3000m", value: "3000" },
                    { label: "5000m", value: "5000" },
                    { label: "8000m", value: "8000" },
                ]}
            />
            <Button
                title="Lista närmsta barer"
                color="#006600"
                onPress={() => {
                    props.navigation.navigate("Bar", { username: "Hejhej" });
                }}
            />
        </View>
    );
};

export default Settings;
