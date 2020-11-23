package com.bdb.lottery.service

import android.app.Service
import java.util.*

object ServiceManager {
    private val mServiceStack: Stack<Service> = Stack()
    fun push(service: Service) {
        mServiceStack.push(service)
    }

    fun pop(service: Service) {
        mServiceStack.remove(service)
    }

    fun stopAll() {
        var service = mServiceStack.pop()
        while (null != service) {
            service.stopSelf()
            service = mServiceStack.pop()
        }
    }
}