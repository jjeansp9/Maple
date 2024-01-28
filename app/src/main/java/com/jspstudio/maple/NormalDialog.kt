package com.jspstudio.maple

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.jspstudio.maple.databinding.DialogNormalBinding

class NormalDialog : DialogFragment() {

    private val binding: DialogNormalBinding by lazy { DialogNormalBinding.inflate(layoutInflater) }

    private var okClickListener: ((response: Long) -> Unit)? = null
    private var cancelClickListener: View.OnClickListener? = null
    private var price = ""

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
    }

    fun setTitle(str: String) { binding.tvTitle.text = str }
    fun setMsg(str: String) { binding.tvContent.text = str }
    fun setPrice(str: String) { price = str }
    fun setEtVisible(setVisible: Boolean) { if (setVisible) binding.etPrice.visibility = View.GONE }
    fun setMsgVisible(setVisible: Boolean) { if (setVisible) binding.tvContent.visibility = View.GONE }
    fun okClick(listener: ((response: Long) -> Unit)?) {
        okClickListener = listener
    }
    fun cancelClick(listener: View.OnClickListener) { cancelClickListener = listener }

    private fun onClick () {
        binding.tvOk.setOnClickListener{
            var num = 0L

            if (binding.etPrice.text.isNotEmpty()) num = binding.etPrice.text.toString().toLong()
            if (okClickListener != null) okClickListener?.let { it1 -> it1(num) }
        }

        cancelClickListener?.let { binding.tvCancel.setOnClickListener(it) }
    }
}