package com.example.transagro.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.transagro.model.LoginResponse;
import com.example.transagro.repository.AuthRepository;

public class AuthViewModel extends ViewModel {
    private AuthRepository authRepository;
    private MutableLiveData<LoginResponse> loginResponseLiveData = new MutableLiveData<>();

    public AuthViewModel() {
        authRepository = AuthRepository.getInstance();
    }

    public void login(String email, String password) {
        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(LoginResponse loginResponse) {
                loginResponseLiveData.postValue(loginResponse);
            }

            @Override
            public void onFailure(String message) {
                loginResponseLiveData.postValue(new LoginResponse(false, message));
            }
        });
    }

    public LiveData<LoginResponse> getLoginResponse() {
        return loginResponseLiveData;
    }
}
