import axios from 'axios';
import { Location, Place } from './Types';
// @ts-ignore
import { GOOGLE_API_KEY } from 'react-native-dotenv';
import haversine from 'haversine-distance';

export const findNearbies = async (location: Location, count: number): Promise<any> => {
    let radius = 500 * ((count + 1) ** 2);
    let type = 'bar';
    let API_KEY = getGoogleApiKey();
    let loc = `${location.latitude},${location.longitude}`;
    if (count > 5) {
        throw new Error("Could not find any bars nearby");
    }
    let url = `https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${loc}&radius=${radius}&type=${type}&key=${API_KEY}`;

    return axios.get(url,)
        .then(response => {
            if (response.data.status === 'OK') {
                return response.data.results
                    .filter((result: any) => result.business_status === 'OPERATIONAL')
                    .map((result: any) => {
                        const placeLoc = {
                            "latitude": result.geometry.location.lat,
                            "longitude": result.geometry.location.lng
                        };
                        let place: Place = {
                            location: placeLoc,
                            distance: calculateDistance(location, placeLoc),
                            place_id: result.place_id,
                            price_level: result.price_level,
                            rating: result.rating,
                            name: result.name,
                            open_now: result.opening_hours?.open_now,
                            photo_refs: result.photos ? result.photos
                                    // @ts-ignore
                                    .map((photo: {}) => getPhotoUrl(photo.photo_reference))
                                : undefined
                        }
                        return place;
                    });
            } else if (response.data.status === 'ZERO_RESULTS') {
                console.log("No results. Trying again: " + 500 * ((count + 2) ** 2));
                return findNearbies(location, count + 1);
            } else {
                console.log("Wrong statuscode %s raising as error", response.data.status)
                throw new Error(response.data.error_message);
            }
        })
        .catch(error => console.error("Could not get nearby places due to error.", error));
}

export const getDetails = async (placeId: string) => {
    let API_KEY = getGoogleApiKey();
    let url = `https://maps.googleapis.com/maps/api/place/details/json?placeid=${placeId}&language=sv&key=${API_KEY}`;
    return axios(url,)
        .then(response => {
            if (response.data.status === 'OK') {
                return response.data.result
            } else {
                throw new Error(`Wrong statuscode ${response.data.status}, throwing error ${response.data.error_message}`);
            }
        })
        .catch(error => console.error("Could not get details due to error.", error))
}

export const getGoogleApiKey = () => {
    const processKey = process.env.GOOGLE_API_KEY;
    if (processKey) {
        return processKey;
    }
    const dotEnvKey = GOOGLE_API_KEY;
    if (dotEnvKey) {
        return dotEnvKey;
    } else {
        throw new Error("No google api key found in environment");
    }
}

const getPhotoUrl = (photoId: string) => {
    let API_KEY = getGoogleApiKey();
    return `https://maps.googleapis.com/maps/api/place/photo?maxheight=414&photoreference=${photoId}&key=${API_KEY}`;
}

const calculateDistance = (userLoc: Location, placeLoc: Location) => {
    const user = {latitude: Number(userLoc.latitude), longitude: Number(userLoc.longitude)}
    const place = {latitude: Number(placeLoc.latitude), longitude: Number(placeLoc.longitude)}
    let distance = Number(haversine(user, place).toFixed(1));
    return distance > 1000 ? (distance / 1000).toFixed(1) + 'km' : distance.toFixed(1) + 'm';
}
