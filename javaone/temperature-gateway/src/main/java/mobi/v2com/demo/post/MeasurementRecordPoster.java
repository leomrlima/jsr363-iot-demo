/*
Copyright 2016 Leonardo de Moura Rocha Lima (leomrlima@gmail.com) and others

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package mobi.v2com.demo.post;

import java.util.Objects;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.v2com.demo.ServerType;
import tec.uom.se.format.SimpleUnitFormat;

/**
 * POSTs MeasurementRecords to a given URL.
 * 
 * Instances are thread-safe.
 * 
 * @author leomrlima@gmail.com
 * @author werner.keil@gmail.com
 *
 */
public class MeasurementRecordPoster {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String postTarget;
    private final ServerType serverType;
    
    /**
     * Creates a new Poster to the specified target.
     * 
     * @param postTarget the POST URL
     * @param serverType the back-end server type
     */
    public MeasurementRecordPoster(String postTarget, ServerType serverType) {
	this.postTarget = Objects.requireNonNull(postTarget, "POST URL is null");
	this.serverType = serverType;
    }

    /**
     * POSTs a MeasurementRecord. A new TCP/IP connection is
     * established/destroyed with every call.
     * 
     * @param record
     *            a non-null MeasurementRecord to be posted
     * @return true if the service returns 200 or 201, false otherwise
     */
    public boolean post(MeasurementRecord<?> record) {
	logger.debug("Posting: {}", Objects.requireNonNull(record));

	// TODO: use some lib for this conversion? maybe?
	/*
	JsonObject json = (JsonObject) Json.createObjectBuilder().add("name", record.sensorId)
		// .add("time", record.time.toInstant().toEpochMilli()/1000)
		.add("value", String.valueOf(record.measurement.getValue())).build();
		*/
	String str = new StringBuilder().append("name=").append(record.sensorId).append("&").append("value=")
		.append(String.valueOf(record.measurement.getValue())).append("&").append("unit=")
		.append(SimpleUnitFormat.getInstance(SimpleUnitFormat.Flavor.ASCII)
			.format(record.measurement.getUnit()))
		.toString();

	Form form = new Form()
		.param("name", record.sensorId)
		.param("value", String.valueOf(record.measurement.getValue()))
		.param("unit", SimpleUnitFormat.getInstance(SimpleUnitFormat.Flavor.ASCII)
			.format(record.measurement.getUnit()));
		
	// logger.debug("Converted to JSON as: {}", json);
	logger.debug("Converted to Form as: {}", str);
	Client client = null; // XXX: consider creating the Client and WebTarget
			      // only once?
	try {
	    /*
	     * client =
	     * ClientBuilder.newBuilder().register(JsonProcessingFeature.class)
	     * .property(JsonGenerator.PRETTY_PRINTING, true).build(); WebTarget
	     * path = client.target(postTarget);
	     * 
	     * Response r = path.request().accept(MediaType.TEXT_PLAIN_TYPE,
	     * MediaType.APPLICATION_JSON_TYPE) .post(Entity.entity(json,
	     * MediaType.APPLICATION_JSON_TYPE.withCharset(StandardCharsets.
	     * UTF_8.name())));
	     */
	    // client = ClientBuilder.newClient();

	    // Response r = path.request().accept(MediaType.TEXT_PLAIN_TYPE)
	    // .post(Entity.entity(str, MediaType.TEXT_PLAIN));
	    client = ClientBuilder.newClient();
//	    URI uri = new URI(postTarget + str);
//	    WebTarget path = client.target(uri);
//	    Response r = Response.created(uri).build();
	    WebTarget path = client.target(postTarget);
	    
	    Response r = path.request().accept(MediaType.TEXT_PLAIN).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED), Response.class);
		    
	    logger.debug("Response Status Info: {}", r.getStatusInfo());
	    if (r.getStatus() == 200 || r.getStatus() == 201 || r.getStatus() == 204) {
		logger.debug("Returned {} response, we're clear", r.getStatus());
		return true;
	    } else {
		logger.warn("Response code is not 200/1/4, is {}", r.getStatus());
		logger.warn("More details: {}", r.getStatusInfo());
	    }
	} catch (Exception e) {
	    logger.error("Error posting", e);
	} finally {
	    if (client != null) {
		try {
		    client.close();
		} catch (Exception e) {
		    logger.warn("Error closing client", e);
		}
	    }
	}

	return false;
    }

}
