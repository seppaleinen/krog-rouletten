const getGoogleApiKey = () => {
    const processKey = process.env.GOOGLE_API_KEY;
    if (processKey) {
        return processKey;
    } else {
        throw new Error("GOOGLE_API_KEY not set in environment");
    }
}

const googleApiKey = getGoogleApiKey();

export default ({config}) => {
    config.android.config = {
        "googleMaps": {
            "apiKey": googleApiKey
        }
    }
    config.ios.config = {
        "googleMapsApiKey": googleApiKey
    }
    return {
        ...config,
    };
};

