package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(java.time.Duration.ofSeconds(5))
                .readTimeout(java.time.Duration.ofSeconds(5))
                .callTimeout(java.time.Duration.ofSeconds(10))
                .build();

        String url = "https://dog.ceo/api/breed/" + breed.toLowerCase() + "/list";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", "DogBreedFetcher/1.0 (+https://example.com)")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new BreedFetcher.BreedNotFoundException(breed);
            }

            String json = response.body().string();

            JSONObject obj = new JSONObject(json);
            String status = obj.optString("status", response.isSuccessful() ? "success" : "error");

            if (!"success".equalsIgnoreCase(status)) {
                throw new BreedFetcher.BreedNotFoundException(breed);
            }

            JSONArray arr = obj.getJSONArray("message");
            List<String> result = new java.util.ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                result.add(arr.getString(i));
            }
            return result;

        } catch (org.json.JSONException | java.io.IOException e) {
            throw new BreedFetcher.BreedNotFoundException(breed);
        }
    }

}