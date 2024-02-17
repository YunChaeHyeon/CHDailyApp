package com.example.mydailyapp

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.query
import com.example.mydailyapp.adapters.TaskRVVBListAdapter
import com.example.mydailyapp.adapters.TaskRVViewBindingAdapter
import com.example.mydailyapp.adapters.TaskRecycleViewAdapter
import com.example.mydailyapp.databinding.FragmentTodolistBinding
import com.example.mydailyapp.models.Task
import com.example.mydailyapp.utils.*
import com.example.mydailyapp.utils.StatusResult.*

import com.example.mydailyapp.viewmodels.TaskViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
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

                val newTask = Task(
                    UUID.randomUUID().toString(),
                    addETTitle.text.toString().trim(),
                    addETDesc.text.toString().trim(),
                    Date()
                )
                //hideKeyBoard(it)
                addTaskDialog.dismiss()
                taskViewModel.insertTask(newTask)
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

        val taskRVVBListAdapter =  TaskRVVBListAdapter { type, position, task ->
            if(type == "delete"){
                taskViewModel
                    .deleteTaskUsingId(task.id)
                    // .deleteTask(task)

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
                        //hideKeyBoard(it)
                        updateTaskDialog.dismiss()
                        taskViewModel
//                            .updateTaskPaticularField( // 날짜 update X
//                                task.id,
//                                updateETTitle.text.toString().trim(),
//                                updateETDesc.text.toString().trim(),
//                            )
                            .updateTask(updateTask) // 날짜 update O

                    }
                }
                updateTaskDialog.show()
            }

        }

        mBinding.taskRV.adapter = taskRVVBListAdapter
        taskRVVBListAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver()
        {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                mBinding.taskRV.smoothScrollToPosition(positionStart)
            }
        })
        callGetTaskList(taskRVVBListAdapter)
        taskViewModel.getTaskList()
        statusCallback()
        callSearch()
    }



    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentTodolistBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    private fun callSearch() {
        mBinding.edSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(query: Editable) {
                if (query.toString().isNotEmpty()){
                    taskViewModel.searchTaskList(query.toString())
                }else{
                    taskViewModel.getTaskList()
                }
            }
        })

        mBinding.edSearch.setOnEditorActionListener{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                //hideKeyBoard(v)
                return@setOnEditorActionListener true
            }
            false
        }

        //callSortByDialog()
    }

    private fun statusCallback() {
        taskViewModel
            .statusLiveData
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        loadingDialog.show()
                    }

                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        when (it.data as StatusResult) {
                            Added -> {
                                Log.d("StatusResult", "Added")
                            }

                            Deleted -> {
                                Log.d("StatusResult", "Deleted")

                            }

                            Updated -> {
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

    private fun callGetTaskList(taskRecycleViewAdapter : TaskRVVBListAdapter) {
        CoroutineScope(Dispatchers.Main).launch {
            taskViewModel.taskStateFlow.collectLatest{
                when(it.status){
                    Status.LOADING -> {
                        loadingDialog.show()
                    }
                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        it.data?.collect { taskList ->
                            taskRecycleViewAdapter.submitList(taskList)
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