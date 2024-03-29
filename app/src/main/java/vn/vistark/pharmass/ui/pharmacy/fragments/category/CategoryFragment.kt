package vn.vistark.pharmass.ui.pharmacy.fragments.category

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.ybq.android.spinkit.SpinKitView
import com.google.gson.Gson
import vn.vistark.pharmass.R
import vn.vistark.pharmass.core.constants.RequestCode
import vn.vistark.pharmass.core.model.GoodsCategory
import vn.vistark.pharmass.core.model.Pharmacy
import vn.vistark.pharmass.event_bus.BroadCastEvent
import vn.vistark.pharmass.processing.GetPharmacyGoodsCategoryProcessing
import vn.vistark.pharmass.ui.pharmacy.PharmacyActivity
import vn.vistark.pharmass.ui.pharmacy.fragments.supplier.SupplierFragment
import vn.vistark.pharmass.ui.pharmacy.pharmacy_ware_house.PharmacyWareHouseActivity

class CategoryFragment : Fragment(), BroadCastEvent {
    private var pharmacyJson: String? = null
    var pharmacy: Pharmacy? = null

    lateinit var loadingIcon: SpinKitView
    lateinit var rvCategories: RecyclerView
    val goodsCategories = ArrayList<GoodsCategory>()
    lateinit var adapter: PharmacyCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pharmacyJson = it.getString(ARG_PHARMACY_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_category, container, false)
        initViews(v)
        loadCategory()
        return v
    }

    private fun loadCategory() {
        if (pharmacyJson != null && pharmacyJson!!.isNotEmpty()) {
            pharmacy = Gson().fromJson(pharmacyJson, Pharmacy::class.java)
        }
        rvCategories.layoutManager = LinearLayoutManager(context)
        rvCategories.setHasFixedSize(true)

        if (pharmacy == null) {
            (activity as PharmacyActivity).initEventBus(this)
            SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE).apply {
                titleText = "Không thể truy cập vào thông tin của nhà thuốc này"
                contentText = "THÔNG TIN TRỐNG"

                setCancelable(false)
                showCancelButton(false)
                setConfirmButton("QUAY LẠI") {
                    it.dismiss()
                    activity?.finish()
                }
                show()
            }
            return
        } else {
            adapter = PharmacyCategoryAdapter(pharmacy!!, goodsCategories)
            rvCategories.adapter = adapter
            adapter.onClicked = {
                val intent = Intent(context, PharmacyWareHouseActivity::class.java)
                intent.putExtra(Pharmacy::class.java.simpleName, pharmacyJson)
                intent.putExtra(GoodsCategory::class.java.simpleName, Gson().toJson(it))
                (context as AppCompatActivity).startActivityForResult(
                    intent,
                    RequestCode.REQUEST_GOODS_CATEGORY_RELOAD_CODE
                )
            }

            loadData()
        }
    }

    private fun loadData() {
        goodsCategories.clear()
        loadingIcon.visibility = View.VISIBLE
        // Xu ly va lay du lieu
        GetPharmacyGoodsCategoryProcessing(context!!, pharmacy!!.id).onFinished = {
            loadingIcon.visibility = View.GONE
            if (it != null) {
                goodsCategories.addAll(it.sortedBy { it.name })
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(
                    context,
                    "Hiện tại bạn chưa có mục hàng nào!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initViews(v: View) {
        rvCategories = v.findViewById(R.id.rvCategories)
        loadingIcon = v.findViewById(R.id.loadingIcon)
    }

    companion object {
        public const val RELOAD_GOODS_CATEGORY = "RELOAD_GOODS_CATEGORY"
        const val ARG_PHARMACY_KEY = "param1"

        @JvmStatic
        fun newInstance(pharmacy: String) =
            CategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PHARMACY_KEY, pharmacy)
                }
            }
    }

    override fun receivedString(key: String, value: String) {
        if (key == RELOAD_GOODS_CATEGORY) {
            loadData()
        }
    }
}