from flask import Flask
from raven.contrib.flask import Sentry
import sys
app = Flask(__name__)
app.debug=True
if sys.version[0] == '2':
    reload(sys)
    sys.setdefaultencoding("utf-8")

sentry = Sentry(app, dsn='https://5f31608fe1c24d2cbbf384e412c0e8c3:77adde0716644851b8d963af9d8b753e@sentry.io/98425')
