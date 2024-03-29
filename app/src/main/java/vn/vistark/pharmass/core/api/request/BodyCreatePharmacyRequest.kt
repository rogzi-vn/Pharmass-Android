package vn.vistark.pharmass.core.api.request


import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName
import vn.vistark.pharmass.core.api.RetrofitClient
import vn.vistark.pharmass.core.model.Coordinates
import vn.vistark.pharmass.core.model.Pharmacy
import vn.vistark.pharmass.core.model.UserAddress
import vn.vistark.pharmass.utils.RegexLibs
import vn.vistark.pharmass.utils.UrlUtils

data class BodyCreatePharmacyRequest(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("logo")
    var logo: String = "",
    @SerializedName("featureImages")
    var featureImages: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("shortDescription")
    var shortDescription: String = "",
    @SerializedName("introduction")
    var introduction: String = "",
    @SerializedName("address")
    var address: UserAddress = UserAddress(),
    @SerializedName("coordinates")
    var coordinates: Coordinates = Coordinates(),
    @SerializedName("confirmed")
    var confirmed: Boolean = false,
    @SerializedName("blocked")
    var blocked: Boolean = false,
    @SerializedName("openTime")
    var openTime: String = "07:00",
    @SerializedName("closeTime")
    var closeTime: String = "22:00",
    @SerializedName("dayInterval")
    var dayInterval: String = "",
    @SerializedName("owner")
    var users: User = User()
) : BaseObservable() {
    data class User(
        @SerializedName("id")
        var id: Int = -1
    )

    fun getFeatureImageFullAddress(): String {
        if (featureImages.isEmpty())
            return ""
        if (featureImages.contains(RegexLibs.url.toRegex()))
            return featureImages
        else
            return UrlUtils.standard("${RetrofitClient.BASE_URL}/$featureImages")
    }

    fun getLogoImageFullAddress(): String {
        if (logo.isEmpty())
            return ""
        if (logo.contains(RegexLibs.url.toRegex()))
            return logo
        else
            return UrlUtils.standard("${RetrofitClient.BASE_URL}/$logo")
    }

    fun fromPharmacy(pharmacy: Pharmacy) {
        id = pharmacy.id
        logo = pharmacy.logo
        featureImages = pharmacy.featureImages
        name = pharmacy.name
        shortDescription = pharmacy.shortDescription
        introduction = pharmacy.introduction
        address = pharmacy.address
        coordinates = pharmacy.coordinates
        confirmed = pharmacy.confirmed
        blocked = pharmacy.blocked
        openTime = pharmacy.openTime
        closeTime = pharmacy.closeTime
        dayInterval = pharmacy.dayInterval
        users = User(pharmacy.users.id)
    }
}