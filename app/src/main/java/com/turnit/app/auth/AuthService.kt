package com.turnit.app.auth

import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault

class AuthService {

    // Метод для регистрации
    fun registerUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (Backendless.isInitialized()) {
            val user = BackendlessUser()
            user.setProperty("email", email) // Используем setProperty для установки полей
            user.password = password

            Backendless.UserService.register(user, object : AsyncCallback<BackendlessUser> {
                override fun handleResponse(response: BackendlessUser) {
                    onSuccess()
                }

                override fun handleFault(fault: BackendlessFault) {
                    onError(fault.message ?: "Ошибка регистрации")
                }
            })
        } else {
            onError("Backendless SDK не инициализирован")
        }
    }

    // Метод для входа
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (Backendless.isInitialized()) {
            Backendless.UserService.login(email, password, object : AsyncCallback<BackendlessUser> {
                override fun handleResponse(response: BackendlessUser) {
                    onSuccess()
                }

                override fun handleFault(fault: BackendlessFault) {
                    onError(fault.message ?: "Ошибка входа")
                }
            })
        } else {
            onError("Backendless SDK не инициализирован")
        }
    }

    // Метод для получения ownerId текущего пользователя
    fun getCurrentUserOwnerId(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        if (Backendless.isInitialized()) {
            val currentUser = Backendless.UserService.CurrentUser() // Используем правильный метод
            if (currentUser != null) {
                val ownerId = currentUser.getProperty("objectId") as? String
                if (!ownerId.isNullOrEmpty()) {
                    onSuccess(ownerId)
                } else {
                    onError("Поле objectId у текущего пользователя отсутствует")
                }
            } else {
                onError("Текущий пользователь не найден")
            }
        } else {
            onError("Backendless SDK не инициализирован")
        }
    }
}
