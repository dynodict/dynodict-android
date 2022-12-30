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
- [ ] Generate list of required Formatters
- [ ] Add enum FormatterNotFoundStrategy to take care the scenario when format can't be found
- [x] Get rid of Key class since it is redundant
- [x] Add tests for StringProviderImpl
- [ ] Consider creation of the new module for strings generation
- [ ] Deploy to real maven repository

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