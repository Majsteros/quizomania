package arkadiuszpalka.quizomania.data.network;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class HttpHandler {
    private OkHttpClient okHttpClient = new OkHttpClient();

    String request(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }
}
