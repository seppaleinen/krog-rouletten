from wtforms import Form, TextField, validators, HiddenField



class ManualForm(Form):
    id = HiddenField('id')
    location = HiddenField('location')
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
