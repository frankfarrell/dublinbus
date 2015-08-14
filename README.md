# dublinbus
Realtime analysis of Dublin Bus Data

Generator.py simulates real gps data feed in python script using real data. 

Server uses Spring, Spring-reactor and Hibernate spatial to persists data in PostGis db dublinbus. 
Build with: 
gradle wrapper
./gradlew build

Sample.csv is a subset of the data for testing. 

Plan: 
Esper engine for analysing timeliness of buses compared to timetable in GTFS format. Alerts if there are patterns. 
Browser to scrub data on date/time/coordinates and maybe area dimensions. 
 1) Map with realtime positions: User can draw a polygon to do a subselection. Maybe a heatmap here too?
 2) Bar chart with historical lateness, cn scrub?
User can select a single bus/group of buses and view graph compaing expected and actual times and some history. 
 
Using Postgis queries. 
Websocket/socketio connecting to spring. 