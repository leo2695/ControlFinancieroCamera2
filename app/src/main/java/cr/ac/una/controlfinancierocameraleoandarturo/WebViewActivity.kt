package cr.ac.una.controlfinancierocameraleoandarturo

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_web_view_activity)

        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url ?: "")
                return true
            }
        }
        webView.settings.javaScriptEnabled = true

        val title = intent.getStringExtra("title")
        val url = intent.getStringExtra("url")
        Log.d("WebViewActivity", "URL recibida: $url")
        Log.d("WebViewActivity", "Title recibido: $title")

        if (title != null && url == null) {
            supportActionBar?.title = title
            val title_url = "https://es.wikipedia.org/wiki/${title}"
            webView.loadUrl(title_url) } else if (url != null) {
            webView.loadUrl(url)
        } else {
            Log.e("WebViewActivity", "Título o descripción son null")
            Toast.makeText(this, "Datos incompletos. No se puede cargar la página.", Toast.LENGTH_SHORT).show()
        }
    }
}
