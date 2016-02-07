package serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

public class JsonToPointDeserializer extends JsonDeserializer<Point> {

    private final static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 26910); 

    @Override
    public Point deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        try {
            String text = jp.getText();
            if(text == null || text.length() <= 0)
                return null;

            String[] coordinates = text.replaceFirst("POINT ?\\(", "").replaceFirst("\\)", "").split(" ");
            double lat = Double.parseDouble(coordinates[0]);
            double lon = Double.parseDouble(coordinates[1]);

            Point point = geometryFactory.createPoint(new Coordinate(lat, lon));
            return point;
        }
        catch(Exception e){
            return null;
        }
    }

}