package fr.publicis.henripoitier.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.publicis.henripoitier.R
import fr.publicis.henripoitier.data.entities.BooksListServerResponseItem
import fr.publicis.henripoitier.databinding.CartItemBinding
import fr.publicis.henripoitier.utils.toEuroPrice

class CartAdapter(
    private val map: HashMap<BooksListServerResponseItem, Int>,
    private val listener: CartRowListener
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var listKeys: ArrayList<BooksListServerResponseItem> = ArrayList(map.keys)
    private var listValues: ArrayList<Int> = ArrayList(map.values)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            CartItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bindView(listKeys[position], listValues[position], listener)
    }

    override fun getItemCount() = listKeys.size

    class CartViewHolder(private val itemBinding: CartItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindView(
            book: BooksListServerResponseItem,
            countBooks: Int,
            listener: CartRowListener
        ) {
            var bookCount = countBooks
            book.title?.let { itemBinding.bookTitle.text = it }
            book.cover?.let {
                Glide.with(itemView.context).load(it).into(itemBinding.coverImageView)
            }
            book.price?.let {
                itemBinding.bookPrice.text = it.toEuroPrice()
            }
            itemBinding.count.text = itemView.context.getString(R.string.book_count, bookCount)
            book.price?.let {
                itemBinding.bookPrice.text = (it * bookCount).toEuroPrice()
            }
            itemBinding.add.setOnClickListener {
                bookCount++
                listener.onAdd(book, bookCount)
                itemBinding.count.text = itemView.context.getString(R.string.book_count, bookCount)
                book.price?.let {
                    itemBinding.bookPrice.text = (it * bookCount).toEuroPrice()
                }
            }
            itemBinding.subtract.setOnClickListener {
                if (bookCount > 1) {
                    bookCount--
                    listener.onSubtract(book, bookCount)
                    itemBinding.count.text =
                        itemView.context.getString(R.string.book_count, bookCount)
                    book.price?.let {
                        itemBinding.bookPrice.text = (it * bookCount).toEuroPrice()
                    }
                }
            }
            itemBinding.deleteBook.setOnClickListener {
                listener.onBookDelete(book)
            }
            itemBinding.root.setOnClickListener {
                listener.onBookClick(book)
            }
        }
    }

    interface CartRowListener {
        fun onBookDelete(book: BooksListServerResponseItem)
        fun onAdd(book: BooksListServerResponseItem, bookCount: Int)
        fun onSubtract(book: BooksListServerResponseItem, bookCount: Int)
        fun onBookClick(book: BooksListServerResponseItem)
    }
}