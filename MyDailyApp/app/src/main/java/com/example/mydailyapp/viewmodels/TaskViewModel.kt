package com.example.mydailyapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Query
import com.example.mydailyapp.models.Task
import com.example.mydailyapp.repository.TaskRepository
import com.example.mydailyapp.utils.Resource

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository(application)

    fun getTaskList() = taskRepository.getTaskList()

    fun insertTask(task: Task) : MutableLiveData<Resource<Long>>{
        return taskRepository.insertTask(task)
    }

    fun deleteTask(task: Task): MutableLiveData<Resource<Int>>{
        return taskRepository.deleteTask(task)
    }

    fun deleteTaskUsingId(taskId: String): MutableLiveData<Resource<Int>>{
        return taskRepository.deleteTaskUsingId(taskId)
    }

    fun updateTask(task: Task): MutableLiveData<Resource<Int>> {
        return taskRepository.updateTask(task)
    }

    fun updateTaskPaticularField(taskId: String,title:String,description:String): MutableLiveData<Resource<Int>> {
        return taskRepository.updateTaskPaticularField(taskId, title, description)
    }

//    val taskStateFlow get() =  taskRepository.taskStateFlow
//    val statusLiveData get() =  taskRepository.statusLiveData
//    val sortByLiveData get() =  taskRepository.sortByLiveData

//    fun setSortBy(sort:Pair<String,Boolean>){
//        taskRepository.setSortBy(sort)
//    }
//
//    fun getTaskList(isAsc : Boolean, sortByName:String) {
//        taskRepository.getTaskList(isAsc, sortByName)
//    }

//    fun insertTask(task: Task){
//        taskRepository.insertTask(task)
//    }


//
//    fun deleteTask(task: Task) {
//        taskRepository.deleteTask(task)
//    }
//
//    fun deleteTaskUsingId(taskId: String){
//        taskRepository.deleteTaskUsingId(taskId)
//    }
//
//    fun updateTask(task: Task) {
//        taskRepository.updateTask(task)
//    }
//
//    fun updateTaskPaticularField(taskId: String,title:String,description:String) {
//        taskRepository.updateTaskPaticularField(taskId, title, description)
//    }
//    fun searchTaskList(query: String){
//        taskRepository.searchTaskList(query)
//    }
}