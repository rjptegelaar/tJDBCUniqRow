<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  exclude-result-prefixes="xsl">

	<!-- 
	Copyright 2017 Paul Tegelaar
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

	 -->

	<xsl:variable name="deps" select="document('../../../target/deps.xml')" />
	
	<xsl:output method="xml" encoding="utf-8" indent="yes" />

	<!-- Identity template : copy all text nodes, elements and attributes -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<!-- When matching DataSeriesBodyType: do nothing -->
	<xsl:template match="IMPORTS">
		<IMPORTS>			
			<xsl:element name="IMPORT">
				<xsl:attribute name="NAME"><xsl:value-of select="concat($deps/list/artifactId,'-',$deps/list/version)"/></xsl:attribute>
				<xsl:attribute name="MODULE"><xsl:value-of select="concat($deps/list/artifactId,'-',$deps/list/version,'.jar')"/> </xsl:attribute>
				<xsl:attribute name="REQUIRED"><xsl:text>true</xsl:text></xsl:attribute>
			</xsl:element>
			
			
			<xsl:apply-templates select="$deps/list/dep[test='false']" />
		</IMPORTS>
	</xsl:template>

	<xsl:template match="dep">		
		<IMPORT>			
			<xsl:variable name="groupid" select="substring-before (text(), ':')"/>
			<xsl:variable name="artifactid" select="substring-before(substring-after (text(), ':'),':')"/>
			<xsl:variable name="extension" select="substring-before(substring-after(substring-after (text(), ':'),':'),':')"/>
			<xsl:variable name="version" select="substring-after(substring-after(substring-after(text(),':'),':'),':')"/>
			<xsl:attribute name="NAME"><xsl:value-of select="concat($artifactid,'-',$version)"/></xsl:attribute>
			<xsl:attribute name="MODULE"><xsl:value-of select="concat($artifactid,'-',$version,'.',$extension)"/></xsl:attribute>
			<xsl:attribute name="REQUIRED"><xsl:text>true</xsl:text></xsl:attribute>						
		</IMPORT>
	</xsl:template>

</xsl:stylesheet>