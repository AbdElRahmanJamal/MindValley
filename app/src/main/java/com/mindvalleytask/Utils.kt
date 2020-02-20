package com.mindvalleytask

import com.google.gson.Gson
import com.mindvalleytask.model.BaseResponse

class Utils {

    companion object {
        fun convertStringToBaseModel(stringJson: String): List<BaseResponse>? {
            return Gson().fromJson<Array<BaseResponse>>(stringJson, Array<BaseResponse>::class.java)
                .asList()
        }
    }
}