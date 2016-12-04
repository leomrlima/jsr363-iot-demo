Sensor Web Temperature Gateway
=============
Reads the internal temperature of Intel Edison processors and posts it every 10 seconds.
 
Usage: 
 * For server type **DIANA** run<br><code>$JAVA_HOME/bin/java -cp temperature-gateway-1.0.1.jar mobi.v2com.demo.MainEdison http://192.168.1.100:8080/temperature/resource/api/v1/sensors diana thermal_zone1</code></li>
 * For server typ **SPARK** run<br><code>$JAVA_HOME/bin/java -cp temperature-gateway-1.0.1.jar mobi.v2com.demo.MainEdison http://192.168.1.100:4567/sensors spark thermal_zone1</code>