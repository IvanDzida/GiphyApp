package com.yolo.giphyapp.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.yolo.data.GiphyItem
import com.yolo.giphyapp.MainActivity
import com.yolo.giphyapp.R
import com.yolo.giphyapp.api.UploadApi
import com.yolo.giphyapp.api.UploadRequestBody
import com.yolo.giphyapp.api.UploadResponse
import com.yolo.giphyapp.databinding.FragmentHomeBinding
import com.yolo.giphyapp.ui.custom.UploadProgress
import com.yolo.giphyapp.ui.shared.GifAdapter
import com.yolo.giphyapp.util.EndlessScrollListener
import com.yolo.giphyapp.util.State
import com.yolo.giphyapp.util.getFileName
import com.yolo.giphyapp.util.snackbar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), GifAdapter.OnItemClickListener, UploadRequestBody.UploadCallback {

    private val viewModel by viewModels<HomeViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: GifAdapter

    var selectedVideo: Uri? = null

    lateinit var uploadProgress: UploadProgress

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent!!.data != null) {
                    selectedVideo = intent.data

                    uploadVideo()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        adapter = GifAdapter(this, mutableListOf())
        binding.apply {
            swipeToRefresh.setOnRefreshListener {
                viewModel.updateState(State.SWIPE_TO_REFRESH)
                adapter.clearData()
                viewModel.trending(isInitial = true)
            }

            recyclerView.setHasFixedSize(true)
            recyclerView.itemAnimator = null
            recyclerView.adapter = adapter

            retryButton.setOnClickListener {
                viewModel.trending(isInitial = true)
            }

            fab.setOnClickListener {
                onUploadClick()
            }
        }

        addEndlessScrollListener()
        observeState()
        observeGifs()

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_home, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchView.clearFocus()
                    val action = HomeFragmentDirections.toSearchFragment(query)
                    findNavController().navigate(action)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(photo: GiphyItem) {
        val action = HomeFragmentDirections.toDetailsFragment(photo)
        findNavController().navigate(action)
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) {
            when(it) {
                State.INITIAL -> {
                    binding.apply {
                        swipeToRefresh.isRefreshing = false
                        recyclerView.isVisible = false
                        retryLayout.isVisible = false
                        initialProgressBar.isVisible = true
                        loadMoreProgressBar.isVisible = false
                    }
                }
                State.LOAD_MORE -> {
                    binding.apply {
                        swipeToRefresh.isRefreshing = false
                        recyclerView.isVisible = true
                        retryLayout.isVisible = false
                        initialProgressBar.isVisible = false
                        loadMoreProgressBar.isVisible = true
                    }
                }
                State.SWIPE_TO_REFRESH -> {
                    binding.apply {
                        swipeToRefresh.isRefreshing = true
                        recyclerView.isVisible = false
                        retryLayout.isVisible = false
                        initialProgressBar.isVisible = false
                        loadMoreProgressBar.isVisible = false
                    }
                }
                State.READY -> {
                    binding.apply {
                        swipeToRefresh.isRefreshing = false
                        recyclerView.isVisible = true
                        retryLayout.isVisible = false
                        initialProgressBar.isVisible = false
                        loadMoreProgressBar.isVisible = false
                    }
                }
                State.ERROR -> {
                    binding.apply {
                        swipeToRefresh.isRefreshing = false
                        recyclerView.isVisible = false
                        retryLayout.isVisible = true
                        initialProgressBar.isVisible = false
                        loadMoreProgressBar.isVisible = false
                    }
                }
                else -> {}
            }
        }
    }

    private fun observeGifs() {
        viewModel.gifs.observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                // Some error happened, emit ERROR state
                viewModel.updateState(State.ERROR)
            }

            adapter.appendData(it)
        }
    }

    private fun addEndlessScrollListener() {
        binding.recyclerView.addOnScrollListener(object : EndlessScrollListener(
            binding.recyclerView.layoutManager as GridLayoutManager
        ) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.trending()
                viewModel.updateState(State.LOAD_MORE)
            }
        })
    }

    private fun onUploadClick() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "video/*"
            startForResult.launch(it)
        }
    }

    private fun uploadVideo() {
        if (selectedVideo == null) {
            binding.root.snackbar(resources.getString(R.string.video_not_selected))
            return
        }

        val parcelFileDescriptor =
            requireContext().contentResolver.openFileDescriptor(selectedVideo!!, "r", null) ?: return

        val file = File(requireContext().cacheDir, requireContext().contentResolver.getFileName(selectedVideo!!))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)
        val body = UploadRequestBody(file, "video", this)
        showProgress()
        UploadApi().uploadVideo(
            MultipartBody.Part.createFormData("file", file.name, body),
            RequestBody.create(MediaType.parse("multipart/form-data"), "json")
        ).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful) {
                    binding.root.snackbar(resources.getString(R.string.successfully_uploaded))
                } else {
                    binding.root.snackbar(resources.getString(R.string.something_went_wrong))
                }
                hideProgress()
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                binding.root.snackbar(t.message!!)
                hideProgress()
            }
        })
    }

    override fun onProgressUpdate(percentage: Int) {
        uploadProgress.updateProgress(percentage)
    }

    private fun showProgress() {
        uploadProgress = UploadProgress((activity as MainActivity?)!!)
        uploadProgress.startLoading()
    }

    private fun hideProgress() {
        uploadProgress.dismiss()
    }
}