package com.lcd.kotlinproject.view.impl.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.*
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.OnClick
import butterknife.OnTouch
import com.ezvizuikit.open.EZUIPlayer
import com.lcd.base.exception.GlobalError
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.data.model.remote.respose.CameraData
import com.lcd.kotlinproject.utils.DateUtil
import com.lcd.kotlinproject.utils.DialogUtil
import com.lcd.kotlinproject.utils.PermissionUtil
import com.lcd.kotlinproject.utils.PermissionUtil.REQUEST_AUDIO
import com.lcd.kotlinproject.utils.PermissionUtil.REQUEST_STORAGE
import com.lcd.kotlinproject.view.widget.VideoView
import com.lcd.kotlinproject.vm.camera.CameraViewModel
import com.lcd.kotlinproject.vm.camera.CameraViewModel.Companion.GET_DEVICEINFO_ERROR
import com.lcd.kotlinproject.vm.camera.CameraViewModel.Companion.GET_DEVICEINFO_SUCCESS
import com.videogo.openapi.EZConstants.EZTalkbackCapability
import com.videogo.openapi.bean.EZDeviceInfo
import com.videogo.util.ConnectionDetector
import com.videogo.util.Utils
import com.videogo.widget.CheckTextButton
import com.videogo.widget.RingView
import com.yuwell.androidbase.tool.ResourceUtil
import com.yuwell.androidbase.view.ToolbarActivity
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

class Camera : ToolbarActivity() {
    @BindView(R.id.button_fullscreen)
    lateinit var checktextbuttonFullScreen: CheckTextButton
    @BindView(R.id.imagebutton_play)
    lateinit var imagebuttonPlay: ImageButton
    @BindView(R.id.imagebutton_voice)
    lateinit var imagebuttonVoice: ImageButton
    @BindView(R.id.videoview)
    lateinit var videoview: VideoView
    @BindView(R.id.button_quality)
    lateinit var buttonQuality: Button
    @BindView(R.id.checkbox_talk)
    lateinit var checkboxTalk: CheckBox
    @BindView(R.id.imageview)
    lateinit var imageview: ImageView
    @BindView(R.id.title)
    lateinit var linearlayoutTitle: LinearLayout
    @BindView(R.id.layout_play_control)
    lateinit var linearlayoutPlayControl: LinearLayout
    @BindView(R.id.linearlayout_play)
    lateinit var linearlayoutPlay: LinearLayout
    @BindView(R.id.linearlayout_function)
    lateinit var linearlayoutFunction: LinearLayout
    @BindView(R.id.linearlayout_remote_control)
    lateinit var linearlayoutRemoteControl: LinearLayout

    private var mIsSoundOpened = true
    private var mCameraData: CameraData? = null
    private var mOrientation = Configuration.ORIENTATION_PORTRAIT
    private var mQuality = 1
    private var mHandler: Handler? = null
    private var mQualityPopupWindow: PopupWindow? = null
    private var mTalkBackPopupWindow: PopupWindow? = null
    private var mCameraViewModel: CameraViewModel? = null
    private var mDeviceInfo: EZDeviceInfo? = null
    private var mPlayState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initViewModel()

        mCameraViewModel?.bindVideoView(this, videoview)
        mCameraViewModel?.bindFullScreenBotton(this, checktextbuttonFullScreen)
        mCameraViewModel?.getDeviceInfo(mCameraData!!.CameraID)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun initView(){
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        videoview.setRatio(16 * 1.0f / 9)
        videoview.viewTreeObserver?.addOnGlobalLayoutListener {
            val textureview = videoview.getChildAt(0) as TextureView
            val layoutparams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)

            textureview.layoutParams = layoutparams
        }

        linearlayoutTitle.bringToFront()
        linearlayoutPlayControl.bringToFront()

        mCameraData = intent.getSerializableExtra("data") as CameraData

        mCameraData?.let {
            videoview.setUrl(it.EZHDurl)
            videoview.setLoadingView(initProgressBar())
            if (!it.EncryPwd.isNullOrBlank()) {
                videoview.setPlayVerifyCode(it.EncryPwd!!)
            }
        }

        toolbar?.setNavigationOnClickListener {
            if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                finish()
            }
        }

        mHandler = Handler(Looper.getMainLooper())
    }

    private fun initViewModel(){
        mCameraViewModel = ViewModelProviders.of(this)[CameraViewModel::class.java]
        mCameraViewModel?.getResult()?.
        observe(this, Observer<Message> {message->
            when (message.what) {
                GET_DEVICEINFO_SUCCESS -> {
                    mDeviceInfo = message.obj as EZDeviceInfo

                    if (mDeviceInfo?.isSupportTalk == EZTalkbackCapability.EZTalkbackNoSupport) {
                        checkboxTalk.isEnabled = false

                        Toast.makeText(this@Camera, R.string.device_donnot_support_talk, Toast.LENGTH_SHORT).show()
                    } else {
                        checkboxTalk.isEnabled = true
                    }
                }
                GET_DEVICEINFO_ERROR -> {
                    longToast( message.obj as CharSequence)
                    checkboxTalk.isEnabled = false
                }
            }
        })

        mCameraViewModel?.globalErrorLiveData?.observe(this, Observer<GlobalError>{
            dismissProgressDialog()
            showMessage(ResourceUtil.getStringId(application, "error_" + it.type))
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_STORAGE || requestCode == REQUEST_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                val listenerPositive = DialogInterface.OnClickListener {_, _->
                        when (requestCode) {
                            REQUEST_STORAGE -> PermissionUtil.applyPermissionStorage(this)
                            REQUEST_AUDIO -> PermissionUtil.applyPermissionAudio(this)
                        }
                    }

                val listenerNegative = DialogInterface.OnClickListener {dialog, _ -> dialog.dismiss()}

                when (requestCode) {
                    REQUEST_STORAGE -> DialogUtil.showConfirmDialog(this, R.string.need_permission, getString(R.string.apply_for_storage_permission), listenerPositive, listenerNegative)
                    REQUEST_AUDIO -> DialogUtil.showConfirmDialog(this, R.string.need_permission, getString(R.string.apply_for_audio_permission), listenerPositive, listenerNegative)
                }
            } else {
                when (requestCode) {
                    REQUEST_STORAGE -> mCameraViewModel!!.capturePicture(DateUtil.currentTimeStamp.toString() + ".jpg")
                    REQUEST_AUDIO -> openTalkPopupWindow()
                }
            }
        }
    }

    override fun getLayoutId() = R.layout.activity_camera

    override fun getTitleId() = R.string.monitor

    @SuppressLint("SourceLockedOrientationActivity")
    @OnClick(R.id.button_capture, R.id.imagebutton_play, R.id.imagebutton_voice, R.id.button_fullscreen, R.id.videoview, R.id.button_quality, R.id.checkbox_talk, R.id.button_play)
    fun onClick(view: View) {
        when (view.id) {
            R.id.button_capture -> mCameraViewModel?.capturePicture(DateUtil.currentTimeStamp + ".jpg")
            R.id.button_play, R.id.imagebutton_play ->
                when (videoview.status) {
                    EZUIPlayer.STATUS_PLAY -> {
                        mPlayState = EZUIPlayer.STATUS_STOP
                        imagebuttonPlay.setImageResource(R.drawable.ic_button_play)
                        linearlayoutPlay.visibility = View.VISIBLE
                        videoview.stopPlay()
                    }

                    EZUIPlayer.STATUS_STOP -> {
                        mPlayState = EZUIPlayer.STATUS_PLAY
                        imagebuttonPlay.setImageResource(R.drawable.ic_button_pause)
                        linearlayoutPlay.visibility = View.GONE
                        videoview.startPlay()
                    }
                }
            R.id.imagebutton_voice ->
                if (mIsSoundOpened) {
                    mIsSoundOpened = false
                    imagebuttonVoice.setImageResource(R.drawable.ic_voice_close)
                    videoview.closeSound()
                } else {
                    mIsSoundOpened = true
                    imagebuttonVoice.setImageResource(R.drawable.ic_voice)
                    videoview.openSound()
                }
            R.id.button_fullscreen ->
                when(mOrientation){
                    Configuration.ORIENTATION_PORTRAIT -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Configuration.ORIENTATION_LANDSCAPE -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            R.id.videoview ->
                if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (linearlayoutTitle.visibility == View.INVISIBLE) {
                        linearlayoutPlayControl.visibility = View.VISIBLE
                        linearlayoutTitle.visibility = View.VISIBLE
                        mHandler?.postDelayed({ 
                            mQualityPopupWindow?.dismiss()
                            linearlayoutPlayControl.visibility = View.INVISIBLE
                            linearlayoutTitle.visibility = View.INVISIBLE
                    }, 5000)
                } else {
                        mHandler?.removeCallbacksAndMessages(null)
                        linearlayoutPlayControl.visibility = View.INVISIBLE
                        linearlayoutTitle.visibility = View.INVISIBLE
                    }
                }
            R.id.button_quality -> openQualityPopupWindow(view)
            R.id.checkbox_talk -> openTalkPopupWindow()
        }
    }

    @OnTouch(R.id.button_left, R.id.button_up, R.id.button_right, R.id.button_down)
    fun onTouch(view: View, motionevent: MotionEvent): Boolean {
        if (mDeviceInfo == null) {
            longToast( R.string.get_device_info_error)
            return true
        }

        if (!mDeviceInfo!!.isSupportPTZ) {
            longToast(R.string.device_donnot_support_remote_control)
            return true
        }

        val nAction = motionevent.action
        val nId = view.id

        mCameraData?.let {
            when (nAction) {
                MotionEvent.ACTION_DOWN -> when (nId) {
                    R.id.button_left -> {
                        mCameraViewModel!!.startMoveLeft(it.CameraID!!, it.ChannelNo)
                        imageview.setImageResource(R.drawable.ic_controller_left)
                    }
                    R.id.button_up -> {
                        mCameraViewModel!!.startMoveUp(it.CameraID!!, it.ChannelNo)
                        imageview.setImageResource(R.drawable.ic_controller_up)
                    }
                    R.id.button_right -> {
                        mCameraViewModel!!.startMoveRight(it.CameraID!!, it.ChannelNo)
                        imageview.setImageResource(R.drawable.ic_controller_right)
                    }
                    R.id.button_down -> {
                        mCameraViewModel!!.startMoveDown(it.CameraID!!, it.ChannelNo)
                        imageview.setImageResource(R.drawable.ic_controller_down)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    when (nId) {
                        R.id.button_left -> mCameraViewModel!!.stopMoveLeft(it.CameraID!!, it.ChannelNo)
                        R.id.button_up -> mCameraViewModel!!.stopMoveUp(it.CameraID!!, it.ChannelNo)
                        R.id.button_right -> mCameraViewModel!!.stopMoveRight(it.CameraID!!, it.ChannelNo)
                        R.id.button_down -> mCameraViewModel!!.stopMoveDown(it.CameraID!!, it.ChannelNo)
                    }

                    imageview.setImageResource(R.drawable.ic_controller)
                }
            }
        }

        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        mHandler?.removeCallbacksAndMessages(null)
        mHandler = null
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onBackPressed() {
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            super.onBackPressed()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mOrientation = newConfig.orientation
        mHandler?.removeCallbacksAndMessages(null)
        mCameraViewModel?.changeOrientation(newConfig.orientation)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLanscapeView()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortraitView()
        }
    }

    private fun setPortraitView() {
        linearlayoutTitle.visibility = View.VISIBLE
        linearlayoutPlayControl.visibility = View.VISIBLE
        linearlayoutRemoteControl.visibility = View.VISIBLE
        linearlayoutFunction.visibility = View.VISIBLE

        val toolbar = toolbar
        toolbar?.setBackgroundColor(Color.WHITE)
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back_black)

        val textview = toolbar?.findViewById<TextView>(R.id.text_title)
        textview?.setTextColor(resources.getColor(android.R.color.black))

        var layoutparams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutparams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        linearlayoutTitle.layoutParams = layoutparams

        layoutparams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutparams.addRule(RelativeLayout.BELOW, R.id.title)
        videoview.layoutParams = layoutparams

        layoutparams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutparams.addRule(RelativeLayout.BELOW, R.id.videoview)

        linearlayoutPlayControl.layoutParams = layoutparams
        linearlayoutPlayControl.setBackgroundColor(Color.BLACK)
        linearlayoutPlayControl.viewTreeObserver?.addOnGlobalLayoutListener {
                if (checkboxTalk.isChecked && mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    mTalkBackPopupWindow?.showAsDropDown(linearlayoutPlayControl)
                }
            }

        buttonQuality.setBackgroundResource(R.drawable.shape_round_rectangle_white_stroke)
    }

    private fun setLanscapeView() {
        linearlayoutTitle.visibility = View.INVISIBLE
        linearlayoutPlayControl.visibility = View.INVISIBLE
        linearlayoutRemoteControl.visibility = View.GONE
        linearlayoutFunction.visibility = View.GONE
        val toolbar = toolbar
        toolbar!!.setBackgroundResource(R.color.black_translucent)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white)
        val textview = toolbar.findViewById<TextView>(R.id.text_title)
        textview.setTextColor(resources.getColor(android.R.color.white))
        var layoutparams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        layoutparams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        videoview.layoutParams = layoutparams
        layoutparams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutparams.addRule(RelativeLayout.ALIGN_TOP, R.id.videoview)
        linearlayoutTitle.layoutParams = layoutparams
        layoutparams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutparams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.videoview)
        linearlayoutPlayControl.layoutParams = layoutparams
        linearlayoutPlayControl.setBackgroundResource(R.color.black_translucent)
        
        if (checkboxTalk.isChecked) {
            mTalkBackPopupWindow!!.dismiss()
        }
        
        buttonQuality.setBackgroundResource(R.drawable.shape_round_rectangle_white_stroke_lanscape)
    }

    private fun openQualityPopupWindow(view: View) {
         mQualityPopupWindow?.dismiss()

        val layoutinflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewgroup = layoutinflater.inflate(R.layout.layout_popwindow_quality, null, true) as ViewGroup
        val onclicklistener = View.OnClickListener { view ->
                if (!ConnectionDetector.isNetworkAvailable(this)) {
                    Toast.makeText(this, R.string.net_inavailable, Toast.LENGTH_SHORT).show()

                    return@OnClickListener
                }

                when (view.id) {
                    R.id.textview_flunet -> {
                        mQuality = 0
                        videoview.stopPlay()
                        videoview.setUrl(mCameraData!!.EZurl)
                        videoview.startPlay()
                        buttonQuality.setText(R.string.quality_flunet)
                    }
                    R.id.textview_hd -> {
                        mQuality = 1
                        videoview.stopPlay()
                        videoview.setUrl(mCameraData!!.EZHDurl)
                        videoview.startPlay()
                        buttonQuality.setText(R.string.quality_hd)
                    }
                }

                 mQualityPopupWindow?.dismiss()
            }

        val textviewHd = viewgroup.findViewById<View>(R.id.textview_hd) as TextView
        textviewHd.setOnClickListener(onclicklistener)

        val textviewFlunet = viewgroup.findViewById<View>(R.id.textview_flunet) as TextView
        textviewFlunet.setOnClickListener(onclicklistener)

        when (mQuality) {
            0 -> textviewHd.visibility = View.VISIBLE
            1 -> textviewFlunet.visibility = View.VISIBLE
        }

        mQualityPopupWindow = PopupWindow(viewgroup, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
        mQualityPopupWindow?.setBackgroundDrawable(BitmapDrawable())
        mQualityPopupWindow?.setOnDismissListener(PopupWindow.OnDismissListener {  mQualityPopupWindow?.dismiss() })

        val nWidthMode = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val nHeightMode = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        mQualityPopupWindow?.contentView?.measure(nWidthMode, nHeightMode)

        val nYOffset = - (view.height +  mQualityPopupWindow?.contentView!!.measuredHeight)
        val nXOffset = (view.width -  mQualityPopupWindow?.contentView!!.measuredWidth) / 2

         mQualityPopupWindow?.showAsDropDown(view, nXOffset, nYOffset)
    }

    private fun openTalkPopupWindow() {
        if (mDeviceInfo != null) {
            val layoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val viewgroup = layoutInflater.inflate(R.layout.layout_talkback, null, true) as ViewGroup
            viewgroup.isFocusable = true
            viewgroup.isFocusableInTouchMode = true

            val ringview = viewgroup.findViewById<View>(R.id.ringview) as RingView
            val buttonTalkBackControl = viewgroup.findViewById<View>(R.id.button_talkback_control) as Button
            val imagebuttonClose = viewgroup.findViewById<View>(R.id.imagebutton_close) as ImageButton

            imagebuttonClose.setOnClickListener {
                mTalkBackPopupWindow?.dismiss()
                checkboxTalk.isChecked = false
                videoview.stopTalk()
                toast(R.string.stop_talk)
            }

            if (mCameraViewModel!!.startTalk()) {
                toast( R.string.start_talk)

                if (mDeviceInfo!!.isSupportTalk == EZTalkbackCapability.EZTalkbackFullDuplex) {
                    ringview.visibility = View.VISIBLE
                    buttonTalkBackControl.isEnabled = false
                    buttonTalkBackControl.setText(R.string.talking)
                } else if (mDeviceInfo!!.isSupportTalk == EZTalkbackCapability.EZTalkbackHalfDuplex) {
                    buttonTalkBackControl.setOnTouchListener { _, motionevent ->
                        when (motionevent.action) {
                            MotionEvent.ACTION_DOWN -> {
                                ringview.visibility = View.VISIBLE
                                videoview.setVoiceTalkStatus(true)
                            }
                            MotionEvent.ACTION_UP -> {
                                ringview.visibility = View.GONE
                                videoview.setVoiceTalkStatus(false)
                            }
                        }
                        false
                    }
                }

                val height = findViewById<View>(R.id.linearlayout_function).height + findViewById<View>(R.id.linearlayout_remote_control).height

                mTalkBackPopupWindow = PopupWindow(viewgroup, RelativeLayout.LayoutParams.MATCH_PARENT, height, true)
                mTalkBackPopupWindow?.animationStyle = R.style.popupwindow
                mTalkBackPopupWindow?.isFocusable = false
                mTalkBackPopupWindow?.isOutsideTouchable = false
                mTalkBackPopupWindow?.showAsDropDown(linearlayoutPlayControl)
                mTalkBackPopupWindow?.update()

                ringview.post {ringview?.setMinRadiusAndDistance(buttonTalkBackControl.height / 2f, Utils.dip2px(this, 22f))}
            }
        } else {
            toast( R.string.get_device_info_error)
        }
    }

    private fun initProgressBar(): RelativeLayout? {
        val relativelayout = RelativeLayout(this)
        relativelayout.setBackgroundColor(Color.BLACK)

        var layoutparams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        relativelayout.layoutParams = layoutparams

        layoutparams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutparams.addRule(RelativeLayout.CENTER_IN_PARENT)

        val progressbar = ProgressBar(this)
        progressbar.indeterminateDrawable = resources.getDrawable(R.drawable.progress)
        relativelayout.addView(progressbar, layoutparams)

        return relativelayout
    }

    companion object{
        fun start(context: Context, cameradata: CameraData?) {
            val starter = Intent(context, Camera::class.java)
            starter.putExtra("data", cameradata)
            context.startActivity(starter)
        }
    }
}