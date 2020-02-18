package com.longle.ui.statistic

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.longle.R
import com.longle.databinding.FragmentStatisticBinding
import com.longle.util.autoCleared

/**
 * A fragment representing about me
 */
class StatisticFragment : Fragment(R.layout.fragment_statistic) {

    private val MAX_PROGRESS = 100
    private val aboutUrl = "https://corona.kompa.ai/"
    private var binding by autoCleared<FragmentStatisticBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView(view)
        loadData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpView(view: View) {
        binding = FragmentStatisticBinding.bind(view)
        binding?.webView?.settings?.javaScriptEnabled = true
        binding?.webView?.settings?.loadWithOverviewMode = true
        binding?.webView?.settings?.useWideViewPort = true
        binding?.webView?.settings?.domStorageEnabled = true
        binding?.webView?.webViewClient = object : WebViewClient() {
            override
            fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        }
        binding?.webView?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(
                view: WebView,
                newProgress: Int
            ) {
                super.onProgressChanged(view, newProgress)
                if (newProgress < MAX_PROGRESS) {
                    binding?.swipeRefresh?.isRefreshing = true
                }
                if (newProgress == MAX_PROGRESS) {
                    binding?.swipeRefresh?.postDelayed({
                        binding?.swipeRefresh?.isRefreshing = false
                    }, 1000)
                }
            }
        }


        binding?.swipeRefresh?.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        binding?.webView?.stopLoading()
        binding?.webView?.loadUrl(aboutUrl)
    }

    override fun onDestroyView() {
        binding?.webView?.stopLoading()
        super.onDestroyView()
    }
}
