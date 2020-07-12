package vn.vistark.pharmass.ui.medicine_category_picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.vistark.pharmass.R
import vn.vistark.pharmass.core.model.MedicineCategory

class MedicineCategoryAdapter(val medicineCategories: ArrayList<MedicineCategory>) :
    RecyclerView.Adapter<SupplierPickerViewHolder>() {

    var onClicked: ((MedicineCategory) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierPickerViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.component_medicine_category_item, parent, false)
        return SupplierPickerViewHolder(v)
    }

    override fun getItemCount(): Int {
        return medicineCategories.size
    }

    override fun onBindViewHolder(holder: SupplierPickerViewHolder, position: Int) {
        val medicineCategory = medicineCategories[position]
        holder.bind(medicineCategory)
        holder.lnRoot.setOnClickListener {
            onClicked?.invoke(medicineCategory)
        }
    }

}