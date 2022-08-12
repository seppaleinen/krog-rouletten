export interface Place {
    name: string;
    location: Location;
    distance: string;
    place_id: string;

    address?: string;
    open_now?: boolean;
    rating?: number;
    price_level?: number;
    open?: string[];
    photo_refs?: string[];
    reviews?: Review[];
    types?: string[];
}

export interface Location {
    latitude: number;
    longitude: number;
}

export interface Review {
    name: string;
    rating: number;
    text: string;
}
