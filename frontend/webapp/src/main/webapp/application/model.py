# coding=UTF-8
from wtforms import Form, validators, HiddenField, SelectField, RadioField


class SearchForm(Form):
    latitude = HiddenField('latitude', [validators.DataRequired()])
    longitude = HiddenField('longitude', [validators.DataRequired()])
    hidden_distance = HiddenField('distance')
    distance = SelectField("Distance: ", [validators.DataRequired()], choices=[
        (100, "100m"),
        (200, "200m"),
        (300, "300m"),
        (500, "500m"),
        (1000, "1 km"),
        (3000, "3 km"),
        (5000, "5 km"),
        (8000, "8 km")],
        default=500)
    bar_typ = RadioField('bar_typ', choices=[
        ('bar',         'Bar'),
        ('restaurant',  'Restaurant'),
        ('night_club',  'Nattklubb')
    ])
    searchtype = HiddenField('searchtype')


class Krog(object):
    def __init__(self, namn, bar_types, beskrivning, adress, oppet_tider, iframe_lank, betyg, reviews, photos, place_id='', distance=0.0):
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
