package com.fxn.pixeditor.imageeditengine.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fxn.pixeditor.imageeditengine.BaseFragment

object FragmentUtil {

    fun hadFragment(activity: AppCompatActivity): Boolean {
        return activity.getSupportFragmentManager().getBackStackEntryCount() !== 0
    }

    fun replaceFragment(activity: AppCompatActivity, contentId: Int, fragment: BaseFragment) {
        val transaction = activity.getSupportFragmentManager().beginTransaction()

        transaction.replace(contentId, fragment, fragment::class.java.name)

        transaction.addToBackStack(null)
        transaction.commit()
    }


    fun addFragment(activity: AppCompatActivity, contentId: Int, fragment: BaseFragment) {
        val transaction = activity.getSupportFragmentManager().beginTransaction()

        transaction.add(contentId, fragment, fragment::class.java.name)
        transaction.commit()
    }

    fun removeFragment(activity: AppCompatActivity, fragment: BaseFragment) {
        activity.getSupportFragmentManager().beginTransaction()
            .remove(fragment)
            .commit()
    }


    fun showFragment(activity: AppCompatActivity, fragment: BaseFragment) {
        activity.getSupportFragmentManager().beginTransaction()
            .show(fragment)
            .commit()
    }

    fun hideFragment(activity: AppCompatActivity, fragment: BaseFragment) {
        activity.getSupportFragmentManager().beginTransaction()
            .hide(fragment)
            .commit()
    }

    fun attachFragment(activity: AppCompatActivity, fragment: BaseFragment) {
        activity.getSupportFragmentManager().beginTransaction()
            .attach(fragment)
            .commit()
    }

    fun detachFragment(activity: AppCompatActivity, fragment: BaseFragment) {
        activity.getSupportFragmentManager().beginTransaction()
            .detach(fragment)
            .commit()
    }

    fun getFragmentByTag(appCompatActivity: AppCompatActivity, tag: String): Fragment? {
        return appCompatActivity.supportFragmentManager.findFragmentByTag(tag)
    }

}