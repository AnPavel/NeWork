package ru.netology.nework.ui

import android.app.Dialog
import android.os.Bundle
import android.app.AlertDialog
import android.util.Log
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import javax.inject.Inject


@AndroidEntryPoint
class LogoutDialog: DialogFragment() {

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                Log.d("MyAppLog", "LogoutDialog * yes")
                appAuth.removeAuth()
                dialog.dismiss()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                Log.d("MyAppLog", "LogoutDialog * no")
                //return@setNegativeButton
                dialog.dismiss()
            }
            .create()


}
