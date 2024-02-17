package com.example.mydailyapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mydailyapp.dao.LottoDao
import com.example.mydailyapp.database.LottoDatabase
import com.example.mydailyapp.database.TaskDatabase
import com.example.mydailyapp.models.Lotto
import com.example.mydailyapp.utils.Resource
import com.example.mydailyapp.utils.StatusResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LottoRepository(application: Application) {

    private val lottoDao = LottoDatabase.getInstance(application).lottoDao

    private val _taskStateFlow = MutableStateFlow<Resource<Flow<List<Lotto>>>>(Resource.Loading())
    val taskStateFlow: StateFlow<Resource<Flow<List<Lotto>>>>
        get() = _taskStateFlow

    private val _statusLiveData = MutableLiveData<Resource<StatusResult>>()
    val statusLiveData: LiveData<Resource<StatusResult>>
        get() = _statusLiveData

    fun getLottoList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _taskStateFlow.emit(Resource.Loading())
                delay(500)
                val result = lottoDao.getLottoList()
                _taskStateFlow.emit(Resource.Success("loading", result))
            } catch (e: Exception) {
                println("Error!")
                _taskStateFlow.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    fun insertLotto(lotto: Lotto)  {
        try{
            _statusLiveData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch{
                val result = lottoDao.insertLotto(lotto)
                handleResult(result.toInt(), "Inserted Task Successfully", StatusResult.Added)
            }
        }catch (e: Exception) {
            _statusLiveData.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun deleteLottoUsingId(id: String) {
        try {
            _statusLiveData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = lottoDao.deleteLottoUsingId(id)
                handleResult(result, "Deleted Lotto Successfully", StatusResult.Deleted)

            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Resource.Error(e.message.toString()))
        }
    }

    private fun handleResult(result: Int, message: String, statusResult: StatusResult) {
        if (result != -1) {
            _statusLiveData.postValue(Resource.Success(message, statusResult))
        } else {
            _statusLiveData.postValue(Resource.Error("Something Went Wrong", statusResult))
        }
    }

}