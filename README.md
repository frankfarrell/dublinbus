# dublinbus
Realtime analysis of Dublin Bus Data

This is an attempt to build a realtime bus tracking and analysis platform. 

It simulates a real feed of gps points using this data: http://dublinked.com/datastore/datasets/dataset-304.php
To run for a given day, run python Generator.py <file location> <starting line number>

The endpoint for these connections is a Spring Reactor TcpServer. 
This persists the data in PostGIS using Hbernate Spatial.

Postgres with the PostGIS extension is used as a datastore. To reconstruct the database, extract dublinbusdb.7z and restore with 'psql dublinbus < dublinbusdb.sql'.

The expected bus routes are loaded into PostGIS from GTFS files published by DublinBus. 
http://dublinked.com/datastore/datasets/dataset-254.php
Install gtfsdb: 
https://github.com/OpenTransitTools/gtfsdb
Load the files with: 
..\gtfsdb>bin\gtfsdb-load --database_url postgresql://postgres@localhost:9995/dublinbus ..\google_transit_dublinbus_P20130315-1546.zip

As gps data points arrive they are compared with the expected bus route. 
There are two possible scenarios: 
1) Clean data: The gps data point vehicle journey id exists in expected routes and the timestamp is roughly sane. 
2) Dirty data: The expected journey is calculated as the closest in time to data point for the route number. 

In both cases the data point is compared with expected and results are pushed via Redis pubsub and SocketIO to leaflet based clients. (Which need a bit of work)

To build the server run: 
gradle wrapper
./gradlew build

REST interfaces are exposed for expected and actual routes including geojson. 

Plan: 
Browser to scrub data on date/time/coordinates and maybe area dimensions. 
 1) Map with realtime positions: User can draw a polygon to do a subselection. Maybe a heatmap here too?
 2) Bar chart with historical lateness, cn scrub?
User can select a single bus/group of buses and view graph compaing expected and actual times and some history. 

Alerts based on bus bunching, etc