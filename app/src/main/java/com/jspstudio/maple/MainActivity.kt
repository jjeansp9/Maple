package com.jspstudio.maple

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import com.jspstudio.maple.databinding.ActivityMainBinding
import java.text.DecimalFormat
import java.util.Random

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var dialog: NormalDialog? = null

    private val PATTERN_NUMBER = "#,###"

    private var price10 = 0L
    private var price60 = 0L

    private var glove10cnt = 0
    private var glove60cnt = 0

    private var success = false

    private val PERCENT_10 = 10
    private val PERCENT_60 = 60

    private var power = 0
    private var maxUpCnt = 5

    private var buyTotal = 0L

    private var gloveStack = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onClick()
    }

    private fun onClick() {
        binding.tvSetGlove10.setOnClickListener { showDialog(binding.tvGlove10Price) }
        binding.tvSetGlove60.setOnClickListener { showDialog(binding.tvGlove60Price) }
        binding.tvUpgrade10.setOnClickListener { upgrade(10) } // 강화
        binding.tvUpgrade60.setOnClickListener { upgrade(60) } // 강화
        binding.tvReset.setOnClickListener { reset() } // 초기화
        binding.tvBuyTotalReset.setOnClickListener {
            buyTotal = 0L
            binding.buyTotal.text = "${getFormattedValue(buyTotal, PATTERN_NUMBER)} 메소"
        } // 총 사용금액 초기화
    }

    private fun showDialog(view: TextView) {
        if (dialog == null || dialog?.isAdded == false) {
            dialog = NormalDialog().apply {
                setTitle("주문서의 금액을 설정해주세요")
                okClick {
                    view.text = "${getFormattedValue(it, PATTERN_NUMBER)} 메소"
                    when(view) {
                        binding.tvGlove10Price -> price10 = it
                        binding.tvGlove60Price -> price60 = it
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

    // 초기화
    private fun reset() {
        binding.tvResult.visibility = View.GONE
        binding.tvPower.visibility = View.GONE

        binding.tvUpgrade10.visibility = View.VISIBLE
        binding.tvUpgrade60.visibility = View.VISIBLE
        binding.tvReset.visibility = View.GONE

        maxUpCnt = 5
        power = 0

        gloveStack = 0

        binding.tvNogadaName.text = "노가다 목장갑"
        binding.tvNogadaName.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.tvUpgradeCount.text = "업그레이드 가능 횟수 : $maxUpCnt"
    }

    // 강화
    private fun upgrade(percentage: Int) {
        if (maxUpCnt <= 0) return
        when(percentage) {
            PERCENT_10 -> {
                if (price10 == 0L) {
                    CustomToast(this, "장공 10% 주문서 금액을 설정해주세요")
                    return
                } else {
                    buyTotal += price10
                }
            }
            PERCENT_60 -> {
                if (price60 == 0L) {
                    CustomToast(this, "장공 60% 주문서 금액을 설정해주세요")
                    return
                } else {
                    buyTotal += price60
                }
            }
        }

        maxUpCnt -= 1

        val random = Random()
        success = random.nextInt(100) < percentage

        if (success) {
            power += if (percentage == PERCENT_10) 3 else 2
            val result = "공격력 : +$power"
            binding.tvPower.text = result
            if (binding.tvPower.visibility == View.GONE) binding.tvPower.visibility = View.VISIBLE

            binding.tvResult.text = "강화 성공~!"
            binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.font_color_success))

            gloveStack += 1
            binding.tvNogadaName.text = "노가다 목장갑 (+${gloveStack})"
            binding.tvNogadaName.setTextColor(ContextCompat.getColor(this, R.color.glove_up_color))

            addTextView("주문서가 한 순간 빛나더니 신비로운 힘이 그대로 아이템에 전해졌습니다.")
        } else {
            binding.tvResult.text = "강화 실패 ㅠㅠ"
            binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.font_color_fail))
            addTextView("주문서가 한 순간 빛났지만 아이템에는 아무런 변화도 일어나지 않았습니다.")
        }

        binding.buyTotal.text = "${getFormattedValue(buyTotal, PATTERN_NUMBER)} 메소"
        binding.tvUpgradeCount.text = "업그레이드 가능 횟수 : $maxUpCnt"

        if (binding.tvResult.visibility == View.GONE) binding.tvResult.visibility = View.VISIBLE
        if (binding.scrollBottom.visibility == View.INVISIBLE) binding.scrollBottom.visibility = View.VISIBLE

        if (maxUpCnt <= 0) {
            binding.tvUpgrade10.visibility = View.GONE
            binding.tvUpgrade60.visibility = View.GONE
            binding.tvReset.visibility = View.VISIBLE
        }
    }

    private fun addTextView(msg: String) {
        val textView = TextView(this)

        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textView.setTextColor(getColor(R.color.font_color_red))
        textView.text = msg
        textView.textSize = 14f
        binding.layoutLog.addView(textView)
    }

    fun getFormattedValue(data: Long, pattern: String): String { return DecimalFormat(pattern).format(data) } // 숫자 형식
}