# dublinbus

Realtime analysis of Dublin Bus Data

This is an attempt to build a realtime bus tracking and analysis platform.

## Background

* Postgres/PostGIS Installed and Running 
* Redis Running on default port
* Download the DublinBus data set and extract archives from http://dublinked.com/datastore/datasets/dataset-304.php (Not necessary for demo)
* Java 8
* Python

## To Run

1. Extract dublinbusdb.7z with 7zip, this contains a dump of db with tables, views, functions and some data. 
Import into postgres with psql: http://www.postgresql.org/docs/9.1/static/backup-dump.html
Eg "psql dublinbus < dublinbusdb.sql"

2. Build the server project
cd server
gradle build

3. This gives a Spring Boot Jar with a simple main. 
To run gradle bootRun

This should bind: 
Reactor TcpServer on port 5001: 
Rest interface to port 8080
SocketIO interface on port 9002

4. To Simulate a GPS Feed, we can run the Generator.py script. 
This takes 2 args: datasource csv file location and line number to start from in this csv file. 
For testing purposes (to save on the 1GB Full Dataset) run: 
"python Generator.py Sample.csv 0"

5. To see if data is being processed, run redis-cli.exe
Command: "SUBSCRIBE busdelayed.*"

This should show a feed of calculations. Pick a line value after busdelayed.<thisValue>

6. If you navigate your browser to localhost:8080?tripId=[thisValue] you will see a mpa with the route and the buses current position/delay. 
(The client is a work in progress!!!)

## Architecture

![Diagram](https://github.com/frankfarrell/dublinbus/blob/master/DublinBusPipeline.png)

* The endpoint for these connections is a Spring Reactor TcpServer. 

* This persists the data in PostGIS using Hibernate Spatial and Spring Data Repositories

* Postgres with the PostGIS extension is used as a datastore.

* The Gps Data is sent to Realtime Processor. This is a simple Spring Service wrapped in a Hystrix Command. 

* REST interfaces are exposed for expected and actual routes including geojson. 

* Redis is used asa cache and also as a publish subscribe broker.
 
* Browsers clients connect via SocketIO and subscribe for events for a given TripID

## Background

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


## Plan: 

Browser to scrub data on date/time/coordinates and maybe area dimensions. 
 1) Map with realtime positions: User can draw a polygon to do a subselection. Maybe a heatmap here too?
 2) Bar chart with historical lateness, cn scrub?
User can select a single bus/group of buses and view graph compaing expected and actual times and some history. 

Alerts based on bus bunching, etc