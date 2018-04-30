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

/**
 * Holds utility methods for processing NMEA sentences
 * 
 * <p>Based on the same class in the nmea2kml project</p>
 *
 *
 */
public class Util {

    /** Logger */
    private static final Logger log = Logger.getLogger(Util.class);
        
    private static String layerKey = "avl_feed"; // override by spring config?

    public Util() { }

    /**
     * Takes the nmea message body, and sends it off to the specified formatter
     * 
     * @param data NMEA message body, can contain multiple sentences
     * @param format The desired output format
     * @param layerKey For gml, a string that gives an indication of the source
     * of the data, put into the gml object, to facilitate mapping the gml object to
     * to a geo-layer by downstream processing.
     * 
     * @return String containing the formatted content.  If an unknown format is supplied,
     * 		   it will default to KML.
     */
    public static String parseNMEA(String data, OutputFormat format, String layerKey) {

        PLIData pliData = parseToPLIData(data);

        if (pliData == null) {  // could not parse the NMEA message body
            log.debug("Could not parse NMEA message: " + data);
            return null;
        }

        String formattedBody = null;

        switch (format) {
            case KML:
                formattedBody = toKML(pliData, layerKey);
                break;

            case GML:
                formattedBody = toGML(pliData, layerKey);
                break;

            default:
                log.error("Unsupported format type!: " + format.toString() + ". Defaulting to KML");
                formattedBody = toKML(pliData, layerKey);
        }

        return formattedBody;
    }

    /**
     * Parses the NMEA values into a PLIData object to be used for
     * creating other formats
     *  
     * @param data The NMEA message body
     * 
     * @return A PLIData object populated with values from the NMEA body
     */
    public static PLIData parseToPLIData(String message) {
        PLIData pliData = new PLIData();   // product of this method

        NMEA nmeaData = new NMEA();  // new instance of nmea (has state)

        try {
            nmeaData.position = nmeaData.parseGPSmessage(message); //get data
//                     NMEA.GPSPosition position = nmeaData.parseGPSmessage(message); //get data
            if (nmeaData.position == null) {
                throw new NmeaProcException("parse error");
            }
            pliData = new PLIData(nmeaData.position.timeStr(), nmeaData.position.dateStr(), nmeaData.position.idStr(), nmeaData);

        } catch (NmeaProcException e) {
            log.debug("ParseToPLIData: parsing failed - " + message);
//                    System.out.println("*** " + e.getMessage());
            pliData = null;
        }
//            }
//        } // end while
        return pliData;
    }

    /**
     * Creates KML Placemark
     * 
     * TODO: Create just Placemark, not wrapped in a <kml> tag.
     * TODO: 
     * 
     * @param data PLI data object from NMEA message
     * @param feedName Name of the incoming feed for the PLI
     * @return String containing valid Placemark, or null if there is
     * insufficient information to produce a valid Placemark
     */
    public static String toKML(PLIData data, String feedName) {

        // TODO:
        // if typeof String
        // if typeof PLIData
        // if typeof PlacemarkType


        // Make sure that that we have at least the minimal amount of information
        // to form a valid KML placemark

        String place = null;  // initialize the returned Placemark xml


        /* What avlsUdop wants:
        <?xml version="1.0" encoding="UTF-8"?>
        <n:Placemark xmlns:n="http://www.opengis.net/kml/2.2" id="H10">
        <n:name>H10</n:name>
        <n:description>RVCFire:Copter:H10:353</n:description>
        <n:TimeStamp>
        <n:when>2012-04-19T14:29:15Z</n:when>
        </n:TimeStamp>
        <n:styleUrl>#msn_engine</n:styleUrl>
        <n:ExtendedData>
        <n:Data name="timestamp">
        <n:value>2012-04-19T14:29:15Z</n:value>
        </n:Data>
        <n:Data name="course">
        <n:value>0</n:value>
        </n:Data>
        <n:Data name="speed">
        <n:value>0</n:value>
        </n:Data>
        <n:Data name="Group">
        <n:value>RVCFire</n:value>
        </n:Data>
        <n:Data name="SubGroup">
        <n:value>Copter</n:value>
        </n:Data>
        <n:Data name="UnitID">
        <n:value>H10</n:value>
        </n:Data>
        </n:ExtendedData>
        <n:Point>
        <n:coordinates>-116.973167,32.818667,0</n:coordinates>
        </n:Point>
        </n:Placemark>
         */


        // Checks for necessary minimum data:

        String isoTime = data.getIsoTime();
        // Must haves
        if (data.getId() == null
                || isoTime == null
                || data.getLat() == null
                || data.getLon() == null) {
            String emsg = "Missing/invalid manditory data, see: "
                    + "\n id   = " + data.getId()
                    + "\n time = " + isoTime
                    + "\n lat  = " + data.getLat()
                    + "\n lon  = " + data.getLon();
            log.error(emsg);
            return null;
        }

        // Optional other elements
        boolean hasExtendedData =
                data.getHeading() != Double.NaN
                && data.getSpeed() != Double.NaN;



        String tempID = data.getUnitID();

        // Start KML Placemark
        place = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>";
        //place = place+="<Placemark id='info:mitll/avl:v1:fire:";
        //place=place+data.getId()+"'>";
        //place = place+="<Placemark id='" + data.getId() + "'>";
        // Input example: "info:mitll/pli:v1/CA:CalFire:RRU:Engine:E40"
        place = place += "<n:Placemark xmlns:n='http://www.opengis.net/kml/2.2' id='" + tempID + "'>";

        place = place + "<n:name>" + tempID + "</n:name>";
        //place = place+"<n:name>Fresno</n:name>";
        place = place + "<n:TimeStamp><n:when>" + isoTime + "</n:when></n:TimeStamp>";

        if (NmeaProcessor.coordOrder.equals("lonlat")) {
            // Add coordinates
            place = place + "<n:Point><n:coordinates>" + data.getLon() + "," + data.getLat() + "</n:coordinates></n:Point>";
        } else { //if(NmeaProcessor.coordOrder.equals("latlon")) {
            place = place + "<n:Point><n:coordinates>" + data.getLat() + "," + data.getLon() + "</n:coordinates></n:Point>";
        }

        if (hasExtendedData) {
            // Add the avldata: time, date, id, speed, heading, 
            place = place + "<n:ExtendedData>";
            place = place + "<n:Data name='speed'><n:value>" + data.getSpeed() + "</n:value></n:Data>";
            place = place + "<n:Data name='course'><n:value>" + data.getHeading() + "</n:value></n:Data>";
            place = place + "<n:Data name='timestamp'><n:value>" + isoTime + "</n:value></n:Data>";

            // Close ExtendedData
            place = place + "</n:ExtendedData></n:Placemark>";
        }


        return place;
    }

    /**
     * Object holding nmea data to be turned into a GML
     * point?
     * 
     * @param PLI object containing PLI information
     * @return Content formatted as GML
     */
    public static String toGML(PLIData pliData, String feedName) {
        String gmlContent = null;
        //Embedded feed name was: "nics_avl"
        /* Sample GML output
        <?xml version="1.0" encoding="UTF-8"?>
        <wfs:FeatureCollection xmlns="http://www.opengis.net/wfs" 
        xmlns:wfs="http://www.opengis.net/wfs" xmlns:gml="http://www.opengis.net/gml" 
        xmlns:NICS="http://mapserver.nics.ll.mit.edu/NICS" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        xsi:schemaLocation="http://mapserver.nics.ll.mit.edu/NICS http://129.55.46.83:8080/geoserver/NICS/wfs?service=WFS&amp;version=1.0.0&amp;request=DescribeFeatureType&amp;typeName=NICS%3Anics_avl http://www.opengis.net/wfs http://129.55.46.83:8080/geoserver/schemas/wfs/1.0.0/WFS-basic.xsd">
        <gml:boundedBy>
        <gml:null>inapplicable</gml:null>
        </gml:boundedBy>
        <gml:featureMember>
        <NICS:nics_avl>			<!-- incoming feed identifier, should not be hard coded -->
        <NICS:id>info:id:TODO</NICS:id>
        <NICS:name>E234</NICS:name>
        <NICS:description>Some Descriptor</NICS:description>
        <NICS:point>
        <gml:Point>
        <gml:coordinates>-34.20994204,117.2840240,0</gml:coordinates>
        </gml:Point>	
        </NICS:point>
        <NICS:course>127.34</NICS:course>
        <NICS:speed>75.204</NICS:speed>
        </NICS:nics_avl>
        </gml:featureMember>
        </wfs:FeatureCollection>		
         */


        // Check for necessary minimum data:
        String isoTime = pliData.getIsoTime();
        String id = pliData.getId(), unitId = pliData.getUnitID();
        String lat = pliData.getLat(), lon = pliData.getLon();
        
        // Must haves
        if (id == null
                || unitId == null
                || isoTime == null
                || lat == null
                || lon == null) {
            String emsg = "Missing/invalid manditory data, see: "
                    + "\n id   = " + pliData.getId()
                    + "\n time = " + isoTime
                    + "\n lat  = " + pliData.getLat()
                    + "\n lon  = " + pliData.getLon();
            log.error(emsg);
            return null;
        }

        // Optional additional elements
        Double speed = pliData.getSpeed(), heading = pliData.getHeading();
        boolean hasHeadingSpeed =
                speed != Double.NaN && heading != Double.NaN;

        final String xmlHeader = "<?xml version='1.0' encoding='UTF-8'?>";
        //final String featureCollectionHeader = "<wfs:FeatureCollection xmlns='http://www.opengis.net/wfs' xmlns:wfs='http://www.opengis.net/wfs' xmlns:gml='http://www.opengis.net/gml' xmlns:NICS='http://mapserver.nics.ll.mit.edu/NICS' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'	xsi:schemaLocation='http://mapserver.nics.ll.mit.edu/NICS http://129.55.46.83:8080/geoserver/NICS/wfs?service=WFS&amp;version=1.0.0&amp;request=DescribeFeatureType&amp;typeName=NICS%3Anics_avl http://www.opengis.net/wfs http://129.55.46.83:8080/geoserver/schemas/wfs/1.0.0/WFS-basic.xsd'>";

        String featureCollectionHeaderOPEN = "<wfs:FeatureCollection xmlns:wfs=\"http://www.opengis.net/wfs\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xmlns:gml=\"http://www.opengis.net/gml\"  xmlns:NICS=\"http://mapserver.nics.ll.mit.edu/NICS\"  xsi:schemaLocation=\"http://mapserver.nics.ll.mit.edu/NICS http://129.55.46.83:8080/geoserver/NICS/wfs?service=WFS&amp;version=1.0.0&amp;request=DescribeFeatureType&amp;typeName=NICS%3A" + feedName + " http://www.opengis.net/wfs http://129.55.46.83:8080/geoserver/schemas/wfs/1.0.0/WFS-basic.xsd\">";
        final String featureCollectionHeaderCLOSE = "</wfs:FeatureCollection>";
        final String gmlFeatureMemberOPEN = "<gml:featureMember>";
        final String gmlFeatureMemberCLOSE = "</gml:featureMember>";
        final String nicsGeomOPEN = "<NICS:geom><gml:Point srsName="
                + "\"EPSG:4326\"><gml:coordinates>";
        final String nicsGeomCLOSE = "</gml:coordinates></gml:Point></NICS:geom>";
        final String nicsNameOPEN = "<NICS:name>", nicsNameCLOSE = "</NICS:name>";

        StringBuilder sb = new StringBuilder();

        sb.append(xmlHeader + featureCollectionHeaderOPEN + gmlFeatureMemberOPEN);
        // TODO: Insert <NICS:testgml fid='blah'> opener
        sb.append("<NICS:" + feedName + ">"); //**************************************
        sb.append("<NICS:id>" + id + "</NICS:id>");
        sb.append(nicsNameOPEN + unitId + nicsNameCLOSE);

        // TODO: Currently no description data coming in from nmea sending devices... although we
        //		 could add our own content here...
        //sb.append("<NICS:description>" + "" + "</NICS:description>");
        sb.append(nicsGeomOPEN + lat + "," + lon +  nicsGeomCLOSE);

        if (hasHeadingSpeed) {
            sb.append("<NICS:speed>" + pliData.getSpeed() + "</NICS:speed>");
            sb.append("<NICS:course>" + pliData.getHeading() + "</NICS:course>");
        }
        sb.append("<NICS:timestamp>" + pliData.getIsoTime() + "</NICS:timestamp>");
        sb.append("</NICS:" + feedName + ">");
        sb.append(gmlFeatureMemberCLOSE + featureCollectionHeaderCLOSE);

        gmlContent = sb.toString();

        return gmlContent;
    }

}
