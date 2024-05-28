package com.example.freeimagefeed.base.base_classes

import android.util.Log
import com.example.freeimagefeed.base.base_entity.Event
import com.example.freeimagefeed.base.base_entity.ResourceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

open class BaseUseCase<PARAM, RESULT>(

) {

    /** UseCase Original Response LiveData**/
    private val _currentData = MutableStateFlow<Event<ResourceState<RESULT>>>(
        Event(ResourceState(false, false, false,))
    )
    val currentData : StateFlow<Event<ResourceState<RESULT>>> = _currentData

    /** Job Context Scope **/
    protected var job : Job? = null

    /** Execute UseCase api or process **/
    // TODO remove couroutine context as called susspend so viewmodel scope take effect
    suspend fun execute(
        runProcess : suspend () -> RESULT,
        offlineAlternate : suspend () -> RESULT?
    ) = coroutineScope {
        job = launch {
            withContext(Dispatchers.Main){
                _currentData.value = Event(
                    ResourceState(
                        false, false, true)
                )
            }
            val res  : ResourceState<RESULT> = try {
                val data = runProcess()
                when{
                    data != null -> ResourceState(true, false, false, data)
                    else ->{
                        val offlineData = offlineAlternate()
                        ResourceState(
                            false, true,false, offlineData, 500,
                            Throwable("Data null")
                        )
                    }
                }
            } catch (e : IOException){
                val offlineData = try {
                    offlineAlternate()
                } catch (e : Throwable){
                    null
                }
                Log.e("BaseUseCase", e.message ?: "Unknown Error")
                ResourceState(false, true, false,offlineData, 500, Throwable("Please Check Your Internet Connection"))
            } catch (e : Throwable){
                Log.e("BaseUseCase", e.message ?: "Unknown Error")
                val offlineData = offlineAlternate()
                ResourceState(false, true, false,offlineData, 500, Throwable(e.message ?: "Unknown Error"))
            }
            withContext(Dispatchers.Main){
                _currentData.value = (Event(res))
            }
        }
    }

    open suspend fun run(param: PARAM){
        // empty override
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
    fun isCancelled(): Boolean {
        return job?.isCancelled ?: true
    }
}