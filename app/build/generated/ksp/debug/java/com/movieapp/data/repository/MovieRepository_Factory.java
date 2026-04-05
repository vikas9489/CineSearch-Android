package com.movieapp.data.repository;

import com.movieapp.data.api.OmdbApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MovieRepository_Factory implements Factory<MovieRepository> {
  private final Provider<OmdbApiService> apiServiceProvider;

  public MovieRepository_Factory(Provider<OmdbApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public MovieRepository get() {
    return newInstance(apiServiceProvider.get());
  }

  public static MovieRepository_Factory create(Provider<OmdbApiService> apiServiceProvider) {
    return new MovieRepository_Factory(apiServiceProvider);
  }

  public static MovieRepository newInstance(OmdbApiService apiService) {
    return new MovieRepository(apiService);
  }
}
