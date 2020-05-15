package com.lcd.kotlinproject.utils

import android.widget.Toast
import com.lcd.kotlinproject.CentralOGManagerApplication
import com.videogo.openapi.EZConstants
import com.videogo.openapi.EZConstants.EZPTZAction
import com.videogo.openapi.EZConstants.EZPTZCommand
import com.videogo.openapi.EZOpenSDK
import com.videogo.openapi.bean.EZDeviceInfo
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

object CameraUtil {
    const val DEVICE_ONLINE = 1
    const val DEVICE_OFFLINE = 0
    const val REMOTE_CONTROL_ERROR = 17

    fun getDeviceInfo(strDeviceSerial: String?): Observable<EZDeviceInfo?>{
        return Observable.create {emitter: ObservableEmitter<EZDeviceInfo?> ->
            try {
                val ezdeviceinfo = EZOpenSDK.getInstance().getDeviceInfo(strDeviceSerial)
                emitter.onNext(ezdeviceinfo)
            } catch (exception: Exception) {
                emitter.onError(exception)
            }

            emitter.onComplete()
        }
    }

    fun captureCamera(strDeviceSerial: String?, nCameraNo: Int): Observable<String?> {
        return Observable.create(ObservableOnSubscribe { emitter: ObservableEmitter<String> ->
            try {
                val strUrl = EZOpenSDK.getInstance().captureCamera(strDeviceSerial, nCameraNo)

                if (!strUrl.isNullOrEmpty()) {
                    emitter.onNext(strUrl)
                } else {
                    emitter.onNext("")
                }
            } catch (exception: java.lang.Exception) {
                emitter.onError(exception)
            }
            emitter.onComplete()
        })
    }

    fun startMoveUp(strDeviceSerial: String, nCameraNo: Int) {
        controlCamera(strDeviceSerial, nCameraNo, EZPTZCommand.EZPTZCommandUp, EZPTZAction.EZPTZActionSTART)
    }

    fun startMoveDown(strDeviceSerial: String, nCameraNo: Int) {
        controlCamera(strDeviceSerial, nCameraNo, EZPTZCommand.EZPTZCommandDown, EZPTZAction.EZPTZActionSTART)
    }

    fun startMoveLeft(strDeviceSerial: String, nCameraNo: Int) {
        controlCamera(strDeviceSerial, nCameraNo, EZPTZCommand.EZPTZCommandLeft, EZPTZAction.EZPTZActionSTART)
    }

    fun startMoveRight(strDeviceSerial: String, nCameraNo: Int) {
        controlCamera(strDeviceSerial, nCameraNo, EZPTZCommand.EZPTZCommandRight, EZPTZAction.EZPTZActionSTART)
    }

    fun stopMoveUp(strDeviceSerial: String, nCameraNo: Int) {
        controlCamera(strDeviceSerial, nCameraNo, EZPTZCommand.EZPTZCommandUp, EZPTZAction.EZPTZActionSTOP)
    }

    fun stopMoveDown(strDeviceSerial: String, nCameraNo: Int) {
        controlCamera(strDeviceSerial, nCameraNo, EZPTZCommand.EZPTZCommandDown, EZPTZAction.EZPTZActionSTOP)
    }

    fun stopMoveLeft(strDeviceSerial: String, nCameraNo: Int) {
        controlCamera(strDeviceSerial, nCameraNo, EZPTZCommand.EZPTZCommandLeft, EZPTZAction.EZPTZActionSTOP)
    }

    fun stopMoveRight(strDeviceSerial: String, nCameraNo: Int) {
        controlCamera(strDeviceSerial, nCameraNo, EZPTZCommand.EZPTZCommandRight, EZPTZAction.EZPTZActionSTOP)
    }

    private fun controlCamera(strDeviceSerial: String, nCameraNo: Int, ezptzcommand: EZPTZCommand, ezptzaction: EZPTZAction) {
        Observable.create(ObservableOnSubscribe { emitter: ObservableEmitter<Boolean> ->
            try {
                emitter.onNext(EZOpenSDK.getInstance().controlPTZ(strDeviceSerial, nCameraNo, ezptzcommand, ezptzaction, EZConstants.PTZ_SPEED_DEFAULT)
                )
            } catch (exception: java.lang.Exception) {
                emitter.onError(exception)
            }

            emitter.onComplete()
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result: Boolean -> }
            ) { throwable: Throwable ->
                CentralOGManagerApplication.getInstance().toast(TipUtil().getErrorTip(REMOTE_CONTROL_ERROR, throwable))
            }
    }
}