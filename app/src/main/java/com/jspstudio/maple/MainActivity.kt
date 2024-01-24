package com.jspstudio.maple

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jspstudio.maple.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var dialog: NormalDialog? = null

    private var price_10 = 0
    private var price_60 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onClick()
    }

    private fun onClick() {
        binding.tvSetGlove10.setOnClickListener {
            showDialog(binding.tvGlove10Price)
        }
        binding.tvSetGlove60.setOnClickListener {
            showDialog(binding.tvGlove60Price)
        }
    }

    private fun showDialog(view: TextView) {
        if (dialog == null || dialog?.isAdded == false) {
            dialog = NormalDialog().apply {
                setTitle("주문서의 금액을 설정해주세요")
                okClick {
                    view.text = it.toString() + " 메소"
                    when(view) {
                        binding.tvGlove10Price -> price_10 = it
                        binding.tvGlove60Price -> price_60 = it
                    }
                    dismiss()
                }
                cancelClick { dismiss() }
            }
        }
        // Dialog가 이미 추가되었는지 확인하고, 추가되지 않았다면 표시
        if (!dialog!!.isAdded) {
            dialog!!.show(this.supportFragmentManager, "normalDialog")
        }
    }
}