# coding=UTF-8
from wtforms import Form, TextField, validators, HiddenField, SelectField, RadioField, BooleanField


class SearchForm(Form):
    latitude = HiddenField('latitude', [validators.required()])
    longitude = HiddenField('longitude', [validators.required()])
    distance = SelectField("Test: ", [validators.required()], choices=[
        (100, "100m"),
        (200, "200m"),
        (300, "300m"),
        (500, "500m"),
        (1000, "1 km"),
        (3000, "3 km"),
        (5000, "5 km"),
        (8000, "8 km")],
        default=500)
    gps = RadioField('gps', choices=[('value','description')])
    adress = TextField('adress')
    stadsdel = RadioField('stadsdel')
    bar_typ = RadioField('bar_typ')
    oppet_tider = RadioField('oppet_tider')


class AdminKrogForm(Form):
    id = HiddenField('id')
    namn = TextField('Namn', [validators.Required()])
    adress = TextField('Adress')
    oppetTider = TextField('Oppettider')
    barTyp = TextField('Bartyp')
    stadsdel = TextField('Stadsdel')
    beskrivning = TextField('Beskrivning')
    betyg = TextField('Betyg')
    hemside_lank = TextField('Hemsida')
    intrade = TextField('Intrade')
    iframe_lank = TextField('Iframe lank', [validators.Required()])
    approved = BooleanField('Approved')


class UserKrogForm(Form):
    namn = TextField('Krog namn', [validators.Required()])
    adress = TextField('Adress eller plats', [validators.Required()])
    beskrivning = TextField('Kommentar (frivilligt)')
    approved = BooleanField('Approved')


class Krog(object):
    def __init__(self, namn, bar_types, beskrivning, adress, oppet_tider, iframe_lank, betyg, reviews, photos):
        self.namn = namn
        self.bar_types = bar_types
        self.beskrivning = beskrivning
        self.adress = adress
        self.oppet_tider = oppet_tider
        self.iframe_lank = iframe_lank
        self.betyg = betyg
        self.reviews = reviews
        self.photos = photos


class Review(object):
    def __init__(self, author_name, comment):
        self.author_name = author_name
        self.comment = comment
