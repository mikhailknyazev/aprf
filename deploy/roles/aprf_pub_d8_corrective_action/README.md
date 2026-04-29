# aprf_pub_d8_corrective_action

## Purpose

Implements the D8 Governance and observability corrective-action path. It
publishes a bounded GitOps change that enables the native catalog cache, syncs
the targeted workload resources through Argo, then restores the authored
baseline through the same GitOps path.

## Inputs

| Input | Meaning |
| --- | --- |
| `aprf_pub_d8_baseline_catalog_cache_enabled` | Expected baseline value, normally `false`. |
| `aprf_pub_d8_corrective_catalog_cache_enabled` | Corrective value, normally `true`. |
| `aprf_pub_d8_sync_resources` | Targeted Argo resources for the D8 sync. |
| GitOps publication inputs | Same bounded GitOps inputs used by `aprf_pub_git_publish`. |

## Outputs

Publishes a corrective commit, waits for Argo sync, publishes a restore commit,
and waits for the baseline restore sync.

## Mutation scope

Mutates the runtime GitOps branch and the Argo Application operation. It does
not directly patch the workload objects.

## Guardrails

Only supports the native catalog cache enable and baseline restore. It refuses
unexpected corrective values.

## Validation

Run the D8 corrective-action playbook only when the workloads, GitOps, Argo,
and monitoring path are ready.
