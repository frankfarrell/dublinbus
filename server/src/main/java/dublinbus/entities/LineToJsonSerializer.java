package dublinbus.entities;

import java.io.IOException;

import org.geotools.geojson.geom.GeometryJSON;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTWriter;

public class LineToJsonSerializer extends JsonSerializer<LineString> {

	@Override
	public void serialize(LineString value, JsonGenerator gen,
			SerializerProvider serializers) throws IOException,
			JsonProcessingException {
	
			String jsonValue = "null";
	        try
	        {
	            if(value != null) {     
	            	 GeometryJSON g = new GeometryJSON();
	                
	            	 //This gives escaped geojson, FIX
	                jsonValue =g.toString(value);
	            }
	        }
	        catch(Exception e) {}

	        gen.writeString(jsonValue);
	}

}
