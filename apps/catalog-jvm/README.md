# catalog-jvm

This is the target Quarkus JVM catalog workload.

- minimal classic blocking baseline
- product-oriented `/products/{productId}` response
- bounded uncached `/products/{productId}` baseline cost, aligned to the native pair
- `/products/{productId}/fault-test/slow` and `/products/{productId}/fault-test/timeout` derived from that same baseline so the
  fault shapes stay ordered and easy to explain
- explicit fault endpoints:
  - `/products/{productId}`
  - `/products/{productId}/fault-test/slow`
  - `/products/{productId}/fault-test/error`
  - `/products/{productId}/fault-test/timeout`
  - `/products/{productId}/fault-test/flaky`
- no cache
- no fault-tolerance layer

Java package root:

- `net.michaelknyazev.aprf.catalog.jvm`

This directory is the active published JVM catalog implementation.
