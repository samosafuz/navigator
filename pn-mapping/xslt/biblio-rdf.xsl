<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:t="http://www.tei-c.org/ns/1.0" xmlns:pi="http://papyri.info/ns"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:bibo="http://purl.org/ontology/bibo/" xmlns:cito="http://purl.org/spar/cito/"
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
  exclude-result-prefixes="t" version="2.0">
  <xsl:output omit-xml-declaration="yes"/>
  
  <xsl:include href="/data/papyri.info/git/navigator/pn-xslt/htm-teibibl.xsl"/>

  <xsl:variable name="path">/data/papyri.info/idp.data</xsl:variable>
  <xsl:variable name="outbase">/data/papyri.info/pn/idp.html</xsl:variable>
  <xsl:variable name="resolve-uris" select="false()"/>
  <xsl:variable name="abbreviation-marker">&#x00B0;</xsl:variable>
  <xsl:variable name="id">http://papyri.info/biblio/<xsl:value-of
      select="replace(/t:bibl/@xml:id, '[a-zA-Z]', '')"/>/ref</xsl:variable>
  <xsl:variable name="cite">http://papyri.info/biblio/<xsl:value-of
    select="replace(/t:bibl/@xml:id, '[a-zA-Z]', '')"/></xsl:variable>
  

  <xsl:template match="/t:bibl">
    <xsl:variable name="citation"><xsl:call-template name="buildCitation"/></xsl:variable>
    <rdf:Description rdf:about="{$id}">
      <rdfs:type rdf:resource="http://purl.org/dc/terms/BibliographicResource"/>
      <dcterms:isReferencedBy rdf:resource="{$cite}"/>
      <dcterms:bibliographicCitation><xsl:value-of select="$citation"/></dcterms:bibliographicCitation>
      <xsl:apply-templates select="t:relatedItem"/>
    </rdf:Description>
  </xsl:template>

  <xsl:template match="t:relatedItem[@type='appearsIn']">
    <dcterms:isPartOf>
      <rdf:Description rdf:about="{t:bibl/t:ptr/@target}/ref">
        <dcterms:hasPart rdf:resource="{$id}"/>
      </rdf:Description>
    </dcterms:isPartOf>
  </xsl:template>

  <xsl:template match="t:relatedItem[@type='reviews']">
    <cito:reviews>
      <rdf:Description rdf:about="{t:bibl/t:ptr/@target}/ref">
        <cito:isReviewedBy rdf:resource="{$id}"/>
      </rdf:Description>
    </cito:reviews>
  </xsl:template>

  <xsl:template match="t:relatedItem[@type='mentions']">
    <xsl:if test="not(contains(t:bibl/t:idno[@type='ddb'], ' '))">
      <cito:cites>
        <rdf:Description rdf:about="http:papyri.info/ddbdp/{t:bibl/t:idno[@type='ddb']}/work">
          <cito:isCitedBy rdf:resource="{$id}"/>
        </rdf:Description>
      </cito:cites>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
