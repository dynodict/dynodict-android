# DynoDict

### Description

This is the SDK to generate and update the list of Strings used in your project.
Use plugin to generate Kotlin placeholders for strings. For example:

```
{
    "translations": [
        {
            "key": "LoginScreen.ButtonName",
            "params": [
                {
                    "type": "string",
                    "key": "param1"
                },
                {
                    "type": "long",
                    "key": "param2",
                    "format":"startDate"
                }
            ],
            "value": "Log in {param1} ({param2})"
        }
    ]
}
```

is converted into:

```
object LoginScreen : StringKey("LoginScreen") {
    object ButtonName : StringKey("ButtonName", LoginScreen) {
        fun get(param1: String, param2: Long): String {
            return DynoDict.instance.get(
                this,
                Parameter.StringParameter(param1, key = "param1"),
                Parameter.LongParameter(param2, key = "param2", format = "startDate"))
        }
    }
}
```

which can be used further in code:

```
 val loginButtonName = LoginScreen.ButtonName.get("Param1", time)
```

### Plugin usage

1. Apply plugin:
   ```
   plugins {
       id 'org.dynodict.plugin'
    }
   ```
2. run gradlew script
   ```
   ./gradlew clean :app:downloadStrings --package=org.dynodict --url=metadata_url 
   ```

This task will download metadata from *metadata_url* and will generate Kotlin file Strings.kt
with Kotlin placeholders as well as json/yaml buckets

3. Enjoy!

### Task customization

For :downloadStrings there are few mandatory parameters which need to be passed:

*--package* - 
