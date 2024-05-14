package cr.ac.una.controlfinancierocamera.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ControlFinancieroViewModel : ViewModel() {
    private val _textLiveData: MutableLiveData<String> = MutableLiveData()
    val textLiveData: LiveData<String> = _textLiveData

    fun updateText(newText: String) {
        _textLiveData.value = newText
    }
}