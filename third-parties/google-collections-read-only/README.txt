=== What's this

The google-collection project here is a modified version of the official one
you can find at this url:

http://code.google.com/p/google-collections/

Please note that the google-collection project is deprecated, the developers
moved to the Guava project here:

http://code.google.com/p/guava-libraries/

The changes we made here were about the ant script, now it has a new target to
generate an OSGI bundle.

=== Building the project

You need ant to build this project.
To create an OSGI bundle, simply write

ant osgi

You can find the bundle into the directory

build/dist/google-collect-snapshot
