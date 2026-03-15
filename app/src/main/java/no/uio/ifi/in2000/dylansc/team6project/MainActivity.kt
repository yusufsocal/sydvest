package no.uio.ifi.in2000.dylansc.team6project


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OpenLayersMapScreen()
        }
    }
}

// La
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OpenLayersMapScreen() {
    AndroidView(
        factory = { context ->
            WebView.setWebContentsDebuggingEnabled(true)

            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        Log.e("MapWebView", "Error: ${error?.description} URL: ${request?.url}")
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(msg: ConsoleMessage?): Boolean {
                        Log.d("MapWebView", "${msg?.messageLevel()}: ${msg?.message()}")
                        return true
                    }
                }

                settings.javaScriptEnabled = true
                settings.userAgentString = settings.userAgentString + " MapApp/1.0"
                settings.domStorageEnabled = true
                settings.allowContentAccess = true
                settings.mixedContentMode =
                    android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW


                loadUrl("file:///android_asset/map.html")
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}