package com.fxn.pixeditor.imageeditengine

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseFragment : Fragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    protected fun setVisibility(view: View, visible: Boolean) {
        if (visible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    protected abstract fun initView(view: View)
}// Required empty public constructor
