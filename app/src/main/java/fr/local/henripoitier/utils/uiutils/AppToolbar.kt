package fr.local.henripoitier.utils.uiutils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import fr.local.henripoitier.R
import fr.local.henripoitier.databinding.ToolbarLayoutBinding

class AppToolbar : LinearLayout {

    private lateinit var binding: ToolbarLayoutBinding

    constructor(context: Context?) : super(context) {
        initView(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs, defStyleAttr)
    }

    private fun initView(attrs: AttributeSet?, defStyle: Int) {
        binding = ToolbarLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.AppToolbar, defStyle, 0
        )
        binding.titleTextView.text = typedArray.getString(R.styleable.AppToolbar_title)
        when {
            typedArray.getBoolean(R.styleable.AppToolbar_cart_btn, false) -> {
                binding.cart.visibility = View.VISIBLE
                setCartText()
            }
            else -> {
                binding.cart.visibility = View.GONE
            }
        }
        val leftDrawable = typedArray.getDrawable(R.styleable.AppToolbar_left_btn)
        if (leftDrawable != null) {
            binding.leftBtn.visibility = View.VISIBLE
            binding.leftBtnImageView.setImageDrawable(leftDrawable)
        } else {
            binding.leftBtn.visibility = View.GONE
        }
        typedArray.recycle()
    }

    fun setLeftButtonClickListener(callback: (View?) -> Unit) {
        binding.leftBtn.setOnClickListener {
            callback.invoke(it)
        }
    }

    fun setCartButtonClickListener(callback: (View?) -> Unit) {
        binding.cart.setOnClickListener {
            callback.invoke(it)
        }
    }

    fun setTitle(title: String) {
        binding.titleTextView.text = title
    }

    fun setCartText(numberItems: Int = 0) {
        binding.cart.text = context.getString(R.string.cart_number_items, numberItems)
    }
}