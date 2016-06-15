from flask import Flask
import sys
app = Flask(__name__)
app.debug=True
reload(sys)
sys.setdefaultencoding('utf-8')
