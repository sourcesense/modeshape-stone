=== What's this?

Stone is a project created to test how to replace the embedded JCR Repository in Sling with another
implementation (JBoss ModeShape).

The project includes OSGI bundles you need to install into Sling, along with some bundles that
are basically a replacement for existing ones.

The project also includes a patched version of ModeShape, created mainly because we had problems
with dynamic classloading in an OSGI context.

BEWARE: at the moment the project is in an unfinished state, so IT'S NOT READY FOR PRODUCTION

=== How to build the project

To build the project you need maven.
From the 'stone' directory, simply run

mvn install

We suggest you to set maven options with something like this:

export MAVEN_OPTS="-Xmx512M -XX:MaxPermSize=512M"

== About third-parties directory

We had to patch the build process of google-collections library because that is
a deprecated library, we plan to change the modeshape dependency on it to
it's new version Guava.

The build of the library is not included with the main one, so you need to run
the build process for these manually.

The library google-collection uses ant; to create the bundle run the
following command:

ant osgi

=== Installation

After you have successfully built the project, you have to manually install the
bundles into Sling.
First of all, replace the bundle
webconsole-security-provider with the one we provide, so that the authentication
won't rely on repository authentication policies; this is needed when you'll
switch-off jackrabbit server and turn-on modeshape.
If you don't do this, after you start the modeshape repository, you won't be
able to log-in anymore.
If you don't need any kind of authentication, you can simply switch-off the
bundle without further operations.

Depending on the source you need to use with modeshape, you have to choose one
amongst the following:

bundles/jcr/modeshape-server/target/com.sourcesense.stone.jcr.modeshape.server-1.0.0-SNAPSHOT-in-memory.jar
bundles/jcr/modeshape-server/target/com.sourcesense.stone.jcr.modeshape.server-1.0.0-SNAPSHOT-h2.jar
bundles/jcr/modeshape-server/target/com.sourcesense.stone.jcr.modeshape.server-1.0.0-SNAPSHOT-postgres.jar

Then you have to install the bundles the server depends on.

bundles/jcr/com.sourcesense.stone.jcr.base/target/com.sourcesense.stone.jcr.base-1.0.0-SNAPSHOT.jar
modeshape-stone/modeshape-common/target/modeshape-common-2.4.0.Final-stone-SNAPSHOT.jar
~/.m2/repository/net/jcip/com.springsource.net.jcip.annotations/1.0.0/com.springsource.net.jcip.annotations-1.0.0.jar
stone/modeshape-stone/modeshape-graph/target/modeshape-graph-2.4.0.Final-stone-SNAPSHOT.jar
third-parties/google-collections-read-only/build/dist/google-collect-snapshot/google-collect-snapshot.jar
~/.m2/repository/joda-time/joda-time/1.6.2/joda-time-1.6.2.jar
stone/modeshape-stone/modeshape-jcr/target/modeshape-jcr-2.4.0.Final-stone-SNAPSHOT.jar

PATCHED VERSION of lucene-core (See 'About Sandboxes' below)
PATCHED VERSION of lucene-analyzer (See 'About Sandboxes' below)

stone/modeshape-stone/modeshape-cnd/target/modeshape-cnd-2.4.0.Final-stone-SNAPSHOT.jar
stone/modeshape-stone/modeshape-jcr-api/target/modeshape-jcr-api-2.4.0.Final-stone-SNAPSHOT.jar
stone/modeshape-stone/modeshape-repository/target/modeshape-repository-2.4.0.Final-stone-SNAPSHOT.jar
stone/modeshape-stone/extensions/modeshape-search-lucene/target/modeshape-search-lucene-2.4.0.Final-stone-SNAPSHOT.jar

PATCHED VERSION of lucene-queryparser (See 'About Sandboxes' below)
PATCHED VERSION of lucene-queries (See 'About Sandboxes' below)
=== About Sandboxes

The sandbox directories contain some experiments we did during the development
process. Since they are experiments, they aren't supposed to properly work or
thery are in an unfinished state.
