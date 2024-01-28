package com.jspstudio.maple

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.jspstudio.maple.databinding.ActivityMainBinding
import java.text.DecimalFormat
import java.util.Random

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var dialog: NormalDialog? = null
    private var itemDialog: ItemBoxDialog? = null

    private lateinit var mediaPlayer: MediaPlayer

    private val PATTERN_NUMBER = "#,###"

    private var price10 = 0L
    private var price60 = 0L

    private var glove10cnt = 0
    private var glove60cnt = 0

    private var success = false

    private val PERCENT_10 = 10
    private val PERCENT_60 = 60

    private var power = 0
    private var dex = 0
    private var acc = 0
    private var speed = 0
    private var maxUpCnt = 5

    private var buyTotal = 0L

    private var gloveStack = 0

    private var setMusic = true
    private var showAds = false

    private val musicList = arrayListOf(
        R.raw.leith,
        R.raw.henesys,
        R.raw.bad_guys,
        R.raw.hyperion,
        R.raw.elinia_bgm,
        R.raw.sleepywood,
        R.raw.ludibrium,
        R.raw.cash
    )
    private val bgList = arrayListOf(
        R.drawable.bg_leith,
        R.drawable.bg_henesys,
        R.drawable.bg_bad_guys,
        R.drawable.bg_hyperion,
        R.drawable.bg_ellinia,
        R.drawable.bg_sleepywood,
        R.drawable.bg_ludibrium,
        R.drawable.bg_cash
    )

    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        MobileAds.initialize(this) { Util.loadAds(this) }

        items = Item.nogadaGlove()

        onClick()
        setMusic = UtilPref.getDataBoolean(this, UtilPref.SET_MUSIC)
        currentPosition = UtilPref.getDataInt(this, UtilPref.SET_THEME)

        Glide.with(this).load(bgList[currentPosition]).into(binding.imgBg)
        mediaPlayer = MediaPlayer.create(this, musicList[currentPosition])

        currentPosition = (currentPosition + 1) % musicList.size

        if (setMusic) {
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { mediaPlayer.start() }
        } else {
            binding.tvSetMusic.text = "음악켜기"
        }

        setAds()
        setupInterstitialAd()
    }

    private fun setAds() {
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()
        if (setMusic) {
            if (mediaPlayer != null) {
                if (!mediaPlayer.isPlaying) mediaPlayer.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (setMusic) {
            if (mediaPlayer != null) mediaPlayer.release()
        }
    }

    override fun onPause() {
        super.onPause()
        if (setMusic) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying) mediaPlayer.pause()
            }
        }
    }

    private fun onClick() {
        binding.tvSetTheme.setOnClickListener { setTheme() }
        binding.tvSetMusic.setOnClickListener { setMusic() }
        binding.tvSetGlove10.setOnClickListener { showDialog(binding.tvGlove10Price) }
        binding.tvSetGlove60.setOnClickListener { showDialog(binding.tvGlove60Price) }
        binding.tvUpgrade10.setOnClickListener { checkUpgrade(10) } // 10퍼 강화
        binding.tvUpgrade60.setOnClickListener { checkUpgrade(60) } // 60퍼 강화
        binding.tvReset.setOnClickListener { reset() } // 강화 초기화
        binding.tvBuyTotalReset.setOnClickListener {
            buyTotal = 0L
            binding.buyTotal.text = "${getFormattedValue(buyTotal, PATTERN_NUMBER)} 메소"
        } // 총 사용금액 초기화
        binding.tvChangeItem.setOnClickListener { showItemBoxDialog() }
    }

    private fun showItemBoxDialog() {
        if (itemDialog == null || itemDialog?.isAdded == false) {
            itemDialog = ItemBoxDialog(this).apply {
                setTitle("강화할 아이템을 선택해주세요")
                okClick {
                    setData(it)
                    dismiss()
                }
            }
        }
        // Dialog가 이미 추가되었는지 확인하고, 추가되지 않았다면 표시
        if (!itemDialog!!.isAdded) {
            itemDialog!!.show(this.supportFragmentManager, "itemBoxDialog")
        }
    }

    private fun showDialog(view: TextView) {
        if (dialog == null || dialog?.isAdded == false) {
            dialog = NormalDialog().apply {
                setTitle("주문서의 금액을 설정해주세요")
                setMsgVisible(true)
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

        if (items.avd != -1) {
            binding.tvAvd.text = "회피율 : +${items.avd}"
            binding.tvAvd.visibility = View.VISIBLE
        } else {
            binding.tvAvd.visibility = View.GONE
        }
        if (items.physicalDef == -1) binding.tvPhysicalDef.visibility = View.GONE
        if (items.magicDef == -1) binding.tvMagicDef.visibility = View.GONE
        if (items.speed == -1) binding.tvSpeed.visibility = View.GONE
        if (items.acc == -1) binding.tvAcc.visibility = View.GONE
        binding.tvDex.visibility = View.GONE

        if (items.upgradeCnt != -1) maxUpCnt = items.upgradeCnt
        else maxUpCnt = 5
        power = 0
        dex = 0
        acc = 0
        speed = 0
        gloveStack = 0

        if (items.book_10!!.isNotEmpty()) binding.tvGlove10.text = items.book_10
        else binding.tvGlove10.text = "장공 주문서 10% (공+3)"
        if (items.book_60!!.isNotEmpty()) binding.tvGlove60.text = items.book_60
        else binding.tvGlove60.text = "장공 주문서 60% (공+2)"

        if (items.name!!.isNotEmpty()) binding.tvNogadaName.text = items.name
        else binding.tvNogadaName.text = "노가다 목장갑"
        binding.tvNogadaName.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.tvUpgradeCount.text = "업그레이드 가능 횟수 : $maxUpCnt"
    }

    private fun checkUpgrade(percentage: Int) {
        if (maxUpCnt <= 0) return
        if (adsStack >= 40) {
            showAds = random.nextInt(100) < 3
            if (showAds) {
                adsStack = 0
                showAds = false
                showAds()
                upgrade(percentage)

            } else {
                upgrade(percentage)
            }
        } else {
            upgrade(percentage)
        }
    }

    private var adsStack = 0
    val random = Random()

    // 강화
    private fun upgrade(percentage: Int) {
        adsStack++
        when(percentage) {
            PERCENT_10 -> {
                if (price10 == 0L) {
                    CustomToast(this, "10% 주문서 금액을 설정해주세요")
                    return
                } else {
                    buyTotal += price10
                }
            }
            PERCENT_60 -> {
                if (price60 == 0L) {
                    CustomToast(this, "60% 주문서 금액을 설정해주세요")
                    return
                } else {
                    buyTotal += price60
                }
            }
        }

        maxUpCnt -= 1

        success = random.nextInt(100) < percentage

        if (success) {

            setOption(percentage)

            binding.tvResult.text = "강화 성공~!"
            binding.tvResult.setTextColor(ContextCompat.getColor(this, R.color.font_color_success))

            gloveStack += 1
            if (items.name!!.isNotEmpty()) binding.tvNogadaName.text = "${items.name} (+${gloveStack})"
            else binding.tvNogadaName.text = "노가다 목장갑 (+${gloveStack})"
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

    private fun setMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            binding.tvSetMusic.text = "음악켜기"
            setMusic = false
        } else {
            mediaPlayer.start()
            binding.tvSetMusic.text = "음악끄기"
            setMusic = true
            mediaPlayer.setOnCompletionListener { mediaPlayer.start() }
        }

        UtilPref.setDataBoolean(this, UtilPref.SET_MUSIC, setMusic)
    }

    private fun setTheme() {
        if (mediaPlayer != null) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        mediaPlayer = MediaPlayer.create(this, musicList[currentPosition])
        if (setMusic) {
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { mediaPlayer.start() }
        }

        Glide.with(this).load(bgList[currentPosition]).into(binding.imgBg)

        currentPosition = (currentPosition + 1) % musicList.size

        UtilPref.setDataInt(this, UtilPref.SET_THEME, currentPosition)
    }

    private var mInterstitialAd: InterstitialAd? = null

    private fun setupInterstitialAd() {

        val test_id = getString(R.string.ads_interstitial_test_id)
        val ads_id = getString(R.string.ads_interstitial_id)

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,
            ads_id,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
    }

    private fun showAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this@MainActivity)
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    mInterstitialAd = null
                    setupInterstitialAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    mInterstitialAd = null
                }

                override fun onAdImpression() {
                }

                override fun onAdShowedFullScreenContent() {
                }
            }
        }
    }

    var items = ItemOption()

    @SuppressLint("SetTextI18n")
    private fun setData(item : ItemOption) {
        if (item == null) return
        binding.tvNogadaName.text = item.name
        binding.tvNogadaName.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.tvReqLev.text = "REQ LEV : ${item.reqLev}"
        binding.tvReqStr.text = "REQ STR : ${item.reqStr}"
        binding.tvReqDex.text = "REQ DEX : ${item.reqDex}"
        binding.tvReqInt.text = "REQ INT : ${item.reqInt}"
        binding.tvReqLuk.text = "REQ LUK : ${item.reqLuk}"

        binding.tvItemType.text = "장비분류 : ${item.type}"
        if (item.physicalAtt != -1) binding.tvPower.text = "공격력 : +${item.physicalAtt}"
        else binding.tvPower.visibility = View.GONE

        if (item.physicalDef != -1) {
            binding.tvPhysicalDef.text = "물리방어력 : +${item.physicalDef}"
            binding.tvPhysicalDef.visibility = View.VISIBLE
        } else {
            binding.tvPhysicalDef.visibility = View.GONE
        }

        if (item.magicDef != -1) {
            binding.tvMagicDef.text = "마법방어력 : +${item.magicDef}"
            binding.tvMagicDef.visibility = View.VISIBLE
        } else {
            binding.tvMagicDef.visibility = View.GONE
        }

        if (item.avd != -1) {
            binding.tvAvd.text = "회피율 : +${item.avd}"
            binding.tvAvd.visibility = View.VISIBLE
        } else {
            binding.tvAvd.visibility = View.GONE
        }
        binding.tvDex.visibility = View.GONE
        binding.tvUpgradeCount.text = "업그레이드 가능 횟수 : ${item.upgradeCnt}"

        if (item.book_10!!.isNotEmpty()) binding.tvGlove10.text = item.book_10
        else binding.tvGlove10.text = "장공 주문서 10% (공+3)"
        if (item.book_60!!.isNotEmpty()) binding.tvGlove60.text = item.book_60
        else binding.tvGlove60.text = "장공 주문서 60% (공+2)"

        if (items.physicalDef == -1) binding.tvPhysicalDef.visibility = View.GONE
        if (items.magicDef == -1) binding.tvMagicDef.visibility = View.GONE
        if (items.speed == -1) binding.tvSpeed.visibility = View.GONE
        if (items.acc == -1) binding.tvAcc.visibility = View.GONE
        binding.tvDex.visibility = View.GONE

        maxUpCnt = item.upgradeCnt
        gloveStack = 0
        power = 0
        dex = 0
        acc = 0
        speed = 0

        Glide.with(this).load(item.imgMain).into(binding.imgNogada)

        binding.tvResult.visibility = View.GONE
        binding.tvPower.visibility = View.GONE
        items = item
    }

    private fun setOption(percentage: Int) {
        if (items.att_10 != -1 && items.att_60 != -1) {
            power += if (percentage == PERCENT_10) items.att_10 else items.att_60
            val result = "공격력 : +$power"
            binding.tvPower.text = result
            if (binding.tvPower.visibility == View.GONE) binding.tvPower.visibility = View.VISIBLE
        }
        if (items.dex_10 != -1 && items.dex_60 != -1) {
            dex += if (percentage == PERCENT_10) items.dex_10 else items.dex_60
            val result = "DEX : +$dex"
            binding.tvDex.text = result
            if (binding.tvDex.visibility == View.GONE) binding.tvDex.visibility = View.VISIBLE
        }
        if (items.acc_10 != -1 && items.acc_60 != -1) {
            acc += if (percentage == PERCENT_10) items.acc_10 else items.acc_60
            val result = "명중률 : +$acc"
            binding.tvAcc.text = result
            if (binding.tvAcc.visibility == View.GONE) binding.tvAcc.visibility = View.VISIBLE
        }
        if (items.speed_10 != -1) {
            if (percentage == PERCENT_10) {
                speed +=  items.speed_10
                val result = "이동속도 : +$speed"
                binding.tvSpeed.text = result
                if (binding.tvSpeed.visibility == View.GONE) binding.tvSpeed.visibility = View.VISIBLE
            }
        }
    }

    fun getFormattedValue(data: Long, pattern: String): String { return DecimalFormat(pattern).format(data) } // 숫자 형식
}