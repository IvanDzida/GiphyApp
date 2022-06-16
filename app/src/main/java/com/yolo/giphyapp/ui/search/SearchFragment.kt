package com.yolo.giphyapp.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yolo.data.GiphyItem
import com.yolo.giphyapp.R
import com.yolo.giphyapp.databinding.FragmentSearchBinding
import com.yolo.giphyapp.ui.shared.GifAdapter
import com.yolo.giphyapp.util.EndlessScrollListener
import com.yolo.giphyapp.util.State
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), GifAdapter.OnItemClickListener {

    private val args by navArgs<SearchFragmentArgs>()

    private val viewModel by viewModels<SearchViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    lateinit var query: String

    lateinit var adapter: GifAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        query = args.query

        adapter = GifAdapter(this, mutableListOf())

        binding.apply {
            swipeToRefresh.setOnRefreshListener {
                viewModel.updateState(State.SWIPE_TO_REFRESH)
                adapter.clearData()
                viewModel.search(query, isInitial = true)
            }
            recyclerView.setHasFixedSize(true)
            recyclerView.itemAnimator = null
            recyclerView.adapter = adapter

            retryButton.setOnClickListener {
                viewModel.search(query, isInitial = true)
            }
        }
        addEndlessScrollListener()



        viewModel.search(query, isInitial = true)
        observeState()
        observeGifs()
    }

    override fun onItemClick(photo: GiphyItem) {
        val action = SearchFragmentDirections.fromSearchToDetailsFragment(photo)
        findNavController().navigate(action)
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
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
            if (it.isEmpty()) {
                // Some error happened, emit ERROR state
                viewModel.updateState(State.ERROR)
            } else {
                adapter.appendData(it)
            }
        }
    }

    private fun addEndlessScrollListener() {
        binding.recyclerView.addOnScrollListener(object : EndlessScrollListener(
            binding.recyclerView.layoutManager as GridLayoutManager
        ) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.search(query)
                viewModel.updateState(State.LOAD_MORE)
            }
        })
    }
}
