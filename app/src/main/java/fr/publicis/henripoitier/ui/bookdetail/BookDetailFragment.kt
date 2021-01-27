package fr.publicis.henripoitier.ui.bookdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import fr.publicis.henripoitier.R
import fr.publicis.henripoitier.databinding.BookDetailFragmentBinding
import fr.publicis.henripoitier.ui.SharedCartViewModel
import fr.publicis.henripoitier.utils.toEuroPrice
import fr.publicis.henripoitier.utils.viewLifecycle

class BookDetailFragment : Fragment() {

    private val sharedCartViewModel: SharedCartViewModel by activityViewModels()
    private var _binding: BookDetailFragmentBinding? by viewLifecycle()
    private val binding
        get() = _binding ?: error("Called before onCreateView or after onDestroyView.")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BookDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setLeftButtonClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.setCartButtonClickListener {
            if (sharedCartViewModel.elementsInCart.value ?: 0 > 0) {
                findNavController().navigate(
                    R.id.action_bookDetailFragment_to_cartFragment
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
        binding.addToCart.setOnClickListener { fab ->
            sharedCartViewModel.addCurrentBook()
            Snackbar.make(fab, R.string.books_added_message, Snackbar.LENGTH_LONG)
                .show()
        }
        sharedCartViewModel.elementsInCart.observe(viewLifecycleOwner, {
            binding.toolbar.setCartText(it)
        })
        sharedCartViewModel.selectedItem.observe(viewLifecycleOwner, { book ->
            book.title?.let {
                binding.toolbar.setTitle(it)
            }
            val strBuilder = StringBuilder()
            book.synopsis?.map { paragraph ->
                paragraph?.let {
                    strBuilder.append(it).append("\n\n")
                }
            }
            binding.synopsis.text = strBuilder.toString()
            book.cover?.let {
                Glide.with(requireActivity()).load(it).into(binding.cover)
            }
            book.price?.let {
                binding.price.text = getString(R.string.book_price, it.toEuroPrice())
            }
        })
    }
}