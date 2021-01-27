package fr.publicis.henripoitier.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import fr.publicis.henripoitier.R
import fr.publicis.henripoitier.data.entities.BooksListServerResponseItem
import fr.publicis.henripoitier.databinding.CartFragmentBinding
import fr.publicis.henripoitier.ui.SharedCartViewModel
import fr.publicis.henripoitier.utils.Resource
import fr.publicis.henripoitier.utils.toEuroPrice
import fr.publicis.henripoitier.utils.viewLifecycle

@AndroidEntryPoint
class CartFragment : Fragment(), CartAdapter.CartRowListener {

    private var adapter: CartAdapter? = null
    private val sharedCartViewModel: SharedCartViewModel by activityViewModels()
    private var _binding: CartFragmentBinding? by viewLifecycle()
    private val binding
        get() = _binding ?: error("Called before onCreateView or after onDestroyView.")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CartFragmentBinding.inflate(inflater, container, false)
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.setHasFixedSize(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setLeftButtonClickListener {
            findNavController().navigateUp()
        }
        binding.reloadButton.setOnClickListener {
            sharedCartViewModel.getCommercialOffer()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedCartViewModel.booksMap.observe(viewLifecycleOwner, {
            adapter = CartAdapter(it, this)
            binding.cartRecyclerView.adapter = adapter
            sharedCartViewModel.getCommercialOffer()
            binding.totalValue.text = sharedCartViewModel.getTotalPrice().toEuroPrice()
        })
        sharedCartViewModel.commercialOffers.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.groupPrice.visibility = View.VISIBLE
                    binding.infoTextView.visibility = View.GONE
                    binding.progressbar.visibility = View.GONE
                    binding.reloadButton.visibility = View.GONE
                    sharedCartViewModel.calculateBestOffer(it.data?.offers)
                }
                Resource.Status.ERROR -> {
                    binding.groupPrice.visibility = View.GONE
                    binding.groupPromo.visibility = View.GONE
                    binding.infoTextView.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.GONE
                    binding.reloadButton.visibility = View.VISIBLE
                    binding.infoTextView.setText(R.string.error_loading_prices)
                }
                Resource.Status.LOADING -> {
                    binding.groupPrice.visibility = View.GONE
                    binding.groupPromo.visibility = View.GONE
                    binding.infoTextView.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.VISIBLE
                    binding.reloadButton.visibility = View.GONE
                    binding.infoTextView.setText(R.string.loading_prices)
                }
            }
        })
        sharedCartViewModel.pairPromo.observe(viewLifecycleOwner, {
            it?.let { safePair ->
                safePair.first?.let {offer ->
                    binding.groupPromo.visibility = View.VISIBLE
                    binding.promoLabel.text = getString(R.string.applied_promotion, offer.type?:"")
                    val str = (safePair.second - sharedCartViewModel.getTotalPrice()).toEuroPrice()
                    binding.promoValue.text = str
                    binding.payValue.text = safePair.second.toEuroPrice()
                } ?: kotlin.run {
                    binding.groupPromo.visibility = View.GONE
                    binding.payValue.text = safePair.second.toEuroPrice()
                }
            }
        })
    }

    override fun onBookDelete(book: BooksListServerResponseItem) {
        sharedCartViewModel.deleteBook(book)
    }

    override fun onAdd(book: BooksListServerResponseItem, bookCount: Int) {
        sharedCartViewModel.addBook(book)
    }

    override fun onSubtract(book: BooksListServerResponseItem, bookCount: Int) {
       sharedCartViewModel.removeSingleBookInstance(book)
    }

    override fun onBookClick(book: BooksListServerResponseItem) {
        sharedCartViewModel.selectBook(book)
        findNavController().navigate(
            R.id.action_cartFragment_to_bookDetailFragment
        )
    }
}