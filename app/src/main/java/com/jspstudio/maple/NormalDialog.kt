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
    private var title = ""
    private var msg = ""
    private var price = ""
    private var etVisible : Boolean? = null
    private var msgVisible : Boolean? = null

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
        binding.tvContent.text = msg
        if (etVisible != null && etVisible!!) binding.etPrice.visibility = View.GONE
        if (msgVisible != null && msgVisible!!) binding.tvContent.visibility = View.GONE
    }

    fun setTitle(str: String) { title = str }
    fun setMsg(str: String) { msg = str }
    fun setPrice(str: String) { price = str }
    fun setEtVisible(setVisible: Boolean) { etVisible = setVisible }
    fun setMsgVisible(setVisible: Boolean) { msgVisible = setVisible }
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