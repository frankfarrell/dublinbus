SELECT
        (ST_Dump(route_shapes.st_makeline)).geom AS geometries
	FROM  route_shapes
	WHERE '1' LIKE route_shapes.trip_id;

--SELECT ST_LineLocatePoint(objects, public.stops.geom) --As a ratio of difference of timestamps associated with start and end
		--((SELECT ST_EndPoint(objects)
--		FROM public.stop_times, public.stops, objects
--		WHERE public.stop_times.trip_id LIKE '1' AND public.stop_times.stop_id= public.stops.stop_id;



--	(SELECT ST_Distance(coordinates, geom.geometries)
	----							FROM  public.route_shapes, (SELECT ST_Dump(st_makeline) AS geometries
	--											FROM  route_shapes
	--											WHERE '1' LIKE route_shapes.trip_id) as geom
	--							WHERE '1' LIKE route_shapes.trip_id	
	--							ORDER BY ST_Distance(St_GeomFromText('POINT(-6.233289 53.463306)', 4326 ), objects.geometries.geom)
	--							LIMIT 1) 
	--						    as distance_table
