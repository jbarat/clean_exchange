package barat.jozsef.data;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import barat.jozsef.data.fixer.FixerWebService;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    @Provides
    FixerWebService fixerWebService(Retrofit retrofit) {
        return retrofit.create(FixerWebService.class);
    }

    @Provides
    @Singleton
    Retrofit retrofit(OkHttpClient secureClient) {
        return new Retrofit.Builder()
                .client(secureClient)
                .baseUrl(NetworkConstants.FIXER_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient okHttpClientSecure(HttpLoggingInterceptor httpLoggingInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(NetworkConstants.FIXER_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .readTimeout(NetworkConstants.FIXER_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .writeTimeout(NetworkConstants.FIXER_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor);

        return builder.build();
    }

    @Provides
    HttpLoggingInterceptor httpLoggingInterceptor(@Named("debug") Boolean debug) {
        return new HttpLoggingInterceptor()
                .setLevel(debug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
    }
}
