package com.example.mydailyapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.mydailyapp.databinding.FragmentLottoBinding
import com.example.mydailyapp.adapters.LottoRVVBListAdapter
import com.example.mydailyapp.adapters.TaskRVVBListAdapter
import com.example.mydailyapp.models.Lotto
import com.example.mydailyapp.models.Task
import com.example.mydailyapp.utils.Status
import com.example.mydailyapp.utils.StatusResult
import com.example.mydailyapp.utils.setupDialog
import com.example.mydailyapp.utils.validateEditText
import com.example.mydailyapp.viewmodels.LottoViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.jsoup.Jsoup
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class Lotto_Fragment : Fragment() {
    private lateinit var mBinding: FragmentLottoBinding

    private val job2 = Job()

    private val lottoViewModel : LottoViewModel by lazy {
        ViewModelProvider(this)[LottoViewModel::class.java]
    }

    private val loadingDialog : Dialog by lazy {
        Dialog(requireContext()).apply {
            setupDialog(R.layout.loading_dialog)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentLottoBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var lottoNumbers = createLottoNumbers()
        super.onViewCreated(view, savedInstanceState)
        mBinding.generatebutton.setOnClickListener{
            lottoNumbers = createLottoNumbers()
            updateLottoBallImage(lottoNumbers)
        }

        CoroutineScope(Dispatchers.IO + job2).launch {
            updateThisWeekLottoBallImage(crawlLottoNumbers())
        }

        mBinding.listAddButton.setOnClickListener {

            val newlotto = Lotto(
                UUID.randomUUID().toString(),
                lottoNumbers[0],
                lottoNumbers[1],
                lottoNumbers[2],
                lottoNumbers[3],
                lottoNumbers[4],
                lottoNumbers[5],
                Date()
            )
            //hideKeyBoard(it)
            lottoViewModel.insertTask(newlotto)

        }

        LottoRVVBListAdapter.setContext(requireContext());

        val lottoRVVBListAdapter =  LottoRVVBListAdapter { type, position, lotto ->
            if(type == "delete"){
                lottoViewModel
                    .deleteLottoUsingId(lotto.id)
                // .deleteTask(task)

            }
        }

        mBinding.lottoRV.adapter = lottoRVVBListAdapter
        lottoRVVBListAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver()
        {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                mBinding.lottoRV.smoothScrollToPosition(positionStart)
            }
        })

        callGetLottoList(lottoRVVBListAdapter)
        lottoViewModel.getTaskList()
        statusCallback()

    }

    fun getDrawableID(number: Int): Int {
        val number = String.format("%02d", number)
        val string = "ball_$number"
        val id = resources.getIdentifier(string, "drawable", requireActivity().packageName)
        return id
    }

    private fun updateLottoBallImage(result: ArrayList<Int>) {
        with(mBinding) {
            ivGame0.setImageResource(getDrawableID(result[0]))
            ivGame1.setImageResource(getDrawableID(result[1]))
            ivGame2.setImageResource(getDrawableID(result[2]))
            ivGame3.setImageResource(getDrawableID(result[3]))
            ivGame4.setImageResource(getDrawableID(result[4]))
            ivGame5.setImageResource(getDrawableID(result[5]))
            //tvAnalyze.text = "번호합: ${result[6]}  홀:짝=${result[7]}:${result[8]}"
        }
    }

    private fun updateThisWeekLottoBallImage(result: ArrayList<Int>) {
        with(mBinding) {
            Resultnum1.setImageResource(getDrawableID(result[0]))
            Resultnum2.setImageResource(getDrawableID(result[1]))
            Resultnum3.setImageResource(getDrawableID(result[2]))
            Resultnum4.setImageResource(getDrawableID(result[3]))
            Resultnum5.setImageResource(getDrawableID(result[4]))
            Resultnum6.setImageResource(getDrawableID(result[5]))
            Resultnum7.setImageResource(getDrawableID(result[6]))
            //tvAnalyze.text = "번호합: ${result[6]}  홀:짝=${result[7]}:${result[8]}"
        }
    }



    private suspend fun crawlLottoNumbers(): ArrayList<Int> {
        val lottoNumbers = ArrayList<Int>()

        try {
            val doc = Jsoup.connect("https://dhlottery.co.kr/common.do?method=main").get()
            for (i in 1..6) {
                val drwtNo = doc.select("#drwtNo$i").text().toInt()
                lottoNumbers.add(drwtNo)
            }
            val bnusNo = doc.select("#bnusNo").text().toInt()
            lottoNumbers.add(bnusNo)

            val lottoDrwNo = doc.select("#lottoDrwNo").text().toInt()
            lottoNumbers.add(lottoDrwNo)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lottoNumbers
    }
    private fun whatIsRank(lottoNumbers: ArrayList<Int>, winningNumbers: ArrayList<Int>): String {
        var matchCount = 0
        for (i in 0..5) {
            if (lottoNumbers.contains(winningNumbers[i])) {
                matchCount += 1
            }
        }

        return if (matchCount == 6) {
            "1등"
        } else if (matchCount == 5) {
            if (lottoNumbers.contains(winningNumbers[6])) {
                "2등"
            } else {
                "3등"
            }
        } else if (matchCount == 4) {
            "4등"
        } else if (matchCount == 3) {
            "5등"
        } else {
            "낙첨"
        }
    }

    private fun createLottoNumbers(): ArrayList<Int> {
        val result = arrayListOf<Int>()

        val source = IntArray(45) { it + 1 }
        val seed =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.KOREA).format(Date()).hashCode()
                .toLong()
        val random = Random(seed)
        source.shuffle(random)
        source.slice(0..5).forEach { num ->
            result.add(num)
        }
        result.sort()

        var evenNumberCount = 0
        var oddNumberCount = 0
        for (num in result) {
            if (num % 2 == 0) {
                evenNumberCount += 1
            } else {
                oddNumberCount += 1
            }
        }
        result.add(result.sum())
        result.add(oddNumberCount)
        result.add(evenNumberCount)

        return result
    }


    private fun statusCallback() {
        lottoViewModel
            .statusLiveData
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        loadingDialog.show()
                    }

                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        when (it.data as StatusResult) {
                            StatusResult.Added -> {
                                Log.d("StatusResult", "Added")
                            }

                            StatusResult.Deleted -> {
                                Log.d("StatusResult", "Deleted")

                            }

                            StatusResult.Updated -> {
                                Log.d("StatusResult", "Updated")

                            }
                        }
                        //it.message?.let { it1 -> longToastShow(it1) }
                    }

                    Status.ERROR -> {
                        loadingDialog.dismiss()
                        //it.message?.let { it1 -> longToastShow(it1) }
                    }
                }
            }
    }

    private fun callGetLottoList(lottoRecycleViewAdapter : LottoRVVBListAdapter) {
        CoroutineScope(Dispatchers.Main).launch {
            lottoViewModel.lottoStateFlow.collectLatest{
                when(it.status){
                    Status.LOADING -> {
                        loadingDialog.show()
                    }
                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        it.data?.collect { lottoList ->
                            lottoRecycleViewAdapter.submitList(lottoList)
                        }
                    }
                    Status.ERROR -> {
                        loadingDialog.dismiss()
                    }
                }
            }
        }

    }

}