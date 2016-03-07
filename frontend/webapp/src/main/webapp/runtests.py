import unittest

if __name__ == '__main__':
	suite = unittest.TestLoader().discover('.', pattern = "*Test.py")
	unittest.TextTestRunner(verbosity=2).run(suite)