package com.bdb.lottery.utils.net.retrofit

import retrofit2.Converter

class StringConverterFactory private constructor(): Converter.Factory() {


    companion object{
        fun create(){
            StringConverterFactory()
        }
    }
}