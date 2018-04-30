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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;



/**
 * Processes NMEA to other formats, initially KML.  Based on
 * URA/NICS nmea2kml project
 * 
 * TODO:
 * <p>
 * <ul>
 * 	<li>Write XSLT version</li>
 *  <li>Output to GML</li>
 * </ul>
 * </p>
 * 
 *
 */
public class NmeaProcessor implements Processor {

	/** Logger */
	private static Logger log = Logger.getLogger(NmeaProcessor.class);
	
	
	/**
	 * The order of the coordinates:
	 * <p><ul>
	 * 		<li>latlon</li>
	 * 		<li>lonlat</li>
	 * </ul></p>
	 */
	public static String coordOrder = "lonlat";	
	
	/**
	 * Desired format of output.  Accepts a String, and is processed into
	 * an OutputFormat enum.
	 * 
	 * <p>Currently supports:
	 * <ul>
	 * 		<li>kml</li>
	 * 		<li>gml</li>
	 * </ul>
	 * </p>
	 */
	private String format = "KML";
   
    private String layerKey = "avls-layer";  // id for gml layer processing

	
	/** 
	 * The OutPut format enumeration set via the 'format' property
	 * <p>
	 * 	Default: OutputFormat.Unset
	 * </p>  
	 */
	private OutputFormat outputFormat = OutputFormat.Unset;
	
	/**
	 * Constructor.
	 */
	public NmeaProcessor() { }
	
	
	@Override
	public void process(Exchange exchange) throws Exception {

		byte[] bodyBytes = null;
		String bodyStr = "";
				
		try{
			bodyBytes = exchange.getIn().getBody(byte[].class);
			bodyStr = new String(bodyBytes);
			
			log.debug("Debug Body:" + bodyStr);
			
			String outBodyStr = Util.parseNMEA(bodyStr, outputFormat, layerKey);
			
			if(outBodyStr == null || outBodyStr.isEmpty()) {
				log.debug("Formatted body came back null, so not forwarding in route");
				exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
			} else {
				// TODO: need to change to bytes at all?  Don't think so... but double check
				exchange.getOut().setBody(outBodyStr);
			}
			
		} catch (Exception e){
			log.error("Caught unhandled exception processing message body: " + e, e);  
		}
	}
	

	// Getters and Setters
	
	public final String getFormat() {
		return format;
	}
	
	public final void setFormat(String format) {
		this.format = format;		
		this.format = this.format.toUpperCase();
		
		log.debug("!!!Set format to: " + this.format);
		outputFormat = OutputFormat.valueOf(this.format);
		log.debug("!!!Set outputFormat to: " + outputFormat.toString());
	}

	// TODO: Don't want this to be set via a property, so maybe remove these, or make private
	public final OutputFormat getOutputFormat() {
		return outputFormat;
	}

	public final void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}


	public String getCoordOrder() {
		return coordOrder;
	}


	public void setCoordOrder(String coordOrder) {
		this.coordOrder = coordOrder;
	}
   
   public String getLayerKey() {
       return layerKey;
   }
   
   public void setLayerKey(String layerKey) {
       this.layerKey = layerKey;
   }

}
