## LiveMap
A real time updating web map for Minecraft servers running Paper.

### Building
The project can be built by downloading it, navigating into the directory, and running `./gradlew build` or
`gradlew.bat build` on Windows.

The compiled jar be `build/LiveMap-X.Y.Z.jar`.

### Contributing
Contributions must follow the following rules:
1) Lines should be 120 characters long at most, this is not a strict requirement, lines *can* be longer.

2) Never use wildcard imports.

3) Sometimes there can be name conflicts when importing, import them with a custom name, prefixed by the source. For
   example "LibAListener" and "LibBListener".

4) Avoid excessive use of `.apply {}` or similar.

5) Do not statically import individual elements from enums or objects.

6) If there is a large block of mostly similar code, align it with spaces, as it makes things more readable.