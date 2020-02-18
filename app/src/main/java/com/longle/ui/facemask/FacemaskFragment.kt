package com.longle.ui.facemask

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.longle.R
import com.longle.databinding.FragmentFaceMaskBinding
import com.longle.di.Injectable
import com.longle.di.ViewModelFactory
import com.longle.util.autoCleared
import com.longle.util.hide
import com.longle.util.show
import javax.inject.Inject

class FacemaskFragment : Fragment(R.layout.fragment_face_mask), Injectable {

    @Inject
    lateinit var factory: ViewModelFactory<FacemaskViewModel>

    private val viewModel: FacemaskViewModel by viewModels { factory }
    private var binding by autoCleared<FragmentFaceMaskBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView(view)
        subscribeUi()
    }

    private fun setUpView(view: View) {
        binding = FragmentFaceMaskBinding.bind(view)
        binding?.webView?.webChromeClient = WebChromeClient()
        binding?.webView?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding?.progressBar?.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding?.progressBar?.hide()
            }
        }
    }

    private fun subscribeUi() {
    }

    override fun onDestroyView() {
        binding?.webView?.stopLoading()
        super.onDestroyView()
    }
}
