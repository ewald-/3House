package treehou.se.habit.ui.servers.create.myopenhab

import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface CreateMyOpenhabContract {

    interface View : BaseView<Presenter> {
        fun showError(error: String)
        fun closeWindow()
        fun loadUsername(name: String)
        fun loadPassword(password: String)
    }

    interface Presenter : BasePresenter {
        fun login(username: String, password: String)
    }

    companion object {
        val ARG_SERVER = "ARG_SERVER"
    }
}
