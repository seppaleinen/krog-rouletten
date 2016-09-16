from flask import Flask
from raven.contrib.flask import Sentry
import sys
app = Flask(__name__)
app.debug=True
if sys.version[0] == '2':
    reload(sys)
    sys.setdefaultencoding("utf-8")

sentry = Sentry(app, dsn='http://04fbd07088774c5886ced92278f4fb70:a1a0095f937b4f01968137775738a707@192.168.99.100:9000/2')
