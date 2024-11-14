//package com.dicoding.storyapp.di
//
//import com.dicoding.storyapp.ApiService
//import com.dicoding.storyapp.data.RegisterRepository
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object RepositoryModule {
//    @Provides
//    @Singleton
//    fun provideRegisterRepository(apiService: ApiService): RegisterRepository {
//        return RegisterRepository(apiService) // Pastikan Anda menggunakan implementasi yang benar
//    }
//}
