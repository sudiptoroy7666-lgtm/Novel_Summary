package com.example.novel_summary.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.novel_summary.R
import com.example.novel_summary.data.model.Bookmark
import com.example.novel_summary.data.model.History
import com.example.novel_summary.databinding.ActivityMainBinding
import com.example.novel_summary.ui.viewmodel.MainViewModel
import com.example.novel_summary.utils.ContentHolder
import com.example.novel_summary.utils.ToastUtils
import com.example.novel_summary.utils.UrlUtils
import com.example.novel_summary.utils.WebViewUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private var currentWebViewJob: Job? = null
    private var isBookmarked = false
    private var isLoading = false
    private var isExitDialogShowing = false
    // ADDED: Store WebView state
    private var webViewUrl: String? = null
    private var webViewTitle: String? = null

    companion object {
        private const val HOME_URL = "https://app.home/"
    }

    // Popular Novel Sites
    private val novelSites = mapOf(
        "WebNovel" to "https://www.webnovel.com",
        "WuxiaWorld" to "https://www.wuxiaworld.com",
        "Royal Road" to "https://www.royalroad.com",
        "Novel Updates" to "https://www.novelupdates.com",
        "BoxNovel" to "https://boxnovel.com",
        "Light Novel Pub" to "https://www.lightnovelpub.com",
        "NovelFull" to "https://novelfull.com",
        "Read Light Novel" to "https://www.readlightnovel.org"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebView()
        setupSearchBar()
        setupNavigationButtons()
        setupSummarizeButton()
        setupBookmarkButton()
        setupHomeButton()
        setupRefreshButton() // ADD THIS LINE
        setupBackPressedCallback()
// NEW:

        // Load home page by default
        intent.getStringExtra("SELECTED_URL")?.let { url ->
            webViewUrl = url
            loadUrl(url)
        } ?: loadHomePage()

    }


    // Add this method to MainActivity class
    private fun setupRefreshButton() {
        binding.btnRefresh.setOnClickListener {
            // Reload current page
            binding.webView.reload()
            ToastUtils.showShort(this, "Refreshing page...")
        }

        // Optional: Update refresh button visibility based on loading state
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.progressBar.progress = newProgress

                // Optional: Disable refresh while loading
                binding.btnRefresh.isEnabled = newProgress == 100
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.getStringExtra("SELECTED_URL")?.let { url ->
            webViewUrl = url
            loadUrl(url)
        }
    }

    private fun setupWebView() {
        WebViewUtils.configureWebView(binding.webView)

        // FIXED: Set WebViewClient BEFORE loading any content
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isLoading = true
                binding.progressBar.isVisible = true
                binding.progressBar.progress = 0
                updateBookmarkState(url)
                updateNavigationButtons()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoading = false
                binding.progressBar.isVisible = false
                binding.progressBar.progress = 100

                webViewUrl = url
                webViewTitle = view?.title

                currentWebViewJob?.cancel()
                currentWebViewJob = CoroutineScope(Dispatchers.Main).launch {
                    view?.evaluateJavascript("(function() { return document.title; })()") { title ->
                        val pageTitle = if (title == "\"\"" || title == "null") {
                            UrlUtils.extractTitleFromUrl(url ?: "")
                        } else {
                            title.trim('"')
                        }

                        if (url != null && !isHomePageUrl(url)) {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.saveToHistory(
                                    History(
                                        url = url,
                                        title = pageTitle
                                    )
                                )
                            }
                        }
                    }
                }

                runOnUiThread {
                    binding.searchEditText.setText(if (url != null && !isHomePageUrl(url)) url else "")
                    updateBookmarkState(url)
                    updateNavigationButtons()
                    binding.btnSummarize.isEnabled = url != null && !isHomePageUrl(url)
                }
            }

            override fun onReceivedError(view: WebView?, request: android.webkit.WebResourceRequest?, error: android.webkit.WebResourceError?) {
                super.onReceivedError(view, request, error)

                // FIXED: Only show error for main frame failures, not resource loading errors
                if (request?.isForMainFrame == true) {
                    runOnUiThread {
                        ToastUtils.showError(this@MainActivity, "Failed to load page: ${error?.description}")
                        binding.progressBar.isVisible = false
                        isLoading = false
                    }
                }
                // Don't show toast for non-main frame errors (like ORB, blocked resources, etc.)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false

                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } catch (e: Exception) {
                        ToastUtils.showError(this@MainActivity, "Cannot open this link")
                    }
                    return true
                }

                return false
            }
        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.progressBar.progress = newProgress
            }
        }
    }


    private fun setupSearchBar() {
        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val input = v.text.toString().trim()
                if (input.isNotEmpty()) {
                    val url = UrlUtils.normalizeUrl(input)
                    loadUrl(url)
                    hideKeyboard()
                    return@setOnEditorActionListener true
                }
                false
            } else {
                false
            }
        }

        binding.btnGo.setOnClickListener {
            val input = binding.searchEditText.text.toString().trim()
            if (input.isNotEmpty()) {
                val url = UrlUtils.normalizeUrl(input)
                loadUrl(url)
                hideKeyboard()
            }
        }
    }

    private fun setupNavigationButtons() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_back -> {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()

                    } else {
                        ToastUtils.showShort(this, "No previous page")
                    }
                    true
                }
                R.id.menu_forward -> {
                    if (binding.webView.canGoForward()) {
                        binding.webView.goForward()

                    } else {
                        ToastUtils.showShort(this, "No forward page")
                    }
                    true
                }
                R.id.menu_bookmarks -> {
                    val intent = Intent(this, Activity_Boolmarks::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_history -> {
                    val intent = Intent(this, Activity_History::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_library -> {
                    startActivity(Intent(this, Activity_Library::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupBookmarkButton() {
        binding.btnBookmark.setOnClickListener {
            // FIXED: Check current URL instead of flag
            val currentUrl = binding.webView.url
            if (currentUrl != null && isHomePageUrl(currentUrl)) {
                ToastUtils.showShort(this, "Cannot bookmark home page")
                return@setOnClickListener
            }

            val url = currentUrl ?: return@setOnClickListener
            val currentTitle = binding.webView.title ?: UrlUtils.extractTitleFromUrl(url)

            if (isBookmarked) {
                // Remove bookmark - database operation on IO thread
                showConfirmationDialog(
                    "Remove Bookmark",
                    "Are you sure you want to remove this bookmark?"
                ) {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.removeBookmark(url)
                        runOnUiThread {
                            isBookmarked = false
                            binding.btnBookmark.setImageResource(android.R.drawable.ic_menu_set_as)
                            ToastUtils.showSuccess(this@MainActivity, "Bookmark removed")
                        }
                    }
                }
            } else {
                // Add bookmark - database operation on IO thread
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.saveBookmark(
                        Bookmark(
                            url = url,
                            title = currentTitle
                        )
                    )
                    runOnUiThread {
                        isBookmarked = true
                        binding.btnBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                        ToastUtils.showSuccess(this@MainActivity, "Page bookmarked successfully")
                    }
                }
            }
        }
    }

    private fun setupHomeButton() {
        binding.btnHome.setOnClickListener {
            loadHomePage()
        }
    }

    private fun setupSummarizeButton() {
        binding.btnSummarize.setOnClickListener {
            // Check current URL
            val currentUrl = binding.webView.url
            if (currentUrl != null && isHomePageUrl(currentUrl)) {
                ToastUtils.showShort(this, "Cannot summarize home page")
                return@setOnClickListener
            }

            if (isLoading) {
                ToastUtils.showShort(this, "Please wait for the page to load completely")
                return@setOnClickListener
            }

            val url = currentUrl ?: return@setOnClickListener

            // Show loading dialog
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Extracting Content")
                .setMessage("Please wait while we extract the content...")
                .setCancelable(false)
                .create()
            dialog.show()

            // Extract content using JavaScript
            CoroutineScope(Dispatchers.Main).launch {
                binding.webView.evaluateJavascript(WebViewUtils.extractCleanText(binding.webView)) { content ->
                    dialog.dismiss()

                    if (content != "\"\"" && content != "null" && content.length > 100) {
                        val cleanContent = content.trim('"')
                        val pageTitle = binding.webView.title ?: "Untitled"

                        // ✅ FIXED: Use ContentHolder instead of Intent extras for large content
                        ContentHolder.setContent(
                            content = cleanContent,
                            url = url,
                            title = pageTitle
                        )

                        // Start activity with minimal Intent data
                        val intent = Intent(this@MainActivity, ActivitySummary::class.java)
                        startActivity(intent)
                    } else {
                        runOnUiThread {
                            ToastUtils.showError(this@MainActivity, "Could not extract content. Please try a different page.")
                        }
                    }
                }
            }
        }
    }

    private fun setupBackPressedCallback() {
        // Modern back button handling using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // FIXED: Check current URL to determine if on home page
                val currentUrl = binding.webView.url
                val isOnHomePage = currentUrl != null && isHomePageUrl(currentUrl)

                if (isOnHomePage) {
                    // Show exit confirmation dialog when on homepage
                    if (!isExitDialogShowing) {
                        showExitConfirmationDialog()
                    }
                } else if (binding.webView.canGoBack()) {
                    binding.webView.goBack()

                } else {
                    loadHomePage()
                }
            }
        })
    }

    private fun loadUrl(url: String) {
        if (UrlUtils.isValidUrl(url)) {
            binding.btnSummarize.isEnabled = false
            binding.webView.loadUrl(url)
        } else {
            ToastUtils.showError(this, "Invalid URL")
        }
    }

    private fun updateBookmarkState(url: String?) {
        if (url == null) return
        if (isHomePageUrl(url)) {
            isBookmarked = false
            binding.btnBookmark.setImageResource(android.R.drawable.ic_menu_set_as)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val bookmark = viewModel.getBookmarkByUrl(url)
            runOnUiThread {
                isBookmarked = bookmark != null
                binding.btnBookmark.setImageResource(
                    if (isBookmarked) android.R.drawable.btn_star_big_on
                    else android.R.drawable.ic_menu_set_as
                )
            }
        }
    }

    private fun updateNavigationButtons() {
        // Update button states based on WebView history
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
        imm?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun showNavigationToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showConfirmationDialog(
        title: String,
        message: String,
        positiveAction: () -> Unit
    ) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ -> positiveAction() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showExitConfirmationDialog() {
        isExitDialogShowing = true
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Exit Application")
            .setMessage("Are you sure you want to exit the app?")
            .setPositiveButton("Exit") { _, _ ->
                isExitDialogShowing = false
                finishAffinity() // Close all activities
            }
            .setNegativeButton("Cancel") { _, _ ->
                isExitDialogShowing = false
            }
            .setOnCancelListener {
                isExitDialogShowing = false
            }
            .show()
    }

    // FIXED: Helper function to check if URL is home page
    private fun isHomePageUrl(url: String?): Boolean {
        return url == HOME_URL || url == "about:blank"
    }

    // FIXED: Load custom home page - Removed Features section
    private fun loadHomePage() {
        // Clear search field immediately
        binding.searchEditText.setText("")
        binding.btnBookmark.setImageResource(android.R.drawable.ic_menu_set_as)
        isBookmarked = false

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>WebNovel Summarizer - Home</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        padding: 20px;
                        color: #333;
                        min-height: 100vh;
                    }
                    
                    .container {
                        max-width: 1200px;
                        margin: 0 auto;
                    }
                    
                    .header {
                        text-align: center;
                        margin-bottom: 40px;
                        padding: 30px;
                        background: rgba(255, 255, 255, 0.95);
                        border-radius: 15px;
                        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
                    }
                    
                    h1 {
                        font-size: 2.5em;
                        color: #667eea;
                        margin-bottom: 10px;
                        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
                    }
                    
                    .subtitle {
                        font-size: 1.2em;
                        color: #666;
                        margin-top: 10px;
                    }
                    
                    .search-box {
                        margin: 30px 0;
                        padding: 20px;
                        background: rgba(255, 255, 255, 0.95);
                        border-radius: 15px;
                        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
                    }
                    
                    .search-box h2 {
                        text-align: center;
                        color: #667eea;
                        margin-bottom: 15px;
                    }
                    
                    .search-input {
                        width: 100%;
                        padding: 15px;
                        border: 2px solid #667eea;
                        border-radius: 8px;
                        font-size: 16px;
                        outline: none;
                        transition: all 0.3s;
                    }
                    
                    .search-input:focus {
                        border-color: #764ba2;
                        box-shadow: 0 0 10px rgba(102, 126, 234, 0.3);
                    }
                    
                    .section {
                        margin: 30px 0;
                        background: rgba(255, 255, 255, 0.95);
                        padding: 25px;
                        border-radius: 15px;
                        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
                    }
                    
                    .section h2 {
                        color: #667eea;
                        margin-bottom: 20px;
                        font-size: 1.8em;
                        border-bottom: 3px solid #667eea;
                        padding-bottom: 10px;
                    }
                    
                    .sites-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
                        gap: 20px;
                    }
                    
                    .site-card {
                        background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
                        padding: 20px;
                        border-radius: 10px;
                        text-align: center;
                        transition: all 0.3s;
                        cursor: pointer;
                        border: 2px solid transparent;
                    }
                    
                    .site-card:hover {
                        transform: translateY(-5px);
                        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
                        border-color: #667eea;
                    }
                    
                    .site-card h3 {
                        color: #667eea;
                        font-size: 1.2em;
                        margin-bottom: 10px;
                    }
                    
                    .site-card p {
                        color: #666;
                        font-size: 0.9em;
                        line-height: 1.5;
                    }
                    
                    @media (max-width: 768px) {
                        .sites-grid {
                            grid-template-columns: 1fr;
                        }
                        
                        h1 {
                            font-size: 2em;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📖 WebNovel Summarizer</h1>
                        <p class="subtitle">Your AI-powered companion for reading and summarizing web novels</p>
                    </div>
                    
                    <div class="search-box">
                        <h2>🔍 Search or Enter URL</h2>
                        <input type="text" class="search-input" id="searchInput" placeholder="Search Google or enter a URL..." onkeypress="handleSearch(event)">
                    </div>
                    
                    <div class="section">
                        <h2>📚 Popular Novel Sites</h2>
                        <div class="sites-grid">
                            <div class="site-card" onclick="openSite('https://www.webnovel.com')">
                                <h3>WebNovel</h3>
                                <p>Official translations and original novels</p>
                            </div>
                            <div class="site-card" onclick="openSite('https://www.wuxiaworld.com')">
                                <h3>WuxiaWorld</h3>
                                <p>Chinese fantasy novels and web novels</p>
                            </div>
                            <div class="site-card" onclick="openSite('https://www.royalroad.com')">
                                <h3>Royal Road</h3>
                                <p>Web serials and original fiction</p>
                            </div>
                            <div class="site-card" onclick="openSite('https://www.novelupdates.com')">
                                <h3>Novel Updates</h3>
                                <p>Directory of translated novels</p>
                            </div>
                            <div class="site-card" onclick="openSite('https://boxnovel.com')">
                                <h3>BoxNovel</h3>
                                <p>Chinese and Korean novels</p>
                            </div>
                            <div class="site-card" onclick="openSite('https://www.lightnovelpub.com')">
                                <h3>Light Novel Pub</h3>
                                <p>Light novels and web novels</p>
                            </div>
                            <div class="site-card" onclick="openSite('https://novelfull.com')">
                                <h3>NovelFull</h3>
                                <p>Complete novels and translations</p>
                            </div>
                            <div class="site-card" onclick="openSite('https://www.readlightnovel.org')">
                                <h3>Read Light Novel</h3>
                                <p>Light novels and translations</p>
                            </div>
                        </div>
                    </div>
                </div>
                
                <script>
                    function handleSearch(event) {
                        if (event.key === 'Enter') {
                            const input = document.getElementById('searchInput').value.trim();
                            if (input) {
                                Android.search(input);
                            }
                        }
                    }
                    
                    function openSite(url) {
                        Android.openUrl(url);
                    }
                    
                    // Focus search input on load
                    document.addEventListener('DOMContentLoaded', function() {
                        const input = document.getElementById('searchInput');
                        if (input) {
                            input.focus();
                        }
                    });
                </script>
            </body>
            </html>
        """.trimIndent()

        // FIXED: Use loadDataWithBaseURL with HOME_URL to track home page state
        binding.webView.loadDataWithBaseURL(
            HOME_URL,
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )

        // Add JavaScript interface for communication
        binding.webView.addJavascriptInterface(object {
            @android.webkit.JavascriptInterface
            fun search(query: String) {
                runOnUiThread {
                    val url = UrlUtils.normalizeUrl(query)
                    loadUrl(url)
                }
            }

            @android.webkit.JavascriptInterface
            fun openUrl(url: String) {
                runOnUiThread {
                    loadUrl(url)
                }
            }
        }, "Android")
    }


    override fun onResume() {
        super.onResume()
        // Restore WebView state
        if (webViewUrl != null && !isHomePageUrl(webViewUrl!!)) {
            updateBookmarkState(webViewUrl)
        }
    }

    override fun onDestroy() {
        currentWebViewJob?.cancel()
        binding.webView.destroy()
        super.onDestroy()
    }


}