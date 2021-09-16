package com.rosberry.pine.ui.fullscreen

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rosberry.pine.R
import com.rosberry.pine.databinding.FragmentImageBinding
import com.rosberry.pine.ui.base.BaseFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FullscreenImageFragment() : BaseFragment<FragmentImageBinding>() {

    companion object {

        private const val IMAGE_KEY = "image_key"
    }

    private val viewModel: FullscreenImageViewModel by viewModels()

    private val permissionRequestLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    onDownloadClicked()
                }
            }

    constructor(image: FullscreenImage) : this() {
        arguments = bundleOf(
                IMAGE_KEY to image
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDownloadingObserver()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentImageBinding? =
            FragmentImageBinding.inflate(inflater, container, false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewModel.image = requireArguments().getParcelable(IMAGE_KEY)

        binding?.backButton?.setOnClickListener {
            viewModel.onBackPressed()
        }

        binding?.downloadButton?.setOnClickListener {
            onDownloadClicked()
        }

        binding?.imageName?.text = viewModel.image?.description

        setImage()

        return binding?.root
    }

    private fun setImage() {
        Picasso.get()
            .load(viewModel.image?.thumbImageUrl)
            .noPlaceholder()
            .fit()
            .into(binding?.image, object : Callback {
                override fun onSuccess() {
                    Picasso.get()
                        .load(viewModel.image?.fullImageUrl)
                        .noPlaceholder()
                        .noFade()
                        .fit()
                        .into(binding?.image)
                }

                override fun onError(e: Exception?) {
                    showSnackbar(R.string.snackbar_no_connection_title,
                            R.string.snackbar_no_connection_action) {
                        setImage()
                    }
                }
            })
    }

    private fun setDownloadingObserver() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bitmap.collect { bitmap ->
                    bitmap?.let {
                        saveBitmap(it)
                    }
                }
            }
        }
    }

    private fun saveBitmap(bitmap: Bitmap?) {
        val contentValues = ContentValues().apply {
            val relativeLocation = Environment.DIRECTORY_PICTURES
            put(MediaStore.MediaColumns.DISPLAY_NAME, viewModel.image?.id)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = requireActivity().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { _ ->
            val stream = resolver?.openOutputStream(uri)

            stream?.let { _ ->
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                hideDownloadProgress()
            }
        }
    }

    private fun showDownloadProgress() {
        binding?.downloadProgressBar?.isVisible = true
        binding?.downloadButton?.isVisible = false
    }

    private fun hideDownloadProgress() {
        binding?.downloadProgressBar?.isVisible = false
        binding?.downloadButton?.isVisible = true
    }

    private fun onDownloadClicked() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionRequestLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            viewModel.downloadImage()
            showDownloadProgress()
        }
    }
}