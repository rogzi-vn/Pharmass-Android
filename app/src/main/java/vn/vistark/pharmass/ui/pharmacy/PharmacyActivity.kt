package vn.vistark.pharmass.ui.pharmacy

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_pharmacy.*
import kotlinx.android.synthetic.main.components_toolbar.*
import vn.vistark.pharmass.R
import vn.vistark.pharmass.component.address_picker.AddressPickerActivity
import vn.vistark.pharmass.component.pharmacy_options_picker.PharmacyOptionsPickerActivity
import vn.vistark.pharmass.component.time_range_picker.TimeRangePickerActivity
import vn.vistark.pharmass.core.api.request.BodyCreatePharmacyRequest
import vn.vistark.pharmass.core.constants.RequestCode
import vn.vistark.pharmass.core.model.Bill
import vn.vistark.pharmass.core.model.Pharmacy
import vn.vistark.pharmass.core.model.User
import vn.vistark.pharmass.core.model.UserAddress
import vn.vistark.pharmass.event_bus.BroadCastEvent
import vn.vistark.pharmass.processing.DeletePharmacyProcessing
import vn.vistark.pharmass.ui.pharmacy.fragments.category.CategoryFragment
import vn.vistark.pharmass.ui.pharmacy.fragments.inventory_limit.InventoryLimitFragment
import vn.vistark.pharmass.ui.pharmacy.fragments.supplier.SupplierFragment
import vn.vistark.pharmass.ui.pharmacy.pharmacy_bill.PharmacyBillActivity
import vn.vistark.pharmass.ui.pharmacy.pharmacy_updater.PharmacyUpdaterActivity
import vn.vistark.pharmass.ui.work.WorkActivity
import vn.vistark.pharmass.utils.GlideUtils
import java.util.*

class PharmacyActivity : AppCompatActivity() {
    var stringEventBus: BroadCastEvent? = null

    fun initEventBus(stringEventBus: BroadCastEvent) {
        this.stringEventBus = stringEventBus
    }

    var pharmacy: Pharmacy? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pharmacy)
        val pharmacyJson = intent.getStringExtra(PharmacyActivity::class.java.simpleName) ?: ""
        pharmacy = Gson().fromJson(pharmacyJson, Pharmacy::class.java)
        if (pharmacyJson.isEmpty() || pharmacy == null) {
            Toast.makeText(
                this,
                "Không thể tiến hành thao tác với nhà thuốc này!",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        tvToolbarLabel.text = pharmacy!!.name
        inits()
        initBottomMenu()
        initEvents()
    }

    private fun initEvents() {
        ivBackButton.setOnClickListener {
            onBackPressed()
        }
        civUserAvatar.setOnClickListener {
            val intent = Intent(this, PharmacyBillActivity::class.java)
            intent.putExtra(
                Pharmacy::class.java.simpleName,
                Gson().toJson(pharmacy)
            )
            startActivity(intent)
        }
    }

    private fun initBottomMenu() {
        // Thêm các tab vào cho menu
        PharmacyBottomMenu.all(pharmacy).forEach {
            tabLayout.addTab(tabLayout.newTab().setText(it.title))
        }
        viewPager.adapter = PharmacyPagerAdapter(supportFragmentManager, pharmacy)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun inits() {
        ivExpandAuthorInfomation.visibility = View.GONE
        ivBackButton.visibility = View.VISIBLE
        tvToolbarLabel.isSelected = true
        tvToolbarLabel.isAllCaps = false
        tvToolbarLabel.setPadding(
            tvToolbarLabel.paddingLeft,
            tvToolbarLabel.paddingTop,
            tvToolbarLabel.paddingRight,
            tvToolbarLabel.paddingBottom
        )
        civUserAvatar.setImageResource(R.drawable.ic_medical_report)
        civUserAvatar.setPadding(
            tvToolbarLabel.paddingLeft + 20,
            tvToolbarLabel.paddingTop + 20,
            tvToolbarLabel.paddingRight,
            tvToolbarLabel.paddingBottom + 20
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.REQUEST_TIME_RANGE_PICKER_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val billsJson =
                data.getStringExtra(Bill::class.java.simpleName) ?: ""
            stringEventBus?.receivedString(Bill::class.java.simpleName, billsJson)

            val pickedType =
                data.getStringExtra(TimeRangePickerActivity::class.java.simpleName) ?: ""
            stringEventBus?.receivedString(
                TimeRangePickerActivity::class.java.simpleName,
                pickedType
            )
        } else if (requestCode == RequestCode.REQUEST_PHARMACY_OPTION_PICKER_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val actionSelected =
                data.getStringExtra(PharmacyOptionsPickerActivity::class.java.simpleName) ?: ""
            if (actionSelected == PharmacyOptionsPickerActivity.EDIT) {
                val intent = Intent(this, PharmacyUpdaterActivity::class.java)
                var bodyCreatePharmacyRequest = BodyCreatePharmacyRequest()
                bodyCreatePharmacyRequest.fromPharmacy(pharmacy!!)
                intent.putExtra(
                    PharmacyUpdaterActivity::class.java.simpleName,
                    Gson().toJson(bodyCreatePharmacyRequest)
                )
                startActivity(intent)
                finish()
            } else if (actionSelected == PharmacyOptionsPickerActivity.REMOVE) {
                removeCurrentPharmacyConfirm()
            }
        } else if ((requestCode == RequestCode.REQUEST_SUPPLIER_UPDATE_CODE || requestCode == RequestCode.REQUEST_SUPPLIER_CREATE_CODE) && resultCode == Activity.RESULT_OK) {
            stringEventBus?.receivedString(
                SupplierFragment.RELOAD_SUPPLIER_LIST,
                ""
            )
        } else if (requestCode == RequestCode.REQUEST_GOODS_CATEGORY_RELOAD_CODE && resultCode == Activity.RESULT_OK) {
            stringEventBus?.receivedString(
                CategoryFragment.RELOAD_GOODS_CATEGORY,
                ""
            )
        }
    }

    private fun removeCurrentPharmacyConfirm() {
        SweetAlertDialog(this).apply {
            titleText = "Bạn thực sự muốn xóa nhà thuốc có tên [${pharmacy!!.name}] hay không?"
            contentText = "XÓA NHÀ THUỐC"

            setConfirmButton("Xóa") {
                it.dismiss()
                DeletePharmacyProcessing(this@PharmacyActivity, pharmacy!!.id)
                    .onFinished = {
                    if (it != null) {
                        Toast.makeText(
                            this@PharmacyActivity,
                            "Đã xóa thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        WorkActivity.update()
                    }
                }
                finish()
            }
            setCancelButton("Hủy") {
                it.dismiss()
            }
            show()
        }
    }
}