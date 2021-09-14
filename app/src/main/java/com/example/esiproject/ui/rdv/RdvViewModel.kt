package com.example.esiproject.ui.rdv

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.CheckRdvAvailabilityQuery
import com.example.esiproject.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import javax.inject.Inject

@HiltViewModel
class RdvViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    val rdvAvailability: MutableLiveData<CheckRdvAvailabilityQuery.CheckRdvAvailability> by lazy {
        MutableLiveData<CheckRdvAvailabilityQuery.CheckRdvAvailability>()
    }


    fun checkAuthorization(response: AuthorizationResponse?, exception: AuthorizationException?) {
        viewModelScope.launch {
            authRepository.checkAuthorization(response, exception)
        }
    }

    fun checkRdvAvailability(date: Any) {
        val apolloClient = ApolloClient(serverUrl = "https://1sc-project.moun3im.com/graphql")
        viewModelScope.launch {
            val response = try {
                apolloClient.query(CheckRdvAvailabilityQuery(date))
            } catch (e: ApolloException) {
                return@launch
            }

            if (response.data?.checkRdvAvailability != null){
                rdvAvailability.value = response.data!!.checkRdvAvailability
            }
        }
    }
}