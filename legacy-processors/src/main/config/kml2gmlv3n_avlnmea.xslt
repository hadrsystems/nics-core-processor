<?xml version="1.0" encoding="UTF-8"?>
<!--

    Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

    3. Neither the name of the copyright holder nor the names of its contributors
    may be used to endorse or promote products derived from this software without
    specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
    SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
    CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
    OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

-->
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"	
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xal="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0"	
	xmlns:n="http://www.opengis.net/kml/2.2"
	exclude-result-prefixes="xs xsl xal n">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:template match="/">
    	<xsl:variable name="courseValue">
    		<xsl:choose>
    		<xsl:when test="n:Placemark/n:ExtendedData/n:Data[@name='course']/n:value !=''">
        		<xsl:value-of select="n:Placemark/n:ExtendedData/n:Data[@name='course']/n:value" />
        	</xsl:when>
        	<xsl:otherwise>
        		<xsl:value-of select="0" />
    		</xsl:otherwise>
			</xsl:choose>
    	</xsl:variable>
    
    
        <wfs:FeatureCollection xmlns:wfs="http://www.opengis.net/wfs"  
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
			xmlns:gml="http://www.opengis.net/gml"  
			xmlns:NICS="http://mapserver.nics.ll.mit.edu/NICS"  
			xsi:schemaLocation="http://mapserver.nics.ll.mit.edu/NICS http://129.55.46.83:8080/geoserver/NICS/wfs?service=WFS&amp;version=1.0.0&amp;request=DescribeFeatureType&amp;typeName=NICS%3Aavlnmea http://www.opengis.net/wfs http://129.55.46.83:8080/geoserver/schemas/wfs/1.0.0/WFS-basic.xsd">
            <gml:featureMember>            	
            	<NICS:avlnmea>            	
            		<NICS:id><xsl:value-of select="n:Placemark/@id" /></NICS:id>
            		<NICS:name><xsl:value-of select="n:Placemark/n:name"/></NICS:name>
            		<NICS:description><xsl:value-of select="n:Placemark/description"/></NICS:description>
            		<NICS:geom>
            			<gml:Point srsName="EPSG:4326">
            				<gml:coordinates><xsl:value-of select="n:Placemark/n:Point/n:coordinates"/></gml:coordinates>
            			</gml:Point>
            		</NICS:geom>
            		<NICS:speed><xsl:value-of select="n:Placemark/n:ExtendedData/n:Data[@name='speed']/n:value"/></NICS:speed>
            		<!-- <NICS:course><xsl:value-of select="n:Placemark/n:ExtendedData/n:Data[@name='heading']/n:value"/></NICS:course>-->
            		<NICS:course><xsl:value-of select="$courseValue"/></NICS:course>
            		<NICS:timestamp><xsl:value-of select="concat(string(n:Placemark/n:TimeStamp/n:when),'-07:00')"/></NICS:timestamp>
            	</NICS:avlnmea>
            </gml:featureMember>          
        </wfs:FeatureCollection>
	 </xsl:template>
</xsl:stylesheet>
