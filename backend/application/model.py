# coding=UTF-8


class Krog(object):
    def __init__(self, namn, bar_types, beskrivning, adress, oppet_tider,
                 iframe_lank, betyg, reviews, photos, place_id='', distance=0.0):
        self.namn = namn
        self.bar_types = bar_types
        self.beskrivning = beskrivning
        self.adress = adress
        self.oppet_tider = oppet_tider
        self.iframe_lank = iframe_lank
        self.betyg = betyg
        self.reviews = reviews
        self.photos = photos
        self.place_id = place_id
        self.distance = distance


class Review(object):
    def __init__(self, author_name, comment):
        self.author_name = author_name
        self.comment = comment
