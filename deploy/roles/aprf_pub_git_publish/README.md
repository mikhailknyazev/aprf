# aprf_pub_git_publish

## Purpose

Publishes bounded rendered content into the reader-owned runtime GitOps
repository. This role is used by normal provisioning and by the D8 Governance
and observability corrective-action flow.

## Inputs

| Input | Meaning |
| --- | --- |
| `aprf_pub_gitops_workdir` | Local runtime GitOps checkout path. |
| `aprf_pub_gitops_sync_paths` | Bounded paths copied into the runtime GitOps checkout. |
| `aprf_pub_git_publish_commit_message` | Commit message for the bounded change. |
| `APRF_PUB_GITOPS_SECRETS_FILE` | Optional local secrets file for HTTPS Git credentials. |

## Outputs

Creates or updates a local GitOps checkout, commits bounded content, and pushes
to the target branch.

## Mutation scope

Mutates a local runtime Git checkout and the configured remote GitOps branch.

## Guardrails

Requires a usable remote URL, branch, commit message, and bounded sync paths.
HTTPS publication requires a PAT from the configured local secrets source.
