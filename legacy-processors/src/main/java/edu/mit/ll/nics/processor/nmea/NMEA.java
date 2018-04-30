/**
 * Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.mit.ll.nics.processor.nmea;

/**
 *
 * @author roet
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

class NmeaProcException extends Exception {
    private static final Logger log = Logger.getLogger(Util.class);
    private String _info;
    
    NmeaProcException(String info) {
        _info = info;
    }

    @Override
    public String toString() {
        return _info;
    }
};

public class NMEA {

    /** Properties */
    private static Properties properties;
    private static String mappingProperties;

    private static boolean initProps() {
        try {
            properties = new Properties();
            properties.load(new FileReader(mappingProperties));


            for (Entry<Object, Object> entry : properties.entrySet()) {
            }
        } catch (FileNotFoundException fnfe) {
            properties = null;
            return false;
        } catch (Exception e) {       
            properties = null;
            return false;
        }
        return true;
    }

    // java interfaces
    interface SentenceParser {

        public boolean parse(String[] tokens, GPSPosition position);
    }

    // utils
    static float Latitude2Decimal(String lat, String NS) {
        float med = Float.parseFloat(lat.substring(2)) / 60.0f;
        med += Float.parseFloat(lat.substring(0, 2));
        if (NS.startsWith("S")) {
            med = -med;
        }
        return med;
    }

    static float Longitude2Decimal(String lon, String WE) {
        float med = Float.parseFloat(lon.substring(3)) / 60.0f;
        med += Float.parseFloat(lon.substring(0, 3));
        if (WE.startsWith("W")) {
            med = -med;
        }
        return med;
    }

    // parsers 
    class DEVID implements SentenceParser {
        // TODO: add id mapping transform logic

        @Override
        public boolean parse(String[] tokens, GPSPosition position) {
            position.deviceId = tokens[1];

            // check for mapping from deviceId to a common name
            String mappedId = getMappedId(position.deviceId);
            if (mappedId != null) {
                position.id = mappedId;
            }

            return true;
        }
    }

    class FRLIN implements SentenceParser {
        // TODO: add id mapping transform logic

        @Override
        public boolean parse(String[] tokens, GPSPosition position) {
            position.id = tokens[2];
            // check for mapping from specified ID  to a common name
            String mappedId = getMappedId(position.id);
            if (mappedId != null) {
                position.id = mappedId;
            }

            return true;
        }
    }

    class GPGGA implements SentenceParser {

        @Override
        public boolean parse(String[] tokens, GPSPosition position) {
            if (position.time == null && tokens[1] != null) {
                position.time = validDateOrTime(tokens[1]);
            }
            
            if (Float.isNaN(position.lat) && !(tokens[2].isEmpty()) && !(tokens[3].isEmpty())) {
                position.lat = validLat(tokens[2], tokens[3]);
            }
            if (Float.isNaN(position.lon) && tokens[4] != null && tokens[5] != null) {
                position.lon = validLon(tokens[4], tokens[5]);
            }
            if (Float.isNaN(position.alt) && tokens[9] != null) {
                position.alt = Float.parseFloat(tokens[9]);
            }
//            position.quality = Integer.parseInt(tokens[6]);

            return true;
        }
    }

    class GPGLL implements SentenceParser {

        @Override
        public boolean parse(String[] tokens, GPSPosition position) {
            if (position.lat == Float.NaN && tokens[1] == null && tokens[2] == null) {
                position.lat = validLat(tokens[1], tokens[2]);
            }
            if (position.lon == Float.NaN && tokens[3] == null && tokens[4] == null) {
                position.lon = validLon(tokens[3], tokens[4]);
            }
            if (position.time == null && tokens[5] != null) {
                position.time = validDateOrTime(tokens[5]);
            }
            return true;
        }
    }

    // strategy is to not overwrite an existing element
    class GPRMC implements SentenceParser {

        @Override
        public boolean parse(String[] tokens, GPSPosition position) {
            if (position.date == null && tokens[9] != null) {
                position.date = validDateOrTime(tokens[9]);
            }

            if (position.time == null && tokens[1] != null) {
                position.time = validDateOrTime(tokens[1]);
            }

            if (Float.isNaN(position.lat) && tokens[3] != null && tokens[4] != null) {
                position.lat = validLat(tokens[3], tokens[4]);
            }

            if (Float.isNaN(position.lon) && tokens[5] != null && tokens[6] != null) {
                position.lon = validLon(tokens[5], tokens[6]);
            }

            if (Float.isNaN(position.velocity) && tokens[7] != null) {
                position.velocity = Float.parseFloat(tokens[7]);
            }
            if (Float.isNaN(position.heading) && tokens[8] != null) {
                position.heading = validHeading(tokens[8]);
            }

            return true;
        }
    }

    class GPVTG implements SentenceParser {

        @Override
        public boolean parse(String[] tokens, GPSPosition position) {
            return true;
        }
    }

    class GPRMZ implements SentenceParser {

        @Override
        public boolean parse(String[] tokens, GPSPosition position) {
            if (Float.isNaN(position.altitude) & tokens[1] != null){
                position.altitude = Float.parseFloat(tokens[1]);
            }
            return true;
        }
    }

    // simple check for possibly valid date or time
    // return nulls if invalid, for later action
    private String validDateOrTime(String time) {
        if (time.matches("\\d\\d\\d\\d\\d\\d.*")) {
            return time;
        } else {
            return null;
        }
    }

    // check for valid latitude
    // return NaN if invalid, for later action
    private float validLat(String sLat, String sNS) {
        float val = Latitude2Decimal(sLat, sNS);
        final float minVal = -90, maxVal = +180;
        if (val >= minVal && val <= maxVal && sNS.matches("[NS]")) {
            return val;
        } else {
            return Float.NaN;
        }
    }

    // check for valid latitude
    // return NaN if invalid, for later action
    private float validLon(String sLon, String sEW) {
        float val = Longitude2Decimal(sLon, sEW);
        final float minVal = -180, maxVal = +180;
        if (val >= minVal && val <= maxVal && sEW.matches("[EW]")) {
            return val;
        } else {
            return Float.NaN;
        }
    }
    
    // check for valid heading value
    // return NaN if invalid
    private float validHeading(String sHeading) {
        float val = Float.valueOf(sHeading);
        final float minVal = 0, maxVal = 360;
        if (val >= minVal && val <= maxVal) {
            return val;
        } else {
            return Float.NaN;
        }
    }

    /**
     *  Default attributes for GPSPosition container class
     */
    public class GPSPosition {

        public String time = null;
        public String date = null;
        public float lat = Float.NaN;
        public float lon = Float.NaN;
        public float alt = Float.NaN;
        public boolean fixed = false;
        public int quality = 0;
        public float heading = Float.NaN;
        public float altitude = Float.NaN;
        public float velocity = Float.NaN;
        public String deviceId = null;
        public String id = null;

        // Get String representations for all
        String timeStr() {
            return time;
        }

        String dateStr() {
            return date;
        }

        String idStr() {
            return id;
        }

        String deviceIdStr() {
            return deviceId;
        }

        String latStr() {
            return Float.toString(lat);
        }

        String lonStr() {
            return Float.toString(lon);
        }

        String altStr() {
            return Float.toString(alt);
        }

        String headingStr() {
            return Float.toString(heading);
        }

        String altitudeStr() {
            return Float.toString(altitude);
        }

        String velocityStr() {
            return Float.toString(velocity);
        }

        String fixedStr() {
            return Boolean.toString(fixed);
        }

        String qualityStr() {
            return Integer.toString(quality);
        }

        public void updatefix() {
            fixed = quality > 0;
        }

        @Override
        public String toString() {
            return String.format("GPS Data: time: %s, date:%s, lat: %s, lon: %s, Q: %d, heading: %s, alt: %s, vel: %s heading: %s", timeStr(), dateStr(), latStr(), lonStr(), quality, headingStr(), altitudeStr(), velocityStr(), headingStr());
        }
    }

    int checkSum(String data) {
        int ret = 0;
        for (int i = 0; i < data.length(); i++) {
            ret ^= data.charAt(i);
        }
        return ret;
    }

    String formatCheckSum(int checksum) {
        String digits = "0123456789ABCDEF";
        int hi = checksum / 16;
        int lo = checksum % 16;
        return "" + digits.charAt(hi) + digits.charAt(lo);
    }

    boolean validChecksum(String sentence) throws NmeaProcException {

        if (!sentence.startsWith("$")) {
            throw new NmeaProcException("Starting dollar ($) sign missing");
        }
        sentence = sentence.substring(1);
        int asterisk = sentence.lastIndexOf('*');
        if (asterisk != -1) {
            if (asterisk >= sentence.length() - 3) {
                String checksumStr = sentence.substring(asterisk + 1);
                int provchsum = Integer.parseInt(checksumStr, 16);
                sentence = sentence.substring(0, asterisk);
                int countchsum = this.checkSum(sentence);
                if (provchsum != countchsum) {
                    throw new NmeaProcException("Wrong checksum (" + countchsum + " != " + provchsum + ")");
                }
            }
        }

        // compute integer body checksum value
        int csValue = 0;
        for (int i = 1; i < sentence.length(); i++) // over body characters
        {
            csValue ^= sentence.charAt(i);
        }


        // make sure only two characters!
        String csBody = Integer.toHexString(csValue).toUpperCase();
        while (csBody.length() < 2) {
            csBody = "0" + csBody;
        }

        // get the code from the sentence
        String csCode = sentence.substring(sentence.length() - 2, sentence.length());

        // return true if the computed body checksum matches the one from the sender
        return csBody.matches(csCode);
    }
    GPSPosition position = new GPSPosition();
    private static final Map<String, SentenceParser> sentenceParsers = new HashMap<String, SentenceParser>();

    public NMEA() {

        initProps();

        // DEVID: contains a GPS deviceId
        sentenceParsers.put("DEVID", new DEVID());

        // FRLIN: Franson sentence, containing ?, username, password
        sentenceParsers.put("FRLIN", new FRLIN());

        // GPGGA: time,lat,lon,alt,quality,horizontalPrecision (no date)
        sentenceParsers.put("GPGGA", new GPGGA());

//        // GGL: time,lat,lon      
//        sentenceParsers.put("GPGGL", new GPGGL()); 

        // GPRMC: time,date,lat,lon,speed,course,magVariation
        sentenceParsers.put("GPRMC", new GPRMC());

//        // ????
//        sentenceParsers.put("GPRMZ", new GPRMZ());

//        //only really good GPS devices have this sentence but ...
//        // Track info: true and mag track heading, ground speed ...
//        // No implementation as yet, we have no use for it
//        sentenceParsers.put("GPVTG", new GPVTG());
    }

    public GPSPosition parseGPSmessage(String message) {

        // Position state, updated by information in message sentences
        GPSPosition position = new GPSPosition();

        // Check for an edge-case: GPSGate messages (GPRMC) sent via UDDP.
        // The message is prepended with username/password, use username
        // as the identifier.
        // Look for lines pre-pended with pattern:  ".*/.*$.*" This is an idiom
        // used by OpenGTS (Open GPS Tracking Service) for sending username
        // and password information - at least for UDP messages. If we see
        // this we currently use the username as the PLI identifier... Ted Roe

        if (message.matches("^.*[/].*[$]*")) { // Ex: "username/password$........."
            if (message.indexOf("$") > 3) { // dollar sign has to be past index 3
                position.id = message.substring(0, message.indexOf('/'));
            }
        }

        // Split up a message into GPS sentences using $
        String[] sentences = message.split("\\$"); // use array      

        NMEA nmea = new NMEA();  // new instance of nmea processor (stateful bean)      

        // process multiple parts (sentences) "$..................$................"
        // must throw the first part away (null or edge-case info: GPSGate/UDP)
        for (int i = 1; i < sentences.length; i++) {
            String sentence = sentences[i];

            try {
                // parse a sentence, updating the object with sentence data items
                nmea.parseGPSsentence("$" + sentence, position); //parse and update position

            } catch (NmeaProcException e) {
//                    System.out.println("*** " + e.getMessage());
                return null;
            }


        }

        return position;

    }

    /**
     * Given a GPS sentence, parse it and return. The Parser will update
     * the GPSPosition object.
     * @param sentence A GPS sentence (NMEA spec type, FRLIN, DEVID, other....)
     * @param position A class that holds GPS information
     * @return
     * @throws NmeaProcException 
     */
    void parseGPSsentence(String sentence, GPSPosition position) throws NmeaProcException {

        // break sentence into tokens
        if (sentence.startsWith("$")) {
            String nmea = sentence.substring(1);
            String[] tokens = nmea.split(",");
            String name = tokens[0];           // sentence name

            validChecksum(sentence); // will throw exception if invalid checksum

            if (sentenceParsers.containsKey(name)) {
                sentenceParsers.get(name).parse(tokens, position);
            }
        }
    }

    /**
     * Retrieves a mapped ID from the properties, if it exists.
     * 
     * @param gpsId The identifier of the GPS source
     * @return The mapped unit id if it exists, null otherwise
     */
    public static String getMappedId(String gpsId) {


        // This is NOT the place to be initializing system properties
        if (properties == null) {
            return gpsId;
        }

        String strProp = "";
        for (Entry<Object, Object> entry : properties.entrySet()) {
            strProp = entry.getKey().toString();
//            System.out.println("Checking against key: " + strProp);
//            log.debug("Checking against key: " + strProp);
            if (strProp.startsWith("devid")) {
                strProp = strProp.replace("devid", "");
//                log.debug("Removed 'devid', prop is now: " + strProp);
                if (strProp.equals(gpsId)) {
//                    log.debug("Mapped " + gpsId + " to " + entry.getValue());
                    return (String) entry.getValue();
                }
            } else {
//                System.out.println("prop didn't start with 'devid'");
//                log.debug("prop didn't start with 'devid'");
            }
        }

//        log.debug("No mapped ID found, returning null...");
        return gpsId;
    }

    public static void setMappingProperties(String filename) {
        mappingProperties = filename;
    }
}
