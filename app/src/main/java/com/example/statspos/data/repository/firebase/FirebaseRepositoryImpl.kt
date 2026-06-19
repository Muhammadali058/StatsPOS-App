package com.example.statspos.data.repository.firebase

import android.util.Log
import com.example.statspos.domain.models.sales.SalesOrders
import com.example.statspos.domain.repository.firebase.FirebaseRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(

) : FirebaseRepository {
    private val db = FirebaseDatabase.getInstance().reference

    override suspend fun loadOrders(): Resource<List<SalesOrders>> {
        return try {
            val snapshot = db.child("clients")
                .child("clientId_${HP.clientId}")
                .child("branchId_${HP.branchId}")
                .child("salesOrders")
                .get()
                .await()

            val orders = snapshot.children.mapNotNull {
                it.getValue(SalesOrders::class.java)
            }

            Resource.Success(orders)

        } catch (e: Exception) {
//            Resource.Error(e.message)
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

}