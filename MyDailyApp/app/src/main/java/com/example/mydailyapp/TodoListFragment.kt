package com.example.mydailyapp

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.mydailyapp.adapters.TaskRecycleViewAdapter
import com.example.mydailyapp.databinding.FragmentTodolistBinding
import com.example.mydailyapp.models.Task
import com.example.mydailyapp.utils.*

import com.example.mydailyapp.viewmodels.TaskViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

import java.util.*


class TodoList_Fragment : Fragment(){

    private var liveText: MutableLiveData<String> = MutableLiveData()

    private lateinit var mBinding: FragmentTodolistBinding

    //======================Dialog====================
    private val addTaskDialog : Dialog by lazy {

        Dialog(requireContext()).apply {
            setupDialog(R.layout.add_task_dialog)
        }
    }

    private val updateTaskDialog : Dialog by lazy {
        Dialog(requireContext()).apply {
            setupDialog(R.layout.update_task_dialog)
        }
    }

    private val loadingDialog : Dialog by lazy {
        Dialog(requireContext()).apply {
            setupDialog(R.layout.loading_dialog)
        }
    }

    private val taskViewModel : TaskViewModel by lazy {
        ViewModelProvider(this)[TaskViewModel::class.java]
    }

//    private val taskRecycleViewAdapter : TaskRecycleViewAdapter by lazy {
//        TaskRecycleViewAdapter { position, task ->
//            taskViewModel
//                .deleteTaskUsingId(task.id)
//               // .deleteTask(task)
//                .observe(this) {
//                    when(it.status){
//                        Status.LOADING -> {
//                            loadingDialog.show()
//                        }
//                        Status.SUCCESS -> {
//                            loadingDialog.dismiss()
//                            if(it.data != -1){
//                                longToastShow("Task Deleted Successfully" , requireContext())
//                            }
//                        }
//                        Status.ERROR -> {
//                            loadingDialog.dismiss()
//                            //it.message?.let { it1 -> longToastShow(it1)}
//                        }
//                    }
//                }
//        }
//    }
    //===========================================

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //mBinding.taskRV.adapter = taskRecycleViewAdapter

        val addCloseImg = addTaskDialog.findViewById<ImageView>(R.id.closeImg)
        addCloseImg.setOnClickListener {addTaskDialog.dismiss()}

        // == Add task Start ==
        val addETTitle = addTaskDialog.findViewById<TextInputEditText>(R.id.edTaskTitle)
        val addETTitleL = addTaskDialog.findViewById<TextInputLayout>(R.id.edTaskTitleL)

        addETTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addETTitle, addETTitleL)
            }

        })

        val addETDesc = addTaskDialog.findViewById<TextInputEditText>(R.id.edTaskDesc)
        val addETDescL = addTaskDialog.findViewById<TextInputLayout>(R.id.edTaskDescL)

        addETDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addETDesc, addETDescL)
            }
        })

        mBinding.addTaskFABtn.setOnClickListener {
            clearEditText(addETTitle, addETTitleL)
            clearEditText(addETDesc, addETDescL)
            addTaskDialog.show()
        }
        val saveTaskBtn = addTaskDialog.findViewById<Button>(R.id.saveTaskBtn)
        saveTaskBtn.setOnClickListener {
            if (validateEditText(addETTitle, addETTitleL)
                && validateEditText(addETDesc, addETDescL)
            ) {
                addTaskDialog.dismiss()

                val newTask = Task(
                    UUID.randomUUID().toString(),
                    addETTitle.text.toString().trim(),
                    addETDesc.text.toString().trim(),
                    Date()
                )
                //hideKeyBoard(it)
//                addTaskDialog.dismiss()
                taskViewModel.insertTask(newTask).observe(requireActivity()){
                    when(it.status){
                        Status.LOADING -> {
                            loadingDialog.show()
                        }
                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            if(it.data?.toInt() != -1){
                                longToastShow("Task Added Successfully" , requireContext())
                            }
                        }
                        Status.ERROR -> {
                            loadingDialog.dismiss()
                        }
                    }
                }
            }
        }
        // == Add task end ==


        //==============
        val updateETTitle = updateTaskDialog.findViewById<TextInputEditText>(R.id.edTaskTitle)
        val updateETTitleL = updateTaskDialog.findViewById<TextInputLayout>(R.id.edTaskTitleL)

        val updateETDesc = updateTaskDialog.findViewById<TextInputEditText>(R.id.edTaskDesc)
        val updateETDescL = updateTaskDialog.findViewById<TextInputLayout>(R.id.edTaskDescL)

        val updateTaskBtn = updateTaskDialog.findViewById<Button>(R.id.updateTaskBtn)

        val updateCloseImg = updateTaskDialog.findViewById<ImageView>(R.id.closeImg)
        updateCloseImg.setOnClickListener {updateTaskDialog.dismiss()}

        val taskRecycleViewAdapter =  TaskRecycleViewAdapter { type , position, task ->
            if(type == "delete"){
                taskViewModel
                    .deleteTaskUsingId(task.id)
                    // .deleteTask(task)
                    .observe(viewLifecycleOwner) {
                        when(it.status){
                            Status.LOADING -> {
                                loadingDialog.show()
                            }
                            Status.SUCCESS -> {
                                loadingDialog.dismiss()
                                if(it.data != -1){
                                    longToastShow("Task Deleted Successfully" , requireContext())
                                }
                            }
                            Status.ERROR -> {
                                loadingDialog.dismiss()
                                //it.message?.let { it1 -> longToastShow(it1)}
                            }
                        }
                    }
            }else if(type == "update"){
                updateETTitle.setText(task.title)
                updateETDesc.setText(task.description)
                updateTaskBtn.setOnClickListener {
                    if (validateEditText(updateETTitle, updateETTitleL)
                        && validateEditText(updateETDesc, updateETDescL)
                    ) {
                        val updateTask = Task(
                            task.id,
                            updateETTitle.text.toString().trim(),
                            updateETDesc.text.toString().trim(),
//                           here i Date updated
                            Date()
                        )
                        updateTaskDialog.dismiss()
                        loadingDialog.show()
                        taskViewModel
                            .updateTaskPaticularField( // 날짜 update X
                                task.id,
                                updateETTitle.text.toString().trim(),
                                updateETDesc.text.toString().trim(),
                            )
                            //.updateTask(updateTask) // 날짜 update O
                            .observe(viewLifecycleOwner) {
                                when(it.status){
                                    Status.LOADING -> {
                                        loadingDialog.show()
                                    }
                                    Status.SUCCESS -> {
                                        loadingDialog.dismiss()
                                        if(it.data != -1){
                                            longToastShow("Task Update Successfully" , requireContext())
                                        }
                                    }
                                    Status.ERROR -> {
                                        loadingDialog.dismiss()
                                        //it.message?.let { it1 -> longToastShow(it1)}
                                    }
                                }
                            }
                    }
                }
                updateTaskDialog.show()
            }

        }

        mBinding.taskRV.adapter = taskRecycleViewAdapter

        callGetTaskList(taskRecycleViewAdapter)
    }



    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentTodolistBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    private fun callGetTaskList(taskRecycleViewAdapter : TaskRecycleViewAdapter) {
        loadingDialog.show()
        CoroutineScope(Dispatchers.Main).launch {
            taskViewModel.getTaskList().collect(){
                when(it.status){
                    Status.LOADING -> {
                        loadingDialog.show()
                    }
                    Status.SUCCESS -> {
                        it.data?.collect { taskList ->
                            loadingDialog.dismiss()
                            taskRecycleViewAdapter.addAllTask(taskList)
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