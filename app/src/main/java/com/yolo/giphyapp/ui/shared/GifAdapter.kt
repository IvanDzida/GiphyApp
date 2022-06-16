package com.yolo.giphyapp.ui.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.yolo.data.GiphyItem
import com.yolo.giphyapp.R
import com.yolo.giphyapp.databinding.GifItemBinding

class GifAdapter(
    private val listener: OnItemClickListener,
    val items: MutableList<GiphyItem>
) :
    RecyclerView.Adapter<GifAdapter.GiphyHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        items.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapData(list: List<GiphyItem>) {
        if (items.isNotEmpty()) {
            items.clear()
        }
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun appendData(list: List<GiphyItem>) {
        val previousSize: Int = items.size
        items.addAll(list)
        notifyItemRangeInserted(previousSize, items.size)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiphyHolder {
        val binding = GifItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return GiphyHolder(binding)
    }

    override fun onBindViewHolder(holder: GiphyHolder, position: Int) {
        val item = items[position]

        holder.bind(item)
    }

    inner class GiphyHolder(private val binding: GifItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    //if (item != null)
                    listener.onItemClick(item)
                }
            }
        }

        fun bind(gif: GiphyItem) {
            binding.apply {
                Glide.with(itemView)
                    .asGif()
                    .placeholder(R.drawable.loading_animation)
                    .load(gif.url)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_clear)
                    .into(imageView)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(photo: GiphyItem)
    }

    class GifComparator : DiffUtil.ItemCallback<GiphyItem>() {
        override fun areItemsTheSame(oldItem: GiphyItem, newItem: GiphyItem): Boolean =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: GiphyItem, newItem: GiphyItem): Boolean =
            oldItem == newItem
    }
}