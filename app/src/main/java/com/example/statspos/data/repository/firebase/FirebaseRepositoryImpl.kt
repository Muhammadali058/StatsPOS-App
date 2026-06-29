package com.example.statspos.data.repository.firebase

import com.example.statspos.domain.models.sales.SalesOrders
import com.example.statspos.domain.repository.firebase.FirebaseRepository
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(

) : FirebaseRepository {
    private val db = FirebaseDatabase.getInstance().reference

    override suspend fun loadOrders(status:String, date:String): Resource<List<SalesOrders>> {
        return try {
            val snapshot = db.child("clients")
                .child("clientId_${HP.clientId}")
                .child("branchId_${HP.branchId}")
                .child("salesOrders")
                .get()
                .await()

            val orders = snapshot.children
                .mapNotNull { it.getValue(SalesOrders::class.java) }
                .filter { it.status.equals(status, ignoreCase = true) }

            var filteredOrders = orders

            if(status.equals("delivered", ignoreCase = true)) {
                filteredOrders = orders.filter {
                    HP.getFormatedDate(HP.toLocalDate(it.date.toString()))
                        .equals(date, ignoreCase = true)
                }
            }

            Resource.Success(filteredOrders)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    override fun loadOrdersRealtime(
        status: String,
        date: String
    ): Flow<Resource<List<SalesOrders>>> = callbackFlow {

        val ref = db.child("clients")
            .child("clientId_${HP.clientId}")
            .child("branchId_${HP.branchId}")
            .child("salesOrders")

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val orders = snapshot.children
                    .mapNotNull { it.getValue(SalesOrders::class.java) }
                    .filter { it.status.equals(status, ignoreCase = true) }

                var filteredOrders = orders

                if (status.equals("delivered", ignoreCase = true)) {
                    filteredOrders = orders.filter {
                        HP.getFormatedDate(
                            HP.toLocalDate(it.date.toString())
                        ).equals(date, ignoreCase = true)
                    }
                }

                trySend(Resource.Success(filteredOrders))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }

        ref.addValueEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }

    override suspend fun updateStatus(id:Long, status:String): Resource<String> {
        return try {
            db.child("clients")
                .child("clientId_${HP.clientId}")
                .child("branchId_${HP.branchId}")
                .child("salesOrders")
                .child("salesOrderId_${id}")
                .child("status")
                .setValue(status)
                .await()

            Resource.Success("Status updated")
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val auth = FirebaseAuth.getInstance()
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }
}
