package com.jspstudio.maple

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.jspstudio.maple.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

//    음원 파일이 외장 메모리나 서버에 있을 때도 있습니다. 이때는 setDataSource ( ) 함수로 음원을 지정합니다.
//
//    mediaPlayer = new MediaPlayer();
//    mediaPlayer.setDataSource(this, Uri.parse(url));

//    파일일 때는 setDataSource ( ) 함수에 String 타입으로 파일 경로를 지정하여 사용합니다.
//
//    mediaPlayer.setDataSource(filePath);
//    출처: https://kkangsnote.tistory.com/50 [깡샘의 토마토:티스토리]

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
            itemDialog = ItemBoxDialog().apply {
                setTitle("강화할 아이템을 선택해주세요")
                okClick {
                    Toast.makeText(this@MainActivity, it.name, Toast.LENGTH_SHORT).show()
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

        maxUpCnt = 5
        power = 0

        gloveStack = 0

        binding.tvNogadaName.text = "노가다 목장갑"
        binding.tvNogadaName.setTextColor(ContextCompat.getColor(this, R.color.white))

        binding.tvUpgradeCount.text = "업그레이드 가능 횟수 : $maxUpCnt"
    }

    private fun checkUpgrade(percentage: Int) {
        if (maxUpCnt <= 0) return
        if (adsStack >= 20) {
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
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,
            "ca-app-pub-3899092616268649/6202800866",
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

    fun getFormattedValue(data: Long, pattern: String): String { return DecimalFormat(pattern).format(data) } // 숫자 형식
}