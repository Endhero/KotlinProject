package com.lcd.kotlinproject.view.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.Toast
import com.ezvizuikit.open.EZUIPlayer
import com.lcd.kotlinproject.utils.TipUtil
import com.videogo.openapi.EZPlayer
import org.jetbrains.anko.longToast

class VideoView @JvmOverloads constructor(context: Context,  attrs: AttributeSet? = null, defStyleAttr: Int = 0): EZUIPlayer(context, attrs, defStyleAttr){
    private var mVideoPlayer: EZPlayer? = null
    private var bOpenSound = true
    private var mTipUtil: TipUtil = TipUtil()

    companion object{
        const val INIT_VIDEO_CONTROL_ERROR = 16
    }

    private fun getVideoPlayer(): EZPlayer? {
        if (mVideoPlayer == null) {
            try {
                val clazz: Class<*> = EZUIPlayer::class.java
                val field = clazz.getDeclaredField("mEZPlayer")
                field.isAccessible = true
                mVideoPlayer = field[this] as EZPlayer
            } catch (exception: Exception) {
                if (mTipUtil == null) {
                    mTipUtil = TipUtil()
                }
                exception.printStackTrace()

                context.longToast(mTipUtil.getErrorTip(INIT_VIDEO_CONTROL_ERROR, exception))
            }
        }

        return mVideoPlayer
    }

    fun capturePicture() = if (getVideoPlayer() != null) mVideoPlayer?.capturePicture() else null

    fun closeSound() {
        if (getVideoPlayer() != null) {
            mVideoPlayer?.closeSound()
            bOpenSound = false
        }
    }

    fun openSound() {
        if (getVideoPlayer() != null) {
            mVideoPlayer?.openSound()
            bOpenSound = true
        }
    }

    fun setPlayVerifyCode(strVerifyCode: String) {
        if (getVideoPlayer() != null) {
            mVideoPlayer?.setPlayVerifyCode(strVerifyCode)
        }
    }

    fun startTalk() {
        if (getVideoPlayer() != null) {
            mVideoPlayer?.closeSound()
            mVideoPlayer?.startVoiceTalk()
        }
    }

    fun stopTalk() {
        if (getVideoPlayer() != null) {
            mVideoPlayer?.stopVoiceTalk()

            if (bOpenSound) {
                mVideoPlayer?.openSound()
            }
        }
    }

    fun setVoiceTalkStatus(bStatus: Boolean) {
        if (getVideoPlayer() != null) {
            mVideoPlayer?.setVoiceTalkStatus(bStatus)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        mVideoPlayer?.release()
    }
}