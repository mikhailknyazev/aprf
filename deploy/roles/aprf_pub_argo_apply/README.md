# aprf_pub_argo_apply

## Purpose

Applies the rendered Argo objects and waits for the article workload root
Application to reconcile. It also imports Java ImageStream tags after image
pushes so OpenShift deployments can roll forward.

## Inputs

| Input | Meaning |
| --- | --- |
| `aprf_pub_bootstrap_namespace_manifests` | Namespace manifests applied before the root Application. |
| `aprf_pub_gitops_rendered_root` | Rendered AppProject and root Application path. |
| `aprf_pub_apps_argo_namespace` | Existing apps Argo CD namespace. |
| `aprf_pub_java_apps` | Java workload image stream names. |

## Outputs

Creates or updates workload namespaces, the workload AppProject, the root
Application, registry import secrets, and ImageStream imports.

## Mutation scope

Mutates the target cluster through `oc` and Kubernetes APIs.

## Guardrails

Works only against the configured apps Argo namespace and article workload
namespaces. It waits for Argo sync before route verification continues.
