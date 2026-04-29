# Sample applications

This directory keeps the four Java services used by the APRF article
companion:

| Service | Runtime | Business role |
| --- | --- | --- |
| `catalog-jvm` | Quarkus JVM | Downstream product service for the bytecode-based Java pair. |
| `experience-jvm` | Quarkus JVM | Routed user-facing product-view service for the bytecode-based Java pair. |
| `catalog-native` | Quarkus native | Downstream product service for the native pair and the D8 corrective cache control. |
| `experience-native` | Quarkus native | Routed user-facing product-view service for the native pair. |

The small Python `basic-app` used in the article is carried with the workload
manifests. It is intentionally small because its main article role is governance, 
quota, monitoring, and GitOps self-heal visibility.

## Business flow

The Java services model a small product browsing path.

| Step | Request path | What happens |
| --- | --- | --- |
| 1 | A user or proof sends traffic to `experience-*`. | The experience service is the public route measured by lifecycle, hardening, chaos, and alert tests. |
| 2 | `experience-*` calls `catalog-*`. | The catalog service returns the product data and can simulate bounded downstream faults. |
| 3 | `experience-*` returns a product view. | The response includes diagnostic fields so APRF proofs can explain what happened. |

The implementation keeps one product id, `PROD-001`, wired through the sample.
That keeps the article and source code readable. A reader can generalize it to
many products later.

## Endpoint contract

| Service family | Endpoint | Purpose |
| --- | --- | --- |
| `catalog-*` | `GET /products/{productId}` | Normal downstream product response. |
| `catalog-*` | `GET /products/{productId}/fault-test/slow` | Simulates a slow downstream product dependency. |
| `catalog-*` | `GET /products/{productId}/fault-test/error` | Simulates a downstream error. |
| `catalog-*` | `GET /products/{productId}/fault-test/timeout` | Simulates a downstream timeout. |
| `catalog-*` | `GET /products/{productId}/fault-test/flaky` | Simulates per-replica intermittent downstream trouble. |
| `experience-*` | `GET /product-views/{productId}` | Normal user-facing product view. |
| `experience-*` | `GET /product-views/{productId}/fault-test/{faultType}` | User-facing route used by D6 and D8 fault drills. |
| `catalog-native` | `POST /admin/cache` | D8 corrective-action control for bounded native cache enable and restore. |
| all Java services | `/q/health/*`, `/q/metrics` | Health and metrics endpoints used by OpenShift and Prometheus. |

The native admin endpoint is intentionally not a normal business endpoint. It
exists so D8 Governance and observability can show a bounded GitOps corrective
action that improves the user-facing route and then restores the authored
baseline.

## APRF discipline mapping

| Discipline | App contribution |
| --- | --- |
| D1 Capacity | Resource requests and limits make the Java pair footprint visible. |
| D2 Overcommitment | Routed load stays healthy while quota-bounded placeholder work is refused. |
| D3 Failure domains | JVM and native replicas are spread across workers and zones, with intended service paths enforced. |
| D4 Lifecycle | Startup, restart, and rolling replacement windows are measured on the Java pairs. |
| D5 Controlled disruption | The routed path remains available through bounded planned disruption. |
| D6 Hardened application patterns | Fault endpoints drive delay, error, timeout, fallback, and circuit-breaker behavior. |
| D7 Chaos | Pod loss and hard worker loss test route continuity under sharper faults. |
| D8 Governance and observability | Alerting plus GitOps corrective action is centered on the native product-view path. |

## Build contract

All four Java services build from the source tree:

| Build path | Services | Notes |
| --- | --- | --- |
| `Dockerfile.jvm` | `catalog-jvm`, `experience-jvm` | Builds the bytecode-based Java services. |
| `Dockerfile.native` | `catalog-native`, `experience-native` | Builds the native services on the Linux bastion with bounded RAM and disk guardrails. |
| `mvnw` | all Java services | Keeps Maven behavior consistent for readers and proof reruns. |

## Reading order

| Step | Read |
| --- | --- |
| 1 | Each service `pom.xml` for dependencies and runtime role. |
| 2 | Resource classes under `src/main/java` for endpoint behavior. |
| 3 | Service classes for product data, fault modes, fallback, and cache behavior. |
| 4 | `Dockerfile.jvm` and `Dockerfile.native` for the build contract. |
| 5 | Matching workload manifests. |
