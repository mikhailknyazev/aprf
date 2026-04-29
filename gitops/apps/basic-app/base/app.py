"""Tiny Python governance workload used as the operational spine in APRF."""

import json
import os
import time
from collections import defaultdict
from datetime import datetime, timezone
from http.server import BaseHTTPRequestHandler, HTTPServer


APP_MESSAGE = os.getenv("APP_MESSAGE", "missing APP_MESSAGE")
START_TIME = time.time()
REQUEST_TOTAL = defaultdict(int)


def log_event(path, status_code):
    """Write small structured logs for user-facing requests only."""
    if path in {"/healthz", "/metrics"}:
        return

    print(
        json.dumps(
            {
                "ts": datetime.now(timezone.utc).isoformat(),
                "app": "basic-app",
                "path": path,
                "status": status_code,
            }
        ),
        flush=True,
    )


def render_metrics():
    """Expose the two tiny metrics that support the basic-app monitoring story."""
    uptime_seconds = max(0.0, time.time() - START_TIME)
    lines = [
        "# HELP basic_app_uptime_seconds Process uptime in seconds",
        "# TYPE basic_app_uptime_seconds gauge",
        f"basic_app_uptime_seconds {uptime_seconds:.3f}",
        "# HELP basic_app_http_requests_total Total HTTP requests by path",
        "# TYPE basic_app_http_requests_total counter",
    ]

    for path, count in sorted(REQUEST_TOTAL.items()):
        lines.append(f'basic_app_http_requests_total{{path="{path}"}} {count}')

    return ("\n".join(lines) + "\n").encode("utf-8")


class Handler(BaseHTTPRequestHandler):
    """Serve health, metrics, and one simple user-facing text response."""

    def log_message(self, format, *args):
        return

    def do_GET(self):
        if self.path in {"/healthz", "/readyz"}:
            body = b"ok\n"
            content_type = "text/plain; charset=utf-8"
        elif self.path == "/metrics":
            body = render_metrics()
            content_type = "text/plain; version=0.0.4; charset=utf-8"
        else:
            body = (
                "basic-app\n"
                f"message={APP_MESSAGE}\n"
                "source=kustomize/workloads/basic-app/base\n"
            ).encode("utf-8")
            content_type = "text/plain; charset=utf-8"

        REQUEST_TOTAL[self.path] += 1
        self.send_response(200)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)
        log_event(self.path, 200)


if __name__ == "__main__":
    HTTPServer(("0.0.0.0", 8080), Handler).serve_forever()
