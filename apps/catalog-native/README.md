# catalog-native

This is the target Quarkus native catalog workload.

- product-oriented `/products/{productId}` response
- same uncached `/products/{productId}` baseline cost as `catalog-jvm`
- `/products/{productId}/fault-test/slow` and `/products/{productId}/fault-test/timeout` derived from that same baseline so the
  fault shapes stay ordered and easy to explain
- explicit fault endpoints:
  - `/products/{productId}`
  - `/products/{productId}/fault-test/slow`
  - `/products/{productId}/fault-test/error`
  - `/products/{productId}/fault-test/timeout`
  - `/products/{productId}/fault-test/flaky`
- native-only cache enable/disable for the D8 Governance and observability
  corrective-action story
- immediate cache invalidation on disable

Java package root:

- `net.michaelknyazev.aprf.catalog.nativeapp`

This directory is the active published native catalog implementation.
