package com.thewyp.android.photogallery

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private val viewModel by viewModels<PhotoGalleryViewModel>()

    private lateinit var adapter: PhotoAdapter

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)
        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d(TAG, "onQueryTextSubmit: $query")
                    viewModel.searchPhtots(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    Log.d(TAG, "onQueryTextChange: $newText")
                    return false
                }
            })
            setOnClickListener {
                searchView.setQuery(viewModel.searchTerm, false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                viewModel.fetchInterestingnessPhotos(1)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(view.context, 3)

        progressBar = view.findViewById(R.id.progressBar)

        adapter = PhotoAdapter()

        recyclerView.adapter = adapter

        viewModel.photosLiveData.observe(
            viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    adapter.submitList(it.data)
                }
                else -> progressBar.visibility = View.GONE
            }
        }

        viewModel.searchPhotosLiveData.observe(
            viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    adapter.submitList(it.data)
                }
                else -> progressBar.visibility = View.GONE
            }
        }

        viewModel.fetchInterestingnessPhotos(1)
    }

    inner class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val image = itemView.findViewById<ImageView>(R.id.image)
        private val title = itemView.findViewById<TextView>(R.id.title)

        fun bind(photo: Photo) {
            Glide.with(itemView)
                .load(photo.url)
                .into(image)
            title.text = photo.title
        }

    }

    inner class PhotoAdapter : ListAdapter<Photo, PhotoHolder>(
        object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem == newItem
            }
        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            return PhotoHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.photo_list_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.bind(currentList[position])
        }

    }

    companion object {
        fun newInstance() =
            PhotoGalleryFragment()
    }
}