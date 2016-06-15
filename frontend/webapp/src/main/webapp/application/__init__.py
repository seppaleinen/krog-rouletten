from flask import Flask
import sys
app = Flask(__name__)
app.debug=True
if sys.version[0] == '2':
    reload(sys)
    sys.setdefaultencoding("utf-8")
