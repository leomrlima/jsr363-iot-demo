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

package mobi.v2com.demo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mobi.v2com.demo.post.MeasurementRecordPoster;
import mobi.v2com.demo.sensor.FileBasedTemperature;

/**
 * Reads the internal temperature of Intel Edison processors and posts it every
 * 10 seconds.
 * 
 * Usage: $JAVA_HOME/bin/java -cp /home/edison/temperature-gateway-1.0.jar mobi.v2com.demo.MainEdison http://192.168.1.100:8080/temperature/resource/api/v1/sensors SPARK thermal_zone1
 * 
 * @author leomrlima@gmail.com
 * @author werner.keil@gmail.com
 *
 */
public class MainEdison {

    public static void main(String... args) {
	String url = args[0];
	ServerType serverType = ServerType.valueOf(args[1].toUpperCase());

	// Create server link
	MeasurementRecordPoster poster = new MeasurementRecordPoster(url, serverType);
	ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

	for (int a1 = 2; a1 < args.length; a1++) {
	    // Create temperature sensor
	    String id = args[a1];
	    FileBasedTemperature sensor = new FileBasedTemperature(id, "/sys/class/thermal/" + id + "/temp");
	    Runnable runner = new ReaderPoster(sensor, poster);
	    executorService.scheduleAtFixedRate(runner, 0, 10, TimeUnit.SECONDS);
	}
    }
}
