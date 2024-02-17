package com.example.mydailyapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mydailyapp.models.Lotto
import com.example.mydailyapp.models.Task
import com.example.mydailyapp.repository.LottoRepository
import com.example.mydailyapp.repository.TaskRepository

class LottoViewModel(application: Application) : AndroidViewModel(application) {

    private val lottoRepository = LottoRepository(application)

    val lottoStateFlow get() =  lottoRepository.taskStateFlow
    val statusLiveData get() =  lottoRepository.statusLiveData

    fun getTaskList() {
        lottoRepository.getLottoList()
    }

    fun insertTask(lotto: Lotto) {
        return lottoRepository.insertLotto(lotto)
    }

    fun deleteLottoUsingId(id: String) {
        return lottoRepository.deleteLottoUsingId(id)
    }

}