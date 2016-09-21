from flask import Flask
from raven.contrib.flask import Sentry
import sys, os
app = Flask(__name__)
app.debug=True
if sys.version[0] == '2':
    reload(sys)
    sys.setdefaultencoding("utf-8")

app.config['SENTRY_RELEASE'] = os.environ.get('SENTRY_RELEASE', '0.0.1')
sentry = Sentry(app)
