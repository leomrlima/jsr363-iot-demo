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

import javax.measure.quantity.Temperature;

import mobi.v2com.demo.post.MeasurementRecord;

/**
 * Generic interface for a Thermometer.
 * 
 * @author llima@v2com.mobi
 *
 */
public interface TemperatureSensor {

	/**
	 * Reads the current temperature from this sensor.
	 * 
	 * @return the measurement record, never null. The measurement attribute on
	 *         MeasurementRecord should be null if and only if there was an
	 *         error reading the device.
	 */
	MeasurementRecord<Temperature> readTemperature();
}
