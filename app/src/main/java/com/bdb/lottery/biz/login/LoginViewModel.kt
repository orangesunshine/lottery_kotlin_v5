package com.bdb.lottery.biz.login

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.base.viewmodel.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDataSource
import com.bdb.lottery.datasource.domain.DomainRemoteDataSource
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class LoginViewModel @ViewModelInject @Inject constructor(
    @ActivityContext private val context: Context,
    private val domain: DomainRemoteDataSource,
    private val app: AppRemoteDataSource
) : BaseViewModel() {
    fun login(username:String,pwd:String,rememberPwd:Boolean,verifyCode: String){

    }

    fun getCustomService(){

    }

    fun getApkVersion(){

    }

    fun trialPlay(){

    }

    fun userInfo(){

    }

    fun verifyCode(){

    }

    fun validate(){

    }

    fun plateformParasms(){

    }
}