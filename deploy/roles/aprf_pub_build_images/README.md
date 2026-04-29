# aprf_pub_build_images

## Purpose

Builds and pushes the four Java images used by the article companion workload
set. JVM images use the supported local/container path. Native images build on
the bastion where Linux resources are predictable.

## Inputs

| Input | Meaning |
| --- | --- |
| `aprf_pub_jvm_apps` | JVM service build definitions. |
| `aprf_pub_native_apps` | Native service build definitions. |
| `aprf_pub_podman_socket` | Mounted host Podman socket for JVM builds. |
| `aprf_pub_effective_bastion_host` and `aprf_pub_effective_bastion_key` | Bastion access for native builds. |
| `aprf_pub_native_build_*` | Native build workspace and resource guardrails. |

## Outputs

Pushes image tags for `catalog-jvm`, `experience-jvm`, `catalog-native`, and
`experience-native` to the configured registry.

## Mutation scope

Mutates the container registry and the bastion native-build workspace under
`/var/tmp/aprf-pub/native-build`.

## Guardrails

Native builds check bastion RAM and `/var` free space before the first build.
Build CPU and memory are bounded. Native builds are not run on the local computer.
