const getGoogleApiKey = () => {
    const processKey = process.env.GOOGLE_API_KEY;
    if (processKey) {
        return processKey;
    } else {
        throw new Error("GOOGLE_API_KEY not set in environment");
    }
}
export const getSentryDSN = () => {
    const processKey = process.env.SENTRY_DSN;
    if (processKey) {
        return processKey;
    } else {
        return undefined;
    }
}
export const getSentryToken = () => {
    const processKey = process.env.SENTRY_TOKEN;
    if (processKey) {
        return processKey;
    } else {
        return undefined;
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
    config.hooks.postPublish[0].config.authToken = getSentryToken();
    return {
        ...config,
    };
};

