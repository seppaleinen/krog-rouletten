# coding=UTF-8
import random, requests, json, urllib, os, jsonpickle
from flask import request, session, redirect, jsonify
from application.model import Krog, Review
from haversine import haversine

API_KEY = os.getenv('MAPS_API_KEY')
MAPS_EMBED_KEY = 'AIzaSyDMtS6rg17-Tr2neNR0b0RSgrF5RxmfUhQ'
GOOGLE_SEARCH = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?%s&key=%s'
GOOGLE_DETAILS = 'https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&language=sv&key=%s'
#Should either be this map or one with directions
GOOGLE_EMBEDDED_MAPS = 'https://www.google.com/maps/embed/v1/place?q=place_id:%s&key=%s'
GOOGLE_EMBEDDED_DIRECTIONS_MAPS = 'https://www.google.com/maps/embed/v1/directions?mode=%s&origin=%s&destination=place_id:%s&key=%s'
GOOGLE_PLACES_PHOTO = 'https://maps.googleapis.com/maps/api/place/photo?maxheight=414&photoreference=%s&key=%s'
GOOGLE_PLACES_LIST_PHOTO = 'https://maps.googleapis.com/maps/api/place/photo?maxwidth=340&photoreference=%s&key=%s'


def random_page():
    form = request.get_json(force=True)

    if form and form['searchtype'] == 'gps':
        try:
            place_id = filter_search_from_previous_results(form)
            print(place_id)

            #session[Helper().get_user_ip()] += place_id + ';'

            return jsonify(details(place_id, form['latitude'] + ',' + form['longitude']))
        except Exception as e:
            return redirect('/error/nocando', 302)
    elif form and form['searchtype'] == 'list':
        search_response = search_google_and_broaden_if_no_results(form)
        krog_lista = []
        for result in search_response['results']:
            dist = Helper.calculate_distance_between_locations(
                form['latitude'],
                form['longitude'],
                result['geometry']['location']['lat'],
                result['geometry']['location']['lng'])

            photo = None
            try:
                for photo_ref in result['photos']:
                    photo = GOOGLE_PLACES_LIST_PHOTO % (photo_ref['photo_reference'], API_KEY)
            except Exception:
                photo = '/static/img/bg2.jpeg'

            krog_lista.append(Krog(
                namn=result['name'],
                bar_types='',
                beskrivning='',
                adress=result['vicinity'],
                oppet_tider='',
                iframe_lank='',
                betyg='',
                reviews='',
                photos=photo,
                distance=dist,
                place_id=result['place_id']
            ))

        return jsonify(jsonpickle.encode(sorted(krog_lista, key=lambda x: x.distance)))
    elif form and ',' in form['stadsdel']:
        try:
            form['latitude'] = form['stadsdel'].split(',')[0]
            form['longitude'] = form['stadsdel'].split(',')[1]
            place_id = filter_search_from_previous_results(form)

            session[Helper().get_user_ip()] += place_id + ';'

            return details(place_id, form['latitude'] + ',' + form['longitude'])
        except Exception as e:
            return redirect('/error/nocando', 302)

    # Hell has frozen over
    return redirect('/error/Error', 500)


def search_google_and_broaden_if_no_results(form):
    distance = form['distance']
    search_params = ''
    search_params += 'location=' + str(form['latitude']) + ',' + str(form['longitude']) + '&'
    search_params += 'radius=' + str(distance) + '&'
    search_params += 'type=bar'
    search_response = requests.get(GOOGLE_SEARCH % (search_params, API_KEY)).json()

    if search_response['status'] == 'OK' and search_response['results']:
        return search_response
    elif search_response['status'] == 'ZERO_RESULTS': # If no results, increase distance and search again
        form['distance'] = int(form['distance']) + 500
        return search_google_and_broaden_if_no_results(form)
    else:
        raise Exception("Wrong statuscode: %s" % search_response['status'])


def get_details_response_from_google(place_id, location=None):
    print("Getting details")
    krog = None

    if place_id:
        details_response = requests.get(GOOGLE_DETAILS % (place_id, API_KEY)).json()
        print("ReSPONSE")

        reviews = []
        try:
            for review in details_response['result']['reviews']:
                if review['text']:
                    reviews.append(Review(author_name=review['author_name'], comment=review['text']))
        except Exception:
            print("No reviews.. Ignoring")

        photos = []
        try:
            for photo in details_response['result']['photos']:
                photos.append(GOOGLE_PLACES_PHOTO % (photo['photo_reference'], API_KEY))
        except Exception:
            photos.append('/static/img/bg2.jpeg')

        bar_types = details_response['result']['types']
        # Remove point_of_interest and establishment from bartypes, as they're not really that interesting..
        if 'point_of_interest' in bar_types: bar_types.remove('point_of_interest')
        if 'establishment' in bar_types: bar_types.remove('establishment')

        try:
            opening_hours = details_response['result']['opening_hours']['weekday_text']
        except Exception:
            opening_hours = []

        try:
            rating = details_response['result']['rating']
        except Exception:
            rating = "N/A"

        dist = None
        if location:
            lat=location.split(',')[0]
            lng=location.split(',')[1]

            dist = Helper.calculate_distance_between_locations(
                lat,
                lng,
                details_response['result']['geometry']['location']['lat'],
                details_response['result']['geometry']['location']['lng'])
            mode = 'transit' if 'km' in dist else 'walking'
            iframe_lank = (GOOGLE_EMBEDDED_DIRECTIONS_MAPS % (mode, lat + ',' + lng,place_id, MAPS_EMBED_KEY))
        else:
            iframe_lank = (GOOGLE_EMBEDDED_MAPS % (place_id, MAPS_EMBED_KEY))

        krog = Krog(
            namn=details_response['result']['name'],
            bar_types=bar_types,
            beskrivning=details_response['result']['name'],
            adress=details_response['result']['formatted_address'],
            oppet_tider=opening_hours,
            iframe_lank=iframe_lank,
            betyg=rating,
            reviews=reviews,
            photos=photos,
            place_id=details_response['result']['place_id'],
            distance=dist
        )
    return jsonpickle.encode(krog)


def filter_search_from_previous_results(form):
    print("SEARCH")
    search_response = search_google_and_broaden_if_no_results(form)
    # Remove earlier search results from new search to remove duplicates
    result_list_without_earlier = []
    #for result in search_response['results']:
    #    if not result['place_id'] in session[Helper().get_user_ip()].split(';'):
    #        result_list_without_earlier.append(result)

    # If there are any results after trimming duplicates, else redo with duplicates
    print("asd")
    if result_list_without_earlier:
        random_search_response = random.choice(result_list_without_earlier)
    else:
        print("Already been through all")
        random_search_response = random.choice(search_response['results'])

    return random_search_response['place_id']


def details(place_id, location):
    return get_details_response_from_google(place_id, location)


class Helper(object):
    @staticmethod
    def get_user_ip():
        # Try to get HTTP_X_REAL_IP, if not available,
        # try get environment variable REMOTE_ADDR else get variable remote_addr
        # Due to different levels of proxies.
        return request.environ.get('HTTP_X_REAL_IP', request.environ.get('REMOTE_ADDR', request.remote_addr))

    @staticmethod
    def calculate_distance_between_locations(user_lat, user_lng, bar_lat, bar_lng):
        user_loc = (float(user_lat), float(user_lng))
        bar_loc = (float(bar_lat), float(bar_lng))
        # Calculates distance between points rounded down to 1 decimal in km
        distance_in_km = "{0:.1f}".format(haversine(user_loc, bar_loc))
        # Returns e.g km if over one 1 km distance, otherwise in meters
        return distance_in_km + 'km' if float(distance_in_km) > 1 else str((float(distance_in_km) * 1000)) + 'm'



