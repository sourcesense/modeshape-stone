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

We had to patch the build process of some libraries ModeShape depends on because they originally
didn't create OSGI compliant jars.
The libraries are:

google-collections
joda-time

The build of these libraries is not included with the main one, so you need to run the build process
for these manually.

The library google-collection uses ant; to create the bundle run the following command:

ant osgi

=== Installation

After you have successfully built the project, you have to manually install the bundles into Sling.
The bundles you need to install are:


=== Sandboxes

