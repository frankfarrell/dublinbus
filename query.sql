SELECT 
	to_timestamp(timestamp/1000000) as time,--human readable
	timestamp, 
	ST_AsText(coordinates) as actual_coordinates, 
	stopid, 		--lastStop, not reliable
	delay, 			--reportedDelay
	vehiclejourneyid, --join on this
	atstop			--what does this mean? We only sample every 20-50 seconds, so not reliable

FROM public.busgps, 

WHERE lineid LIKE '747' AND vehiclejourneyid = '14960' ORDER BY vehiclejourneyid,time;




SELECT SUBSTRING(trip_id FROM '^[^.]*') as trip_id, --join on this
	GREATEST(arrival_time, departure_time),  --Convert this to unix epoch time
	ST_AsText(ST_MakePoint(stop_lon, stop_lat)) as expected_coordinates, 
	stop_sequence, 
	SUBSTRING(public.stop_times.stop_id FROM '[1-9]{0,1}.{3}$'),--Skim off the zero at the end, this is the format from GPS file
	stop_name,
       shape_dist_traveled, 
       timepoint
  FROM public.stop_times, public.stops
  WHERE 
	public.stop_times.stop_id = public.stops.stop_id
  AND trip_id LIKE '14960.1708.0-747-b12-1.467.I'
  ORDER BY departure_time;
