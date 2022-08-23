import { Button, Dimensions, Text, View, Image } from 'react-native';
import Carousel from 'react-native-sideswipe';
import React from 'react';
import { getPhotoUrl } from './Service';

const {width, height} = Dimensions.get('window');

const Details = (navData: any) => {
    const place = navData.navigation.getParam("place");
    const details = navData.navigation.getParam("details");

    return (
        <View>
            <Carousel
                data={details.photos}
                style={{width}}
                itemWidth={width}
                threshold={120}
                contentOffset={0}
                renderItem={({item}) => (
                    <View style={{
                        width: width,
                        height: height,
                        flex: 1,
                    }}>
                        {item.photo_reference ? <Image style={{width: "100%", height: 400}}
                                                       source={{uri: getPhotoUrl(item.photo_reference)}}></Image> : <Text> </Text>}
                    </View>
                )}
            />
        </View>
    )
};

Details.navigationOptions = (navData: any) => {
    return {
        headerTitle: navData.navigation.getParam("place").name,
    };
};


export default Details;