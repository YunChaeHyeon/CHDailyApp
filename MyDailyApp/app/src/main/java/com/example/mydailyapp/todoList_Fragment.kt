package com.example.mydailyapp.bottom_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mydailyapp.R
//import com.example.mydailyapp.databinding.ActivityMainBinding
//import com.example.mydailyapp.databinding.FragmentTodoListBinding


class TodoList_Fragment : Fragment() {

    //private var mBinding: FragmentTodoListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        //mBinding = FragmentTodoListBinding.inflate(inflater, container, false)

        //val view = mBinding!!.root
        //return view
        return inflater.inflate(R.layout.fragment_todo_list, container, false)
    }

}