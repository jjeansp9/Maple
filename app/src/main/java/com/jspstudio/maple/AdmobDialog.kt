package com.jspstudio.maple

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.jspstudio.maple.databinding.DialogAdmobBinding
import com.jspstudio.maple.databinding.DialogNormalBinding

class AdmobDialog(val mContext: AppCompatActivity) : DialogFragment() {

    private val binding: DialogAdmobBinding by lazy { DialogAdmobBinding.inflate(layoutInflater) }

    private var okClickListener: ((response: Int) -> Unit)? = null
    private var cancelClickListener: View.OnClickListener? = null
    private var title = ""
    private var msg = ""
    private var price = ""
    private var item = ItemOption()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
    }

    private fun initData() {
        binding.tvTitle.text = title
        if (item != null) {
            Glide.with(requireContext()).load(item.img).into(binding.imgItem)
            binding.tvItemName.text = item.name
        }
    }

    fun setTitle(str: String) { title = str }
    fun setItem(item: ItemOption) { this.item = item }
    fun okClick(listener: ((response: Int) -> Unit)?) {
        okClickListener = listener
    }

    private fun onClick () {
        binding.layoutShowAds.setOnClickListener{
            Util.showAds(mContext) {
                if (it == ResponseCode.SUCCESS) {
                    if (okClickListener != null) okClickListener?.let { it1 -> it1(it) }
                    dismiss()

                } else {
                    Toast.makeText(mContext, "아직 광고가 없습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}