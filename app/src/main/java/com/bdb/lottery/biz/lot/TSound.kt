package com.bdb.lottery.biz.lot

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import com.bdb.lottery.biz.globallivedata.AccountManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TSound @Inject constructor(
    @ApplicationContext private val context: Context, private val accountManager: AccountManager
) {
    private val mSoundPool: SoundPool = if (Build.VERSION.SDK_INT > 21) {
        //传入音频数量
        SoundPool.Builder().setMaxStreams(4).setAudioAttributes(
            //AudioAttributes是一个封装音频各种属性的方法、设置音频流的合适的属性
            AudioAttributes.Builder().setLegacyStreamType(
                AudioManager.STREAM_MUSIC
            ).build()
        ).build()
    } else {
        SoundPool(4, AudioManager.STREAM_MUSIC, 0)
    }

    private val mBetBallSoundId: Int
    private val BET_BALL_ASSERT_PATH = "sound/bet_num_ball_click.mp3"

    private val mChipSoundId: Int
    private val CHIP_ASSERT_PATH = "sound/chip_click.mp3"

    private val mCountDownSoundId: Int
    private val COUNT_DOWN_ASSERT_PATH = "sound/bet_countdown.mp3"

    private val mOpenSoundId: Int
    private val OPEN_ASSERT_PATH = "sound/open_lottery.mp3"

    init {
        mBetBallSoundId = mSoundPool.load(context.assets.openFd(BET_BALL_ASSERT_PATH), 1)
        mChipSoundId = mSoundPool.load(context.assets.openFd(CHIP_ASSERT_PATH), 1)
        mCountDownSoundId = mSoundPool.load(context.assets.openFd(COUNT_DOWN_ASSERT_PATH), 1)
        mOpenSoundId = mSoundPool.load(context.assets.openFd(OPEN_ASSERT_PATH), 1)
    }

    //根据用户登录、喜好判断能否播放声音
    private fun canPlaySound(): Boolean {
//        return accountManager.isLogin() && accountManager.getUserInfo()
//            ?.let { !it.isShiWan && it.userPersonalise?.voiceStatus ?: false } ?: false
        return true
    }

    //播放投注球点击音效
    fun playBetBallClickSound() {
        if (!canPlaySound()) return
        mSoundPool.play(mBetBallSoundId, 1f, 1f, 1, 0, 1f)
    }

    //播放筹码点击音效
    fun playChipClickSound() {
        if (!canPlaySound()) return
        mSoundPool.play(mChipSoundId, 1f, 1f, 1, 0, 1f)
    }

    //播放投注倒计时音效
    fun playBetCountDownSound() {
        if (!canPlaySound()) return
        mSoundPool.play(mCountDownSoundId, 1f, 1f, 1, -1, 1f)//循环播放
    }

    //播放投注倒计时音效
    fun stopBetCountDownSound() {
        mSoundPool.stop(mCountDownSoundId)
    }

    //播放开奖音效
    fun playOpenSound() {
        if (!canPlaySound()) return
        mSoundPool.play(mOpenSoundId, 1f, 1f, 1, 0, 1f)
    }
}