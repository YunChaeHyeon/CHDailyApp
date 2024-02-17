package com.example.mydailyapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mydailyapp.Lotto.LottoService
import com.example.mydailyapp.databinding.FragmentLottoBinding
import com.example.mydailyapp.databinding.FragmentTodolistBinding
import com.example.mydailyapp.models.LottoResponse
import com.example.mydailyapp.models.fetchLatestLottoNumbers
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class Lotto_Fragment : Fragment() {
    private lateinit var mBinding: FragmentLottoBinding

    private val job = Job()

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
        super.onViewCreated(view, savedInstanceState)

        val lottoNumbers = createLottoNumbers()

        CoroutineScope(Dispatchers.IO + job).launch {
            val winningNumbers = async { crawlLottoNumbers() }
            val rank = whatIsRank(lottoNumbers, winningNumbers.await())
            val text = "${winningNumbers.await()} : $rank"

            withContext(Dispatchers.Main) {
                mBinding.lottoNumbersTextView.text = "이번 주 로또 당첨 번호  "+text
            }
        }
    }

    private suspend fun crawlLottoNumbers() : ArrayList<Int> {
        val lottoNumbers = ArrayList<Int>()
        // var doc: Document? = null
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

        } catch (e: IOException) {
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


}