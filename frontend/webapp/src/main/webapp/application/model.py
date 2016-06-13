from wtforms import Form, TextField, validators, HiddenField, SelectField, RadioField


class SearchForm(Form):
    latitude = HiddenField('latitude', [validators.required()])
    longitude = HiddenField('longitude', [validators.required()])
    distance = SelectField("Test: ", [validators.required()], choices=[
        (0.1, "100m"),
        (0.2, "200m"),
        (0.3, "300m"),
        (0.5, "500m"),
        (1, "1km"),
        (3, "3km"),
        (5, "5km"),
        (8, "8km")],
        default=8)
    search_type = RadioField('Label', choices=[('value','description'),('value_two','whatever')])
    adress = TextField('adress')
    stadsdel = RadioField('stadsdel')
    bar_typ = RadioField('bar_typ')
    oppet_tider = RadioField('oppet_tider')


class ManualForm(Form):
    id = HiddenField('id')
    namn = TextField('Namn', [validators.Required()])
    adress = TextField('Adress')
    oppetTider = TextField('Oppettider')
    barTyp = TextField('Bartyp')
    stadsdel = TextField('Stadsdel')
    beskrivning = TextField('Beskrivning')
    betyg = TextField('Betyg')
    hemside_lank = TextField('Hemside Lank')
    intrade = TextField('Intrade')
    iframe_lank = TextField('Iframe lank', [validators.Required()])


class Objekt(object):
    def __init__(self):
        self.namn = None
        self.adress = None
        self.karta = None
        self.beskrivning = None
