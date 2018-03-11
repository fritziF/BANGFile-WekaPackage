# BANG-file Clustering WEKA Package

BANG file is a multidimensional structure of the grid file type.
It organizes the value space surrounding the data values, instead of comparing the data values themselves.
Its tree structured directory partitions the data space into block regions with successive binary divisions on dimensions.
The clustering algorithm identifies densely populated regions as cluster centers and expands those with neighboring blocks.

Inserted data has to be normalized to an interval [0, 1].

#### Bulding it

Use the following commands to build the WEKA package:

```
$ mvn clean install
$ ant make_package -Dpackage=BANGFile
```

Optionally you can specify -Dmaven.test.skip=true to skip the tests

The result of the build will be an installable WEKA package located at ```dist/BANGFile.zip```.

#### Running the tests

This will run all unit tests in the project (and sub-modules):

```
$ mvn test
```
