import json
import re
import uuid
import time
import urllib.request
import threading

class AftermathPythonSdk:
    def __init__(self, collector_url="http://localhost:8090/api/v1/incidents", service_name="python-service"):
        self.collector_url = collector_url
        self.service_name = service_name
        self.sensitive_keys = {"authorization", "cookie", "x-api-key", "password", "secret", "token"}

    def redact_headers(self, headers):
        redacted = {}
        for k, v in (headers or {}).items():
            if k.lower() in self.sensitive_keys:
                redacted[k] = "[REDACTED]"
            else:
                redacted[k] = v
        return redacted

    def capture_exception(self, exception, request_info=None):
        try:
            event = {
                "incidentId": str(uuid.uuid4()),
                "traceId": str(uuid.uuid4()),
                "timestamp": int(time.time() * 1000),
                "request": {
                    "method": request_info.get("method", "GET") if request_info else "GET",
                    "uri": request_info.get("uri", "/") if request_info else "/",
                    "queryParams": request_info.get("queryParams", {}) if request_info else {},
                    "headers": self.redact_headers(request_info.get("headers", {})) if request_info else {},
                    "body": request_info.get("body", "") if request_info else "",
                    "timestamp": int(time.time() * 1000)
                },
                "error": {
                    "exceptionClass": type(exception).__name__,
                    "message": str(exception),
                    "stackTrace": str(exception),
                    "statusCode": 500
                },
                "deployment": {
                    "serviceName": self.service_name,
                    "serviceVersion": "1.0.0",
                    "environment": "production",
                    "commitHash": "python-commit"
                }
            }
            threading.Thread(target=self._send_async, args=(event,), daemon=True).start()
        except Exception:
            pass # Fail-open protection

    def _send_async(self, event):
        try:
            data = json.dumps(event).encode("utf-8")
            req = urllib.request.Request(
                self.collector_url,
                data=data,
                headers={"Content-Type": "application/json"},
                method="POST"
            )
            urllib.request.urlopen(req, timeout=2)
        except Exception:
            pass
