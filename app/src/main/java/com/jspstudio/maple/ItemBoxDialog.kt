package com.jspstudio.maple

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.jspstudio.maple.databinding.DialogItemBoxBinding
import com.jspstudio.maple.databinding.DialogNormalBinding

class ItemBoxDialog : DialogFragment() {

    private val binding: DialogItemBoxBinding by lazy { DialogItemBoxBinding.inflate(layoutInflater) }

    private var okClickListener: ((response: Long) -> Unit)? = null
    private var title = ""
    private var price = ""

    private val itemList = arrayListOf(
        R.drawable.ic_nogada_glove_item,
        R.drawable.humang_item
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        initData()
        initView()
    }

    private fun initData() {
        binding.tvTitle.text = title
    }

    private fun initView() {

        var index = 0

        for (i in 0 until 32) {
            val imageView = ImageView(context)
            val density = resources.displayMetrics.density
            val widthInDp = 60
            val heightInDp = 60
            val paddingDp = (8 * density).toInt()

            imageView.layoutParams = ViewGroup.LayoutParams((widthInDp * density).toInt(), (heightInDp * density).toInt())
            imageView.background = context?.getDrawable(R.drawable.item_box)

            val marginLayoutParams = ViewGroup.MarginLayoutParams(imageView.layoutParams)
            marginLayoutParams.setMargins(4, 4, 4, 4)
            imageView.layoutParams = marginLayoutParams
            imageView.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
            if (itemList.size - 1 >= i && index == i) {
                context?.let { Glide.with(it).load(itemList[index]).into(imageView) }
                index++
            }

            imageView.setOnClickListener {
                var num = 0L

                if (binding.etPrice.text.isNotEmpty()) num = binding.etPrice.text.toString().toLong()
                if (okClickListener != null) okClickListener?.let { it1 -> it1(num) }
                dialog?.dismiss()
            }

            binding.layoutItemBox.addView(imageView)
        }
    }

    fun setTitle(str: String) { title = str }
    fun okClick(listener: ((response: Long) -> Unit)?) {
        okClickListener = listener
    }

    private fun onClick () {

    }
}