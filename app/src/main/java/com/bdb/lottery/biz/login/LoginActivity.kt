package com.bdb.lottery.biz.login

import android.os.Bundle
import androidx.activity.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.biz.register.RegisterActivity
import com.bdb.lottery.const.HttpConstUrl
import com.bdb.lottery.const.ICache
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.Devices
import com.bdb.lottery.utils.cache.Cache
import com.bumptech.glide.load.model.GlideUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.login_activity.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity(R.layout.login_activity) {
    private val vm by viewModels<LoginViewModel>()

    @Inject
    lateinit var domainLocalDs: DomainLocalDs

    override fun observe() {
        observe(vm.needValidated.getLiveData()) {
            showValidate(it)
        }

        observe(vm.validateLd.getLiveData()) {
            showValidate(it)
        }
    }

    //显示验证码
    fun showValidate(validate: Boolean?) {
        validate?.let {
            if (it) {
                login_verifycode_ll.visible(true)
                val gliderUrl = GlideUrl(
                    DomainLocalDs().getDomain() + HttpConstUrl.URL_GET_VERIFYCODE
                ) {
                    val header: MutableMap<String, String> = HashMap()
                    header["C"] = "a"
                    header["D"] = Devices.getDeviceUUid()
                    header
                }
                login_verifycode_iv.loadImageUrl(gliderUrl)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //立即注册
        login_toregister_bt.setOnClickListener({ start<RegisterActivity>() })

        //立即试玩
        login_trial_play_bt.setOnClickListener({ vm.trialPlay() })

        //在线客服
        login_online_customservice_bt.setOnClickListener({
            if (Cache.getString(ICache.CACHE_CUSTOM_SERVICE_URL).isSpace()) {
                //缓存
            } else {
                vm.getCustomService()
            }
        })

        //登录
        login_bt.setOnClickListener({
            //校验用户名
            val username: String = login_username_et.text.toString()
            if (username.isNullOrEmpty()) toast(R.string.login_username_error_hint)

            //校验密码
            val pwd: String = login_pwd_et.text.toString()
            if (pwd.isNullOrEmpty()) toast(R.string.login_pwd_error_hint)

            //记住密码
            val rememberPwd = false

            //校验验证码
            val verifycode: String = login_verifycode_et.text.toString()

            vm.login(username, pwd, rememberPwd, verifycode, { toast(it) })
        })

        vm.plateformParasms()

    }

    //验证码
    fun renderVerifycode() {

    }

    //切换密码明文，密文
    fun switchPwd() {

    }

    //显示版本信息
    fun renderApkVersion() {

    }

    override fun attachActionBar(): Boolean {
        return false
    }

    override fun attachStatusBar(): Boolean {
        return true
    }
}