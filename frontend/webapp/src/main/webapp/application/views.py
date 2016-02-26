from application import app
from flask import render_template
import random

BACKEND = 'http://localhost:10080'

class Objekt(object):
    def __init__(self):
        self.namn = None
        self.adress = None
        self.karta = None
        self.beskrivning = None

@app.route("/")
def home():
    kellys = Objekt()
    kellys.namn = "Kellys"
    kellys.adress = "Folkungagatan 49, 116 22 Stockholm"
    kellys.karta = "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.1076553101873!2d18.072471615934845!3d59.314459281653626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fa6e38fd49%3A0x76f561b83359a005!2sKellys!5e0!3m2!1sen!2sse!4v1456504153773"
    kellys.beskrivning = "Rockbar"

    arken = Objekt()
    arken.namn = "Gota ark"
    arken.adress = "Medborgarplatsen 25, 118 72 Stockholm"
    arken.karta = "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2036.0389246761372!2d18.069153315934884!3d59.315606981654064!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x465f77fabedd14f1%3A0x5f2231305d9933ed!2zR8O2dGEgQXJr!5e0!3m2!1sen!2sse!4v1456513979498"
    arken.beskrivning = "Sunkhak"

    list = [kellys, arken]


    objekt = random.choice(list)
    return render_template('index.html',
                           namn=objekt.namn,
                           adress=objekt.adress,
                           karta=objekt.karta,
                           beskrivning=objekt.beskrivning
                           )
