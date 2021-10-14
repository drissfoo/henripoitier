package fr.local.henripoitier.ui.booklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import fr.local.henripoitier.R
import fr.local.henripoitier.data.entities.BooksListServerResponse
import fr.local.henripoitier.data.entities.BooksListServerResponseItem
import fr.local.henripoitier.databinding.BooksListFragmentBinding
import fr.local.henripoitier.ui.SharedCartViewModel
import fr.local.henripoitier.utils.Resource.Status.*
import fr.local.henripoitier.utils.viewLifecycle

@AndroidEntryPoint
class BooksListFragment : Fragment(), BooksAdapter.BookRowListener {

    private val viewModel: BooksListViewModel by viewModels()
    private val sharedCartViewModel: SharedCartViewModel by activityViewModels()
    private var _binding: BooksListFragmentBinding? by viewLifecycle()
    private val binding
        get() = _binding ?: error("Called before onCreateView or after onDestroyView.")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BooksListFragmentBinding.inflate(inflater, container, false)
        binding.booksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.booksRecyclerView.setHasFixedSize(true)
        binding.booksRecyclerView.adapter = BooksAdapter(listOf(), this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.reloadButton.setOnClickListener {
            viewModel.refreshBooks()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            viewModel.refreshBooks()
        }
        binding.toolbar.setCartButtonClickListener {
            if (sharedCartViewModel.elementsInCart.value ?: 0 > 0) {
                findNavController().navigate(
                    R.id.action_booksListFragment_to_cartFragment
                )
            } else {
                val snack =
                    Snackbar.make(
                        binding.toolbar,
                        R.string.empty_cart_message,
                        Snackbar.LENGTH_SHORT
                    )
                snack.show()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.books.observe(viewLifecycleOwner, {
            it?.let { resource ->
                binding.swipeRefreshLayout.isRefreshing = false
                when (resource.status) {
                    SUCCESS -> {
                        binding.infoTextView.visibility = View.GONE
                        binding.progressbar.visibility = View.GONE
                        binding.reloadButton.visibility = View.GONE
                        binding.swipeRefreshLayout.visibility = View.VISIBLE
                        resource.data?.let { books -> retrieveList(books) }
                    }
                    ERROR -> {
                        binding.infoTextView.visibility = View.VISIBLE
                        binding.progressbar.visibility = View.GONE
                        binding.reloadButton.visibility = View.VISIBLE
                        binding.infoTextView.text = getString(R.string.error_loading_books)
                        binding.swipeRefreshLayout.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    LOADING -> {
                        binding.infoTextView.visibility = View.VISIBLE
                        binding.reloadButton.visibility = View.GONE
                        binding.progressbar.visibility = View.VISIBLE
                        binding.infoTextView.text = getString(R.string.loading_books)
                        binding.swipeRefreshLayout.visibility = View.GONE
                    }
                }
            }
        })
        sharedCartViewModel.elementsInCart.observe(viewLifecycleOwner, {
            binding.toolbar.setCartText(it)
        })
    }

    private fun retrieveList(booksListServerResponse: BooksListServerResponse) {
        binding.booksRecyclerView.adapter = BooksAdapter(booksListServerResponse, this)
    }

    override fun onBookClick(book: BooksListServerResponseItem) {
        sharedCartViewModel.selectBook(book)
        findNavController().navigate(
            R.id.action_booksListFragment_to_bookDetailFragment
        )
    }

    override fun onAddToCart(book: BooksListServerResponseItem) {
        sharedCartViewModel.addBook(book)
        val snack =
                Snackbar.make(
                        binding.toolbar,
                        R.string.books_added_message,
                        Snackbar.LENGTH_SHORT
                )
        snack.show()
    }
}