package com.movieapp.ui.movielist;

import com.movieapp.data.repository.MovieRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class MovieListViewModel_Factory implements Factory<MovieListViewModel> {
  private final Provider<MovieRepository> repositoryProvider;

  public MovieListViewModel_Factory(Provider<MovieRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public MovieListViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static MovieListViewModel_Factory create(Provider<MovieRepository> repositoryProvider) {
    return new MovieListViewModel_Factory(repositoryProvider);
  }

  public static MovieListViewModel newInstance(MovieRepository repository) {
    return new MovieListViewModel(repository);
  }
}
