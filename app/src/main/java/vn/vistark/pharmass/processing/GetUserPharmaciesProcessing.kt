package vn.vistark.pharmass.processing

import android.content.Context
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vistark.pharmass.core.api.APIUtils
import vn.vistark.pharmass.core.api.ErrorLibrary
import vn.vistark.pharmass.core.api.request.BodyCreatePharmacyRequest
import vn.vistark.pharmass.core.model.Pharmacy
import vn.vistark.pharmass.core.api.response.Error400Response
import vn.vistark.pharmass.core.api.response.Error401Response
import vn.vistark.pharmass.core.constants.Constants
import vn.vistark.pharmass.utils.DialogNotify

class GetUserPharmaciesProcessing(
    context: Context,
    userId: Int = Constants.user.id
) {
    var onFinished: ((List<Pharmacy>?) -> Unit)? = null

    init {
        APIUtils.mAPIServices?.getPharmacyOfUser(userId)
            ?.enqueue(object : Callback<List<Pharmacy>> {
                override fun onFailure(call: Call<List<Pharmacy>>, t: Throwable) {
                    DialogNotify.error(
                        context,
                        t.message ?: "Lỗi không xác định khi lấy danh sách nhà thuốc"
                    )
                }

                override fun onResponse(
                    call: Call<List<Pharmacy>>,
                    response: Response<List<Pharmacy>>
                ) {
                    if (response.isSuccessful) {
                        // Khi thực hiện thành công
                        onFinished?.invoke(response.body())
                        return
                    } else if (response.code() == 400) {
                        // Khi nhận được thông báo lỗi 400
                        val error400 = Gson()
                            .fromJson(response.errorBody()?.string(), Error400Response::class.java)
                        if (error400 != null) {
                            val extractError = ErrorLibrary.find(
                                error400.message.firstOrNull()?.messages?.firstOrNull()?.id ?: ""
                            )
                            if (extractError.isNotEmpty()) {
                                DialogNotify.error(context, extractError)
                                onFinished?.invoke(null)
                                return
                            }
                        }
                    } else if (response.code() == 401) {
                        Constants.token = "" // Xóa token ngay
                        val error401Response = Gson().fromJson(
                            response.errorBody()?.string(),
                            Error401Response::class.java
                        )
                        if (error401Response != null) {
                            DialogNotify.error(context, error401Response.message)
                        }
                        onFinished?.invoke(null)
                        return
                    }
                    // Nếu lỗi không nằm trong dự tính
                    DialogNotify.error(context, response.message())
                    onFinished?.invoke(null)
                }
            })
    }
}