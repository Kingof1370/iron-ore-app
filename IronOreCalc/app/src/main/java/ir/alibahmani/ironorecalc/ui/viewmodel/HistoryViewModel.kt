package ir.alibahmani.ironorecalc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.alibahmani.ironorecalc.domain.model.SavedProject
import ir.alibahmani.ironorecalc.domain.usecase.DeleteProjectUseCase
import ir.alibahmani.ironorecalc.domain.usecase.GetProjectsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getProjects: GetProjectsUseCase,
    private val deleteProject: DeleteProjectUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allProjects = getProjects()
    val projects: StateFlow<List<SavedProject>> = combine(_allProjects, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearch(query: String) { _searchQuery.value = query }

    fun delete(projectId: Long) {
        viewModelScope.launch { deleteProject(projectId) }
    }
}
