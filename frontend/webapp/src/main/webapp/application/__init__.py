from flask import Flask
from raven.contrib.flask import Sentry
import sys
app = Flask(__name__)
app.debug=True
if sys.version[0] == '2':
    reload(sys)
    sys.setdefaultencoding("utf-8")

sentry = Sentry(app, dsn='http://d8cca3633d034a2e819e3fd21c40e018:b617d55e41af4735a80a11ef15b4a916@192.168.99.100:9000/2')
