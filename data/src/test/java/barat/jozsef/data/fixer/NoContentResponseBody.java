package barat.jozsef.data.fixer;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Used in test as body less http response.
 */
public class NoContentResponseBody extends ResponseBody {

    @Override
    public MediaType contentType() {
        return MediaType.parse("");
    }

    @Override
    public long contentLength() {
        return 0;
    }

    @Override
    public BufferedSource source() {
        return new Buffer();
    }
}