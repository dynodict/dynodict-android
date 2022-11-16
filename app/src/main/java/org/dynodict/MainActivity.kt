package org.dynodict

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dynodict.remote.RemoteManagerImpl
import org.dynodict.remote.RemoteSettings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Button(onClick = { startGettingMetadata() }) {
                    Text(text = "Start getting translations")
                }
            }
        }
    }

    private fun startGettingMetadata() {
        lifecycleScope.launch(Dispatchers.IO) {
            val metadata =
                RemoteManagerImpl(RemoteSettings("https://raw.githubusercontent.com/mkovalyk/GraphicEditor/master/")).getMetadata()
            Log.d("RemoteManager", "Metadata: $metadata")
        }
    }
}