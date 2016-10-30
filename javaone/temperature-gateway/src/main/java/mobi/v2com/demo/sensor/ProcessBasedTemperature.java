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
package mobi.v2com.demo.sensor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.measure.Quantity;
import javax.measure.quantity.Temperature;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;

import org.slf4j.LoggerFactory;

import mobi.v2com.demo.post.MeasurementRecord;

/**
 * Base class for TemperatureSensors that need to start a process to get the temperature value. 
 * 
 * @author leomrlima@gmail.com
 *
 */
public abstract class ProcessBasedTemperature implements TemperatureSensor {

	protected abstract String[] getProcessLine();
	
	protected abstract Quantity<Temperature> parseResponse(String response);
	
	protected final QuantityFactory<Temperature> temperatureFactory;
	
	private final String id;
	
	public ProcessBasedTemperature(String id) {
		ServiceProvider provider = ServiceProvider.current();
		temperatureFactory = provider.getQuantityFactory(Temperature.class);
		this.id = id;
	}
	
	@Override
	public MeasurementRecord<Temperature> readTemperature() {
		//read from a process
		MeasurementRecord<Temperature> response = new MeasurementRecord<>();
		response.sensorId = id;
		response.time = ZonedDateTime.now();
		try {
			Process p = new ProcessBuilder(getProcessLine())
					.redirectErrorStream(true).start();
			boolean finishedOk = p.waitFor(10, TimeUnit.SECONDS);
			if (finishedOk) {
				try (BufferedReader buffer = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
					response.measurement = parseResponse(buffer.lines().collect(Collectors.joining("\n")));
		        }
			} else {
				LoggerFactory.getLogger(getClass()).warn("Error reading temperature, didn't finish ok");
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).warn("Error reading temperature", e);
		}
		return response;
	}

}
