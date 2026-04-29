# experience-native

This is the target Quarkus native experience workload.

- `/product-views/{productId}` as the main business path
- `/product-views/{productId}/fault-test/{faultType}` for bounded fault drills
- retry, timeout, circuit breaker, fallback, and bounded concurrency behavior

Java package root:

- `net.michaelknyazev.aprf.experience.nativeapp`
