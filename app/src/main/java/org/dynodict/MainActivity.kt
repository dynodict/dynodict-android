package org.dynodict

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.dynodict.model.DString
import org.dynodict.model.metadata.BucketsMetadata
import org.dynodict.remote.RemoteManagerImpl
import org.dynodict.remote.RemoteSettings
import org.dynodict.storage.FileBucketsStorage
import org.dynodict.storage.FileMetadataStorage
import org.dynodict.storage.StoreManagerImpl

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
            val selectedLanguageState by selectedLanguage.collectAsState()
            Column(horizontalAlignment = CenterHorizontally) {
                Button(onClick = { startGettingMetadata() }) {
                    Text(text = "Start getting translations")
                }
                Column(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .selectableGroup()
                ) {
                    metadataState?.languages.orEmpty().forEach { language ->
                        LanguageRadioButton(language, language == selectedLanguageState, ::languageSelected)
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(items = stringsState) { item ->
                        StringItem(item)
                    }
                }
            }
        }
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
    fun LanguageRadioButton(language: String, isSelected: Boolean, onSelected: (String) -> Unit = {}) {
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

    private fun createDynoDict(): DynoDictManager {
        val json = Json {
            ignoreUnknownKeys = true
        }

        val manager =
            RemoteManagerImpl(RemoteSettings("https://raw.githubusercontent.com/mkovalyk/GraphicEditor/master/"), json)
        val bucketsStorage = FileBucketsStorage(filesDir, json)
        val metadataStorage = FileMetadataStorage(filesDir, json)
        val storageManager = StoreManagerImpl(bucketsStorage, metadataStorage)
        return DynoDictManagerImpl(manager, storageManager, object : ErrorHandler {
            override fun onErrorOccurred(ex: Exception): ExceptionResolution {
                Log.d("XXX", "errorOccurred: $ex")
                return ExceptionResolution.Handled
            }
        })
    }


    private fun startGettingMetadata() {
        lifecycleScope.launch(Dispatchers.IO) {
            dynoDict.updateTranslations()
            val meta = dynoDict.getMetadata()
            metadata.value = meta
            val selectedLang = if (selectedLanguage.value.isNotEmpty()) {
                selectedLanguage.value
            } else {
                meta?.defaultLanguage.orEmpty()
            }
            val result = dynoDict.getAllForLanguage(selectedLang)
            strings.value = result
        }
    }
}