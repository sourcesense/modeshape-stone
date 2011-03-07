#!/bin/bash

SLING_URL="http://localhost:8080/system/console/bundles"

JCIP_VERSION="1.0"
JODA_VERSION="2.0"
MODESHAPE_VERSION="2.4.0.Final-stone-SNAPSHOT"
STONE_VERSION="1.0.0-SNAPSHOT"
GOOGLE_VERSION="snapshot"

GOOGLE_COLLECTIONS_HOME="./google-collections-read-only"
JCIP_HOME="./jcip-annotations"
JODA_TIME_HOME="./joda-time/JodaTime"
MODESHAPE_HOME="./modeshape-stone"
MODESHAPE_SERVER_HOME="./bundles/jcr/modeshape-server"
EXTERNAL_DEPENDENCIES_HOME="./bundles/external-dependencies"
WEBCONSOLE_SECURITY_HOME="./bundles/extensions/webconsole-security-provider"

GOOGLE_COLLECTIONS="$GOOGLE_COLLECTIONS_HOME/build/dist/google-collect-snapshot/google-collect-$GOOGLE_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$GOOGLE_COLLECTIONS;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

JCIP_ANNOTATIONS="$JCIP_HOME/target/net.jcip.annotations-$JCIP_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$JCIP_ANNOTATIONS;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

JODA_TIME="$JODA_TIME_HOME/target/joda-time-$JODA_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$JODA_TIME;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

MODESHAPE_COMMON="$MODESHAPE_HOME/modeshape-common/target/modeshape-common-$MODESHAPE_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$MODESHAPE_COMMON;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

MODESHAPE_GRAPH="$MODESHAPE_HOME/modeshape-graph/target/modeshape-graph-$MODESHAPE_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$MODESHAPE_GRAPH;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

MODESHAPE_SEARCH_LUCENE="$MODESHAPE_HOME/extensions/modeshape-search-lucene/target/modeshape-search-lucene-$MODESHAPE_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$MODESHAPE_SEARCH_LUCENE;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

MODESHAPE_CND="$MODESHAPE_HOME/modeshape-cnd/target/modeshape-cnd-$MODESHAPE_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$MODESHAPE_CND;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

MODESHAPE_REPOSITORY="$MODESHAPE_HOME/modeshape-repository/target/modeshape-repository-$MODESHAPE_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$MODESHAPE_REPOSITORY;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

MODESHAPE_JCR="$MODESHAPE_HOME/modeshape-jcr/target/modeshape-jcr-$MODESHAPE_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$MODESHAPE_JCR;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

MODESHAPE_JCR_API="$MODESHAPE_HOME/modeshape-jcr-api/target/modeshape-jcr-api-$MODESHAPE_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$MODESHAPE_JCR_API;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

MODESHAPE_SERVER="$MODESHAPE_SERVER_HOME/target/com.sourcesense.stone.jcr.modeshape.server-$STONE_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$MODESHAPE_SERVER;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

EXTERNAL_DEPENDENCIES="$EXTERNAL_DEPENDENCIES_HOME/target/com.sourcesense.stone.external.dependencies-$STONE_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$EXTERNAL_DEPENDENCIES;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL

WEBCONSOLE_SECURITY="$WEBCONSOLE_SECURITY_HOME/target/com.sourcesense.stone.extensions.webconsolesecurityprovider-$STONE_VERSION.jar"

curl -v -u admin:admin \
        -F "bundlefile=@$WEBCONSOLE_SECURITY;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL