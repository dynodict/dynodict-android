package org.dynodict

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dynodict.model.Bucket
import org.dynodict.remote.MetadataResponse
import org.dynodict.remote.RemoteManagerImpl
import org.dynodict.remote.RemoteSettings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Button(onClick = { startGettingMetadata() }) {
                Text(text = "Start getting translations")
            }
        }
    }

    private fun startGettingMetadata() {
        lifecycleScope.launch(Dispatchers.IO) {
            val manager =
                RemoteManagerImpl(RemoteSettings("https://raw.githubusercontent.com/mkovalyk/GraphicEditor/master/"))
            val metadata = manager.getMetadata()
            val string = manager.getStrings(metadata!!)
            Log.d("RemoteManager", "Metadata: $metadata")
            Log.d("RemoteManager", "String: $string")
        }
    }

//    private fun MetadataResponse.toListOfBucketInfo(): List<Bucket> {
//        return buckets.flatMap { bucket ->
//            bucket.languages.map { bucket.copy(languages = listOf(it)) }
//        }.map { Bucket(it.editionVersion, it.name, it.languages[0], it.schemeVersion) }
//    }
}