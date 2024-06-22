package domain.models.util

sealed class DataLoadStatus {
    data object Empty : DataLoadStatus()

    data object Loading : DataLoadStatus()

    data object Success : DataLoadStatus()

    data object Failed : DataLoadStatus()
}

open class LoadStatusState {
    var loadStatus: DataLoadStatus = DataLoadStatus.Loading
    var errorCode: String? = null

    fun setSuccess() {
        this.loadStatus = DataLoadStatus.Success
    }

    fun setError(errorCode: String?) {
        this.loadStatus = DataLoadStatus.Failed
        this.errorCode = errorCode
    }

    fun setLoading() {
        this.loadStatus = DataLoadStatus.Loading
    }

    fun isLoaded(): Boolean = this.loadStatus == DataLoadStatus.Success

    fun isLoading(): Boolean = this.loadStatus == DataLoadStatus.Loading

    fun isFailed(): Boolean = this.loadStatus == DataLoadStatus.Failed
}
