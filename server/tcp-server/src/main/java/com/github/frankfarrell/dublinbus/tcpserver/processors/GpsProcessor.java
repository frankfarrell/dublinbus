package com.github.frankfarrell.dublinbus.tcpserver.processors;


import com.github.frankfarrell.dublinbus.tcpserver.Constants;
import com.github.frankfarrell.dublinbus.tcpserver.model.GpsPoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GpsProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(GpsProcessor.class);

    @Override
    public void process(final Exchange exchange) throws Exception {
        final String gpsFrame = exchange.getIn().getMandatoryBody(String.class);

        log.info("Record received {}", gpsFrame);
        /*
        TODO Map the csv line to GPS object that has data we need
        Should we calculate any derived attributes here?
        Geohash, direction etc?
         */

        final GpsPoint gpsPoint = new GpsPoint();

        exchange.getOut().setHeader(Constants.BUS_ROUTE_ID, "46a");
        exchange.getOut().setHeader(Constants.GPS_DATA, gpsFrame);
    }
}
