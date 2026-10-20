package com.sagarwifi.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var errorPanel: LinearLayout

    companion object {
        private const val HOME_URL = "https://sagarwifi.blogspot.com/?m=1"
        // Website template context includes this WhatsApp contact. Change if the business number changes.
        private const val WHATSAPP_NUMBER = "919727341942"
        private const val NOTIFICATION_PERMISSION_REQUEST = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        buildScreen()
        configureWebView()
        if (savedInstanceState == null) {
            val notificationUrl = intent.getStringExtra("notification_url")
            webView.loadUrl(validBlogUrl(notificationUrl) ?: HOME_URL)
        } else {
            webView.restoreState(savedInstanceState)
        }
        requestNotificationPermissionIfNeeded()
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(8, 12, 22))
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(8), dp(12), dp(8))
            setBackgroundColor(Color.rgb(10, 20, 40))
        }
        val title = TextView(this).apply {
            text = "SAGAR WIFI"
            textSize = 20f
            setTextColor(Color.rgb(64, 169, 255))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        header.addView(title, LinearLayout.LayoutParams(0, dp(48), 1f))
        val homeButton = Button(this).apply {
            text = "HOME"
            setTextColor(Color.WHITE)
            setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(20, 100, 210)))
            setOnClickListener { webView.loadUrl(HOME_URL) }
        }
        header.addView(homeButton)

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(6), dp(2), dp(6), dp(2))
            setBackgroundColor(Color.rgb(14, 24, 43))
        }
        val refresh = Button(this).apply {
            text = "↻ Refresh"
            setTextColor(Color.WHITE)
            setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(27, 56, 91)))
            setOnClickListener { webView.reload() }
        }
        val whatsapp = Button(this).apply {
            text = "WhatsApp"
            setTextColor(Color.WHITE)
            setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(13, 115, 85)))
            setOnClickListener { openWhatsApp() }
        }
        actions.addView(refresh, LinearLayout.LayoutParams(0, dp(44), 1f))
        actions.addView(whatsapp, LinearLayout.LayoutParams(0, dp(44), 1f))

        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            isIndeterminate = true
            progressTintList = android.content.res.ColorStateList.valueOf(Color.rgb(40, 140, 255))
            visibility = View.GONE
        }
        webView = WebView(this)
        errorPanel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(dp(24), dp(24), dp(24), dp(24))
            setBackgroundColor(Color.rgb(8, 12, 22))
            visibility = View.GONE
            addView(TextView(this@MainActivity).apply {
                text = "Connection issue"
                textSize = 19f
                setTextColor(Color.WHITE)
            })
            addView(TextView(this@MainActivity).apply {
                text = "Internet check karke dobara try karein."
                setPadding(0, dp(8), 0, dp(12))
                setTextColor(Color.LTGRAY)
            })
            addView(Button(this@MainActivity).apply {
                text = "Try again"
                setOnClickListener { errorPanel.visibility = View.GONE; webView.visibility = View.VISIBLE; webView.reload() }
            })
        }

        root.addView(header)
        root.addView(actions)
        root.addView(progress, LinearLayout.LayoutParams(-1, dp(3)))
        root.addView(webView, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(errorPanel, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun configureWebView() {
        webView.setBackgroundColor(Color.rgb(8, 12, 22))
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            allowFileAccess = false
            allowContentAccess = false
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
        }
        CookieManager.getInstance().setAcceptCookie(true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progress.visibility = if (newProgress in 1..99) View.VISIBLE else View.GONE
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                val scheme = uri.scheme ?: return true
                if (scheme == "http" || scheme == "https") {
                    val host = uri.host.orEmpty().lowercase()
                    if (host == "sagarwifi.blogspot.com" || host.endsWith(".blogspot.com")) return false
                    return openExternal(uri)
                }
                return openExternal(uri)
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                errorPanel.visibility = View.GONE
                webView.visibility = View.VISIBLE
            }
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) {
                    webView.visibility = View.GONE
                    errorPanel.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun validBlogUrl(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return try {
            val uri = Uri.parse(raw)
            if (uri.scheme == "https" && uri.host == "sagarwifi.blogspot.com") uri.toString() else null
        } catch (_: Exception) { null }
    }

    private fun openExternal(uri: Uri): Boolean {
        return try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
            true
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Is link ko kholne ke liye app nahi mila", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun openWhatsApp() {
        val uri = Uri.parse("https://wa.me/$WHATSAPP_NUMBER")
        openExternal(uri)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SagarFirebaseMessagingService.CHANNEL_ID,
                "Sagar WiFi updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Sagar WiFi offers and website updates" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        validBlogUrl(intent.getStringExtra("notification_url"))?.let { webView.loadUrl(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Android 13; kept for older Android back navigation.")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
