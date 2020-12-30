package com.bdb.lottery.biz.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.biz.main.MainActivity
import com.bdb.lottery.biz.register.RegisterActivity
import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.domain.DomainLocalDs
import com.bdb.lottery.extension.*
import com.bdb.lottery.utils.device.TDevice
import com.bdb.lottery.utils.ui.app.Apps
import com.bdb.lottery.utils.ui.app.TApp
import com.bumptech.glide.load.model.GlideUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.login_activity.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity(R.layout.login_activity) {
    private val vm by viewModels<LoginViewModel>()

    @Inject
    lateinit var domainLocalDs: DomainLocalDs

    @Inject
    lateinit var tDevice: TDevice

    @Inject
    lateinit var tApp: TApp

    private var mPwdVisible = false

    override fun observe() {
        //进入页面判断是否显示验证码
        ob(vm.needValidated.getLiveData()) {
            showValidate(it)
        }

        //登录连续错误，要求显示验证码
        ob(vm.validateLd.getLiveData()) {
            showValidate(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginVersionNameTv.text = Apps.getAppVersionName()//显示版本信息

        loginToRegisterBt.setOnClickListener { startNdFinish<RegisterActivity>() }//立即注册

        loginTrialPlayBt.setOnClickListener { vm.trialPlay { startNdFinish<MainActivity>() } }//立即试玩

        //在线客服
        loginOnlineCustomServiceBt.setOnClickListener {
            vm.cachePriCustomServiceUrl()
        }

        //登录
        login_bt.setOnClickListener {
            //校验用户名
            val username: String = login_username_et.text.toString()
            if (username.isEmpty()) {
                toast(R.string.login_username_error_hint)
                return@setOnClickListener
            }

            //校验密码
            val pwd: String = loginPwdEt.text.toString()
            if (pwd.isEmpty()) {
                toast(R.string.login_pwd_error_hint)
                return@setOnClickListener
            }

            //记住密码
            val rememberPwd = false

            //校验验证码
            val verifycode: String = loginVerifyCodeEt.text.toString()
            vm.login(username, pwd, rememberPwd, verifycode) { startNdFinish<MainActivity>() }
        }

        //密码清空按钮
        loginPwdEt.doAfterTextChanged { loginPwdClearIv.visible(null != it && it.length > 0) }
        loginPwdClearIv.setOnClickListener { loginPwdEt.setText("") }
        loginPwdCipherIv.setOnClickListener {
            switchPwdCipher()
            loginPwdEt.setSelection(loginPwdEt.text.toString().length)
        }//切换明文、密文


        vm.validate()//进入判断是否显示验证码
        vm.refreshRsaKey()//刷新rsa公钥
    }

    //切换明文、密文
    fun switchPwdCipher() {
        mPwdVisible = !mPwdVisible
        loginPwdCipherIv.setImageResource(if (mPwdVisible) R.drawable.login_pwd_plaintext_ic else R.drawable.login_pwd_ciphertext_ic)
        loginPwdEt.transformationMethod =
            if (mPwdVisible) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
    }

    //显示验证码
    private fun showValidate(validate: Boolean?) {
        validate?.let {
            if (it) {
                login_verifycode_ll.visible(true)
                val url =
                    domainLocalDs.getDomain() + URL.URL_VERIFYCODE + "?" + Math.random()
                val gliderUrl = GlideUrl(
                    url
                ) {
                    val header: MutableMap<String, String> = HashMap()
                    header["C"] = "a"
                    header["D"] = tDevice.getDeviceUUid()
                    header
                }
                loginVerifyCodeIv.loadImageUrl(gliderUrl)
            }
        }
    }

    override fun getVm(): BaseViewModel? {
        return vm
    }

    override fun attachActionBar(): Boolean {
        return false
    }

    override fun attachStatusBar(): Boolean {
        return true
    }
}