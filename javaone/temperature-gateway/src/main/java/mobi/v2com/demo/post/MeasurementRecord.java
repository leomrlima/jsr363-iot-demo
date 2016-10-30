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
package mobi.v2com.demo.post;

import java.time.ZonedDateTime;

import javax.measure.Quantity;

/**
 * Value class (DTO) for Measurement Records.
 * 
 * Instances are NOT thread-safe. This is meant to be a very short lived object.
 * 
 * @author leomrlima@gmail.com
 *
 * @param <Q> the Quantity class for this record, like Temperature
 */
public class MeasurementRecord<Q extends Quantity<Q>> {

	public String sensorId;
	public ZonedDateTime time;
	public Quantity<Q> measurement;

	@Override
	public String toString() {
		return "MeasurementRecord [" + (sensorId != null ? "sensorId=" + sensorId + ", " : "")
				+ (time != null ? "time=" + time + ", " : "")
				+ (measurement != null ? "measurement=" + measurement : "") + "]";
	}

}
