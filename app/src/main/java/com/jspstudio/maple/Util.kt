package com.jspstudio.maple

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object Util {
    private val test_id = "ca-app-pub-3940256099942544/5224354917" // test 광고단위 id
    //private val ads_id = "ca-app-pub-3899092616268649/1603224656" // 광고단위 id. 테스트할때는 사용 절대금지 [광고 정지사유]
    private var mRewardedAd: RewardedAd? = null

    fun loadAds(context: Context) {
        val adRequest: AdRequest = AdRequest.Builder().build()
        RewardedAd.load(context, test_id,
            adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(@NonNull loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    //Handle the error.
                    loadAds(context)
                }

                override fun onAdLoaded(@NonNull rewardedAd: RewardedAd) {
                    mRewardedAd = rewardedAd
                    mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            mRewardedAd = null
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when ad fails to show.
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            //Don't forget to set the ad reference to null so you
                            // don't show the ad a second time.
                            loadAds(context)
                        }
                    }
                }
            })
    }

    fun showAds(context: Activity, successListener: ((response: Int) -> Unit)?) {
        if (mRewardedAd != null) {
            mRewardedAd?.show(context,
                OnUserEarnedRewardListener { rewardItem -> //Handle the reward.
                    val rewardAmount = rewardItem.amount
                    val rewardType = rewardItem.type
                    //txtDisplayMess.setText("$rewardAmount : $rewardType")
                    if (successListener != null) successListener(ResponseCode.SUCCESS)
                })
        } else {
            if (successListener != null) successListener(ResponseCode.FAIL)
        }
    }
}