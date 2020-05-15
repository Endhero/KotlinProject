package com.lcd.kotlinproject.utils

import com.lcd.base.remote.Ret
import com.lcd.kotlinproject.CentralOGManagerApplication
import com.lcd.kotlinproject.R
import com.lcd.kotlinproject.utils.CameraUtil.REMOTE_CONTROL_ERROR
import com.lcd.kotlinproject.view.widget.VideoView.Companion.INIT_VIDEO_CONTROL_ERROR
import com.lcd.kotlinproject.vm.alarm.AlarmListViewModel.Companion.SEARCH_ALARM_ERROR
import com.lcd.kotlinproject.vm.alarm.AlarmListViewModel.Companion.SEARCH_ALARM_FAIL
import com.lcd.kotlinproject.vm.alarm.AlarmViewModel.Companion.GET_DEVICE_LIST_ERROR
import com.lcd.kotlinproject.vm.alarm.AlarmViewModel.Companion.GET_DEVICE_LIST_FAIL
import com.lcd.kotlinproject.vm.camera.CameraListViewModel.Companion.GET_CAMERA_LIST_ERROR
import com.lcd.kotlinproject.vm.camera.CameraListViewModel.Companion.GET_CAMERA_LIST_FAIL
import com.lcd.kotlinproject.vm.camera.CameraViewModel.Companion.GET_DEVICEINFO_ERROR
import com.lcd.kotlinproject.vm.camera.CameraViewModel.Companion.VIDEO_PLAY_FAIL
import com.lcd.kotlinproject.vm.maintain.MaintainJobListViewModel.Companion.GET_MAINTAIN_JOB_LIST_ERROR
import com.lcd.kotlinproject.vm.maintain.MaintainJobListViewModel.Companion.GET_MAINTAIN_JOB_LIST_FAIL
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_MAINTAIN_PLAN_ERROR
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_MAINTAIN_PLAN_FAIL
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_ORGUID_ERROR
import com.lcd.kotlinproject.vm.maintain.MaintainPlanListViewModel.Companion.GET_ORGUID_FAIL
import com.lcd.kotlinproject.vm.maintain.MaintainRecordViewModel.Companion.GET_MAINTAIN_RECORD_ERROR
import com.lcd.kotlinproject.vm.maintain.MaintainRecordViewModel.Companion.GET_MAINTAIN_RECORD_FAIL
import com.tencent.bugly.crashreport.CrashReport
import com.videogo.exception.BaseException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by liuchangda 2020/ 03/ 18
 */

class TipUtil {
    private val mContext= CentralOGManagerApplication.getInstance()

    fun getErrorTip(nType: Int, throwable: Throwable): String {
        var strError = mContext.getString(R.string.unknown_error)

        when (nType) {
            GET_CAMERA_LIST_ERROR -> strError = mContext.getString(R.string.get_camera_list_error_format, analyseError(throwable))
            GET_DEVICE_LIST_ERROR -> strError = mContext.getString(R.string.get_device_list_error_format, analyseError(throwable))
            SEARCH_ALARM_ERROR -> strError = mContext.getString(R.string.search_alarm_error_format, analyseError(throwable))
            GET_DEVICEINFO_ERROR -> strError = mContext.getString(R.string.get_device_info_error_format, analyseError(throwable))
            INIT_VIDEO_CONTROL_ERROR -> strError = mContext.getString(R.string.video_control_error_format, analyseError(throwable))
            REMOTE_CONTROL_ERROR -> strError = analyseError(throwable)
            GET_ORGUID_ERROR -> strError = mContext.getString(R.string.get_orguid_error_format, analyseError(throwable))
            GET_MAINTAIN_PLAN_ERROR -> strError = mContext.getString(R.string.get_maintain_plan_error_format, analyseError(throwable))
            GET_MAINTAIN_RECORD_ERROR -> strError = mContext.getString(R.string.get_maintain_record_error_format, analyseError(throwable))
            GET_MAINTAIN_JOB_LIST_ERROR -> strError = mContext.getString(R.string.get_maintain_job_list_error_format, analyseError(throwable))
        }

        return strError
    }

    fun getFailTip(nType: Int, ret: Ret<*>): String {
        var strFail = mContext.getString(R.string.unknown_fail)

        when (nType) {
            GET_CAMERA_LIST_FAIL -> strFail = mContext.getString(R.string.get_camera_list_fail_format, ret.msg)
            GET_DEVICE_LIST_FAIL -> strFail = mContext.getString(R.string.get_device_list_fail_format, ret.msg)
            SEARCH_ALARM_FAIL -> strFail = mContext.getString(R.string.search_alarm_fail_format, ret.msg)
            GET_ORGUID_FAIL -> strFail = mContext.getString(R.string.get_orguid_fail_format, ret.msg)
            GET_MAINTAIN_PLAN_FAIL -> strFail = mContext.getString(R.string.get_maintain_plan_fail_format, ret.msg)
            GET_MAINTAIN_RECORD_FAIL -> strFail = mContext.getString(R.string.get_maintain_record_fail_format, ret.msg)
            GET_MAINTAIN_JOB_LIST_FAIL -> strFail = mContext.getString(R.string.get_maintain_job_list_fail_format, ret.msg)
        }

        strFail += "(" + ret.code.toString() + ")"

        return strFail
    }

    fun getFailTip(nType: Int, str: String?): String {
        var strFail = mContext.getString(R.string.unknown_fail)

        when (nType) {
            VIDEO_PLAY_FAIL -> strFail = mContext.getString(R.string.play_video_fail_format, str)
        }

        return strFail
    }

    private fun analyseError(throwable: Throwable) = when (throwable) {
        is BaseException -> {
            val errorinfo = throwable.errorInfo
            var str = mContext.getString(R.string.unknown_ezsdk_error)

            if (errorinfo != null) {
                if (!errorinfo.description.isNullOrBlank() && !errorinfo.description.contains("unknow")) {
                    str = errorinfo.description + "(" + errorinfo.errorCode + ")"
                } else {
                    str = getEzOpenError(errorinfo.errorCode)

                    if (str == mContext.getString(R.string.unknown_ezsdk_error)) {
                        str += "(" + errorinfo.errorCode + ")"
                    }
                }
            }

            str
        }
        is JSONException -> {
            CrashReport.postCatchedException(throwable)
            mContext.getString(R.string.reponse_error)
        }
        is UnknownHostException -> mContext.getString(R.string.net_error)
        is HttpException -> mContext.getString(R.string.server_error_format, throwable.code().toString() + "")
        is SocketTimeoutException -> mContext.getString(R.string.server_respose_time_out_error)
        is ConnectException -> mContext.getString(R.string.server_request_time_out_error)
        is SocketException -> mContext.getString(R.string.socket_error)
        is RuntimeException -> {
            CrashReport.postCatchedException(throwable)
            mContext.getString(R.string.internal_error)
        }
        else -> mContext.getString(R.string.unknown_error)
    }


    private fun getEzOpenError(nErrorCode: Int): String {
        var strError = mContext.getString(R.string.unknown_ezsdk_error)

        when (nErrorCode) {
            101001 -> strError = "用户名不合法"
            101002 -> strError = "用户名已被占用"
            101003 -> strError = "密码不合法"
            101004 -> strError = "密码为同一字符"
            101006 -> strError = "手机号码已经被注册"
            101007 -> strError = "手机号未注册"
            101008 -> strError = "手机号码不合法"
            101009 -> strError = "用户名与手机不匹配"
            101010 -> strError = "获取验证码失败"
            101011 -> strError = "验证码错误"
            101012 -> strError = "验证码失效"
            101013 -> strError = "用户不存在"
            101014 -> strError = "密码不正确或者appKey不正确"
            101015 -> strError = "用户被锁住"
            101021 -> strError = "验证参数异常"
            101026 -> strError = "邮箱已经被注册"
            101031 -> strError = "邮箱未注册"
            101032 -> strError = "邮箱不合法"
            101041 -> strError = "获取验证码过于频繁"
            101043 -> strError = "手机验证码输入错误超过规定次数"
            102000 -> strError = "设备不存在"
            102001 -> strError = "摄像机不存在"
            102003 -> strError = "设备不在线"
            102004 -> strError = "设备异常"
            102007 -> strError = "设备序列号不正确"
            102009 -> strError = "设备请求响应超时异常"
            105000 -> strError = "设备已被自己添加"
            105001 -> strError = "设备已被别人添加"
            105002 -> strError = "设备验证码错误"
            107001 -> strError = "邀请不存在"
            107002 -> strError = "邀请验证失败"
            107003 -> strError = "邀请用户不匹配"
            107004 -> strError = "无法取消邀请"
            107005 -> strError = "无法删除邀请"
            107006 -> strError = "不能邀请自己"
            107007 -> strError = "重复邀请"
            110001 -> strError = "参数错误"
            110002 -> strError = "accessToken异常或过期"
            110004 -> strError = "用户不存在"
            110005 -> strError = "appKey异常"
            110006 -> strError = "ip受限"
            110007 -> strError = "调用接口总次数达到上限请升级企业版"
            110008 -> strError = "签名错误"
            110009 -> strError = "签名参数错误"
            110010 -> strError = "签名超时"
            110011 -> strError = "未开通萤石云服务"
            110012 -> strError = "第三方账户与萤石账号已经绑定"
            110013 -> strError = "应用没有权限调用此接口"
            110014 -> strError = "APPKEY下对应的第三方userId和phone未绑定"
            110017 -> strError = "appKey不存在"
            110018 -> strError = "AccessToken与Appkey不匹配"
            110019 -> strError = "密码错误"
            110020 -> strError = "请求方法为空"
            110021 -> strError = "ticket校验失败"
            110022 -> strError = "透传目的地非法"
            110023 -> strError = "appKey与bundleId不匹配"
            110024 -> strError = "无透传权限"
            110025 -> strError = "appKey被禁止使用通明通道"
            110026 -> strError = "设备数量超出个人版限制，当前设备无法操作请升级企业版"
            110027 -> strError = "appKey数量超出安全限制，升级企业版可取消限制"
            110028 -> strError = "个人版账户抓图接口日调用次数超出限制请升级企业版"
            110029 -> strError = "调用频率超过个人版账户频率限制20次/分钟 请升级企业版"
            110030 -> strError = "appKey和appSecret不匹配 请检查appKey和appSecret是否对应"
            110031 -> strError = "子账户或萤石用户没有权限"
            110032 -> strError = "子账户不存在"
            110033 -> strError = "子账户未设置授权策略"
            110034 -> strError = "子账户已存在"
            110035 -> strError = "获取子账户AccessToken异常,子账户不存在或子账户不属于该开发者"
            110036 -> strError = "子账户被禁用"
            110051 -> strError = "无权限进行抓图"
            120001 -> strError = "通道不存在"
            120002 -> strError = "设备不存在"
            120003 -> strError = "参数异常，SDK版本过低"
            120004 -> strError = "参数异常，SDK版本过低"
            120005 -> strError = "安全认证失败"
            120006 -> strError = "网络异常"
            120007 -> strError = "设备不在线"
            120008 -> strError = "设备响应超时"
            120009 -> strError = "子账号不能添加设备"
            120010 -> strError = "设备验证码错误"
            120012 -> strError = "设备添加失败"
            120013 -> strError = "设备已被别人添加"
            120014 -> strError = "设备序列号不正确"
            120015 -> strError = "设备不支持该功能"
            120016 -> strError = "当前设备正在格式化"
            120017 -> strError = "设备已被自己添加"
            120018 -> strError = "该用户不拥有该设备"
            120019 -> strError = "设备不支持云存储服务"
            120020 -> strError = "设备在线，被自己添加"
            120021 -> strError = "设备在线，但是未被用户添加"
            120022 -> strError = "设备在线，但是已经被别的用户添加"
            120023 -> strError = "设备不在线，未被用户添加"
            120024 -> strError = "设备不在线，但是已经被别的用户添加"
            120025 -> strError = "重复申请分享"
            120026 -> strError = "视频广场不存在该视频"
            120027 -> strError = "视频转码失败"
            120028 -> strError = "设备固件升级包不存在"
            120029 -> strError = "设备不在线，但是已经被自己添加"
            120030 -> strError = "该用户不拥有该视频广场视频"
            120031 -> strError = "开启终端绑定，请在萤石客户端关闭终端绑定"
            120032 -> strError = "该用户下通道不存在"
            120033 -> strError = "无法收藏自己分享的视频"
            120034 -> strError = "该用户下无设备"
            120090 -> strError = "用户反馈失败"
            120095 -> strError = "APP包下载失败"
            120096 -> strError = "APP包信息删除失败"
            120101 -> strError = "视频不支持分享给本人"
            120102 -> strError = "无相应邀请信息"
            120103 -> strError = "好友已存在"
            120104 -> strError = "好友不存在"
            120105 -> strError = "好友状态错误"
            120106 -> strError = "对应群组不存在"
            120107 -> strError = "不能添加自己为好友"
            120108 -> strError = "当前用户和所添加用户不是好友关系"
            120109 -> strError = "对应分享不存在"
            120110 -> strError = "好友群组不属于当前用户"
            120111 -> strError = "好友不是等待验证状态"
            120112 -> strError = "添加应用下的用户为好友失败"
            120201 -> strError = "操作报警信息失败"
            120202 -> strError = "操作留言信息失败"
            120301 -> strError = "根据UUID查询报警消息不存在"
            120302 -> strError = "根据UUID查询图片不存在"
            120303 -> strError = "根据FID查询图片不存在"
            120305 -> strError = "设备ip解析错误"
            120401 -> strError = "用户云空间信息不存在"
            120402 -> strError = "云空间操作失败"
            120403 -> strError = "用户目录不存在"
            120404 -> strError = "要操作的目标目录不存在"
            120405 -> strError = "要删除的文件信息不存在"
            120406 -> strError = "已开通云存储"
            120407 -> strError = "开通记录失败"
            120500 -> strError = "获取数据错误"
            120501 -> strError = "开锁失败"
            120502 -> strError = "室内机未收到呼叫"
            120503 -> strError = "正在响铃"
            120504 -> strError = "室内机正在通话"
            120505 -> strError = "设备操作失败"
            120506 -> strError = "非法命令"
            120507 -> strError = "智能锁密码错误"
            120508 -> strError = "开关锁失败"
            120509 -> strError = "开关锁超时"
            120510 -> strError = "智能锁设备繁忙"
            120511 -> strError = "远程开锁功能未打开"
            120600 -> strError = "临时密码数已达上限"
            120601 -> strError = "添加临时密码失败"
            120602 -> strError = "删除临时密码失败"
            120603 -> strError = "该临时密码不存在"
            120604 -> strError = "指纹锁射频通信失败,请稍后再试"
            120605 -> strError = "其他用户正在认证中"
            120606 -> strError = "验证已启动,请在120s内进行本地验证和调用添加设备接口"
            120607 -> strError = "删除用户失败"
            120608 -> strError = "用户不存在"
            120609 -> strError = "设备响应超时,门锁通信故障或者电量不足"
            120610 -> strError = "获取临时密码列表失败"
            130001 -> strError = "用户不存在"
            130002 -> strError = "手机号码已经注册"
            130003 -> strError = "手机验证码错误"
            130004 -> strError = "终端绑定操作失败"
            149999 -> strError = "数据异常"
            150000 -> strError = "服务器异常"
            160000 -> strError = "设备不支持云台控制"
            160001 -> strError = "用户无云台控制权限"
            160002 -> strError = "设备云台旋转达到上限位"
            160003 -> strError = "设备云台旋转达到下限位"
            160004 -> strError = "设备云台旋转达到左限位"
            160005 -> strError = "设备云台旋转达到右限位"
            160006 -> strError = "云台当前操作失败"
            160007 -> strError = "预置点个数超过最大值"
            160009 -> strError = "正在调用预置点"
            160010 -> strError = "该预置点已经是当前位置"
            160011 -> strError = "预置点不存在"
            160013 -> strError = "设备版本已是最新"
            160014 -> strError = "设备正在升级"
            160015 -> strError = "设备正在重启"
            160016 -> strError = "加密未开启，无须关闭"
            160017 -> strError = "设备抓图失败"
            160018 -> strError = "设备升级失败"
            160019 -> strError = "加密已开启"
            160020 -> strError = "不支持该命令"
            160023 -> strError = "订阅操作失败"
            160024 -> strError = "取消订阅操作失败"
            160025 -> strError = "客流统计配置失败"
            160026 -> strError = "设备处于隐私遮蔽状态"
            160027 -> strError = "设备正在镜像操作"
            160028 -> strError = "设备正在键控动作"
            160029 -> strError = "设备处于语音对讲状态"
            160030 -> strError = "卡密输入错误次数过多，24小时后再输入"
            160031 -> strError = "卡密信息不存在"
            160032 -> strError = "卡密状态不对或已过期"
            160033 -> strError = "卡密非卖品，只能开通对应的绑定设备"
            160035 -> strError = "购买云存储服务失败"
            160040 -> strError = "添加的设备不在同一局域网"
            160041 -> strError = "添加的设备被其他设备关联或响应超时"
            160042 -> strError = "添加的设备密码错误"
            160043 -> strError = "添加的设备超出最大数量"
            160044 -> strError = "添加的设备网络不可达超时"
            160045 -> strError = "添加的设备的IP和其他通道的IP冲突"
            160046 -> strError = "添加的设备的IP和本设备的IP冲突"
            160047 -> strError = "码流类型不支持"
            160048 -> strError = "带宽超出系统接入带宽"
            160049 -> strError = "IP或者端口不合法"
            160050 -> strError = "添加的设备版本不支持需要升级才能接入"
            160051 -> strError = "添加的设备不支持接入"
            160052 -> strError = "添加的设备通道号出错"
            160053 -> strError = "添加的设备分辨率不支持"
            160054 -> strError = "添加的设备账号被锁定"
            160055 -> strError = "添加的设备取码流出错"
            160056 -> strError = "删除设备失败"
            160057 -> strError = "删除的设备未关联"
            160060 -> strError = "地址未绑定"
            160061 -> strError = "账户流量已超出或未购买，限制开通"
            160062 -> strError = "该通道直播已开通"
            160063 -> strError = "直播未使用或直播已关闭"
            160070 -> strError = "设备不能转移给自己"
            160071 -> strError = "设备不能转移，设备与其他设备存在关联关系"
            160072 -> strError = "设备不能转移，通道被分享给其他用户或者分享到视频广场"
            160073 -> strError = "云存储转移失败"
            160080 -> strError = "当前正在声源定位"
            160081 -> strError = "当前正在轨迹巡航"
            160082 -> strError = "设备正在响应本次声源定位"
            160083 -> strError = "当前正在开启隐私遮蔽"
            160084 -> strError = "当前正在关闭隐私遮蔽"
            170003 -> strError = "refreshToken不存在"
            170004 -> strError = "refreshToken已过期"
            320001 -> strError = "未知错误"
            320002 -> strError = "参数无效"
            320003 -> strError = "暂不支持此操作"
            320004 -> strError = "内存溢出"
            320005 -> strError = "创建CAS session失败"
            320006 -> strError = "创建cloud session失败"
            320007 -> strError = "token失效"
            320008 -> strError = "token池里面没有token,请传入token"
            320009 -> strError = "传入新的INIT_PARAM并reset"
            320010 -> strError = "请重试"
            320011 -> strError = "500毫秒后请重试"
            320012 -> strError = "token池已满"
            320013 -> strError = "P2P client超过限制"
            320014 -> strError = "sdk未初始化"
            320015 -> strError = "超时"
            320016 -> strError = "正在打洞中"
            320017 -> strError = "没有视频文件头"
            320018 -> strError = "解码错误/超时"
            320019 -> strError = "取消"
            320020 -> strError = "播放过程中预连接被用户清除预操作信息"
            320021 -> strError = "流加密码不对"
            320022 -> strError = "未传入播放窗口"
            360001 -> strError = "客户端请求超时"
            360002 -> strError = "对讲发起超时"
            360003 -> strError = "TTS的设备端发生错误"
            360004 -> strError = "TTS内部发生错误"
            360005 -> strError = "客户端发送的消息错误"
            360006 -> strError = "客户端接收发生错误"
            360007 -> strError = "TTS关闭了与客户端的连接"
            360010 -> strError = "设备正在对讲中"
            360011 -> strError = "设备响应超时"
            360012 -> strError = "设备不在线"
            360013 -> strError = "设备开启了隐私保护"
            360014 -> strError = "token校验无权限"
            360016 -> strError = "验证token失败"
            360102 -> strError = "TTS初始化失败"
            361001 -> strError = "对讲服务端排队超时"
            361002 -> strError = "对讲服务端处理超时"
            361003 -> strError = "设备链接对讲服务器超时"
            361004 -> strError = "服务器内部错误"
            361005 -> strError = "解析消息失败"
            361006 -> strError = "请求重定向"
            361007 -> strError = "请求url非法"
            361008 -> strError = "token失效"
            361009 -> strError = "设备验证码或者通信秘钥不匹配"
            361010 -> strError = "设备已经在对讲"
            361011 -> strError = "设备10s响应超时"
            361012 -> strError = "设备不在线"
            361013 -> strError = "设备开启隐私保护拒绝对讲"
            361014 -> strError = "token无权限"
            361015 -> strError = "设备返回session不存在"
            361016 -> strError = "验证token其他异常错误"
            361017 -> strError = "服务端监听设备建立端口超时"
            361018 -> strError = "设备链路异常"
            361019 -> strError = "对讲服务端不支持的信令消息"
            361020 -> strError = "对讲服务端解析对讲请求未携带会话描述能力集"
            361021 -> strError = "对讲服务端优先能力集结果为空"
            361022 -> strError = "cas链路异常"
            361023 -> strError = "对讲服务端分配对讲会话资源失败"
            361024 -> strError = "对讲服务端解析信令消息失败"
            380011 -> strError = "设备隐私保护中"
            380045 -> strError = "设备直连取流连接数量过大"
            380047 -> strError = "设备不支持该命令"
            380077 -> strError = "设备正在对讲中"
            380102 -> strError = "数据接收异常"
            380205 -> strError = "设备检测入参异常"
            380209 -> strError = "网络连接超时"
            380212 -> strError = "设备端网络连接超时"
            390001 -> strError = "通用错误返回"
            390002 -> strError = "入参为空指针"
            390003 -> strError = "入参值无效"
            390004 -> strError = "信令消息解析非法"
            390005 -> strError = "内存资源不足"
            390006 -> strError = "协议格式不对或者消息体长度超过STREAM_MAX_MSGBODY_LEN"
            390007 -> strError = "设备序列号长度不合法"
            390008 -> strError = "取流url长度不合法"
            390009 -> strError = "解析vtm返回vtdu地址不合法"
            390010 -> strError = "解析vtm返回级联vtdu地址不合法"
            390011 -> strError = "解析vtm返回会话标识长度不合法"
            390012 -> strError = "vtdu返回流头长度不合法"
            390013 -> strError = "vtdu会话长度非法"
            390014 -> strError = "回调函数未注册"
            390015 -> strError = "vtdu成功响应未携带会话标识"
            390016 -> strError = "vtdu成功响应未携带流头"
            390017 -> strError = "无数据流，尚未使用"
            390018 -> strError = "信令消息体PB解析失败"
            390019 -> strError = "信令消息体PB封装失败"
            390020 -> strError = "申请系统内存资源失败"
            390021 -> strError = "vtdu地址尚未获取到"
            390022 -> strError = "客户端尚未支持"
            390023 -> strError = "获取系统socket资源失败"
            390024 -> strError = "上层填充的StreamSsnId不匹配"
            390025 -> strError = "链接服务器失败"
            390026 -> strError = "客户端请求未收到服务端应答"
            390027 -> strError = "链路断开"
            390028 -> strError = "没有取流链接"
            390029 -> strError = "流成功停止"
            390030 -> strError = "客户端防串流校验失败"
            390031 -> strError = "应用层tcp粘包处理缓冲区满"
            390032 -> strError = "无效状态迁移"
            390033 -> strError = "无效客户端状态"
            390034 -> strError = "向vtm取流流媒体信息请求超时"
            390035 -> strError = "向代理取流请求超时"
            390036 -> strError = "向代理保活取流请求超时"
            390037 -> strError = "向vtdu取流请求超时"
            390038 -> strError = "向vtdu保活取流请求超时"
            391001 -> strError = "vtm地址或端口非法"
            391002 -> strError = "vtm生成文件描述符失败"
            391003 -> strError = "vtm设置文件描述符非阻塞失败"
            391004 -> strError = "vtm设置文件描述符阻塞失败"
            391005 -> strError = "vtm解析服务器ip失败"
            391006 -> strError = "vtm描述符select失败"
            391007 -> strError = "vtm文件描述符不在可读中"
            391008 -> strError = "vtm网络发生错误getsockopt"
            391009 -> strError = "vtm描述符select超时"
            391101 -> strError = "proxy地址或端口非法"
            391102 -> strError = "proxy生成文件描述符失败"
            391103 -> strError = "proxy设置文件描述符非阻塞失败"
            391104 -> strError = "proxy设置文件描述符阻塞失败"
            391105 -> strError = "proxy解析服务器ip失败"
            391106 -> strError = "proxy描述符select失败"
            391107 -> strError = "proxy文件描述符不在可读中"
            391108 -> strError = "proxy网络发生错误getsockopt"
            391109 -> strError = "proxy描述符select超时"
            391201 -> strError = "vtdu地址或端口非法"
            391202 -> strError = "vtdu生成文件描述符失败"
            391203 -> strError = "vtdu设置文件描述符非阻塞失败"
            391204 -> strError = "vtdu设置文件描述符阻塞失败"
            391205 -> strError = "vtdu解析服务器ip失败"
            391206 -> strError = "vtdu描述符select失败"
            391207 -> strError = "vtdu文件描述符不在可读中"
            391208 -> strError = "vtdu网络发生错误getsockopt"
            391209 -> strError = "vtdu描述符select超时，请稍候再试"
            395000 -> strError = "cas回复信令，发现内存已经释放，刷新重试"
            395400 -> strError = "私有化协议vtm检测到非法参数，刷新重试"
            395402 -> strError = "回放找不到录像文件"
            395403 -> strError = "操作码或信令密钥与设备不匹配"
            395404 -> strError = "设备不在线"
            395405 -> strError = "流媒体向设备发送或接受信令超时/cas响应超时"
            395406 -> strError = "token失效"
            395407 -> strError = "客户端的URL格式错误"
            395409 -> strError = "预览开启隐私保护"
            395410 -> strError = "设备达到最大连接数"
            395411 -> strError = "token无权限"
            395412 -> strError = "session不存在"
            395413 -> strError = "验证token其他异常"
            395415 -> strError = "设备通道错误"
            395416 -> strError = "设备达到最大连接数"
            395451 -> strError = "设备不支持的码流类型"
            395452 -> strError = "设备链接流媒体服务器失败，刷新重试"
            395500 -> strError = "服务器处理失败，刷新重试"
            395501 -> strError = "流媒体vtdu达到最大负载，请稍后重试"
            395503 -> strError = "vtm返回分配vtdu失败，服务器负载达到上限，请稍后重试"
            395544 -> strError = "设备返回无视频源，请检查设备是否接触良好"
            395545 -> strError = "视频分享时间已经结束"
            395546 -> strError = "vtdu返回达到取流并发路数限制，请升级为企业版"
            395560 -> strError = "蚁兵代理不支持的用户取流类型，会重定向到vtdu取流"
            395557 -> strError = "回放服务器等待流头超时，刷新重试"
            395600 -> strError = "分享设备不在分享时间内"
            395601 -> strError = "群组分享用户没权限"
            395602 -> strError = "群组分享权限变更"
            395556 -> strError = "ticket取流验证失败"
            395530 -> strError = "机房故障不可用，请稍后重试"
            395701 -> strError = "cas信令返回格式错误，刷新重试"
            396001 -> strError = "客户端参数出错"
            396099 -> strError = "客户端默认错误"
            396101 -> strError = "不支持的命令"
            396102 -> strError = "设备流头发送失败，刷新重试"
            396103 -> strError = "cas/设备返回错误1"
            396104 -> strError = "cas/设备返回错误-1"
            396105 -> strError = "设备返回错误码3"
            396106 -> strError = "设备返回错误码4"
            396107 -> strError = "设备返回错误码5"
            396108 -> strError = "cas信令回应重复"
            396109 -> strError = "视频广场取消分享"
            396110 -> strError = "设备信令默认错误"
            396501 -> strError = "设备数据链路和实际链路不匹配，刷新重试"
            396502 -> strError = "设备数据链路重复建立连接，刷新重试"
            396503 -> strError = "设备数据链路端口不匹配，刷新重试"
            396504 -> strError = "缓存设备数据链路失败，刷新重试"
            396505 -> strError = "设备发送确认头消息重复，刷新重试"
            396506 -> strError = "设备数据先于确定头部到达，刷新重试"
            396508 -> strError = "设备数据头部长度非法，刷新重试或者重启设备"
            396509 -> strError = "索引找不到设备数据管理块，刷新重试"
            396510 -> strError = "设备数据链路vtdu内存块协议状态不匹配"
            396511 -> strError = "设备数据头部没有streamkey错误"
            396512 -> strError = "设备数据头部非法"
            396513 -> strError = "设备数据长度过小"
            396514 -> strError = "设备老协议推流头部没有streamkey错误"
            396515 -> strError = "设备老协议推流数据非法"
            396516 -> strError = "设备老协议索引找不到内存管理块"
            396517 -> strError = "设备老协议推流数据非法"
            396518 -> strError = "设备数据包过大，刷新重试或者重启设备"
            396519 -> strError = "设备推流链路网络不稳定"
            396520 -> strError = "设备推流链路网络不稳定"
            400001 -> strError = "参数为空"
            400002 -> strError = "参数错误"
            400025 -> strError = "设备不支持对讲"
            400029 -> strError = "没有初始化或资源被释放"
            400030 -> strError = "json解析异常"
            400031 -> strError = "网络异常"
            400032 -> strError = "设备信息异常为空"
            400034 -> strError = "取流超时，刷新重试"
            400035 -> strError = "设备已加密，需要输入验证码"
            400036 -> strError = "播放验证码错误"
            400037 -> strError = "surfacehold错误"
            400100 -> strError = "未知错误"
            400200 -> strError = "player sdk出错"
            400300 -> strError = "内存溢出"
            400901 -> strError = "设备不在线"
            400902 -> strError = "accesstoken异常或失效"
            400903 -> strError = "当前账号开启了终端绑定"
            400904 -> strError = "设备正在对讲中"
            400905 -> strError = "设备开启了隐私保护，不允许预览、对讲等"
        }

        return strError
    }
}