# experience-jvm

This is the target Quarkus JVM experience workload.

- minimal classic upstream service
- `/product-views/{productId}` as the main business path
- `/product-views/{productId}/fault-test/{faultType}` for bounded fault drills
- one downstream catalog call per request
- no cache
- no fault-tolerance layer

Java package root:

- `net.michaelknyazev.aprf.experience.jvm`
