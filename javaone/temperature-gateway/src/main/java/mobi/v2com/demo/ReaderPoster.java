/*
Copyright 2016 Leonardo de Moura Rocha Lima (leomrlima@gmail.com)

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
package mobi.v2com.demo;

import java.util.Objects;

import javax.measure.quantity.Temperature;

import org.slf4j.LoggerFactory;

import mobi.v2com.demo.post.MeasurementRecord;
import mobi.v2com.demo.post.MeasurementRecordPoster;
import mobi.v2com.demo.sensor.TemperatureSensor;

/**
 * When run, reads and posts temperature from a sensor
 * 
 * @author leomrlima@gmail.com
 *
 */
public class ReaderPoster implements Runnable {

	private TemperatureSensor sensor;
	private MeasurementRecordPoster poster;
	
	public ReaderPoster(TemperatureSensor sensor, MeasurementRecordPoster poster) {
		this.sensor = Objects.requireNonNull(sensor);
		this.poster = Objects.requireNonNull(poster);
	}

	public void run() {
		LoggerFactory.getLogger(ReaderPoster.class).debug("Read...");
		MeasurementRecord<Temperature> record = null;
		synchronized (sensor) {
			record = sensor.readTemperature();
		}

		LoggerFactory.getLogger(ReaderPoster.class).debug("Post {}...", record);

		boolean result = false;
		if (record != null && record.measurement != null) {
			synchronized (poster) {
				result = poster.post(record);	
			}
		}
		LoggerFactory.getLogger(ReaderPoster.class).debug("Final result {}!", result);
	}

}
