package com.scruge.scruge.view.ui.bounty

import com.scruge.scruge.services.Service
import com.scruge.scruge.viewmodel.bounty.project.ProjectQ
import com.ysoftware.mvvm.array.ArrayViewModel

class ProjectAVM: ArrayViewModel<Project, ProjectVM, ProjectQ>() {

    override fun fetchData(query: ProjectQ?, block: (Result<Collection<ProjectVM>>) -> Unit) {
        Service.api.getProjects { result ->
            block(result.map { it.projects.map { ProjectVM(it) }})
        }
    }
}