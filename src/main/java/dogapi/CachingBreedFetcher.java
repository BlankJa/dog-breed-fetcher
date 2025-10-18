package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    // TODO Task 2: Complete this class
    private final BreedFetcher delegate;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.delegate = Objects.requireNonNull(fetcher, "fetcher must not be null");
    }

    @Override
    public List<String> getSubBreeds(String breed)  throws BreedFetcher.BreedNotFoundException {
        // return statement included so that the starter code can compile and run.
        String key = breed.toLowerCase();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        // not cached: call the underlying fetcher and record the call
        callsMade++;
        List<String> result = delegate.getSubBreeds(breed);

        // cache successful results only (as unmodifiable copy)
        List<String> copy = Collections.unmodifiableList(new ArrayList<>(result));
        cache.put(key, copy);
        return copy;
    }

    public int getCallsMade() {
        return callsMade;
    }
}