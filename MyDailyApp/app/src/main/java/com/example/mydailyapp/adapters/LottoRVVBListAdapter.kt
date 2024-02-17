package com.example.mydailyapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mydailyapp.databinding.ViewLottoLayoutBinding
import com.example.mydailyapp.models.Lotto
import java.text.SimpleDateFormat
import java.util.*


class LottoRVVBListAdapter(
    private val deleteUpdateCallback : (type:String,position: Int, lotto: Lotto) -> Unit
) : ListAdapter<Lotto, LottoRVVBListAdapter.ViewHolder>(DiffCallback())
{

    class ViewHolder(val viewLottoLayoutBinding: ViewLottoLayoutBinding)
        : RecyclerView.ViewHolder(viewLottoLayoutBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ViewLottoLayoutBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )

    }

    companion object {

        private lateinit var context: Context

        fun setContext(con: Context) {
            context=con
        }
        fun getDrawableID(number: Int): Int {
            val number = String.format("%02d", number)
            val string = "ball_$number"
            val id = context?.resources?.getIdentifier(string, "drawable", context!!.packageName)
            return id!!
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lotto = getItem(position)

        with(holder.viewLottoLayoutBinding) {
            listLotto1.setImageResource(getDrawableID(lotto.lottoNum1))
            listLotto2.setImageResource(getDrawableID(lotto.lottoNum2))
            listLotto3.setImageResource(getDrawableID(lotto.lottoNum3))
            listLotto4.setImageResource(getDrawableID(lotto.lottoNum4))
            listLotto5.setImageResource(getDrawableID(lotto.lottoNum5))
            listLotto6.setImageResource(getDrawableID(lotto.lottoNum6))
        }

//        holder.viewLottoLayoutBinding.listLotto1.text = lotto.lottoNum1.toString()
//        holder.viewLottoLayoutBinding.listLotto2.text = lotto.lottoNum2.toString()
//        holder.viewLottoLayoutBinding.listLotto3.text = lotto.lottoNum3.toString()
//        holder.viewLottoLayoutBinding.listLotto4.text = lotto.lottoNum4.toString()
//        holder.viewLottoLayoutBinding.listLotto5.text = lotto.lottoNum5.toString()
//        holder.viewLottoLayoutBinding.listLotto6.text = lotto.lottoNum6.toString()

        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())
        holder.viewLottoLayoutBinding.dateTxt.text = dateFormat.format(lotto.date)

        holder.viewLottoLayoutBinding.deleteImg.setOnClickListener {
            if(holder.adapterPosition != -1){
                deleteUpdateCallback( "delete",holder.adapterPosition , lotto)
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Lotto>() {
        override fun areItemsTheSame(oldItem: Lotto, newItem: Lotto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Lotto, newItem: Lotto): Boolean {
            return oldItem == newItem
        }
    }

}