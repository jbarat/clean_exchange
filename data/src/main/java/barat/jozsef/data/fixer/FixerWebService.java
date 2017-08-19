package barat.jozsef.data.fixer;

import io.reactivex.Observable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit web service for the fixer.io.
 */
public interface FixerWebService {

    @GET("latest")
    Observable<Result<FixerResponse>> getLatest(@Query("base") String baseCurrency);
}
