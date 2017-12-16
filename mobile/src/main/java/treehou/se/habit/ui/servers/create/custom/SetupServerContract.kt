package treehou.se.habit.ui.servers.create.custom

import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface SetupServerContract {

    interface View : BaseView<Presenter> {
        fun closeWindow()
        fun loadServer(server: ServerDB)
        fun showTopLabel(show: Boolean)
    }

    interface Presenter : BasePresenter {
        fun saveServer(server: ServerDB)
    }
}
