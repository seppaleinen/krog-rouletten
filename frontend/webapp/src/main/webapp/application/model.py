from wtforms import Form, TextField, validators


class ManualForm(Form):
    namn = TextField('Namn', [validators.Required()])
    adress = TextField('Adress', [validators.Required()])
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
