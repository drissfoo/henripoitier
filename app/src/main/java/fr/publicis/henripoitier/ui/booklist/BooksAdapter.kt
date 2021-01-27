package fr.publicis.henripoitier.ui.booklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.publicis.henripoitier.data.entities.BooksListServerResponseItem
import fr.publicis.henripoitier.databinding.BookItemBinding
import fr.publicis.henripoitier.utils.toEuroPrice

class BooksAdapter(
    private val list: List<BooksListServerResponseItem>,
    private val listener: BookRowListener
) :
    RecyclerView.Adapter<BooksAdapter.BooksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        return BooksViewHolder(
            BookItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        holder.bindView(list[position], listener)
    }

    override fun getItemCount() = list.size

    class BooksViewHolder(private val itemBinding: BookItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindView(
            book: BooksListServerResponseItem,
            listener: BookRowListener
        ) {
            book.title?.let { itemBinding.bookTitle.text = it }
            book.cover?.let {
                Glide.with(itemView.context).load(it).into(itemBinding.coverImageView)
            }
            book.price?.let {
                itemBinding.bookPrice.text = it.toEuroPrice()
            }
            if (!book.synopsis.isNullOrEmpty()) {
                book.synopsis[0]?.let {
                    itemBinding.bookDesc.text = it
                }
            }
            itemBinding.root.setOnClickListener {
                listener.onBookClick(book)
            }
            itemBinding.addToCartImageView.setOnClickListener {
                listener.onAddToCart(book)
            }
        }
    }

    interface BookRowListener {
        fun onBookClick(book: BooksListServerResponseItem)
        fun onAddToCart(book: BooksListServerResponseItem)
    }
}