# Ideas and planning

Commands
```
# Search nearby
curl 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=59.3338483,18.07461&radius=10000&type=bar&rankby=prominence&key=API_KEY'

# Get details
curl 'https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJu-dN5ludX0YR5bHEIi3av_s&key=API_KEY'
```

150 000 request limit per 24 hr


### Search
request:
* location=lat,lng
* radius=meters
* type=bar,restaurant,night_club
* rankby=prominence,distance
* opennow
* minprice,maxprice=0(affordable),4(expensive)

response:
* name
* place_id
* rating
* vicinity
* status


### Details

request:
* placeid

response:
* address_components.sublocality=stadsdel(norrmalm, södermalm...)
* formatted_address
* photos.photo_reference
* photos.height
* photos.width
* rating
* website