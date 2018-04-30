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

import org.apache.log4j.Logger;

import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data structure class to hold some common PLI fields
 *
 *
 */
public class PLIData {

    private static Logger log = Logger.getLogger(PLIData.class);
    private String time;
    private String lat;
    private String lon;
    private Double alt;
    private Double speed;
    private Double heading;
    private String date;
    private String id;
    private String deviceId;

    /**
     * Default Constructor attributes
     */
    public PLIData() {
        this.time = null;//"";
        this.lat = null;//"";
        this.lon = null;//"";
        this.alt = Double.NaN;//0.;
        this.speed = Double.NaN;//0.;
        this.heading = Double.NaN;//0.;
        this.date = null;//"";
        this.id = null;//"";
        this.deviceId = null;//"";
    }

    /**
     * Construct from individual parameters (legacy method)
     * 
     * @param time
     * @param lat
     * @param lon
     * @param speed
     * @param heading
     * @param date
     * @param id         The logical name of GPS source (person, vehicle, etc.)
     * @param deviceId   The unique GPS device identifier
     */
    public PLIData(String time, String lat, String lon, String alt, Double speed, Double heading,
            String date, String id, String deviceId) {

        this.time = time;
        this.lat = lat;
        this.lon = lon;

        this.speed = speed;
        this.heading = heading;
        this.date = date;
        this.id = id;
    }

    /**
     * Construct from non-NMEA data (time/data/id) and NMEA class data (preferred)
     * @param time
     * @param date
     * @param id
     * @param nemaData 
     */
    public PLIData(String time, String date, String id, NMEA nemaData) {
        this.time = time;
        this.id = id;
        this.date = date;
        this.lat = nemaData.position.latStr();
        this.lon = nemaData.position.lonStr();
        this.alt = Double.valueOf(nemaData.position.alt);
        this.speed = Double.valueOf(nemaData.position.velocity);
        this.heading = Double.valueOf(nemaData.position.heading);
    }

    /**
     * Parses the id field for various known formats, and pulls the Unit ID
     * out for displaying on NICS
     * 
     * @return
     */
    public String getUnitID() {
        String unitId = null;

        String tempID = this.id;

//		System.out.println("\n\nTESTING ID PROCESSING\nid: " + tempID + "\n");
//        log.debug("\n\nTESTING ID PROCESSING\nid: " + tempID + "\n");

        if (tempID.startsWith("info:")) {
            // example:  "info:us.ca.calfire/fku:E4362";
            String values[] = tempID.split(":");
//			System.out.println("Splitting on ':', values length: " + values.length);
            log.debug("Splitting on ':', values length: " + values.length);
            try {
                unitId = values[values.length - 1]; // pull off whatever trails the final ":"
            } catch (Exception e) {
                unitId = this.id;
            }

        } else if (!tempID.contains(":") && tempID.contains(".")) {
            // Meant to catch GSPGate usernames, like:  us.ca.calfire.rvu.B9304
            String values[] = tempID.split(".");
//			System.out.println("Splitting on '.', values length: " + values.length);
            log.debug("Splitting on '.', values length: " + values.length);
            try {
                unitId = values[values.length - 1];
            } catch (Exception e) {

                unitId = this.id;
            }

        } else {
//			System.out.println("Nothing matched, just setting to id: " + this.id);
            log.debug("Nothing matched, just setting to id: " + this.id);
            unitId = this.id;
        }

        return unitId;
    }

    /**
     * Turns day and time into ISO8601 compliant time TODO: verify
     * 
     * @author Merfeld
     * 
     * @param day
     * @param time
     * @return
     */
    public static String toISO8601(String day, String time) {
        if (day == null || time == null) {
            return null;
        }

        log.debug("Converting time string from: '" + day + "' and '" + time + "'");
        String timestamp = "";
        if (day.length() > 0 && time.length() > 0) {
            log.debug("day and time variables have content, using passed in time");
            time = time.substring(0, 2) + ':' + time.substring(2, 4) + ':' + time.substring(4, 6);
            timestamp = day + "T" + time + "Z";
        } else {
            log.debug("day and time variables are empty, using current time");
            SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            now.setTimeZone(TimeZone.getTimeZone("GMT"));

            timestamp = now.format(new Date());
        }

        log.debug("Returning ISO8601 timestamp: " + timestamp);

        return timestamp;
    }

    // Set methods, with validation checks
    public void setId(String d) {
        if (d != null && d.length() > 0) { // must be not null
            this.id = d;
        }
    }

    public void setDeviceId(String d) { // must be not null
        if (d != null && d.length() > 0) {
            this.deviceId = d;
        }
    }

    public void setTime(String d) { // six digits
            this.time = d;
    }

    public void setDate(String d) { // six digits
            this.date = d;
    }

    public void setLat(String d) {
        this.lat = d;
    }

    public void setLon(String d) {
        this.lon = d;
    }

    public void setAlt(Double d) {
        this.alt = d;
    }

    public void setSpeed(Double d) {
        this.speed = d;
    }

    public void setHeading(Double d) {
        this.heading = d;
    }

    // Get methods
    public String getId() {
        return this.id;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public String getTime() {
        return this.time;
    }

    public String getIsoTime() {
        String itime = toISO8601(this.date, this.time);
        return itime;
    }

    public String getDate() {
        return this.date;
    }

    public String getLat() {
        return this.lat;
    }

    public String getLon() {
        return this.lon;
    }

    public Double getAlt() {
        return this.alt;
    }

    public Double getSpeed() {
        return this.speed;
    }

    public Double getHeading() {
        return this.heading;
    }
}
