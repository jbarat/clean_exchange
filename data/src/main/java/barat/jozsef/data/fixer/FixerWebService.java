package barat.jozsef.data.fixer;

import io.reactivex.Single;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FixerWebService {

    @GET("latest")
    Single<Result<FixerResponse>> getLatest(@Query("base") String baseCurrency);
}
