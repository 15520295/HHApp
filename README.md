# SBike
This App for hitchhiking perpuse. Both driver and passenger can use and put thier request to server. The App will find the matching request and will notify you to inform about the trip.
----------------------
You can find most of the Class to handle the Maps API and Place API in Framework packed:
+ Direction Packed: for finding direction and return a Route object. The Route will hold all the information about the direction

+ Place Packed: for finding place name by using costumize PlaceAutoComple and PlacePicker from Google. The PlaceAutoComple can be customize to anything you want. It also have a feature that can recomend the place depends on User current location.
+ LocationUtils: hold many function to control the location like convert latlng to str or get Primary text from location or get Address from location
+ ImageUtils: for converting Bitmap or resizing it
+ TimeUtils: get time and calculate to time or compare time
+ SimpleMapActivity: parent Class for Activity that have a map fragment

SimpleMapActivity components: you can add these component to the Activity that extend SimpleMapActivity to use. Each component have to own job, here are these components:
+ Direction Manager: this is a component for the Activity hold the map fragment. Use this to draw the route on the map fragment in the Activity
+ DB Manager: get data from Firebase like user info, request info...
+ MapCameraManager: for controling the map fragment camera
+ MarkerManager: draw maker on map fragment


All main Activities are in Main Packed and models're in Model Packed. ActivitiesAuth for authentication Activities.

If you have any bug or any question please put in Issues tab. We will answer as soon as we can.
Tks for reading.
