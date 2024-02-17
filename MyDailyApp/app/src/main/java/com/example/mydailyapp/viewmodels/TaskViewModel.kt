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

    val taskStateFlow get() =  taskRepository.taskStateFlow
    val statusLiveData get() =  taskRepository.statusLiveData
    //val sortByLiveData get() =  taskRepository.sortByLiveData

    fun getTaskList() {
        taskRepository.getTaskList()
    }

    fun insertTask(task: Task) {
        return taskRepository.insertTask(task)
    }

    fun deleteTask(task: Task) {
        return taskRepository.deleteTask(task)
    }

    fun deleteTaskUsingId(taskId: String) {
        return taskRepository.deleteTaskUsingId(taskId)
    }

    fun updateTask(task: Task)  {
        return taskRepository.updateTask(task)
    }

    fun updateTaskPaticularField(taskId: String,title:String,description:String) {
        return taskRepository.updateTaskPaticularField(taskId, title, description)
    }

    fun searchTaskList(query: String){
        taskRepository.searchTaskList(query)
    }
}