package org.dynodict

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.dynodict.manager.DynoDictManagerImpl
import org.dynodict.model.DLocale
import org.dynodict.model.DString
import org.dynodict.model.Key
import org.dynodict.model.metadata.BucketsMetadata

class MainActivity : AppCompatActivity() {

    private val dynoDict by lazy { createDynoDict() }

    private val strings = MutableStateFlow<List<DString>>(emptyList())
    private val metadata = MutableStateFlow<BucketsMetadata?>(null)
    private val selectedLanguage = MutableStateFlow<String>("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val stringsState by strings.collectAsState()
            val metadataState by metadata.collectAsState()
            var loginButtonName by remember { mutableStateOf("") }
            val selectedLanguageState by selectedLanguage.collectAsState()
            Column(horizontalAlignment = CenterHorizontally) {
                Button(onClick = { startGettingMetadata() }) {
                    Text(text = "Get strings")
                }
                Column(
                    modifier = Modifier
                        .heightIn(max = 200.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .selectableGroup()
                ) {
                    metadataState?.languages.orEmpty().forEach { language ->
                        LanguageRadioButton(
                            language,
                            language == selectedLanguageState,
                            ::languageSelected
                        )
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(items = stringsState) { item ->
                        StringItem(item)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()

                Button({
                    loginButtonName = LoginScreen.ButtonName.get("Param1", 10)
                }) {
                    Text(text = "Get string: LoginScreen.ButtonName")
                }

                Text(text = loginButtonName)

                Spacer(modifier = Modifier.height(16.dp))
                Divider()

                AnimatedVisibility(visible = stringsState.isNotEmpty()) {
                    Column(horizontalAlignment = CenterHorizontally) {
                        CustomKeyArea()
                    }
                }
            }
        }
    }

    @Composable
    private fun CustomKeyArea() {
        var key by remember { mutableStateOf("LoginScreen.Input.Email") }
        var value by remember { mutableStateOf("") }
        OutlinedTextField(value = key, { key = it }, label = { Text(text = "Key") })
        Button(onClick = {
            value = dynoDict.get(Key(key))
        }, enabled = key.trim().isNotEmpty()) {
            Text(text = "Find Translation")
        }
        Text(text = "Result - $value")
    }

    @Composable
    fun StringItem(string: DString) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(string.key, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                string.value, modifier = Modifier.weight(1f), style = MaterialTheme.typography.h5
            )
        }
    }

    @Preview
    @Composable
    fun StringItemPreview() {
        StringItem(DString("Login.Button", "Log in"))
    }

    @Composable
    fun LanguageRadioButton(
        language: String,
        isSelected: Boolean,
        onSelected: (String) -> Unit = {}
    ) {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier
                .selectable(selected = isSelected, onClick = { onSelected.invoke(language) })
        ) {
            RadioButton(selected = isSelected, onClick = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = language, style = MaterialTheme.typography.h6)
        }
    }


    @Preview
    @Composable
    fun LanguageRadioPreview() {
        Column {
            LanguageRadioButton("en", isSelected = false)
            LanguageRadioButton("ua", isSelected = true)
        }
    }

    private fun languageSelected(lang: String) {
        selectedLanguage.value = lang
        startGettingMetadata()
    }

    private fun createDynoDict(): DynoDict {
        return DynoDict.initWith(this, "https://raw.githubusercontent.com/mkovalyk/GraphicEditor/master/")
    }


    private fun startGettingMetadata() {
        lifecycleScope.launch(Dispatchers.IO) {
            dynoDict.updateStrings()
            val storageManager = (dynoDict.manager as DynoDictManagerImpl).storageManager
            val meta = storageManager.getMetadata()
            metadata.value = meta
            val selectedLang = selectedLanguage.value.ifEmpty {
                meta?.defaultLanguage.orEmpty()
            }
            val result = storageManager.getAllForLanguage(selectedLang)
            strings.value = result
            dynoDict.setLocale(DLocale(selectedLang))
        }
    }
}