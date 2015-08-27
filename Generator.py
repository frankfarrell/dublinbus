import csv
import time
import sys
from itertools import islice
import datetime
import json
import socket  

#except socket.gaierror:
  #  print 'Hostname could not be resolved. Exiting'
  #  sys.exit()

def getCurrentTime(startTimeOffset=0):
    now = time.time()
    now = int(now*1000000)
    now = now - startTimeOffset
    print "Now is, " , datetime.datetime.fromtimestamp(now/1000000).strftime('%Y-%m-%d %H:%M:%S')
    return now

def sendToServer(data):
    print "sending", data
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(("localhost" , 5001))
    n = s.send(data+"\n")
    s.close()
	
def sendRowToServer(row):
    asMap = {
        "timestamp": int(row[0]),
        "lineId": row[1],
        "journeyPatternId":row[3],
		"direction": row[2],
        "vehicleJourneyId":row[5],
		"timeFrame": row[4],
        "congestion":True if int(row[7]) == 1 else False,
        "coordinates" : "POINT (" + row[8] +" " +row[9] + ")",
        "delay" : float(row[10]),
        "blockId" :row[11],
        "vehicleId" :row[12],
        "stopId" :row[13],
        "atStop" : True if int(row[14]) == 1 else False,	
	}
    sendToServer(json.dumps(asMap))

lineNumber =  int(sys.argv[2])

with open(sys.argv[1], "rb") as csvfile:
    datareader = csv.reader(csvfile) 
    sliceRows = islice(datareader, lineNumber, None)
    firstRow = sliceRows.next()
    startTimeOffset = int(time.time()*1000000) - int(firstRow[0])
    print "First row", int(firstRow[0])
    print "Offset", int(startTimeOffset)
    print firstRow
    for row in sliceRows:
        rowTime =  int(row[0])
        while rowTime > getCurrentTime(startTimeOffset):
            time.sleep(1)
        sendRowToServer(row)
		
		

# Read row specified in arg and print timestamp here. This is the start of time. 
# Start a clock form this time. Get current time and difference. Set up a function that works off this. 
# Read each row, if row time is equal or less than current time, post it off to our server. 