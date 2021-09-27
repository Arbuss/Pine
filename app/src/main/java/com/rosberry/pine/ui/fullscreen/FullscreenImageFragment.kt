package com.rosberry.pine.ui.fullscreen

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rosberry.pine.R
import com.rosberry.pine.data.repository.model.Image
import com.rosberry.pine.databinding.FragmentImageBinding
import com.rosberry.pine.extension.getScreenWidth
import com.rosberry.pine.extension.share
import com.rosberry.pine.ui.base.BaseFragment
import com.rosberry.pine.util.ImageUtil
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    constructor(image: Image) : this() {
        arguments = bundleOf(
                IMAGE_KEY to image
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDownloadingObserver()
        setSharingObserver()
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

        binding?.shareButton?.setOnClickListener {
            shareImageUrl()
        }

        binding?.likeButton?.setOnClickListener {
            viewModel.like()
        }

        binding?.imageName?.text = viewModel.image?.description

        setImage()

        return binding?.root
    }

    private fun setImage() {
        val (width, height) = ImageUtil.calcImageSize(getScreenWidth(), viewModel.image!!.width,
                viewModel.image!!.height)
        Picasso.get()
            .load(viewModel.image?.urls?.thumb)
            .noPlaceholder()
            .resize(width, height)
            .into(binding?.image, object : Callback {
                override fun onSuccess() {
                    Picasso.get()
                        .load(viewModel.image?.urls?.raw)
                        .noPlaceholder()
                        .noFade()
                        .resize(width, height)
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
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.bitmap.collect { bitmap ->
                    bitmap?.let {
                        saveBitmap(it)
                    }
                }
            }
        }
    }

    private fun setSharingObserver() {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.sharingItemAddress.collect { address ->
                    address?.let {
                        share(address, context)
                        hideSharingProgress()
                    }
                }
            }
        }
    }

    private fun saveBitmap(bitmap: Bitmap?) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, viewModel.image?.id)
            put(MediaStore.Images.Media.DISPLAY_NAME, viewModel.image?.id + "raw")
            put(MediaStore.Images.Media.DESCRIPTION, viewModel.image?.description)
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val resolver = requireActivity().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { _ ->
            val stream = resolver?.openOutputStream(uri)

            stream?.let { _ ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stream.flush()
                    withContext(Dispatchers.Main) {
                        hideDownloadProgress()
                    }
                }
            }
        }
    }

    private fun showDownloadProgress() {
        binding?.downloadProgressBar?.isVisible = true
        binding?.downloadButton?.isInvisible = true
    }

    private fun hideDownloadProgress() {
        binding?.downloadProgressBar?.isVisible = false
        binding?.downloadButton?.isInvisible = false
    }

    private fun showSharingProgress() {
        binding?.shareProgressBar?.isVisible = true
        binding?.shareButton?.isInvisible = true
    }

    private fun hideSharingProgress() {
        binding?.shareProgressBar?.isVisible = false
        binding?.shareButton?.isInvisible = false
    }

    private fun shareImageUrl() {
        showSharingProgress()
        requireContext().cacheDir?.let {
            viewModel.saveBitmapToCache(it)
        }
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