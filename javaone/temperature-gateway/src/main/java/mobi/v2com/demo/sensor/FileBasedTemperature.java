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

import javax.measure.Quantity;
import javax.measure.quantity.Temperature;

import tec.uom.se.unit.Units;

/**
 * Implements a Temperature Sensor that gets its value from a file.
 * The file is expected to contain a single value with the temperature as mC
 * 
 * @author leomrlima@gmail.com
 *
 */
public class FileBasedTemperature extends ProcessBasedTemperature {

	private final String filePath;
	
	public FileBasedTemperature(String id, String file) {
		super(id);
		this.filePath = file;
	}

	@Override
	protected String[] getProcessLine() {
		return new String[] {
				"cat", filePath
		};
	}

	@Override
	protected Quantity<Temperature> parseResponse(String response) {
		return temperatureFactory.create(Integer.parseInt(response)/(double)1000, Units.CELSIUS);
	}

}
