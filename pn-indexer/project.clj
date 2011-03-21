(defproject pn-indexer "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [ant/ant-launcher "1.6.2"]
                 [org.apache.maven/maven-ant-tasks "2.0.10"]
                 [org.apache.solr/solr-core "1.4.1"]
                 [org.apache.solr/solr-solrj "1.4.1"]
                 [com.hp.hpl.jena/jena "2.6.2"]
                 [com.hp.hpl.jena/iri "0.7"]
                 [net.sf.saxon/saxon9he "9.2.0.2"]
                 [com.ibm.icu/icu4j "3.4.4"]
                 [log4j/log4j "1.2.16"]
                 [org.slf4j/slf4j-api "1.5.6"]
                 [org.slf4j/slf4j-log4j12 "1.5.6"]
                 [xerces/xercesImpl "2.9.1"]
                 [xml-apis/xml-apis "1.0.b2"]
                 [org.apache.solr/solr-commons-csv "1.4.1"]
                 [org.apache.solr/solr-solrj "1.4.1"]
                 [commons-httpclient/commons-httpclient "3.1"]
                 [commons-logging/commons-logging "1.1.1"]
                 [commons-codec/commons-codec "1.4"]
                 [papyri.info.mulgara/driver "2.1.9"]
                 [papyri.info.mulgara/querylang "2.1.9"]]
  :main info.papyri.indexer)