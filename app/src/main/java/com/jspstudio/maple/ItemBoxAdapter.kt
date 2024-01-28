package com.jspstudio.maple

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jspstudio.maple.databinding.ListItemBoxBinding


class ItemBoxAdapter constructor(val context: Context, var homeItems:MutableList<ItemOption>):RecyclerView.Adapter<ItemBoxAdapter.VH>(){

    inner class VH constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding : ListItemBoxBinding = ListItemBoxBinding.bind(itemView)
    }

    // (2) 리스너 인터페이스 - 수락, 거절, 이름
    interface OnItemClickListener {
        fun itemClick(item: ItemOption)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layoutInflater:LayoutInflater = LayoutInflater.from(context)
        var itemView: View = layoutInflater.inflate(R.layout.list_item_box, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (homeItems != null) {
            Glide.with(context).load(homeItems[position].img).into(holder.binding.img)
            holder.binding.img.setOnClickListener { itemClickListener.itemClick(homeItems[position]) }
        }
    }

    override fun getItemCount(): Int = homeItems.size


}