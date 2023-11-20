# Goal

Create a mechanism to dynamically load translation dynamically from the server.
Benefits compared to regular strings.xml values:

- Ability to change translation without releasing new version of the app
- Ability to change translation without updating the app
- Compile-time safety of translation parameters
- Ability to provide parameters to translation
- Ability to split whole data set into buckets and download only required buckets on demand
- Perfect fit for Compose world

# Approach
There are two main components used for translation:

- Metadata - contains information about translation keys, parameters, etc.
- Buckets - contains actual translation for each language

## Metadata

Metadata is a json file which contains information about translation keys, parameters, etc.
Format is as following:

``` 
{
    "defaultLanguage": "en",
    "languages": [
        "en",
        "ua"
    ],
    "buckets": [
        {
            "name": "login",
            "schemeVersion": 1,
            "editionVersion": 1
        }
    ]
}
```

- defaultLanguage - default language which will be used if no translation is found for the current
  language
- languages - list of supported languages
- buckets - information about buckets which contains translation

## Buckets

Buckets are json files which contains actual translation for each language.
Format is as following:

```
{
    "language": "en",
    "editionVersion": 1,
    "translations": [
        {
            "key": "LoginScreen.ButtonName",
            "params": [
                {
                    "name": "param1",
                    "type": "string"
                },
                {
                    "name": "param2",
                    "type": "long",
                    "format": "startDate"
                }
            ],
            "value": "Log in {param1} ({param2})"
        }
    ]
}
```

- language - language for which this bucket is created
- editionVersion - version of this bucket.
- translations - list of translation keys with parameters and values

Name of the Bucket is generated in the following way:

- name - name of the bucket
- schemeVersion - version of the metadata scheme. It is used to determine scheme version and rely on
  it.
  Buckets can be updated only for the same scheme version. If scheme version is changed, buckets
  should not be updated.
- language - language for which this bucket is created
