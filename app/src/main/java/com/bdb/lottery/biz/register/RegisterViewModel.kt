package com.bdb.lottery.biz.register

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.domain.DomainRemoteDs
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class RegisterViewModel @ViewModelInject @Inject constructor(
    @ActivityContext private val context: Context,
    private val domain: DomainRemoteDs,
    private val app: AppRemoteDs
) : ViewModel() {
    fun register() {

    }
}