package vn.vistark.pharmass.core.api

import android.annotation.SuppressLint

data class ErrorLibrary(val code: String, val name: String) {
    companion object {
        val all: Array<ErrorLibrary>
            get() {
                return arrayOf(
                    ErrorLibrary("Auth.form.error.username.taken", "Tên đăng nhập đã được sử dụng"),
                    ErrorLibrary("Auth.form.error.email.taken", "Email đã được sử dụng"),
                    ErrorLibrary("Auth.form.error.invalid", "Sai thông tin đăng nhập")
                )
            }

        @SuppressLint("DefaultLocale")
        fun find(code: String): String {
            println("Find with code: $code")
            return all.find { it.code.toLowerCase() == code.toLowerCase() }?.name ?: ""
        }
    }
}