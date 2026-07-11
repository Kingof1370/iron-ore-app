package ir.alibahmani.ironorecalc.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.alibahmani.ironorecalc.data.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProjectRepository
) : ViewModel() {
    val totalCount: Flow<Int> = repository.getProjectCount()
    val todayCount: Flow<Int> = repository.getTodayCount()
}
