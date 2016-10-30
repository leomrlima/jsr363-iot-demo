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

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.measure.quantity.Temperature;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jssc.SerialPort;
import jssc.SerialPortException;
import mobi.v2com.demo.post.MeasurementRecord;
import tec.uom.se.unit.Units;

/**
 * RH-USB Temperature and Relative Humidity sensor. Instances are <b>NOT</b> thread-safe, and it relies on the serial port not being used by any other application/service/class/thread...
 * 
 * <p>
 * Serial Protocol:<ul>
 * <li>PA\r: Transmit percent Relative Humidity and temperature in Degrees Fahrenheit, without units.</li>
 * <li>C\r:Transmit temperature in Deg C</li>
 * <li>F\r:Transmit temperature in Deg F.</li>
 * </ul>
 * </p>
 * @author leomrlima@gmail.com
 *
 */
@SuppressWarnings("unused")
public class USBTemperatureSensor implements TemperatureSensor {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final String id;
	private final QuantityFactory<Temperature> temperatureFactory;
	private final SerialPort port;

	private static final byte[] F_COMMAND = "F\r".getBytes();
	private static final Pattern F_RESPONSE = Pattern.compile("(\\d+\\.\\d+)\\sF\r\n>");
	
	private static final byte[] C_COMMAND = "C\r".getBytes();
	private static final Pattern C_RESPONSE = Pattern.compile("(\\d+\\.\\d+)\\sC\r\n>");
	
	/**
	 * @param id this sensor's identification string. No rule is applied here (only that it's not null) and this is used for creating MeasurementRecords 
	 * @param usbPath the serial port name/path to connect and read the sensor. Can't be null
	 */
	public USBTemperatureSensor(String id, String usbPath) {
		this.id = Objects.requireNonNull(id, "Sensor ID is null");
		
		port = new SerialPort( Objects.requireNonNull(usbPath, "Serial port path is null"));
		
		ServiceProvider provider = ServiceProvider.current();
		temperatureFactory = provider.getQuantityFactory(Temperature.class);

	}

	public MeasurementRecord<Temperature> readTemperature() {
		// TODO Really read
		MeasurementRecord<Temperature> record = new MeasurementRecord<>();
		record.sensorId = id;
		
		try {
			if (port.openPort()) {
				port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				port.writeBytes(C_COMMAND);
				String response = null;
				for (int a1 = 0; a1 < 10; a1++) {
					response = port.readString();
					logger.debug("Sensor response: {}", response);
					if (response != null) {
						break;
					}
					try {
						Thread.sleep(100); //FIXME: implement via callback!!
					} catch (Exception e) {
						//noop
					}
				}
				if (response != null) {
				Matcher responseMatcher = C_RESPONSE.matcher(response);
				if (responseMatcher.matches()) {
					record.measurement = temperatureFactory.create(Double.parseDouble(responseMatcher.group(1)), Units.CELSIUS);
					logger.debug("Parsed Reading: {}", record.measurement);
				} else {
					logger.debug("Wrong response!");
				}
				} else {
					logger.warn("Couldn't get a response from {}", port.getPortName());
				}
				
			} else {
				logger.warn("Couldn't open serial port {}", port.getPortName());
			}
		} catch (SerialPortException e) {
			logger.warn("Trouble reading serial port / USB ", port.getPortName(), e);
		} finally {
			if (port.isOpened()) {
				try {
					port.closePort();
				} catch (SerialPortException e) {
					logger.warn("Trouble closing serial port / USB {}", port.getPortName(), e);
				}
			}
		}
		record.time = ZonedDateTime.now(); //XXX: timestamp at start or end of request?
		
		return record;
	}

}
