#!/bin/bash

function install_bundle {
    echo "Installing bundle $1..."
    curl -u $SLING_USER:$SLING_PASSWORD \
        -F "bundlefile=@$1;type=application/java-archive" \
        -F "action=install" \
        -F "bundlestartlevel=20" \
        $SLING_URL
}

SLING_URL="http://localhost:8080/system/console/bundles"
SLING_USER="admin"
SLING_PASSWORD="admin"

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
install_bundle $GOOGLE_COLLECTIONS

JCIP_ANNOTATIONS="$JCIP_HOME/target/net.jcip.annotations-$JCIP_VERSION.jar"
install_bundle $JCIP_ANNOTATIONS

JODA_TIME="$JODA_TIME_HOME/target/joda-time-$JODA_VERSION.jar"
install_bundle $JODA_TIME

MODESHAPE_COMMON="$MODESHAPE_HOME/modeshape-common/target/modeshape-common-$MODESHAPE_VERSION.jar"
install_bundle $MODESHAPE_COMMON

MODESHAPE_GRAPH="$MODESHAPE_HOME/modeshape-graph/target/modeshape-graph-$MODESHAPE_VERSION.jar"
install_bundle $MODESHAPE_GRAPH

MODESHAPE_SEARCH_LUCENE="$MODESHAPE_HOME/extensions/modeshape-search-lucene/target/modeshape-search-lucene-$MODESHAPE_VERSION.jar"
install_bundle $MODESHAPE_SEARCH_LUCENE

MODESHAPE_CND="$MODESHAPE_HOME/modeshape-cnd/target/modeshape-cnd-$MODESHAPE_VERSION.jar"
install_bundle $MODESHAPE_CND

MODESHAPE_REPOSITORY="$MODESHAPE_HOME/modeshape-repository/target/modeshape-repository-$MODESHAPE_VERSION.jar"
install_bundle $MODESHAPE_REPOSITORY

MODESHAPE_JCR="$MODESHAPE_HOME/modeshape-jcr/target/modeshape-jcr-$MODESHAPE_VERSION.jar"
install_bundle $MODESHAPE_JCR

MODESHAPE_JCR_API="$MODESHAPE_HOME/modeshape-jcr-api/target/modeshape-jcr-api-$MODESHAPE_VERSION.jar"
install_bundle $MODESHAPE_JCR_API

MODESHAPE_SERVER="$MODESHAPE_SERVER_HOME/target/com.sourcesense.stone.jcr.modeshape.server-$STONE_VERSION.jar"
install_bundle $MODESHAPE_SERVER

EXTERNAL_DEPENDENCIES="$EXTERNAL_DEPENDENCIES_HOME/target/com.sourcesense.stone.external.dependencies-$STONE_VERSION.jar"
install_bundle $EXTERNAL_DEPENDENCIES

WEBCONSOLE_SECURITY="$WEBCONSOLE_SECURITY_HOME/target/com.sourcesense.stone.extensions.webconsolesecurityprovider-$STONE_VERSION.jar"
install_bundle $WEBCONSOLE_SECURITY
