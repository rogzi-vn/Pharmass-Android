package vn.vistark.pharmass.ui.pharmacy.fragments.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.vistark.pharmass.R
import vn.vistark.pharmass.core.model.GoodsCategory

class PharmacyCategoryAdapter(val goodsCategories: ArrayList<GoodsCategory>) :
    RecyclerView.Adapter<PharmacyCategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacyCategoryViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.component_pharmacy_category_item, parent, false)
        return PharmacyCategoryViewHolder(v)
    }

    override fun getItemCount(): Int {
        return goodsCategories.size
    }

    override fun onBindViewHolder(holder: PharmacyCategoryViewHolder, position: Int) {
        val goodsCategory = goodsCategories[position]
        holder.bind(goodsCategory)
    }

}