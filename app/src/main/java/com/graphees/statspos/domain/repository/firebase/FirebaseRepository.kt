package com.graphees.statspos.domain.repository.firebase

import com.graphees.statspos.domain.models.sales.SalesOrders
import com.graphees.statspos.utils.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    suspend fun loadOrders(status:String, date:String): Resource<List<SalesOrders>>
    fun loadOrdersRealtime(status:String, date:String): Flow<Resource<List<SalesOrders>>>
    suspend fun updateStatus(id:Long, status:String): Resource<String>
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun getFCMToken(): Resource<String>
}