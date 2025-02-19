<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (C) 2007-2021 Crafter Software Corporation. All Rights Reserved.
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License version 3 as published by
  ~ the Free Software Foundation.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:xslt="http://www.w3.org/1999/XSL/Transform">

    <!-- to keep the right formatting -->
    <xsl:output method="xml" indent="yes" cdata-section-elements="body content subject"/>
    <xsl:strip-space elements="*"/>

    <!-- copy all elements -->
    <xsl:template match="node() | @*">
        <!-- insert line breaks before comments -->
        <xsl:if test="self::comment()">
            <xsl:text>&#10;</xsl:text>
        </xsl:if>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
        <!-- insert line breaks after comments -->
        <xsl:if test="self::comment()">
            <xsl:text>&#10;</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:variable name="regex">/static-assets/([^&lt;"'\)\?\#]+)</xsl:variable>

    <xsl:template
            match="/dependency-resolver/item-types/item-type/dependency-types/dependency-type/includes/pattern/find-regex[contains(text(), $regex)]">
        <xsl:element name="find-regex">
            <xsl:text>/static-assets/([^&lt;"'\)\?\]\#]+)</xsl:text>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>