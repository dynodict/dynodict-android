# TODO

- [x] Finish default approach
- [x] Don't validate url parameter
- [x] Initialize default Dynodict with parameters and use Content provider
- [x] rename java to kotlin folders
- [ ] Add property to set source path
- [x] Add package
- [x] Add imports
- [ ] Inflate parameters
- [ ] Generate JSON for assets folder
- [ ] Attach folder for package
- [ ] Custom formatter Ideas:
- [ ]

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
