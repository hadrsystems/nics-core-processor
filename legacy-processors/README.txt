This project contains code for some legacy/custom Processor classes for use with a Camel Spring
application like nics-core-processor/spring-runner.

Currently this is acting as a child pom in the nics-core-processor repository. So to successfully build, copy
the legacy-processors directory into your nics-core-processors directory, and assuming you've already built
the rest, you should be able to build just legacy-processors successfully.

Build with 'mvn package', and the resuling target/legacy-processors-VERSION.tar.gz will contain a
directory with the libraries necessary to run these processors.

You'll still have to copy the src/main/scripts/* and src/main/config/spring/* over to wherever your
untarred the legacy-processor-VERSION/. Scripts go in the root dir, and src/main/config/spring/* go
into config/spring/.

This contains the following processors/applications:

nmea-processor - Consumes NMEA formatted data, and publishes gml to GDFC
xri-splitter - Consumes proprietary GST tracks from Riverside and splits aircraft and ground vehicles to separate topics
gst2gml - Consumes the air and ground vehicals from xri-splitter, and publishes the resulting gml to GDFC


Resulting directory structure is like other NICS components:

/component-name-VERSION/
	config/
		component.properties
		log4j-component.properties
		(misc other files, like xslt files)
		spring/
			component.xml
	lib/

	runComponent.sh



