# GitOps Manifest Bundle

This directory is a curated published copy of the main YAML manifests that support the article story.

- `apps/`
  - the five public workload trees:
    - `basic-app`
    - `catalog-jvm`
    - `experience-jvm`
    - `catalog-native`
    - `experience-native`
  - includes:
    - namespaces
    - deployments
    - services
    - routes
    - ImageStreams
    - ServiceMonitors
    - PrometheusRules
    - major app-local special config such as `ConfigMap` and
      `PodDisruptionBudget`
- `argo_apps/`
  - root Application and AppProject manifests
