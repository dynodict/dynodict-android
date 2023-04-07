# TODO

- [x] Finish default approach
- [x] Don't validate url parameter
- [x] Initialize default Dynodict with parameters and use Content provider
- [x] rename java to kotlin folders
- [x] Add property to set source path
- [x] Add package
- [x] Add imports
- [x] Inflate parameters
- [x] Generate JSON for assets folder
- [x] Attach folder for package
- [x] Generate list of required Formatters
- [x] Get rid of Key class since it is redundant
- [x] Add tests for StringProviderImpl
- [x] ~~Consider creation of the new module for strings generation~~ Too much things to be done like
- [x] ~~Automatic upgrade of the library~~ No need since no module is generated. At least in first
  version
- [x] Try to find the package name automatically
- [x] Create abstraction over kotlinx.serialization - Migrate to StringFormat instead of
- [x] Create task to deploy using one command
- [x] Clean up
- [x] Parameters verification
- [x] Create remote models for DString, Bucket, etc.
- [ ] Create StringProviderCallback to catch all Exception happened during getting the string
- [ ] Migrate to Initializer instead of ContentProvider
- [ ] Add tests
  * DynoDictManagerImpl
  * ParametersEvaluator
  * ObjectsGenerator. IllegalTypeException -> identifyType() +
  * RemoteManagerImpl
  * Bucket.generateFilename()
  * StorageManagerImpl()
  * MetadataMapper + 
  * BucketMapper +
- [x] ~~Add enum FormatterNotFoundStrategy to take care the scenario when format can't be found~~ No need since it can be handled via DynodictCallback
- [ ] Deploy to real maven repository
- [ ] Create **sh** file to deploy all sequentially
- [ ] Split by retrofit and core implementation

### Parameters edge cases

- There are more parameters than in metadata - remove
- There are too few parameters so nothing to inflate - just skip?
- Incorrect formatter
- Incorrect type

```json
{
  "format": "startTime",
  "type": "Int",
  "key": "param1"
}
```

1. Create plugin to generate code based on input metadata, YAML, JSON
2. Abstract away from Kotlin Serialization stuff
3. Plugin should also copy default methods

Plugins functionality:

1. Download json or import file which should be parsed or copied
2. It is either yaml or json file
3. Convert it to Structured list of data
4. Store default json values in assets directory
5. Generate objects

Format contract:

1. camelCase