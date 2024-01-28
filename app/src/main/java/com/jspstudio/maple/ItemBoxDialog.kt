package com.jspstudio.maple

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.jspstudio.maple.databinding.DialogItemBoxBinding
import com.jspstudio.maple.databinding.DialogNormalBinding

class ItemBoxDialog : DialogFragment() {

    private val binding: DialogItemBoxBinding by lazy { DialogItemBoxBinding.inflate(layoutInflater) }

    private var okClickListener: ((response: ItemOption) -> Unit)? = null
    private var title = ""
    private var price = ""


    private val itemList = mutableListOf<ItemOption>()

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
        initView()
    }

    private fun initData() {
        binding.tvTitle.text = title

        itemList.add(Item.nogadaGlove())
        itemList.add(Item.humang())
        for (i in 0 until 30) {
            itemList.add(ItemOption())
        }
    }

    val mAdapter by lazy { context?.let { ItemBoxAdapter(it, itemList) } }

    private fun initView() {
        initData()

        if (mAdapter != null) {
            mAdapter?.setItemClickListener(object : ItemBoxAdapter.OnItemClickListener{
                override fun itemClick(item : ItemOption) {
                    if (dialog != null) {
                        //if (okClickListener != null) okClickListener?.let { it1 -> it1(item) }
                        showDialog()

                    }
                }
            })
        }
        binding.recycler.adapter = mAdapter
        var index = 0
        for (i in 0 until 32) {
            mAdapter?.notifyItemChanged(i, itemList)
        }
    }

    fun setTitle(str: String) { title = str }
    fun okClick(listener: ((response: ItemOption) -> Unit)?) {
        okClickListener = listener
    }

    private fun onClick () {

    }

    private var adsDialog: NormalDialog? = null

    private fun showDialog() {
        if (adsDialog == null || adsDialog?.isAdded == false) {
            adsDialog = NormalDialog().apply {
                setTitle("광고 시청")
                setMsg("아이템 변경을 위해 광고를 시청하시겠습니까?")
                setEtVisible(true)
                okClick {
//                    view.text = "${getFormattedValue(it, PATTERN_NUMBER)} 메소"
//                    when(view) {
//                        binding.tvGlove10Price -> price10 = it
//                        binding.tvGlove60Price -> price60 = it
//                    }
//                    dismiss()
                }
                cancelClick { dismiss() }
            }
        }
        // Dialog가 이미 추가되었는지 확인하고, 추가되지 않았다면 표시
        if (!adsDialog!!.isAdded) {
            adsDialog!!.show(this.childFragmentManager, "normalDialog")
        }
    }
}