package com.movieapp.di;

import com.movieapp.data.api.OmdbApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvideOmdbApiServiceFactory implements Factory<OmdbApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public AppModule_ProvideOmdbApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public OmdbApiService get() {
    return provideOmdbApiService(retrofitProvider.get());
  }

  public static AppModule_ProvideOmdbApiServiceFactory create(Provider<Retrofit> retrofitProvider) {
    return new AppModule_ProvideOmdbApiServiceFactory(retrofitProvider);
  }

  public static OmdbApiService provideOmdbApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideOmdbApiService(retrofit));
  }
}
